package project.follow.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.follow.AdminTraderFollowUserService;
import project.follow.AdminTraderService;
import project.follow.Trader;
import project.follow.TraderFollowUser;
import project.party.PartyService;
import project.party.model.Party;

public class AdminTraderFollowUserAction extends PageActionSupport {

	private static final long serialVersionUID = 8332128342431706931L;

	private static Log logger = LogFactory.getLog(AdminTraderFollowUserAction.class);

	private AdminTraderService adminTraderService;
	private AdminTraderFollowUserService adminTraderFollowUserService;
	private PartyService partyService;

	/**
	 * 查询参数 交易员名称
	 */
	private String name_para;
	/**
	 * 用户名
	 */
	private String username_para;

	/**
	 * 修改参数
	 */

	/**
	 * 用户Uid
	 */
	private String usercode;
	/**
	 * 添加用户类型 '1':'真实用户','2':'虚假用户'
	 */
	private String user_type;

	private String username;

	private String id;

	private String trader_usercode;
	private String trader_username;

	/**
	 * 跟随购买品种 symbol
	 */
	private String symbol;
	/**
	 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
	 */
	private String follow_type;

	/**
	 * 跟单张数或比例---具体值
	 */
	private double volume;
	/**
	 * 最大持仓张数
	 */
	private double volume_max;
	/**
	 * 累计跟单收益 PROFIT
	 */
	private double profit;
	/**
	 * 累计跟单本金 AMOUNT_SUM
	 */
	private double amount_sum;
	/**
	 * 止盈百分比
	 */
	private double stop_profit;
	/**
	 * 止损百分比
	 */
	private double stop_loss;

	public String list() {

		this.pageSize = 20;
		this.page = this.adminTraderFollowUserService.pagedQuery(this.pageNo, this.pageSize, this.name_para,
				this.username_para);

		return "list";
	}

	public String toAdd() {
		return "add";
	}

	private String verification() {
		if (StringUtils.isEmptyString(this.trader_usercode))
			return "请输入交易员UID";
		if (StringUtils.isEmptyString(this.usercode))
			return "请输入用户UID";

		if (this.volume < 0)
			return "跟单张数或比例不能小于0";
		if (this.volume_max < 0)
			return "最大持仓张数不能小于0";
		if (this.profit < 0)
			return "累计跟单收益不能小于0";
		if (this.amount_sum < 0)
			return "累计跟单本金不能小于0";

		return null;
	}

	public String add() {
		try {

			this.error = verification();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toAdd();

			Party party = this.partyService.findPartyByUsercode(this.usercode);
			if (party == null && "1".equals(this.user_type)) {
				this.error = "用户UID不存在!";
				return toAdd();
			}
			Party trader_party = this.partyService.findPartyByUsercode(this.trader_usercode);
			if (trader_party == null) {
				this.error = "交易员不存在!";
				return toAdd();
			}
			Trader trader = this.adminTraderService.findByPartyId(trader_party.getId().toString());
			if (trader == null) {
				this.error = "交易员不存在!";
				return toAdd();
			}

			TraderFollowUser entity = new TraderFollowUser();
			if (party == null) {
				entity.setPartyId("");
				entity.setUsername(this.username);
			} else {
				entity.setPartyId(party.getId());
				entity.setUsername(party.getUsername());
			}
			/**
			 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
			 */

			entity.setFollow_type(this.follow_type);
			entity.setStop_loss(this.stop_loss);
			entity.setStop_profit(this.stop_profit);
			entity.setSymbol(this.symbol);
			entity.setVolume(this.volume);
			entity.setProfit(this.profit);
			entity.setAmount_sum(this.amount_sum);

			entity.setVolume_max(this.volume_max);
			/**
			 * 状态 是否还在跟随状态 1,跟随，2，取消跟随
			 */
			entity.setState("1");

			this.adminTraderFollowUserService.save(entity, trader.getId().toString());
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("error ", t);
			this.error = "[ERROR] " + t.getMessage();
			return toAdd();
		}
		return list();
	}

	private String verificationUpdate() {

		if (this.volume < 0)
			return "跟单张数或比例不能小于0";
		if (this.volume_max < 0)
			return "最大持仓张数不能小于0";
		if (this.profit < 0)
			return "累计跟单收益不能小于0";
		if (this.amount_sum < 0)
			return "累计跟单本金不能小于0";
		return null;
	}

	public String toUpdate() {

		TraderFollowUser entity = adminTraderFollowUserService.findById(this.id);

		Trader trader = this.adminTraderService.findByPartyId(entity.getTrader_partyId().toString());
		Party party = this.partyService.cachePartyBy(entity.getTrader_partyId().toString(), true);
		this.trader_username = trader.getName();
		this.trader_usercode = party.getUsercode();
		this.username = entity.getUsername();

		this.follow_type = entity.getFollow_type();
		this.stop_loss = entity.getStop_loss();
		this.stop_profit = entity.getStop_profit();
		this.symbol = entity.getSymbol();
		this.volume = entity.getVolume();
		this.volume_max = entity.getVolume_max();
		this.profit = entity.getProfit();
		this.amount_sum = entity.getAmount_sum();

		return "update";
	}

	public String update() {

		try {
			this.error = verificationUpdate();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toUpdate();

			TraderFollowUser entity = adminTraderFollowUserService.findById(this.id);

			/**
			 * 跟单固定张数/固定比例---选择 1,固定张数，2，固定比例
			 */
			entity.setFollow_type(this.follow_type);
			entity.setSymbol(this.symbol);
			entity.setVolume(this.volume);
			entity.setVolume_max(this.volume_max);
			entity.setProfit(this.profit);
			entity.setAmount_sum(this.amount_sum);

			/**
			 * 状态 是否还在跟随状态 1,跟随，2，取消跟随
			 */
			entity.setState("1");

			this.adminTraderFollowUserService.update(entity);
			this.message = "操作成功";
			return list();
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toUpdate();
		} catch (Throwable t) {
			logger.error("update error ", t);
			this.error = "程序错误";
			return toUpdate();
		}
	}

	public String toCancel() {
		try {

			this.adminTraderFollowUserService.delete(this.id);
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

	public String getTrader_usercode() {
		return trader_usercode;
	}

	public void setTrader_usercode(String trader_usercode) {
		this.trader_usercode = trader_usercode;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getFollow_type() {
		return follow_type;
	}

	public void setFollow_type(String follow_type) {
		this.follow_type = follow_type;
	}

	public double getVolume() {
		return volume;
	}

	public void setVolume(double volume) {
		this.volume = volume;
	}

	public double getVolume_max() {
		return volume_max;
	}

	public void setVolume_max(double volume_max) {
		this.volume_max = volume_max;
	}

	public double getStop_profit() {
		return stop_profit;
	}

	public void setStop_profit(double stop_profit) {
		this.stop_profit = stop_profit;
	}

	public double getStop_loss() {
		return stop_loss;
	}

	public void setStop_loss(double stop_loss) {
		this.stop_loss = stop_loss;
	}

	public String getUser_type() {
		return user_type;
	}

	public void setUser_type(String user_type) {
		this.user_type = user_type;
	}

	public void setAdminTraderService(AdminTraderService adminTraderService) {
		this.adminTraderService = adminTraderService;
	}

	public void setAdminTraderFollowUserService(AdminTraderFollowUserService adminTraderFollowUserService) {
		this.adminTraderFollowUserService = adminTraderFollowUserService;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getTrader_username() {
		return trader_username;
	}

	public void setTrader_username(String trader_username) {
		this.trader_username = trader_username;
	}

	public double getProfit() {
		return profit;
	}

	public double getAmount_sum() {
		return amount_sum;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public void setAmount_sum(double amount_sum) {
		this.amount_sum = amount_sum;
	}

}
