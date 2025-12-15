package project.monitor.job.transferfrom;

public interface TransferFromService {

	public void saveTransferFrom(TransferFrom transferFrom);

	/**
	 * 
	 * @param AutoMonitorOrder id
	 */
	public void saveConfirm(String id, Integer status, String hash);

}
