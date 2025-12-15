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
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.ResultObject;
import project.data.DataService;
import project.data.model.Kline;
import project.hobi.util.DateUtils;
import project.item.ItemService;
import project.item.model.Item;

/**
 * K线图
 *
 */
@RestController
@CrossOrigin
public class KlineController {
	
	private Logger logger = LogManager.getLogger(KlineController.class);

	@Autowired
	private DataService dataService;

	@Autowired
	private ItemService itemService;

	@RequestMapping("api/hobi!getKline.action")
	public String getKline(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String symbol = request.getParameter("symbol");
		// 1min, 5min, 15min, 30min, 60min, 4hour, 1day, 1mon, 1week
		String line = request.getParameter("line");
		try {
			if (StringUtils.isNullOrEmpty(symbol)) {
				resultObject.setCode("400");
				resultObject.setMsg("[symbol]参数为空");
				return JSON.toJSONString(resultObject);
			}

			// 数据处理
			List<Kline> data = this.dataService.kline(symbol, line);
			if ("1day".equals(line) || "1mon".equals(line) || "1week".equals(line)) {
				for (int i = 0; i < data.size(); i++) {
					data.get(i).setCurrent_time(
							DateUtils.timeStamp2Date(String.valueOf(data.get(i).getTs()), "yyyy-MM-dd"));
				}
			} else if ("1min".equals(line)) {
				for (int i = 0; i < data.size(); i++) {
					data.get(i).setCurrent_time(DateUtils.timeStamp2Date(String.valueOf(data.get(i).getTs()), "HH:mm"));
				}
			} else {
				for (int i = 0; i < data.size(); i++) {
					data.get(i).setCurrent_time(
							DateUtils.timeStamp2Date(String.valueOf(data.get(i).getTs()), "MM-dd HH:mm"));
				}
			}
			resultObject.setData(this.revise(data, line));
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

	private List<Map<String, Object>> revise(List<Kline> data, String line) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		Item item = null;
		for (int i = 0; i < data.size(); i++) {
			Kline kline = data.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("line", line);
			map.put("symbol", kline.getSymbol());
			map.put("ts", kline.getTs());
			map.put("current_time", kline.getCurrent_time());
			if (item == null) {
				item = this.itemService.cacheBySymbol(kline.getSymbol(), true);
			}
			if (item.getDecimals() == null || item.getDecimals() < 0) {
				map.put("open", kline.getOpen());
				map.put("close", kline.getClose());
				map.put("high", kline.getHigh());
				map.put("low", kline.getLow());
				map.put("volume", kline.getVolume());
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

				map.put("open", Double.valueOf(df.format(kline.getOpen())));
				map.put("close", Double.valueOf(df.format(kline.getClose())));
				map.put("high", Double.valueOf(df.format(kline.getHigh())));
				map.put("low", Double.valueOf(df.format(kline.getLow())));
				
				if (item.getMultiple() > 0) {
					map.put("volume", Double.valueOf(df.format(Arith.mul(kline.getVolume(), item.getMultiple()))));
				}else {
					map.put("volume", Double.valueOf(df.format(kline.getVolume())));
				}
			}
			list.add(map);
		}
		return list;
	}
}
