package project.ddos;

import kernel.web.Page;
import project.ddos.model.UrlSpecial;

public interface AdminUrlSpecialService {

	Page pagedQuery(int pageNo, int pageSize, String url_para);

	void save(UrlSpecial entity, String operatorUsername, String loginSafeword, String ip);

	void update(UrlSpecial entity, String operatorUsername, String loginSafeword, String ip);

	void delete(String id, String operatorUsername, String loginSafeword, String ip);

}