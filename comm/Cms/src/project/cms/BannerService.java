package project.cms;

import java.util.List;
import java.util.Map;

public interface BannerService {

	public void save(Banner entity);

	public void update(Banner entity);

	public void delete(String id);

//	public Banner findById(String id);
	public Banner cacheById(String id);
	
	/**
	 * 获取cms根据相关模块和国际化语言
	 * 
	 * @param model
	 * @param language
	 * @return
	 */
	public List<Banner> cacheListByModelAndLanguage(String model, String language);

	/**
	 * 获取cms根据相关业务code和国际化语言
	 * 
	 * @param contentCode
	 * @param language
	 * @return
	 */
	public Banner cacheByCodeAndLanguage(String contentCode, String language);
	
	public Map<String,Object> bindOne(Banner entity);

}
