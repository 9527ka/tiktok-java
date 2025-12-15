package project.web.api.websocket;

import java.io.IOException;
import java.util.Calendar;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * webSocket服务层 这里我们连接webSocket的时候，
 * 
 * 路径中传一个参数值type，用来区分不同页面推送不同的数据
 * 
 */
@ServerEndpoint(value = "/websocket/{type}/{param}")
public class WebSocketServer {

	private Logger logger = LogManager.getLogger(WebSocketServer.class);

	/**
	 * 静态变量，用来记录当前在线连接数。
	 * 
	 * 后面把它设计成线程安全的。
	 */
	private static int onlineCount = 0;

	/**
	 * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
	 */
	public static ConcurrentHashMap<String, WebSocketServer> realtimeMap = new ConcurrentHashMap<String, WebSocketServer>();
	public static ConcurrentHashMap<String, WebSocketServer> tradeMap = new ConcurrentHashMap<String, WebSocketServer>();
	public static ConcurrentHashMap<String, WebSocketServer> depthMap = new ConcurrentHashMap<String, WebSocketServer>();

	/**
	 * 与某个客户端的连接会话，需要通过它来给客户端发送数据
	 */
	private Session session;
	
	private String setKey;
	
	private long timeStr;

	/**
	 * 连接建立成功调用的方法
	 */
	@OnOpen
	public void onOpen(@PathParam(value = "type") String type, 
			@PathParam(value = "param") String param, Session session) {

		this.session = session;
		String setKey = this.session.getId() + "_" + type + "_" + param;
		this.setKey = setKey;
//		this.setTimeStr(getTimeInMillis());
		// 加入set中
		if (WebSocketEnum.SOCKET_ENUM_REALTIME.getCode().equals(type)) {
			realtimeMap.put(setKey, this);
		}else if (WebSocketEnum.SOCKET_ENUM_TRADE.getCode().equals(type)) {
			tradeMap.put(setKey, this);
		}else if (WebSocketEnum.SOCKET_ENUM_DEPTH.getCode().equals(type)) {
			depthMap.put(setKey, this);
		}
		
		// 在线数加1
		addOnlineCount();
		System.out.println("有新连接加入！当前在线人数为" + getOnlineCount());
		logger.info("有新连接加入！请求ID：{}，当前在线人数为{}", setKey, getOnlineCount());
//		try {
//			sendMessage("-连接已建立-");
//		} catch (IOException e) {
//			System.out.println("IO异常");
//		}
	}

	/**
	 * 连接关闭调用的方法
	 */
	@OnClose
	public void onClose() {
		System.out.println("关闭连接的setKey：" + setKey);
		if (setKey != null && !"".equals(setKey)) {
			String type = setKey.split("_")[1];
			// 从set中删除
			if (WebSocketEnum.SOCKET_ENUM_REALTIME.getCode().equals(type)) {
				realtimeMap.remove(setKey);
			}else if (WebSocketEnum.SOCKET_ENUM_TRADE.getCode().equals(type)) {
				tradeMap.remove(setKey);
			}else if (WebSocketEnum.SOCKET_ENUM_DEPTH.getCode().equals(type)) {
				depthMap.remove(setKey);
			}
			// 在线数减1
			subOnlineCount();
			try {
				if (session != null) {
					session.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println("有一连接关闭！请求ID："+ session.getId() + "当前在线人数为" + getOnlineCount());
			logger.info("有一连接关闭！请求ID：{}，当前在线人数为", session, getOnlineCount());
		}
	}
	
	@OnMessage
	public void onMessage(String message, Session session) {
		if (this.setKey != null && !"".equals(this.setKey)) {
			String type = setKey.split("_")[1];
			if (WebSocketEnum.SOCKET_ENUM_REALTIME.getCode().equals(type)) {
				realtimeMap.get(this.setKey).setTimeStr(getTimeInMillis());
			}else if (WebSocketEnum.SOCKET_ENUM_TRADE.getCode().equals(type)) {
				tradeMap.get(this.setKey).setTimeStr(getTimeInMillis());
			}else if (WebSocketEnum.SOCKET_ENUM_DEPTH.getCode().equals(type)) {
				depthMap.get(this.setKey).setTimeStr(getTimeInMillis());
			}
		}
	}

	/**
	 * 发生错误时调用
	 **/
	@OnError
	public void onError(Session session, Throwable error) {
		logger.error("发生错误:" + error);
		error.printStackTrace();
	}

//	public void sendMessage(String message) throws IOException {
//		synchronized (session) {
//			getSession().getBasicRemote().sendText(message);
//		}
//	}

	/**
	 * 单发消息
	 */
	public void sendMessage(String message) throws IOException {
		// 阻塞式（同步）
		// this.session.getBasicRemote().sendText(message);
		// 非阻塞式（异步）
		this.session.getAsyncRemote().sendText(message);
	}

	/**
	 * 给指定的请求发送消息
	 * 
	 */
	public void sendToMessageById(String key, String message, String type) {
		try {
			if (WebSocketEnum.SOCKET_ENUM_REALTIME.getCode().equals(type)) {
				if (realtimeMap.get(key) != null) {
					realtimeMap.get(key).sendMessage(message);
					logger.info("给前端推送消息！key：{}，消息内容{}", key, message);
				} else {
					System.out.println("realtimeMap中没有此key，不推送消息");
				}
			}else if (WebSocketEnum.SOCKET_ENUM_TRADE.getCode().equals(type)) {
				if (tradeMap.get(key) != null) {
					tradeMap.get(key).sendMessage(message);
					logger.info("给前端推送消息！key：{}，消息内容{}", key, message);
				} else {
					System.out.println("tradeMap中没有此key，不推送消息");
				}
			}else if (WebSocketEnum.SOCKET_ENUM_DEPTH.getCode().equals(type)) {
				if (depthMap.get(key) != null) {
					depthMap.get(key).sendMessage(message);
					logger.info("给前端推送消息！key：{}，消息内容{}", key, message);
				} else {
					System.out.println("depthMap中没有此key，不推送消息");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static long getTimeInMillis() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.SECOND, c.get(Calendar.SECOND) + 60);
		return c.getTimeInMillis();
	}
	
	public Session getSession() {
		return session;
	}
	
	public void setTimeStr(long timeStr) {
		this.timeStr = timeStr;
	}
	
	public long getTimeStr() {
		return timeStr;
	}

	public static synchronized int getOnlineCount() {
		return onlineCount;
	}

	public static synchronized void addOnlineCount() {
		WebSocketServer.onlineCount++;
	}

	public static synchronized void subOnlineCount() {
		WebSocketServer.onlineCount--;
	}
}
