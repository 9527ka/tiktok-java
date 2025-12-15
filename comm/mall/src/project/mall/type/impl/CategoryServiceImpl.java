package project.mall.type.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import kernel.util.StringUtils;
import kernel.web.Page;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.MallRedisKeys;
import project.mall.type.CategoryService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.type.vo.CategoryVO;
import project.redis.RedisHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CategoryServiceImpl extends HibernateDaoSupport implements CategoryService {

    private RedisHandler redisHandler;

    @Override
    public Category getById(String id) {
        if (StrUtil.isBlank(id)) {
            return null;
        }

        return getHibernateTemplate().get(Category.class, id);
    }

    @Override
    public List<Category> listByIds(List<String> idList) {
        if (CollectionUtil.isEmpty(idList)) {
            return new ArrayList<>();
        }

        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
        query.add(Restrictions.in("id", idList));
        return (List<Category>) getHibernateTemplate().findByCriteria(query);
    }


    @Override
    public List<Category> listCategory(int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
        query.add(Restrictions.eq("type", 1));
        query.add(Restrictions.eq("status", 1));
        query.addOrder(Order.asc("rank"));
        return (List<Category>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

    @Override
    public Page pageListCategory(int pageNum, int pageSize) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Category.class);
        criteria.add(Restrictions.eq("type", 1));
        criteria.addOrder(Order.asc("rank"));

        int total = 0;
        Object rowCountInfo = criteria.setProjection(Projections.rowCount()).uniqueResult();
        if (rowCountInfo != null) {
            total = ((Long) (rowCountInfo)).intValue();
        }
        criteria.setProjection(null);

        Page page = new Page(pageNum, pageSize, Integer.MAX_VALUE);

        //HibernateUtils.applyParameters(query, parameters);
        criteria.setFirstResult(page.getFirstElementNumber());
        criteria.setMaxResults(pageSize);
        List list = criteria.list();
        page.setElements(list);
        page.setThisPageNumber(list.size());
        page.setTotalElements(total);

        return page;
    }

    @Override
    public List<Category> listRecommendCategory(int pageNum, int pageSize) {
        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
        query.add(Restrictions.eq("status", 1));
        query.add(Restrictions.eq("type", 1));
        query.add(Restrictions.gt("recTime", 0L));
        query.addOrder(Order.asc("rank"));
        return (List<Category>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }


    @Override
    public List<Category> listTopLevelCategorys() {
        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
        query.add(Restrictions.eq("type", 1));
        query.add(Restrictions.gt("parentId", "0"));
        query.add(Restrictions.eq("level", 1));

        query.addOrder(Order.asc("rank"));

        return (List<Category>) getHibernateTemplate().findByCriteria(query);
    }

    @Override
    public List<Category> listSubCategorys(String parentId) {
        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
        query.add(Restrictions.eq("type", 1));
        query.add(Restrictions.eq("status", 1));
        if (StrUtil.isNotBlank(parentId) && !Objects.equals(parentId, "0")) {
            query.add(Restrictions.eq("parentId", parentId));
        }
        query.add(Restrictions.eq("level", 2));

        query.addOrder(Order.asc("rank"));

        return (List<Category>) getHibernateTemplate().findByCriteria(query);
    }

    @Override
    public List<CategoryVO> getCategoryTree(boolean showHidden) {
        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
        if (!showHidden) {
            // 只展示未被禁用的分类
            query.add(Restrictions.eq("status", 1));
        }
        query.add(Restrictions.eq("type", 1));

        List<Category> allValidCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(query);
        List<String> categoryIdList = allValidCategoryList.stream().map(entity -> entity.getId().toString()).collect(Collectors.toList());

        return loadBuildCategoryTree(categoryIdList);
    }


    @Override
    public List<CategoryVO> loadBuildCategoryTree(List<String> categoryIdList) {
        if (CollectionUtil.isEmpty(categoryIdList)) {
            return new ArrayList<>();
        }

        DetachedCriteria query = DetachedCriteria.forClass(Category.class);
        // 只展示未被禁用的分类
        query.add(Restrictions.in("id", categoryIdList));

        List<Category> allValidCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(query);
        Map<String, Category> allCategoryMap = allValidCategoryList.stream().collect(Collectors.toMap(entity -> entity.getId().toString(), Function.identity(), (key1, key2) -> key2));

        Map<String, List<Category>> topCategoryTreeMap = new HashMap<>();
        for (Category oneCategory : allValidCategoryList) {
            // 二级分类
            if (StrUtil.isBlank(oneCategory.getParentId()) || Objects.equals(oneCategory.getParentId(), "0")) {
                // 一级分类
                List<Category> sublingList = topCategoryTreeMap.get(oneCategory.getId().toString());
                if (sublingList == null) {
                    sublingList = new ArrayList<>();
                }
                topCategoryTreeMap.put(oneCategory.getId().toString(), sublingList);
            } else {
                List<Category> sublingList = topCategoryTreeMap.get(oneCategory.getParentId());
                if (sublingList == null) {
                    // 上级分类还没遍历到，此处直接生成一条 map 记录
                    sublingList = new ArrayList<>();
                    topCategoryTreeMap.put(oneCategory.getParentId(), sublingList);
                }
                sublingList.add(oneCategory);
            }
        }

        List<CategoryVO> retTreeList = new ArrayList();
        for (String parentId : topCategoryTreeMap.keySet()) {
            Category topCategory = allCategoryMap.get(parentId);
            if (topCategory == null) {
                logger.error("---> 子分类指向的上级分类:" + parentId + " 记录不存在！");
                continue;
            }

            CategoryVO topCategoryVo = new CategoryVO();
            BeanUtil.copyProperties(topCategory, topCategoryVo);
            topCategoryVo.setId(topCategory.getId().toString());
            topCategoryVo.setSubList(new ArrayList());
            retTreeList.add(topCategoryVo);

            List<Category> subCategoryList = topCategoryTreeMap.get(parentId);
            if (CollectionUtil.isNotEmpty(subCategoryList)) {
                for (Category oneSubCategory : subCategoryList) {
                    CategoryVO subCategoryVo = new CategoryVO();
                    BeanUtil.copyProperties(oneSubCategory, subCategoryVo);
                    subCategoryVo.setId(oneSubCategory.getId().toString());

                    topCategoryVo.getSubList().add(subCategoryVo);
                }
            }
        }

        Collections.sort(retTreeList, new Comparator<CategoryVO>() {
            @Override
            public int compare(CategoryVO o1, CategoryVO o2) {
                return o1.getRank() - o2.getRank();
            }
        });
        for (CategoryVO oneTopCategory : retTreeList) {
            List<CategoryVO> oneSubCategoryList = oneTopCategory.getSubList();
            Collections.sort(oneSubCategoryList, new Comparator<CategoryVO>() {
                @Override
                public int compare(CategoryVO o1, CategoryVO o2) {
                    return o1.getRank() - o2.getRank();
                }
            });
        }

        return retTreeList;
    }


    @Override
    public CategoryLang selectLang(String lang, String categoryId) {
        CategoryLang categoryLang = null;
        String key = MallRedisKeys.TYPE_LANG + lang + ":" + categoryId;
        String value = this.redisHandler.getString(key);
        if (StringUtils.isNotEmpty(value)){
            categoryLang = JSONObject.parseObject(value, CategoryLang.class);
        }else {
            DetachedCriteria query = DetachedCriteria.forClass(CategoryLang.class);
            query.add(Restrictions.eq("categoryId", categoryId));
            query.add(Restrictions.eq("lang", lang));
            List<CategoryLang> results = (List<CategoryLang>) this.getHibernateTemplate().findByCriteria(query);
            if (CollectionUtil.isNotEmpty(results)){
                categoryLang = results.get(0);
                this.redisHandler.setSyncString(key, JSONObject.toJSONString(categoryLang));
            }
        }
        return categoryLang;
    }

    public void updateStatus(String categoryId, int status) {
        if (StrUtil.isBlank(categoryId) || Objects.equals(categoryId, "0")) {
            return;
        }
            String hqlUpdate = "update Category c set c.status = :status where c.id = :id or c.parentId = :id ";
            getHibernateTemplate().execute(session -> {
                return session.createQuery(hqlUpdate)
                        .setParameter("status", status)
                        .setParameter("id", categoryId)
                        .executeUpdate();
            });
    }


    public RedisHandler getRedisHandler() {
        return redisHandler;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }
}
