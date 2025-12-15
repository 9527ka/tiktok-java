package security;

import java.util.List;


public interface RoleService {
   
   /**
    * 根据角色名查询角色
    */
   public Role findRoleByName(String roleName);
   
   
   public List<Role> getAll();
   
   
   public Role get(String id );
   
   /**
    * 增加角色
    */
   public void addRole(Role role,String operaterUsername,String ip);
   
   
   /**
    * 删除单个角色
    */
   public void removeById(String id,String operaterUsername,String ip);
   
   /**
    * 更新角色
    */
   public void update(Role role,String operaterUsername,String beforeResourceName,String afterResourceName,String code,String ip);


}
