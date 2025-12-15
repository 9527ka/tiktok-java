package project.news;

import java.io.Serializable;
import java.util.List;

import kernel.web.Page;
import project.invest.expert.model.Expert;
import project.invest.project.model.Project;

public interface NewsService {

	public News findById(Serializable id);
	
	public News cacheById(Serializable id);

	public News getIndex(String language);

	public void save(News entity);

	public void delete(String id);

	public void update(News entity);

	public Page cachePagedQuery(int pageNo, int pageSize, String language);

	List<String> selectByLanguage(String language);

	List<String> selectAnnouncements(String language);

	List<News>  listNewsPage(String language, int pageNum, int pageSize);

	List<Expert>  listExpertPage(String language, int pageNum, int pageSize);

}
