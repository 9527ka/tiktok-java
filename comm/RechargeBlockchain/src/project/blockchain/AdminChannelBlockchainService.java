package project.blockchain;

import kernel.web.Page;
import kernel.web.ResultObject;

import javax.servlet.http.HttpServletRequest;

/**
 * 后台区块链地址查询
 *
 */
public interface AdminChannelBlockchainService {

	/**
	 * 代理分页查询 name_para链名称，coin_para币种名称
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String coin_para);

	Page pagedPersonQuery(int pageNo, int pageSize, String userName, String roleName, String chainName, String coinSymbol, String address);

	ResultObject toAdd(HttpServletRequest request);

	ResultObject toUpdate(HttpServletRequest request);

	ResultObject toDelete(HttpServletRequest request);

	ResultObject selectById(HttpServletRequest request);

	ChannelBlockchain selectById(String id);

	/**
	 * 初始化用户的区块链地址
	 * @param username 用户名
	 */
	void initialPersonBlockChains(String username);

	PartyBlockchain selectPersonById(String id);

	ResultObject updatePersonBlockChain(HttpServletRequest request);

	ResultObject personDelete(HttpServletRequest request);
}
