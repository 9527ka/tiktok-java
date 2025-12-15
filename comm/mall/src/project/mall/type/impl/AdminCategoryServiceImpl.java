package project.mall.type.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.MallRedisKeys;
import project.mall.type.AdminCategoryService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.redis.RedisHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class AdminCategoryServiceImpl extends HibernateDaoSupport implements AdminCategoryService {

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;

    private JdbcTemplate jdbcTemplate;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String parentId, Integer level, String startTime, String endTime) {
        StringBuffer queryString = new StringBuffer();
        Map<String, Object> parameters = new HashMap<String, Object>();
        queryString.append(" SELECT ");
        queryString.append(" c.UUID id, l.NAME name, c.PARENT_ID parentId, c.RANK rank, c.LEVEL level, l.LANG lang, ");
        //queryString.append(" c.UUID id, l.NAME name, l.LANG lang, ");
        queryString.append(" c.STATUS status, c.SORT sort, l.CATEGORY_ID categoryId, c.CREATE_TIME createTime, ");
        queryString.append(" c.ICON_IMG iconImg, c.REC_TIME recTime ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_CATEGORY c LEFT JOIN T_MALL_CATEGORY_LANG l ON c.UUID = l.CATEGORY_ID ");
        queryString.append(" WHERE c.TYPE = 1 and l.LANG = 'cn' ");

        if (!StringUtils.isNullOrEmpty(parentId) && !Objects.equals(parentId, "0")) {
            queryString.append(" and c.PARENT_ID =:parentId");
            parameters.put("parentId", parentId);
        }
        if (level > 0) {
            queryString.append(" and c.LEVEL =:level");
            parameters.put("level", level);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(c.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(c.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY c.LEVEL, c.CREATE_TIME DESC ");

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        for (Map<String, Object> map : (List<Map<String, Object>>) page.getElements()) {
            int pageLevel = (Integer) map.get("level");
            if (pageLevel == 2) {
                String parentCategoryLang = redisHandler.getString(MallRedisKeys.TYPE_LANG + "cn" + ":" + map.get("parentId"));
                if (StringUtils.isEmptyString(parentCategoryLang)) {
                    continue;
                }
                CategoryLang pLang = JSONArray.parseObject(parentCategoryLang, CategoryLang.class);
                map.put("name", pLang.getName() + "-" + map.get("name"));
            }
        }
        return page;
    }

    public List listCategoryBylevel(Integer level) {
        StringBuffer queryString = new StringBuffer();
        Map<String, Object> parameters = new HashMap<String, Object>();
        queryString.append(" SELECT ");
        queryString.append(" c.UUID id, l.NAME name");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_CATEGORY c LEFT JOIN T_MALL_CATEGORY_LANG l ON c.UUID = l.CATEGORY_ID ");
        queryString.append(" WHERE l.LANG = 'cn' and c.TYPE=1 and c.LEVEL = " + level);
        queryString.append(" ORDER BY c.CREATE_TIME DESC ");
        List<Map<String, Object>> list = this.jdbcTemplate.queryForList(queryString.toString());
        return list;
    }

    public LinkedHashMap<Object, String> getParentCategory(String categoryId, Integer level) {
        LinkedHashMap<Object, String> categoryMap = new LinkedHashMap<>();

        List<Map<String, Object>> categorys = listCategoryBylevel(level);
        for (Map<String, Object> category : categorys) {
            if (null != categoryId && categoryId.equals((String) category.get("id"))) {
                continue;
            }
            categoryMap.put(category.get("id"), (String) category.get("name"));
        }
        return categoryMap;
    }

    @Override
    public void save(String name, int rank, String des, String parentId) {
        Category category = new Category();
        if (rank <= 0) {
            rank = 9999;
        }
        category.setSort(rank);
        category.setRank(rank);
        category.setRecTime(0L);
        category.setStatus(0);
        category.setCreateTime(new Date());
        category.setType(1);
        if (StrUtil.isBlank(parentId)
                || parentId.equals("null")
                || parentId.equals("0")) {
            parentId = "0";
            category.setLevel(1);
        } else {
            category.setLevel(2);
        }
        category.setParentId(parentId);

        this.getHibernateTemplate().save(category);

        CategoryLang categoryLang = new CategoryLang();
        categoryLang.setCategoryId(category.getId().toString());
        categoryLang.setLang("cn");
        categoryLang.setName(name);
        categoryLang.setDes(des);
        redisHandler.setSyncString(MallRedisKeys.TYPE_LANG + categoryLang.getLang() + ":" + categoryLang.getCategoryId(), JSON.toJSONString(categoryLang));
        getHibernateTemplate().save(categoryLang);
    }

    @Override
    public List<CategoryLang> findLanByCategoryId(String categoryId, String lang) {
        String sql = "SELECT DISTINCT " +
                "mc.UUID " +
                "FROM T_MALL_CATEGORY mc " +
                "WHERE mc.`STATUS` = 1 and mc.TYPE=1 ";
        List<String> categoryIds = this.jdbcTemplate.queryForList(sql, String.class);
        if (CollectionUtil.isEmpty(categoryIds)) {
            return null;
        }

        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(CategoryLang.class);
        if (!StringUtils.isEmptyString(categoryId)) {
            criteria.add(Restrictions.eq("categoryId", categoryId));
        }

        if (!StringUtils.isEmptyString(lang)) {
            criteria.add(Restrictions.eq("lang", lang));
        }
        if (CollectionUtil.isNotEmpty(categoryIds)) {
            criteria.add(Restrictions.in("categoryId", categoryIds));
        }
        if (CollectionUtils.isNotEmpty(criteria.list())) {
            return criteria.list();
        }
        return null;
    }

    @Override
    public List<CategoryLang> findLanByCategoryIds(String categoryId, String lang) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(CategoryLang.class);
        if (!StringUtils.isEmptyString(categoryId)) {
            criteria.add(Restrictions.eq("categoryId", categoryId));
        }

        if (!StringUtils.isEmptyString(lang)) {
            criteria.add(Restrictions.eq("lang", lang));
        }
        if (CollectionUtils.isNotEmpty(criteria.list())) {
            return criteria.list();
        }
        return null;
    }


    @Override
    public void update(Category bean, String name, String lang, String categoryId, String categoryLanId, String des) {
        List<CategoryLang> lanByCategoryId = this.findLanByCategoryIds(bean.getId().toString(), lang);
        CategoryLang categoryLang = new CategoryLang();
        if (CollectionUtils.isEmpty(lanByCategoryId)) {
            categoryLang.setName(name);
            categoryLang.setCategoryId(bean.getId().toString());
            categoryLang.setLang(lang);
            categoryLang.setDes(des);
            getHibernateTemplate().save(categoryLang);
        } else {
            categoryLang = lanByCategoryId.get(0);
            categoryLang.setDes(des);
            categoryLang.setName(name);
            getHibernateTemplate().update(categoryLang);
        }

        redisHandler.setSyncString(MallRedisKeys.TYPE_LANG + categoryLang.getLang() + ":" + bean.getId().toString(), JSON.toJSONString(categoryLang));
        this.getHibernateTemplate().update(bean);
    }

    @Override
    public Category findById(String id) {
        return this.getHibernateTemplate().get(Category.class, id);
    }

    @Override
    public int count(String categoryId) {

        StringBuffer countSql = new StringBuffer("SELECT COUNT(1)  FROM  T_MALL_CATEGORY WHERE TYPE = 1 and PARENT_ID='" + categoryId + "'");
        int totalCnt = jdbcTemplate.queryForObject(countSql.toString(), Integer.class);
        return totalCnt;
    }

    @Override
    public void delete(List<CategoryLang> categoryLangList) {
        categoryLangList.forEach(e -> {
            this.getHibernateTemplate().delete(e);
            redisHandler.remove(MallRedisKeys.TYPE_LANG + e.getLang() + ":" + e.getCategoryId().toString());
        });
    }

    @Override
    public void updateStatus(String id, Integer status) {
        Category category = this.findById(id);
        if (status != 0) {
            category.setRecTime(new Date().getTime());
        } else {
            category.setRecTime(0L);
        }

        this.getHibernateTemplate().update(category);
    }

    /**
     * 注意：如果只更新局部字段不要调用本方法！
     *
     * @param entity
     */
    public void update(Category entity) {
        if (entity == null || entity.getId() == null) {
            return;
        }

        this.getHibernateTemplate().update(entity);
    }

    @Override
    public void updateHideCategory(String categoryId) {
        if (StrUtil.isBlank(categoryId) || Objects.equals(categoryId, "0")) {
            return;
        }
        String hqlUpdate = "update Category c set c.type = 0 where c.id = :id or c.parentId = :id ";
        getHibernateTemplate().execute(session -> {
            return session.createQuery(hqlUpdate)
                    .setParameter("id", categoryId)
                    .executeUpdate();
        });
    }

    public List listSubCategory(String parentId) {
        StringBuffer queryString = new StringBuffer();

        queryString.append(" SELECT ");
        queryString.append(" c.UUID id, l.NAME name, c.PARENT_ID parentId, c.RANK rank, c.LEVEL level, l.LANG lang, ");
        queryString.append(" c.STATUS status, c.SORT sort, l.CATEGORY_ID categoryId, c.CREATE_TIME createTime, ");
        queryString.append(" c.ICON_IMG iconImg, c.REC_TIME recTime ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_CATEGORY c LEFT JOIN T_MALL_CATEGORY_LANG l ON c.UUID = l.CATEGORY_ID ");
        queryString.append(" WHERE l.LANG = 'cn' and c.TYPE=1 and c.PARENT_ID = ? ");
        queryString.append(" ORDER BY c.CREATE_TIME DESC ");

        List<Map<String, Object>> list = this.jdbcTemplate.queryForList(queryString.toString(), parentId);

        return list;
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}