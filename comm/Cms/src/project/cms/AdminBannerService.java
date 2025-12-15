package project.cms;

import java.util.Map;

import kernel.web.Page;

public interface AdminBannerService {

	public Page pagedQuery(int pageNo, int pageSize,String language);

}
