package project.log;

import java.io.Serializable;
import java.util.Date;

import kernel.web.Page;

public interface LogService {
	/**
	 * 同步保存
	 */
	public void saveSync(Log entity);

	/**
	 * 异步保存
	 */
	public void saveAsyn(Log entity);

	public Page pagedQuery(int pageNo, int pageSize, Serializable partyId, String[] category, String[] extra,
			Date createTime_begin, Date createTime_end);

}
