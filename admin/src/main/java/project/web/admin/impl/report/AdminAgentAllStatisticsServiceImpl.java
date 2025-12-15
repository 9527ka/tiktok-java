package project.web.admin.impl.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.collections.CollectionUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;


import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.PoiUtil;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.mall.utils.PlatformNameEnum;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.user.UserData;
import project.user.UserDataService;
import project.web.admin.service.report.AdminAgentAllStatisticsService;

public class AdminAgentAllStatisticsServiceImpl extends HibernateDaoSupport implements AdminAgentAllStatisticsService {
	private PagedQueryDao pagedQueryDao;
	private UserRecomService userRecomService;
	private UserDataService userDataService;
	private PartyService partyService;
	private NamedParameterJdbcOperations namedParameterJdbcTemplate;

	private List<Party> agentPartys() {
		List<Party> cacheAll = this.partyService.getAll();
		List<Party> result = new ArrayList<Party>();
		for (Party party : cacheAll) {
			if (Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())) {
				result.add(party);
			}
		}
		return result;
	}

	private Page getPageList(int pageNo, int pageSize, String startTime, String endTime, String loginPartyId,
			String roleName, String usernameOrUid, String targetPartyId, String allPartyId) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		StringBuffer queryString = new StringBuffer();
		queryString.append(
				"SELECT party.ROLENAME AS rolename,party.USERNAME AS username,party.USERCODE AS UID,party.UUID AS partyId ");// 用户
		queryString.append("FROM PAT_PARTY party ");
		queryString.append("LEFT JOIN PAT_USER_RECOM ur ON party.UUID = ur.PARTY_ID ");// 推荐人 根目录判定
		queryString.append("WHERE 1=1 ");
		queryString.append("AND party.ROLENAME IN('" + Constants.SECURITY_ROLE_AGENT + "','"+Constants.SECURITY_ROLE_AGENTLOW+"') ");
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(targetPartyId)) {
			List children = this.userRecomService.findRecomsToPartyId(targetPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(allPartyId)) {
			List children = this.userRecomService.findChildren(allPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (StringUtils.isNullOrEmpty(targetPartyId) && StringUtils.isNullOrEmpty(usernameOrUid) && StringUtils.isNullOrEmpty(allPartyId)) {// 目标partyId为空
																									// ，username参数为空，的情况下，如果是视图，显示根目录
			queryString.append(" and ur.RECO_ID is NULL ");
		}
		if (!StringUtils.isNullOrEmpty(usernameOrUid)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username", "%" + usernameOrUid + "%");
		}
		queryString.append("ORDER BY party.USERCODE ASC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
		return page;
	}

	public Page pagedQuery(int pageNo, int pageSize, String startTime, String endTime, String loginPartyId,
			String roleName, String usernameOrUid, String targetPartyId, String allPartyId,String platformName) {
		Page page = getPageList(pageNo, pageSize, startTime, endTime, loginPartyId, usernameOrUid, roleName,
				targetPartyId, allPartyId);// 获取当前页的用户相关

		/**
		 * 页面查询第一层partyId级
		 */
		List<String> list_partyId = new ArrayList<String>();

		for (int i = 0; i < page.getElements().size(); i++) {
			Map<String, Object> map_party = (Map<String, Object>) page.getElements().get(i);
			list_partyId.add(map_party.get("partyId").toString());
		}
		/**
		 * 
		 */

		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < list_partyId.size(); i++) {
			int reco_agent = 0;
			int all_agent = 0;
			/**
			 * 所有子集
			 */
			List<String> children_all = this.userRecomService.findChildren(list_partyId.get(i));
			List<UserRecom> recoms = userRecomService.findRecoms(list_partyId.get(i));
			/**
			 * 正式用户 团队
			 */
			List<String> children_member = new ArrayList<>();
			for (int j = 0; j < children_all.size(); j++) {
				String partyId = children_all.get(j);
				Party party = partyService.cachePartyBy(partyId,true);
				if(null == party){
					logger.info("party 为null id为"+partyId);
				}
				if (Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())||Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
					reco_agent++;
				} else if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
					children_member.add(partyId);
				}

			}
			/**
			 * 正式用户 直推
			 */
			List<String> all_member = new ArrayList<>();
			for (int j = 0; j < recoms.size(); j++) {
				String partyId = children_all.get(j);
				Party party = partyService.cachePartyBy(partyId,true);
				if(null == party){
					logger.info("party 为null id为"+partyId);
				}
				if (Constants.SECURITY_ROLE_AGENT.equals(party.getRolename())||Constants.SECURITY_ROLE_AGENTLOW.equals(party.getRolename())) {
					all_agent++;
				} else if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
					all_member.add(partyId);
				}

			}

			Map<String, Object> item_result = this.sumUserData(children_member,startTime,endTime);
			item_result.put("reco_agent", reco_agent);
			item_result.put("all_agent", all_agent);
			item_result.put("reco_member", children_member.size());
			item_result.put("all_member", all_member.size());
			item_result.put("partyId", list_partyId.get(i));

			if (null != platformName && PlatformNameEnum.WORTEN.getDescription().equals(platformName)){
				item_result.put("totalUsdt",Arith.sub((Double) item_result.get("recharge_usdt"), (Double) item_result.get("withdraw_usdt")));
				item_result.put("totalUsdc",Arith.sub((Double) item_result.get("recharge_usdc"), (Double) item_result.get("withdraw_usdc")));
				item_result.put("totalBtc",Arith.sub((Double) item_result.get("recharge_btc"), (Double) item_result.get("withdraw_btc")));
				item_result.put("totalEth",Arith.sub((Double) item_result.get("recharge_eth"), (Double) item_result.get("withdraw_eth")));
			}

			Party party = partyService.cachePartyBy(list_partyId.get(i),true);
			item_result.put("username", party.getUsername());
			item_result.put("UID", party.getUsercode());
			
			result.add(item_result);
		}
		
		
		Page page_result = new Page();;
		
		page_result.setElements(result);
		
		compute(page_result.getElements());// 计算总收益
		return page_result;
	}
	private Map<String, Object> sumUserData(List<String> children, String startTime, String endTime) {
		if(CollectionUtils.isEmpty(children)) {//children数据为空时，数据填充,这里操作减少dubbo调用
			return sumData(new HashMap<String, Object>(), new ArrayList<UserData>());
		}
		Map<String, Object>  item_result = new HashMap<String, Object>();
		List<Map<String, UserData>> datas = this.userDataService.cacheByPartyIds(children);
		for (int i = 0; i < datas.size(); i++) {
			Map<String, UserData> data_all = datas.get(i);
			if (data_all == null) {
				continue;
			}
			List<UserData> userdata= filterData(data_all,startTime,endTime);
			item_result = sumData(item_result, userdata);
		}
		if(item_result.isEmpty()) {//item_result数据为空时，数据填充
			item_result = sumData(item_result, new ArrayList<UserData>());
		}
		return item_result;

	}


	private Map<String, Object> sumData(Map<String, Object> item_result,List<UserData> datas) {

			double recharge_dapp = 0;
			double withdraw_dapp = 0;
			double recharge = 0;
			double recharge_usdt = 0;
			double recharge_eth = 0;
			double recharge_btc = 0;
			double recharge_usdc = 0;
			double recharge_ltc = 0;
			double withdraw = 0;
			double withdraw_eth = 0;
			double withdraw_btc = 0;
			double withdraw_usdt = 0;
			double withdraw_usdc = 0;
			double recharge_withdrawal_fee = 0;
			double gift_money = 0;
			double balance_amount = 0;
			double amount = 0;
			double rechargeCommission = 0;
			double withdrawCommission = 0;
			double fee = 0;
			double order_income = 0;
			double finance_amount = 0;
			double finance_income = 0;
			double exchange_amount =0;
			double exchange_fee = 0;
			double exchange_income =0;
			double coin_income =0;
			double furtures_amount =0;
			double furtures_fee=0;
			double furtures_income=0;
			double miner_income=0;
			double miner_amount=0;
			double third_recharge_amount=0;
			double exchange_lever_amount = 0;
			double exchange_lever_fee = 0;
			double exchange_lever_order_income = 0;
					
			for(int i = 0 ;i< datas.size(); i++) {
				UserData data = datas.get(i);
								
				// 充提
				recharge_dapp = Arith.add(data.getRecharge_dapp(), recharge_dapp);
				withdraw_dapp = Arith.add(data.getWithdraw_dapp(), withdraw_dapp);
				recharge = Arith.add(data.getRecharge(), recharge);
				recharge_usdt = Arith.add(data.getRecharge_usdt(), recharge_usdt);
				recharge_eth = Arith.add(data.getRecharge_eth(), recharge_eth);
				recharge_btc = Arith.add(data.getRecharge_btc(), recharge_btc);
				recharge_usdc = Arith.add(data.getRecharge_usdc(), recharge_usdc);
				recharge_ltc = Arith.add(data.getRecharge_ltc(), recharge_ltc);
				withdraw = Arith.add(data.getWithdraw(), withdraw);
				withdraw_eth = Arith.add(data.getWithdraw_eth(), withdraw_eth);
				withdraw_btc = Arith.add(data.getWithdraw_btc(), withdraw_btc);
				withdraw_usdt = Arith.add(data.getWithdraw_usdt(), withdraw_usdt);
				withdraw_usdc = Arith.add(data.getWithdraw_usdc(), withdraw_usdc);
				recharge_withdrawal_fee = Arith.add(data.getRecharge_withdrawal_fee(), recharge_withdrawal_fee);
				gift_money = Arith.add(data.getGift_money(), gift_money);
				balance_amount = Arith.add( Arith.sub(data.getRecharge(), data.getWithdraw()), balance_amount);
				// 永续
				amount = Arith.add( data.getAmount(), amount);

				rechargeCommission = Arith.add( data.getRechargeCommission(), rechargeCommission);
				withdrawCommission = Arith.add( data.getWithdrawCommission(), withdrawCommission);
				fee = Arith.add(data.getFee(), fee);
				order_income = Arith.add( data.getOrder_income(), order_income);
				// 理财
				finance_amount = Arith.add( data.getFinance_amount(), finance_amount);
				finance_income = Arith.add( data.getFinance_income(), finance_income);
				// 币币
				exchange_amount = Arith.add( data.getExchange_amount(), exchange_amount);
				exchange_fee = Arith.add( data.getExchange_fee(), exchange_fee);
//				exchange_income = Arith.add(data.getExchange_income(), exchange_income);
				exchange_income = 0;
				coin_income = Arith.add(data.getCoin_income(), coin_income);
				// 交割
				furtures_amount = Arith.add( data.getFurtures_amount(), furtures_amount);
				furtures_fee = Arith.add( data.getFurtures_fee(), furtures_fee);
				furtures_income = Arith.add(data.getFurtures_income(), furtures_income);
				//矿机
				miner_income = Arith.add(data.getMiner_income(), miner_income); 
				miner_amount = Arith.add(data.getMiner_amount(), miner_amount); 
				//三方充值货币金额
				third_recharge_amount = Arith.add(data.getThird_recharge_amount(), third_recharge_amount);
				//币币杠杆
				exchange_lever_amount = Arith.add(data.getExchange_lever_amount(), exchange_lever_amount);
				exchange_lever_fee = Arith.add(data.getExchange_lever_fee(), exchange_lever_fee);
				exchange_lever_order_income = Arith.add(data.getExchange_lever_order_income(), exchange_lever_order_income);
			}
			
			if(item_result != null && item_result.size() != 0) {
				// 充提
				item_result.put("rechargeCommission", Arith.add(Double.valueOf( item_result.get("rechargeCommission").toString()), rechargeCommission));
				item_result.put("withdrawCommission", Arith.add(Double.valueOf( item_result.get("withdrawCommission").toString()), withdrawCommission));
				item_result.put("withdraw_dapp",Arith.add(Double.valueOf( item_result.get("withdraw_dapp").toString()) ,withdraw_dapp));
				item_result.put("recharge", Arith.add(Double.valueOf( item_result.get("recharge").toString()), recharge));
				item_result.put("recharge_usdt", Arith.add(Double.valueOf( item_result.get("recharge_usdt").toString()), recharge_usdt));
				item_result.put("recharge_eth", Arith.add(Double.valueOf( item_result.get("recharge_eth").toString()), recharge_eth));
				item_result.put("recharge_btc", Arith.add(Double.valueOf( item_result.get("recharge_btc").toString()), recharge_btc));
				item_result.put("recharge_usdc", Arith.add(Double.valueOf( item_result.get("recharge_usdc").toString()), recharge_usdc));
				item_result.put("recharge_ltc", Arith.add(Double.valueOf( item_result.get("recharge_ltc").toString()), recharge_ltc));
				item_result.put("withdraw",Arith.add(Double.valueOf( item_result.get("withdraw").toString()) ,withdraw));
				item_result.put("withdraw_eth",Arith.add(Double.valueOf( item_result.get("withdraw_eth").toString()) ,withdraw_eth));
				item_result.put("withdraw_btc",Arith.add(Double.valueOf( item_result.get("withdraw_btc").toString()) ,withdraw_btc));
				item_result.put("withdraw_usdt",Arith.add(Double.valueOf( item_result.get("withdraw_usdt").toString()) ,withdraw_usdt));
				item_result.put("withdraw_usdc",Arith.add(Double.valueOf( item_result.get("withdraw_usdc").toString()) ,withdraw_usdc));
				item_result.put("recharge_withdrawal_fee", Arith.add(Double.valueOf( item_result.get("recharge_withdrawal_fee").toString()),recharge_withdrawal_fee));
				item_result.put("gift_money", Arith.add(Double.valueOf( item_result.get("gift_money").toString()),gift_money));
				item_result.put("balance_amount", Arith.add(Double.valueOf( item_result.get("balance_amount").toString()),balance_amount));
				// 永续
				item_result.put("amount", Arith.add(Double.valueOf( item_result.get("amount").toString()),amount));
				item_result.put("fee", Arith.add(Double.valueOf( item_result.get("fee").toString()),fee));
				item_result.put("order_income", Arith.add(Double.valueOf( item_result.get("order_income").toString()),order_income));
				// 理财
				item_result.put("finance_amount", Arith.add(Double.valueOf( item_result.get("finance_amount").toString()),finance_amount));
				item_result.put("finance_income", Arith.add(Double.valueOf( item_result.get("finance_income").toString()),finance_income));
				// 币币
				item_result.put("exchange_amount", Arith.add(Double.valueOf( item_result.get("exchange_amount").toString()),exchange_amount));
				item_result.put("exchange_fee", Arith.add(Double.valueOf( item_result.get("exchange_fee").toString()),exchange_fee));
//				item_result.put("exchange_income", Arith.add(Double.valueOf( item_result.get("exchange_income").toString()),exchange_income));
				item_result.put("exchange_income", 0);
				item_result.put("coin_income", Arith.add(Double.valueOf( item_result.get("coin_income").toString()),coin_income));
				// 交割
				item_result.put("furtures_amount", Arith.add(Double.valueOf( item_result.get("furtures_amount").toString()),furtures_amount));
				item_result.put("furtures_fee", Arith.add(Double.valueOf( item_result.get("furtures_fee").toString()),furtures_fee));
				item_result.put("furtures_income", Arith.add(Double.valueOf( item_result.get("furtures_income").toString()),furtures_income));
				//矿机
				item_result.put("miner_income", Arith.add(Double.valueOf( item_result.get("miner_income").toString()),miner_income));
				item_result.put("miner_amount", Arith.add(Double.valueOf( item_result.get("miner_amount").toString()),miner_amount));
				//三方充值货币金额
				item_result.put("third_recharge_amount", Arith.add(Double.valueOf( item_result.get("third_recharge_amount").toString()),third_recharge_amount));
				//币币杠杆
				item_result.put("exchange_lever_amount", Arith.add(Double.valueOf( item_result.get("exchange_lever_amount").toString()),exchange_lever_amount));
				item_result.put("exchange_lever_fee", Arith.add(Double.valueOf( item_result.get("exchange_lever_fee").toString()),exchange_lever_fee));
				item_result.put("exchange_lever_order_income", Arith.add(Double.valueOf( item_result.get("exchange_lever_order_income").toString()),exchange_lever_order_income));
			
			}else {
				// 充提
				item_result.put("recharge_dapp", recharge_dapp);
				item_result.put("withdraw_dapp", withdraw_dapp);
				item_result.put("recharge", recharge);
				item_result.put("recharge_usdt", recharge_usdt);
				item_result.put("recharge_eth", recharge_eth);
				item_result.put("recharge_btc", recharge_btc);
				item_result.put("recharge_usdc", recharge_usdc);
				item_result.put("recharge_ltc", recharge_ltc);
				item_result.put("withdraw", withdraw);
				item_result.put("withdraw_eth", withdraw_eth);
				item_result.put("withdraw_btc", withdraw_btc);
				item_result.put("withdraw_usdt", withdraw_usdt);
				item_result.put("withdraw_usdc", withdraw_usdc);
				item_result.put("recharge_withdrawal_fee", recharge_withdrawal_fee);
				item_result.put("gift_money", gift_money);
				item_result.put("balance_amount", balance_amount);
				// 永续
				item_result.put("amount", amount);
				item_result.put("rechargeCommission", rechargeCommission);
				item_result.put("withdrawCommission", withdrawCommission);
				item_result.put("fee", fee);
				item_result.put("order_income", order_income);
				// 理财
				item_result.put("finance_amount", finance_amount);
				item_result.put("finance_income", finance_income);
				// 币币
				item_result.put("exchange_amount", exchange_amount);
				item_result.put("exchange_fee", exchange_fee);
				item_result.put("exchange_income", 0);
				item_result.put("coin_income", coin_income);
				// 交割
				item_result.put("furtures_amount", furtures_amount);
				item_result.put("furtures_fee", furtures_fee);
				item_result.put("furtures_income", furtures_income);
				// 矿机
				item_result.put("miner_income", miner_income);
				item_result.put("miner_amount", miner_amount);
				//三方充值货币金额
				item_result.put("third_recharge_amount", third_recharge_amount);
				//币币杠杆
				item_result.put("exchange_lever_amount", exchange_lever_amount);
				item_result.put("exchange_lever_fee", exchange_lever_fee);
				item_result.put("exchange_lever_order_income", exchange_lever_order_income);
			}
			
			
			
			return item_result;
		
	}

	private List<UserData> filterData(Map<String, UserData> datas, String startTime, String endTime) {
//		Map<String, Map<String, UserData>> result = new HashMap<>();
		
		 List<UserData> result = new ArrayList<UserData>();
		
		for(Entry<String, UserData> valueEntry:datas.entrySet()) {
			UserData  userdata = valueEntry.getValue();
			Date time = userdata.getCreateTime();
			if (!StringUtils.isNullOrEmpty(startTime)) {
				Date startDate = DateUtils.toDate(startTime, DateUtils.DF_yyyyMMdd);
				int intervalDays = DateUtils.getIntervalDaysByTwoDate(startDate, time);// 开始-数据时间
				if (intervalDays > 0) // 开始>数据时间 ，则过滤
					continue;
			}
			if (!StringUtils.isNullOrEmpty(endTime)) {
				Date endDate = DateUtils.toDate(endTime, DateUtils.DF_yyyyMMdd);
				int intervalDays = DateUtils.getIntervalDaysByTwoDate(endDate, time);// 结束-数据时间
				if (intervalDays < 0) // 结束<数据时间
					continue;
			}
			result.add(userdata);
			
		}

		return result;
	}
		
	private void compute(List<Map<String, Object>> datas) {
		if (CollectionUtils.isEmpty(datas))
			return;
		Double totle_income = 0d;
		Double totle_fee = 0d;
		Double business_profit = 0d;//交易盈亏
		Double fin_miner_amount = 0d;//理财 矿机 交易额
		Double fin_miner_income = 0d;//理财 矿机 收益
		for (Map<String, Object> data : datas) {
			totle_income = 0d;
			totle_fee = 0d;
			business_profit = 0d;
			fin_miner_amount = 0d;
			fin_miner_income = 0d;
//			if(null!=data.get("rolename")) {
//				data.put("rolename", Constants.ROLE_MAP.get(data.get("rolename").toString()));
//			}
			if (null != data.get("order_income"))
				data.put("order_income", Arith.sub(0, new Double(data.get("order_income").toString())));// 订单收益负数
			if (null != data.get("finance_income"))
				data.put("finance_income", Arith.sub(0, new Double(data.get("finance_income").toString())));// 理财收益负数
			if (null != data.get("exchange_income"))
//				data.put("exchange_income", Arith.sub(0, new Double(data.get("exchange_income").toString())));// 币币收益负数
				data.put("exchange_income", 0);// 币币收益负数
			
			if (null != data.get("furtures_income"))
				data.put("furtures_income", Arith.sub(0, new Double(data.get("furtures_income").toString())));// 交割收益负数
			if (null != data.get("miner_income"))
				data.put("miner_income", Arith.sub(0, new Double(data.get("miner_income").toString())));// 矿机收益负数
			if (null != data.get("exchange_lever_order_income"))
				data.put("exchange_lever_order_income", Arith.sub(0, new Double(data.get("exchange_lever_order_income").toString())));// 币币收益负数
			
			if (!dataExistNull(data))
				continue;
			totle_income = Arith.add(totle_income, new Double(data.get("recharge_withdrawal_fee").toString()));
			totle_income = Arith.add(totle_income, new Double(data.get("order_income").toString()));
			totle_income = Arith.add(totle_income, new Double(data.get("fee").toString()));
			totle_income = Arith.add(totle_income, new Double(data.get("finance_income").toString()));
			totle_income = Arith.add(totle_income, new Double(data.get("exchange_fee").toString()));
//			totle_income = Arith.add(totle_income, new Double(data.get("exchange_income").toString()));
			totle_income = Arith.add(totle_income, new Double(0));
			totle_income = Arith.add(totle_income, new Double(data.get("furtures_fee").toString()));
			totle_income = Arith.add(totle_income, new Double(data.get("furtures_income").toString()));
			totle_income = Arith.add(totle_income, new Double(data.get("miner_income").toString()));
			totle_income = Arith.add(totle_income, new Double(data.get("exchange_lever_order_income").toString()));
			data.put("totle_income", totle_income);
			
			totle_fee = Arith.add(totle_fee, new Double(data.get("recharge_withdrawal_fee").toString()));
			totle_fee = Arith.add(totle_fee, new Double(data.get("fee").toString()));
			totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_fee").toString()));
			totle_fee = Arith.add(totle_fee, new Double(data.get("furtures_fee").toString()));
			totle_fee = Arith.add(totle_fee, new Double(data.get("exchange_lever_fee").toString()));
			data.put("totle_fee", totle_fee);
			
			business_profit = Arith.add(business_profit, new Double(data.get("order_income").toString()));
			business_profit = Arith.add(business_profit, new Double(data.get("exchange_income").toString()));
			business_profit = Arith.add(business_profit, new Double(data.get("furtures_income").toString()));
			business_profit = Arith.add(business_profit, new Double(data.get("exchange_lever_order_income").toString()));
			data.put("business_profit", business_profit);
			
			fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("finance_amount").toString()));
			fin_miner_amount = Arith.add(fin_miner_amount, new Double(data.get("miner_amount").toString()));
			data.put("fin_miner_amount", fin_miner_amount);
			
			fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("finance_income").toString()));
			fin_miner_income = Arith.add(fin_miner_income, new Double(data.get("miner_income").toString()));
			data.put("fin_miner_income",  fin_miner_income);
		}
	}

	/**
	 * 统计的数据存在空时，不统计总额
	 * 
	 * @param data
	 * @return
	 */
	private boolean dataExistNull(Map<String, Object> data) {
		if (null == data.get("recharge_withdrawal_fee"))
			return false;
		if (null == data.get("order_income"))
			return false;
		if (null == data.get("fee"))
			return false;
		if (null == data.get("finance_income"))
			return false;
		if (null == data.get("exchange_fee"))
			return false;
		if (null == data.get("exchange_income"))
			return false;
		if (null == data.get("furtures_fee"))
			return false;
		if (null == data.get("furtures_income"))
			return false;
		return true;
	}

	public String loadExportData(HttpServletResponse response, int pageSize, String startTime, String endTime,
			String loginPartyId, String usernameOrUid, String roleName, String targetPartyId) throws IOException {
		// 生成数据信息
		int sheetNum = 0;
		// 生成表头
		Integer i = 0;
		// 在内存中保持100行，超过100行将被刷新到磁盘
		// HSSFWorkbook wb = new HSSFWorkbook();//excel文件,一个excel文件包含多个表
		// HSSFSheet sheet = wb.createSheet();//表，一个表包含多个行
		SXSSFWorkbook wb = new SXSSFWorkbook(100);
		Sheet sheet = wb.createSheet(); // 表，一个表包含多个行
		Drawing patriarch = sheet.createDrawingPatriarch();
		CellStyle style = wb.createCellStyle();
		Row row = null;// 行，一行包括多个单元格
		Cell cell = null;// 单元格
		Page page = null;
		int pageNo = 1;

		Map<String, Integer[]> headMap = new LinkedHashMap<String, Integer[]>();
//		<td colspan="3"  style="text-align:center;vertical-align: middle;">用户</td>
//		<td colspan="3" style="text-align:center;">充提</td>
//		<td colspan="3" style="text-align:center;">永续合约</td>
//		<td colspan="2" style="text-align:center;">理财</td>
//		<td colspan="4"  style="text-align:center;vertical-align: middle;">币币</td>
//		<td colspan="3"  style="text-align:center;vertical-align: middle;">交割合约</td>
//		<td colspan="1" rowspan="2" style="text-align:center; vertical-align: middle;">收益</td>
		headMap.put("用户", new Integer[] { 0, 4 });
		headMap.put("充提", new Integer[] { 0, 9 });
		headMap.put("永续合约", new Integer[] { 0, 2 });
		headMap.put("理财收益", new Integer[] { 1, 1 });
		headMap.put("币币", new Integer[] { 0, 2 });
		headMap.put("交割合约", new Integer[] { 0, 2 });
		headMap.put("收益", new Integer[] { 1, 1 });

		createMergedHead(wb, sheet, headMap, i++);

		while (true) {
			page = this.pagedQuery(pageNo, pageSize, startTime, endTime, loginPartyId, usernameOrUid, roleName,
					targetPartyId,null,null);
			if (sheetNum == 0 && (page == null || page.getElements() == null || page.getElements().size() == 0)) {
				return "无导出数据！";
			}
			String[] headTitles = { "用户名", "UID", "网络用户输", "网络代理数", "USDT充值", "ETH充值", "BTC充值","充值换算USDT总计",  "赠送", "提现", "手续费", "充提差额(USDT)","充提总差额(USDT计价)", "手续费", "订单收益", "收益",
					"手续费", "收益", "手续费", "订单收益" };

			if (i == 1)
				PoiUtil.createHead(response, headTitles, wb, sheet, style, row, cell, i,
						"代理商充提报表_" + DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT),
						"代理商充提报表_" + DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT) + ".xlsx");
			List<Object[]> list = this.dataBachHandel(page.getElements());

			if (page.getElements() != null) {

				i = PoiUtil.createCell(list, patriarch, wb, sheet, row, cell, style, i);
				list.clear();

				if (page.getElements().size() < pageSize || i >= 59999) {
					PoiUtil.out(wb, response);
					break;
				}
				sheetNum++;
				pageNo++;
			} else {
				break;
			}
		}
		return "";
	}

	private void createMergedHead(SXSSFWorkbook wb, Sheet sheet, Map<String, Integer[]> headMap, int i) {
		Font font = wb.createFont();
		font.setFontHeightInPoints((short) 10);
		font.setFontName("Courier New");
		CellStyle style = wb.createCellStyle();
		style.setFont(font);
		style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.CENTER);
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		Row row = sheet.createRow(i);
		int rowPosition = 0;// 行坐标
		int cellPosition = 0;// 列坐标
		for (Entry<String, Integer[]> entry : headMap.entrySet()) {
			Cell cell = row.createCell(cellPosition);
			cell.setCellStyle(style);
			cell.setCellValue(entry.getKey());
			CellRangeAddress region = new CellRangeAddress(rowPosition, rowPosition + entry.getValue()[0], cellPosition,
					cellPosition + entry.getValue()[1] - 1);
			sheet.addMergedRegion(region);
			cellPosition += entry.getValue()[1];
		}
	}

	public List<Object[]> dataBachHandel(List<Map<String, Object>> list) {
		List<Object[]> result = new ArrayList<Object[]>();
		int i = 0;
		for (Map<String, Object> data : list) {
			i = 0;
			Object[] objs = new Object[21];
			objs[i++] = data.get("username");
			objs[i++] = data.get("UID");
			objs[i++] = data.get("reco_member");
			
//			objs[i++] = data.get("rolename") != null
//					&& Constants.SECURITY_ROLE_AGENT.equals(data.get("rolename").toString()) ? "代理商"
//							: Constants.SECURITY_ROLE_MEMBER.equals(data.get("rolename").toString()) ? "正式用户" : "";
			objs[i++] = data.get("reco_agent");
//			objs[i++] = data.get("money");

			
			objs[i++] = data.get("recharge_usdt");
			objs[i++] = data.get("recharge_eth");
			objs[i++] = data.get("recharge_btc");
			objs[i++] = data.get("recharge");
			objs[i++] = data.get("gift_money");
			objs[i++] = data.get("withdraw");
			objs[i++] = data.get("recharge_withdrawal_fee");
			double recharge_usdt = 0D;
			double withdraw  = 0D;
			if (null != data.get("recharge_usdt")) {
				recharge_usdt = Double.parseDouble(data.get("recharge_usdt").toString());
			}
			if (null != data.get("withdraw")) {
				withdraw = Double.parseDouble(data.get("withdraw").toString());
			}
			
			objs[i++] = Arith.sub(recharge_usdt, withdraw);
			objs[i++] = data.get("balance_amount");

//			objs[i++] = data.get("amount");
			objs[i++] = data.get("fee");
			objs[i++] = data.get("order_income");

//			objs[i++] = data.get("finance_amount");
			objs[i++] = data.get("finance_income");

//			objs[i++] = data.get("exchange_amount");
			objs[i++] = data.get("exchange_fee");
			objs[i++] = data.get("exchange_income");
//			objs[i++] = data.get("coin_income");

//			objs[i++] = data.get("furtures_amount");
			objs[i++] = data.get("furtures_fee");
			objs[i++] = data.get("furtures_income");

			objs[i++] = data.get("totle_income");
			result.add(objs);
		}
		return result;
	}
	/**
	 * 获取用户推荐数网络 列表（第i层用户数）
	 * @return 
	 */
	public List<Integer> getRecoNumNetList(String partyId) {
		Map<Integer, Integer> recoNumNet = getRecoNumNet(partyId);
		List<Integer> keys = new ArrayList<Integer>(recoNumNet.keySet());
		Collections.sort(keys);
		List<Integer> list = new LinkedList<Integer>();
		for (int i = 0; i < keys.size(); i++) {
			list.add(recoNumNet.get(keys.get(i)));
		}
		return list;
	}
	/**
	 * 获取用户推荐数网络
	 * @return key:网络层级，value:用户数
	 */
	public Map<Integer,Integer> getRecoNumNet(String partyId){
		List<String> all = this.userRecomService.findChildren(partyId);
		int allSize = all.size();
		int sum = 0;
		int level = 1;
		Map<Integer,Integer> result = new HashMap<Integer,Integer>();
		findRecomsNet(partyId,level,result,sum,allSize);
		
		return result;
	}
	public void findRecomsNet(String partyId,int level,Map<Integer,Integer> result,int sum,int allSize) {
		if(sum>=allSize) return;
		List<UserRecom> users = this.userRecomService.findRecoms(partyId);
		if(CollectionUtils.isEmpty(users)) {
			return;
		}
		int num = 0;
		for(UserRecom user:users) {
			findRecomsNet(user.getPartyId().toString(),level+1,result,sum,allSize);
			Party party = partyService.cachePartyBy(user.getPartyId(),true);
			if (!Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {//非正式用户不统计
				continue;
			}
			num++;
		}
		sum+=users.size();//总数
		if(num>0) {
			result.put(level, result.get(level)==null?num:result.get(level)+num);
		}
	}
	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setUserDataService(UserDataService userDataService) {
		this.userDataService = userDataService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

}
