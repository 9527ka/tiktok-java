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
import project.data.model.Depth;
import project.data.model.DepthEntry;
import project.item.ItemService;
import project.item.model.Item;
import project.web.api.websocket.WebSocketServer;

public class DepthPushJob implements Runnable {

	private Logger logger = LogManager.getLogger(DepthPushJob.class);

	private DataService dataService;

	private ItemService itemService;

	private WebSocketServer webSocketServer;

	public void start() {
		new Thread(this, "depthPushJob").start();
		if (logger.isInfoEnabled())
			logger.info("启动depthPushJob！");
	}

	public void run() {

		while (true) {
			try {
				this.depthHandle();
			} catch (Exception e) {
				logger.error("run fail", e);
			} finally {
				ThreadUtils.sleep(500);
			}
		}

	}

	private void depthHandle() {
		try {
			// 数据处理
			ResultObject depthResult = new ResultObject();
			
			Map<String, String> depthResultMap = new HashMap<>();
			
			if (!WebSocketServer.depthMap.isEmpty()) {
				
				// 客户端请求的所有币种，去重集合
				Set<String> symbolSet = new HashSet<String>();
				for (String socketKey : WebSocketServer.depthMap.keySet()) {
					String symbolKey = socketKey.split("_")[2];
					symbolSet.add(symbolKey);
				}
				
				for (String symbol : symbolSet) {
					Depth depthData = this.dataService.depth(symbol);
					if (depthData != null) {
						depthResult.setData(this.depthRevise(depthData, symbol));
					}
					depthResultMap.put(symbol, JSONObject.toJSONString(depthResult));
				}
				
				
				if (depthResultMap.isEmpty()) {
					return;
				}
				
				for (String socketKey : WebSocketServer.depthMap.keySet()) {
//					long timeMillins = System.currentTimeMillis();
					WebSocketServer server = WebSocketServer.depthMap.get(socketKey);
//					if (server.getTimeStr() != 0 && timeMillins > server.getTimeStr()) {
//						server.onClose();
//						return;
//					}
					String type = socketKey.split("_")[1];
					String symbolKey = socketKey.split("_")[2];
					server.sendToMessageById(socketKey, depthResultMap.get(symbolKey), type);
				}
			}
			
			
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * 市场深度数据 解析
	 */
	private Map<String, Object> depthRevise(Depth data, String symbol) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("symbol", symbol);
		map.put("ts", data.getTs());
		Item item = this.itemService.cacheBySymbol(data.getSymbol(), true);
		List<Map<String, Object>> asks_list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < data.getAsks().size(); i++) {
			DepthEntry depthEntry = data.getAsks().get(i);
			Map<String, Object> asks_map = new HashMap<String, Object>();

			if (item.getDecimals() == null || item.getDecimals() < 0) {
				asks_map.put("price", depthEntry.getPrice());
				asks_map.put("amount", depthEntry.getAmount());
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

				asks_map.put("price", df.format(depthEntry.getPrice()));
				asks_map.put("amount", df.format(depthEntry.getAmount()));

			}
			asks_list.add(asks_map);

		}
		map.put("asks", asks_list);
		List<Map<String, Object>> bids_list = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < data.getBids().size(); i++) {
			DepthEntry depthEntry = data.getBids().get(i);
			Map<String, Object> bids_map = new HashMap<String, Object>();
			if (item.getDecimals() == null || item.getDecimals() < 0) {
				bids_map.put("price", depthEntry.getPrice());
				bids_map.put("amount", depthEntry.getAmount());
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

				bids_map.put("price", df.format(depthEntry.getPrice()));
				bids_map.put("amount", df.format(depthEntry.getAmount()));

			}
			bids_list.add(bids_map);

		}

		map.put("bids", bids_list);

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
