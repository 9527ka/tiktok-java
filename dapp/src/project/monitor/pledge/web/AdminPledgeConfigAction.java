package project.monitor.pledge.web;

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
import project.monitor.activity.AdminActivityService;
import project.monitor.pledge.AdminPledgeConfigService;
import project.monitor.pledge.PledgeConfig;
import project.monitor.pledge.PledgeConfigService;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

public class AdminPledgeConfigAction extends PageActionSupport {

	private static final long serialVersionUID = 7387761379600206318L;

	private static Log logger = LogFactory.getLog(AdminPledgeConfigAction.class);

	protected String name_para;
	protected String title_para;

	protected AdminPledgeConfigService adminPledgeConfigService;
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
	 * 结束时间,送ETH时间
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
		this.page = this.adminPledgeConfigService.pagedQuery(this.pageNo, this.pageSize, this.name_para, this.title_para);
//		List<Map> list = page.getElements();
//
//		for (int i = 0; i < list.size(); i++) {
//
//			Map map = list.get(i);
//			if ("2c948a827cd5f779017cd2322f5d0001".equals((map.get("id") + "").toString())) {
//				Map map2 = list.get(0);
//				list.set(0, list.get(i));
//				list.set(i, map2);
//				break;
//			}
//
//		}
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

		if (StringUtils.isEmptyString(this.config))
			return "请输入收益费率";
		
		
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
			if (!Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())
					&& !Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
				throw new BusinessException("只能增加代理商质押配置");
			}
			PledgeConfig entity_before = this.pledgeConfigService.findByPartyId(party.getId().toString());

			if (entity_before != null) {
				this.error = "添加失败，该UID配置已存在";
				return toAdd();
			}

			PledgeConfig entity = new PledgeConfig();
			entity.setPartyId(party.getId());
			entity.setConfig(config);
			entity.setUsdt(this.usdt);
			entity.setLimit_days(limit_days);
			entity.setEth(this.eth);
			entity.setTitle(this.title);
			entity.setTitle_img(this.title_img);

			entity.setContent(this.content);
			entity.setContent_img(this.content_img);

//
			this.pledgeConfigService.save(entity);
			this.message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setPartyId(party.getId());
			log.setUsername(party.getUsername());
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动添加质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("PledgeConfig.Add error ", t);
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
//			return "请输入活动奖励派发时间";
//		}

		return null;
	}

	public String toUpdate() {
		PledgeConfig entity = this.pledgeConfigService.findById(this.id);
		Party party = partyService.cachePartyBy(entity.getPartyId(), true);
		this.content = entity.getContent();
		this.content_img = entity.getContent_img();
		this.title = entity.getTitle();
		this.title_img = entity.getTitle_img();
		this.usdt = entity.getUsdt();
		this.eth = entity.getEth();
		config =entity.getConfig();
		limit_days = entity.getLimit_days();
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

			PledgeConfig entity = this.pledgeConfigService.findById(this.id);
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
			entity.setLimit_days(limit_days);
			this.pledgeConfigService.update(entity);
			this.message = "操作成功";

			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			if (party != null) {
				log.setUsername(party.getUsername());
				log.setPartyId(party.getId());
			}
			log.setOperator(this.getUsername_login());
			log.setLog("管理员手动修改质押配置,修改前标题文本为：[" + before_title + "],用户USDT限制数量为[" + beforeusdt + "]," + "奖励ETH数量为["
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

			PledgeConfig entity = this.pledgeConfigService.findById(this.id);
			if(entity.getPartyId()==null||StringUtils.isEmptyString(entity.getPartyId().toString())) {
				throw new BusinessException("全局配置不能删除");
			}
			this.pledgeConfigService.delete(entity);
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
			log.setLog("管理员手动删除质押配置,操作ip:[" + this.getIp(getRequest()) + "]");
			logService.saveSync(log);

			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return list();
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			return list();
		}
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

	public void setPledgeConfigService(PledgeConfigService pledgeConfigService) {
		this.pledgeConfigService = pledgeConfigService;
	}

	public void setAdminPledgeConfigService(AdminPledgeConfigService adminPledgeConfigService) {
		this.adminPledgeConfigService = adminPledgeConfigService;
	}

}
