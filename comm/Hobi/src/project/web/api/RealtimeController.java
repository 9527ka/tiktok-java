package project.web.api;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import project.data.model.Realtime;
import project.item.ItemService;
import project.item.model.Item;

/**
 * 行情实时价格 http接口
 *
 */
@RestController
@CrossOrigin
public class RealtimeController {
	
	private Logger logger = LogManager.getLogger(RealtimeController.class);

	@Autowired
	private DataService dataService;

	@Autowired
	private ItemService itemService;

	@RequestMapping("api/hobi!getRealtime.action")
	public String getRealtime(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		String symbol = request.getParameter("symbol");
		// asc升序 desc 降序
		String order = request.getParameter("order");
		try {

			if (StringUtils.isNullOrEmpty(symbol)) {
				resultObject.setCode("400");
				resultObject.setMsg("[symbol]参数为空");
				return JSON.toJSONString(resultObject);
			}

			// 数据处理
			List<Realtime> data = this.dataService.realtime(symbol);

			if (!StringUtils.isNullOrEmpty(order)) {
				List<Realtime> list_clone = new ArrayList<Realtime>();

				for (int i = 0; i < data.size(); i++) {
					Realtime realtime = (Realtime) data.get(i).clone();
					realtime.setOrder(order);
					list_clone.add(realtime);
				}
				Collections.sort(list_clone);
				data = list_clone;
			}
			resultObject.setData(this.revise(data));
			return JSONObject.toJSONString(resultObject);
		} catch (BusinessException e) {
			resultObject.setCode("402");
			resultObject.setMsg(e.getMessage());
			return JSONObject.toJSONString(resultObject);
		} catch (Throwable e) {
			resultObject.setCode("500");
			resultObject.setMsg("服务器错误(500)");
			logger.error("error:", e);
			return JSONObject.toJSONString(resultObject);
		}
	}

	private List<Map<String, Object>> revise(List<Realtime> data) {
		List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < data.size(); i++) {
			Realtime realtime = data.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("symbol", realtime.getSymbol());
			map.put("ts", realtime.getTs());
			map.put("current_time", realtime.getCurrent_time());
			map.put("name", realtime.getName());
			map.put("change_ratio", realtime.getChange_ratio());
			Item item = this.itemService.cacheBySymbol(realtime.getSymbol(), true);
			if (item.getDecimals() == null || item.getDecimals() < 0) {
				map.put("open", realtime.getOpen());
				map.put("close", realtime.getClose());
				map.put("high", realtime.getHigh());
				map.put("low", realtime.getLow());
				map.put("volume", realtime.getVolume());
				map.put("amount", realtime.getAmount());
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

				map.put("open", df.format(realtime.getOpen()));
				map.put("close", df.format(realtime.getClose()));
				map.put("high", df.format(realtime.getHigh()));
				map.put("low", df.format(realtime.getLow()));
				map.put("volume", df.format(realtime.getVolume()));
				map.put("amount", df.format(realtime.getAmount()));

			}
			list.add(map);

		}

		return list;
	}
}
