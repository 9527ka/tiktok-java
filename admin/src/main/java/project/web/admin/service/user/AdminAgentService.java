package project.web.admin.service.user;

import java.io.Serializable;
import java.util.List;

import kernel.web.Page;
import project.user.Agent;


public interface AdminAgentService {

	/**
	 * 代理分页查询
	 * 
	 */
	public Page pagedQuery(int pageNo, int pageSize, String name_para, String checkedPartyId);
	/**
	 * 切换视图查询
	 */
	public Page pagedQueryNetwork(int pageNo, int pageSize,String loginPartyId,String usernameOrUid,String roleName,String targetPartyId);
	

	/**
	 * 
	 * @param loginPartyId
	 * @param checkedPartyId
	 * @param url
	 */
	public List<AgentNodes> findAgentNodes(String loginPartyId, String checkedPartyId, String url);

	/**
	 * 代理注册
	 */
	public void save(String name, String username, String password,boolean login_authority, String remarks, String parents_usercode,boolean opera_authority);
	
	public void update(String id,String name,boolean login_authority, String remarks,boolean opera_authority);
	/**
	 * 修改代理商关系
	 */
	public void update(Agent agent);
	
	public Agent findByPartyId(Serializable partyId);
	
	public Agent get(String id);
}
