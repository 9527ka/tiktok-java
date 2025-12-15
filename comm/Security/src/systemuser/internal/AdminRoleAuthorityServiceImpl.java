package systemuser.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import project.Constants;
import project.syspara.SysparaService;
import project.user.googleauth.GoogleAuthService;
import security.Resource;
import security.ResourceService;
import security.Role;
import security.RoleService;
import security.SecUser;
import security.internal.SecUserService;
import systemuser.AdminRoleAuthorityService;
import systemuser.ResourceMappingService;
import systemuser.model.ResourceMapping;

public class AdminRoleAuthorityServiceImpl extends HibernateDaoSupport implements AdminRoleAuthorityService{

	private RoleService roleService;
	private NamedParameterJdbcOperations namedParameterJdbcTemplate;
	private ResourceService resourceService;
	private ResourceMappingService resourceMappingService;
//	protected IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService;
	private SysparaService sysparaService;
	private PasswordEncoder passwordEncoder;
	private SecUserService secUserService;
	private GoogleAuthService googleAuthService;
	/**
	 * 角色列表
	 * @return
	 */
	public List<Map<String,Object>> getAllRole(){
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT role.UUID AS id,role.ROLE_NAME AS roleName,GROUP_CONCAT(DISTINCT r_name.NAME separator ' , ') AS names ");
		queryString.append("FROM SCT_ROLE role ");
		queryString.append("LEFT JOIN SCT_ROLE_RESOURCE role_resource ON role_resource.ROLE_UUID=role.UUID ");//获取角色资源
		queryString.append("LEFT JOIN SCT_RESOURCE_MAPPING resource_mapping ON resource_mapping.RESOURCE_UUID=role_resource.RESOURCE_UUID ");//角色资源对应的映射
		queryString.append("LEFT JOIN SCT_RESOURCE_SET_NAME r_name ON r_name.UUID=resource_mapping.SET_UUID ");//映射对应的名字
		queryString.append("WHERE 1=1 ");
		Map<String,Object> parameters = new HashMap<String,Object>();
		queryString.append("AND ROLE_NAME NOT IN(:roles)  ");
		List<String> roles = new ArrayList<String>(Constants.ROLE_MAP.keySet());

		roles.remove(Constants.SECURITY_ROLE_FINANCE);
		roles.remove(Constants.SECURITY_ROLE_CUSTOMER);
		roles.remove(Constants.SECURITY_ROLE_MAINTAINER);
		roles.remove(Constants.SECURITY_ROLE_AGENT);
		parameters.put("roles", roles);
		
		queryString.append("GROUP BY role.UUID ");
		List<Map<String, Object>> list = this.namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
		return list;
	}
	/**
	 * 获取角色所有的映射id
	 * @param roleId
	 * @return
	 */
	public List<String> getRoleResourceMappingIdById(String roleId){
		Role role = roleService.get(roleId);
		if(null==role) throw new BusinessException("角色不存在");
		Set<Resource> resources = role.getResources();
		if(CollectionUtils.isEmpty(resources)) return new ArrayList<String>(); 
		
		
		List<String> ids = new ArrayList<String>();
		for(Resource r:resources) {
			if(Resource.RESOURCE_TYPE_OPERATION.equals(r.getResType())) {
//				ids.add(r.getResString());
				ids.add(r.getId().toString());
			}
		}
		List<String> result = new ArrayList<String>();
		for(Map<String, Object> map:getResourceName(ids)) {
			result.add(map.get("set_id").toString());
		}
		return result;
	}
	
	/**
	 * 根据资源获取操作权限id
	 * @param resources
	 * @return
	 */
	private List<String> getOPResourceIdByResources(Collection<Resource> resources){
		List<String> ids = new ArrayList<String>();
		for(Resource r:resources) {
			if(Resource.RESOURCE_TYPE_OPERATION.equals(r.getResType())) {
//				ids.add(r.getResString());
				ids.add(r.getId().toString());
			}
		}
		return ids;
	} 
	/**
	 * 根据资源id列表 获取到映射的名字和id
	 * @param resourcesIds
	 * @return
	 */
	public List<Map<String, Object>> getResourceName(List<String> resourcesIds){
		if(resourcesIds!=null&&resourcesIds.size()==0) {
			return new ArrayList<Map<String, Object>>();
		}
		StringBuffer queryString = new StringBuffer();
		queryString.append("SELECT r_map.SET_UUID set_id,r_name.NAME AS name,GROUP_CONCAT(r_map.RESOURCE_UUID separator ',') AS resources ");
		queryString.append("FROM SCT_RESOURCE_MAPPING r_map ");//映射表
		queryString.append("LEFT JOIN SCT_RESOURCE_SET_NAME r_name ON r_name.UUID=r_map.SET_UUID ");//名字表
		queryString.append("WHERE 1=1 ");
		Map<String,Object> parameters = new HashMap<String,Object>();
		
		if(resourcesIds!=null) {
			queryString.append("AND r_map.RESOURCE_UUID IN(:ids) ");
			parameters.put("ids", resourcesIds);
		}
		queryString.append("GROUP BY r_map.SET_UUID ");
		List<Map<String, Object>> list = this.namedParameterJdbcTemplate.queryForList(queryString.toString(), parameters);
		return list;
	}
	/**
	 * 根据映射id 更新角色资源
	 * @param roleId
	 * @param resourceMapIds 映射id ("a,b,c"的形式)
	 */
	public void updateRoleResource(String roleId,String resourceMapIds,String operaterUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode) {
//		checkEmailCode(code);
		if(this.googleAuthService.isEnableGoogleAuth()) {
			checkGoogleAuthCode(superGoogleAuthCode);
		}
		checkLoginSafeword(operaterUsername,loginSafeword);
		Role role = roleService.get(roleId);
		if(null==role) throw new BusinessException("角色不存在");
		List<Map<String, Object>> beforeResourceMap = this.getResourceName(getOPResourceIdByResources(role.getResources()));
		
		resourceMapIds = checkResourceUserRecord(resourceMapIds, operaterUsername, beforeResourceMap);
		
		List<String> ids = new LinkedList<>();
		if(StringUtils.isEmptyString(resourceMapIds)) {
			role.setResources(new HashSet<Resource>());
		}else {
			
			//获取到映射的权限
			List<ResourceMapping> mappings = 
					resourceMappingService.findBySetIds(Arrays.asList(resourceMapIds.replaceAll(" ", "").split(",")));
			for(ResourceMapping mapping:mappings) {
				ids.add(mapping.getResource_id());
				ids.add(Resource.RESOURCE_TYPE_URL+"_"+mapping.getResource_id());//同时添加url权限
			}
			List<Resource> list = resourceService.getByIds(ids);
			role.setResources(new HashSet<Resource>(list));
		}
		//如果客服默认添加客服中心权限，个人中心是属于客服默认权限，所以mapping映射表没有存在映射关系，不会因为修改而不添加
		//补充添加，不会因为修改了权限了导致消失
		if(Constants.SECURITY_ROLE_CUSTOMER.equals(role.getRoleName())) {
			Set<Resource> resources = role.getResources();
//			resources.add(resourceService.get("URL_ADMIN_PERSONAL_CUSTOMER"));
			resources.add(resourceService.get("OP_ADMIN_ONLINECHAT"));
			role.setResources(resources);
		}
		List<String> beforeResourceName = new ArrayList<String>();	
		for(Map<String, Object> map:beforeResourceMap) {
			//过滤名字未空的权限，由于权限可能存在隐藏的操作
			if(map.get("name")==null||StringUtils.isEmptyString(map.get("name").toString())) {
				continue;
			}
			beforeResourceName.add(map.get("name").toString());
		}
		List<Map<String, Object>> afterResourceMap = this.getResourceName(getOPResourceIdByResources(role.getResources()));
		List<String> afterResourceName = new ArrayList<String>();	
		for(Map<String, Object> map:afterResourceMap) {
			//过滤名字未空的权限，由于权限可能存在隐藏的操作
			if(map.get("name")==null||StringUtils.isEmptyString(map.get("name").toString())) {
				continue;
			}
			afterResourceName.add(map.get("name").toString());
		}
		roleService.update(role,operaterUsername,String.join(",", beforeResourceName),String.join(",", afterResourceName),code,ip);
		
	}
	/**
	 * 假分核查权限检验处理
	 * @param resourceMapIds
	 * @param operaterUsername
	 * @param beforeResourceMap
	 */
	private String checkResourceUserRecord(String resourceMapIds,String operaterUsername,List<Map<String, Object>> beforeResourceMap) {
		if(!"root".equals(operaterUsername)&&!CollectionUtils.isEmpty(beforeResourceMap)) {
			boolean hasUR = false;
			for(Map<String, Object> data:beforeResourceMap) {
				//非root操作，有假分权限 且 新权限中无假分权限则加回
				if("SECURITY_USER_RECORD".equals(data.get("set_id").toString())
						&&(StringUtils.isEmptyString(resourceMapIds)||resourceMapIds.indexOf("SECURITY_USER_RECORD")==-1)) {
					resourceMapIds+=", SECURITY_USER_RECORD";
					hasUR = true;
					break;
				}
			}
			//非root操作，无假分权限则移除
			if(!hasUR && resourceMapIds.indexOf("SECURITY_USER_RECORD")!=-1) {
				resourceMapIds.replace("SECURITY_USER_RECORD", "");
			}
		}
		return resourceMapIds;
	}
	/**
	 * 验证谷歌验证码
	 * @param code
	 */
	private void checkGoogleAuthCode(String code) {
		String secret = sysparaService.find("super_google_auth_secret").getValue();
		boolean checkCode = googleAuthService.checkCode(secret, code);
		if(!checkCode) {
			throw new BusinessException("谷歌验证码错误");
		}
	}
	/**
	 * 验证登录人资金密码
	 * @param operatorUsername
	 * @param loginSafeword
	 */
	private void checkLoginSafeword(String operatorUsername,String loginSafeword) {
		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
		String sysSafeword = sec.getSafeword();
		String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
		if (!safeword_md5.equals(sysSafeword)) {
			throw new BusinessException("登录人资金密码错误");
		}
		
	}
	/**
	 * 验证管理员唯一邮箱
	 * @param code
	 */
	private void checkEmailCode(String code) {
//		String value = sysparaService.find("admin_verify_email").getValue();
//		String authCode = identifyingCodeTimeWindowService.getAuthCode(value);
//		if(StringUtils.isEmptyString(authCode)||!authCode.equals(code)) {
//			throw new BusinessException("验证码错误");
//		}
//		identifyingCodeTimeWindowService.delAuthCode(value);
	}
	public void delete(String roleId,String operaterUsername,String loginSafeword,String code,String ip,String superGoogleAuthCode) {
//		checkEmailCode(code);
		checkGoogleAuthCode(superGoogleAuthCode);
		checkLoginSafeword(operaterUsername,loginSafeword);
		Role role = roleService.get(roleId);
		if(null==role) throw new BusinessException("角色不存在");
		
		if(Constants.ROLE_MAP.containsKey(role.getRoleName())) {
			throw new BusinessException("该权限无法删除");
		}
		this.roleService.removeById(role.getId().toString(),operaterUsername,ip);
		
	}
	
	public void setRoleService(RoleService roleService) {
		this.roleService = roleService;
	}

	public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
		this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
	}

	public void setResourceService(ResourceService resourceService) {
		this.resourceService = resourceService;
	}

	public void setResourceMappingService(ResourceMappingService resourceMappingService) {
		this.resourceMappingService = resourceMappingService;
	}
//	public void setIdentifyingCodeTimeWindowService(IdentifyingCodeTimeWindowService identifyingCodeTimeWindowService) {
//		this.identifyingCodeTimeWindowService = identifyingCodeTimeWindowService;
//	}
	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}
	public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
		this.passwordEncoder = passwordEncoder;
	}
	public void setSecUserService(SecUserService secUserService) {
		this.secUserService = secUserService;
	}
	public void setGoogleAuthService(GoogleAuthService googleAuthService) {
		this.googleAuthService = googleAuthService;
	}
	
	
}
