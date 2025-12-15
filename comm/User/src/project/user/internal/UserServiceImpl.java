package project.user.internal;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.Constants;
import project.event.message.LogoffAccountEvent;
import project.event.model.LogoffAccountInfo;
import project.event.model.UserChangeInfo;
import project.log.Log;
import project.log.LogService;
import project.mall.goods.SellerGoodsService;
import project.mall.seller.FocusSellerService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.party.PartyRedisKeys;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.user.UserRedisKeys;
import project.user.UserService;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.user.token.TokenService;
import project.withdraw.Withdraw;
import security.Role;
import security.SecUser;
import security.internal.SecUserService;

import javax.annotation.Resource;

@Slf4j
public class UserServiceImpl implements UserService {
	private SecUserService secUserService;
	private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
	private PasswordEncoder passwordEncoder;
	private PartyService partyService;
	private OnlineUserService onlineUserService;

	private RedisHandler redisHandler;
	private SellerService sellerService;
	private TokenService tokenService;
	private FocusSellerService focusSellerService;
	private SellerGoodsService sellerGoodsService;
	private LogService logService;

	/**
	 * 图片验证key，保证前后一致性
	 */
	private Map<String, String> imageCodeCache = new ConcurrentHashMap<String, String>();

	@Override
	public SecUser addLogin(String username, String password) {
		SecUser user = secUserService.findUserByLoginName(username);

		if (user == null) {
			throw new BusinessException("用户不存在");
		}
		Party party = partyService.cachePartyBy(user.getPartyId(), false);
		String[] rolesArrty = new String[] { Constants.SECURITY_ROLE_GUEST, Constants.SECURITY_ROLE_MEMBER, Constants.SECURITY_ROLE_TEST };
		if (party == null || !party.getLogin_authority()) {
			throw new BusinessException("登录失败");
		}
		Set<Role> roles = user.getRoles();
		boolean find = false;
		for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
			Role role = (Role) iterator.next();
			for (int i = 0; i < rolesArrty.length; i++) {
				if (role.getRoleName().equals(rolesArrty[i])) {
					find = true;
				}
			}
		}

		if (!find) {
			throw new BusinessException("登录失败");
		}
		String password_encoder = passwordEncoder.encodePassword(password, user.getUsername());

		if (!password_encoder.equals(user.getPassword())) {
			throw new BusinessException("密码不正确");
		}

		party.setLast_loginTime(new Date());
		partyService.update(party);

		return user;

	}
	
	/**
	 * 验证码登录
	 */
	@Override
	public SecUser addLogin_idcode(String username, String verifcode) {
		SecUser user = secUserService.findUserByLoginName(username);

		if (user == null) {
			throw new BusinessException("用户名不存在");
		}
		Party party = partyService.cachePartyBy(user.getPartyId(), false);
		String[] rolesArrty = new String[] {};
		if (party == null || !party.getLogin_authority()) {
			throw new BusinessException("登录失败");
		}
		Set<Role> roles = user.getRoles();
		boolean find = false;
		for (Iterator iterator = roles.iterator(); iterator.hasNext();) {
			Role role = (Role) iterator.next();
			for (int i = 0; i < rolesArrty.length; i++) {
				if (role.getRoleName().equals(rolesArrty[i])) {
					find = true;
				}
			}
		}

		if (!find) {
			throw new BusinessException("登录失败");
		}

		String authcode = this.identifyingCodeTimeWindowService.getAuthCode(username);

		if ((authcode == null) || (!authcode.equals(verifcode))) {
			throw new BusinessException("登录失败");
		}
		this.identifyingCodeTimeWindowService.delAuthCode(username);
		return user;
	}
	
	@Override
	public void online(String partyId) {
		if (StringUtils.isNullOrEmpty(partyId)) {
			return;
		}
//		登录时候默认设置在线状态为在线1(离开2，离线3)
		Map<String, Object> statusParams = new ConcurrentHashMap<String, Object>();
		statusParams.put("status",1);
		statusParams.put("operateTime",new Date());
		redisHandler.setSync(UserRedisKeys.ONLINE_USER_STATUS_PARTYID+partyId,statusParams);
		onlineUserService.put(partyId, new Date());
	}

	@Override
	public void offline(String partyId) {
		if (StringUtils.isNullOrEmpty(partyId)) {
			return;
		}
//		将在线标识给移除
		onlineUserService.del(partyId);
//		移除token，设置重新登录
		tokenService.removeLoginToken(partyId);
	}

	@Override
	public void logout(String partyId) {
		if (StringUtils.isNullOrEmpty(partyId)) {
			return;
		}
		onlineUserService.del(partyId);
	}

	public void updateSyncUserInfo(UserChangeInfo changeInfo) {
		if (StrUtil.isBlank(changeInfo.getPartyId()) || Objects.equals(changeInfo.getPartyId(), "0")) {
			return;
		}

		SecUser user = secUserService.findUserByPartyId(changeInfo.getPartyId());
		if (user == null) {
			log.error("syncUserInfo 服务发现 partyId:" + changeInfo.getPartyId() + ", 对应的 user 记录不存在！");
			return;
		}

		boolean changed = false;

		String oldUserName = changeInfo.getOldUserName();
		String newUserName = changeInfo.getNewUserName();
		if (oldUserName == null) {
			oldUserName = "";
		}
		if (newUserName == null) {
			newUserName = "";
		}
		if (!oldUserName.equals(newUserName)) {
			// 用户账号发生了改变
			if (StrUtil.isBlank(newUserName)) {
				throw new BusinessException("错误的参数");
			}
			if (StrUtil.isBlank(changeInfo.getPassword())) {
				throw new BusinessException("未提交密码，修改失败");
			}
			user.setUsername(newUserName);

			// 密文密码也需要更新
			String password_encoder = passwordEncoder.encodePassword(changeInfo.getPassword(), newUserName);
			user.setPassword(password_encoder);
			changed = true;
		}

		String oldEmail = changeInfo.getOldEmail();
		String newEmail = changeInfo.getNewEmail();
		if (oldEmail == null) {
			oldEmail = "";
		}
		if (newEmail == null) {
			newEmail = "";
		}
		if (!oldEmail.equals(newEmail)) {
			// 用户邮箱发生了改变
			user.setEmail(newEmail);
			changed = true;
		}

		if (changed) {
			secUserService.update(user);
			//更新密码以后记录 操作日志
			Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setUsername(user.getUsername());
			log.setPartyId(user.getPartyId());
			log.setLog("用户[" + user.getUsername()+ "]账号修改，修改前账号名为["+oldUserName+"]");
			this.logService.saveSync(log);
		}
	}

	@Override
	@Transactional
	public void updateLogoffAccount(String partyId, String reason) {
		SecUser userEntity = secUserService.findUserByPartyId(partyId);
		Party party = partyService.getById(partyId);

		String logoffSufix = ":off:" + (System.currentTimeMillis() / 1000L);
		String oriAccount = userEntity.getUsername();
		String newAccount = oriAccount + logoffSufix;
		if (newAccount.length() > 64) {
			newAccount = newAccount.substring(0, 64);
		}

		String newEmail = party.getEmail();
		if (StrUtil.isNotBlank(newEmail)) {
			newEmail = newEmail + ":off";
			if (newEmail.length() > 64) {
				newEmail = "";
			}
		}

		String newPhone = party.getPhone();
		if (StrUtil.isNotBlank(newPhone)) {
			newPhone = newPhone + ":off";
		}

		//
		userEntity.setUsername(newAccount);
		userEntity.setEnabled(false);
		userEntity.setEmail(newEmail);
		userEntity.setRemarks(reason);
		secUserService.update(userEntity);

		party.setUsername(newAccount);
		party.setPhone(newPhone);
		party.setEmail(newEmail);
		party.setEnabled(false);
		partyService.update(party);

		// 清掉缓存，可用于支持手动改数据库记录恢复账号
		redisHandler.remove(PartyRedisKeys.PARTY_ID + party.getId());
		redisHandler.remove(PartyRedisKeys.PARTY_USERNAME + party.getUsername());
		redisHandler.remove(PartyRedisKeys.PARTY_USERNAME + oriAccount);

		Seller seller = sellerService.getSeller(party.getId().toString());
		if (seller != null) {
			seller.setStatus(0);
			seller.setName(seller.getName() + logoffSufix);
			sellerService.updateSeller(seller);

			// 删掉所有关注的商铺
			focusSellerService.deleteAllFocus(seller.getId().toString());

			// 标记其下所有店铺商品状态
			sellerGoodsService.deleteAllSellerGoods(seller.getId().toString());
		}

		// 发布事件，可能其他业务也需要修改相关的字段
		WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
		LogoffAccountInfo info = new LogoffAccountInfo();
		info.setPartyId(partyId);
		info.setOriAccount(oriAccount);
		info.setNewAccount(newAccount);
		wac.publishEvent(new LogoffAccountEvent(this, info));
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public Map<String, String> getImageCodeCache() {
		return imageCodeCache;
	}

	public void putImageCode(String key, String value) {
		imageCodeCache.put(key, value);
	}

	public String cacheImageCode(String key) {
		return imageCodeCache.get(key);
	}

	public void cacheRemoveImageCode(String key) {
		imageCodeCache.remove(key);
	}

	public void setTokenService(TokenService tokenService) {
		this.tokenService = tokenService;
	}

	public void putRandKey(String key, String value) {
		imageCodeCache.put(key, value);
	}

	public IdentifyingCodeTimeWindowService getIdentifyingCodeTimeWindowService() {
		return identifyingCodeTimeWindowService;
	}

	public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
		this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
	}

	public void setOnlineUserService(OnlineUserService onlineUserService) {
		this.onlineUserService = onlineUserService;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	public void setRedisHandler(RedisHandler redisHandler) {
		this.redisHandler = redisHandler;
	}

	public void setFocusSellerService(FocusSellerService focusSellerService) {
		this.focusSellerService = focusSellerService;
	}

	public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
		this.sellerGoodsService = sellerGoodsService;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}
}
