package project.web.api.job;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
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
import project.data.model.Realtime;
import project.data.model.Trade;
import project.data.model.TradeEntry;
import project.item.ItemService;
import project.item.model.Item;
import project.web.api.websocket.WebSocketServer;

/**
 * 行情数据推送Job
 *
 */
public class RealtimePushJob implements Runnable {
	
	private Logger logger = LogManager.getLogger(RealtimePushJob.class);

	private DataService dataService;

	private ItemService itemService;

	private WebSocketServer webSocketServer;

	public void start() {
		new Thread(this, "realtimePushJob").start();
		if (logger.isInfoEnabled())
			logger.info("启动realtimePushJob！");
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
			Map<String, String> realtimeResultMap = new HashMap<>();
			
//			List<String> symbolList = itemService.cacheGetAllSymbol();
//			if (null == symbolList || symbolList.size() <= 0) {
//				return;
//			}
//			StringBuilder str = new StringBuilder();
//			for (String symbol : symbolList) {
//				str.append(symbol + ",");
//			}
//			String symbols = str.substring(0, str.length() -1);
			
			// 行情实时价格
			if (!WebSocketServer.realtimeMap.isEmpty()) {
				
				// 客户端请求的所有币种，去重集合
				Set<String> symbolSet = new HashSet<String>();
				for (String socketKey : WebSocketServer.realtimeMap.keySet()) {
					String symbolKey = socketKey.split("_")[2];
					symbolSet.add(symbolKey);
				}
				
				for (String symbol : symbolSet) {
					List<Realtime> realtimeData = this.dataService.realtime(symbol);
					this.realtimeRevise(realtimeResultMap, realtimeData, symbol);
				}
				
				if (realtimeResultMap.isEmpty()) {
					return;
				}
				
				for (String socketKey : WebSocketServer.realtimeMap.keySet()) {
//					long timeMillins = System.currentTimeMillis();
					WebSocketServer server = WebSocketServer.realtimeMap.get(socketKey);
//					if (server.getTimeStr() != 0 && timeMillins > server.getTimeStr()) {
//						server.onClose();
//						return;
//					}
					
					String type = socketKey.split("_")[1];
					String symbolKey = socketKey.split("_")[2];
					server.sendToMessageById(socketKey, realtimeResultMap.get(symbolKey), type);
				}
			}
			
		} catch (Throwable e) {
			e.printStackTrace();
		}

	}

	/**
	 * 行情实时价格解析
	 */
	private void realtimeRevise(Map<String, String> realtimeResultMap, List<Realtime> data, String symbol) {
		
		for (int i = 0; i < data.size(); i++) {
			
			ResultObject realtimeResult = new ResultObject();
			List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
			
			Realtime realtime = data.get(i);
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("symbol", symbol);
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
			realtimeResult.setData(list);
			realtimeResultMap.put(realtime.getSymbol(), JSONObject.toJSONString(realtimeResult));
		}
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
