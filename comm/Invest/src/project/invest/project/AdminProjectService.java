package project.invest.project;

import kernel.web.Page;
import project.invest.project.model.Project;
import project.invest.project.model.ProjectLang;

import java.util.List;

public interface AdminProjectService {

    /**
     * 分页查询
     * @param pageNo
     * @param pageSize
     * @param name
     * @param status
     * @param startTime
     * @param endTime
     * @return
     */
    Page pagedQuery(int pageNo, int pageSize, String name,  Integer status, String startTime, String endTime,Integer ending, String PName);


    /**
     * 新增
     * @param name
     */
    void save(String name,String baseId);

    /**
     * 根据id查询
     * @param id
     * @return
     */
    Project findById(String id);

    /**
     * 根据goodsId与语言查找
     * @param GoodsId
     * @param lang
     * @return
     */
    List<ProjectLang> findLanByProjectId(String GoodsId, String lang);


    void delete(String id,List<ProjectLang> projectLangList);

    void update(Project project, String name, String lang, String guarantyAgency, String desSafe_text, String desSettle_text, String desUse_text,String projectLanId);

    /**
     * 是否首页推荐
     * @param id
     * @param status
     */
    void updateStatus(String id, Integer status);

    /**
     * 更新项目进度
     * @param typeValue
     * @param proportion
     */
    void updateRenew(Integer typeValue, Double proportion);

    /**
     * 根据项目分类查询项目
     */
    List<Project> findProjectByBaseId(String BaseId);
}