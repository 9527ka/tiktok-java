package project.monitor.activity;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface ActivityOrderService {

	/**
	 * 根据用户钱包地址，返回该用户的活动
	 * 
	 * @return 前端API格式 activity_id:"", //活动id status: 0, //0未参与活动，1已参与活动 pop_up: 0,
	 *         //0无需弹窗，1需弹窗 title: "",//活动标题 img: "",// 活动标题图片 content: "",//活动内容
	 *         img_detail: "",//活动内容图片
	 */
	public Map<String, String> saveFindBy(String from);

	public boolean savejoin(String from, String activityid);

	public void saveOrderProcess(ActivityOrder entity);

	public List<ActivityOrder> findBeforeDate(int succeeded, Date createTime);
	
	public ActivityOrder findByPartyId(String partyId);
	
	public void save(ActivityOrder entity);

	public void update(ActivityOrder entity);

	public ActivityOrder findById(String id);
	

	public void delete(ActivityOrder entity);
}
