package project.web.api;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.ResultObject;
import project.data.DataService;
import project.data.model.Trend;
import project.item.ItemService;
import project.item.model.Item;

/**
 * 分时图
 *
 */
@RestController
@CrossOrigin
public class TrendController {
	
	private Logger logger = LogManager.getLogger(KlineController.class);

	@Autowired
	private DataService dataService;
	@Autowired
	private ItemService itemService;
	
	@RequestMapping("api/hobi!getTrend.action")
	public String getTrend(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();

		try {
			String symbol = request.getParameter("symbol");
			if (StringUtils.isNullOrEmpty(symbol)) {
				resultObject.setCode("400");
				resultObject.setMsg("[symbol]参数为空");
				return JSON.toJSONString(resultObject);
			}
			// 数据处理
			List<Trend> data = this.dataService.trend(symbol);
			if (data != null) {
				resultObject.setData(revise(data));
			}
		} catch (BusinessException e) {
			resultObject.setCode("402");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable e) {
			resultObject.setCode("500");
			resultObject.setMsg("服务器错误(500)");
			logger.error("error:", e);
		}
		return JSONObject.toJSONString(resultObject);
	}

	private List<Map<String, Object>> revise(List<Trend> data) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Item item = null;
		for (int i = 0; i < data.size(); i++) {
			Trend trend = data.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("symbol", trend.getSymbol());
			map.put("ts", trend.getTs());
			map.put("current_time", trend.getCurrent_time());
			if (item == null) {
				item = this.itemService.cacheBySymbol(trend.getSymbol(), true);
			}
			if (item.getDecimals() == null || item.getDecimals() < 0) {
				map.put("trend", trend.getTrend());
				map.put("volume", trend.getVolume());
				map.put("amount", trend.getAmount());
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

				map.put("trend", df.format(trend.getTrend()));
				map.put("volume", df.format(trend.getVolume()));
				map.put("amount", df.format(trend.getAmount()));

			}
			list.add(map);
		}

		return list;
	}
}
