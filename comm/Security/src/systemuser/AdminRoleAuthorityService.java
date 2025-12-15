package systemuser;

import java.util.List;
import java.util.Map;


public interface AdminRoleAuthorityService {

	/**
	 * 角色列表
	 * @return
	 */
	public List<Map<String,Object>> getAllRole();
	
	/**
	 * 获取角色所有的映射id
	 * @param roleId
	 * @return
	 */
	public List<String> getRoleResourceMappingIdById(String roleId);
	/**
	 * 根据映射id 更新角色资源
	 * @param roleId
	 * @param resourceMapIds 映射id
	 */
	public void updateRoleResource(String roleId,String resourceMapIds,String operaterUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode);
	/**
	 * 根据资源id列表 获取到映射的名字和id
	 * @param resourcesIds
	 * @return
	 */
	public List<Map<String, Object>> getResourceName(List<String> resourcesIds);
	
	public void delete(String roleId,String operaterUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode);
}
