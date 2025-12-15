package project.user;

import java.util.List;
import java.util.Map;

public interface UserSafewordApplyService {

	public void save(UserSafewordApply entity);

	public void update(UserSafewordApply entity);

	public void delete(String id);

	public UserSafewordApply findById(String id);

	public List<UserSafewordApply> findByPartyId(String partyId);
	
	/**
	 * 人工重置  操作类型 operate:	 0/修改资金密码；1/取消谷歌绑定；2/取消手机绑定；3/取消邮箱绑定；
	 */
	public void saveApply(String partyId, String idcard_path_front, String idcard_path_back, String idcard_path_hold, String safeword, 
			String safeword_confirm, Integer operate, String remark);

	public Map<String, Object> bindOne(UserSafewordApply apply);

}
