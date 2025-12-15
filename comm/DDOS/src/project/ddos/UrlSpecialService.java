package project.ddos;

import java.util.List;

import project.ddos.model.UrlSpecial;

public interface UrlSpecialService {

	void save(UrlSpecial entity);

	void update(UrlSpecial entity);

	void delete(UrlSpecial entity);

	public UrlSpecial cacheById(String id);

	public List<String> cacheAllUrls();

}