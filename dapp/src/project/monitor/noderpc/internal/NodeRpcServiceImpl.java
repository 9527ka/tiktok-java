package project.monitor.noderpc.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import project.monitor.noderpc.NodeRpcActionEnum;
import project.monitor.noderpc.NodeRpcService;
import project.monitor.noderpc.NodeRpcVerificationEndecrypt;
import project.monitor.noderpc.http.HttpHelper;
import project.monitor.noderpc.http.HttpMethodType;
import project.syspara.SysparaService;

public class NodeRpcServiceImpl implements NodeRpcService {

	private static final Log logger = LogFactory.getLog(NodeRpcServiceImpl.class);
	private SysparaService sysparaService;
	private String encryptKey = "addddd";

	private String url;

	private boolean initia = false;

	public void init() {
		this.url = sysparaService.find("rpc_node_url").getValue();
		initia = true;
	}

	public JSONObject send(NodeRpcActionEnum action, String[] values) {
		if (!initia) {
			init();
		}

		String url = this.url + action.getUrl();
		try {
			Map<String, Object> paramSend = new HashMap<String, Object>();
			paramSend.put(action.getParamName(), encryptParam(values));
			String result = HttpHelper.getJSONFromHttp(url, paramSend, HttpMethodType.GET);
			JSONObject resultJson = JSON.parseObject(result);

			return resultJson;

		} catch (Exception e) {
			logger.error("NodeRpcServiceImpl send error", e);
			e.printStackTrace();
		}
		return null;
	}

	public String encryptParam(String[] values) throws Exception {
		String value = String.join("&", values);
		return NodeRpcVerificationEndecrypt.encryptDES(value, encryptKey);
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
