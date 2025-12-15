package project.log;

import java.io.Serializable;

public interface CodeLogService {
	/**
	 * 同步保存
	 */
	public void saveSync(CodeLog entity);

	/**
	 * 异步保存
	 */
	public void saveAsyn(CodeLog entity);


}
