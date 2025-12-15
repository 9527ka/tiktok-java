package project.blockchain.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import ext.Strings;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

//import email.EmailSendService;
import kernel.exception.BusinessException;
import project.Constants;
import project.blockchain.ChannelBlockchain;
import project.blockchain.ChannelBlockchainService;
import project.blockchain.PartyBlockchain;
import project.log.Log;
import project.log.LogService;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
//import project.user.idcode.IdentifyingCodeService;
//import project.user.idcode.IdentifyingCodeTimeWindowService;
import security.SecUser;
import security.internal.SecUserService;

public class ChannelBlockchainServiceImpl extends HibernateDaoSupport implements ChannelBlockchainService {
	private volatile Map<String, ChannelBlockchain> cache = new ConcurrentHashMap<>();

	private LogService logService;

	private SecUserService secUserService;
	private PasswordEncoder passwordEncoder;
//	protected IdentifyingCodeService identifyingCodeService;
//	protected IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
	private SysparaService sysparaService;
//	private EmailSendService emailSendService;
	private GoogleAuthService googleAuthService;

	public void save(ChannelBlockchain entity, String userName, String safeword, String login_ip, String code,
			String superGoogleAuthCode) {
//		checkEmailCode(code);
		checkGoogleAuthCode(superGoogleAuthCode);
		SecUser sec = this.secUserService.findUserByLoginName(userName);
		String sysSafeword = sec.getSafeword();

		String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}

		this.getHibernateTemplate().save(entity);
		this.cache.put(entity.getId().toString(), entity);
		String log = "新增地址，名称[" + entity.getBlockchain_name() + "]，地址[" + entity.getAddress() + "]" + "币种["
				+ entity.getCoin() + "]，图片[" + entity.getImg() + "]，" + "ip[" + login_ip + "],验证码:[" + code + "]";
		saveLog(sec, userName, log);

		sendCodeToSysUsers(sec, userName, "新增区块链地址地址", "用户[" + userName + "],新增区块链地址地址");
	}

	private void sendCodeToSysUsers(SecUser secUser, String operator, String title, String content) {
		logger.info(title + ":" + content);
		List<SecUser> users = secUserService.findAllSysUsers();
		String log = "以下系统用户尚未配置邮箱，无法收到邮件:";
		for (SecUser user : users) {
			if (StringUtils.isEmpty(user.getEmail())) {
				log += user.getUsername() + ",";
			} else {
//				emailSendService.sendEmail(user.getEmail(), title, content);
				log += user.getUsername() + ",";
			}
		}
		saveLog(secUser, operator, log);
	}

	/**
	 * 验证谷歌验证码
	 * 
	 * @param code
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = googleAuthService.checkCode(secret, code);
		if (!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}

	/**
	 * 验证管理员唯一邮箱
	 * 
	 * @param code
	 */
	private void checkEmailCode(String code) {
//		String value = sysparaService.find("admin_verify_email").getValue();
//		String authCode = identifyingCodeTimeWindowService.getAuthCode(value);
//		if (StringUtils.isEmpty(authCode) || !authCode.equals(code)) {
//			throw new BusinessException("验证码错误");
//		}
//		identifyingCodeTimeWindowService.delAuthCode(value);
	}

	public void update(ChannelBlockchain old, ChannelBlockchain entity, String userName, String partyId,
			String safeword, String login_ip, String code, String superGoogleAuthCode) {
//		checkEmailCode(code);
		checkGoogleAuthCode(superGoogleAuthCode);
		SecUser sec = this.secUserService.findUserByLoginName(userName);
		String sysSafeword = sec.getSafeword();

		String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}

		getHibernateTemplate().update(entity);
		this.cache.put(entity.getId().toString(), entity);

		String log = "区块链充值地址修改。原名称[" + old.getBlockchain_name() + "]，原地址[" + old.getAddress() + "]" + "原币种["
				+ old.getCoin() + "]，原图片[" + old.getImg() + "]。" + "修改后，新名称[" + entity.getBlockchain_name() + "]，新地址["
				+ entity.getAddress() + "]" + "新币种[" + entity.getCoin() + "]，新图片[" + entity.getImg() + "]，" + "ip["
				+ login_ip + "],验证码:[" + code + "]";
		saveLog(sec, userName, log);
		sendCodeToSysUsers(sec, userName, "区块链充值地址修改", "用户[" + userName + "],区块链充值地址修改");
	}

	public void saveLog(SecUser secUser, String operator, String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setOperator(operator);
		log.setUsername(secUser.getUsername());
		log.setPartyId(secUser.getPartyId());
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}

	public void delete(String id, String safeword, String userName, String loginIp, String code,
			String superGoogleAuthCode) {
//		checkEmailCode(code);
		checkGoogleAuthCode(superGoogleAuthCode);
		SecUser sec = this.secUserService.findUserByLoginName(userName);
		String sysSafeword = sec.getSafeword();

		String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("资金密码错误");
		}

		ChannelBlockchain entity = findById(id);
		getHibernateTemplate().delete(entity);
		this.cache.remove(entity.getId().toString());
		String log = "删除地址，名称[" + entity.getBlockchain_name() + "]，地址[" + entity.getAddress() + "]" + "币种["
				+ entity.getCoin() + "]，图片[" + entity.getImg() + "]，" + "ip[" + loginIp + "],验证码:[" + code + "]";
		saveLog(sec, userName, log);
		sendCodeToSysUsers(sec, userName, "区块链充值地址删除", "用户[" + userName + "],区块链充值地址删除");
	}

	public List<ChannelBlockchain> findAll() {
		List list = getHibernateTemplate().find(" FROM ChannelBlockchain  ", new Object[] {});
		return list;
	}

	public ChannelBlockchain findById(String id) {
		return (ChannelBlockchain) getHibernateTemplate().get(ChannelBlockchain.class, id);
	}

	@Override
	public ChannelBlockchain findByNameAndCoinAndAdd(String blockchain_name, String coin, String address) {
		List<ChannelBlockchain> list = new ArrayList<ChannelBlockchain>();
		if (StringUtils.isEmpty(address)) {
			list = (List<ChannelBlockchain>) getHibernateTemplate().find(" FROM ChannelBlockchain WHERE blockchain_name = ?0 and coin = ?1 ",
					new Object[] { blockchain_name, coin });
		} else {
			list = (List<ChannelBlockchain>) getHibernateTemplate().find(
					" FROM ChannelBlockchain WHERE blockchain_name = ?0 and coin = ?1 and address = ?2 ",
					new Object[] { blockchain_name, coin, address });
		}
		if (list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public PartyBlockchain findByPersonNameAndCoinAndAdd(String blockchain_name, String coin, String address,String username) {
		List<PartyBlockchain> list = new ArrayList<PartyBlockchain>();
		if (StringUtils.isEmpty(address)) {
			list = (List<PartyBlockchain>) getHibernateTemplate().find(" FROM PartyBlockchain WHERE chainName = ?0 and coinSymbol = ?1 and userName = ?2 ",
					new Object[] { blockchain_name, coin ,username});
		} else {
			list = (List<PartyBlockchain>) getHibernateTemplate().find(
					" FROM PartyBlockchain WHERE chainName = ?0 and coinSymbol = ?1 and address = ?2 and userName = ?3 ",
					new Object[] { blockchain_name, coin, address ,username});
		}
		if (list.size() > 0) {
			return list.get(0);
		}

		return null;
	}

	public List<ChannelBlockchain> findByCoin(String coin) {
		List<ChannelBlockchain> list = (List<ChannelBlockchain>) getHibernateTemplate().find(" FROM ChannelBlockchain WHERE  coin = ?0 ",
				new Object[] { coin });
		if (list.size() > 0)
			return filterBlockchain(list);
		return null;
	}

	public List<ChannelBlockchain> findByCoinAndName(String coin, String blockchain_name) {
		List<ChannelBlockchain> list = (List<ChannelBlockchain>) getHibernateTemplate().find(" FROM ChannelBlockchain WHERE blockchain_name = ?0 and coin = ?1 ",
				new Object[] { blockchain_name, coin });
		if (list.size() > 0)
			return filterBlockchain(list);
		return null;
	}

	/**
	 * 过滤充值地址链，随机获取
	 * 
	 * @param list
	 * @return
	 */
	public List<ChannelBlockchain> filterBlockchain(List<ChannelBlockchain> list) {
		Map<String, List<ChannelBlockchain>> map = new HashMap<String, List<ChannelBlockchain>>();
		for (ChannelBlockchain cb : list) {
			if (map.containsKey(cb.getBlockchain_name())) {
				map.get(cb.getBlockchain_name()).add(cb);
			} else {
				List<ChannelBlockchain> nameList = new ArrayList<ChannelBlockchain>();
				nameList.add(cb);
				map.put(cb.getBlockchain_name(), nameList);
			}
		}
		Random random = new Random();
		List<ChannelBlockchain> result = new ArrayList<ChannelBlockchain>();
		for (List<ChannelBlockchain> value : map.values()) {
			if (value.size() == 1) {
				result.addAll(value);
			} else {
				int randIndex = random.nextInt(value.size());// 随机抽取一个，减1即为索引
				result.add(value.get(randIndex));
			}
		}
		return result;
	}

	@Override
	public PartyBlockchain findPersonBlockchain(String username, String coinSymbol, String blockchainName) {
		if (StringUtils.isEmpty(username)) {
			return null;
		}
		DetachedCriteria query = DetachedCriteria.forClass(PartyBlockchain.class);
		query.add(Property.forName("userName").eq(username));
		query.add(Property.forName("coinSymbol").eq(coinSymbol));
		if(!Strings.isNullOrEmpty(blockchainName)){
			query.add(Property.forName("chainName").eq(blockchainName));
		}
		List retList = getHibernateTemplate().findByCriteria(query);
		if (retList == null || retList.isEmpty()) {
			return null;
		}
		return (PartyBlockchain)retList.get(0);
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

//	public void setIdentifyingCodeService(IdentifyingCodeService identifyingCodeService) {
//		this.identifyingCodeService = identifyingCodeService;
//	}
//
//	public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
//		this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
//	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

//	public void setEmailSendService(EmailSendService emailSendService) {
//		this.emailSendService = emailSendService;
//	}

	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
		this.googleAuthService = googleAuthService;
	}

}
