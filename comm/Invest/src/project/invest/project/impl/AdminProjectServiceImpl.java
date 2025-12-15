package project.invest.project.impl;

import com.alibaba.fastjson.JSON;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.InvestRedisKeys;
import project.invest.project.AdminProjectService;
import project.invest.project.model.Project;
import project.invest.project.model.ProjectLang;
import project.redis.RedisHandler;

import java.util.*;

public class AdminProjectServiceImpl extends HibernateDaoSupport implements AdminProjectService {
    private static Log log = LogFactory.getLog(AdminProjectServiceImpl.class);

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;

    private JdbcTemplate jdbcTemplate;



    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name, Integer status, String startTime, String endTime, Integer ending, String PName) {

        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" p.UUID id, c.PName, l.GUARANTY_AGENCY guarantyAgency, p.INVEST_SIZE investSize, l.NAME name, p.STATUS status, p.TYPE type, ");
        queryString.append(" p.INVEST_PROGRESS investProgress, p.INVEST_PROGRESS_MAN investProgressMan, p.INVEST_MIN investMin, p.INVEST_MAX investMax,");
        queryString.append(" p.BONUS_RATE bonusRate, p.BONUS bonus, p.INVEST_SELL_ADD investSellAdd, p.ENDING ending, p.REC_TIME recTime, p.CREATE_TIME createTime ");
        queryString.append(" FROM ");
        queryString.append(" T_INVEST_PROJECT p ");
        queryString.append(" LEFT JOIN T_INVEST_PROJECT_LANG l ON p.UUID = l.PROJECT_ID ");
        queryString.append(" LEFT JOIN ( SELECT NAME AS PName, BASE_ID AS baseId FROM T_INVEST_CATEGORY WHERE LANG = 'cn' ) c ON p.BASE_ID = c.baseId ");
        queryString.append(" WHERE 1=1 and l.LANG = 'cn' ");
        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isNullOrEmpty(name)) {
            queryString.append(" AND l.NAME like:name ");
            parameters.put("name", "%" + name + "%");
        }
        if (!StringUtils.isNullOrEmpty(PName)) {
            queryString.append(" AND c.PName like :PName ");
            parameters.put("PName", "%" + PName + "%");
        }
        if (null != status) {
            queryString.append(" AND p.status =:status ");
            parameters.put("status", status);
        }
        if (null != ending) {
            queryString.append(" AND p.ending =:ending ");
            parameters.put("ending", ending);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(p.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(p.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY p.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Project findById(String id) {
        return this.getHibernateTemplate().get(Project.class, id);
    }



    @Override
    public List<ProjectLang> findLanByProjectId(String projectId, String lang) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(ProjectLang.class);
        criteria.add( Restrictions.eq("projectId",  projectId) );
        if(!StringUtils.isEmptyString(lang)){
            criteria.add( Restrictions.eq("lang",  lang) );
        }
        if(CollectionUtils.isNotEmpty(criteria.list())){
            return  criteria.list();
        }
        return null;
    }

    @Override
    public List<Project> findProjectByBaseId(String BaseId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Project.class);
        criteria.add( Restrictions.eq("baseId",  BaseId) );
        if(CollectionUtils.isNotEmpty(criteria.list())){
            return  criteria.list();
        }
        return null;
    }


    private List<ProjectLang> findProjectByName(String name, String lang){
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(ProjectLang.class);
        criteria.add( Restrictions.eq("name",  name) );
        criteria.add( Restrictions.eq("lang", lang ) );
        return criteria.list();
    }

    @Override
    public void save(String name,String baseId) {
        List<ProjectLang> projectLangs = findProjectByName(name,"cn");
        if(CollectionUtils.isNotEmpty(projectLangs)){
            throw new BusinessException("商品名称已存在");
        }
        Project project = new Project();
        project.setBonusRate(0);
        project.setBonus(0);
        project.setEnding(0);
        project.setStatus(1);
        project.setType(1);
        project.setBaseId(baseId);
        project.setRepeating(false);
        project.setUpTime(new Date().getTime());
        project.setCreateTime(new Date());
        project.setSort(0);
        this.getHibernateTemplate().save(project);

        ProjectLang projectLang = new ProjectLang();
        projectLang.setName(name);
        projectLang.setLang("cn");
        projectLang.setProjectId(project.getId().toString());
        getHibernateTemplate().save(projectLang);
        redisHandler.setSyncString(InvestRedisKeys.INVEST_PROJECT_LANG+projectLang.getLang()+":"+project.getId().toString(), JSON.toJSONString(projectLang));
    }




    @Override
    public void delete(String id,List<ProjectLang> projectLangList) {
        Project project = this.findById(id);
        this.getHibernateTemplate().delete(project);
        projectLangList.forEach(e ->{
            getHibernateTemplate().delete(e);
        });
    }

    @Override
    public void update(Project bean, String name, String lang, String guarantyAgency, String desSafe_text, String desSettle_text, String desUse_text, String projectLanId) {

        log.info("是否存在为空 projectLanId = "+projectLanId);
        List<ProjectLang> lanByProjectId = this.findLanByProjectId(bean.getId().toString(), lang);
        if(CollectionUtils.isEmpty(lanByProjectId)){
            ProjectLang projectLang = new ProjectLang();
            projectLang.setName(name);
            projectLang.setProjectId(bean.getId().toString());
            projectLang.setDesSafe(desSafe_text);
            projectLang.setGuarantyAgency(guarantyAgency);
            projectLang.setDesSettle(desSettle_text);
            projectLang.setDesUse(desUse_text);
            projectLang.setLang(lang);
            getHibernateTemplate().save(projectLang);
            redisHandler.setSyncString(InvestRedisKeys.INVEST_PROJECT_LANG+lang+":"+bean.getId().toString(), JSON.toJSONString(projectLang));
        } else {
            ProjectLang projectLang = lanByProjectId.get(0);
            projectLang.setName(name);
            projectLang.setDesSafe(desSafe_text);
            projectLang.setGuarantyAgency(guarantyAgency);
            projectLang.setDesSettle(desSettle_text);
            projectLang.setDesUse(desUse_text);
            getHibernateTemplate().update(projectLang);
            redisHandler.setSyncString(InvestRedisKeys.INVEST_PROJECT_LANG+lang+":"+bean.getId().toString(), JSON.toJSONString(projectLang));
        }

        this.getHibernateTemplate().update(bean);
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Project project = this.findById(id);
        if(status!=0){
            project.setRecTime(new Date().getTime());
        } else {
            project.setRecTime(0);
        }
        this.getHibernateTemplate().update(project);


    }

    @Override
    public void updateRenew(Integer type, Double proportion) {
        String sql = "";
        if(type == 1){
            sql = "UPDATE T_INVEST_PROJECT SET INVEST_PROGRESS_MAN = INVEST_PROGRESS_MAN + "  + proportion;
        } else {
            sql = "UPDATE T_INVEST_PROJECT SET INVEST_PROGRESS_MAN = INVEST_PROGRESS_MAN + INVEST_SELL_ADD";
        }
        jdbcTemplate.update(sql);
        this.getHibernateTemplate().getSessionFactory().getCurrentSession().clear();
    }


    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}