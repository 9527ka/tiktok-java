package project.mall.goods.impl;

import com.sun.xml.bind.v2.model.core.ID;
import jnr.ffi.annotations.In;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import org.apache.http.util.Asserts;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.util.Assert;
import project.mall.goods.GoodsAttributeCategoryService;
import project.mall.goods.KeepGoodsService;
import project.mall.goods.dto.CategoryGoodCountDto;
import project.mall.goods.dto.GoodsAttributeCategoryDto;
import project.mall.goods.model.GoodsAttribute;
import project.mall.goods.model.GoodsAttributeCategory;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class GoodsAttributeCategoryServiceImpl extends HibernateDaoSupport implements GoodsAttributeCategoryService {


    private JdbcTemplate jdbcTemplate;


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int getCount() {
        StringBuffer countSql = new StringBuffer("select count(1)  FROM  T_MALL_GOODS_ATTRIBUTE_CATEGORY");
        int  totalCnt = jdbcTemplate.queryForObject(countSql.toString(),Integer.class);
        return totalCnt;
    }

    @Override
    public List<GoodsAttributeCategoryDto> list(int pageNum, int pageSize , String names) {
//        int start = (pageNum - 1) * pageSize;
        StringBuffer queryString=new StringBuffer(" SELECT c.*,ac.attrCount FROM T_MALL_GOODS_ATTRIBUTE_CATEGORY c ");
        queryString.append("LEFT JOIN (SELECT COUNT(*) AS 'attrCount' ,CATEGORY_ID FROM T_MALL_GOODS_ATTRIBUTE GROUP BY  CATEGORY_ID) ac ON c.ID=ac.CATEGORY_ID WHERE 1 = 1 ");
        if (!StringUtils.isNullOrEmpty(names)) {
            queryString.append(" AND (c.NAME like '%"+names+"%' or c.ID like '%"+names+"%' )");
        }
        queryString.append(" ORDER BY c.CREATE_TIME DESC ");
        queryString.append("   limit " + (pageNum - 1) * pageSize + "," + pageSize);
        List list=jdbcTemplate.queryForList(queryString.toString());
//        List list = jdbcTemplate.queryForList("SELECT c.*,ac.attrCount FROM t_mall_goods_attribute_category c " +
//                "LEFT JOIN (SELECT COUNT(*) AS 'attrCount' ,CATEGORY_ID FROM t_mall_goods_attribute GROUP BY  CATEGORY_ID) ac " +
//                "ON c.ID=ac.CATEGORY_ID LIMIT " + start + "," + pageSize + "");
        Iterator it = list.iterator();
        List<GoodsAttributeCategoryDto> resultList = new ArrayList<>();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            GoodsAttributeCategoryDto dto = new GoodsAttributeCategoryDto();
            String id = rowMap.get("ID").toString();
            String name = rowMap.get("NAME").toString();
            String sort = rowMap.get("SORT").toString();
            dto.setSort(sort);
            dto.setName(name);
            dto.setId(id);
            if (rowMap.get("attrCount") != null) {
                dto.setAttrCount(Integer.parseInt(rowMap.get("attrCount").toString()));
            }
            if (rowMap.get("CREATE_TIME") != null) {
                LocalDateTime localDateTime = (LocalDateTime) rowMap.get("CREATE_TIME");
                dto.setCreateTime(Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant()));
            }
            resultList.add(dto);
        }
        return resultList;
    }


    /**
     * 新增分类
     *
     * @param name
     * @param sort
     */
    @Override
    public void save(String name, int sort) {
        GoodsAttributeCategory category = new GoodsAttributeCategory();
        category.setName(name);
        category.setSort(sort);
        category.setCreateTime(new Date());
        getHibernateTemplate().save(category);
    }


    @Override
    public void updateById(String id, String name, int sort) {
        GoodsAttributeCategory goodsAttributeCategory=       getHibernateTemplate().get(GoodsAttributeCategory.class,id);
        if (goodsAttributeCategory==null){
            throw  new BusinessException("参数错误");
        }
        goodsAttributeCategory.setSort(sort);
        goodsAttributeCategory.setName(name);
        getHibernateTemplate().update(goodsAttributeCategory);
    }

    /**
     * 删除
     * @param id
     */
    @Override
    public void deleteById(String id) {
        GoodsAttributeCategory goodsAttributeCategory= getHibernateTemplate().get(GoodsAttributeCategory.class,id);
        if (goodsAttributeCategory==null){
            throw  new BusinessException("属性已删除或不存在!");
        }
        getHibernateTemplate().delete(goodsAttributeCategory);
    }

    @Override
    public List<GoodsAttributeCategory> findAllAttributeCategory() {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(GoodsAttributeCategory.class);
        criteria.addOrder(Order.desc("createTime"));
        return criteria.list();
    }
}
