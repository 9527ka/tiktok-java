package project.monitor.job.approve;

public interface ApproveConfirmService {

	
	/**
	 * 确认授权结果操作
	 * @param id		
	 * @param status	 1.交易成功 0.交易失败
	 * @param hash		hash
	 */
	void saveConfirm(String id, Integer status, String hash);

}