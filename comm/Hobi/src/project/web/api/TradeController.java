package project.web.api;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.data.DataService;
import project.data.model.Trade;
import project.data.model.TradeEntry;
import project.item.ItemService;
import project.item.model.Item;

/**
 * 获得近期交易记录 页面保持20条，这个只能刷到最新的几条记录。
 *
 */
@RestController
@CrossOrigin
public class TradeController extends BaseAction {

	@Autowired
	private DataService dataService;
	@Autowired
	private ItemService itemService;

	@RequestMapping("api/hobi!getTrade.action")
	public String getTrade(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();

		try {
			String symbol = request.getParameter("symbol");
			if (StringUtils.isNullOrEmpty(symbol)) {
				resultObject.setCode("400");
				resultObject.setMsg("[symbol]参数为空");
				return JSON.toJSONString(resultObject);
			}

			// 数据处理
			Trade data = this.dataService.trade(symbol);
			if (data != null) {
				resultObject.setData(revise(data));
			}
			return JSONObject.toJSONString(resultObject);
		} catch (BusinessException e) {
			resultObject.setCode("402");
			resultObject.setMsg(e.getMessage());
			return JSONObject.toJSONString(resultObject);
		} catch (Throwable e) {
			resultObject.setCode("500");
			resultObject.setMsg("服务器错误(500)");
			return JSONObject.toJSONString(resultObject);
		} 
	}

	private Map<String, Object> revise(Trade data) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("symbol", data.getSymbol());
		map.put("ts", data.getTs());
		Item item = this.itemService.cacheBySymbol(data.getSymbol(), true);
		List<Map<String, Object>> tradeEntry_list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < data.getData().size(); i++) {
			TradeEntry tradeEntry = data.getData().get(i);
			Map<String, Object> tradeEntry_map = new HashMap<String, Object>();
			tradeEntry_map.put("direction", tradeEntry.getDirection());
			tradeEntry_map.put("ts", tradeEntry.getTs());
			tradeEntry_map.put("current_time", tradeEntry.getCurrent_time());

			if (item.getDecimals() == null || item.getDecimals() < 0) {
				tradeEntry_map.put("price", tradeEntry.getPrice());
				tradeEntry_map.put("amount", tradeEntry.getAmount());
			} else {
				String format = "";
				if (item.getDecimals() == 0) {
					format = "#";
				} else {
					format = "#.";
					for (int j = 0; j < item.getDecimals(); j++) {
						format = format + "#";
					}
				}

				DecimalFormat df = new DecimalFormat(format);
				df.setRoundingMode(RoundingMode.FLOOR);// 向下取整

				tradeEntry_map.put("price", df.format(tradeEntry.getPrice()));
				tradeEntry_map.put("amount", df.format(tradeEntry.getAmount()));

			}
			tradeEntry_list.add(tradeEntry_map);

		}
		map.put("data", tradeEntry_list);
		return map;
	}
}
