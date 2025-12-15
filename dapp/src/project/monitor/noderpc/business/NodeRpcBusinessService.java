package project.monitor.noderpc.business;

import java.util.List;

public interface NodeRpcBusinessService {

	/**
	 * 验证地址是否存在
	 * 
	 * @param address
	 * @return true:已经存在,false:不存在该地址
	 */
	boolean sendCheck(String address);

	/**
	 * 新增地址
	 * 
	 * @param address
	 * @return
	 */
	boolean sendAdd(String address);

	/**
	 * 删除地址
	 * 
	 * @param address
	 * @return
	 */
	boolean sendDelete(String address);

	/**
	 * 获取地址对应的code
	 * 
	 * @param address
	 * @return projectCode
	 */
	public String sendGet(String address);
	
	/**
	 * 获取到安全的合约地址
	 * 
	 * @param address
	 */
	public List<String> sendContactList();

}