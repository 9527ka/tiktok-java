package project.news;

import java.io.Serializable;

import kernel.web.Page;

public interface AdminNewsService {

	public News findById(Serializable id);

	/**
	 * 新增新闻
	 * @param entity
	 */
	public void save(News entity);

	public void delete(News news);

	public void update(News entity);

	Page pagedQuery(int pageNo, int pageSize, String title, String lang, String startTime, String endTime, Integer status);
}
