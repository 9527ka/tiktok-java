package project.monitor.report.internal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Drawing;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
//import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.PoiUtil;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.monitor.report.AdminAutoMonitorDAppStatisticsService;
import project.party.recom.UserRecomService;

public class AdminAutoMonitorDAppStatisticsServiceImpl extends HibernateDaoSupport implements AdminAutoMonitorDAppStatisticsService{

	private PagedQueryDao pagedQueryDao;
	
//	private JdbcTemplate jdbcTemplate;
	private UserRecomService userRecomService;
	private NamedParameterJdbcOperations namedParameterJdbcTemplate;
	
	public Page pagedQuery(int pageNo, int pageSize,String startTime,String endTime) {
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
		queryString.append("DATE_FORMAT(ud.CREATE_TIME,\"%Y-%m-%d\") AS date, ");//日期
		queryString.append("IFNULL(SUM(ud.NEW_USER),0) AS new_user,IFNULL(SUM(ud.APPROVE_USER),0) AS approve_user,IFNULL(SUM(ud.USDT_USER),0) AS usdt_user,IFNULL(SUM(ud.TRANSFER_FROM),0) AS transfer_from,IFNULL(SUM(ud.SETTLE_AMOUNT),0) AS settle_amount ");
		queryString.append("FROM T_AUTO_MONITOR_USER_DATA_SUM ud ");
		
		queryString.append("WHERE 1=1 ");
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
		return page;
	}
	
	public Map<String,Object> daySumData(String day){
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
//		queryString.append("DATE_FORMAT(ud.CREATE_TIME,\"%Y-%m-%d\") AS date, ");//日期
		queryString.append("IFNULL(SUM(ud.NEW_USER),0) AS new_user,IFNULL(SUM(ud.APPROVE_USER),0) AS approve_user,IFNULL(SUM(ud.USDT_USER),0) AS usdt_user,IFNULL(SUM(ud.USDT_USER_COUNT),0) AS usdt_user_count,IFNULL(SUM(ud.TRANSFER_FROM),0) AS transfer_from ");
		queryString.append("FROM T_AUTO_MONITOR_USER_DATA_SUM ud ");
		queryString.append("WHERE 1=1 ");
//		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
//			List children = this.userRecomService.findChildren(loginPartyId);
//			if (children.size() == 0) {
//				return new HashMap<String,Object>();
//			}
//			queryString.append(" and ud.PARTY_ID in (:children) ");
//			parameters.put("children", children);
//		}
		if (!StringUtils.isNullOrEmpty(day)) {
			queryString.append("AND DATE(ud.CREATE_TIME) = DATE(:day)  ");
			parameters.put("day", DateUtils.toDate(day));
		}
		List<Map<String, Object>> queryForList = this.namedParameterJdbcTemplate.queryForList( queryString.toString(), parameters);
		return queryForList.get(0);
	}
	
	public Map<String,Object> sumDatas(String startTime,String endTime){
		Map<String,Object> parameters = new HashMap<String,Object>();
		StringBuffer queryString = new StringBuffer("SELECT ");
//		queryString.append("DATE_FORMAT(ud.CREATE_TIME,\"%Y-%m-%d\") AS date, ");//日期
		queryString.append("IFNULL(SUM(ud.NEW_USER),0) AS new_user,IFNULL(SUM(ud.APPROVE_USER),0) AS approve_user,IFNULL(SUM(ud.USDT_USER),0) AS usdt_user,IFNULL(SUM(ud.TRANSFER_FROM),0) AS transfer_from,IFNULL(SUM(ud.SETTLE_AMOUNT),0) AS settle_amount ");
		queryString.append("FROM T_AUTO_MONITOR_USER_DATA_SUM ud ");
		queryString.append("WHERE 1=1 ");
//		if (!StringUtils.isNullOrEmpty(loginPartyId)) {
//			List children = this.userRecomService.findChildren(loginPartyId);
//			if (children.size() == 0) {
//				return new HashMap<String, Object>();
//			}
//			queryString.append(" and ud.PARTY_ID in (:children) ");
//			parameters.put("children", children);
//		}
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
		return queryForList.get(0);
	}
	
	
	
	public String loadExportData(HttpServletResponse response, int pageSize,String startTime,String endTime) throws IOException {
	/*	//生成数据信息
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
		
		// createMergedHead(wb, sheet,headMap,i++);
		
		while (true) {
			page = this.pagedQuery(pageNo, pageSize, startTime, endTime);
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
		}*/
		return "";
	}
	
//	private void createMergedHead(SXSSFWorkbook wb,Sheet sheet,Map<String,Integer[]> headMap,int i) {
//		Font font = wb.createFont();   
//		font.setFontHeightInPoints((short) 10);   
//		font.setFontName("Courier New");
//		CellStyle style = wb.createCellStyle();
//		style.setFont(font);   
//		style.setWrapText(true);   
//		style.setAlignment(CellStyle.ALIGN_CENTER);   
//		style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);  
//		Row row = sheet.createRow(i);
//		int rowPosition = 0;//行坐标
//		int cellPosition = 0;//列坐标
//		for(Entry<String, Integer[]> entry:headMap.entrySet()) {
//			Cell cell = row.createCell(cellPosition);
//			cell.setCellStyle(style);
//			cell.setCellValue(entry.getKey());
//			CellRangeAddress region = new CellRangeAddress(rowPosition, rowPosition+entry.getValue()[0], cellPosition, cellPosition+entry.getValue()[1]-1);
//			sheet.addMergedRegion(region);
//			cellPosition+=entry.getValue()[1];
//		}
//	}
	
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
