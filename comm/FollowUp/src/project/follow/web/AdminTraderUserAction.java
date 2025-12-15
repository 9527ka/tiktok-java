package project.follow.web;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.follow.AdminTraderUserService;
import project.follow.TraderUser;
import project.party.PartyService;
import project.party.model.Party;

public class AdminTraderUserAction extends PageActionSupport {
	private static final long serialVersionUID = 6847335915005935672L;

	private static Log logger = LogFactory.getLog(AdminTraderUserAction.class);

	private AdminTraderUserService adminTraderUserService;
	private PartyService partyService;

	/**
	 * 查询参数
	 */
	private String name_para;

	private String username_para;

	private String rolename_para;

	/**
	 * 修改参数
	 */

	/**
	 * 用户Uid
	 */
	private String usercode;

	private String id;

	private String name;

	/**
	 * 累计金额
	 */
	private double amount_sum;

	/**
	 * 累计收益
	 */
	private double profit;

	/**
	 * 入驻时间----CREATE_TIME
	 */
	private Date create_time;

	public String list() {

		this.pageSize = 20;
		this.page = this.adminTraderUserService.pagedQuery(this.pageNo, this.pageSize, this.name_para,
				this.username_para);
//		for (Map<String, Object> data : (List<Map<String, Object>>) (page.getElements())) {
//			data.put("profit_ratio", Arith.mul(Double.parseDouble(data.get("profit_ratio").toString()), 100));
//			data.put("profit_share_ratio",
//					Arith.mul(Double.parseDouble(data.get("profit_share_ratio").toString()), 100));
//
//		}
		return "list";
	}

	public String toAdd() {
		return "add";
	}

	private String verification() {
		if (this.create_time == null)
			return "请输入入驻时间";
		if (this.amount_sum < 0.0D)
			return "累计金额不能小于0";
		if (this.profit < 0.0D)
			return "累计收益不能小于0";
		if (StringUtils.isEmptyString(this.name))
			return "请输入名称";

		return null;
	}

	public String add() {
		try {

			this.error = verification();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toAdd();

			Party party = this.partyService.findPartyByUsercode(usercode);
			if (party == null) {
				this.error = "UID不存在!";
				return toAdd();
			}

			TraderUser trader_user = new TraderUser();
			trader_user.setPartyId(party.getId());
			trader_user.setName(this.name);
			trader_user.setAmount_sum(this.amount_sum);
			trader_user.setProfit(this.profit);
			trader_user.setCreate_time(this.create_time);

			this.adminTraderUserService.save(trader_user);
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("AdminTraderUserAction.add error ", t);
			this.error = "[ERROR] " + t.getMessage();
			return toAdd();
		}
		return list();
	}

	private String verificationUpdate() {
		if (this.create_time == null)
			return "请输入入驻时间";
		if (this.amount_sum < 0.0D)
			return "累计金额不能小于0";
		if (this.profit < 0.0D)
			return "累计收益不能小于0";
		if (StringUtils.isEmptyString(this.name))
			return "请输入名称";

		return null;
	}

	public String toUpdate() {
		TraderUser trader_user = this.adminTraderUserService.findById(this.id);

		this.name = trader_user.getName();
		this.amount_sum = trader_user.getAmount_sum();
		this.profit = trader_user.getProfit();
		this.create_time = trader_user.getCreate_time();

		return "update";
	}

	public String update() {

		TraderUser trader_user = this.adminTraderUserService.findById(this.id);
		try {
			this.error = verificationUpdate();
			if (!StringUtils.isNullOrEmpty(this.error))
				return "update";

			trader_user.setName(this.name);
			trader_user.setAmount_sum(this.amount_sum);
			trader_user.setProfit(this.profit);
			trader_user.setCreate_time(this.create_time);

			this.adminTraderUserService.update(trader_user);
			this.message = "操作成功";
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

			this.adminTraderUserService.delete(this.id);
			this.message = "操作成功";
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

	public String getUsercode() {
		return usercode;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getRolename_para() {
		return rolename_para;
	}

	public double getAmount_sum() {
		return amount_sum;
	}

	public void setRolename_para(String rolename_para) {
		this.rolename_para = rolename_para;
	}

	public void setAmount_sum(double amount_sum) {
		this.amount_sum = amount_sum;
	}

	public void setAdminTraderUserService(AdminTraderUserService adminTraderUserService) {
		this.adminTraderUserService = adminTraderUserService;
	}

}
