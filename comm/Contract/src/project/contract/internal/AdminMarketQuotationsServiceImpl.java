package project.contract.internal;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.Arith;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.contract.AdminMarketQuotationsService;
import project.data.AdjustmentValueService;
import project.data.DataCache;
import project.data.DataService;
import project.data.model.Realtime;
import project.item.AdminItemService;
import project.item.model.Item;

public class AdminMarketQuotationsServiceImpl extends HibernateDaoSupport implements AdminMarketQuotationsService{

	private AdminItemService adminItemService;
	private DataService dataService;
	private AdjustmentValueService adjustmentValueService;
	
	public Page pageQuery(int pageNo,int pageSize) {
		Page page = adminItemService.pagedQuerySymbolsByMarket(pageNo, pageSize, null);
//		Page pagedQuery = adminItemService.pagedQuery(pageNo, pageSize);
		page.setElements(this.marketQuotationslist(page.getElements()));
		return page;
	}
	
	
	/**
	 * 行情列表
	 * @param symbols
	 * @return
	 */
	private List<Map<String,Object>> marketQuotationslist(List<String> symbols) {
		if(CollectionUtils.isEmpty(symbols)) return null;
//		List<String> symbols = new LinkedList<String>();
//		for(Item i:items) {
//			symbols.add(i.getSymbol());
//		}
		List<Map<String,Object>> resultList = new LinkedList<Map<String,Object>>();
		Map<String,Object> resultMap = new HashMap<String,Object>();
		List<Realtime> realtimes = this.dataService.realtime(StringUtils.join(symbols,","));
		Set<String> symbolKey =new HashSet<String>();
		for(Realtime realtime:realtimes) {
			if(symbolKey.contains(realtime.getSymbol())) continue;
			resultMap = new HashMap<String,Object>();
			resultMap.put("symbol", realtime.getSymbol());
			resultMap.put("name", realtime.getName());
			Double currentValue = this.adjustmentValueService.getCurrentValue(realtime.getSymbol());
			if (currentValue == null) {
				resultMap.put("adjust_value", 0);
				resultMap.put("new_price", new BigDecimal(String.valueOf(realtime.getClose())).toPlainString());
			} else {
				resultMap.put("adjust_value", new BigDecimal(String.valueOf(currentValue)).toPlainString());
				resultMap.put("new_price", new BigDecimal(String.valueOf(Arith.sub(realtime.getClose(), currentValue))).toPlainString());
			}
			resultMap.put("after_value", new BigDecimal(String.valueOf(realtime.getClose())).toPlainString());
//			resultMap.put("url","http://192.168.43.170:8080/wap/#/pages/item/item_detail?symbol="+realtime.getSymbol());
//			resultMap.put("url","http://172.20.10.2:8080/wap/#/pages/item/item_detail?symbol="+realtime.getSymbol());
//			resultMap.put("url","http://45.76.212.230:8089/wap/#/pages/item/item_detail?symbol="+realtime.getSymbol());
			resultMap.put("url",Constants.WEB_URL+"#/pages/item/item_detail?symbol="+realtime.getSymbol());
//			if (currentValue == null) {
//				resultMap.put("new_price", realtime.getClose());
//			} else {
//				resultMap.put("new_price", Arith.sub(realtime.getClose(), currentValue));
//			}
			resultList.add(resultMap);
			symbolKey.add(realtime.getSymbol());
		}
		return resultList;
	}


	public void setAdminItemService(AdminItemService adminItemService) {
		this.adminItemService = adminItemService;
	}


	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}


	public void setAdjustmentValueService(AdjustmentValueService adjustmentValueService) {
		this.adjustmentValueService = adjustmentValueService;
	}

	
}
