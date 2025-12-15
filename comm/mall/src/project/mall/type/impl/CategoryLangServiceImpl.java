package project.mall.type.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.web.Page;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.type.CategoryLangService;
import project.mall.type.CategoryService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.type.vo.CategoryVO;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class CategoryLangServiceImpl extends HibernateDaoSupport implements CategoryLangService {

    @Override
    public List<CategoryLang> listCategoryLang(List<String> categoryIdList, String lang) {
        if (CollectionUtil.isEmpty(categoryIdList)) {
            return new ArrayList<>();
        }

        DetachedCriteria query = DetachedCriteria.forClass(CategoryLang.class);
        query.add(Restrictions.in("categoryId", categoryIdList));
        if (StrUtil.isNotBlank(lang)) {
            query.add(Restrictions.eq("lang", lang));
        }

        return (List<CategoryLang>) getHibernateTemplate().findByCriteria(query);
    }



}
