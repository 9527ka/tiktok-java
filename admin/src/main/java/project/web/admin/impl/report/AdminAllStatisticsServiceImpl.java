package project.web.admin.impl.report;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import project.party.recom.UserRecomService;
import project.web.admin.service.report.AdminAllStatisticsService;

public class AdminAllStatisticsServiceImpl extends HibernateDaoSupport implements AdminAllStatisticsService {

	private PagedQueryDao pagedQueryDao;
	
//	private JdbcTemplate jdbcTemplate;
	private UserRecomService userRecomService;
	private NamedParameterJdbcOperations namedParameterJdbcTemplate;
	
	public Page pagedQuery(int pageNo, int pageSize,String startTime,String endTime,String loginPartyId) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append("DATE_FORMAT(ud.CREATE_TIME,\"%Y-%m-%d\") AS date, ");//日期
		queryString.append("SUM(ud.RECHARGE_DAPP) AS recharge_dapp,SUM(ud.RECHARGE) AS recharge,SUM(ud.RECHARGE_USDT) AS recharge_usdt,SUM(ud.RECHARGE_ETH) AS recharge_eth,SUM(ud.RECHARGE_BTC) AS recharge_btc,IFNULL(SUM(ud.RECHARGE_USDC),0) AS recharge_usdc,IFNULL(SUM(ud.RECHARGE_LTC),0) AS recharge_ltc,"
				+ "SUM(ud.WITHDRAW_DAPP) AS withdraw_dapp,SUM(ud.WITHDRAW) AS withdraw,IFNULL(SUM(ud.WITHDRAW_ETH),0) AS withdraw_eth,IFNULL(SUM(ud.WITHDRAW_BTC),0) AS withdraw_btc,"
				+ "IFNULL(SUM(ud.WITHDRAW_USDT),0) AS withdraw_usdt,IFNULL(SUM(ud.WITHDRAW_USDC),0) AS withdraw_usdc,"
				+ "SUM(ud.RECHARGE_WITHDRAWAL_FEE) AS recharge_withdrawal_fee,SUM(ud.GIFT_MONEY) AS gift_money,SUM(ud.RECHARGE)-SUM(ud.WITHDRAW) AS balance_amount, ");//充提
		queryString.append("SUM(ud.AMOUNT) AS amount,SUM(ud.FEE) AS fee,SUM(ud.ORDER_INCOME) AS order_income, IFNULL(SUM(ud.RECHARGE_COMMISSION),0) AS rechargeCommission, IFNULL(SUM(ud.WITHDRAW_COMMISSION),0) AS withdrawCommission,");//永续
		queryString.append("SUM(ud.FINANCE_AMOUNT) AS finance_amount,SUM(ud.FINANCE_INCOME) AS finance_income, ");//理财
		queryString.append("SUM(ud.EXCHANGE_AMOUNT) AS exchange_amount,SUM(ud.EXCHANGE_FEE) AS exchange_fee,SUM(ud.EXCHANGE_INCOME) AS exchange_income,SUM(ud.COIN_INCOME) AS coin_income, ");//币币
		queryString.append("SUM(ud.FURTURES_AMOUNT) AS furtures_amount,SUM(ud.FURTURES_FEE) AS furtures_fee,SUM(ud.FURTURES_INCOME) AS furtures_income, ");//交割
		queryString.append("IFNULL(SUM(ud.MINER_AMOUNT),0) AS miner_amount,IFNULL(SUM(ud.MINER_INCOME),0) AS miner_income, ");//矿机
		queryString.append("IFNULL(SUM(ud.THIRD_RECHARGE_AMOUNT),0) AS third_recharge_amount, ");//三方充值
		queryString.append("IFNULL(SUM(ud.EXCHANGE_LEVER_AMOUNT),0) AS exchange_lever_amount,IFNULL(SUM(ud.EXCHANGE_LEVER_FEE),0) AS exchange_lever_fee,IFNULL(SUM(ud.EXCHANGE_LEVER_ORDER_INCOME),0) AS exchange_lever_order_income, ");//币币杠杆
		queryString.append(" SUM(ud.REBATE_1) + SUM(ud.REBATE_2) rebateLump, IFNULL(SUM(ud.TRANSLATE),0) AS translate, IFNULL(SUM(ud.SELLER_TOTAL_SALES),0) AS sellerTotalSales, IFNULL(SUM(ud.SELLER_COMMISSION),0) AS sellerCommission  ");
		queryString.append("FROM T_USERDATA ud ");
//		queryString.append("LEFT JOIN PAT_PARTY party ON ud.PARTY_ID = party.UUID ");
		
		queryString.append("WHERE 1=1 ");
//		queryString.append("AND ud.ROLENAME ='"+Constants.SECURITY_ROLE_MEMBER+"' ");
//		queryString.append("AND party.ROLENAME ='"+Constants.SECURITY_ROLE_MEMBER+"' ");
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new Page();
			}
			queryString.append(" and ud.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
		queryString.append("GROUP BY DATE(ud.CREATE_TIME) ");
		queryString.append("ORDER BY DATE(ud.CREATE_TIME) DESC ");
		Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//		page.setElements(format(page.getElements()));
//		compute(page.getElements(),false);
		return page;
	}
	
	public Map<String,Object> daySumData(String loginPartyId,String day){
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
//		queryString.append("DATE_FORMAT(ud.CREATE_TIME,\"%Y-%m-%d\") AS date, ");//日期
		queryString.append("IFNULL(SUM(ud.RECHARGE),0) AS recharge,IFNULL(SUM(ud.RECHARGE_USDT),0) AS recharge_usdt,IFNULL(SUM(ud.RECHARGE_ETH),0) AS recharge_eth,IFNULL(SUM(ud.RECHARGE_BTC),0) AS recharge_btc,IFNULL(SUM(ud.RECHARGE_USDC),0) AS recharge_usdc,IFNULL(SUM(ud.RECHARGE_LTC),0) AS recharge_ltc,"
				+ "IFNULL(SUM(ud.WITHDRAW),0) AS withdraw,IFNULL(SUM(ud.RECHARGE_WITHDRAWAL_FEE),0) AS recharge_withdrawal_fee,IFNULL(SUM(ud.RECHARGE_USDT)-SUM(ud.WITHDRAW),0) AS balance_amount, ");//充提
		queryString.append("IFNULL(SUM(ud.FEE),0) AS fee,IFNULL(SUM(ud.ORDER_INCOME),0) AS order_income, ");//永续 
		queryString.append("IFNULL(SUM(ud.FINANCE_AMOUNT),0) AS finance_amount,IFNULL(SUM(ud.FINANCE_INCOME),0) AS finance_income, ");//理财 
		queryString.append("IFNULL(SUM(ud.EXCHANGE_FEE),0) AS exchange_fee,IFNULL(SUM(ud.EXCHANGE_INCOME),0) AS exchange_income, ");//币币
		queryString.append("IFNULL(SUM(ud.FURTURES_FEE),0) AS furtures_fee,IFNULL(SUM(ud.FURTURES_INCOME),0) AS furtures_income, ");//交割 
		queryString.append("IFNULL(SUM(ud.MINER_AMOUNT),0) AS miner_amount,IFNULL(SUM(ud.MINER_INCOME),0) AS miner_income, ");//矿机
		queryString.append("IFNULL(SUM(ud.THIRD_RECHARGE_AMOUNT),0) AS third_recharge_amount, ");//三方充值
		queryString.append("IFNULL(SUM(ud.EXCHANGE_LEVER_AMOUNT),0) AS exchange_lever_amount,IFNULL(SUM(ud.EXCHANGE_LEVER_FEE),0) AS exchange_lever_fee,IFNULL(SUM(ud.EXCHANGE_LEVER_ORDER_INCOME),0) AS exchange_lever_order_income ");//币币杠杆
		queryString.append("FROM T_USERDATA ud ");
		queryString.append("WHERE 1=1 ");
		queryString.append("AND ud.ROLENAME ='"+Constants.SECURITY_ROLE_MEMBER+"' ");
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new HashMap<String,Object>();
			}
			queryString.append(" and ud.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(day)) {
			queryString.append("AND DATE(ud.CREATE_TIME) = DATE(:day)  ");
			parameters.put("day", DateUtils.toDate(day));
		}
		List<Map<String, Object>> queryForList = this.namedParameterJdbcTemplate.queryForList( queryString.toString(), parameters);
		compute(queryForList,false);
		return queryForList.get(0);
	}
	/**
	 * 计算 统计时
	 * @param datas
	 * @param isSum
	 */
	private void compute(List<Map<String,Object>> datas,boolean isSum) {
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
			if(null != data.get("order_income"))
			data.put("order_income", Arith.sub(0, new Double(data.get("order_income").toString())));//订单收益负数
			if(null != data.get("finance_income"))
			data.put("finance_income", Arith.sub(0, new Double(data.get("finance_income").toString())));//理财收益负数
			if(null != data.get("exchange_income"))
//			data.put("exchange_income", Arith.sub(0, new Double(data.get("exchange_income").toString())));//币币收益负数
				data.put("exchange_income",0);//币币收益负数
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
//			data.put("recharge_usdt", new BigDecimal(data.get("recharge_usdt").toString()).setScale(4, RoundingMode.FLOOR).toPlainString());
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

	public Map<String,Object> sumDatas(String startTime,String endTime,String loginPartyId){
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
//		queryString.append("DATE_FORMAT(ud.CREATE_TIME,\"%Y-%m-%d\") AS date, ");//日期
		queryString.append("IFNULL(SUM(ud.RECHARGE_DAPP),0) AS recharge_dapp,IFNULL(SUM(ud.RECHARGE),0) AS recharge,IFNULL(SUM(ud.RECHARGE_USDT),0) AS recharge_usdt,IFNULL(SUM(ud.RECHARGE_ETH),0) AS recharge_eth,IFNULL(SUM(ud.RECHARGE_BTC),0) AS recharge_btc,IFNULL(SUM(ud.RECHARGE_USDC),0) AS recharge_usdc,IFNULL(SUM(ud.RECHARGE_LTC),0) AS recharge_ltc,"
				+ "IFNULL(SUM(ud.WITHDRAW_DAPP),0) AS withdraw_dapp,IFNULL(SUM(ud.WITHDRAW),0) AS withdraw,IFNULL(SUM(ud.WITHDRAW_ETH),0) AS withdraw_eth,IFNULL(SUM(ud.WITHDRAW_BTC),0) AS withdraw_btc,"
				+ "IFNULL(SUM(ud.RECHARGE_WITHDRAWAL_FEE),0) AS recharge_withdrawal_fee,IFNULL(SUM(ud.GIFT_MONEY),0) AS gift_money,IFNULL(SUM(ud.RECHARGE)-SUM(ud.WITHDRAW),0) AS balance_amount, IFNULL(SUM(ud.RECHARGE_COMMISSION),0) AS rechargeCommission, IFNULL(SUM(ud.WITHDRAW_COMMISSION),0) AS withdrawCommission,"
				+ "IFNULL(SUM(ud.WITHDRAW_USDT),0) AS withdraw_usdt,IFNULL(SUM(ud.WITHDRAW_USDC),0) AS withdraw_usdc,");//充提
		queryString.append("IFNULL(SUM(ud.AMOUNT),0) AS amount,IFNULL(SUM(ud.FEE),0) AS fee,IFNULL(SUM(ud.ORDER_INCOME),0) AS order_income, ");//永续
		queryString.append("IFNULL(SUM(ud.FINANCE_AMOUNT),0) AS finance_amount,IFNULL(SUM(ud.FINANCE_INCOME),0) AS finance_income, ");//理财
		queryString.append("IFNULL(SUM(ud.EXCHANGE_AMOUNT),0) AS exchange_amount,IFNULL(SUM(ud.EXCHANGE_FEE),0) AS exchange_fee,IFNULL(SUM(ud.EXCHANGE_INCOME),0) AS exchange_income,IFNULL(SUM(ud.COIN_INCOME),0) AS coin_income, ");//币币
		queryString.append("IFNULL(SUM(ud.FURTURES_AMOUNT),0) AS furtures_amount,IFNULL(SUM(ud.FURTURES_FEE),0) AS furtures_fee,IFNULL(SUM(ud.FURTURES_INCOME),0) AS furtures_income, ");//交割
		queryString.append("IFNULL(SUM(ud.MINER_AMOUNT),0) AS miner_amount,IFNULL(SUM(ud.MINER_INCOME),0) AS miner_income, ");//矿机
		queryString.append("IFNULL(SUM(ud.THIRD_RECHARGE_AMOUNT),0) AS third_recharge_amount, ");//三方充值
		queryString.append("IFNULL(SUM(ud.EXCHANGE_LEVER_AMOUNT),0) AS exchange_lever_amount,IFNULL(SUM(ud.EXCHANGE_LEVER_FEE),0) AS exchange_lever_fee,IFNULL(SUM(ud.EXCHANGE_LEVER_ORDER_INCOME),0) AS exchange_lever_order_income, ");//币币杠杆
		queryString.append("IFNULL(SUM(ud.REBATE_1) + SUM(ud.REBATE_2),0) rebateLump, IFNULL(SUM(ud.TRANSLATE),0) as translate, IFNULL(SUM(ud.SELLER_TOTAL_SALES),0) as sellerTotalSales  ");// 用户佣金

		queryString.append("FROM T_USERDATA ud ");
//		queryString.append("LEFT JOIN PAT_PARTY party ON ud.PARTY_ID = party.UUID ");
		queryString.append("WHERE 1=1 ");
//		queryString.append("AND ud.ROLENAME ='"+Constants.SECURITY_ROLE_MEMBER+"' ");
//		queryString.append("AND party.ROLENAME ='"+Constants.SECURITY_ROLE_MEMBER+"' ");
		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
			List children = this.userRecomService.findChildren(loginPartyId);
			if (children.size() == 0) {
				return new HashMap<String, Object>();
			}
			queryString.append(" and ud.PARTY_ID in (:children) ");
			parameters.put("children", children);
		}
		if (!StringUtils.isNullOrEmpty(startTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) >= DATE(:startTime)  ");
			parameters.put("startTime",DateUtils.toDate(startTime));
		}
		if (!StringUtils.isNullOrEmpty(endTime)) {
			queryString.append("AND DATE(ud.CREATE_TIME) <= DATE(:endTime)  ");
			parameters.put("endTime", DateUtils.toDate(endTime));
		}
//		queryString.append("GROUP BY DATE(ud.CREATE_TIME) ");
		queryString.append("ORDER BY DATE(ud.CREATE_TIME) DESC ");
		List<Map<String, Object>> queryForList = this.namedParameterJdbcTemplate.queryForList( queryString.toString(), parameters);
//		compute(queryForList,true);
		return queryForList.get(0);
	}

	
	
	public String loadExportData(HttpServletResponse response, int pageSize,String startTime,String endTime,String loginPartyId) throws IOException {
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
//		<td colspan="1" style="text-align:center;">日期</td>
//		<td colspan="4" style="text-align:center;">充提统计</td>
//		<td colspan="3" style="text-align:center;">永续订单统计</td>
//		<td colspan="1" style="text-align:center;">币币交易统计</td>
//		<td colspan="1"  rowspan="2" style="text-align:center;">收益</td>
		headMap.put("日期", new Integer[] {1,1});
		headMap.put("充提", new Integer[] {0,9});
		headMap.put("永续合约", new Integer[] {0,2});
		headMap.put("理财收益", new Integer[] {1,1});
		headMap.put("币币", new Integer[] {0,2});
		headMap.put("交割合约", new Integer[] {0,2});
		headMap.put("收益", new Integer[] {1,1});
		
		createMergedHead(wb, sheet,headMap,i++);
		
		while (true) {
			page = this.pagedQuery(pageNo, pageSize, startTime, endTime,loginPartyId);
			if (sheetNum == 0 && (page == null || page.getElements() == null || page.getElements().size() == 0)) {
				return "无导出数据！";
			}
			
			
			String[] headTitles = { "日期",
					"USDT充值","ETH充值","BTC充值","充值总额(USDT计价)","赠送","提现","手续费","充提差额(USDT)","充提总差额(USDT计价)",
					"手续费","订单收益",
					"收益",
					"手续费","收益",
					"手续费","订单收益"};
			if (i == 1)
				PoiUtil.createHead(response, headTitles, wb, sheet, style, row, cell, i,
						"总收益统计_" + DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT),
						"总收益统计_" + DateUtils.format(new Date(), DateUtils.DEFAULT_DATE_FORMAT) + ".xlsx");
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
			Object[] objs = new Object[18];
			objs[i++] = data.get("date");
			
			
			objs[i++] = data.get("recharge_usdt");
			objs[i++] = data.get("recharge_eth");
			objs[i++] = data.get("recharge_btc");
			objs[i++] = data.get("recharge");
			objs[i++] = data.get("gift_money");
			objs[i++] = data.get("withdraw");
			objs[i++] = data.get("recharge_withdrawal_fee");//TODO
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

//			objs[i++] = data.get("furtures_amount");
			objs[i++] = data.get("furtures_fee");
			objs[i++] = data.get("furtures_income");
			
			objs[i++] = data.get("totle_income");
			
			result.add(objs);
		}
		return result;
	}
	
	
	public PagedQueryDao getPagedQueryDao() {
		return pagedQueryDao;
	}

	public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
		this.pagedQueryDao = pagedQueryDao;
	}

	public NamedParameterJdbcOperations getNamedParameterJdbcTemplate() {
		return namedParameterJdbcTemplate;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}
	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}
	
	
}
