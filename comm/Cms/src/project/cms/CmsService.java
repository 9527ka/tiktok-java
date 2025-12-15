package project.cms;

import java.util.List;

public interface CmsService {

	public void saveOrUpdate(Cms entity);

	public void delete(Cms cms);

	/**
	 * 获取cms根据相关模块和国际化语言
	 * @param language
	 * @return
	 */
	public List<Cms> cacheListByModelAndLanguage(String language);

	/**
	 * 根据国际化语言获取cms
	 * @param lang
	 * @return
	 */
	public List<Cms> findCmsListByLang(String lang);

	/**
	 * 获取cms根据相关业务code和国际化语言
	 *
	 * @param contentCode
	 * @param language
	 * @return
	 */
	public Cms cacheByCodeAndLanguage(String contentCode, String language);

}
