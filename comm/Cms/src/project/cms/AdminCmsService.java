package project.cms;

import java.util.Map;

import kernel.web.Page;

public interface AdminCmsService {


	public void saveOrUpdate(Cms entity);


	public Cms findById(String id);

	public void delete(Cms cms);

	public Map<String, String> getModelDatasDictionary();


	Page pagedQuery(int pageNo, int pageSize, String language, String title, String startTime, String endTime, Integer type, Integer status);
}
