package project.user.internal;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.util.UUIDGenerator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import project.Constants;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.ImageQr;
import project.user.LocalNormalReg;
import project.user.LocalUserService;
import project.user.QRGenerateService;
import project.user.idcode.IdentifyingCodeTimeWindowService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import security.Role;
import security.RoleService;
import security.SaltSigureUtils;
import security.SecUser;
import security.internal.SecUserService;
import util.LockFilter;
import util.Strings;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class LocalUserServiceImpl extends HibernateDaoSupport implements LocalUserService {
	
	private WalletService walletService;
	private PartyService partyService;
	private IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
	private SecUserService secUserService;
	private RoleService roleService;
	private UserRecomService userRecomService;
	protected MoneyLogService moneyLogService;
	private QRGenerateService qRGenerateService;
	private SysparaService sysparaService;
	private PasswordEncoder passwordEncoder;
	protected JdbcTemplate jdbcTemplate;
	private LogService logService;

	public void saveRegister(LocalNormalReg reg, String type) {
		
		reg.setUsername(reg.getUsername().trim());
		reg.setPassword(reg.getPassword().trim());
		
		if (!"null".equals(reg.getSafeword()) && !StringUtils.isEmptyString(reg.getSafeword())) {
			reg.setSafeword(reg.getSafeword().trim());
		}

		String key = reg.getUsername();
		String authcode = this.identifyingCodeTimeWindowService.getAuthCode(key);

		if ((authcode == null) || (!authcode.equals(reg.getIdentifying_code()))) {
			throw new BusinessException("验证码不正确");
		}

		Party party_reco = this.partyService.findPartyByUsercode(reg.getReco_usercode());

		if ("true".equals(this.sysparaService.find("register_need_usercode").getValue())) {
			if (null == party_reco) {
				throw new BusinessException("推荐码不正确");
			}
			if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRolename())) {
				throw new BusinessException("推荐人无权限推荐");
			}
			if (!party_reco.getEnabled()) {
				throw new BusinessException("推荐人无权限推荐");
			}
		}

		if ("true".equals(this.sysparaService.find("register_need_usercode_turn").getValue())) {
			if (!party_reco.getRegister_usercode()) {
				throw new BusinessException("推荐人无权限推荐");
			}
		}

		if (this.secUserService.findUserByLoginName(reg.getUsername()) != null) {
			throw new BusinessException("用户名重复");
		}
		
		int ever_user_level_num = this.sysparaService.find("ever_user_level_num").getInteger();
		int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();

		Party party = new Party();
		party.setUsername(reg.getUsername());
		party.setUsercode(this.getUsercode());
		party.setUser_level(ever_user_level_num_custom * 10 + ever_user_level_num);
		party.setSafeword(this.passwordEncoder.encodePassword(reg.getSafeword(), SaltSigureUtils.saltfigure));
		party.setRolename(Constants.SECURITY_ROLE_MEMBER);
		party = this.partyService.save(party);
				
//		if (reg.getUsername().indexOf("@") == -1) {
		if (type.equals("1")) {
			// 手机注册			
//			if (StringUtils.isEmptyString(reg.getUsername()) || !Strings.isNumber(reg.getUsername()) || reg.getUsername().length() > 15) {
			if (StringUtils.isEmptyString(reg.getUsername()) || reg.getUsername().length() > 20) {
				throw new BusinessException("请输入正确的手机号码");
			}
			this.savePhone(reg.getUsername(), party.getId().toString());
		} else {
			// 邮箱注册
			if (!Strings.isEmail(reg.getUsername())) {
				throw new BusinessException("请输入正确的邮箱地址");
			}
			this.saveEmail(reg.getUsername(), party.getId().toString());
		}

		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);
		secUser.setUsername(reg.getUsername());
		secUser.setPassword(reg.getPassword());
		this.secUserService.saveUser(secUser);

		// usdt账户
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		if (party_reco != null) {
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			// 父类partyId
			userRecom.setReco_id(party_reco.getId());
			this.userRecomService.save(userRecom);
		}

//		User user = new User();
//		user.setPartyId(party.getId());
//		this.getHibernateTemplate().save(user);		
		String uuid = UUIDGenerator.getUUID();
		String partyId = party.getId().toString();
		String partyRecoId = party_reco != null ? party_reco.getId().toString() : "";
		this.jdbcTemplate.execute("INSERT INTO T_USER(UUID,PARTY_ID,PARENT_PARTY_ID) VALUES('"+uuid+"','"+partyId+"','"+partyRecoId+"')");
		


		// 用户注册自动赠送金额
		String register_gift_coin = this.sysparaService.find("register_gift_coin").getValue();
		if (!"".equals(register_gift_coin) && register_gift_coin != null) {
			
			String[] register_gift_coins = register_gift_coin.split(",");
			String gift_symbol = register_gift_coins[0];
			double gift_sum = Double.valueOf(register_gift_coins[1]);
			
			if ("usdt".equals(gift_symbol)) {
				
				Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getId());
				double amount_before = walletExtend.getMoney();
				if (Arith.add(gift_sum, walletExtend.getMoney()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.update(wallet.getPartyId().toString(), gift_sum);
				
				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
				
			} else {
				
				WalletExtend walletExtend = this.walletService.saveExtendByPara(party.getId(), gift_symbol);
				double amount_before = walletExtend.getAmount();
				if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.updateExtend(walletExtend.getPartyId().toString(), gift_symbol, gift_sum);

				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
			}
		}

		this.identifyingCodeTimeWindowService.delAuthCode(key);
	}

    /**
     * 手机/邮箱注册（无验证码）
     */
    public void checkRegister(String type,String username, String userCode) {
        username = username.trim();
        if (!userCode.equals("000000")) {
            Party party_reco = this.partyService.findPartyByUsercode(userCode);
            if (null == party_reco) {
                throw new BusinessException("邀请码无效");
            }
            if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRolename())) {
                throw new BusinessException("推荐人无权限推荐");
            }
            if (!party_reco.getEnabled()) {
                throw new BusinessException("推荐人无权限推荐");
            }
        }
        if(username.contains("@")){
            if(Objects.nonNull(this.partyService.getPartyByEmail(username))){
                throw new BusinessException("该邮箱已被占用，请更换其他邮箱注册");
            }
        }else {
            if (this.secUserService.findUserByLoginName(username) != null) {
                throw new BusinessException("用户名重复");
            }
        }

        if (type.equals("1")) {
            // 手机注册
            if (StringUtils.isEmptyString(username) || username.length() > 20) {
                throw new BusinessException("请输入正确的手机号码");
            }
        } else if (type.equals("2")) {
            // 邮箱注册
            if (!Strings.isEmail(username)) {
                throw new BusinessException("请输入正确的邮箱地址");
            }
            if (userCode.length()>64){
                throw new BusinessException("邮箱的长度不能超过64个字符");
            }
        } else {
            // 用户名注册
            if (username.length() < 6 || username.length() > 30) {
                throw new BusinessException("用户名必须6-30位");
            }
        }
    }

    /**
	 * 手机/邮箱注册（无验证码）
	 */
	public void saveRegisterNoVerifcode(LocalNormalReg reg, String type) {
		reg.setUsername(reg.getUsername().trim());
		reg.setPassword(reg.getPassword().trim());
		
		if (!"null".equals(reg.getSafeword()) && !StringUtils.isEmptyString(reg.getSafeword())) {
			reg.setSafeword(reg.getSafeword().trim());
		}

		// 绑定到代理商的用户记录
		Party party_reco = null;

		// 2023-4-20 前只识别商家挂在哪个代理下，根据最新需求，买家也要记录
		// if (reg.getRoleType()==1  && !reg.getReco_usercode().equals("000000")) {
		if (!reg.getReco_usercode().equals("000000")) {
			party_reco = this.partyService.findPartyByUsercode(reg.getReco_usercode());
			if (null == party_reco) {
				throw new BusinessException("邀请码无效");
			}
			if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRolename())) {
				throw new BusinessException("推荐人无权限推荐");
			}
			if (!party_reco.getEnabled()) {
				throw new BusinessException("推荐人无权限推荐");
			}
		}

		if (this.secUserService.findUserByLoginName(reg.getUsername()) != null) {
			throw new BusinessException("用户名重复");
		}
		if(reg.getUsername().contains("@") && Objects.nonNull(this.partyService.getPartyByEmail(reg.getUsername()))){
			throw new BusinessException("该邮箱已被占用，请更换其他邮箱注册");
		}
		
//		int ever_user_level_num = this.sysparaService.find("ever_user_level_num").getInteger();
//		int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();
		Random random = new Random();
		int avatar_num = (1 + random.nextInt(19));
		Party party = new Party();
		party.setAvatar(avatar_num+"");
		party.setUsername(reg.getUsername());
		party.setUsercode(this.getUsercode());
		party.setUser_level(0);
		//party.setSafeword(this.passwordEncoder.encodePassword(reg.getSafeword(), SaltSigureUtils.saltfigure));
		party.setRolename(Constants.SECURITY_ROLE_MEMBER);
		
		if (type.equals("1")) {
			// 手机注册
//			if (StringUtils.isEmptyString(reg.getUsername()) || !Strings.isNumber(reg.getUsername()) || reg.getUsername().length() > 15) {
			if (StringUtils.isEmptyString(reg.getUsername()) || reg.getUsername().length() > 20) {
				throw new BusinessException("请输入正确的手机号码");
			}
			party.setPhone(reg.getUsername());
			party.setPhone_authority(false);
		} else if (type.equals("2")) {
			// 邮箱注册
			if (!Strings.isEmail(reg.getUsername())) {
				throw new BusinessException("请输入正确的邮箱地址");
			}
			if (reg.getUsername().length()>64){
				throw new BusinessException("邮箱的长度不能超过64个字符");
			}
			party.setEmail(reg.getUsername());
			party.setEmail_authority(false);
		} else {
			// 用户名注册
			if (reg.getUsername().length() < 6 || reg.getUsername().length() > 30) {
				throw new BusinessException("用户名必须6-30位");
			}
		}
		
		party = this.partyService.save(party);

		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);
		secUser.setUsername(reg.getUsername());
		secUser.setPassword(reg.getPassword());
		this.secUserService.saveUser(secUser);

		// usdt账户
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		if (party_reco != null) {
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			// 父类partyId
			userRecom.setReco_id(party_reco.getId());
			this.userRecomService.save(userRecom);
		}


//		String uuid = UUIDGenerator.getUUID();
//		String partyId = party.getId().toString();
//		String partyRecoId = party_reco != null ? party_reco.getId().toString() : "";
//		this.jdbcTemplate.execute("INSERT INTO T_USER(UUID,PARTY_ID,PARENT_PARTY_ID) VALUES('"+uuid+"','"+partyId+"','"+partyRecoId+"')");
		


		// 用户注册自动赠送金额
		String register_gift_coin = this.sysparaService.find("register_gift_coin").getValue();
		if (!"".equals(register_gift_coin) && register_gift_coin != null) {
			
			String[] register_gift_coins = register_gift_coin.split(",");
			String gift_symbol = register_gift_coins[0];
			double gift_sum = Double.valueOf(register_gift_coins[1]);
			
			if ("usdt".equals(gift_symbol)) {
				
				Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getId());
				double amount_before = walletExtend.getMoney();
				if (Arith.add(gift_sum, walletExtend.getMoney()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.update(wallet.getPartyId().toString(), gift_sum);
				
				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
				
			} else {
				
				WalletExtend walletExtend = this.walletService.saveExtendByPara(party.getId(), gift_symbol);
				double amount_before = walletExtend.getAmount();
				if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.updateExtend(walletExtend.getPartyId().toString(), gift_symbol, gift_sum);

				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
			}
		}
	}

	/**
	 * 手机/邮箱注册（验证码）
	 */
	public void saveRegisterWithVerifcode(LocalNormalReg reg, String type) {
		reg.setUsername(reg.getUsername().trim());
		reg.setPassword(reg.getPassword().trim());

		if (!"null".equals(reg.getSafeword()) && !StringUtils.isEmptyString(reg.getSafeword())) {
			reg.setSafeword(reg.getSafeword().trim());
		}

		Party party_reco = null;

		if (!reg.getReco_usercode().equals("000000")) {
			party_reco = this.partyService.findPartyByUsercode(reg.getReco_usercode());
			if (null == party_reco) {
				throw new BusinessException("邀请码无效");
			}
			if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRolename())) {
				throw new BusinessException("推荐人无权限推荐");
			}
			if (!party_reco.getEnabled()) {
				throw new BusinessException("推荐人无权限推荐");
			}
		}

//		if (this.secUserService.findUserByLoginName(reg.getUsername()) != null) {
//			throw new BusinessException("用户名重复");
//		}
//		if(reg.getUsername().contains("@") && Objects.nonNull(this.partyService.getPartyByEmail(reg.getUsername()))){
//			throw new BusinessException("该邮箱已被占用，请更换其他邮箱注册");
//		}  发送验证码时已验证

		Random random = new Random();
		int avatar_num = (1 + random.nextInt(19));
		Party party = new Party();
		party.setAvatar(avatar_num+"");
		party.setUsername(reg.getUsername());
		party.setUsercode(this.getUsercode());
		party.setUser_level(0);
		party.setRolename(Constants.SECURITY_ROLE_MEMBER);
		party.setPhone(reg.getPhone());
		party.setEmail(reg.getUsername());

		if (StringUtils.isNotEmpty(reg.getWhatsApp())){
			party.setWhatsApp(reg.getWhatsApp());
		}
		if (type.equals("1")) {//验证的为手机号
			if (StringUtils.isEmptyString(reg.getPhone().replaceAll("\\s","")) || reg.getPhone().replaceAll("\\s","").length() > 20) {
				throw new BusinessException("请输入正确的手机号码");
			}
			party.setPhone_authority(true);
		} else if (type.equals("2")) {//验证的为邮箱
			// 邮箱注册
			if (!Strings.isEmail(reg.getUsername())) {
				throw new BusinessException("请输入正确的邮箱地址");
			}
			if (reg.getUsername().length()>64){
				throw new BusinessException("邮箱的长度不能超过64个字符");
			}
//			2023-09-27 注册修改去掉邮箱校验
			party.setEmail_authority(false);
		} else {
			// 用户名注册
			if (reg.getUsername().length() < 6 || reg.getUsername().length() > 30) {
				throw new BusinessException("用户名必须6-30位");
			}
		}

		party = this.partyService.save(party);
		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);
		secUser.setUsername(reg.getUsername());
		secUser.setPassword(reg.getPassword());
		if(StringUtils.isEmpty(reg.getLoginFlag())){

		} else if (reg.getLoginFlag().equals("1")) {
			secUser.setLinecode(reg.getLoginCode());
		} else if (reg.getLoginFlag().equals("2")) {
			secUser.setTiktokcode(reg.getLoginCode());
		} else if (reg.getLoginFlag().equals("3")) {
			secUser.setFacebookcode(reg.getLoginCode());
		}
		this.secUserService.saveUser(secUser);

		// usdt账户
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		if (party_reco != null) {
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			// 父类partyId
			userRecom.setReco_id(party_reco.getId());
			this.userRecomService.save(userRecom);
		}


		// 用户注册自动赠送金额
		String register_gift_coin = this.sysparaService.find("register_gift_coin").getValue();
		if (!"".equals(register_gift_coin) && register_gift_coin != null) {

			String[] register_gift_coins = register_gift_coin.split(",");
			String gift_symbol = register_gift_coins[0];
			double gift_sum = Double.valueOf(register_gift_coins[1]);

			if ("usdt".equals(gift_symbol)) {

				Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getId());
				double amount_before = walletExtend.getMoney();
				if (Arith.add(gift_sum, walletExtend.getMoney()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.update(wallet.getPartyId().toString(), gift_sum);

				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);

			} else {

				WalletExtend walletExtend = this.walletService.saveExtendByPara(party.getId(), gift_symbol);
				double amount_before = walletExtend.getAmount();
				if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.updateExtend(walletExtend.getPartyId().toString(), gift_symbol, gift_sum);

				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
			}
		}
	}

	/**
	 * JustShop Argos2邮箱注册（验证码）,同时保存手机号
	 */
	public void saveRegisterNoVerifcodeJs(LocalNormalReg reg,String type) {
		reg.setUsername(reg.getUsername().trim());
		reg.setPassword(reg.getPassword().trim());

		if (!"null".equals(reg.getSafeword()) && !StringUtils.isEmptyString(reg.getSafeword())) {
			reg.setSafeword(reg.getSafeword().trim());
		}

		// 绑定到代理商的用户记录
		Party party_reco = null;

		// 2023-4-20 前只识别商家挂在哪个代理下，根据最新需求，买家也要记录
		// if (reg.getRoleType()==1  && !reg.getReco_usercode().equals("000000")) {
		if (!reg.getReco_usercode().equals("000000")) {
			party_reco = this.partyService.findPartyByUsercode(reg.getReco_usercode());
			if (null == party_reco) {
				throw new BusinessException("邀请码无效");
			}
			if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRolename())) {
				throw new BusinessException("推荐人无权限推荐");
			}
			if (!party_reco.getEnabled()) {
				throw new BusinessException("推荐人无权限推荐");
			}
		}

//		校验邮箱必须与其他用户的用户名称不一致，并且与绑定的邮箱也不一致
		if (Objects.nonNull(this.partyService.findPartyByUsername(reg.getUsername())) || Objects.nonNull(this.partyService.getPartyByEmail(reg.getEmail()))) {
			throw new BusinessException("该邮箱已被占用，请更换其他邮箱注册");
		}
//		手机号同理 注意手机号里面有空格
		if (Objects.nonNull(this.partyService.findPartyByUsername(reg.getPhone().replaceAll("\\s",""))) || Objects.nonNull(this.partyService.findPartyByVerifiedPhone(reg.getPhone()))) {
			throw new BusinessException("该手机号已被占用，请绑定其他手机号");
		}
		//		校验邮箱的有效性
		if (!Strings.isEmail(reg.getUsername())) {
			throw new BusinessException("请输入正确的邮箱地址");
		}

		if (reg.getUsername().length()>64){
			throw new BusinessException("邮箱的长度不能超过64个字符");
		}
		if (StringUtils.isEmptyString(reg.getPhone()) || reg.getPhone().length() > 20) {
			throw new BusinessException("请输入正确的手机号码");
		}

		Random random = new Random();
		int avatar_num = (1 + random.nextInt(19));
		Party party = new Party();
		party.setAvatar(avatar_num+"");
		party.setUsername(reg.getUsername());
		party.setUsercode(this.getUsercode());
		party.setUser_level(0);
		party.setRolename(Constants.SECURITY_ROLE_MEMBER);
//		邮箱去除校验，短信通过验证码以后自动认证
		party.setEmail(reg.getUsername());
		party.setPhone(reg.getPhone());
		if (StringUtils.isNotEmpty(reg.getWhatsApp())){
			party.setWhatsApp(reg.getWhatsApp());
		}
		if ("1".equals(type)) {
			party.setPhone_authority(true);
		}
		party.setEmail_authority(false);

		party = this.partyService.save(party);

		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);
		secUser.setUsername(reg.getUsername());
		secUser.setPassword(reg.getPassword());
		this.secUserService.saveUser(secUser);

		// usdt账户
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		if (party_reco != null) {
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			// 父类partyId
			userRecom.setReco_id(party_reco.getId());
			this.userRecomService.save(userRecom);
		}



		// 用户注册自动赠送金额
		String register_gift_coin = this.sysparaService.find("register_gift_coin").getValue();
		if (!"".equals(register_gift_coin) && register_gift_coin != null) {

			String[] register_gift_coins = register_gift_coin.split(",");
			String gift_symbol = register_gift_coins[0];
			double gift_sum = Double.valueOf(register_gift_coins[1]);

			if ("usdt".equals(gift_symbol)) {

				Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getId());
				double amount_before = walletExtend.getMoney();
				if (Arith.add(gift_sum, walletExtend.getMoney()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.update(wallet.getPartyId().toString(), gift_sum);

				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);

			} else {

				WalletExtend walletExtend = this.walletService.saveExtendByPara(party.getId(), gift_symbol);
				double amount_before = walletExtend.getAmount();
				if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				this.walletService.updateExtend(walletExtend.getPartyId().toString(), gift_symbol, gift_sum);

				// 保存账变日志
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));
				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
			}
		}
	}
	
	/**
	 * 承兑商注册
	 */
	public Party saveRegisterC2cUser(String username, String password, String re_password, String type, String usercode, String ip) {
		
		Party party = null;
		
		boolean lock = false;
		
		try {

			if (!LockFilter.add(username)) {
				throw new BusinessException("重复提交");
			}
			
			lock = true;
			
			if (StringUtils.isEmptyString(username)) {
				throw new BusinessException("用户名不能为空");
			}

			if (StringUtils.isEmptyString(password)) {
				throw new BusinessException("登录密码不能为空");
			}

			if (StringUtils.isEmptyString(re_password)) {
				throw new BusinessException("密码确认不能为空");
			}

			if (password.length() < 6 || password.length() > 20 || re_password.length() < 6 || re_password.length() > 20) {
				throw new BusinessException("密码必须6-20位");
			}

			if (!password.equals(re_password)) {
				throw new BusinessException("两次输入的密码不相同");
			}
			
			if (StringUtils.isEmptyString(type) || !Arrays.asList("1", "2").contains(type)) {
				throw new BusinessException("注册类型（手机或邮箱）不能为空");
			}

			LocalNormalReg reg = new LocalNormalReg();
			reg.setUsername(username);
			reg.setPassword(password);
			reg.setSafeword("000000");
			reg.setReco_usercode(usercode);

			this.saveRegisterNoVerifcode(reg, type);

			SecUser secUser = this.secUserService.findUserByLoginName(username);
			
//			project.log.Log log = new project.log.Log();
//			log.setCategory(Constants.LOG_CATEGORY_SECURITY);
//			log.setLog("管理员操作：后台承兑商用户注册,ip[" + ip + "]");
//			log.setPartyId(secUser.getPartyId());
//			log.setUsername(username);
//			this.logService.saveAsyn(log);
			
			party = this.partyService.cachePartyBy(secUser.getPartyId(), false);

		} catch (BusinessException e) {
			throw e;
		} catch (Throwable t) {
			throw t;
		} finally {
			if (lock) {
				LockFilter.remove(username);
			}
		}
		
		return party;
	}

	/**
	 * 无验证码注册
	 */
	public void saveRegisterUsername(LocalNormalReg reg) {
		reg.setUsername(reg.getUsername().trim());
		reg.setPassword(reg.getPassword().trim());
		reg.setSafeword(reg.getSafeword().trim());

		Party party_reco = this.partyService.findPartyByUsercode(reg.getReco_usercode());
//		用户注册是否需要推荐码
		if ("true".equals(sysparaService.find("register_need_usercode").getValue())) {

			if (party_reco == null) {

				throw new BusinessException("推荐码不正确");
			}
			if (Constants.SECURITY_ROLE_TEST.equals(party_reco.getRolename())) {
				throw new BusinessException("推荐人无权限推荐");
			}
			if (!party_reco.getEnabled()) {
				throw new BusinessException("推荐人无权限推荐");
			}

		}

		if ("true".equals(sysparaService.find("register_need_usercode_turn").getValue())) {
			if (!party_reco.getRegister_usercode()) {
				throw new BusinessException("推荐人无权限推荐");
			}
		}

		if (secUserService.findUserByLoginName(reg.getUsername()) != null) {
			throw new BusinessException("用户名重复");
		}

		/**
		 * 用户code
		 */
		String usercode = getUsercode();

		/**
		 * party
		 */
		Party party = new Party();
		party.setUsername(reg.getUsername());
		party.setUsercode(usercode);
		int ever_user_level_num = sysparaService.find("ever_user_level_num").getInteger();
//		int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();
//		party.setUser_level(ever_user_level_num_custom * 10 + ever_user_level_num);
		party.setUser_level(100);
		party.setSafeword(passwordEncoder.encodePassword(reg.getSafeword(), SaltSigureUtils.saltfigure));
		party.setRolename(Constants.SECURITY_ROLE_MEMBER);

		party = partyService.save(party);

		/**
		 * SecUser
		 */
		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_MEMBER);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);

		secUser.setUsername(reg.getUsername());

		secUser.setPassword(reg.getPassword());

		this.secUserService.saveUser(secUser);

		/**
		 * usdt账户
		 */
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		this.walletService.save(wallet);

		if (party_reco != null) {
			UserRecom userRecom = new UserRecom();
			userRecom.setPartyId(party.getId());
			userRecom.setReco_id(party_reco.getId());// 父类partyId
			this.userRecomService.save(userRecom);
		}

//		User user = new User();
//		user.setPartyId(party.getId());
//		this.getHibernateTemplate().save(user);
		String uuid = UUIDGenerator.getUUID();
		String partyId = party.getId().toString();
		String partyRecoId = party_reco != null?party_reco.getId().toString():"";
		jdbcTemplate.execute("INSERT INTO T_USER(UUID,PARTY_ID,PARENT_PARTY_ID) VALUES('"+uuid+"','"+partyId+"','"+partyRecoId+"')");




		/**
		 * 用户注册自动赠送金额 start
		 */
		String register_gift_coin = sysparaService.find("register_gift_coin").getValue();
		if (!"".equals(register_gift_coin) && register_gift_coin != null) {
			String[] register_gift_coins = register_gift_coin.split(",");
			String gift_symbol = register_gift_coins[0];
			double gift_sum = Double.valueOf(register_gift_coins[1]);

			if ("usdt".equals(gift_symbol)) {
				Wallet walletExtend = this.walletService.saveWalletByPartyId(party.getId());
				double amount_before = walletExtend.getMoney();
				if (Arith.add(gift_sum, walletExtend.getMoney()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				walletService.update(wallet.getPartyId().toString(), gift_sum);
				/*
				 * 保存账变日志
				 */
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));

				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
			} else {
				WalletExtend walletExtend = this.walletService.saveExtendByPara(party.getId(), gift_symbol);
				double amount_before = walletExtend.getAmount();
				if (Arith.add(gift_sum, walletExtend.getAmount()) < 0.0D) {
					throw new BusinessException("操作失败！修正后账户余额小于0。");
				}
				walletService.updateExtend(walletExtend.getPartyId().toString(), gift_symbol, gift_sum);

				/*
				 * 保存账变日志
				 */
				MoneyLog moneyLog = new MoneyLog();
				moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
				moneyLog.setAmount_before(amount_before);
				moneyLog.setAmount(gift_sum);
				moneyLog.setAmount_after(Arith.add(amount_before, gift_sum));

				moneyLog.setPartyId(party.getId());
				moneyLog.setWallettype(gift_symbol.toUpperCase());
				moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
				moneyLog.setLog("用户注册自动赠送金额");
				this.moneyLogService.save(moneyLog);
			}

		}
		/**
		 * 用户注册自动赠送金额 end
		 */

	}

	/**
	 * 无验证码注册
	 */
	public void saveRegisterUsernameTest(LocalNormalReg reg) {
		reg.setUsername(reg.getUsername().trim());
		reg.setPassword(reg.getPassword().trim());
		reg.setSafeword(reg.getSafeword().trim());

		String test_user_code = sysparaService.find("test_user_code").getValue();

		if (StringUtils.isEmptyString(test_user_code) || !test_user_code.equals(reg.getReco_usercode())) {
			throw new BusinessException("试用码不正确");
		}

		if (secUserService.findUserByLoginName(reg.getUsername()) != null) {
			throw new BusinessException("用户名重复");
		}

		/**
		 * 用户code
		 */
		String usercode = getUsercode();

		/**
		 * party
		 */
		Party party = new Party();
		party.setUsername(reg.getUsername());
		party.setUsercode(usercode);
		int ever_user_level_num = sysparaService.find("ever_user_level_num").getInteger();
		int ever_user_level_num_custom = this.sysparaService.find("ever_user_level_num_custom").getInteger();
		party.setUser_level(ever_user_level_num_custom * 10 + ever_user_level_num);
//		party.setUser_level(100);
		party.setSafeword(passwordEncoder.encodePassword(reg.getSafeword(), SaltSigureUtils.saltfigure));
		party.setRolename(Constants.SECURITY_ROLE_TEST);
//		party.setCan_turn_member(true);
		// 关闭充值提现权限
		party.setRecharge_authority(false);
		party.setWithdraw_authority(false);
		party = partyService.save(party);

		/**
		 * SecUser
		 */
		Role role = this.roleService.findRoleByName(Constants.SECURITY_ROLE_TEST);

		SecUser secUser = new SecUser();
		secUser.setPartyId(String.valueOf(party.getId()));
		secUser.getRoles().add(role);

		secUser.setUsername(reg.getUsername());

		secUser.setPassword(reg.getPassword());

		this.secUserService.saveUser(secUser);

		double test_user_money = sysparaService.find("test_user_money").getDouble();
		/**
		 * usdt账户
		 */
		Wallet wallet = new Wallet();
		wallet.setPartyId(party.getId().toString());
		wallet.setMoney(Arith.roundDown(test_user_money,2));
		this.walletService.save(wallet);

//		User user = new User();
//		user.setPartyId(party.getId());
//		this.getHibernateTemplate().save(user);

//		this.userDataService.saveRegister(party.getId());

	}

	private String getUsercode() {
		Syspara syspara = this.sysparaService.find("user_uid_sequence");
		int random = (int) (Math.random() * 3 + 1);
		int user_uid_sequence = syspara.getInteger() + random;
		syspara.setValue(user_uid_sequence);
		this.sysparaService.update(syspara);
		String usercode = String.valueOf(user_uid_sequence);
//		Party party = this.partyService.findPartyByUsercode(usercode);
//		if (party != null) {
//			usercode = getUsercode();
//		}
		return usercode;
	}

	public List<ImageQr> findImageByUsercode(String usercode, String img_language, String img_type) {
		if ("poster".equals(img_type)) {
			List list = getHibernateTemplate().find(
					" FROM ImageQr WHERE img_language = ? and img_type = ? and usercode = ?  ",
					new Object[] { img_language, img_type, usercode });

			if (list.size() > 0) {
				return list;
			}

		}
		if ("qr".equals(img_type)) {
			List list = getHibernateTemplate().find(" FROM ImageQr WHERE usercode = ?  and img_type = ? ",
					new Object[] { usercode, img_type });
			if (list.size() > 0) {
				return list;
			}
		}
		return null;
	}

//	public List<ImageQr> findAndSaveImageByUsercode(String usercode, String img_language, String img_type) {
//		if ("poster".equals(img_type)) {
//			List list = findImageByUsercode(usercode, img_language, img_type);
//
//			if (list != null) {
//				return list;
//			} else {
//				String image_name = qRGenerateService.generate185(usercode);
//				PosterBase64Thread posterBase64Thread = new PosterBase64Thread(image_name, usercode, img_language,
//						img_type);
//
//				Thread t = new Thread(posterBase64Thread);
//				t.start();
//			}
//
//		}
//		if ("qr".equals(img_type)) {
//			List list = findImageByUsercode(usercode, img_language, img_type);
//			if (list != null) {
//				return list;
//			} else {
//				String image_name = qRGenerateService.generate(usercode);
//
//				byte[] data = null;
//				// 读取图片字节数组
//				try {
//					InputStream in = new FileInputStream(Constants.IMAGES_DIR + image_name);
//					data = new byte[in.available()];
//					in.read(data);
//					in.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				BASE64Encoder encoder = new BASE64Encoder();
//				ImageQr image = new ImageQr();
//				image.setImg_language("");
//				image.setImg_type(img_type);
//				image.setUsercode(usercode);
//				image.setImg_name(usercode + "_qr");
//				image.setCreate_time(new Date());
//				image.setContent(encoder.encode(Objects.requireNonNull(data)));
//				saveImageQr(image);
//				List<ImageQr> imageqr = new ArrayList<ImageQr>();
//				imageqr.add(image);
//				return imageqr;
//
//			}
//		}
//		return null;
//
//	}
//
//	public void saveImageQr(ImageQr entity) {
//		this.getHibernateTemplate().save(entity);
//	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

//	public void setIdentifyingCodeTimeWindow(IdentifyingCodeTimeWindow identifyingCodeTimeWindow) {
//		this.identifyingCodeTimeWindow = identifyingCodeTimeWindow;
//	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	@Override
	public String getPhone(String partyId) {
		// TODO Auto-generated method stub
		Party party = this.partyService.cachePartyBy(partyId, false);
		return party.getPhone();
	}

	@Override
	public void savePhone(String phone, String partyId) {
		/**
		 * party
		 */
		Party party = this.partyService.cachePartyBy(partyId, false);
		party.setPhone(phone);
		party.setPhone_authority(true);

		// 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
		// 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
		// 如：级别11表示：新注册的前端显示为VIP1；
		int userLevel = party.getUser_level();
		party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + 2);
				
		this.partyService.update(party);
	}

	@Override
	public String getEmail(String partyId) {
		// TODO Auto-generated method stub
		Party party = this.partyService.cachePartyBy(partyId, false);
		return party.getEmail();
	}

	@Override
	public void saveEmail(String email, String partyId) {
		/**
		 * party
		 */
		Party party = this.partyService.cachePartyBy(partyId, false);
		party.setEmail(email);
		party.setEmail_authority(true);

		// 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
		// 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
		// 如：级别11表示：新注册的前端显示为VIP1；
		int userLevel = party.getUser_level();
		party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + 2);
		
		this.partyService.update(party);
	}


	public void setqRGenerateService(QRGenerateService qRGenerateService) {
		this.qRGenerateService = qRGenerateService;
	}

	public class PosterThread implements Runnable {
		String image_name;
		String usercode;

		public void run() {
			try {
				qRGenerateService.generate_poster(image_name, usercode);

			} catch (Exception e) {
				logger.error("error:", e);
			}

		}

		public PosterThread(String image_name, String usercode) {
			this.image_name = image_name;
			this.usercode = usercode;
		}

	}

//	public class PosterBase64Thread implements Runnable {
//		String image_name;
//		String usercode;
//		String img_language;
//		String img_type;
//
//		public void run() {
//			try {
//				List<Map<String, String>> list_image = qRGenerateService.generate_poster_base64(image_name, usercode,
//						img_language);
//				List list = findImageByUsercode(usercode, img_language, img_type);
//				if (list == null) {
//					if (list_image != null) {
//						for (int i = 0; i < list_image.size(); i++) {
//							ImageQr image = new ImageQr();
//							image.setImg_language(img_language);
//							image.setImg_type(img_type);
//							image.setUsercode(usercode);
//							image.setCreate_time(new Date());
//							image.setImg_name(usercode + "_poster_" + i + "_" + img_language);
//							image.setContent(list_image.get(i).get(usercode + "_poster_" + i + "_" + img_language));
//							saveImageQr(image);
//						}
//					}
//				}
//
//			} catch (Exception e) {
//				logger.error("error:", e);
//			}
//
//		}
//
//		public PosterBase64Thread(String image_name, String usercode, String img_language, String img_type) {
//			this.image_name = image_name;
//			this.usercode = usercode;
//			this.img_language = img_language;
//			this.img_type = img_type;
//		}
//
//	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
		this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public void setMoneyLogService(MoneyLogService moneyLogService) {
		this.moneyLogService = moneyLogService;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
}
