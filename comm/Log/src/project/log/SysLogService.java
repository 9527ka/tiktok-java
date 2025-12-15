package project.log;

/**
 * 系统日志
 */
public interface SysLogService {
    
	/**
	 * 同步保存
	 */
	public void saveSync(SysLog entity);

	/**
	 * 异步保存
	 */
	public void saveAsyn(SysLog entity);
	
	

	

}
