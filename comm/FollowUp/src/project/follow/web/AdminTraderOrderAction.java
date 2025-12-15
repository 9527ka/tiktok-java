package project.follow.web;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.follow.AdminTraderFollowUserService;
import project.follow.AdminTraderOrderService;
import project.follow.AdminTraderService;
import project.follow.Trader;
import project.follow.TraderFollowUser;
import project.follow.TraderOrder;
import project.item.ItemService;
import project.item.model.Item;
import project.party.PartyService;
import project.party.model.Party;

public class AdminTraderOrderAction extends PageActionSupport {
	private static final long serialVersionUID = 767846072581152211L;

	private static Log logger = LogFactory.getLog(AdminTraderOrderAction.class);
	
	Map<String,String> item_map = new HashMap<String,String>();

	private AdminTraderService adminTraderService;
	private AdminTraderOrderService adminTraderOrderService;
	private ItemService itemService;
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
	
	private String id;

	/**
	 * 修改参数
	 */
	
	/**
	 * 交易员Uid
	 */
	private String usercode;

	/**
	 * 品种
	 */
	private String symbol;
	/**
	 * 订单 号
	 */
	private String order_no;
	
	/**
	 * "buy":买(多) "sell":卖(空)
	 */
	private String direction;
	
	/**
	 * 收益
	 */
	private double profit;
	
	private Date create_time;
	/**
	 * 平仓时间
	 */
	private Date close_time;

	/**
	 * 杠杆倍数[“开仓”若有10倍多单，就不能再下20倍多单]
	 */
	private Double lever_rate;
	
	/**
	 * 委托数量(张)
	 */
	private Double volume_open;
	/**
	 * 涨跌幅
	 */
	private double change_ratio;
	/**
	 * 状态。submitted 已提交（持仓）， created 完成（平仓）
	 */
	private String state = "created";
	/**
	 * 成交均价(成本)
	 */
	private Double trade_avg_price;

	/**
	 * 平仓均价
	 */
	private Double close_avg_price;
	
	

	public String list() {

		this.pageSize = 20;
		this.page = this.adminTraderOrderService.pagedQuery(this.pageNo, this.pageSize, this.name_para,
				this.username_para,this.rolename_para);

		return "list";
	}

	public String toAdd() {
		return "add";
	}

	private String verification() {
		
		if (StringUtils.isEmptyString(this.usercode))
			return "请输入交易员UID";
		if (StringUtils.isEmptyString(this.symbol))
			return "请输入品种";
		if (this.create_time == null)
			return "请输入开仓时间";
		if (this.close_time == null)
			return "请输入平仓时间";

		if (this.lever_rate <= 0 || this.lever_rate %1 != 0)
			return "杠杆倍数不能小于等于0,并且不能有小数";
		if (this.volume_open <= 0 || this.volume_open %1 != 0)
			return "委托数量不能小于等于0,并且不能有小数";
		if (this.trade_avg_price < 0)
			return "买入价格不能小于0";
		if (this.close_avg_price < 0)
			return "平常价格不能小于0";

		return null;
	}

	public String add() {
		try {

			this.error = verification();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toAdd();

			Party party = this.partyService.findPartyByUsercode(this.usercode);
			if (party == null ) {
				this.error = "交易员UID不存在!";
				return toAdd();
			}
			
			Trader trader = this.adminTraderService.findByPartyId(party.getId().toString());
			if (trader == null) {
				this.error = "交易员不存在!";
				return toAdd();
			}

			TraderOrder entity = new TraderOrder();
				entity.setPartyId(party.getId());

		
			entity.setSymbol(this.symbol);
			entity.setProfit(this.profit);
			entity.setChange_ratio(this.change_ratio);
			entity.setClose_avg_price(this.close_avg_price);
			entity.setTrade_avg_price(this.trade_avg_price);
			entity.setClose_time(this.close_time);
			entity.setCreate_time(this.create_time);
			entity.setDirection(this.direction);
			entity.setLever_rate(this.lever_rate);
			entity.setProfit(this.profit);
			entity.setState(this.state);
			entity.setVolume_open(this.volume_open);
			

			this.adminTraderOrderService.save(entity);
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
		if (StringUtils.isEmptyString(this.usercode))
			return "请输入交易员UID";
		if (StringUtils.isEmptyString(this.symbol))
			return "请输入品种";
		if (this.create_time == null)
			return "请输入开仓时间";
		if (this.close_time == null)
			return "请输入平仓时间";

		if (this.lever_rate <= 0 || this.lever_rate %1 != 0)
			return "杠杆倍数不能小于等于0,并且不能有小数";
		if (this.volume_open <= 0 || this.volume_open %1 != 0)
			return "委托数量不能小于等于0,并且不能有小数";
		if (this.trade_avg_price < 0)
			return "买入价格不能小于0";
		if (this.close_avg_price < 0)
			return "平常价格不能小于0";
		return null;
	}

	public String toUpdate() {

		TraderOrder entity = adminTraderOrderService.findById(this.id);
		Party party = this.partyService.cachePartyBy(entity.getPartyId(), true);
		
		this.usercode = party.getUsercode();
		
		this.symbol = entity.getSymbol();
		this.profit = entity.getProfit();
		this.change_ratio = entity.getChange_ratio();
		this.close_avg_price = entity.getClose_avg_price();
		this.trade_avg_price = entity.getTrade_avg_price();
		this.close_time = entity.getClose_time();
		this.create_time = entity.getCreate_time();
		this.direction = entity.getDirection();
		this.lever_rate = entity.getLever_rate();
		this.profit = entity.getProfit();
		this.state = entity.getState();
		this.volume_open = entity.getVolume_open();

		return "update";
	}

	public String update() {

		try {
			this.error = verificationUpdate();
			if (!StringUtils.isNullOrEmpty(this.error))
				return toUpdate();

			TraderOrder entity = adminTraderOrderService.findById(this.id);
			
			
			entity.setSymbol(this.symbol);
			entity.setProfit(this.profit);
			entity.setChange_ratio(this.change_ratio);
			entity.setClose_avg_price(this.close_avg_price);
			entity.setTrade_avg_price(this.trade_avg_price);
			entity.setClose_time(this.close_time);
			entity.setCreate_time(this.create_time);
			entity.setDirection(this.direction);
			entity.setLever_rate(this.lever_rate);
			entity.setProfit(this.profit);
			entity.setState(this.state);
			entity.setVolume_open(this.volume_open);

			this.adminTraderOrderService.update(entity);
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

	public String toDelete() {
		try {

			this.adminTraderOrderService.delete(this.id);
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

	public String getSymbol() {
		return symbol;
	}
	public String getOrder_no() {
		return order_no;
	}
	public String getDirection() {
		return direction;
	}
	public double getProfit() {
		return profit;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public Date getClose_time() {
		return close_time;
	}
	public Double getLever_rate() {
		return lever_rate;
	}
	public Double getVolume_open() {
		return volume_open;
	}
	public double getChange_ratio() {
		return change_ratio;
	}
	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}
	public void setOrder_no(String order_no) {
		this.order_no = order_no;
	}
	public void setDirection(String direction) {
		this.direction = direction;
	}
	public void setProfit(double profit) {
		this.profit = profit;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public void setClose_time(Date close_time) {
		this.close_time = close_time;
	}
	public void setLever_rate(Double lever_rate) {
		this.lever_rate = lever_rate;
	}
	public void setVolume_open(Double volume_open) {
		this.volume_open = volume_open;
	}
	public void setChange_ratio(double change_ratio) {
		this.change_ratio = change_ratio;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public Double getTrade_avg_price() {
		return trade_avg_price;
	}
	public Double getClose_avg_price() {
		return close_avg_price;
	}
	public void setTrade_avg_price(Double trade_avg_price) {
		this.trade_avg_price = trade_avg_price;
	}
	public void setClose_avg_price(Double close_avg_price) {
		this.close_avg_price = close_avg_price;
	}

	public String getRolename_para() {
		return rolename_para;
	}

	public void setAdminTraderService(AdminTraderService adminTraderService) {
		this.adminTraderService = adminTraderService;
	}

	public void setAdminTraderOrderService(AdminTraderOrderService adminTraderOrderService) {
		this.adminTraderOrderService = adminTraderOrderService;
	}

	public void setRolename_para(String rolename_para) {
		this.rolename_para = rolename_para;
	}

	public String getId() {
		return id;
	}

	public String getUsercode() {
		return usercode;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setUsercode(String usercode) {
		this.usercode = usercode;
	}


	public Map<String, String> getItem_map() {
		List<Item> items = this.itemService.cacheGetAll();
		if(items != null) {
			for(Item item :items) {
				this.item_map.put(item.getSymbol().toString(), item.getName().toString());
				
			}
		}
		return item_map;
	}

	public void setItem_map(Map<String, String> item_map) {
		this.item_map = item_map;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}
	
	
}
