package systemuser;

import systemuser.model.Customer;

public interface CustomerService {

	public void save(Customer entity);

	/**
	 * 更新
	 * 
	 * @param entity
	 * @param isOnline true:必须在线才更新，false：都能更新
	 */
	public boolean update(Customer entity, boolean isOnline);

	public void delete(String id);

	public Customer cacheByUsername(String username);

	/**
	 * 分配一个在线客服给用户
	 * 
	 * @return
	 */
	public Customer cacheOnlineOne();
}
