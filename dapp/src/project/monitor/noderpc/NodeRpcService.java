package project.monitor.noderpc;

import com.alibaba.fastjson.JSONObject;

public interface NodeRpcService {

	public JSONObject send(NodeRpcActionEnum action, String[] values);

}