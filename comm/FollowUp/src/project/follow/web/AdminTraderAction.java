package project.follow.web;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.follow.AdminTraderService;
import project.follow.Trader;
import project.party.PartyService;
import project.party.model.Party;

public class AdminTraderAction extends PageActionSupport {

	private static final long serialVersionUID = 6138149637303319862L;

	private static Log logger = LogFactory.getLog(AdminTraderAction.class);

	private AdminTraderService adminTraderService;
	private PartyService partyService;

	/**
	 * 查询参数
	 */
	private String name_para;

	private String username_para;

	/**
	 * 修改参数
	 */

	/**
	 * 用户名称
	 */
	/**
	 * 用户Uid
	 */
	private String usercode;

	private String id;
	/**
	 * 交易员名称
	 */
	private String name;
	/**
	 * 交易员简介
	 */
	private String remarks;
	/**
	 * 带单币种（多品种的话用;隔开）
	 */
	private String symbols;

	/**
	 * 近3周收益
	 */
	private double week_3_profit;
	/**
	 * 近3周累计金额
	 */
	private double week_3_order_amount;

	/**
	 * 近3周收益率
	 */
	private double week_3_profit_ratio;
	/**
	 * 近3周盈利笔数
	 */
	private int week_3_order_profit;
	/**
	 * 近3周交易笔数
	 */
	private int week_3_order_sum;

	/**
	 * 累计金额
	 */
	private double order_amount;

	/**
	 * 累计收益
	 */
	private double profit;

	/**
	 * 累计收益率
	 */
	private double profit_ratio;

	/**
	 * 累计盈利笔数
	 */
	private int order_profit;

	/**
	 * 累计亏损笔数
	 */
	private int order_loss;
	/**
	 * 累计交易笔数
	 */
	private int order_sum;

	/**
	 * 累计跟随人数
	 */
	private int follower_sum;

	/**
	 * 利润分成比例---PROFIT_SHARE_RATIO
	 */
	private double profit_share_ratio;

	/**
	 * 状态（是否开启跟单）---STATE,0为未开启，1为开启
	 */
	private String state;

	/**
	 * 当前跟随人数---FOLLOWER_NOW
	 */
	private int follower_now;

	/**
	 * 此次跟单最多跟随人数---FOLLOWER_MAX
	 */
	private int follower_max;

	/**
	 * 入驻时间----CREATE_TIME
	 */
	private Date create_time;
	/**
	 * 头像图片---IMG
	 */
	private String img;

	/**
	 * 近3周收益--------------------偏差值
	 */
	private double deviation_week_3_profit;
	/**
	 * 近3周累计金额-偏差值
	 */
	private double deviation_week_3_order_amount;

	/**
	 * 近3周收益率-偏差值
	 */
	private double deviation_week_3_profit_ratio;
	/**
	 * 近3周盈利笔数-偏差值
	 */
	private int deviation_week_3_order_profit;
	/**
	 * 近3周交易笔数-偏差值
	 */
	private int deviation_week_3_order_sum;
	/**
	 * 累计金额-偏差值
	 */
	private double deviation_order_amount;

	/**
	 * 累计收益-偏差值
	 */
	private double deviation_profit;

	/**
	 * 累计收益率-偏差值
	 */
	private double deviation_profit_ratio;

	/**
	 * 累计盈利笔数-偏差值
	 */
	private int deviation_order_profit;

	/**
	 * 累计亏损笔数-偏差值
	 */
	private int deviation_order_loss;
	/**
	 * 累计交易笔数-偏差值
	 */
	private int deviation_order_sum;

	/**
	 * 累计跟随人数-偏差值
	 */
	private int deviation_follower_sum;

	/**
	 * 当前跟随人数-偏差值---DEVIATION_FOLLOWER_NOW
	 */
	private int deviation_follower_now;
	/**
	 * 跟单最小下单数
	 */
	private int follow_volumn_min;

	public String list() {

		this.pageSize = 20;
		this.page = this.adminTraderService.pagedQuery(this.pageNo, this.pageSize, this.name_para, this.username_para);
		for (Map<String, Object> data : (List<Map<String, Object>>) (page.getElements())) {
			data.put("profit_ratio", Arith.mul(Arith.add(Double.parseDouble(data.get("profit_ratio").toString()),
					Double.parseDouble(data.get("deviation_profit_ratio").toString())), 100));
			data.put("profit_share_ratio",
					Arith.mul(Double.parseDouble(data.get("profit_share_ratio").toString()), 100));
			data.put("follower_now", Arith.add(Double.parseDouble(data.get("follower_now").toString()),
					Double.parseDouble(data.get("deviation_follower_now").toString())));
			data.put("follower_sum", Arith.add(Double.parseDouble(data.get("follower_sum").toString()),
					Double.parseDouble(data.get("deviation_follower_sum").toString())));
			data.put("profit", Arith.add(Double.parseDouble(data.get("profit").toString()),
					Double.parseDouble(data.get("deviation_profit").toString())));
		}
		return "list";
	}

	public String toAdd() {
		return "add";
	}

	private String verification() {
		if (StringUtils.isEmptyString(this.name))
			return "请输入交易员名称";
		if (StringUtils.isEmptyString(this.img))
			return "请上传交易员头像";
		if (this.create_time == null)
			return "请输入入驻时间";
//		if (StringUtils.isEmptyString(this.remarks))
//			return "请输入交易员简介";
		if (StringUtils.isEmptyString(this.symbols))
			return "请输入带币品种";
//		if (Arith.add(this.profit,this.deviation_profit) < 0.0D) 
//			return "累计收益加偏差值不能小于0";
//		if (Arith.add(this.profit_ratio,this.deviation_profit_ratio) < 0.0D) 
//			return "累计收益率加偏差值不能小于0";
		if (Arith.add(this.order_profit, this.deviation_order_profit) < 0)
			return "累计盈利笔数加偏差值不能小于0";
		if (Arith.add(this.order_loss, this.deviation_order_loss) < 0)
			return "累计亏损笔数加偏差值不能小于0";
//		if (Arith.add(this.week_3_profit,this.deviation_week_3_profit) < 0.0D) 
//			return "近3周收益加偏差值不能小于0";
		if (Arith.add(this.week_3_order_amount, this.deviation_week_3_order_amount) < 0.0D)
			return "近3周累计金额加偏差值不能小于0";
//		if (Arith.add(this.week_3_profit_ratio,this.deviation_week_3_profit_ratio) < 0.0D) 
//			return "近3周收益率加偏差值不能小于0";
		if (Arith.add(this.week_3_order_profit, this.deviation_week_3_order_profit) < 0)
			return "近3周盈利笔数加偏差值不能小于0";
		if (Arith.add(this.week_3_order_sum, this.deviation_week_3_order_sum) < 0)
			return "近3周交易笔数加偏差值不能小于0";
		if (Arith.add(this.order_amount, this.deviation_order_amount) < 0.0D)
			return "累计金额加偏差值不能小于0";
		if (Arith.add(this.follower_sum, this.deviation_follower_sum) < 0)
			return "累计跟随加偏差值人数不能小于0";
		if (Arith.add(this.follower_now, this.deviation_follower_now) < 0)
			return "当前跟随人数加偏差值不能小于0";
		if (this.profit_share_ratio < 0.0D)
			return "利润分成比例不能小于0";
		if (this.follower_max <= 0)
			return "此次跟单最多跟随人数不能小于等于0";
		if (StringUtils.isEmptyString(this.img))
			return "请上传头像";
		if (this.follower_max < Arith.add(this.follower_now, this.deviation_follower_now))
			return "此次跟单最多跟随人数不能小于当前跟随人数加偏差值";
		if (this.follow_volumn_min < 0)
			return "最小跟单张数不能小于0";
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
			if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
				this.error = "试用用户无法添加";
				return toAdd();
			}
			if (this.adminTraderService.findByPartyId(party.getId().toString()) != null) {
				this.error = "交易员已存在!";
				return toAdd();
			}
			Trader trader = new Trader();
			trader.setPartyId(party.getId());
			trader.setName(this.name);
			trader.setRemarks(this.remarks);
			trader.setSymbols(this.symbols);
			/**
			 * 统计数据
			 */
			trader.setWeek_3_profit(this.week_3_profit);
			trader.setWeek_3_order_amount(this.week_3_order_amount);
			trader.setWeek_3_profit_ratio(Arith.div(this.week_3_profit_ratio, 100));
			trader.setWeek_3_order_profit(this.week_3_order_profit);
			trader.setWeek_3_order_sum(this.week_3_order_sum);
			trader.setOrder_amount(this.order_amount);
			trader.setProfit(this.profit);
			trader.setProfit_ratio(Arith.div(this.profit_ratio, 100));
			trader.setOrder_profit(this.order_profit);
			trader.setOrder_loss(this.order_loss);
			trader.setOrder_sum((int) Arith.add(this.order_loss, this.order_profit));
			trader.setFollower_sum(this.follower_sum);
			trader.setFollower_now(this.follower_now);
			/**
			 * 偏差值Deviation_w
			 */
			trader.setDeviation_week_3_profit(this.deviation_week_3_profit);
			trader.setDeviation_week_3_order_amount(this.deviation_week_3_order_amount);
			trader.setDeviation_week_3_profit_ratio(Arith.div(this.deviation_week_3_profit_ratio, 100));
			trader.setDeviation_week_3_order_profit(this.deviation_week_3_order_profit);
			trader.setDeviation_week_3_order_sum(this.deviation_week_3_order_sum);
			trader.setDeviation_order_amount(this.deviation_order_amount);
			trader.setDeviation_profit(this.deviation_profit);
			trader.setDeviation_profit_ratio(Arith.div(this.deviation_profit_ratio, 100));
			trader.setDeviation_order_profit(this.deviation_order_profit);
			trader.setDeviation_order_loss(this.deviation_order_loss);
			trader.setDeviation_order_sum((int) Arith.add(this.deviation_order_loss, this.deviation_order_profit));
			trader.setDeviation_follower_sum(this.deviation_follower_sum);
			trader.setDeviation_follower_now(this.deviation_follower_now);

			trader.setProfit_share_ratio(Arith.div(this.profit_share_ratio, 100));
			trader.setState(this.state);
			trader.setFollower_max(this.follower_max);
			trader.setCreate_time(this.create_time);
			trader.setImg(this.img);
			trader.setFollow_volumn_min(follow_volumn_min);

			this.adminTraderService.save(trader);
			this.message = "操作成功";
		} catch (BusinessException e) {
			this.error = e.getMessage();
			return toAdd();
		} catch (Throwable t) {
			logger.error("UserAction.register error ", t);
			this.error = "[ERROR] " + t.getMessage();
			return toAdd();
		}
		return list();
	}

	private String verificationUpdate() {
		if (StringUtils.isEmptyString(this.name))
			return "请输入交易员名称";
		if (StringUtils.isEmptyString(this.img))
			return "请上传交易员头像";
		if (this.create_time == null)
			return "请输入入驻时间";
//			if (StringUtils.isEmptyString(this.remarks))
//				return "请输入交易员简介";
		if (StringUtils.isEmptyString(this.symbols))
			return "请输入带币品种";
		if (this.profit_share_ratio < 0.0D)
			return "利润分成比例不能小于0";

		if (this.follower_max <= 0)
			return "此次跟单最多跟随人数不能小于等于0";
		if (StringUtils.isEmptyString(this.img))
			return "请上传头像";

//			if (Arith.add(this.profit,this.deviation_profit) < 0.0D) 
//			return "累计收益加偏差值不能小于0";
//		if (Arith.add(this.profit_ratio,this.deviation_profit_ratio) < 0.0D) 
//			return "累计收益率加偏差值不能小于0";
		if (Arith.add(this.order_profit, this.deviation_order_profit) < 0)
			return "累计盈利笔数加偏差值不能小于0";
		if (Arith.add(this.order_loss, this.deviation_order_loss) < 0)
			return "累计亏损笔数加偏差值不能小于0";
//		if (Arith.add(this.week_3_profit,this.deviation_week_3_profit) < 0.0D) 
//			return "近3周收益加偏差值不能小于0";
		if (Arith.add(this.week_3_order_amount, this.deviation_week_3_order_amount) < 0.0D)
			return "近3周累计金额加偏差值不能小于0";
//		if (Arith.add(this.week_3_profit_ratio,this.deviation_week_3_profit_ratio) < 0.0D) 
//			return "近3周收益率加偏差值不能小于0";
		if (Arith.add(this.week_3_order_profit, this.deviation_week_3_order_profit) < 0)
			return "近3周盈利笔数加偏差值不能小于0";
		if (Arith.add(this.week_3_order_sum, this.deviation_week_3_order_sum) < 0)
			return "近3周交易笔数加偏差值不能小于0";
		if (Arith.add(this.order_amount, this.deviation_order_amount) < 0.0D)
			return "累计金额加偏差值不能小于0";
		if (Arith.add(this.follower_sum, this.deviation_follower_sum) < 0)
			return "累计跟随加偏差值人数不能小于0";

		if (Arith.add(this.follower_now, this.deviation_follower_now) < 0)
			return "当前跟随人数加偏差值不能小于0";

		if (this.follower_max < Arith.add(this.follower_now, this.deviation_follower_now))
			return "此次跟单最多跟随人数不能小于当前跟随人数加偏差值";

//			if (this.daily_rate < 0.0D) {
//				return "日利率不能小于0";
//			}
//			
		if (this.follow_volumn_min < 0)
			return "最小跟单张数不能小于0";
		return null;
	}

	public String toUpdate() {
		Trader trader = this.adminTraderService.findById(this.id);

		Party party = this.partyService.cachePartyBy(trader.getPartyId(), true);
		this.usercode = party.getUsercode();

		this.name = trader.getName();
		this.remarks = trader.getRemarks();
		this.symbols = trader.getSymbols();
		this.profit = trader.getProfit();
		this.profit_ratio = Arith.mul(trader.getProfit_ratio(), 100);
		this.order_profit = trader.getOrder_profit();
		this.order_loss = trader.getOrder_loss();

		this.follower_sum = trader.getFollower_sum();
		this.profit_share_ratio = Arith.mul(trader.getProfit_share_ratio(), 100);
		this.follower_max = trader.getFollower_max();
		this.follower_now = trader.getFollower_now();

		this.week_3_profit = trader.getWeek_3_profit();
		this.week_3_order_amount = trader.getWeek_3_order_amount();
		this.week_3_profit_ratio = Arith.mul(trader.getWeek_3_profit_ratio(), 100);
		this.week_3_order_profit = trader.getWeek_3_order_profit();
		this.week_3_order_sum = trader.getWeek_3_order_sum();
		this.order_amount = trader.getOrder_amount();

		/**
		 * 偏差值Deviation_w
		 */
		this.deviation_week_3_profit = trader.getDeviation_week_3_profit();
		this.deviation_week_3_order_amount = trader.getDeviation_week_3_order_amount();
		this.deviation_week_3_profit_ratio = Arith.mul(trader.getDeviation_week_3_profit_ratio(), 100);
		this.deviation_week_3_order_profit = trader.getDeviation_week_3_order_profit();
		this.deviation_week_3_order_sum = trader.getDeviation_week_3_order_sum();
		this.deviation_order_amount = trader.getDeviation_order_amount();
		this.deviation_profit = trader.getDeviation_profit();
		this.deviation_profit_ratio = Arith.mul(trader.getDeviation_profit_ratio(), 100);
		this.deviation_order_profit = trader.getDeviation_order_profit();
		this.deviation_order_loss = trader.getDeviation_order_loss();
		this.deviation_follower_sum = trader.getDeviation_follower_sum();
		this.deviation_follower_now = trader.getDeviation_follower_now();

		this.img = trader.getImg();
		this.state = trader.getState();
		this.create_time = DateUtils.toDate(trader.getCreate_time().toString(), DateUtils.DF_yyyyMMdd);
		this.follow_volumn_min = trader.getFollow_volumn_min();

		return "update";
	}

	public String update() {

		Trader trader = this.adminTraderService.findById(this.id);
		try {
			this.error = verificationUpdate();
			if (!StringUtils.isNullOrEmpty(this.error))
				return "update";

			trader.setName(this.name);
			trader.setRemarks(this.remarks);
			trader.setSymbols(this.symbols);
			/**
			 * 统计数据
			 */
			trader.setWeek_3_profit(this.week_3_profit);
			trader.setWeek_3_order_amount(this.week_3_order_amount);
			trader.setWeek_3_profit_ratio(Arith.div(this.week_3_profit_ratio, 100));
			trader.setWeek_3_order_profit(this.week_3_order_profit);
			trader.setWeek_3_order_sum(this.week_3_order_sum);
			trader.setOrder_amount(this.order_amount);
			trader.setProfit(this.profit);
			trader.setProfit_ratio(Arith.div(this.profit_ratio, 100));
			trader.setOrder_profit(this.order_profit);
			trader.setOrder_loss(this.order_loss);
			trader.setOrder_sum((int) Arith.add(this.order_loss, this.order_profit));
			trader.setFollower_sum(this.follower_sum);
			trader.setFollower_now(this.follower_now);

			/**
			 * 偏差值Deviation_w
			 */
			trader.setDeviation_week_3_profit(this.deviation_week_3_profit);
			trader.setDeviation_week_3_order_amount(this.deviation_week_3_order_amount);
			trader.setDeviation_week_3_profit_ratio(Arith.div(this.deviation_week_3_profit_ratio, 100));
			trader.setDeviation_week_3_order_profit(this.deviation_week_3_order_profit);
			trader.setDeviation_week_3_order_sum(this.deviation_week_3_order_sum);
			trader.setDeviation_order_amount(this.deviation_order_amount);
			trader.setDeviation_profit(this.deviation_profit);
			trader.setDeviation_profit_ratio(Arith.div(this.deviation_profit_ratio, 100));
			trader.setDeviation_order_profit(this.deviation_order_profit);
			trader.setDeviation_order_loss(this.deviation_order_loss);
			trader.setDeviation_order_sum((int) Arith.add(this.deviation_order_loss, this.deviation_order_profit));
			trader.setDeviation_follower_sum(this.deviation_follower_sum);
			trader.setDeviation_follower_now(this.deviation_follower_now);

			trader.setState(this.state);
			trader.setFollower_max(this.follower_max);
			trader.setProfit_share_ratio(Arith.div(this.profit_share_ratio, 100));
			trader.setCreate_time(this.create_time);
			trader.setImg(this.img);
			trader.setFollow_volumn_min(follow_volumn_min);

			this.adminTraderService.update(trader);
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

			this.adminTraderService.delete(this.id);
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

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}

	public String getSymbols() {
		return symbols;
	}

	public void setSymbols(String symbols) {
		this.symbols = symbols;
	}

	public double getProfit() {
		return profit;
	}

	public void setProfit(double profit) {
		this.profit = profit;
	}

	public double getProfit_ratio() {
		return profit_ratio;
	}

	public void setProfit_ratio(double profit_ratio) {
		this.profit_ratio = profit_ratio;
	}

	public int getOrder_profit() {
		return order_profit;
	}

	public void setOrder_profit(int order_profit) {
		this.order_profit = order_profit;
	}

	public int getOrder_loss() {
		return order_loss;
	}

	public void setOrder_loss(int order_loss) {
		this.order_loss = order_loss;
	}

	public int getOrder_sum() {
		return order_sum;
	}

	public void setOrder_sum(int order_sum) {
		this.order_sum = order_sum;
	}

	public int getFollower_sum() {
		return follower_sum;
	}

	public void setFollower_sum(int follower_sum) {
		this.follower_sum = follower_sum;
	}

	public double getProfit_share_ratio() {
		return profit_share_ratio;
	}

	public void setProfit_share_ratio(double profit_share_ratio) {
		this.profit_share_ratio = profit_share_ratio;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public int getFollower_now() {
		return follower_now;
	}

	public void setFollower_now(int follower_now) {
		this.follower_now = follower_now;
	}

	public int getFollower_max() {
		return follower_max;
	}

	public void setFollower_max(int follower_max) {
		this.follower_max = follower_max;
	}

	public Date getCreate_time() {
		return create_time;
	}

	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}

	public void setAdminTraderService(AdminTraderService adminTraderService) {
		this.adminTraderService = adminTraderService;
	}

	public double getWeek_3_profit() {
		return week_3_profit;
	}

	public void setWeek_3_profit(double week_3_profit) {
		this.week_3_profit = week_3_profit;
	}

	public double getWeek_3_order_amount() {
		return week_3_order_amount;
	}

	public void setWeek_3_order_amount(double week_3_order_amount) {
		this.week_3_order_amount = week_3_order_amount;
	}

	public double getWeek_3_profit_ratio() {
		return week_3_profit_ratio;
	}

	public void setWeek_3_profit_ratio(double week_3_profit_ratio) {
		this.week_3_profit_ratio = week_3_profit_ratio;
	}

	public Integer getWeek_3_order_profit() {
		return week_3_order_profit;
	}

	public void setWeek_3_order_profit(Integer week_3_order_profit) {
		this.week_3_order_profit = week_3_order_profit;
	}

	public Integer getWeek_3_order_sum() {
		return week_3_order_sum;
	}

	public void setWeek_3_order_sum(Integer week_3_order_sum) {
		this.week_3_order_sum = week_3_order_sum;
	}

	public double getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(double order_amount) {
		this.order_amount = order_amount;
	}

	public double getDeviation_week_3_profit() {
		return deviation_week_3_profit;
	}

	public double getDeviation_week_3_order_amount() {
		return deviation_week_3_order_amount;
	}

	public double getDeviation_week_3_profit_ratio() {
		return deviation_week_3_profit_ratio;
	}

	public int getDeviation_week_3_order_profit() {
		return deviation_week_3_order_profit;
	}

	public int getDeviation_week_3_order_sum() {
		return deviation_week_3_order_sum;
	}

	public double getDeviation_order_amount() {
		return deviation_order_amount;
	}

	public double getDeviation_profit() {
		return deviation_profit;
	}

	public double getDeviation_profit_ratio() {
		return deviation_profit_ratio;
	}

	public int getDeviation_order_profit() {
		return deviation_order_profit;
	}

	public int getDeviation_order_loss() {
		return deviation_order_loss;
	}

	public int getDeviation_order_sum() {
		return deviation_order_sum;
	}

	public int getDeviation_follower_sum() {
		return deviation_follower_sum;
	}

	public int getDeviation_follower_now() {
		return deviation_follower_now;
	}

	public void setWeek_3_order_profit(int week_3_order_profit) {
		this.week_3_order_profit = week_3_order_profit;
	}

	public void setWeek_3_order_sum(int week_3_order_sum) {
		this.week_3_order_sum = week_3_order_sum;
	}

	public void setDeviation_week_3_profit(double deviation_week_3_profit) {
		this.deviation_week_3_profit = deviation_week_3_profit;
	}

	public void setDeviation_week_3_order_amount(double deviation_week_3_order_amount) {
		this.deviation_week_3_order_amount = deviation_week_3_order_amount;
	}

	public void setDeviation_week_3_profit_ratio(double deviation_week_3_profit_ratio) {
		this.deviation_week_3_profit_ratio = deviation_week_3_profit_ratio;
	}

	public void setDeviation_week_3_order_profit(int deviation_week_3_order_profit) {
		this.deviation_week_3_order_profit = deviation_week_3_order_profit;
	}

	public void setDeviation_week_3_order_sum(int deviation_week_3_order_sum) {
		this.deviation_week_3_order_sum = deviation_week_3_order_sum;
	}

	public void setDeviation_order_amount(double deviation_order_amount) {
		this.deviation_order_amount = deviation_order_amount;
	}

	public void setDeviation_profit(double deviation_profit) {
		this.deviation_profit = deviation_profit;
	}

	public void setDeviation_profit_ratio(double deviation_profit_ratio) {
		this.deviation_profit_ratio = deviation_profit_ratio;
	}

	public void setDeviation_order_profit(int deviation_order_profit) {
		this.deviation_order_profit = deviation_order_profit;
	}

	public void setDeviation_order_loss(int deviation_order_loss) {
		this.deviation_order_loss = deviation_order_loss;
	}

	public void setDeviation_order_sum(int deviation_order_sum) {
		this.deviation_order_sum = deviation_order_sum;
	}

	public void setDeviation_follower_sum(int deviation_follower_sum) {
		this.deviation_follower_sum = deviation_follower_sum;
	}

	public void setDeviation_follower_now(int deviation_follower_now) {
		this.deviation_follower_now = deviation_follower_now;
	}

	public int getFollow_volumn_min() {
		return follow_volumn_min;
	}

	public void setFollow_volumn_min(int follow_volumn_min) {
		this.follow_volumn_min = follow_volumn_min;
	}

}
