package project.monitor.telegram.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.hobi.http.HttpHelper;
import project.hobi.http.HttpMethodType;
import project.monitor.telegram.TelegramMessageService;
import project.syspara.SysparaService;

public class TelegramMessageServiceImpl implements TelegramMessageService {

	private static final Log logger = LogFactory.getLog(TelegramMessageServiceImpl.class);
	private SysparaService sysparaService;
	private String chat_id;

	private String url;

	private boolean initia = false;

	/**
	 * 接口调用间隔（毫秒）
	 */
	private int interval = 500;

	private volatile Date last_time = new Date();

	public void send(String text) {
		this.send(text, "HTML");
	}

	@Override
	public void send(String text, String parse_mode) {

		while (true) {
			if ((new Date().getTime() - last_time.getTime()) < interval) {
				ThreadUtils.sleep(interval);
			} else {
				break;
			}
		}

		if (!initia) {
			init();
		}
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("chat_id", this.chat_id);
		param.put("text", text);
		if (!StringUtils.isNullOrEmpty(parse_mode)) {
			param.put("parse_mode", parse_mode);
		} else {
			param.put("parse_mode", "");
		}
		try {
			String result = HttpHelper.getJSONFromHttp(this.url, param, HttpMethodType.GET);
			JSONObject resultJson = JSON.parseObject(result);

			String status = resultJson.getString("ok");
			if ("false".equals(status)) {
				logger.error("Telegram消息发送失败，失败原因：" + resultJson.getString("description"));
			}

		} catch (Exception e) {
			logger.error("error", e);
		} finally {
			last_time = new Date();

		}

	}

	private void init() {
		String token = sysparaService.find("telegram_message_token").getValue();
		chat_id = sysparaService.find("telegram_message_chat_id").getValue();
		this.url = "https://api.telegram.org/bot" + token + "/sendMessage";
		initia = true;

	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
