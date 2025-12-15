package project.futures;

import java.util.Map;

import kernel.web.Page;
import project.item.model.Item;

public interface AdminContractManageService {
	/**
	 * 添加合约产品
	 */
	public String addContractItem(Item entity);
	
	public Map<String,String> getFuturesSymbols();
	
	public String addFutures(FuturesPara entity,String ip,String operaUsername,String loginSafeword);
	
	public void deleteFuturesPara(String id,String ip,String operaUsername,String loginSafeword,String superGoogleAuthCode);
}
