package project.web.admin.impl.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import cn.hutool.core.collection.CollectionUtil;
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

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.PoiUtil;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.data.DataService;
import project.party.recom.UserRecomService;
import project.syspara.SysparaService;
import project.wallet.AssetService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import project.web.admin.service.report.AdminUserAllStatisticsService;

public class AdminUserAllStatisticsServiceImpl extends HibernateDaoSupport  implements AdminUserAllStatisticsService {

	protected PagedQueryDao pagedQueryDao;
	protected UserRecomService userRecomService;
	protected WalletService walletService;
//	protected FinanceOrderService financeOrderService;
	protected SysparaService sysparaService;
	protected DataService dataService;
//	protected MinerOrderService minerOrderService;
	protected AssetService assetService;

	private NamedParameterJdbcOperations namedParameterJdbcTemplate;
	
	private List<String> rootAgentId() {
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer();
		queryString.append(
				"SELECT party.UUID AS partyId ");// 用户
		queryString.append("FROM PAT_PARTY party ");
		queryString.append("LEFT JOIN PAT_USER_RECOM ur ON party.UUID = ur.PARTY_ID ");// 推荐人 根目录判定
		queryString.append("WHERE 1=1 ");
		queryString.append("AND party.ROLENAME IN('" + Constants.SECURITY_ROLE_AGENT + "','"+Constants.SECURITY_ROLE_AGENTLOW +"') ");
		queryString.append(" and ur.RECO_ID is NULL ");
		Page page = this.pagedQueryDao.pagedQuerySQL(1, Integer.MAX_VALUE, queryString.toString(), parameters);
		List<String> rootIds = new ArrayList<String>();
		for(Map<String,Object> data:(List<Map<String,Object>>)page.getElements()) {
			rootIds.add(data.get("partyId").toString());
		}
		Set<String> userIds = new HashSet<String>();
		for(String partyId:rootIds) {
			userIds.addAll(userRecomService.findChildren(partyId));
		}
		return new ArrayList<String>(userIds);
	}
	public Page pagedQueryNoAgentParent(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,boolean isAgentView,String sortColumn,String sortType) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT party.ROLENAME AS rolename,party.USERNAME AS username,party.USERCODE AS UID,IFNULL(uds.RECO_NUM,0) AS reco_num,party.UUID AS partyId,IFNULL(wallet.MONEY,0) AS money, ");//用户
		queryString.append("IFNULL(SUM(ud.RECHARGE),0) AS recharge,IFNULL(SUM(ud.RECHARGE_USDT),0) AS recharge_usdt,IFNULL(SUM(ud.RECHARGE_ETH),0) AS recharge_eth,IFNULL(SUM(ud.RECHARGE_BTC),0) AS recharge_btc,IFNULL(SUM(ud.RECHARGE_USDC),0) AS recharge_usdc,IFNULL(SUM(ud.RECHARGE_LTC),0) AS recharge_ltc,"
				+ "IFNULL(SUM(ud.WITHDRAW),0) AS withdraw,IFNULL(SUM(ud.WITHDRAW_ETH),0) AS withdraw_eth,IFNULL(SUM(ud.WITHDRAW_BTC),0) AS withdraw_btc,"
				+ "IFNULL(SUM(ud.RECHARGE_WITHDRAWAL_FEE),0) AS recharge_withdrawal_fee,IFNULL(SUM(ud.GIFT_MONEY),0) AS gift_money,IFNULL(SUM(ud.RECHARGE)-SUM(ud.WITHDRAW),0) AS balance_amount, ");//充提
		queryString.append("IFNULL(SUM(ud.AMOUNT),0) AS amount,IFNULL(SUM(ud.FEE),0) AS fee,IFNULL(SUM(ud.ORDER_INCOME),0) AS order_income, ");//永续
		queryString.append("IFNULL(SUM(ud.FINANCE_AMOUNT),0) AS finance_amount,IFNULL(SUM(ud.FINANCE_INCOME),0) AS finance_income, ");//理财
		queryString.append("IFNULL(SUM(ud.EXCHANGE_AMOUNT),0) AS exchange_amount,IFNULL(SUM(ud.EXCHANGE_FEE),0) AS exchange_fee,IFNULL(SUM(ud.EXCHANGE_INCOME),0) AS exchange_income,IFNULL(SUM(ud.COIN_INCOME),0) AS coin_income, ");//币币
		queryString.append("IFNULL(SUM(ud.FURTURES_AMOUNT),0) AS furtures_amount,IFNULL(SUM(ud.FURTURES_FEE),0) AS furtures_fee,IFNULL(SUM(ud.FURTURES_INCOME),0) AS furtures_income, ");//交割
		queryString.append("IFNULL(SUM(ud.MINER_AMOUNT),0) AS miner_amount,IFNULL(SUM(ud.MINER_INCOME),0) AS miner_income, ");//矿机
		queryString.append("IFNULL(SUM(ud.THIRD_RECHARGE_AMOUNT),0) AS third_recharge_amount, ");//三方充值
		queryString.append("IFNULL(SUM(ud.EXCHANGE_LEVER_AMOUNT),0) AS exchange_lever_amount,IFNULL(SUM(ud.EXCHANGE_LEVER_FEE),0) AS exchange_lever_fee,IFNULL(SUM(ud.EXCHANGE_LEVER_ORDER_INCOME),0) AS exchange_lever_order_income ");//币币杠杆
//		queryString.append("FROM T_USERDATA ud ");
//		queryString.append("LEFT JOIN PAT_PARTY party ON ud.PARTY_ID = party.UUID ");
		queryString.append("FROM PAT_PARTY party ");
		queryString.append("LEFT JOIN T_USERDATA ud ON ud.PARTY_ID = party.UUID ");
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
		queryString.append("LEFT JOIN T_USERDATASUM uds ON ud.PARTY_ID = uds.PARTY_ID ");//推荐总数
//		queryString.append("LEFT JOIN PAT_USER_RECOM ur ON ud.PARTY_ID = ur.PARTY_ID ");//推荐人 根目录判定
		queryString.append("LEFT JOIN T_WALLET wallet ON party.UUID = wallet.PARTY_ID ");//
		queryString.append("WHERE 1=1 ");
//		queryString.append("AND party.ROLENAME IN('"+Constants.SECURITY_ROLE_MEMBER+"','"+Constants.SECURITY_ROLE_AGENT+"')");//限定用户权限只能是用户或代理商
		queryString.append("AND party.ROLENAME IN('"+Constants.SECURITY_ROLE_MEMBER+"')");//限定用户权限只能是用户或代理商
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(targetPartyId)) {
//			List children = this.userRecomService.findChildren_1_Level(targetPartyId);
			List children = this.userRecomService.findChildren(targetPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}else if(isAgentView){//目标partyId为空的情况下，如果是视图，显示根目录
//			queryString.append(" and ur.RECO_ID is NULL ");
//			roleName = Constants.SECURITY_ROLE_AGENT;//改条件下只查代理商
			
			roleName = "";
			queryString.append(" and party.ROLENAME in   (:rolename_agent) ");
			parameters.put("rolename_agent",new ArrayList<String>( Arrays.asList(Constants.SECURITY_ROLE_AGENT,Constants.SECURITY_ROLE_AGENTLOW)));

		}
		if (!StringUtils.isNullOrEmpty(usernameOrUid)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+usernameOrUid+"%");
		}
		if (!StringUtils.isNullOrEmpty(roleName)) {
			queryString.append("AND party.ROLENAME =:roleName ");
			parameters.put("roleName",roleName);
		}
		queryString.append(" and party.UUID not in (:agentChildren) ");
		parameters.put("agentChildren", rootAgentId());
		
		
//		if (!StringUtils.isNullOrEmpty(startTime)) {
//			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
//			parameters.put("startTime",DateUtils.toDate(startTime));
//		}
//		if (!StringUtils.isNullOrEmpty(endTime)) {
//			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
//			parameters.put("endTime", DateUtils.toDate(endTime));
//		}
//		queryString.append("GROUP BY ud.PARTY_ID ");
		queryString.append("GROUP BY party.UUID ");
		queryString.append("ORDER BY "+sortHandle(sortColumn, sortType)+" DATE(ud.CREATE_TIME) DESC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		page.setElements(format(page.getElements()));
		compute(page.getElements());
		return page;
	}
	public Page pagedQuery(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,boolean isAgentView,String sortColumn,String sortType) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT party.ROLENAME AS rolename,party.USERNAME AS username,party.USERCODE AS UID,IFNULL(uds.RECO_NUM,0) AS reco_num,party.UUID AS partyId,IFNULL(wallet.MONEY,0) AS money,IFNULL(wallet_extend_usdt.AMOUNT,0) AS extend_usdt, ");//用户
		queryString.append("IFNULL(SUM(ud.RECHARGE),0) AS recharge,IFNULL(SUM(ud.RECHARGE_USDT),0) AS recharge_usdt,IFNULL(SUM(ud.RECHARGE_ETH),0) AS recharge_eth,IFNULL(SUM(ud.RECHARGE_BTC),0) AS recharge_btc,IFNULL(SUM(ud.RECHARGE_USDC),0) AS recharge_usdc,IFNULL(SUM(ud.RECHARGE_LTC),0) AS recharge_ltc,"
				+ "IFNULL(SUM(ud.WITHDRAW),0) AS withdraw,IFNULL(SUM(ud.WITHDRAW_ETH),0) AS withdraw_eth,IFNULL(SUM(ud.WITHDRAW_BTC),0) AS withdraw_btc,"
				+ "IFNULL(SUM(ud.RECHARGE_WITHDRAWAL_FEE),0) AS recharge_withdrawal_fee,IFNULL(SUM(ud.GIFT_MONEY),0) AS gift_money,IFNULL(SUM(ud.RECHARGE)-SUM(ud.WITHDRAW),0) AS balance_amount, ");//充提
		queryString.append("IFNULL(SUM(ud.AMOUNT),0) AS amount,IFNULL(SUM(ud.FEE),0) AS fee,IFNULL(SUM(ud.ORDER_INCOME),0) AS order_income, ");//永续
		queryString.append("IFNULL(SUM(ud.FINANCE_AMOUNT),0) AS finance_amount,IFNULL(SUM(ud.FINANCE_INCOME),0) AS finance_income, ");//理财
		queryString.append("IFNULL(SUM(ud.EXCHANGE_AMOUNT),0) AS exchange_amount,IFNULL(SUM(ud.EXCHANGE_FEE),0) AS exchange_fee,IFNULL(SUM(ud.EXCHANGE_INCOME),0) AS exchange_income,IFNULL(SUM(ud.COIN_INCOME),0) AS coin_income, ");//币币
		queryString.append("IFNULL(SUM(ud.FURTURES_AMOUNT),0) AS furtures_amount,IFNULL(SUM(ud.FURTURES_FEE),0) AS furtures_fee,IFNULL(SUM(ud.FURTURES_INCOME),0) AS furtures_income, ");//交割
		queryString.append("IFNULL(SUM(ud.MINER_AMOUNT),0) AS miner_amount,IFNULL(SUM(ud.MINER_INCOME),0) AS miner_income, ");//矿机
		queryString.append("IFNULL(SUM(ud.THIRD_RECHARGE_AMOUNT),0) AS third_recharge_amount, ");//三方充值
		queryString.append("IFNULL(SUM(ud.EXCHANGE_LEVER_AMOUNT),0) AS exchange_lever_amount,IFNULL(SUM(ud.EXCHANGE_LEVER_FEE),0) AS exchange_lever_fee,IFNULL(SUM(ud.EXCHANGE_LEVER_ORDER_INCOME),0) AS exchange_lever_order_income ");//币币杠杆
//		queryString.append("FROM T_USERDATA ud ");
//		queryString.append("LEFT JOIN PAT_PARTY party ON ud.PARTY_ID = party.UUID ");
		queryString.append("FROM PAT_PARTY party ");
		queryString.append("LEFT JOIN T_USERDATA ud ON ud.PARTY_ID = party.UUID ");
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
		queryString.append("LEFT JOIN T_USERDATASUM uds ON ud.PARTY_ID = uds.PARTY_ID ");//推荐总数
//		queryString.append("LEFT JOIN PAT_USER_RECOM ur ON ud.PARTY_ID = ur.PARTY_ID ");//推荐人 根目录判定
		queryString.append("LEFT JOIN T_WALLET wallet ON party.UUID = wallet.PARTY_ID ");//
		queryString.append("LEFT JOIN T_WALLET_EXTEND wallet_extend_usdt ON (party.UUID = wallet_extend_usdt.PARTY_ID  and wallet_extend_usdt.WALLETTYPE = 'USDT_USER') ");//
		queryString.append("WHERE 1=1 ");
//		queryString.append("AND party.ROLENAME IN('"+Constants.SECURITY_ROLE_MEMBER+"','"+Constants.SECURITY_ROLE_AGENT+"')");//限定用户权限只能是用户或代理商
		queryString.append("AND party.ROLENAME IN('"+Constants.SECURITY_ROLE_MEMBER+"')");//限定用户权限只能是用户或代理商
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(targetPartyId)) {
//			List children = this.userRecomService.findChildren_1_Level(targetPartyId);
			List children = this.userRecomService.findChildren(targetPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}else if(isAgentView){//目标partyId为空的情况下，如果是视图，显示根目录
//			queryString.append(" and ur.RECO_ID is NULL ");
//			roleName = Constants.SECURITY_ROLE_AGENT;//改条件下只查代理商
			
			roleName = "";
			queryString.append(" and party.ROLENAME in   (:rolename_agent) ");
			parameters.put("rolename_agent",new ArrayList<String>( Arrays.asList(Constants.SECURITY_ROLE_AGENT,Constants.SECURITY_ROLE_AGENTLOW)));

		}
		if (!StringUtils.isNullOrEmpty(usernameOrUid)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+usernameOrUid+"%");
		}
		if (!StringUtils.isNullOrEmpty(roleName)) {
			queryString.append("AND party.ROLENAME =:roleName ");
			parameters.put("roleName",roleName);
		}
//		if (!StringUtils.isNullOrEmpty(startTime)) {
//			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
//			parameters.put("startTime",DateUtils.toDate(startTime));
//		}
//		if (!StringUtils.isNullOrEmpty(endTime)) {
//			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
//			parameters.put("endTime", DateUtils.toDate(endTime));
//		}
//		queryString.append("GROUP BY ud.PARTY_ID ");
		queryString.append("GROUP BY party.UUID ");
		queryString.append("ORDER BY "+sortHandle(sortColumn, sortType)+" DATE(ud.CREATE_TIME) DESC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		page.setElements(format(page.getElements()));
		compute(page.getElements());
		return page;
	}
	
	public Page exchangePagedQuery(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,
								   boolean isAgentView,String sortColumn,String sortType, String sellerId ,String sellerName, String all_para_party_id) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT party.ROLENAME AS rolename,party.USERNAME AS username,party.REMARKS AS remarks, party.USERCODE AS UID,IFNULL(uds.RECO_NUM,0) AS reco_num,party.UUID AS partyId,IFNULL(wallet.MONEY,0) AS money, ");//用户
		queryString.append("IFNULL(SUM(ud.RECHARGE),0) AS recharge,IFNULL(SUM(ud.RECHARGE_USDT),0) AS recharge_usdt,IFNULL(SUM(ud.RECHARGE_ETH),0) AS recharge_eth,IFNULL(SUM(ud.RECHARGE_BTC),0) AS recharge_btc,IFNULL(SUM(ud.RECHARGE_USDC),0) AS recharge_usdc,IFNULL(SUM(ud.RECHARGE_LTC),0) AS recharge_ltc,IFNULL(SUM(ud.RECHARGE_USDT),0) AS recharge_usdt,"
				+ "IFNULL(SUM(ud.WITHDRAW),0) AS withdraw,IFNULL(SUM(ud.WITHDRAW_ETH),0) AS withdraw_eth,IFNULL(SUM(ud.WITHDRAW_BTC),0) AS withdraw_btc,"
				+ "IFNULL(SUM(ud.WITHDRAW_USDT),0) AS withdraw_usdt,IFNULL(SUM(ud.WITHDRAW_USDC),0) AS withdraw_usdc,"
				+ "IFNULL(SUM(ud.RECHARGE_WITHDRAWAL_FEE),0) AS recharge_withdrawal_fee,IFNULL(SUM(ud.GIFT_MONEY),0) AS gift_money,IFNULL(SUM(ud.RECHARGE)-SUM(ud.WITHDRAW),0) AS balance_amount, ");//充提
		queryString.append("IFNULL(SUM(ud.AMOUNT),0) AS amount,IFNULL(SUM(ud.FEE),0) AS fee,IFNULL(SUM(ud.ORDER_INCOME),0) AS order_income, IFNULL(SUM(ud.RECHARGE_COMMISSION),0) AS rechargeCommission,");//永续
		queryString.append("IFNULL(SUM(ud.FINANCE_AMOUNT),0) AS finance_amount,IFNULL(SUM(ud.FINANCE_INCOME),0) AS finance_income, IFNULL(SUM(ud.RECHARGE_COMMISSION),0) AS rechargeCommission, IFNULL(SUM(ud.WITHDRAW_COMMISSION),0) AS withdrawCommission,");//理财
		queryString.append("IFNULL(SUM(ud.EXCHANGE_AMOUNT),0) AS exchange_amount,IFNULL(SUM(ud.EXCHANGE_FEE),0) AS exchange_fee,IFNULL(SUM(ud.EXCHANGE_INCOME),0) AS exchange_income,IFNULL(SUM(ud.COIN_INCOME),0) AS coin_income, ");//币币
		queryString.append("IFNULL(SUM(ud.FURTURES_AMOUNT),0) AS furtures_amount,IFNULL(SUM(ud.FURTURES_FEE),0) AS furtures_fee,IFNULL(SUM(ud.FURTURES_INCOME),0) AS furtures_income, ");//交割
		queryString.append("IFNULL(SUM(ud.MINER_AMOUNT),0) AS miner_amount,IFNULL(SUM(ud.MINER_INCOME),0) AS miner_income, ");//矿机
		queryString.append("IFNULL(SUM(ud.THIRD_RECHARGE_AMOUNT),0) AS third_recharge_amount, ");//三方充值
		queryString.append("IFNULL(SUM(ud.EXCHANGE_LEVER_AMOUNT),0) AS exchange_lever_amount,IFNULL(SUM(ud.EXCHANGE_LEVER_FEE),0) AS exchange_lever_fee,IFNULL(SUM(ud.EXCHANGE_LEVER_ORDER_INCOME),0) AS exchange_lever_order_income, ");//币币杠杆
		queryString.append("IFNULL(SUM(ud.REBATE_1),0) AS rebate1, IFNULL(SUM(ud.REBATE_2),0) AS rebate2,  IFNULL(sum(ud.REBATE_1) + sum(ud.REBATE_2),0) rebateLump,  ");//佣金
		queryString.append("IFNULL(SUM(ud.TRANSLATE),0) AS translate, IFNULL(SUM(ud.SELLER_TOTAL_SALES),0) AS sellerTotalSales, IFNULL(SUM(ud.SELLER_COMMISSION),0) AS sellerCommission, ");//佣金
		queryString.append("s.NAME sellerName, s.UUID sellerId  ");
//		queryString.append("FROM T_USERDATA ud ");
//		queryString.append("LEFT JOIN PAT_PARTY party ON ud.PARTY_ID = party.UUID ");
		queryString.append("FROM PAT_PARTY party ");
		queryString.append("LEFT JOIN T_MALL_SELLER s ON s.UUID = party.UUID ");
		queryString.append("LEFT JOIN T_USERDATA ud ON ud.PARTY_ID = party.UUID ");
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
		queryString.append("LEFT JOIN T_USERDATASUM uds ON ud.PARTY_ID = uds.PARTY_ID ");//推荐总数
//		queryString.append("LEFT JOIN PAT_USER_RECOM ur ON ud.PARTY_ID = ur.PARTY_ID ");//推荐人 根目录判定
		queryString.append("LEFT JOIN T_WALLET wallet ON party.UUID = wallet.PARTY_ID ");//
		queryString.append("WHERE 1=1 ");
//		queryString.append("AND party.ROLENAME IN('"+Constants.SECURITY_ROLE_MEMBER+"','"+Constants.SECURITY_ROLE_AGENT+"')");//限定用户权限只能是用户或代理商
		queryString.append("AND party.ROLENAME IN('"+Constants.SECURITY_ROLE_MEMBER+"')");//限定用户权限只能是用户或代理商
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(targetPartyId)) {
//			List children = this.userRecomService.findChildren_1_Level(targetPartyId);
			List children = this.userRecomService.findDirectlyChildrens(targetPartyId);
			if (children.size() == 0) {
				return new Page();
			}
//			if(children.size() > 2 ){
//				 children = children.subList(0, 2);
//			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);
		} else if(!StringUtils.isNullOrEmpty(all_para_party_id)){
			List children = this.userRecomService.findChildren(all_para_party_id);
			if (children.size() == 0) {
				return new Page();
			}
//			if(children.size() > 2 ){
//				 children = children.subList(0, 2);
//			}
			queryString.append(" and party.UUID in (:children) ");
			parameters.put("children", children);

		} else if(isAgentView){//目标partyId为空的情况下，如果是视图，显示根目录
//			queryString.append(" and ur.RECO_ID is NULL ");
			roleName = Constants.SECURITY_ROLE_AGENT;//改条件下只查代理商
		}
		if (!StringUtils.isNullOrEmpty(usernameOrUid)) {
			queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
			parameters.put("username","%"+usernameOrUid+"%");
		}
		if (!StringUtils.isNullOrEmpty(roleName)) {
			queryString.append("AND party.ROLENAME =:roleName ");
			parameters.put("roleName",roleName);
		}
		if (!StringUtils.isNullOrEmpty(sellerId)) {
			queryString.append("AND s.UUID =:sellerId ");
			parameters.put("sellerId",sellerId);
		}
		if (!StringUtils.isNullOrEmpty(sellerName)) {
			queryString.append("AND s.NAME =:sellerName ");
			parameters.put("sellerName",sellerName);
		}
//		if (!StringUtils.isNullOrEmpty(startTime)) {
//			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
//			parameters.put("startTime",DateUtils.toDate(startTime));
//		}
//		if (!StringUtils.isNullOrEmpty(endTime)) {
//			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
//			parameters.put("endTime", DateUtils.toDate(endTime));
//		}
//		queryString.append("GROUP BY ud.PARTY_ID ");
		queryString.append("GROUP BY party.UUID ");
		queryString.append("ORDER BY "+sortHandle(sortColumn, sortType)+" DATE(ud.CREATE_TIME) DESC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		page.setElements(format(page.getElements()));
		compute(page.getElements());
		return page;
	}
	
	public String sortHandle(String column,String type) {
		//自定义判断处理，防止注入
		List<String> columns=Arrays.asList(new String[] {"recharge_usdt","gift_money","withdraw","third_recharge_amount"});
		List<String> types=Arrays.asList(new String[] {"ASC","DESC"});
		String sql = "";
		if(columns.contains(column)) {
			sql = column;
		}else {
			return sql;
		}
		
		if(types.contains(type)) {
			sql += " "+type+",";
		}else {
			sql += " DESC,";
		}
		return sql;
	}
	
	
	private void compute(List<Map<String,Object>> datas) {
		if(CollectionUtils.isEmpty(datas)) return;
		Double totle_income=0d;
		Double totle_fee = 0d;
		Double business_profit = 0d;//交易盈亏
		Double fin_miner_amount = 0d;//理财 矿机 交易额
		Double fin_miner_income = 0d;//理财 矿机 收益
		for(Map<String,Object> data:datas) {
			totle_income=0d;
			totle_fee = 0d;
			business_profit = 0d;
			fin_miner_amount = 0d;
			fin_miner_income = 0d;
//			if(null!=data.get("rolename")) {
//				data.put("rolename", Constants.ROLE_MAP.get(data.get("rolename").toString()));
//			}
			if(null != data.get("order_income"))
			data.put("order_income", Arith.sub(0, new Double(data.get("order_income").toString())));//订单收益负数
			if(null != data.get("finance_income"))
			data.put("finance_income", Arith.sub(0, new Double(data.get("finance_income").toString())));//理财收益负数
			if(null != data.get("exchange_income"))
//			data.put("exchange_income", Arith.sub(0, new Double(data.get("exchange_income").toString())));//币币收益负数
			data.put("exchange_income", 0);//币币收益负数
			if(null != data.get("furtures_income"))
			data.put("furtures_income", Arith.sub(0, new Double(data.get("furtures_income").toString())));//交割收益负数
			if (null != data.get("miner_income"))
				data.put("miner_income", Arith.sub(0, new Double(data.get("miner_income").toString())));// 矿机收益负数
			if (null != data.get("exchange_lever_order_income"))
				data.put("exchange_lever_order_income", Arith.sub(0, new Double(data.get("exchange_lever_order_income").toString())));// 币币收益负数
			
			if(!dataExistNull(data)) continue;
			totle_income = Arith.add(totle_income,new Double(data.get("recharge_withdrawal_fee").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("order_income").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("fee").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("finance_income").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("exchange_fee").toString()));
			totle_income = Arith.add(totle_income,new Double(0));
//			totle_income = Arith.add(totle_income,new Double(data.get("exchange_income").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("furtures_fee").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("furtures_income").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("miner_income").toString()));
			totle_income = Arith.add(totle_income,new Double(data.get("exchange_lever_order_income").toString()));
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
	 * @param data
	 * @return
	 */
	private boolean dataExistNull(Map<String,Object> data) {
		if(null == data.get("recharge_withdrawal_fee")) return false;
		if(null == data.get("order_income")) return false;
		if(null == data.get("fee")) return false;
		if(null == data.get("finance_income")) return false;
		if(null == data.get("exchange_fee")) return false;
		if(null == data.get("exchange_income")) return false;
		if(null == data.get("furtures_fee")) return false;
		if(null == data.get("furtures_income")) return false;
		return true;
	}
	

	public List<Map<String,Object>> getWalletExtends(String loginPartyId,String targetPartyId) {
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List<String> children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new ArrayList<>();
			}
			if(!children.contains(targetPartyId)) throw new BusinessException("目标用户不属于登录人下级");
		}
		List<WalletExtend> findExtend = walletService.findExtend(targetPartyId);
		List<Map<String,Object>> result = new LinkedList<Map<String,Object>>();
		
		
		
		for(WalletExtend we : findExtend) {
			Map<String,Object> data = new HashMap<String,Object>();
			if ("USDT_USER".equals(we.getWallettype()) 
					|| "ETH_DAPP".equals(we.getWallettype()) 
					|| "USDT_DAPP".equals(we.getWallettype()) 
					|| "ETH_USER".equals(we.getWallettype())) {
				continue;
			}
			data.put("wallettype", we.getWallettype());
			data.put("amount", new BigDecimal(we.getAmount()).setScale(8, RoundingMode.FLOOR).toPlainString());
			result.add(data);
		}
		Map<String,Object> data = new HashMap<String,Object>();
		Wallet wallet = walletService.saveWalletByPartyId(targetPartyId);
		data.put("wallettype", "usdt");
		data.put("amount",null==wallet?0:new BigDecimal(wallet.getMoney()).setScale(8, RoundingMode.FLOOR).toPlainString() );
		result.add(0,data);
		
		
		

		
		
		return result;
	}
	public Map<String,Object> getNameMap(){
		Map<String,Object> data = new LinkedHashMap<String, Object>();
		
		data.put("money_all_coin", "钱包资产折合[USDT]");

		data.put("money_miner", "矿机");
		data.put("money_finance", "理财");
		data.put("money_contract", "永续合约");
		data.put("money_futures", "交割合约");
		data.put("money_fund", "基金");
//		data.put("money_trader", "理财资产");
		data.put("money_ico", "ico");
		data.put("money", "总资产");
		return data;
	}
	public List<Map<String,Object>> getAssetsAll(String loginPartyId,String targetPartyId) {
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List<String> children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new ArrayList<>();
			}
			if(!children.contains(targetPartyId)) throw new BusinessException("目标用户不属于登录人下级");
		}
		Map<String, Object> moneyAll = assetService.getMoneyAll(targetPartyId);
		Map<String, Object> nameMap = getNameMap();
		List<Map<String,Object>> result = new LinkedList<Map<String,Object>>();
		
		for(Entry<String, Object> entry :nameMap.entrySet()) {
			if("money_trader".equals(entry.getKey())) {
				continue;
			}
			Map<String,Object> data = new HashMap<String,Object>();
			data.put("name", entry.getValue());
			data.put("value", moneyAll.get(entry.getKey()));
			result.add(data);
		}
//		for(Entry<String, Object> entry :moneyAll.entrySet()) {
//			if("money_trader".equals(entry.getKey())) {
//				continue;
//			}
//			Map<String,Object> data = new HashMap<String,Object>();
//			data.put("name", nameMap.get(entry.getKey()));
//			data.put("value", entry.getValue());
//			result.add(data);
//		}
		return result;
	}
	public String loadExportData(HttpServletResponse response, int pageSize,String startTime,String endTime,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId,boolean isAgentView,String sortColumn,String sortType) throws IOException {
		//生成数据信息
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
		int pageNo =1;
		
		Map<String,Integer[]> headMap = new LinkedHashMap<String,Integer[]>();
//		<td colspan="3"  style="text-align:center;vertical-align: middle;">用户</td>
//		<td colspan="3" style="text-align:center;">充提</td>
//		<td colspan="3" style="text-align:center;">永续合约</td>
//		<td colspan="2" style="text-align:center;">理财</td>
//		<td colspan="4"  style="text-align:center;vertical-align: middle;">币币</td>
//		<td colspan="3"  style="text-align:center;vertical-align: middle;">交割合约</td>
//		<td colspan="1" rowspan="2" style="text-align:center; vertical-align: middle;">收益</td>
		headMap.put("用户", new Integer[] {0,5});
		headMap.put("充提", new Integer[] {0,9});
		headMap.put("永续合约", new Integer[] {0,2});
		headMap.put("理财收益", new Integer[] {1,1});
		headMap.put("币币", new Integer[] {0,2});
		headMap.put("交割合约", new Integer[] {0,2});
		headMap.put("收益", new Integer[] {1,1});
		
		createMergedHead(wb, sheet,headMap,i++);
		
		while (true) {
			page = this.pagedQuery(pageNo, pageSize, startTime, endTime,loginPartyId,usernameOrUid,roleName,targetPartyId,isAgentView,sortColumn,sortType);
			if (sheetNum == 0 && (page == null || page.getElements() == null || page.getElements().size() == 0)) {
				return "无导出数据！";
			}
			
			
			String[] headTitles = {"用户名","UID","账户类型","团队人数","USDT",
					"USDT充值","ETH充值","BTC充值","充值总额(USDT计价)","赠送","提现","手续费","充提差额(USDT)","充提总差额(USDT计价)",
					"手续费","订单收益",
					"收益",
					"手续费","收益",
					"手续费","订单收益"};
			if (i == 1)
				PoiUtil.createHead(response, headTitles, wb, sheet, style, row, cell, i,
						"用户收益统计_" + DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT),
						"用户收益统计_" + DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT) + ".xlsx");
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
	
	private void createMergedHead(SXSSFWorkbook wb,Sheet sheet,Map<String,Integer[]> headMap,int i) {
		Font font = wb.createFont();   
		font.setFontHeightInPoints((short) 10);   
		font.setFontName("Courier New");
		CellStyle style = wb.createCellStyle();
		style.setFont(font);   
		style.setWrapText(true);
		style.setAlignment(HorizontalAlignment.CENTER);   
		style.setVerticalAlignment(VerticalAlignment.CENTER);
		Row row = sheet.createRow(i);
		int rowPosition = 0;//行坐标
		int cellPosition = 0;//列坐标
		for(Entry<String, Integer[]> entry:headMap.entrySet()) {
			Cell cell = row.createCell(cellPosition);
			cell.setCellStyle(style);
			cell.setCellValue(entry.getKey());
			CellRangeAddress region = new CellRangeAddress(rowPosition, rowPosition+entry.getValue()[0], cellPosition, cellPosition+entry.getValue()[1]-1);
			sheet.addMergedRegion(region);
			cellPosition+=entry.getValue()[1];
		}
	}
	
	public List<Object[]> dataBachHandel(List<Map<String,Object>> list){
		List<Object[]> result = new ArrayList<Object[]>();
		int i = 0;
		for(Map<String,Object> data:list) {
			i = 0;
			Object[] objs = new Object[22];
			objs[i++] = data.get("username");
			objs[i++] = data.get("UID");
			objs[i++] = data.get("rolename")!=null&&Constants.SECURITY_ROLE_AGENT.equals(data.get("rolename").toString())?"代理商"
					:Constants.SECURITY_ROLE_MEMBER.equals(data.get("rolename").toString())?"正式用户":"";
			objs[i++] = data.get("reco_num");
			objs[i++] = data.get("money");
			
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
//			objs[i++] = data.get("exchange_income");
			objs[i++] = 0;
//			objs[i++] = data.get("coin_income");

//			objs[i++] = data.get("furtures_amount");
			objs[i++] = data.get("furtures_fee");
			objs[i++] = data.get("furtures_income");
			
			objs[i++] = data.get("totle_income");
			result.add(objs);
		}
		return result;
	}

	@Override
	public Map<String, Object> queryWillIncomeBySellerIds(List<String> sellerIds , String startTime , String endTime ) {

		Map<String, Object> result = new HashMap<>();
		if (CollectionUtil.isEmpty(sellerIds)) {
			return result;
		}

		StringBuilder sql = new StringBuilder("select SELLER_ID as sellerId, cast((SUM(IFNULL(SYSTEM_PRICE,0)) + SUM(IFNULL(PROFIT,0))) AS DECIMAL (19, 2)) AS willIncome  " +
				" FROM T_MALL_ORDERS_PRIZE  WHERE PURCH_STATUS =1 AND PROFIT_STATUS = 0  AND SELLER_ID in (:sellersList)  ");

		Map<String, Object> params = new HashMap<>();
		params.put("sellersList", sellerIds);

		if(StringUtils.isNotEmpty(startTime) ) {
			params.put("startTime", startTime);
			sql.append("AND CREATE_TIME >=:startTime ");
		}

		if(StringUtils.isNotEmpty(endTime)) {
			params.put("endTime", endTime);
			sql.append("AND CREATE_TIME  <=:endTime ");
		}
		sql.append("GROUP BY SELLER_ID ");
		List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql.toString(), params);

		for (Map<String, Object> data : maps) {
			result.put(data.get("sellerId").toString(), data.get("willIncome"));
		}
		return result;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}




//	public void setFinanceOrderService(FinanceOrderService financeOrderService) {
//		this.financeOrderService = financeOrderService;
//	}




	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}




	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}


	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

//	public void setMinerOrderService(MinerOrderService minerOrderService) {
//		this.minerOrderService = minerOrderService;
//	}
	public void setAssetService(AssetService assetService) {
		this.assetService = assetService;
	}
	
	
}
