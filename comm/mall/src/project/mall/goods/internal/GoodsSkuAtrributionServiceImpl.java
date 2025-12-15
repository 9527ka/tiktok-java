package project.mall.goods.internal;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.google.common.collect.Maps;
import kernel.cache.LocalCachePool;
import kernel.constants.LocalCacheBucketKey;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.*;
import project.mall.goods.model.*;
import project.mall.goods.model.GoodsAttrsSkuVo;
import project.mall.goods.vo.SellerGoodsSkuInfoVO;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

public class GoodsSkuAtrributionServiceImpl extends HibernateDaoSupport implements GoodsSkuAtrributionService {
    private static Logger log = LoggerFactory.getLogger(GoodsSkuAtrributionServiceImpl.class);

    private JdbcTemplate jdbcTemplate;

    private SellerGoodsService sellerGoodsService;

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setSellerGoodsService(SellerGoodsService sellerGoodsService) {
        this.sellerGoodsService = sellerGoodsService;
    }

    @Override
    public List<GoodsAttributeVo> findCachedGoodsAttributeBySkuId(String skuId, String lang) {
        Cache<String, List<GoodsAttributeVo>> goodsAttrListBySkuCache = LocalCachePool.buildCache(LocalCacheBucketKey.GoodsAttrListBySkuCache, 1024, 300, null);
        String cacheKey = skuId + ":" + lang;

        List<GoodsAttributeVo> cacheValue = goodsAttrListBySkuCache.getIfPresent(cacheKey);
        if (cacheValue == null) {
            cacheValue = findGoodsAttributeBySkuId(skuId, lang);
            if (CollectionUtil.isNotEmpty(cacheValue)) {
                goodsAttrListBySkuCache.put(cacheKey, cacheValue);
            }
        } else {
            log.info("----> findCachedGoodsAttributeBySkuId 参数: skuId:" + skuId + ", lang:" + lang + " 命中缓存");
        }

        return cacheValue;
    }

    @Transactional(readOnly = true)
    @Override
    public List<GoodsAttributeVo> findGoodsAttributeBySkuId(String skuId, String lang) {
        GoodsSku goodsSku = getHibernateTemplate().get(GoodsSku.class, skuId);
        if (goodsSku == null) {
            log.error("findGoodsAttributeBySkuId===》》找不到商品库存信息");
            return Collections.emptyList();
        }
        String spData = goodsSku.getSpData();
        if (StrUtil.isEmpty(spData)) {
            log.error("findGoodsAttributeBySkuId===》》找不到商品属性信息");
            return Collections.emptyList();
        }
        if (!JSONUtil.isJson(spData)) {
            log.error("findGoodsAttributeBySkuId===》》商品属性信息格式错误");
            return Collections.emptyList();
        }
        JSONArray spDataJson = JSONUtil.parseArray(spData);
        List<GoodsAttributeVo> vos = new ArrayList<>();
        spDataJson.forEach(item -> {
            JSONObject obj = (JSONObject) item;
            Object attrNameId = obj.getByPath("attrId");
            Object attrValueId = obj.getByPath("attrValueId");
            if (attrNameId != null && attrValueId != null) {
                GoodsAttributeVo vo = getAttrVo(attrNameId.toString(), attrValueId.toString(), lang);
                Optional.ofNullable(vo).ifPresent(vos::add);
            }
        });

        return vos.stream().sorted(Comparator.comparing(GoodsAttributeVo::getSort)).collect(Collectors.toList());
    }

    private GoodsAttributeVo getAttrVo(String attrNameId, String attrValueId, String lang) {
        GoodsAttributeVo vo = new GoodsAttributeVo();
        GoodsAttribute goodsAttr = getHibernateTemplate().get(GoodsAttribute.class, attrNameId.toString());
        if (goodsAttr == null) {
            log.error("属性不存在！ " + attrNameId.toString());
            return null;
        }
        vo.setSort(goodsAttr.getSort());


        Session session = getSession();
        Criteria criteriaName = session.createCriteria(GoodsAttributeLang.class);
        criteriaName.add(Restrictions.eq("attrId", goodsAttr.getId()));
        criteriaName.add(Restrictions.eq("lang", lang));
        criteriaName.add(Restrictions.eq("type", 0));
        criteriaName.setMaxResults(1);
        List<GoodsAttributeLang> attrNames = criteriaName.list();
        if (attrNames != null && attrNames.size() == 1) {
            vo.setAttrName(attrNames.get(0).getName());
            vo.setAttrNameId(attrNames.get(0).getId().toString());
        }
        Criteria criteriaValue = getSession().createCriteria(GoodsAttributeValueLang.class);
        criteriaValue.add(Restrictions.eq("attrValueId", attrValueId));
        criteriaValue.add(Restrictions.eq("lang", lang));
//        criteriaValue.add(Restrictions.eq("type", 0));
        criteriaValue.setMaxResults(1);
        List<GoodsAttributeValueLang> attrValues = criteriaValue.list();
        if (attrNames != null && attrNames.size() == 1 && attrValues.size()>0) {
            vo.setAttrValue(attrValues.get(0).getName());
            vo.setAttrValueId(attrValues.get(0).getId().toString());
        }
        return vo;
    }

    private Session getSession() {
        Session session;
        try {
            session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        } catch (HibernateException e) {
            session = getHibernateTemplate().getSessionFactory().openSession();
        }
        return session;
    }

    /**
     * 慎用，容易有脏数据，该方法用于容忍一段时间脏数据的场景。
     *
     * @param goodId
     * @param lang
     * @return
     */
    public GoodSkuAttrDto getCachedGoodsAttrListSku(String goodId, String lang) {
        Cache<String, GoodSkuAttrDto> goodsSkuAttrListCache = LocalCachePool.buildCache(LocalCacheBucketKey.GoodsSkuAttrListCache, 1024, 300, null);
        GoodSkuAttrDto attrDto = goodsSkuAttrListCache.getIfPresent(goodId + ":" + lang);
        if (attrDto != null) {
            log.info("----> getCachedGoodsAttrListSku 参数: goodId:" + goodId + ", lang:" + lang + " 命中缓存");
            return attrDto;
        }

        attrDto = getGoodsAttrListSku(goodId, lang);
        if (attrDto != null) {
            goodsSkuAttrListCache.put(goodId + ":" + lang, attrDto);
        } else {
            log.warn("----> getCachedGoodsAttrListSku 参数: goodId:" + goodId + ", lang:" + lang + " 返回数据为 null");
        }

        return attrDto;
    }

    @Override
    public GoodSkuAttrDto getGoodsAttrListSku(String goodId, String lang) {
        //1 获取sku数据
        Criteria criteria = getSession().createCriteria(GoodsSku.class);
        criteria.add(Restrictions.eq("goodId", goodId));
        criteria.add(Restrictions.eq("deleted", 0));
        List<GoodsSku> goodsSkus = criteria.list();

        if (CollectionUtil.isEmpty(goodsSkus)) {
            return null;
        }

        //1. spdata解析数据
        List<String> attrIds = new ArrayList<>();//查询数据库属性Id
        List<String> attrValueIds = new ArrayList<>(); //查询数据库属性参数Id

        List<SkuDto> skuDtoList = new ArrayList<>(); //sku数据
        Map<String, List<String>> skuImg = Maps.newHashMap();
        for (GoodsSku goodsSku : goodsSkus) {
            List<SkuAttrDto> skuAttrDtoList =  com.alibaba.fastjson.JSONObject.parseArray(goodsSku.getSpData(), SkuAttrDto.class);
            SkuDto skuDto=new SkuDto();
            skuDto.setSkuId(goodsSku.getId().toString());
            if (goodsSku.getPrice()!=null) {
                skuDto.setPrice(goodsSku.getPrice().doubleValue());
            }
            //判断是不是空的集合对象
            if (CollectionUtils.isNotEmpty(skuAttrDtoList)) {
                skuAttrDtoList.forEach(s -> { //增加数据查询id
                    attrIds.add(s.getAttrId());
                    attrValueIds.add(s.getAttrValueId());
                });
            }
            skuDto.setAttrs(skuAttrDtoList); //spdata 对象属性
            skuDto.setCoverImg(goodsSku.getCoverImg());
            String pic = goodsSku.getPic();
            if (StringUtils.isNotEmpty(pic)){
                List<String> img = com.alibaba.fastjson.JSONObject.parseArray(pic, String.class);
                skuImg.put(goodsSku.getId().toString(), img);
                skuDto.setImg(img);
                skuDto.setHidden(false);
            }
            skuDtoList.add(skuDto);
        }

        Map<String, GoodsAttributeLang> goodsAttributeLangMap = getGoodAttrLang(attrIds, lang); //获取属性国际化name
        Map<String, GoodsAttributeValueLang> goodsAttributeValueLangMap = getAttrValueLang(attrValueIds, lang); //获取属性参数国际化name
        Map<String, GoodAttrDto> goodAttrDtoMap = new HashMap<>();
        for (SkuDto skuDto : skuDtoList) {
            if(CollectionUtils.isNotEmpty(skuDto.getAttrs())) {
                for (SkuAttrDto skuAttrDto : skuDto.getAttrs()) {
                    skuAttrDto.setAttrName(Optional.ofNullable(goodsAttributeLangMap.get(skuAttrDto.getAttrId())).orElseGet(GoodsAttributeLang::new).getName());
                    skuAttrDto.setAttrValueName(Optional.ofNullable(goodsAttributeValueLangMap.get(skuAttrDto.getAttrValueId())).orElseGet(GoodsAttributeValueLang::new).getName());
                    GoodAttrDto skuAttrsDto = goodAttrDtoMap.get(skuAttrDto.getAttrId());
                    if (skuAttrsDto == null) {
                        skuAttrsDto = new GoodAttrDto();
                        skuAttrsDto.setAttrValues(new ArrayList<>());
                        goodAttrDtoMap.put(skuAttrDto.getAttrId(), skuAttrsDto);
                    }
                    skuAttrsDto.setAttrId(skuAttrDto.getAttrId());
                    skuAttrsDto.setAttrName(skuAttrDto.getAttrName());
                    GoodAttrValueDto skuAttrValueDto = new GoodAttrValueDto();
                    skuAttrValueDto.setAttrValueId(skuAttrDto.getAttrValueId());
                    skuAttrValueDto.setAttrValueName(skuAttrDto.getAttrValueName());
                    if (skuAttrDto.isIcon()) {
                        skuAttrValueDto.setIcon(skuAttrDto.isIcon());
                        skuAttrValueDto.setIconImg(skuAttrDto.getIconImg());
                    }
                    skuAttrsDto.getAttrValues().add(skuAttrValueDto);
                }
            }
        }

        List<GoodAttrDto> goodAttrList = new ArrayList<>();
        for (String key : goodAttrDtoMap.keySet()) {
            List<GoodAttrValueDto> newList = new ArrayList<>();
            GoodAttrDto goodAttrDto = goodAttrDtoMap.get(key);
            Set<String> idSet = new HashSet<>();
            for (GoodAttrValueDto goodAttrValueDto : goodAttrDto.getAttrValues()) {
                if (idSet.add(goodAttrValueDto.getAttrValueId())) {
                    newList.add(goodAttrValueDto);
                }
            }
            goodAttrDto.setAttrValues(newList);
            goodAttrList.add(goodAttrDto);
        }
        GoodSkuAttrDto  skuAttrDto=new GoodSkuAttrDto();
        skuAttrDto.setGoodAttrs(goodAttrList);
        skuAttrDto.setSkus(skuDtoList);
        skuAttrDto.setSkuImg(skuImg);
        return skuAttrDto;
    }

    @Override
    public GoodSkuAttrDto getGoodsAttrListSkuBySellerGoods(SellerGoods sellerGoods, String lang) {
        if (sellerGoods == null || StringUtils.isEmptyString(sellerGoods.getGoodsId())) {
            return new GoodSkuAttrDto();
        }
        GoodSkuAttrDto goodsAttrListSku = getGoodsAttrListSku(sellerGoods.getGoodsId(), lang);
        if (goodsAttrListSku != null) {
            List<SkuDto> skus = goodsAttrListSku.getSkus();
            for (SkuDto skuDto : skus){
                // 取代 TODO
                SellerGoodsSkuInfoVO sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuDto.getSkuId());
                //SellerGoodsSku sellerGoodSku = sellerGoodsService.findSellerGoodSku(sellerGoods, skuDto.getSkuId());
                skuDto.setSellingPrice(sellerGoodSku.getSellingPrice());
                skuDto.setDiscountPrice(sellerGoodSku.getDiscountPrice());
            }
        }

        return goodsAttrListSku;
    }

    public GoodSkuAttrDto getCachedGoodsAttrListSkuBySellerGoods(SellerGoods sellerGoods, String lang) {
        if (sellerGoods == null || StringUtils.isEmptyString(sellerGoods.getGoodsId())) {
            return new GoodSkuAttrDto();
        }

        GoodSkuAttrDto goodsAttrListSku = getCachedGoodsAttrListSku(sellerGoods.getGoodsId(), lang);
        Date discountStartTime = sellerGoods.getDiscountStartTime();
        Date discountEndTime = sellerGoods.getDiscountEndTime();
        boolean hasDiscount = false;
        if (discountStartTime != null
                && discountEndTime != null
                && discountStartTime.compareTo(DateUtils.getDayStart(new Date())) < 1
                && discountEndTime.compareTo(DateUtils.getDayStart(new Date())) > -1) {
            hasDiscount = true;
        }
        if (goodsAttrListSku != null) {
            List<SkuDto> skus = goodsAttrListSku.getSkus();
            for (SkuDto skuDto : skus) {
                // 取代 TODO
                //SellerGoodsSku sellerGoodSku = sellerGoodsService.findCachedSellerGoodSku(sellerGoods, skuDto.getSkuId());
                SellerGoodsSkuInfoVO sellerGoodSku = sellerGoodsService.findCachedSellerGoodSku(sellerGoods, skuDto.getSkuId());
                skuDto.setSellingPrice(Objects.nonNull(sellerGoodSku.getSellingPrice())? BigDecimal.valueOf(sellerGoodSku.getSellingPrice()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue():null);
                skuDto.setDiscountPrice(hasDiscount && Objects.nonNull(sellerGoodSku.getDiscountPrice())?BigDecimal.valueOf(sellerGoodSku.getDiscountPrice()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue():null);
            }
        }

        return goodsAttrListSku;
    }

    @Override
    public String selectSkuCoverImg(String skuId) {
        String sql = "SELECT COVER_IMG FROM T_MALL_GOODS_SKU WHERE ID = '"+ skuId + "'";
        List<String> coverImg = this.jdbcTemplate.queryForList(sql, String.class);
        if (CollectionUtil.isNotEmpty(coverImg)){
            return coverImg.get(0);
        }
        return null;
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
     * 慎用，容易有脏数据，该方法用于容忍一段时间脏数据的场景。
     *
     * @param attrValueIds
     * @param lang
     * @return
     */
    public Map<String, GoodsAttributeValueLang> getCachedAttrValueLang(List<String> attrValueIds, String lang) {
        Map<String, GoodsAttributeValueLang> retMap = new HashMap<>();
        if (CollectionUtil.isEmpty(attrValueIds)) {
            return new HashMap<>();
        }

        Cache<String, GoodsAttributeValueLang> goodsAttrValueLangCache = LocalCachePool.buildCache(LocalCacheBucketKey.GoodsAttrValueLangCache, 1024, 300, null);
        List<String> notCachedIdList = new ArrayList<>();
        for (String oneAttrValueId : attrValueIds) {
            GoodsAttributeValueLang oneCachedLang = goodsAttrValueLangCache.getIfPresent(oneAttrValueId + ":" + lang);
            if (oneCachedLang == null) {
                notCachedIdList.add(oneAttrValueId);
                continue;
            }

            retMap.put(oneAttrValueId, oneCachedLang);
        }

        if (notCachedIdList.isEmpty()) {
            return retMap;
        }

        Map<String, GoodsAttributeValueLang> notCachedMap = getAttrValueLang(notCachedIdList, lang);
        retMap.putAll(notCachedMap);
        for (String oneAttrValueId : notCachedMap.keySet()) {
            goodsAttrValueLangCache.put(oneAttrValueId + ":" + lang, notCachedMap.get(oneAttrValueId));
        }

        return retMap;
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
    public Map<String, List<GoodsAttributeVo>> listGoodsAttributeBySkuIds(List<String> skuIds, String lang) {
        Map<String, List<GoodsAttributeVo>> attrMap = new HashMap<>();
        Criteria criteria = getSession().createCriteria(GoodsSku.class);
        criteria.add(Restrictions.in("id", skuIds));
        criteria.list().forEach(item -> {
            String spData = ((GoodsSku) item).getSpData();
            if (StrUtil.isNotEmpty(spData) && JSONUtil.isJson(spData)) {
                JSONArray spDataJson = JSONUtil.parseArray(spData);
                List<GoodsAttributeVo> vos = new ArrayList<>();
                spDataJson.forEach(x -> {
                    JSON obj = JSONUtil.parse(x);
                    Object attrNameId = obj.getByPath("attrId");
                    Object attrValueId = obj.getByPath("attrValueId");
                    if (attrNameId != null && attrValueId != null) {
                        GoodsAttributeVo vo = getAttrVo(attrNameId.toString(), attrValueId.toString(), lang);
                        Optional.ofNullable(vo).ifPresent(vos::add);
                    }
                });
                attrMap.put(((GoodsSku) item).getId().toString(), vos.stream().sorted(Comparator.comparing(GoodsAttributeVo::getSort)).collect(Collectors.toList()));
            }
        });
        return attrMap;
    }

    private GoodsAttrsSkuVo getGoodsAttrsSku(String systemGoodsId, String lang) {
        Criteria criteria = getSession().createCriteria(GoodsSku.class);
        criteria.add(Restrictions.eq("goodId", systemGoodsId));
        criteria.add(Restrictions.eq("deleted", 0));
        //商品所有sku记录
        List<GoodsSku> goodsSkus = criteria.list();
        List<GoodsAttrsValuesVo> attrsValuesVos = new ArrayList<>();
        Map<String, String> attrIdSkuMap = new HashMap<>();
        GoodsAttrsSkuVo skuVo = new GoodsAttrsSkuVo();
        Set<String> avaAttrIds = new HashSet<>();
        if (goodsSkus != null && goodsSkus.size() > 0) {

            //商品所有的属性及属性值集合
            List<GoodsAttributeVo> goodsSttrVos = new ArrayList<>();
            for (GoodsSku sku : goodsSkus) {
                //sku所有的属性及属性集合
                List<GoodsAttributeVo> skuSttrVos = new ArrayList<>();
                String spData = sku.getSpData();
                if (StrUtil.isNotEmpty(spData) && JSONUtil.isJson(spData)) {
                    JSONArray spDataJson = JSONUtil.parseArray(spData);
                    spDataJson.forEach(x -> {
                        JSON obj = JSONUtil.parse(x);
                        Object attrNameId = obj.getByPath("attrId");
                        avaAttrIds.add(attrNameId.toString());
                        Object attrValueId = obj.getByPath("attrValueId");
                        if (attrNameId != null && attrValueId != null) {
                            GoodsAttributeVo vo = getAttrVo(attrNameId.toString(), attrValueId.toString(), lang);
                            Optional.ofNullable(vo).ifPresent(skuSttrVos::add);
                            Optional.ofNullable(vo).ifPresent(goodsSttrVos::add);
                        }
                    });
                }
                if (skuSttrVos != null && skuSttrVos.size() > 0) {
                    //属性值id1_属性值id2_属性值id3
                    String valueIdStr = skuSttrVos.stream().sorted(new Comparator<GoodsAttributeVo>() {
                        @Override
                        public int compare(GoodsAttributeVo o1, GoodsAttributeVo o2) {
                            return o1.getSort().compareTo(o2.getSort());
                        }
                    }).map(GoodsAttributeVo::getAttrValueId).collect(Collectors.joining("_"));
                    attrIdSkuMap.put(valueIdStr, sku.getId().toString());
                }
            }
            Map<String, List<GoodsAttributeVo>> mapAttrNames = goodsSttrVos.stream().sorted(new Comparator<GoodsAttributeVo>() {
                @Override
                public int compare(GoodsAttributeVo o1, GoodsAttributeVo o2) {
                    return o1.getSort().compareTo(o2.getSort());
                }
            }).collect(Collectors.groupingBy(GoodsAttributeVo::getAttrNameId));
            for (Map.Entry<String, List<GoodsAttributeVo>> entry : mapAttrNames.entrySet()) {
                GoodsAttrsValuesVo valuesVo = new GoodsAttrsValuesVo();
                valuesVo.setAttrNameId(entry.getKey());
                ArrayList<GoodsAttributeVo> entryValues = entry.getValue().stream().collect(Collectors.collectingAndThen(Collectors.toCollection(
                        () -> new TreeSet<GoodsAttributeVo>(Comparator.comparing(GoodsAttributeVo::getAttrValueId))), ArrayList::new));
                valuesVo.setAttrValues(entryValues.stream().map(GoodsAttributeVo::getAttrValue).collect(Collectors.toList()));
                valuesVo.setAttrValueIds(entryValues.stream().map(GoodsAttributeVo::getAttrValueId).collect(Collectors.toList()));
                for (GoodsAttributeVo vo : entry.getValue()) {
                    if (StrUtil.isEmpty(valuesVo.getAttrName())) {
                        valuesVo.setAttrName(vo.getAttrName());
                        break;
                    }
                }
                attrsValuesVos.add(valuesVo);
            }
            skuVo.setAttrsIdSkuId(attrIdSkuMap);
            skuVo.setSystemGoodsId(systemGoodsId);
            skuVo.setAttrsValuesVos(attrsValuesVos);
        }
        List<GoodsAttrsValuesVo> collect = skuVo.getAttrsValuesVos().stream().filter(g -> avaAttrIds.contains(g.getAttrNameId())).collect(Collectors.toList());
        skuVo.setAttrsValuesVos(collect);
        return skuVo;
    }

    @Override
    public GoodsAttrsSkuVo findGoodsAttributeByGoodsId(String systemGoodsId, String lang) {
        return getGoodsAttrsSku(systemGoodsId, lang);
    }

    @Override
    public Map<String, GoodsAttrsSkuVo> findGoodsAttributeByGoodsIds(List<String> systemGoodsIds, String lang) {
        Map<String, GoodsAttrsSkuVo> goodsAttrsSkuMap = new HashMap<>();
        for (String goodsId : systemGoodsIds) {
            GoodsAttrsSkuVo vo = getGoodsAttrsSku(goodsId, lang);
            if (vo != null) {
                goodsAttrsSkuMap.put(goodsId, vo);
            }
        }
        return goodsAttrsSkuMap;
    }
}
