package project.follow.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.web.PageActionSupport;
import project.follow.AdminTraderFollowUserOrderService;
import project.follow.AdminTraderFollowUserService;
import project.follow.AdminTraderService;
import project.party.PartyService;

public class AdminTraderFollowUserOrderAction extends PageActionSupport {

	private static final long serialVersionUID = -8594296082160028590L;

	private static Log logger = LogFactory.getLog(AdminTraderFollowUserOrderAction.class);

	private AdminTraderFollowUserOrderService adminTraderFollowUserOrderService;
	private PartyService partyService;

	/**
	 * 查询参数 交易员名称
	 */
	private String name_para;
	/**
	 * 用户名
	 */
	private String username_para;
	
	private String rolename_para;

	/**
	 * 添加用户类型 '1':'真实用户','2':'虚假用户'
	 */
	private String user_type;
	
	private String username;

	private String id;


	public String list() {

		this.pageSize = 20;
		this.page = this.adminTraderFollowUserOrderService.pagedQuery(this.pageNo, this.pageSize, this.name_para,
				this.username_para,this.rolename_para);

		return "list";
	}



	public PartyService getPartyService() {
		return partyService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public String getName_para() {
		return name_para;
	}

	public void setName_para(String name_para) {
		this.name_para = name_para;
	}

	public String getUsername_para() {
		return username_para;
	}

	public void setUsername_para(String username_para) {
		this.username_para = username_para;
	}

	public String getUser_type() {
		return user_type;
	}



	public String getUsername() {
		return username;
	}



	public String getId() {
		return id;
	}



	public void setAdminTraderFollowUserOrderService(AdminTraderFollowUserOrderService adminTraderFollowUserOrderService) {
		this.adminTraderFollowUserOrderService = adminTraderFollowUserOrderService;
	}



	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public void setId(String id) {
		this.id = id;
	}



	public String getRolename_para() {
		return rolename_para;
	}

	public void setRolename_para(String rolename_para) {
		this.rolename_para = rolename_para;
	}
	
	

}
