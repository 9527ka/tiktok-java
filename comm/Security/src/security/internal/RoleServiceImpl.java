package security.internal;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.wallet.internal.WalletServiceImpl;
import security.Role;
import security.RoleService;

public class RoleServiceImpl extends HibernateDaoSupport implements RoleService {


    private Logger log = LoggerFactory.getLogger(RoleServiceImpl.class);
    private SecurityAuthoritiesHolder securityAuthoritiesHolder;

    private NamedParameterJdbcOperations namedParameterJdbcTemplate;
    
    private LogService logService;
    
	public Role findRoleByName(String roleName) {
    	List<Role> list = null;
    	try {
            list = (List<Role>) this.getHibernateTemplate().find("FROM Role WHERE roleName = ?0", new Object[] {roleName});
		} catch (Exception e) {
			e.fillInStackTrace();
		}
    	
    	if (list.size() > 0) {
			return list.get(0);
		}
        return null;
    }

    @Override
    public List<Role> getAll() {
        return (List<Role>) this.getHibernateTemplate().find("FROM Role ");
    }

    @Override
    public Role get(String id) {
        return this.getHibernateTemplate().get(Role.class, id);
    }
    
    public void addRole(Role role,String operaterUsername,String ip) {
        Role roleDB = this.findRoleByName(role.getRoleName());
        // 如果存在重名的角色
        if (null != roleDB) {
            throw new BusinessException("存在重复的角色名称");
        }
        // // 如果该角色的资源为空
        // if (role.getCompoundResource().size() == 0) {
        // throw new BusinessException("security_role_compoundResource_null");
        // }
        // roleManagerDao.addRole(role);
        this.getHibernateTemplate().save(role);

        // 重置缓存
        securityAuthoritiesHolder.clean();
        
        
        saveLog(role,operaterUsername,"ip:"+ip+"管理员添加角色："+role.getRoleName());

    }

    public void setSecurityAuthoritiesHolder(SecurityAuthoritiesHolder securityAuthoritiesHolder) {
        this.securityAuthoritiesHolder = securityAuthoritiesHolder;
    }
    
    public void update(Role role,String operaterUsername,String beforeResourceName,String afterResourceName,String code,String ip) {
        // 如果存在重名的角色
        log.info("roleName:" + role.getRoleName());
        log.info("roleId:" + role.getId());
       List list = this.getHibernateTemplate().find("select ro FROM Role ro where ro.roleName = ?0 and ro.id != ?1", new Object[] {
                role.getRoleName(), role.getId() });
        if (list.size() > 0) {
            throw new BusinessException("存在重复的角色名称");
        }
        getHibernateTemplate().merge(role);
//        getHibernateTemplate().flush();
//        getHibernateTemplate().clear();
        // 重置缓存
        securityAuthoritiesHolder.clean();
        
        saveLog(role,operaterUsername,"ip:"+ip+"管理员修改角色名及角色权限，角色名：["+role.getRoleName()+"],原有权限：["+beforeResourceName+"],修改后权限：["+afterResourceName+"],验证码：["+code+"]");
    }

    @Override
    public void removeById(String id,String operaterUsername,String ip) {
        String sql = "select * FROM SCT_USER_ROLE WHERE ROLE_UUID = :role_id";
        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("role_id", id);
        List<Map<String, Object>> list = namedParameterJdbcTemplate.queryForList(sql, parameters);
        
        if (list.size() > 0) {
            throw new BusinessException("角色被用户关联，不可删除");
        }
        Role role =   this.getHibernateTemplate().get(Role.class, id);
        if (role != null) {
            getHibernateTemplate().delete(role); 
        }
       

     // 重置缓存
        securityAuthoritiesHolder.clean();
        
        
        saveLog(role,operaterUsername,"ip:"+ip+"管理员删除角色"+role.getRoleName());
    }
    
    
    public void saveLog(Role role, String operator,String context) {
		Log log = new Log();
		log.setCategory(Constants.LOG_CATEGORY_OPERATION);
		log.setUsername(operator);
		log.setOperator(operator);
		log.setLog(context);
		log.setCreateTime(new Date());
		logService.saveSync(log);
	}


    public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

	public void setLogService(LogService logService) {
		this.logService = logService;
	}
    
    

}
