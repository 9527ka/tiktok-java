package project.web.api.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import kernel.exception.BusinessException;
import kernel.web.ResultObject;
import project.item.ItemService;
import project.item.model.Item;
import project.web.api.service.LocalSysparaService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 币种
 *
 */
@RestController
@CrossOrigin
public class ItemController {
	
	private Logger logger = LogManager.getLogger(ItemController.class);

	@Autowired
	private ItemService itemService;
	
	@Autowired
	private LocalSysparaService localSysparaService;
	
	/**
	 * 热门币种
	 */
	@RequestMapping("api/item!list.action")
	public Object list(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		List<Map<String, String>> data = new ArrayList<>();

		try {
			// String market = request.getParameter("market");
			String symbol = request.getParameter("symbol");
			List<Item> list = itemService.cacheGetByMarket(symbol);
			// 按id排序
			Collections.sort(list, new Comparator<Item>() {
				@Override
				public int compare(Item arg0, Item arg1) {
					return arg0.getId().toString().compareTo(arg1.getId().toString());
				}
			});
			
			Map<String, Object> topPara = localSysparaService.find("index_top_symbols");
			String symbols = topPara.get("index_top_symbols").toString();
			
			List<String> symbolList = Arrays.asList(symbols.split(","));
			
			for (int i = 0; i < list.size(); i++) {
				Item item = list.get(i);
				String isTop = "0";
				if (symbolList.contains(item.getSymbol())) {
					isTop = "1";
				}
				Map<String, String> map = new HashMap<>();
				map.put("symbol", item.getSymbol());
				map.put("symbolFullName", item.getSymbolFullName());
				map.put("isTop", isTop);
				
				data.add(map);	
			}

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		}

		resultObject.setData(data);
		return resultObject;
	}
}
