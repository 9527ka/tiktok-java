package project.mall.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.FlushMode;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Property;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.mall.goods.GoodsAttributeService;
import project.mall.goods.dto.*;
import project.mall.goods.model.*;
import project.mall.orders.model.MallAddress;
import util.ArithmeticUtils;

import java.util.*;

public class GoodsAttributeServiceImpl extends HibernateDaoSupport implements GoodsAttributeService {
    private JdbcTemplate jdbcTemplate;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public boolean queryExistBySortAndCategoryId(int sort, String categoryId) {

        DetachedCriteria query = DetachedCriteria.forClass(GoodsAttribute.class);
        query.add(Property.forName("sort").eq(sort));
        query.add(Property.forName("categoryId").eq(categoryId));
        List<GoodsAttribute> list = (List<GoodsAttribute>) getHibernateTemplate().findByCriteria(query);
        return list.size() > 0;
    }

    /**
     * 新增
     * {
     * * "cn": {
     * * "categoryId": "402880f385f137160185f13b07310001",
     * * "id": "402880f385f3b2ac0185f3b2fc560000",
     * * "lang": "cn",
     * * "name": "颜色",
     * * "values": "白色,黑色,"
     * * }
     * * }
     */
    @Override
    public void save(Map<String, GoodsAttributeDescDto> attributes, GoodsAttributeDescDto goodsAttributeDescDto) {
//        Date now = new Date();
//        //属性
//        GoodsAttribute goodsAttribute = new GoodsAttribute();
//        goodsAttribute.setCreateTime(now);
//        goodsAttribute.setCategoryId(goodsAttributeDescDto.getCategoryId());
//        goodsAttribute.setSort(goodsAttributeDescDto.getSort());
//        getHibernateTemplate().save(goodsAttribute);
//        String cLang = null;
//        for (String lang : attributes.keySet()) {
//            if (cLang == null) {
//                cLang = lang;
//            }
//            GoodsAttributeDescDto dto = attributes.get(lang);
//            //属性国际化
//            GoodsAttributeLang goodsAttributeLang = new GoodsAttributeLang();
//            goodsAttributeLang.setAttrId(goodsAttribute.getId().toString());
//            goodsAttributeLang.setLang(lang);
//            goodsAttributeLang.setName(dto.getName());
//            goodsAttributeLang.setType(0);
//            getHibernateTemplate().save(goodsAttributeLang);
//        }
//        List<GoodsAttributeValue> goodsAttributeValues = new ArrayList<>();
//        for (String lang : attributes.keySet()) {
//            GoodsAttributeDescDto dto = attributes.get(lang);
//            //属性参数
//            List<String> valueList = StrUtil.split(dto.getValues(), ',');
//            if (CollectionUtils.isNotEmpty(valueList)) {
//                int i = 0;
//                for (String value : valueList) {
//                    //属性参数
//                    if (cLang.equals(lang)) {
//                        GoodsAttributeValue dbAttributeValue = new GoodsAttributeValue();
//                        dbAttributeValue.setGoodAttributeId(goodsAttribute.getId().toString());
//                        dbAttributeValue.setCreateTime(now);
//                        goodsAttributeValues.add(dbAttributeValue);
//                        getHibernateTemplate().save(dbAttributeValue);
//                    }
//                    //属性参数国际化
//                    if (i < goodsAttributeValues.size()) {
//                        GoodsAttributeValueLang goodsAttributeValueLang = new GoodsAttributeValueLang();
//                        goodsAttributeValueLang.setAttrValueId(goodsAttributeValues.get(i).getId().toString());
//                        goodsAttributeValueLang.setLang(lang);
//                        goodsAttributeValueLang.setType(0);
//                        goodsAttributeValueLang.setName(value);
//                        getHibernateTemplate().save(goodsAttributeValueLang);
//                    }
//                    i++;
//                }
//            }
//        }
    }

    @Override
    @Transactional
    public void saveAttr(GoodsAttributeDescDto descDto) {

        Date now = new Date();
        //属性
        GoodsAttribute goodsAttribute = new GoodsAttribute();
        goodsAttribute.setCreateTime(now);
        goodsAttribute.setCategoryId(descDto.getCategoryId());
        goodsAttribute.setSort(descDto.getSort());
        getHibernateTemplate().save(goodsAttribute);
        GoodsAttributeLang goodsAttributeLang = new GoodsAttributeLang();
        goodsAttributeLang.setAttrId(goodsAttribute.getId().toString());
        goodsAttributeLang.setLang("cn");
        goodsAttributeLang.setName(descDto.getName());
        goodsAttributeLang.setType(0);
        getHibernateTemplate().save(goodsAttributeLang);
    }

    @Override
    public void saveAndUpdate(String attrId, String name, String lang, String categoryId, int sort) {

        Date now = new Date();
        GoodsAttribute goodsAttribute=null;
        if (StrUtil.isNotEmpty(attrId)){
            goodsAttribute= getHibernateTemplate().get(GoodsAttribute.class, attrId);
        }
        if (goodsAttribute == null) {
            goodsAttribute = new GoodsAttribute();
            goodsAttribute.setSort(sort);
            goodsAttribute.setCategoryId(categoryId);
            goodsAttribute.setCreateTime(now);
            getHibernateTemplate().save(goodsAttribute);
            GoodsAttributeLang goodsAttributeLang = new GoodsAttributeLang();
            goodsAttributeLang.setAttrId(goodsAttribute.getId().toString());
            goodsAttributeLang.setName(name);
            goodsAttributeLang.setType(0);
            goodsAttributeLang.setLang(lang);
            getHibernateTemplate().save(goodsAttributeLang);
        } else {
            goodsAttribute.setSort(sort);
            goodsAttribute.setCategoryId(categoryId);
            goodsAttribute.setCreateTime(now);
            getHibernateTemplate().update(goodsAttribute);
            DetachedCriteria queryAttrLang = DetachedCriteria.forClass(GoodsAttributeLang.class);
            queryAttrLang.add(Property.forName("attrId").eq(goodsAttribute.getId().toString()));
            queryAttrLang.add(Property.forName("type").eq(0));
            queryAttrLang.add(Property.forName("lang").eq(lang));
            List<GoodsAttributeLang> attributeLangs = (List<GoodsAttributeLang>) getHibernateTemplate().findByCriteria(queryAttrLang);
            GoodsAttributeLang goodsAttributeLang = null;
            if (!CollectionUtils.isEmpty(attributeLangs)) {
                goodsAttributeLang = attributeLangs.get(0);
            }
            if (goodsAttributeLang == null) {
                goodsAttributeLang = new GoodsAttributeLang();
                goodsAttributeLang.setAttrId(attrId);
                goodsAttributeLang.setName(name);
                goodsAttributeLang.setType(0);
                goodsAttributeLang.setLang(lang);
                getHibernateTemplate().save(goodsAttributeLang);
            } else {
                goodsAttributeLang.setName(name);
                getHibernateTemplate().update(goodsAttributeLang);
            }
        }
    }

    @Override
    @Transactional
    public String saveAttrValue(String attrId, String name, String lang) {

        GoodsAttribute goodsAttribute = getHibernateTemplate().get(GoodsAttribute.class, attrId);
        if (goodsAttribute == null) {
            throw new BusinessException("参数错误");
        }
        GoodsAttributeValue goodsAttributeValue = new GoodsAttributeValue();
        goodsAttributeValue.setCreateTime(new Date());
        goodsAttributeValue.setGoodAttributeId(attrId);
        getHibernateTemplate().save(goodsAttributeValue);
        GoodsAttributeValueLang goodsAttributeValueLang = new GoodsAttributeValueLang();
        goodsAttributeValueLang.setAttrValueId(goodsAttributeValue.getId().toString());
        goodsAttributeValueLang.setLang(lang);
        goodsAttributeValueLang.setName(name);
        goodsAttributeValueLang.setType(0);
        getHibernateTemplate().save(goodsAttributeValueLang);
        return goodsAttributeValue.getId().toString();
    }

    @Override
    public List<GoodAttrDto> findByCategoryId(String categoryId, String lang) {
        //1. 查询属性
        DetachedCriteria attrQuery = DetachedCriteria.forClass(GoodsAttribute.class);
        attrQuery.add(Property.forName("categoryId").eq(categoryId));
        attrQuery.addOrder(Order.asc("sort"));
        List<GoodsAttribute> goodsAttributes = (List<GoodsAttribute>) getHibernateTemplate().findByCriteria(attrQuery);
        List<String> attributeId = new ArrayList<>();
        for (GoodsAttribute attribute : goodsAttributes) {
            attributeId.add(attribute.getId().toString());
        }
        Map<String, GoodsAttributeLang> attributeLangMap = getGoodAttrLang(attributeId, lang);
        //获取参数
        DetachedCriteria query = DetachedCriteria.forClass(GoodsAttributeValue.class);
        query.add(Property.forName("goodAttributeId").in(attributeId));
        List<GoodsAttributeValue> goodsAttributeValues = (List<GoodsAttributeValue>) getHibernateTemplate().findByCriteria(query);
        List<String> attrValueIds = new ArrayList<>();
        for (GoodsAttributeValue attributeValue : goodsAttributeValues) {
            attrValueIds.add(attributeValue.getId().toString());
        }
        Map<String, GoodsAttributeValueLang> attributeValueLangMap = getAttrValueLang(attrValueIds, lang);
        List<GoodAttrDto> list = new ArrayList<>();
        for (GoodsAttribute goodsAttribute : goodsAttributes) {
            String attrId = goodsAttribute.getId().toString();
            GoodsAttributeLang goodsAttributeLang = attributeLangMap.get(attrId);
            if (goodsAttributeLang != null) {
                GoodAttrDto goodAttrDto = new GoodAttrDto();
                goodAttrDto.setAttrName(goodsAttributeLang.getName());
                goodAttrDto.setAttrId(goodsAttributeLang.getAttrId());
                goodAttrDto.setAttrValues(new ArrayList<>());
                for (GoodsAttributeValue attributeValue : goodsAttributeValues) {
                    String attrValueId = attributeValue.getId().toString();
                    GoodsAttributeValueLang attributeLang = attributeValueLangMap.get(attrValueId);
                    if (attributeLang != null && goodsAttributeLang.getAttrId().equals(attributeValue.getGoodAttributeId())) {
                        GoodAttrValueDto goodAttrValueDto = new GoodAttrValueDto();
                        goodAttrValueDto.setAttrValueId(attributeLang.getAttrValueId());
                        goodAttrValueDto.setAttrValueName(attributeLang.getName());
                        goodAttrDto.getAttrValues().add(goodAttrValueDto);
                    }
                }
                list.add(goodAttrDto);
            }
        }
        return list;
    }

    /**
     * 获取属性国际化
     *
     * @param attrIds
     * @param lang
     * @return
     */
    public Map<String, GoodsAttributeLang> getGoodAttrLang(List<String> attrIds, String lang) {

        DetachedCriteria queryAttrLang = DetachedCriteria.forClass(GoodsAttributeLang.class);
        queryAttrLang.add(Property.forName("attrId").in(attrIds));
        queryAttrLang.add(Property.forName("type").eq(0));
        queryAttrLang.add(Property.forName("lang").eq(lang));
        List<GoodsAttributeLang> attributeLangs = (List<GoodsAttributeLang>) getHibernateTemplate().findByCriteria(queryAttrLang);
        Map<String, GoodsAttributeLang> map = new HashMap<>();
        for (GoodsAttributeLang attributeLang : attributeLangs) {
            map.put(attributeLang.getAttrId(), attributeLang);
        }
        return map;
    }

    /**
     * 获取属性参数国际化
     *
     * @param attrValueIds
     * @param lang
     * @return
     */
    public Map<String, GoodsAttributeValueLang> getAttrValueLang(List<String> attrValueIds, String lang) {

        DetachedCriteria queryAttrLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
        queryAttrLang.add(Property.forName("attrValueId").in(attrValueIds));
        queryAttrLang.add(Property.forName("type").eq(0));
        queryAttrLang.add(Property.forName("lang").eq(lang));
        List<GoodsAttributeValueLang> attributeLangs = (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryAttrLang);
        Map<String, GoodsAttributeValueLang> map = new HashMap<>();
        for (GoodsAttributeValueLang attributeValueLang : attributeLangs) {
            map.put(attributeValueLang.getAttrValueId(), attributeValueLang);
        }
        return map;
    }

    @Override
    public void updateAttrById(GoodsAttributeDescDto descDto) {
//        Date now = new Date();
//        GoodsAttribute goodsAttribute = getHibernateTemplate().get(GoodsAttribute.class, descDto.getId());
//        goodsAttribute.setSort(descDto.getSort());
//        getHibernateTemplate().update(goodsAttribute);
//        DetachedCriteria queryAttrLang = DetachedCriteria.forClass(GoodsAttributeLang.class);
//        queryAttrLang.add(Property.forName("attrId").eq(descDto.getId()));
//        queryAttrLang.add(Property.forName("type").eq(0));
//        queryAttrLang.add(Property.forName("lang").eq(descDto.getLang()));
//        List<GoodsAttributeLang> goodsAttributeLangs = (List<GoodsAttributeLang>) getHibernateTemplate().findByCriteria(queryAttrLang);
//        GoodsAttributeLang goodsAttributeLang = new GoodsAttributeLang();
//        if (CollectionUtil.isEmpty(goodsAttributeLangs)) {
//            goodsAttributeLang = new GoodsAttributeLang();
//        } else {
//            goodsAttributeLang = goodsAttributeLangs.get(0);
//        }
//        goodsAttributeLang.setName(descDto.getName());
//        goodsAttributeLang.setAttrId(goodsAttribute.getId().toString());
//        goodsAttributeLang.setLang(descDto.getLang());
//        getHibernateTemplate().saveOrUpdate(goodsAttributeLang);
//        // deleteByAttrId(descDto.getId());
//        List<String> valueList = StrUtil.split(descDto.getValues(), ',');
//        List<String> valuesIdList = StrUtil.split(descDto.getValuesId(), ',');
//        int length = 0;
//        if (valueList.size() > valuesIdList.size()) {
//            length = valueList.size();
//        } else {
//            length = valuesIdList.size();
//        }
//        List<ValueIdAndValueDto> list=new ArrayList<>();
//        for (int i=0;i<length;i++) {
//            String valueId = null;
//            String value = null;
//            if (i < valueList.size()) {
//                value = valueList.get(i);
//            }
//            if (i < valuesIdList.size()) {
//                valueId = valueList.get(i);
//            }
//            ValueIdAndValueDto  valueIdAndValueDto=new ValueIdAndValueDto();
//            valueIdAndValueDto.setValue(value);
//            valueIdAndValueDto.setValueId(valueId);
//            list.add(valueIdAndValueDto);
//        }
//        for (ValueIdAndValueDto dto:list){
//            if (StrUtil.isNotEmpty(dto.getValueId())){
//                GoodsAttributeValue dbAttributeValue = new GoodsAttributeValue();
//                dbAttributeValue.setGoodAttributeId(goodsAttribute.getId().toString());
//                dbAttributeValue.setCreateTime(now);
//                getHibernateTemplate().save(dbAttributeValue);
//
//                GoodsAttributeValueLang goodsAttributeValueLang = new GoodsAttributeValueLang();
//                goodsAttributeValueLang.setAttrValueId(dbAttributeValue.getId().toString());
//                goodsAttributeValueLang.setLang(descDto.getLang());
//                goodsAttributeValueLang.setType(0);
//                goodsAttributeValueLang.setName(dto.getValue());
//                getHibernateTemplate().save(goodsAttributeValueLang);
//            }
//            else {
//                GoodsAttributeValue goodsAttributeValue = getHibernateTemplate().get(GoodsAttributeValue.class, dto.getValueId());
//                if (goodsAttributeValue==null){
//                    GoodsAttributeValue dbAttributeValue = new GoodsAttributeValue();
//                    dbAttributeValue.setGoodAttributeId(goodsAttribute.getId().toString());
//                    dbAttributeValue.setCreateTime(now);
//                    getHibernateTemplate().save(dbAttributeValue);
//                    GoodsAttributeValueLang goodsAttributeValueLang = new GoodsAttributeValueLang();
//                    goodsAttributeValueLang.setAttrValueId(dbAttributeValue.getId().toString());
//                    goodsAttributeValueLang.setLang(descDto.getLang());
//                    goodsAttributeValueLang.setType(0);
//                    goodsAttributeValueLang.setName(dto.getValue());
//                    getHibernateTemplate().save(goodsAttributeValueLang);
//                }
//                else {
//                    DetachedCriteria queryValueLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
//                    queryValueLang.add(Property.forName("attrValueId").eq(dto.getValueId()));
//                    queryValueLang.add(Property.forName("type").eq(0));
//                    queryValueLang.add(Property.forName("lang").eq(descDto.getLang()));
//                    List<GoodsAttributeValueLang> valueLangs = (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryValueLang);
//                    GoodsAttributeValueLang goodsAttributeValueLang=null;
//                    if (CollectionUtils.isEmpty(valueLangs)){
//                        goodsAttributeValueLang=new GoodsAttributeValueLang();
//                        goodsAttributeValueLang.setAttrValueId(goodsAttributeValue.getId().toString());
//                        goodsAttributeValueLang.setLang(descDto.getLang());
//                        goodsAttributeValueLang.setType(0);
//                    }
//                    goodsAttributeValueLang.setName(dto.getValue());
//                    getHibernateTemplate().saveOrUpdate(goodsAttributeValueLang);
//                }
//            }
//        }
//        Set<String> valuesIdSet=new HashSet<>();
//        for (int i = 0; i < valueList.size(); i++) {
//            String valueId = valuesIdList.get(i);
//            String value = valueList.get(i);
//            valuesIdSet.add(valueId);
//            GoodsAttributeValue goodsAttributeValue = getHibernateTemplate().get(GoodsAttributeValue.class, valueId);
//            if (goodsAttributeValue == null) { //新增
//                GoodsAttributeValue dbAttributeValue = new GoodsAttributeValue();
//                dbAttributeValue.setGoodAttributeId(goodsAttribute.getId().toString());
//                dbAttributeValue.setCreateTime(now);
//                getHibernateTemplate().save(dbAttributeValue);
//                GoodsAttributeValueLang goodsAttributeValueLang = new GoodsAttributeValueLang();
//                goodsAttributeValueLang.setAttrValueId(dbAttributeValue.getId().toString());
//                goodsAttributeValueLang.setLang(descDto.getLang());
//                goodsAttributeValueLang.setType(0);
//                goodsAttributeValueLang.setName(value);
//                getHibernateTemplate().save(goodsAttributeValueLang);
//            } else {
//                DetachedCriteria queryValueLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
//                queryValueLang.add(Property.forName("attrValueId").eq(valueId));
//                queryValueLang.add(Property.forName("type").eq(0));
//                queryValueLang.add(Property.forName("lang").eq(descDto.getLang()));
//                List<GoodsAttributeValueLang> valueLangs = (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryValueLang);
//                GoodsAttributeValueLang goodsAttributeValueLang=null;
//                if (CollectionUtils.isEmpty(valueLangs)){
//                    goodsAttributeValueLang=new GoodsAttributeValueLang();
//                    goodsAttributeValueLang.setAttrValueId(goodsAttributeValue.getId().toString());
//                    goodsAttributeValueLang.setLang(descDto.getLang());
//                    goodsAttributeValueLang.setType(0);
//                }
//                goodsAttributeValueLang.setName(value);
//                getHibernateTemplate().saveOrUpdate(goodsAttributeValueLang);
//            }
//        }
        //删除db多余数据
//        DetachedCriteria query = DetachedCriteria.forClass(GoodsAttributeValue.class);
//        query.add(Property.forName("goodAttributeId").eq(descDto.getId()));
//        List<GoodsAttributeValue> goodsAttributeValues = (List<GoodsAttributeValue>) getHibernateTemplate().findByCriteria(query);
//
//        for (GoodsAttributeValue goodsAttributeValue:goodsAttributeValues){
//                if (!valuesIdSet.contains(goodsAttributeValue.getId().toString())){
//
//                }
//        }
//        DetachedCriteria queryValueLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
//        queryValueLang.add(Property.forName("attrValueId").);
//        queryValueLang.add(Property.forName("type").eq(0));
//        queryValueLang.add(Property.forName("lang").eq(lang));
//        List<GoodsAttributeValueLang> valueLangs = (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryValueLang);
    }

    /**
     * 修改规则
     *
     * @param id
     */

    @Override
    public void updateById(String id, Map<String, GoodsAttributeDescDto> attributes, int sort) {

//        GoodsAttribute goodsAttribute = getHibernateTemplate().get(GoodsAttribute.class, id);
//        goodsAttribute.setSort(sort);
//        getHibernateTemplate().update(goodsAttribute);
//        DetachedCriteria queryAttrLang = DetachedCriteria.forClass(GoodsAttributeLang.class);
//        queryAttrLang.add(Property.forName("attrId").eq(id));
//        queryAttrLang.add(Property.forName("type").eq(0));
//        List<GoodsAttributeLang> goodsAttributeLangs = (List<GoodsAttributeLang>) getHibernateTemplate().findByCriteria(queryAttrLang);
//        Date now = new Date();
//        for (GoodsAttributeLang goodsAttributeLang : goodsAttributeLangs) {
//            String lang = goodsAttributeLang.getLang();
//            GoodsAttributeDescDto goodsAttributeDto = attributes.get(lang);
//            if (goodsAttributeDto != null) {
//                goodsAttributeLang.setName(goodsAttributeDto.getName());
//                getHibernateTemplate().update(goodsAttributeLang); //修改属性名称
//            }
//            deleteByAttrId(id);
//        }
//        String cLang = null;
//        for (String lang : attributes.keySet()) {
//            if (cLang == null) {
//                cLang = lang;
//            }
//        }
//        List<GoodsAttributeValue> goodsAttributeValues = new ArrayList<>();
//        for (String lang : attributes.keySet()) {
//            GoodsAttributeDescDto dto = attributes.get(lang);
//            //属性参数
//            List<String> valueList = StrUtil.split(dto.getValues(), ',');
//            if (CollectionUtils.isNotEmpty(valueList)) {
//                int i = 0;
//                for (String value : valueList) {
//                    //属性参数
//                    if (cLang.equals(lang)) {
//                        GoodsAttributeValue dbAttributeValue = new GoodsAttributeValue();
//                        dbAttributeValue.setGoodAttributeId(goodsAttribute.getId().toString());
//                        dbAttributeValue.setCreateTime(now);
//                        goodsAttributeValues.add(dbAttributeValue);
//                        getHibernateTemplate().save(dbAttributeValue);
//                    }
//                    //属性参数国际化
//                    if (i < goodsAttributeValues.size()) {
//                        GoodsAttributeValueLang goodsAttributeValueLang = new GoodsAttributeValueLang();
//                        goodsAttributeValueLang.setAttrValueId(goodsAttributeValues.get(i).getId().toString());
//                        goodsAttributeValueLang.setLang(lang);
//                        goodsAttributeValueLang.setType(0);
//                        goodsAttributeValueLang.setName(value);
//                        getHibernateTemplate().save(goodsAttributeValueLang);
//                    }
//                    i++;
//                }
//            }
//        }
    }

    /**
     * {
     * "cn": {
     * "categoryId": "402880f385f137160185f13b07310001",
     * "id": "402880f385f3b2ac0185f3b2fc560000",
     * "lang": "cn",
     * "name": "颜色",
     * "values": "白色,黑色,"
     * }
     * }
     *
     * @param id
     * @return
     */
    @Override
    public GoodsAttributeDescDto getById(String id, String lang) {
//        //查询属性
//        GoodsAttribute goodsAttribute = getHibernateTemplate().get(GoodsAttribute.class, id);
//        DetachedCriteria queryAttrLang = DetachedCriteria.forClass(GoodsAttributeLang.class);
//        queryAttrLang.add(Property.forName("attrId").eq(id));
//        queryAttrLang.add(Property.forName("type").eq(0));
//        queryAttrLang.add(Property.forName("lang").eq(lang));
//        List<GoodsAttributeLang> goodsAttributeLangs = (List<GoodsAttributeLang>) getHibernateTemplate().findByCriteria(queryAttrLang);
//        if (CollectionUtils.isEmpty(goodsAttributeLangs)) {
//            return null;
//        }
//        //查询属性参数数据
//        DetachedCriteria query = DetachedCriteria.forClass(GoodsAttributeValue.class);
//        query.add(Property.forName("goodAttributeId").eq(id));
//        List<GoodsAttributeValue> goodsAttributeValues = (List<GoodsAttributeValue>) getHibernateTemplate().findByCriteria(query);
//        List<String> valuesId = new ArrayList<>();
//        for (GoodsAttributeValue value : goodsAttributeValues) {
//            valuesId.add(value.getId().toString());
//        }
//        //查询属性参数数据国际化
//        DetachedCriteria queryValueLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
//        queryValueLang.add(Property.forName("attrValueId").in(valuesId));
//        queryValueLang.add(Property.forName("type").eq(0));
//        queryValueLang.add(Property.forName("lang").eq(lang));
//        List<GoodsAttributeValueLang> valueLangs = (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryValueLang);
//        //拼接返回数据
//        GoodsAttributeDescDto goodsAttributeDescDto = new GoodsAttributeDescDto();
//        goodsAttributeDescDto.setName(goodsAttributeLangs.get(0).getName());
//        goodsAttributeDescDto.setId(goodsAttributeLangs.get(0).getAttrId());
//        goodsAttributeDescDto.setLang(goodsAttributeLangs.get(0).getLang());
//        goodsAttributeDescDto.setSort(goodsAttribute.getSort());
//        goodsAttributeDescDto.setCategoryId(goodsAttribute.getCategoryId());
//        StringBuffer valuesSb = new StringBuffer("");
//        if (CollectionUtils.isNotEmpty(valueLangs)) {
//            for (GoodsAttributeValueLang attributeValueLang : valueLangs) {
//                valuesSb.append(attributeValueLang.getName() + ",");
//            }
//        }
//        if (valuesSb.length() > 0) {
//            goodsAttributeDescDto.setValues(valuesSb.substring(0, valuesSb.length() - 1));
//        }
        return null;
    }

    @Override
    public GoodsAttribute findGoodsAttributeById(String id) {

        return getHibernateTemplate().get(GoodsAttribute.class, id);
    }

    @Override
    public GoodsAttributeLang findAttributeLangById(String attributeId, String lang) {

        DetachedCriteria queryAttrLang = DetachedCriteria.forClass(GoodsAttributeLang.class);
        queryAttrLang.add(Property.forName("attrId").eq(attributeId));
        queryAttrLang.add(Property.forName("type").eq(0));
        queryAttrLang.add(Property.forName("lang").eq(lang));
        List<GoodsAttributeLang> goodsAttributeLangs = (List<GoodsAttributeLang>) getHibernateTemplate().findByCriteria(queryAttrLang);
        if (CollectionUtils.isEmpty(goodsAttributeLangs)) {
            return null;
        }
//        DetachedCriteria query = DetachedCriteria.forClass(GoodsAttributeValue.class);
//        query.add(Property.forName("goodAttributeId").eq(attributeId));
//        List<GoodsAttributeValue> goodsAttributeValues = (List<GoodsAttributeValue>) getHibernateTemplate().findByCriteria(query);
//        List<String> valuesId = new ArrayList<>();
//        for (GoodsAttributeValue value : goodsAttributeValues) {
//            valuesId.add(value.getId().toString());
//        }
//        //查询属性参数数据国际化
//        DetachedCriteria queryValueLang = DetachedCriteria.forClass(GoodsAttributeValueLang.class);
//        queryValueLang.add(Property.forName("attrValueId").in(valuesId));
//        queryValueLang.add(Property.forName("type").eq(0));
//        queryValueLang.add(Property.forName("lang").eq(lang));
//        List<GoodsAttributeValueLang> valueLangs = (List<GoodsAttributeValueLang>) getHibernateTemplate().findByCriteria(queryValueLang);
//        GoodsAttributeLangDto goodsAttributeLangDto = new GoodsAttributeLangDto();
//        goodsAttributeLangDto.setName(goodsAttributeLangs.get(0).getName());
//        goodsAttributeLangDto.setId(goodsAttributeLangs.get(0).getAttrId());
//        goodsAttributeLangDto.setLang(goodsAttributeLangs.get(0).getLang());
//        StringBuffer valuesSb = new StringBuffer("");
//        StringBuffer valuesIdSb = new StringBuffer("");
//        if (CollectionUtils.isNotEmpty(valueLangs)) {
//            for (GoodsAttributeValueLang attributeValueLang : valueLangs) {
//                valuesSb.append(attributeValueLang.getName() + ",");
//                valuesIdSb.append(attributeValueLang.getId().toString() + ",");
//            }
//        }
//        if (valuesSb.length() > 0) {
//            goodsAttributeLangDto.setValues(valuesSb.substring(0, valuesSb.length() - 1));
//            goodsAttributeLangDto.setValuesId(valuesIdSb.substring(0, valuesIdSb.length() - 1));
//        } else {
//            goodsAttributeLangDto.setValuesId(JSONObject.toJSONString(valuesId));
//        }
        return goodsAttributeLangs.get(0);
    }

    @Override
    public void removeById(String id) {
        //删除属性
        GoodsAttribute goodsAttribute = getHibernateTemplate().get(GoodsAttribute.class, id);
        if (goodsAttribute == null) {
            throw new BusinessException("参数错误");
        }
        getHibernateTemplate().delete(goodsAttribute);
        jdbcTemplate.execute("UPDATE T_MALL_GOODS_ATTRIBUTE_LANG SET TYPE =1 WHERE ATTR_ID ='" + id + "'");
        deleteByAttrId(id);
    }

    private void deleteByAttrId(String attrId) {
        //删除属性参数国际化
        jdbcTemplate.execute("UPDATE T_MALL_GOODS_ATTRIBUTE_VALUE_LANG SET TYPE =1 WHERE ATTR_VALUE_ID IN (SELECT  ID FROM T_MALL_GOODS_ATTRIBUTE_VALUE WHERE GOOD_ATTRIBUTE_ID='" + attrId + "' )");
        //删除属性参数
        jdbcTemplate.execute("DELETE FROM T_MALL_GOODS_ATTRIBUTE_VALUE WHERE GOOD_ATTRIBUTE_ID='" + attrId + "'");
    }

    @Override
    public int getCount(String categoryId) {

        StringBuffer countSql = new StringBuffer("select count(1)  FROM  T_MALL_GOODS_ATTRIBUTE where  CATEGORY_ID='" + categoryId + "'");
        int totalCnt = jdbcTemplate.queryForObject(countSql.toString(), Integer.class);
        return totalCnt;
    }

    /**
     * 获取列表数据
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<GoodsAttributeDto> list(int pageNum, int pageSize, String categoryId) {

        int start = (pageNum - 1) * pageSize;
        String sql = "SELECT ga.ID,ga.CATEGORY_ID,ga.SORT,gal.`NAME` AS 'attrName',gac.`NAME`  AS 'categoryName',gac.`ID`  AS 'categoryId'  FROM T_MALL_GOODS_ATTRIBUTE ga " +
                " LEFT JOIN T_MALL_GOODS_ATTRIBUTE_LANG gal ON ga.ID = gal.ATTR_ID " +
                " LEFT JOIN T_MALL_GOODS_ATTRIBUTE_CATEGORY gac ON gac.ID=ga.CATEGORY_ID " +
                "WHERE IFNULL(gal.LANG, 'en') = 'en' AND ga.CATEGORY_ID='" + categoryId + "'  ORDER BY sort  LIMIT " + start + "," + pageSize + "";
        List list = jdbcTemplate.queryForList(sql);
        Iterator it = list.iterator();
        List<GoodsAttributeDto> resultList = new ArrayList<>();
        List<String> attrIds = new ArrayList<>();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            GoodsAttributeDto dto = new GoodsAttributeDto();
            String id = rowMap.get("ID").toString();
            String attrName = rowMap.get("attrName").toString();
            if (rowMap.get("categoryName") != null) {
                String categoryName = rowMap.get("categoryName").toString();
                dto.setCategoryName(categoryName);
            }
            dto.setCategoryId(categoryId);
            String sort = rowMap.get("SORT").toString();
            dto.setSort(Integer.parseInt(sort));
            dto.setAttrName(attrName);
            dto.setId(id);
            resultList.add(dto);
            attrIds.add(id);
        }
        Map<String, List<String>> map = getAttrValueList(attrIds);
        if (map != null) {
            for (GoodsAttributeDto dto : resultList) {
                List<String> list1 = map.get(dto.getId());
                if (CollectionUtils.isNotEmpty(list1)) {
                    dto.setValueStr(StrUtil.join(",", list1));
                }
            }
        }
        return resultList;
    }

    /**
     * 获取属性参数值
     *
     * @param attrIds
     * @return
     */
    public Map<String, List<String>> getAttrValueList(List<String> attrIds) {

        if (CollectionUtil.isEmpty(attrIds)) return null;
        StringBuffer stringBuffer = new StringBuffer();
        for (String s : attrIds) {
            stringBuffer.append("'" + s + "',");
        }
        String attrIdStr = stringBuffer.substring(0, stringBuffer.length() - 1);
        String sql = "SELECT gav.GOOD_ATTRIBUTE_ID,gavl.`NAME`  FROM T_MALL_GOODS_ATTRIBUTE_VALUE gav " +
                "        LEFT JOIN T_MALL_GOODS_ATTRIBUTE_VALUE_LANG  gavl  ON gav.ID=gavl.ATTR_VALUE_ID " +
                "        WHERE gav.GOOD_ATTRIBUTE_ID IN(" + attrIdStr + ")  AND gavl.TYPE=0  AND IFNULL(gavl.LANG, 'cn')='cn' ";
        List list = jdbcTemplate.queryForList(sql);
        Map<String, List<String>> map = new HashMap<>();
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            String attrId = rowMap.get("GOOD_ATTRIBUTE_ID").toString();
            String name = rowMap.get("NAME").toString();
            List<String> valueList = map.get(attrId);
            if (valueList == null) {
                valueList = new ArrayList<>();
                map.put(attrId, valueList);
            }
            valueList.add(name);
        }
        return map;
    }

    public List<GoodsSku> findDbSku(String goodId) {

        DetachedCriteria skuQuery = DetachedCriteria.forClass(GoodsSku.class);
        skuQuery.add(Property.forName("goodId").eq(goodId));
        skuQuery.add(Property.forName("deleted").eq(0));
        return (List<GoodsSku>) getHibernateTemplate().findByCriteria(skuQuery);
    }

    public void clearSku(String goodId) {
        List<GoodsSku> list = findDbSku(goodId);
        if (CollectionUtils.isNotEmpty(list)) {
            for (GoodsSku sku : list) {
                sku.setDeleted(1);
                getHibernateTemplate().update(sku);
            }
        }
    }

    /**
     * 多个规则的Sku
     */
    @Override
    public void saveGenerateSku(String goodId, String categoryId) {
        clearSku(goodId);
        if (StringUtils.isNullOrEmpty(categoryId)) {
            return;
        }
        //1. 查询属性
        DetachedCriteria attrQuery = DetachedCriteria.forClass(GoodsAttribute.class);
        attrQuery.add(Property.forName("categoryId").eq(categoryId));
        attrQuery.addOrder(Order.asc("sort"));
        List<GoodsAttribute> goodsAttributes = (List<GoodsAttribute>) getHibernateTemplate().findByCriteria(attrQuery);
        if (0 == goodsAttributes.size()) {
            throw new BusinessException("所选属性未配置规则！");
        }
        List<String> attributeId = new ArrayList<>();
        for (GoodsAttribute attribute : goodsAttributes) {
            attributeId.add(attribute.getId().toString());
        }
        //2.查询属性参数数据
        DetachedCriteria query = DetachedCriteria.forClass(GoodsAttributeValue.class);
        query.add(Property.forName("goodAttributeId").in(attributeId));
        List<GoodsAttributeValue> goodsAttributeValues = (List<GoodsAttributeValue>) getHibernateTemplate().findByCriteria(query);
        //生成sku数据
        Map<String, List<String>> attrGroupMap = new HashMap<>();
        for (GoodsAttributeValue attributeValue : goodsAttributeValues) { //按属性id分组
            String attrId = attributeValue.getGoodAttributeId();
            List<String> valueList = attrGroupMap.get(attrId);
            if (valueList == null) {
                valueList = new ArrayList<>();
                attrGroupMap.put(attrId, valueList);
            }
            valueList.add(attributeValue.getGoodAttributeId() + "@" + attributeValue.getId().toString());
        }
        List<List<String>> skuList = new ArrayList<>();
        for (String attrId : attrGroupMap.keySet()) {
            skuList.add(attrGroupMap.get(attrId));
        }
        List<List<String>> handlerList = ArithmeticUtils.descartes(skuList);
//        //保存sku数据
        for (List<String> valueList : handlerList) {
            GoodsSku goodsSku = new GoodsSku();
            List<HashMap> list = new ArrayList<>();
            for (String s : valueList) {
                String[] idArrary = s.split("@");
                HashMap<String, String> idMap = new HashMap<>();
                idMap.put("attrId", idArrary[0]);
                idMap.put("attrValueId", idArrary[1]);
                list.add(idMap);
            }
            goodsSku.setSpData(JSONObject.toJSONString(list));
            goodsSku.setGoodId(goodId);
            getHibernateTemplate().save(goodsSku);
        }
    }

    public static void main(String[] args) {

        System.out.println(JSONObject.parseObject("{\"attrId\":\"2c909ecc86018f2f0186019004c40000\", " +
                "\"attrValueId\":\"2c909ecc86018f2f0186019004e20002\"}", HashMap.class)
        );
    }

}
