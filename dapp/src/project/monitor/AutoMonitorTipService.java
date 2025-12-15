package project.monitor;

import java.io.Serializable;

import project.monitor.model.AutoMonitorTip;

public interface AutoMonitorTipService {

	/**
	 * 读完后判断阀值 提醒（业务不应该在这里实现，调用接口 如果不存在则加入提醒列表，如果存在提醒列表里则不做操作
	 *
	 *
	 * 未完成
	 */
	public void saveTipNewThreshold(AutoMonitorTip entity);

	/**
	 * before 可以为空，不计条件，否则是这几个小时前内是否有数据
	 */
	public AutoMonitorTip find(String partyId, int tiptype, Integer before);
	
	
	public void update(AutoMonitorTip entity);
}
