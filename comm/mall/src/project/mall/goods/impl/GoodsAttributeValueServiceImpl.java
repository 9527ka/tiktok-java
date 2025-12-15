package project.mall.goods.impl;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.goods.dto.GoodsAttributeDto;
import project.mall.goods.dto.GoodsAttributeValueDto;
import project.mall.goods.model.GoodsAttributeValue;
import project.mall.goods.model.GoodsAttributeValueLang;

import java.util.*;

public class GoodsAttributeValueServiceImpl  extends HibernateDaoSupport implements project.mall.goods.GoodsAttributeValueService {
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }
    @Override
    public List<GoodsAttributeValueDto> list(int pageNum, int pageSize, String attrId) {
        int start = (pageNum - 1) * pageSize;
        String sql = "SELECT av.ID,av.GOOD_ATTRIBUTE_ID,gvl.ATTR_VALUE_ID,gvl.LANG,gvl.`NAME`  FROM T_MALL_GOODS_ATTRIBUTE_VALUE av " +
                "    LEFT JOIN T_MALL_GOODS_ATTRIBUTE_VALUE_LANG gvl  ON av.ID=gvl.ATTR_VALUE_ID " +
                "    WHERE  av.GOOD_ATTRIBUTE_ID='"+attrId+"'AND IFNULL(gvl.LANG, 'en') ='en'  AND gvl.TYPE=0    LIMIT " + start + "," + pageSize + "";
        List list = jdbcTemplate.queryForList(sql);
        Iterator it = list.iterator();
        List<GoodsAttributeValueDto> resultList = new ArrayList<>();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            String id = (String) rowMap.getOrDefault("ID","");
            String name = (String)  rowMap.getOrDefault("NAME","");
            String lang = (String) rowMap.getOrDefault("LANG","");
            GoodsAttributeValueDto goodsAttributeValueDto=new GoodsAttributeValueDto();
            goodsAttributeValueDto.setAttrId(attrId);
            goodsAttributeValueDto.setName(name);
            goodsAttributeValueDto.setId(id);
            goodsAttributeValueDto.setLang(lang);
            resultList.add(goodsAttributeValueDto);
        }

        return resultList;
    }

    @Override
    public void saveOrUpdate(String name, String lang, String attrId, String id) {
        GoodsAttributeValue goodsAttributeValue=null;
        if (StrUtil.isNotEmpty(id)){
            goodsAttributeValue= getHibernateTemplate().get(GoodsAttributeValue.class,id);
        }
        if (goodsAttributeValue==null){
            goodsAttributeValue=new GoodsAttributeValue();
            goodsAttributeValue.setGoodAttributeId(attrId);
            goodsAttributeValue.setCreateTime(new Date());
            getHibernateTemplate().save(goodsAttributeValue);

            GoodsAttributeValueLang goodsAttributeValueLang = new GoodsAttributeValueLang();
            goodsAttributeValueLang.setAttrValueId(goodsAttributeValue.getId().toString());
            goodsAttributeValueLang.setLang(lang);
            goodsAttributeValueLang.setType(0);
            goodsAttributeValueLang.setName(name);
            getHibernateTemplate().save(goodsAttributeValueLang);
        }
        else {
            GoodsAttributeValueLang goodsAttributeValueLang= findLangData(id,lang);
            if (goodsAttributeValueLang==null){
                goodsAttributeValueLang=new GoodsAttributeValueLang();
                goodsAttributeValueLang.setAttrValueId(goodsAttributeValue.getId().toString());
                goodsAttributeValueLang.setLang(lang);
                goodsAttributeValueLang.setType(0);
                getHibernateTemplate().save(goodsAttributeValueLang);
            }
            goodsAttributeValueLang.setName(name);
            getHibernateTemplate().saveOrUpdate(goodsAttributeValue);
        }

    }

    @Override
    public void delete(String id) {
        GoodsAttributeValue  goodsAttributeValue= getHibernateTemplate().get(GoodsAttributeValue.class,id);
        if (goodsAttributeValue==null){
            throw  new BusinessException("参数错误!");
        }
        getHibernateTemplate().delete(goodsAttributeValue);
        DetachedCriteria queryValueLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
        queryValueLang.add(Property.forName("attrValueId").eq(id));
        queryValueLang.add(Property.forName("type").eq(0));
        List<GoodsAttributeValueLang> langs=  (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryValueLang);

        for (GoodsAttributeValueLang lang:langs){
            lang.setType(1);
            getHibernateTemplate().update(lang);
        }
    }

    /**
     * 获取参数国际化
     * @param attrValueId
     * @param lang
     * @return
     */
    @Override
    public GoodsAttributeValueLang findLangData(String attrValueId,String lang){
        DetachedCriteria queryValueLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
        queryValueLang.add(Property.forName("attrValueId").eq(attrValueId));
        queryValueLang.add(Property.forName("type").eq(0));
        queryValueLang.add(Property.forName("lang").eq(lang));
        List<GoodsAttributeValueLang> langs=  (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryValueLang);

        if (!CollectionUtils.isEmpty(langs)){
                return  langs.get(0);
        }
        return null;
    }

}
