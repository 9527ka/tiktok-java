package project.monitor.noderpc.business;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;

import project.monitor.noderpc.NodeRpcActionEnum;
import project.monitor.noderpc.NodeRpcService;
import project.syspara.SysparaService;

public class NodeRpcBusinessServiceImpl implements NodeRpcBusinessService {
	private Logger logger = LoggerFactory.getLogger(NodeRpcBusinessServiceImpl.class);
	private NodeRpcService nodeRpcService;
	private SysparaService sysparaService;

	/**
	 * 验证地址是否存在
	 * 
	 * @param address
	 * @return true:已经存在,false:不存在该地址
	 */
	@Override
	public boolean sendCheck(String address) {
		try {

			List<String> param = new ArrayList<String>();
			param.add(address);// [钱包地址]

			JSONObject object = nodeRpcService.send(NodeRpcActionEnum.check, param.toArray(new String[0]));
			return object.getBoolean("ans");
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send check fail address:{},e:{}", address, e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 新增地址
	 * 
	 * @param address
	 * @return
	 */
	@Override
	public boolean sendAdd(String address) {
		try {

			List<String> param = new ArrayList<String>();
			param.add(address);// [钱包地址]
			param.add(sysparaService.find("node_project_code").getValue());// [项目code]
			JSONObject object = nodeRpcService.send(NodeRpcActionEnum.add, param.toArray(new String[0]));
			return "200".equals(object.getString("code"));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send add fail address:{},e:{}", address, e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 删除地址
	 * 
	 * @param address
	 * @return
	 */
	@Override
	public boolean sendDelete(String address) {
		try {
			List<String> param = new ArrayList<String>();
			param.add(address);// [钱包地址]
			param.add(sysparaService.find("node_project_code").getValue());// [项目code]
			JSONObject object = nodeRpcService.send(NodeRpcActionEnum.delete, param.toArray(new String[0]));
			return "200".equals(object.getString("code"));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send delete fail address:{},e:{}", address, e);
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 获取地址对应的code
	 * 
	 * @param address
	 * @return projectCode 返回-1时表示不存在
	 */
	public String sendGet(String address) {
		try {
			List<String> param = new ArrayList<String>();
			param.add(address);// [钱包地址]
//			param.add(sysparaService.find("node_project_code").getValue());// [项目code]
			JSONObject object = nodeRpcService.send(NodeRpcActionEnum.get, param.toArray(new String[0]));
			if ("200".equals(object.getString("code"))) {
				// pro为-1时表示不存再
				return object.getString("pro");
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send get fail address:{},e:" + e, address);
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取到安全的合约地址
	 * 
	 * @param address
	 */
	public List<String> sendContactList() {
		try {
			List<String> param = new ArrayList<String>();
			param.add("contactAddresses");// [密码]
//			param.add(sysparaService.find("node_project_code").getValue());// [项目code]
			JSONObject object = nodeRpcService.send(NodeRpcActionEnum.contactAddresses, param.toArray(new String[0]));
			if ("200".equals(object.getString("code"))) {
				// pro为-1时表示不存再
				return object.getJSONArray("addresses").toJavaList(String.class);
			}
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send ContactList fail ,e:" + e);
			e.printStackTrace();
		}
		return null;
	}
	public void setNodeRpcService(NodeRpcService nodeRpcService) {
		this.nodeRpcService = nodeRpcService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
