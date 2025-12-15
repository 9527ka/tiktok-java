package project.monitor.pledge.web;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.log.LogService;
import project.monitor.AdminPledgeOrderService;
import project.monitor.activity.AdminActivityService;
import project.monitor.pledge.PledgeConfig;
import project.monitor.pledge.PledgeConfigService;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeOrderService;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

public class AdminPledgeOrderAction extends PageActionSupport {

	private static final long serialVersionUID = 7387761379600206318L;

	private static Log logger = LogFactory.getLog(AdminPledgeOrderAction.class);

	protected String name_para;
	protected String title_para;

	protected AdminPledgeOrderService adminPledgeOrderService;
	protected PledgeOrderService pledgeOrderService;
	protected PledgeConfigService pledgeConfigService;

	protected PartyService partyService;

	protected LogService logService;
	protected SecUserService secUserService;
	protected PasswordEncoder passwordEncoder;

	/**
	 * 登录人资金密码
	 */
	protected String login_safeword;
	/**
	 * 现金
	 */
	private double usdt = 0.0D;

	/**
	 * 送ETH
	 */
	private double eth = 0.0D;
	protected String id;

	protected String usercode;
	protected String title;
	protected String title_img;
	protected String content;
	protected String content_img;

	/**
	 * 结束时间
	 */
//	private Date endtime;

	/**
	 * 送ETH时间
	 */
//	private Date sendtime;
	/**
	 * 首页弹出新闻，如果为true弹出
	 */
	private Boolean index = false;

//	private Date createTime;

	/**
	 * 状态。0 停用， 1 启用
	 */
	private String state = "0";

//	private String sendtime_update;
//	private String endtime_update;
	
	private String config;
	/**
	 * 限制天数
	 */
	private int	limit_days;

	public String list() {
		this.pageSize = 30;
		this.page = this.adminPledgeOrderService.pagedQuery(this.pageNo, this.pageSize, this.name_para, this.title_para,this.getLoginPartyId());

		for(Map map:(List<Map>)page.getElements()) {
			map.put("income", map.get("income")==null?null:new BigDecimal(map.get("income").toString()).toPlainString());
		}
		return "list";
	}

	public String toAdd() {
		PledgeConfig entity = this.pledgeConfigService.findById("2c948a827cd5f779017cd2322f5d0001");
		this.content_img = entity.getContent_img();
		this.title_img = entity.getTitle_img();
		this.config =entity.getConfig();
		return "add";
	}

	protected String verification() {
		if (StringUtils.isEmptyString(this.usercode))
			return "请输入UID";
		if (StringUtils.isEmptyString(this.title))
			return "请输入质押标题文本";
		if (StringUtils.isEmptyString(this.title_img))
			return "请上传质押标题图片";

		if (StringUtils.isEmptyString(this.content_img))
			return "请上传质押内容图片";
		if (StringUtils.isEmptyString(this.config))
			return "请输入收益费率";
//		if (this.sendtime == null) {
//			return "请输入活动奖励派发时间";
//		}
		return null;
	}

	public String add() {
		try {
			this.error = verification();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toAdd();

			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return toAdd();
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), this.login_safeword);

			Party party = partyService.findPartyByUsercode(usercode);
			if (party == null) {
				throw new BusinessException("UID不存在");
			}
			if(!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())&&!Constants.SECURITY_ROLE_GUEST.equals(party.getRolename())) {
				throw new BusinessException("只能添加用户质押配置");
			}
			PledgeOrder entity_before = this.pledgeOrderService.findByPartyId(party.getId());

			if (entity_before != null) {
				this.error = "添加失败，该UID配置已存在";
				return toAdd();
			}

			PledgeOrder entity = new PledgeOrder();
			entity.setPartyId(party.getId());
			entity.setConfig(config);
			entity.setUsdt(this.usdt);
			entity.setLimit_days(limit_days);
			entity.setEth(this.eth);
			entity.setTitle(this.title);
			entity.setTitle_img(this.title_img);

			entity.setContent(this.content);
			entity.setContent_img(this.content_img);

//			entity.setSendtime(this.sendtime);
			entity.setCreateTime(new Date());
			
//
			this.pledgeOrderService.save(entity);
			this.message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加用户质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("PledgeOrder.Add error ", t);
			this.error = "[ERROR] " + t.getMessage();
			return toAdd();
		}
		return list();
	}

	protected String verificationUpdate() {

		if (StringUtils.isEmptyString(this.title))
			return "请输入质押标题文本";
		if (StringUtils.isEmptyString(this.title_img))
			return "请上传质押标题图片";

		if (StringUtils.isEmptyString(this.content_img))
			return "请上传质押内容图片";
		if (StringUtils.isEmptyString(this.config))
			return "请输入收益费率";
//		if (this.endtime_update == null) {
//			return "请输入活动准入结束时间";
//		}
//		if (this.sendtime_update == null) {
//			return "请输入奖励派发时间";
//		}

		return null;
	}

	public String toUpdate() {
		PledgeOrder entity = this.pledgeOrderService.findById(this.id);
		Party party = partyService.cachePartyBy(entity.getPartyId(), true);
		this.content = entity.getContent();
		this.content_img = entity.getContent_img();
		this.title = entity.getTitle();
		this.title_img = entity.getTitle_img();
		this.usdt = entity.getUsdt();
		this.eth = entity.getEth();
		config =entity.getConfig();
//		limit_days = entity.getLimit_days();
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		this.sendtime_update = sdf.format(entity.getSendtime());
		if (party != null)
			this.usercode = party.getUsercode();

		return "update";

	}

	public String update() {

//		Finance finance = this.financeService.findById(this.id);
		try {
			this.error = verificationUpdate();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toUpdate();

			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return toUpdate();
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), this.login_safeword);

			PledgeOrder entity = this.pledgeOrderService.findById(this.id);
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			String before_title = entity.getTitle();
			double beforeusdt = entity.getUsdt();
			double before_eth = entity.getEth();
			String before_config = entity.getConfig();
			entity.setContent(this.content);
			entity.setContent_img(this.content_img);
			entity.setTitle(this.title);
			entity.setTitle_img(this.title_img);
			entity.setUsdt(this.usdt);
			entity.setEth(this.eth);
			entity.setConfig(config);
//			entity.setLimit_days(limit_days);
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//			entity.setSendtime(sdf.parse(this.sendtime_update));
			this.pledgeOrderService.update(entity);
			this.message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改用户质押配置,修改前标题文本为：[" + before_title + "],用户USDT限制数量为[" + beforeusdt + "]," + "奖励ETH数量为["
					+ before_eth + "],修改前配置:["+before_config+"],修改后标题文本为[" + this.title + "],用户USDT限制数量为[" + this.usdt + "],奖励ETH数量为[" + this.eth
					+ "],修改后配置:["+config+"],操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);
			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return "update";
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			return "update";
		}
	}

	public String toDelete() {
		try {
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return list();
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), this.login_safeword);

			PledgeOrder entity = this.pledgeOrderService.findById(this.id);
			if(entity.getPartyId()==null||StringUtils.isEmptyString(entity.getPartyId().toString())) {
				throw new BusinessException("全局配置不能删除");
			}
//			if(entity.getApply()) {
//				throw new BusinessException("用户已加入的质押不能删除");
//			}
			this.pledgeOrderService.delete(entity);
			this.message = "操作成功";

			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动删除用户质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return list();
		} catch (Throwable t) {
			logger.error("toDelete error ", t);
			this.error = "程序错误";
			return list();
		}
	}
	public String updateLimitDays() {
		try {
			if (StringUtils.isNullOrEmpty(this.login_safeword)) {
				this.error = "请输入登录人资金密码";
				return list();
			}
			if(limit_days<0) {
				this.error = "请输入正确的限制天数";
				return list();
			}
			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			checkLoginSafeword(sec, this.getUsername_login(), this.login_safeword);

			PledgeOrder entity = this.pledgeOrderService.findById(this.id);
			Party party = null;
			if (!StringUtils.isNullOrEmpty(entity.getPartyId().toString())) {
				party = partyService.cachePartyBy(entity.getPartyId(), true);
			}
			int before_limit_days = entity.getLimit_days();
			entity.setLimit_days(limit_days);
			//已加入的额外修改派送时间
			if(entity.getApply()) {
//				entity.setSendtime(DateUtils.addDay(new Date(), limit_days));
				entity.setSendtime(
						DateUtils.addDate(DateUtils.toDate(DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT)),
								limit_days));
			}
			this.pledgeOrderService.update(entity);
			this.message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改用户质押配置,修改前限制天数为：[" + before_limit_days + "],修改后限制天数为[" + this.limit_days + "],操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);
		} catch (BusinessException e) {
			this.error = e.getMessage();
		} catch (Throwable t) {
			logger.error("updateLimitDays error ", t);
			this.error = "程序错误";
		}
		return list();
	}
	/**
	 * 验证登录人资金密码
	 * 
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = secUser.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
	}

	public String getName_para() {
		return this.name_para;
	}

	public void setName_para(String name_para) {
		this.name_para = name_para;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setLogService(LogService logService) {
		this.logService = logService;
	}

	public String getLogin_safeword() {
		return login_safeword;
	}

	public void setLogin_safeword(String login_safeword) {
		this.login_safeword = login_safeword;
	}

	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}

	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}

	public String getUsercode() {
		return usercode;
	}

	public String getTitle() {
		return title;
	}

	public String getTitle_img() {
		return title_img;
	}

	public String getContent() {
		return content;
	}

	public String getContent_img() {
		return content_img;
	}

	public Boolean getIndex() {
		return index;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setTitle_img(String title_img) {
		this.title_img = title_img;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public void setContent_img(String content_img) {
		this.content_img = content_img;
	}

	public void setIndex(Boolean index) {
		this.index = index;
	}

	public double getUsdt() {
		return usdt;
	}

	public double getEth() {
		return eth;
	}

	public void setUsdt(double usdt) {
		this.usdt = usdt;
	}

	public void setEth(double eth) {
		this.eth = eth;
	}

	public String getTitle_para() {
		return title_para;
	}

	public void setTitle_para(String title_para) {
		this.title_para = title_para;
	}

	public String getConfig() {
		return config;
	}

	public int getLimit_days() {
		return limit_days;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public void setLimit_days(int limit_days) {
		this.limit_days = limit_days;
	}

	public void setPledgeOrderService(PledgeOrderService pledgeOrderService) {
		this.pledgeOrderService = pledgeOrderService;
	}

//	public Date getSendtime() {
//		return sendtime;
//	}
//
//	public String getSendtime_update() {
//		return sendtime_update;
//	}
//
//	public void setSendtime(Date sendtime) {
//		this.sendtime = sendtime;
//	}
//
//	public void setSendtime_update(String sendtime_update) {
//		this.sendtime_update = sendtime_update;
//	}

	public void setAdminPledgeOrderService(AdminPledgeOrderService adminPledgeOrderService) {
		this.adminPledgeOrderService = adminPledgeOrderService;
	}

	public void setPledgeConfigService(PledgeConfigService pledgeConfigService) {
		this.pledgeConfigService = pledgeConfigService;
	}
	

}
