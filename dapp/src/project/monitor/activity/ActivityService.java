package project.monitor.activity;

import java.util.Date;
import java.util.List;

public interface ActivityService {

	/**
	 * 返回前所有sendtime（结束前）的时间
	 * 
	 * @param sendtime
	 * @return
	 */
	public List<Activity> findBeforeDate(Date sendtime);

	public Activity get(String id);
	
	public void save(Activity entity);

	public void update(Activity entity);

	public void delete(Activity entity);

}
