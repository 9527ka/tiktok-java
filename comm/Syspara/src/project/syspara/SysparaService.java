package project.syspara;

import kernel.web.Page;

/**
 * 系统参数
 */
public interface SysparaService {

	public Syspara find(String code);

	Syspara findFromDB(String code);

	String findString(String code);

	/**
	 * 数据库读取，主要用于bean初始化，（spring 初始化bean在redis数据加载之前，导致无法读取问题）
	 * 
	 * @param code
	 * @return
	 */
	public Syspara findByDB(String code);

	public void save(Syspara entity);

	public void update(Syspara entity);

	/**
	 * 返回type=2(系统参数，管理员可修改)参数
	 */
	public Page pagedQuery(int pageNo, int pageSize);
		
	/**
	 * 获取 系统参数（ROOT) 列表
	 */
	public Page pagedQueryByNotes(int pageNo, int pageSize, String notes_para);
	
	/**
	 * 获取 系统参数（ADMIN) 列表
	 */
	public Page pagedQueryByNotesAdmin(int pageNo, int pageSize, String notes_para);

    void loadCacheUpdate();
}
