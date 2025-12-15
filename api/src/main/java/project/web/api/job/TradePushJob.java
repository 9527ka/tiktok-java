package project.web.api.job;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.alibaba.fastjson.JSONObject;

import kernel.util.ThreadUtils;
import kernel.web.ResultObject;
import project.data.DataService;
import project.data.model.Trade;
import project.data.model.TradeEntry;
import project.item.ItemService;
import project.item.model.Item;
import project.web.api.websocket.WebSocketServer;

/**
 * 近期交易记录
 *
 */
public class TradePushJob implements Runnable {

	private Logger logger = LogManager.getLogger(TradePushJob.class);

	private DataService dataService;

	private ItemService itemService;

	private WebSocketServer webSocketServer;

	public void start() {
		new Thread(this, "tradePushJob").start();
		if (logger.isInfoEnabled())
			logger.info("启动tradePushJob！");
	}

	public void run() {

		while (true) {
			try {
				this.realtimeHandle();
			} catch (Exception e) {
				logger.error("run fail", e);
			} finally {
				ThreadUtils.sleep(500);
			}
		}

	}

	private void realtimeHandle() {
		try {
			// 数据处理
			ResultObject tradeResult = new ResultObject();
			
			Map<String, String> tradeResultMap = new HashMap<>();
			
			if (!WebSocketServer.tradeMap.isEmpty()) {
				
				// 客户端请求的所有币种，去重集合
				Set<String> symbolSet = new HashSet<String>();
				for (String socketKey : WebSocketServer.tradeMap.keySet()) {
					String symbolKey = socketKey.split("_")[2];
					symbolSet.add(symbolKey);
				}
				
				for (String symbol : symbolSet) {
					Trade tradeData = this.dataService.trade(symbol);
					if (tradeData != null) {
						tradeResult.setData(tradeRevise(tradeData, symbol));
					}
					tradeResultMap.put(symbol, JSONObject.toJSONString(tradeResult));
				}
				
				if (tradeResultMap.isEmpty()) {
					return;
				}
				
				for (String socketKey : WebSocketServer.tradeMap.keySet()) {
//					long timeMillins = System.currentTimeMillis();
					WebSocketServer server = WebSocketServer.tradeMap.get(socketKey);
//					if (server.getTimeStr() != 0 && timeMillins > server.getTimeStr()) {
//						server.onClose();
//						return;
//					}
					String type = socketKey.split("_")[1];
					String symbolKey = socketKey.split("_")[2];
					server.sendToMessageById(socketKey, tradeResultMap.get(symbolKey), type);
				}
			}
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 近期交易记录解析
	 */
	private Map<String, Object> tradeRevise(Trade data, String symbol) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("symbol", symbol);
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

	public void setDataService(DataService dataService) {
		this.dataService = dataService;
	}

	public void setItemService(ItemService itemService) {
		this.itemService = itemService;
	}

	public void setWebSocketServer(WebSocketServer webSocketServer) {
		this.webSocketServer = webSocketServer;
	}
	
	
}
