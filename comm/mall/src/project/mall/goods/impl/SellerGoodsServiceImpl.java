package project.mall.goods.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.github.benmanes.caffeine.cache.Cache;
import ext.Strings;
import kernel.cache.LocalCachePool;
import kernel.constants.LocalCacheBucketKey;
import kernel.util.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.mall.LanguageEnum;
import project.mall.MallRedisKeys;
import project.mall.event.message.SellerGoodsViewCountEvent;
import project.mall.event.model.SellerGoodsViewCountInfo;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.*;
import project.mall.goods.model.*;
import project.mall.goods.vo.*;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;
import project.party.PartyRedisKeys;
import project.redis.RedisHandler;
import project.web.api.SellerGoodsQuery;
import util.cache.CacheOperation;
import util.concurrent.gofun.core.FunParams;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaUpdate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class SellerGoodsServiceImpl extends HibernateDaoSupport implements SellerGoodsService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private JdbcTemplate jdbcTemplate;

    private RedisHandler redisHandler;


    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {

        this.jdbcTemplate = jdbcTemplate;
    }

    public void setRedisHandler(RedisHandler redisHandler) {

        this.redisHandler = redisHandler;
    }

    // 特殊用途：标记 topN 统计业务处理的最近时间
    public static final Map<String, Long> lastStatTopNSellerTimeMap = new HashMap<>();

    @Override
    public long getNumberOfVisitorsByDate(String sellerId, Date startTime, Date endTime) {

        long result = 0;
        String startStr = util.DateUtil.formatDate(util.DateUtil.minDate(startTime), util.DateUtil.DATE_FORMAT_FULL);
        String endStr = util.DateUtil.formatDate(util.DateUtil.maxDate(endTime), util.DateUtil.DATE_FORMAT_FULL);

        List<BigDecimal> list = jdbcTemplate.queryForList("SELECT IFNULL(sum(VIEWS_NUM + VIRTUAL_VIEWS_NUM), 0)  " +
                "FROM T_MALL_SELLER_GOODS_STATISTICS  WHERE  " +
                "SELLER_ID='" + sellerId + "' AND  CREATE_TIME  BETWEEN '" + startStr + "' AND '" + endStr + "'", BigDecimal.class);

        if (CollectionUtils.isNotEmpty(list)) {
            result = list.get(0).longValue();
        }
        return result;
    }

    @Override
    public List<SellerGoods> listRecommendAndNewGoods(int type, PageInfo pageInfo) {
        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
//        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
//        categoryQuery.add(Property.forName("type").eq(1));
//        categoryQuery.add(Property.forName("status").eq(1));
//        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
//        List<String> validCategoryIdList = validCategoryList.stream()
//                // 只保留二级分类id
//                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
//                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(validCategoryIdList)) {
//            // TODO
//            // return new ArrayList();
//        }
//

        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoods.class);
        // 分类可见，商品才可见
        // caster 分类过滤延迟生效
        // criteria.add(Restrictions.in("secondaryCategoryId", validCategoryIdList));
        if (type == 1) {
            // 推荐商品
            criteria.add(Restrictions.gt("systemRecTime", new Long(0)));
            criteria.addOrder(Order.desc("systemRecTime"));
        } else if (type == 0) {
            // 新商品
            criteria.add(Restrictions.gt("systemNewTime", new Long(0)));
            criteria.addOrder(Order.desc("systemNewTime"));
        } else if (type == 2) {
            // 热销商品
            criteria.add(Restrictions.gt("sellWellTime", new Long(0)));
            criteria.addOrder(Order.desc("sellWellTime"));
        }
        criteria.add(Restrictions.eq("isShelf", 1));
        criteria.add(Restrictions.eq("isValid", 1));
        criteria.addOrder(Order.asc("id"));
        return (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(criteria,
                pageInfo.getPageNum() - 1, pageInfo.getPageSize());
    }

    @Override
    public List<SellerGoods> listAutoSellerGoodsList(int type) {

        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoods.class);
        if (type == 1) {
            // 推荐商品
            criteria.add(Restrictions.gt("systemRecTime", new Long(0)));
        } else if (type == 2) {
            // 新商品
            criteria.add(Restrictions.gt("systemNewTime", new Long(0)));
        } else if (type == 3) {
            // 热销商品
            criteria.add(Restrictions.gt("sellWellTime", new Long(0)));
        }

        criteria.addOrder(Order.asc("id"));
        return (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(criteria, 0, 1000);
    }

    @Override
    public List<SellerGoods> listRecommendAndLikeGoods(String partyId, String sellerId, int type) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoods.class);
        if (type == 1) {
            // 猜你喜欢
            int pageNum = 1;
            int pageSize = 12;
            String sql = "SELECT " +
                    "msgb.SELLER_GOODS_ID " +
                    "FROM T_MALL_SELLER_GOODS_BROWS_HISTORY msgb  " +
                    "LEFT JOIN T_MALL_SELLER_GOODS msg ON msgb.SELLER_GOODS_ID = msg.UUID " +
                    "WHERE 1 = 1  " +
                    "AND msgb.PARTY_ID = ?  " +
                    "AND msg.IS_VALID = 1 AND msg.IS_SHELF = 1 " +
                    "ORDER BY msgb.CREATE_TIME DESC LIMIT ?, ?";
            List<SellerGoods> result = new ArrayList<>();
            List<String> sellerGoodsId = this.jdbcTemplate.queryForList(sql, String.class, new Object[]{partyId, pageNum - 1, pageNum * pageSize});
            if (CollectionUtil.isEmpty(sellerGoodsId)) {
                return result;
            }
            criteria.add(Restrictions.in("id", sellerGoodsId));
            criteria.add(Restrictions.eq("isShelf", 1));
            criteria.add(Restrictions.eq("isValid", 1));
            List<SellerGoods> sellerGoods = (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(criteria);
            result.addAll(sellerGoods);
            return result;
        } else {
            criteria.add(Restrictions.eq("isShelf", 1));
            criteria.add(Restrictions.eq("sellerId", sellerId));
            criteria.add(Restrictions.gt("recTime", new Long(0)));
            criteria.addOrder(Order.desc("upTime"));
            criteria.add(Restrictions.eq("isValid", 1));
            Criteria executableCriteria = criteria.getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession());
            executableCriteria.setMaxResults(10);
            return (List<SellerGoods>) executableCriteria.list();
        }
    }

    @Override
    public void insertBrowsHistory(String userId, String sellerGoodsId, String sellerId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoodsBrowsHistory.class);
        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.add(Restrictions.eq("sellerGoodsId", sellerGoodsId));
        criteria.add(Restrictions.eq("partyId", userId));
        List<SellerGoodsBrowsHistory> browsHistories = (List<SellerGoodsBrowsHistory>) this.getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtil.isNotEmpty(browsHistories)) {
            SellerGoodsBrowsHistory browsHistory = browsHistories.get(0);
            browsHistory.setTimes(browsHistory.getTimes() + 1);
            browsHistory.setCreateTime(new Date());
            this.getHibernateTemplate().update(browsHistory);
        } else {
            SellerGoodsBrowsHistory history = new SellerGoodsBrowsHistory(userId, sellerId, sellerGoodsId);
            this.getHibernateTemplate().save(history);
        }
    }


    @Override
    public MallPageInfo listGoodsSell(int pageNum, int pageSize, String sellerId, String categoryId, String secondaryCategoryId, Integer isNew,
                                      Integer rec, Integer isRec, Integer isHot, Integer isPrice, String lang, Integer discount) {
        // 是否采用综合排序来展示商品
        boolean isOrderByWeight = true;
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        if (null != discount && discount == 1) {
            query.add(Restrictions.gt("discountRatio", 0.0D));
            query.add(Restrictions.le("discountStartTime", new Date()));
            query.add(Restrictions.gt("discountEndTime", new Date()));
//            query.addOrder(Order.desc("upTime"));
        }
        if (sellerId != null) {
            query.add(Property.forName("sellerId").eq(sellerId));
        }
        if (StrUtil.isNotBlank(categoryId) && !Objects.equals(categoryId, "0")) {
            query.add(Property.forName("categoryId").eq(categoryId));
        }
        if (StrUtil.isNotBlank(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
            query.add(Property.forName("secondaryCategoryId").eq(secondaryCategoryId));
        }
        if (isPrice != null) {
            // 基于价格排序
            if (isPrice == 1) {
                isOrderByWeight = false;
                query.addOrder(Order.asc("sellingPrice"));
            } else if (isPrice == 2) {
                isOrderByWeight = false;
                query.addOrder(Order.desc("sellingPrice"));
            }
        }
        if (isNew != null) {
            // 基于上新排序
            /*if (sellerId != null) {//此情况说明是在店铺里面查询，此时isNew不是首页推荐，isNew只是表示只是判断商品是否按照最后保存时间正反序排序
                //此时不需要判断首页推荐标识，仅需排序即可
            } else {
                query.add(Restrictions.gt("newTime", new Long(0)));//非店铺里面查询时，首页情况需要判断上新标识
            }*/
            if (isNew == 1) {
                isOrderByWeight = false;
                query.addOrder(Order.asc("firstShelfTime"));
            } else if (isNew == 2) {
                isOrderByWeight = false;
                query.addOrder(Order.desc("firstShelfTime"));
            }
        }
        if (isRec != null && sellerId != null) {
            query.add(Restrictions.gt("recTime", new Long(0)));
            if (isRec == 1) {
                isOrderByWeight = false;
                query.addOrder(Order.asc("upTime"));
            } else if (isRec == 2) {
                isOrderByWeight = false;
                query.addOrder(Order.desc("upTime"));
            }
        }
        if (isHot != null) {
            // 基于销量排序
            if (isHot == 1) {
                isOrderByWeight = false;
                query.addOrder(Order.asc("soldNum"));
            } else if (isHot == 2) {
                isOrderByWeight = false;
                query.addOrder(Order.desc("soldNum"));
            }
//            query.add(Restrictions.gt("soldNum",new Integer(0)));
//            query.addOrder(Order.desc("soldNum"));
        }

        if (isOrderByWeight) {
            // 综合排序
            query.addOrder(Order.desc("showWeight1"));
            query.addOrder(Order.desc("showWeight2"));
        }

//        List<String> goodsIds = this.selectGoodsByLang(lang);
//        if (goodsIds.isEmpty()) {
//            if (lang.equals("en")) {
//                return new MallPageInfo();
//            } else {
//                goodsIds = this.selectGoodsByLang("en");
//                if (goodsIds.isEmpty()) {
//                    return new MallPageInfo();
//                }
//            }
//        }
        if (rec > 0) {
            query.add(Property.forName("recTime").gt(0L));
        }
//        query.add(Property.forName("goodsId").in(goodsIds));
        query.add(Property.forName("isShelf").eq(1));
        query.add(Property.forName("isValid").eq(1));
        query.addOrder(Order.desc("createTime"));
        query.addOrder(Order.desc("id"));
        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = new MallPageInfo();

        List result = getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
        mallPageInfo.setTotalElements(totalCount == null ? 0 : totalCount.intValue());
        mallPageInfo.setPageNum(pageNum);
        mallPageInfo.setPageSize(pageSize);
        mallPageInfo.setElements(result);

        return mallPageInfo;
    }

    private List<String> selectGoodsByLang(String lang) {
        List<String> goodsIds = jdbcTemplate.queryForList("SELECT DISTINCT " +
                "m.GOODS_ID  " +
                "FROM " +
                "T_MALL_SELLER_GOODS m " +
                "LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID  " +
                "WHERE " +
                "s.LANG = '" + lang + "'  " +
                "AND s.TYPE = 0 " +
                "AND m.IS_SHELF = 1 ", String.class);
        return goodsIds;
    }

    @Override
    public List<SellerGoods> getCategoryGoodList(int pageNum, int pageSize, String sellerId, String categoryId) {
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("isShelf").eq(0));
        query.add(Property.forName("categoryId").eq(categoryId));
        query.add(Property.forName("sellerId").eq(sellerId));
        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

    @Override
    public List<CategoryGoodCountDto> getCategoryGoodCount(String sellerId, String lang) {
        List list = jdbcTemplate.queryForList(
                "SELECT * FROM  (SELECT COUNT(*) as  'count', CATEGORY_ID   " +
                        "FROM T_MALL_SELLER_GOODS " +
                        "WHERE  SELLER_ID='" + sellerId + "' and IS_VALID = 1   " +
                        "GROUP BY CATEGORY_ID ) cc " +
                        "LEFT JOIN T_MALL_CATEGORY mc " +
                        "  ON mc.UUID=cc.CATEGORY_ID AND mc.STATUS = 1");
        Iterator it = list.iterator();
        List<CategoryGoodCountDto> resultList = new ArrayList<>();
        while (it.hasNext()) {
            Map rowrMap = (Map) it.next();
            if (rowrMap.get("CATEGORY_ID") != null) {
                int count = Integer.parseInt(rowrMap.getOrDefault("count", "0") + "");
                String categoryId = rowrMap.get("CATEGORY_ID").toString();
                Object icon_img = rowrMap.getOrDefault("ICON_IMG", "");
                String iconImg = null == icon_img ? "" : icon_img.toString();
                String key = MallRedisKeys.TYPE_LANG + lang + ":" + categoryId;
                String js = redisHandler.getString(key);
                if (StringUtils.isEmptyString(js)) {
                    continue;
                }
                CategoryLang pLang = JSONArray.parseObject(js, CategoryLang.class);
                CategoryGoodCountDto goodCountDto = new CategoryGoodCountDto();
                goodCountDto.setName(pLang.getName());
                goodCountDto.setCategoryId(pLang.getCategoryId());
                goodCountDto.setIconImg(iconImg);
                goodCountDto.setGoodCount(count);
                resultList.add(goodCountDto);
            }
        }
        return resultList;
    }

//    @Override
//    public MallPageInfo listGoodsSellAdmin(int pageNum, int pageSize, SellerGoodsQuery sellerGoodsQuery, Integer isNew, Integer isRec, Integer isHot, Integer isPrice) {
//        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
//        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
//        categoryQuery.add(Property.forName("type").eq(1));
//        categoryQuery.add(Property.forName("status").eq(1));
//        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
//        List<String> validCategoryIdList = validCategoryList.stream()
//                // 只保留二级分类id
//                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
//                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(validCategoryIdList)) {
//            // return new MallPageInfo();
//        }
//
//        String sellerId = sellerGoodsQuery.getSellerId();
//        String categoryId = sellerGoodsQuery.getCategoryId();
//        String secondaryCategoryId = sellerGoodsQuery.getSecondaryCategoryId();
//        String lang = sellerGoodsQuery.getLang();
//        List<String> sellerGoodsIdList = null;
//        if (StringUtils.isNotEmpty(sellerGoodsQuery.getName())) {
//            NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
//            Map<String, Object> params = new HashMap<>();
//            params.put("sellerId", sellerId);
//            if (StringUtils.isEmptyString(lang)) {
//                lang = "en";
//            }
//            params.put("lang", sellerGoodsQuery.getLang());
//            params.put("name", "%" + sellerGoodsQuery.getName() + "%");
//            String sql = "select distinct a.UUID as sellerGoodsId from T_MALL_SELLER_GOODS a left join T_MALL_SYSTEM_GOODS_LANG b on a.GOODS_ID = b.GOODS_ID where a.SELLER_ID = :sellerId and b.LANG = :lang and b.NAME like :name ";
//            sellerGoodsIdList = nameJdbc.queryForList(sql, params, String.class);
//            // 查询不到数据，填充id为-1，避免后续查询
//            if (CollectionUtil.isEmpty(sellerGoodsIdList)) {
//                sellerGoodsIdList.add("-1");
//            }
//        }
//
//        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
//        if (StrUtil.isNotBlank(sellerGoodsQuery.getGoodsId())) {
//            query.add(Property.forName("id").eq(sellerGoodsQuery.getGoodsId()));
//        }
//        if (CollectionUtil.isNotEmpty(sellerGoodsIdList)) {
//            query.add(Property.forName("id").in(sellerGoodsIdList));
//        }
//        String isShelf = sellerGoodsQuery.getIsShelf();
//        if (StringUtils.isNotEmpty(isShelf) && StringUtils.isNumeric(isShelf)) {
//            query.add(Property.forName("isShelf").eq(Integer.parseInt(isShelf)));
//        }
//        if (isPrice != null) {
//            query.addOrder(Order.desc("sellingPrices"));
//        }
//        if (StringUtils.isNotEmpty(sellerId)) {
//            query.add(Property.forName("sellerId").eq(sellerId));
//        }
//        if (StringUtils.isNotEmpty(categoryId) && !Objects.equals(categoryId, "0")) {
//            query.add(Property.forName("categoryId").eq(categoryId));
//        }
//        if (StringUtils.isNotEmpty(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
//            query.add(Property.forName("secondaryCategoryId").eq(secondaryCategoryId));
//        }
////        else {// caster 分类过滤延迟生效
////            query.add(Property.forName("secondaryCategoryId").in(validCategoryIdList));
//////            // not in 写法：
//////            query.add(
//////                Restrictions.not(
//////                    Restrictions.in("secondaryCategoryId", validCategoryIdList)
//////            ));
////        }
//
//        if (isNew != null && sellerId != null) {
//            query.add(Restrictions.gt("newTime", new Long(0)));
//            query.addOrder(Order.desc("newTime"));
//        }
//        if (isRec != null && sellerId != null) {
//            query.add(Restrictions.gt("recTime", new Long(0)));
//            query.addOrder(Order.desc("recTime"));
//        }
//        if (isNew != null && sellerId == null) {
//            query.add(Restrictions.gt("systemNewTime", new Long(0)));
//            query.addOrder(Order.desc("systemNewTime"));
//        }
//        if (isRec != null && sellerId == null) {
//            query.add(Restrictions.gt("systemRecTime", new Long(0)));
//            query.addOrder(Order.desc("systemRecTime"));
//        }
//        if (isHot != null) {
////            query.add(Restrictions.gt("soldNum",new Integer(0)));
//            query.addOrder(Order.desc("soldNum"));
//        }
//
//        // 影响查询性能，建议在业务层面为商品配置全多语言，不要通过这种方式做过滤
////        List<String> goodsIds = jdbcTemplate.queryForList("SELECT DISTINCT " +
////                "m.GOODS_ID  " +
////                "FROM " +
////                "T_MALL_SELLER_GOODS m " +
////                "LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID  " +
////                "WHERE " +
////                "s.LANG = '" + lang + "'  " +
////                "AND s.TYPE = 0 ", String.class);
////        if (goodsIds.isEmpty()) {
////            return new MallPageInfo();
////        }
//
////        query.add(Property.forName("goodsId").in(goodsIds));
//        query.add(Property.forName("isValid").eq(1));
////        query.add(Property.forName("isShelf").eq(1));
//        // 查询总条数
//        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
//        query.setProjection(null);
//        query.addOrder(Order.desc("upTime"));
//        query.addOrder(Order.desc("id"));
//
//        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));
//
//        return mallPageInfo;
//    }

    public MallPageInfo listGoodsSellAdmin(int pageNum, int pageSize, SellerGoodsQuery sellerGoodsQuery, Integer isNew, Integer isRec, Integer isHot, Integer isPrice) {
        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
        categoryQuery.add(Property.forName("type").eq(1));
        categoryQuery.add(Property.forName("status").eq(1));
        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
        List<String> validCategoryIdList = validCategoryList.stream()
                // 只保留二级分类id
                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(validCategoryIdList)) {
            // return new MallPageInfo();
        }

        String sellerId = sellerGoodsQuery.getSellerId();
        String categoryId = sellerGoodsQuery.getCategoryId();
        String secondaryCategoryId = sellerGoodsQuery.getSecondaryCategoryId();
        String lang = sellerGoodsQuery.getLang();

        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        List<String> sellerGoodsIdList = null;
        if (StringUtils.isNotEmpty(sellerGoodsQuery.getName())) {
            NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();

            params.put("name", "%" + sellerGoodsQuery.getName() + "%");
            String sql = "select a.UUID as sellerGoodsId from T_MALL_SELLER_GOODS a, T_MALL_SYSTEM_GOODS b " +
                    "where a.GOODS_ID = b.UUID ";
            if (StrUtil.isNotBlank(sellerId)) {
                sql = sql + " and a.SELLER_ID = :sellerId ";
                params.put("sellerId", sellerId);
            }
            if (StringUtils.isEmptyString(lang)) {
                sql = sql + "and (b.CN_TITLE like :name or b.EN_TITLE like :name or b.ZH_TITLE like :name) ";
            } else {
                if (lang.equals(LanguageEnum.CN.getLang())) {
                    sql = sql + "and b.CN_TITLE like :name ";
                } else if (lang.equals(LanguageEnum.EN.getLang())) {
                    sql = sql + "and b.EN_TITLE like :name ";
                } else if (lang.equals(LanguageEnum.TW.getLang())) {
                    sql = sql + "and b.ZH_TITLE like :name ";
                } else {
                    throw new RuntimeException("暂不支持");
                }
            }

            sellerGoodsIdList = nameJdbc.queryForList(sql, params, String.class);
            // 查询不到数据，填充id为-1，避免后续查询
            if (CollectionUtil.isEmpty(sellerGoodsIdList)) {
                sellerGoodsIdList.add("-1");
            }
        }

        if (StrUtil.isNotBlank(sellerGoodsQuery.getGoodsId())) {
            query.add(Property.forName("id").eq(sellerGoodsQuery.getGoodsId()));
        }
        if (CollectionUtil.isNotEmpty(sellerGoodsIdList)) {
            query.add(Property.forName("id").in(sellerGoodsIdList));
        }
        String isShelf = sellerGoodsQuery.getIsShelf();
        if (StringUtils.isNotEmpty(isShelf) && StringUtils.isNumeric(isShelf)) {
            query.add(Property.forName("isShelf").eq(Integer.parseInt(isShelf)));
        }
        if (isPrice != null) {
            query.addOrder(Order.desc("sellingPrices"));
        }
        if (StringUtils.isNotEmpty(sellerId)) {
            query.add(Property.forName("sellerId").eq(sellerId));
        }
        if (StringUtils.isNotEmpty(categoryId) && !Objects.equals(categoryId, "0")) {
            query.add(Property.forName("categoryId").eq(categoryId));
        }
        if (StringUtils.isNotEmpty(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
            query.add(Property.forName("secondaryCategoryId").eq(secondaryCategoryId));
        }
//        else {// caster 分类过滤延迟生效
//            query.add(Property.forName("secondaryCategoryId").in(validCategoryIdList));
////            // not in 写法：
////            query.add(
////                Restrictions.not(
////                    Restrictions.in("secondaryCategoryId", validCategoryIdList)
////            ));
//        }

        if (isNew != null && sellerId != null) {
            query.add(Restrictions.gt("newTime", new Long(0)));
            query.addOrder(Order.desc("newTime"));
        }
        if (isRec != null && sellerId != null) {
            query.add(Restrictions.gt("recTime", new Long(0)));
            query.addOrder(Order.desc("recTime"));
        }
        if (isNew != null && sellerId == null) {
            query.add(Restrictions.gt("systemNewTime", new Long(0)));
            query.addOrder(Order.desc("systemNewTime"));
        }
        if (isRec != null && sellerId == null) {
            query.add(Restrictions.gt("systemRecTime", new Long(0)));
            query.addOrder(Order.desc("systemRecTime"));
        }
        if (isHot != null) {
//            query.add(Restrictions.gt("soldNum",new Integer(0)));
            query.addOrder(Order.desc("soldNum"));
        }

        // 影响查询性能，建议在业务层面为商品配置全多语言，不要通过这种方式做过滤
//        List<String> goodsIds = jdbcTemplate.queryForList("SELECT DISTINCT " +
//                "m.GOODS_ID  " +
//                "FROM " +
//                "T_MALL_SELLER_GOODS m " +
//                "LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID  " +
//                "WHERE " +
//                "s.LANG = '" + lang + "'  " +
//                "AND s.TYPE = 0 ", String.class);
//        if (goodsIds.isEmpty()) {
//            return new MallPageInfo();
//        }

//        query.add(Property.forName("goodsId").in(goodsIds));
        query.add(Property.forName("isValid").eq(1));
//        query.add(Property.forName("isShelf").eq(1));
        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);
        query.addOrder(Order.desc("upTime"));
        query.addOrder(Order.desc("id"));

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }

    @Override
//    public MallPageInfo listSystemGoods(int pageNum, int pageSize, String categoryId, String secondaryCategoryId, String lang, String sellerId, String goodsName, String id) {
//        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
//        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
//        categoryQuery.add(Property.forName("type").eq(1));
//        categoryQuery.add(Property.forName("status").eq(1));
//        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
//        List<String> validCategoryIdList = validCategoryList.stream()
//                // 只保留二级分类id
//                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
//                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
//        if (CollectionUtil.isEmpty(validCategoryIdList)) {
//            // return new MallPageInfo();
//        }
//
//        if (StringUtils.isEmptyString(lang)) {
//            lang = "en";
//        }
//        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
//        List<String> goodsIdList = null;
//        if (StringUtils.isNotEmpty(goodsName)) {
//            Map<String, Object> params = new HashMap<>();
//
//            params.put("lang", lang);
//            params.put("goodsName", "%" + goodsName + "%");
//            String sql = "select a.UUID as goodsId from T_MALL_SYSTEM_GOODS a  left join T_MALL_SYSTEM_GOODS_LANG b on a.uuid = b.GOODS_ID where b.lang = :lang  and b.name like :goodsName";
//            goodsIdList = nameJdbc.queryForList(sql, params, String.class);
//            // 查询不到数据，填充id为-1，避免后续查询
//            if (CollectionUtil.isEmpty(goodsIdList)) {
//                goodsIdList.add("-1");
//            }
//        }
//
//        List<String> goodsLang = jdbcTemplate.queryForList("SELECT DISTINCT " +
//                "m.UUID  " +
//                "FROM " +
//                "T_MALL_SYSTEM_GOODS m " +
//                "LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.UUID = s.GOODS_ID  " +
//                "WHERE " +
//                "s.LANG = '" + lang + "'  " +
//                "AND s.TYPE = 0 and m.IS_SHELF = 1", String.class);
//
//
//        if (CollectionUtil.isEmpty(goodsLang)) {
//            return new MallPageInfo(pageSize, pageSize, 0, null);
//        }
//
//        DetachedCriteria query = DetachedCriteria.forClass(SystemGoods.class);
//        if (StringUtils.isNotEmpty(id)) {
//            query.add(Property.forName("id").eq(id));
//        }
//        if (StringUtils.isNotEmpty(categoryId) && !Objects.equals(categoryId, "0")) {
//            query.add(Property.forName("categoryId").eq(categoryId));
//        }
//        if (StringUtils.isNotEmpty(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
//            query.add(Property.forName("secondaryCategoryId").eq(secondaryCategoryId));
//        }
////        else {// caster 分类过滤延迟生效
////            query.add(Property.forName("secondaryCategoryId").in(validCategoryIdList));
////        }
//
//        if (CollectionUtil.isNotEmpty(goodsLang)) {
//            query.add(Property.forName("id").in(goodsLang));
//        }
//        if (CollectionUtil.isNotEmpty(goodsIdList)) {
//            query.add(Property.forName("id").in(goodsIdList));
//        }
//
//        DetachedCriteria sellerGoodsQuery = DetachedCriteria.forClass(SellerGoods.class);
//        sellerGoodsQuery.setProjection(Projections.property("goodsId"));
//        sellerGoodsQuery.add(Property.forName("sellerId").eq(sellerId));
//        sellerGoodsQuery.add(Property.forName("isValid").eq(1));
//        query.add(Property.forName("id").notIn(sellerGoodsQuery));
//
//        // 查询总条数
//        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
//        query.setProjection(null);
//        query.addOrder(Order.desc("upTime"));
//        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));
//
//        return mallPageInfo;
//    }

    public MallPageInfo listSystemGoods(int pageNum, int pageSize, String categoryId, String secondaryCategoryId, String lang, String sellerId, String goodsName, String id) {
        // 添加分类过滤条件，被禁用/删除的分类下的商品不展示
        DetachedCriteria categoryQuery = DetachedCriteria.forClass(Category.class);
        categoryQuery.add(Property.forName("type").eq(1));
        categoryQuery.add(Property.forName("status").eq(1));
        List<Category> validCategoryList = (List<Category>) getHibernateTemplate().findByCriteria(categoryQuery);
        List<String> validCategoryIdList = validCategoryList.stream()
                // 只保留二级分类id
                .filter(entity -> StrUtil.isNotBlank(entity.getParentId()) && !Objects.equals(entity.getParentId(), "0"))
                .map(entity -> entity.getId().toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(validCategoryIdList)) {
            // return new MallPageInfo();
        }

        DetachedCriteria query = DetachedCriteria.forClass(SystemGoods.class);
        query.add(Property.forName("isShelf").eq(1));
        if (StringUtils.isNotEmpty(goodsName)) {
            if (StringUtils.isEmptyString(lang)) {
                query.add(Restrictions.or(
                        Restrictions.like("cnTitle", goodsName, MatchMode.ANYWHERE),
                        Restrictions.like("enTitle", goodsName, MatchMode.ANYWHERE),
                        Restrictions.like("zhTitle", goodsName, MatchMode.ANYWHERE)));
            } else {
                if (lang.equals(LanguageEnum.CN.getLang())) {
                    query.add(Restrictions.like("cnTitle", goodsName, MatchMode.ANYWHERE));
                } else if (lang.equals(LanguageEnum.EN.getLang())) {
                    query.add(Restrictions.like("enTitle", goodsName, MatchMode.ANYWHERE));
                } else if (lang.equals(LanguageEnum.TW.getLang())) {
                    query.add(Restrictions.like("zhTitle", goodsName, MatchMode.ANYWHERE));
                } else {
                    log.warn("---> SellerGoodsServiceImpl.listSystemGoods 暂不支持该语种:{}", lang);
                }
            }
        } else {
            query.add(Property.forName("multiLangs").isNotNull());
        }

        if (StringUtils.isNotEmpty(id)) {
            query.add(Property.forName("id").eq(id));
        }
        if (StringUtils.isNotEmpty(categoryId) && !Objects.equals(categoryId, "0")) {
            query.add(Property.forName("categoryId").eq(categoryId));
        }
        if (StringUtils.isNotEmpty(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
            query.add(Property.forName("secondaryCategoryId").eq(secondaryCategoryId));
        }
//        else {// caster 分类过滤延迟生效
//            query.add(Property.forName("secondaryCategoryId").in(validCategoryIdList));
//        }

        DetachedCriteria sellerGoodsQuery = DetachedCriteria.forClass(SellerGoods.class);
        sellerGoodsQuery.setProjection(Projections.property("goodsId"));
        sellerGoodsQuery.add(Property.forName("sellerId").eq(sellerId));
        sellerGoodsQuery.add(Property.forName("isValid").eq(1));
        query.add(Property.forName("id").notIn(sellerGoodsQuery));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);
        query.addOrder(Order.desc("upTime"));
        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }

    @Override
    public List<SystemGoods> querySearchKeyword(String lang, String keyword, int isHot, int isNew, int isPrice) {
        keyword = keyword.trim();

        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT distinct a.GOODS_ID as goodsId ");
        sqlStr.append(" FROM T_MALL_SELLER_GOODS as a, T_MALL_SYSTEM_GOODS as b");
        sqlStr.append(" where a.GOODS_ID = b.UUID ");
        sqlStr.append(" AND a.IS_SHELF = 1 ");
        sqlStr.append(" AND b.IS_SHELF = 1 ");
        sqlStr.append(" AND a.is_VALID = 1 ");
        if (StrUtil.isBlank(lang)) {
            sqlStr.append(" AND (b.CN_TITLE LIKE :keyword or b.EN_TITLE LIKE :keyword or b.ZH_TITLE LIKE :keyword) ");
        } else if (lang.equals(LanguageEnum.CN.getLang())) {
            sqlStr.append(" AND b.CN_TITLE LIKE :keyword ");
        } else if (lang.equals(LanguageEnum.EN.getLang())) {
            sqlStr.append(" AND b.EN_TITLE LIKE :keyword ");
        } else if (lang.equals(LanguageEnum.TW.getLang())) {
            sqlStr.append(" AND b.ZH_TITLE LIKE :keyword ");
        } else {
            log.warn("---> SellerGoodsServiceImpl.querySearchKeyword 暂不支持该语种:{}", lang);
        }

        String orderBy = "";
        if (isPrice > 0) {
            if (isPrice == 1) {
                orderBy = orderBy + "order by a.SELLING_PRICE DESC,";
            }
            if (isPrice == 2) {
                orderBy = orderBy + "order by a.SELLING_PRICE ASC,";
            }
        }
        if (isNew > 0) {
            if (isNew == 1) {
                orderBy = orderBy + "order by a.NEW_TIME DESC,";
            }
            if (isNew == 2) {
                orderBy = orderBy + "order by a.NEW_TIME asc,";
            }
        }
        if (isHot > 0) {
            if (isHot == 1) {
                orderBy = orderBy + "order by a.SOLD_NUM DESC,";
            }
            if (isHot == 2) {
                orderBy = orderBy + "order by a.SOLD_NUM asc,";
            }
        }
        if (orderBy.endsWith(",")) {
            orderBy = orderBy.substring(0, orderBy.length() - 1);
        }

        String sql = sqlStr.toString() + " " + orderBy;
        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("keyword", "%" + keyword + "%");

        List<String> systemGoodsIds = nameJdbc.queryForList(sql, params, String.class);
        if (CollectionUtil.isEmpty(systemGoodsIds)) {
            return new ArrayList<>();
        }

        DetachedCriteria query = DetachedCriteria.forClass(SystemGoods.class);
        query.add(Property.forName("id").in(systemGoodsIds));

        return (List<SystemGoods>) getHibernateTemplate().findByCriteria(query);
    }

    @Override
    public List<SystemGoods> queryAdminSearchGoods(int pageNum, int pageSize, String keywords, String lang) {
        List<String> goodsIds = jdbcTemplate.queryForList("SELECT DISTINCT " +
                "m.UUID  " +
                "FROM " +
                "T_MALL_SYSTEM_GOODS m " +
                "LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.UUID = s.GOODS_ID  " +
                "WHERE " +
                "s.LANG = '" + lang + "'  " +
                "AND s.NAME LIKE '%" + keywords + "%'  ", String.class);
        if (goodsIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 这个有问题吧？ 到底是要查询 SellerGoods 还是要查询 SystemGoods ？ TODO
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("id").in(goodsIds));
//        query.add(Property.forName("sellerId").in(sellerId));
        return (List<SystemGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

//    @Override
//    public List<SellerGoods> querySearchsellerGoods(int pageNum, int pageSize, String sellerId, String keywords, String lang) {
//        StringBuffer sqlStr = new StringBuffer();
//        sqlStr.append(" SELECT DISTINCT  ")
//                .append(" m.GOODS_ID   ")
//                .append(" FROM  ")
//                .append(" T_MALL_SELLER_GOODS m ")
//                .append(" LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID  ")
//                .append(" WHERE ")
//                .append(" s.LANG = ? ")
//                .append(" AND m.IS_SHELF = 1 ")
//                .append(" AND m.is_VALID = 1 ")
//                .append(" AND s.NAME LIKE ?")
//                .append(" AND m.SELLER_ID = ? ")
//        ;
//        List<Object> params = new ArrayList<>();
//        params.add(lang);
//        params.add("%" + keywords + "%");
//        params.add(sellerId);
//        List<String> goodsIds = jdbcTemplate.queryForList(sqlStr.toString(), String.class, params.toArray());
//
//        if (goodsIds.isEmpty()) {
//            return new ArrayList<>();
//        }
//        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
//        query.add(Property.forName("goodsId").in(goodsIds));
//        query.add(Property.forName("sellerId").in(sellerId));
//        query.add(Property.forName("isShelf").eq(1));
//        query.add(Property.forName("isValid").eq(1));
//        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
//    }

    public List<SellerGoods> querySearchsellerGoods(int pageNum, int pageSize, String sellerId, String keywords, String lang) {
        if (pageNum <= 0) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 1;
        }

        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT a.UUID as id, a.GOODS_ID as goodsId, a.SELLER_ID as sellerId, ");
        sqlStr.append(" a.CATEGORY_ID as categoryId, a.SECONDARY_CATEGORY_ID as secondaryCategoryId, a.SOLD_NUM as soldNum, ");
        sqlStr.append(" a.VIEWS_NUM as viewsNum, a.SYSTEM_PRICE as systemPrice, a.SELLING_PRICE as sellingPrice, ");
        sqlStr.append(" a.PROFIT_RATIO as profitRatio, a.DISCOUNT_PRICE as discountPrice, a.ORDER_PRICE as orderPrice, ");
        sqlStr.append(" a.DISCOUNT_RATIO as discountRatio, a.UP_TIME as upTime, a.FIRST_SHELF_TIME as firstShelfTime, ");
        sqlStr.append(" a.CREATE_TIME as createTime, a.REC_TIME as recTime, a.NEW_TIME as newTime, ");
        sqlStr.append(" a.IS_SHELF as isShelf, a.SYSTEM_REC_TIME as systemRecTime, a.SYSTEM_NEW_TIME as systemNewTime, ");
        sqlStr.append(" a.STOP_TIME as stopTime, a.BUY_MIN as buyMin, a.DISCOUNT_START_TIME as discountStartTime, ");
        sqlStr.append(" a.DISCOUNT_END_TIME as discountEndTime, a.IS_COMBO as isCombo, a.IS_VALID as isValid, ");
        sqlStr.append(" a.SELL_WELL_TIME as sellWellTime, a.SHOW_WEIGHT1 as showWeight1, a.SHOW_WEIGHT2 as showWeight2, ");
        sqlStr.append(" a.SYS_NEW_DURATION as sysNewDuration, a.SYS_REC_DURATION as sysRecDuration, a.SELL_WELL_DURATION as sellWellDuration ");
        sqlStr.append(" FROM T_MALL_SELLER_GOODS as a, T_MALL_SYSTEM_GOODS as b");
        sqlStr.append(" where a.GOODS_ID = b.UUID ");
        sqlStr.append(" AND a.IS_SHELF = 1 ");
        sqlStr.append(" AND a.is_VALID = 1 ");
        sqlStr.append(" AND (b.CN_TITLE LIKE ? or b.EN_TITLE LIKE ? or b.ZH_TITLE LIKE ?) ");
        sqlStr.append(" AND a.SELLER_ID = ? ");

        sqlStr.append(" limit ").append((pageNum - 1) * pageSize).append(",").append(pageSize);
        List<Object> params = new ArrayList<>();
        params.add("%" + keywords + "%");
        params.add("%" + keywords + "%");
        params.add("%" + keywords + "%");
        params.add(sellerId);

        List<SellerGoods> sellerGoodsList = new ArrayList<>();
        List<Map<String, Object>> results = jdbcTemplate.queryForList(sqlStr.toString(), params.toArray());

        if (results.isEmpty()) {
            return sellerGoodsList;
        }

        Set<String> systemGoodsIds = new HashSet<>();
        for (Map<String, Object> oneRow : results) {
            String rowJson = JacksonUtils.objectToJson(oneRow);
            SellerGoods oneSellerGoods = JacksonUtils.jsonToObject(rowJson, SellerGoods.class);
            sellerGoodsList.add(oneSellerGoods);

            systemGoodsIds.add(oneSellerGoods.getGoodsId());
        }
        // 补上 SystemGoods 字段值
        if (!systemGoodsIds.isEmpty()) {
            List<SystemGoods> systemGoodsList = this.getSystemGoodsBatch(new ArrayList<>(systemGoodsIds));
            Map<String, SystemGoods> systemGoodsMap = new HashMap<>();
            for (SystemGoods oneSystemGoods : systemGoodsList) {
                systemGoodsMap.put(oneSystemGoods.getId(), oneSystemGoods);
            }

            for (SellerGoods oneSellerGoods : sellerGoodsList) {
                oneSellerGoods.setSystemGoods(systemGoodsMap.get(oneSellerGoods.getGoodsId()));
            }
        }

        return sellerGoodsList;
    }

//    @Override
//    public List<SellerGoods> querySearchsellerGoods(int pageNum, int pageSize, String keywords, String lang, Integer isNew,
//                                                    Integer isRec, Integer isHot, Integer isPrice, Integer is_discount) {
//        StringBuffer sqlStr = new StringBuffer();
//        sqlStr.append(" SELECT DISTINCT  ")
//                .append(" m.GOODS_ID   ")
//                .append(" FROM  ")
//                .append(" T_MALL_SELLER_GOODS m ")
//                .append(" LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID  ")
//                .append(" WHERE ")
//                .append(" s.LANG = ? ")
//                .append(" AND m.IS_SHELF = 1 ")
//                .append(" AND m.is_VALID = 1 ")
//                .append(" AND s.NAME LIKE ?")
//        ;
//        List<Object> params = new ArrayList<>();
//        params.add(lang);
//        params.add("%" + keywords + "%");
//        List<String> goodsIds = jdbcTemplate.queryForList(sqlStr.toString(), String.class, params.toArray());
//        if (goodsIds.isEmpty()) {
//            return new ArrayList<>();
//        }
//        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
//        query.add(Property.forName("goodsId").in(goodsIds));
//        if (is_discount != null && is_discount == 1) {
//            query.add(Restrictions.gt("discountRatio", 0.0D));
//            query.add(Restrictions.gt("discountStartTime", new Date()));
//            query.add(Restrictions.lt("discountEndTime", new Date()));
//        }
//        if (isPrice != null) {
//            if (isPrice == 1) {
//                query.addOrder(Order.desc("sellingPrice"));
//            }
//            if (isPrice == 2) {
//                query.addOrder(Order.asc("sellingPrice"));
//            }
//        }
//        if (isNew != null) {
//            query.add(Restrictions.gt("systemNewTime", new Long(0)));
//            if (isNew == 1) {
//                query.addOrder(Order.desc("systemNewTime"));
//            }
//            if (isNew == 2) {
//                query.addOrder(Order.asc("systemNewTime"));
//            }
//        }
//        if (isRec != null) {
//            query.add(Restrictions.gt("systemRecTime", new Long(0)));
//            if (isRec == 1) {
//                query.addOrder(Order.desc("systemRecTime"));
//            }
//            if (isRec == 2) {
//                query.addOrder(Order.asc("systemRecTime"));
//            }
//        }
//        if (isHot != null) {
//            if (isHot == 1) {
//                query.addOrder(Order.desc("soldNum"));
//            }
//            if (isHot == 2) {
//                query.addOrder(Order.asc("soldNum"));
//            }
//        }
//        query.add(Property.forName("isShelf").in(1));
//        query.add(Property.forName("isValid").eq(1));
//        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
//    }

//    @Override
//    public PageInfo pagedSearchsellerGoods(int pageNum, int pageSize, String keywords, String lang, Integer isNew,
//                                           Integer isRec, Integer isHot, Integer isPrice, Integer is_discount) {
//        StringBuffer queryGoodIdsStr = new StringBuffer();
//        queryGoodIdsStr.append(" SELECT DISTINCT ")
//                .append(" m.GOODS_ID ")
//                .append(" FROM ")
//                .append(" T_MALL_SELLER_GOODS m ")
//                .append(" LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID ")
//                .append(" WHERE ")
//                .append(" m.IS_SHELF=1 AND m.IS_VALID = 1 AND s.LANG = ? ")
//                .append(" AND s.NAME LIKE ? ")
//        ;
//        List<Object> params = new ArrayList<>();
//
//        params.add(lang);
//        params.add("%" + keywords + "%");
//        List<String> goodsIds = jdbcTemplate.queryForList(queryGoodIdsStr.toString(), String.class, params.toArray());
//        if (goodsIds.isEmpty()) {
//            return new PageInfo();
//        }
//        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
//        query.add(Property.forName("goodsId").in(goodsIds));
//        if (is_discount != null && is_discount == 1) {
//            query.add(Restrictions.gt("discountRatio", 0.0D));
//            query.add(Restrictions.gt("discountStartTime", new Date()));
//            query.add(Restrictions.lt("discountEndTime", new Date()));
//        }
//        boolean orderByShowWeight = true;
//        if (isPrice != null) {
//            if (isPrice == 1) {
//                query.addOrder(Order.desc("sellingPrice"));
//                orderByShowWeight = false;
//            }
//            if (isPrice == 2) {
//                query.addOrder(Order.asc("sellingPrice"));
//                orderByShowWeight = false;
//            }
//        }
//        if (isNew != null) {
//            query.add(Restrictions.gt("systemNewTime", new Long(0)));
//            if (isNew == 1) {
//                query.addOrder(Order.desc("systemNewTime"));
//                orderByShowWeight = false;
//            }
//            if (isNew == 2) {
//                query.addOrder(Order.asc("systemNewTime"));
//                orderByShowWeight = false;
//            }
//        }
//        if (isRec != null) {
//            query.add(Restrictions.gt("systemRecTime", new Long(0)));
//            if (isRec == 1) {
//                query.addOrder(Order.desc("systemRecTime"));
//                orderByShowWeight = false;
//            }
//            if (isRec == 2) {
//                query.addOrder(Order.asc("systemRecTime"));
//                orderByShowWeight = false;
//            }
//        }
//        if (isHot != null) {
//            if (isHot == 1) {
//                query.addOrder(Order.desc("soldNum"));
//                orderByShowWeight = false;
//            }
//            if (isHot == 2) {
//                query.addOrder(Order.asc("soldNum"));
//                orderByShowWeight = false;
//            }
//        }
//        if (orderByShowWeight) {
//            query.addOrder(Order.desc("showWeight1"));
//            query.addOrder(Order.desc("showWeight2"));
//        }
//
//        query.add(Property.forName("isShelf").eq(1));
//        query.add(Property.forName("isValid").eq(1));
//        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
//        query.setProjection(null);
//        PageInfo pageInfo = new PageInfo();
//        pageInfo.setTotalElements(totalCount.intValue());
//        pageInfo.setElements((List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));
//        return pageInfo;
//    }

    @Override
    public PageInfo pagedSearchsellerGoods(int pageNum, int pageSize, String keywords, String lang, Integer isNew,
                                           Integer isRec, Integer isHot, Integer isPrice, Integer is_discount) {
        StringBuffer selectSqlStr = new StringBuffer();
        selectSqlStr.append(" SELECT a.UUID as id, a.GOODS_ID as goodsId, a.SELLER_ID as sellerId, ");
        selectSqlStr.append(" a.CATEGORY_ID as categoryId, a.SECONDARY_CATEGORY_ID as secondaryCategoryId, a.SOLD_NUM as soldNum, ");
        selectSqlStr.append(" a.VIEWS_NUM as viewsNum, a.SYSTEM_PRICE as systemPrice, a.SELLING_PRICE as sellingPrice, ");
        selectSqlStr.append(" a.PROFIT_RATIO as profitRatio, a.DISCOUNT_PRICE as discountPrice, a.ORDER_PRICE as orderPrice, ");
        selectSqlStr.append(" a.DISCOUNT_RATIO as discountRatio, a.UP_TIME as upTime, a.FIRST_SHELF_TIME as firstShelfTime, ");
        selectSqlStr.append(" a.CREATE_TIME as createTime, a.REC_TIME as recTime, a.NEW_TIME as newTime, ");
        selectSqlStr.append(" a.IS_SHELF as isShelf, a.SYSTEM_REC_TIME as systemRecTime, a.SYSTEM_NEW_TIME as systemNewTime, ");
        selectSqlStr.append(" a.STOP_TIME as stopTime, a.BUY_MIN as buyMin, a.DISCOUNT_START_TIME as discountStartTime, ");
        selectSqlStr.append(" a.DISCOUNT_END_TIME as discountEndTime, a.IS_COMBO as isCombo, a.IS_VALID as isValid, ");
        selectSqlStr.append(" a.SELL_WELL_TIME as sellWellTime, a.SHOW_WEIGHT1 as showWeight1, a.SHOW_WEIGHT2 as showWeight2, ");
        selectSqlStr.append(" a.SYS_NEW_DURATION as sysNewDuration, a.SYS_REC_DURATION as sysRecDuration, a.SELL_WELL_DURATION as sellWellDuration ");
        selectSqlStr.append(" FROM T_MALL_SELLER_GOODS as a, T_MALL_SYSTEM_GOODS as b");

        StringBuffer countSqlStr = new StringBuffer();
        countSqlStr.append(" select count(*) from T_MALL_SELLER_GOODS as a, T_MALL_SYSTEM_GOODS as b ");

        StringBuffer conditionSqlStr = new StringBuffer();
        conditionSqlStr.append(" where a.GOODS_ID = b.UUID ");
        conditionSqlStr.append(" AND a.IS_SHELF = 1 ");
        conditionSqlStr.append(" AND a.is_VALID = 1 ");
        conditionSqlStr.append(" AND (b.CN_TITLE LIKE ? or b.EN_TITLE LIKE ? or b.ZH_TITLE LIKE ?) ");

        List<Object> params = new ArrayList<>();
        params.add("%" + keywords + "%");
        params.add("%" + keywords + "%");
        params.add("%" + keywords + "%");

        Date now = new Date();
        String nowStr = DateUtil.formatDateTime(now);
        if (is_discount != null && is_discount == 1) {
            conditionSqlStr.append(" AND a.DISCOUNT_RATIO > ? ");
            conditionSqlStr.append(" AND a.DISCOUNT_START_TIME <= ? ");
            conditionSqlStr.append(" AND a.DISCOUNT_END_TIME > ? ");

            params.add(0.0D);
            params.add(nowStr);
            params.add(nowStr);
        }

        String orderByStr = "";
        boolean orderByShowWeight = true;
        if (isPrice != null) {
            if (isPrice == 1) {
                orderByStr = orderByStr + "order by a.SELLING_PRICE desc,";
                orderByShowWeight = false;
            }
            if (isPrice == 2) {
                orderByStr = orderByStr + "order by a.SELLING_PRICE asc,";
                orderByShowWeight = false;
            }
        }
        if (isNew != null) {
            conditionSqlStr.append(" AND a.SYSTEM_NEW_TIME > 0 ");

            if (isNew == 1) {
                orderByStr = orderByStr + "order by a.SYSTEM_NEW_TIME desc,";
                orderByShowWeight = false;
            }
            if (isNew == 2) {
                orderByStr = orderByStr + "order by a.SYSTEM_NEW_TIME asc,";
                orderByShowWeight = false;
            }
        }
        if (isRec != null) {
            conditionSqlStr.append(" AND a.SYSTEM_REC_TIME > 0 ");
            if (isRec == 1) {
                orderByStr = orderByStr + "order by a.SYSTEM_REC_TIME desc,";
                orderByShowWeight = false;
            }
            if (isRec == 2) {
                orderByStr = orderByStr + "order by a.SYSTEM_REC_TIME asc,";
                orderByShowWeight = false;
            }
        }
        if (isHot != null) {
            if (isHot == 1) {
                orderByStr = orderByStr + "order by a.SOLD_NUM desc,";
                orderByShowWeight = false;
            }
            if (isHot == 2) {
                orderByStr = orderByStr + "order by a.SOLD_NUM asc,";
                orderByShowWeight = false;
            }
        }
        if (orderByShowWeight) {
            orderByStr = orderByStr + "order by a.SHOW_WEIGHT1 desc,a.SHOW_WEIGHT2 desc,";
        }

        String pageSql = " limit " + ((pageNum - 1) * pageSize) + "," + pageSize;
        PageInfo pageInfo = new PageInfo();

        List<SellerGoods> sellerGoodsList = new ArrayList<>();
        int totalCount = jdbcTemplate.queryForObject(countSqlStr.toString() + conditionSqlStr.toString(), params.toArray(), Integer.class);
        pageInfo.setTotalElements(totalCount);
        if (totalCount == 0) {
            pageInfo.setElements(sellerGoodsList);
        } else {
            orderByStr = orderByStr.trim();
            if (StrUtil.isNotBlank(orderByStr) && orderByStr.endsWith(",")) {
                orderByStr = orderByStr.substring(0, orderByStr.length() - 1);
                orderByStr = " " + orderByStr + " ";
            }

            log.info("---> SellerGoodsServiceImpl pagedSearchsellerGoods 组装的分页查询sql:{}", (selectSqlStr.toString() + conditionSqlStr.toString() + orderByStr + pageSql));
            List<Map<String, Object>> results = jdbcTemplate.queryForList(selectSqlStr.toString() + conditionSqlStr.toString() + orderByStr + pageSql, params.toArray());

            Set<String> systemGoodsIds = new HashSet<>();
            for (Map<String, Object> oneRow : results) {
                String rowJson = JacksonUtils.objectToJson(oneRow);
                SellerGoods oneSellerGoods = JacksonUtils.jsonToObject(rowJson, SellerGoods.class);
                sellerGoodsList.add(oneSellerGoods);

                systemGoodsIds.add(oneSellerGoods.getGoodsId());
            }
            pageInfo.setElements(sellerGoodsList);

            // 补上 SystemGoods 字段值
            if (!systemGoodsIds.isEmpty()) {
                List<SystemGoods> systemGoodsList = this.getSystemGoodsBatch(new ArrayList<>(systemGoodsIds));
                Map<String, SystemGoods> systemGoodsMap = new HashMap<>();
                for (SystemGoods oneSystemGoods : systemGoodsList) {
                    systemGoodsMap.put(oneSystemGoods.getId(), oneSystemGoods);
                }

                for (SellerGoods oneSellerGoods : sellerGoodsList) {
                    oneSellerGoods.setSystemGoods(systemGoodsMap.get(oneSellerGoods.getGoodsId()));
                }
            }
        }

        return pageInfo;
    }

    @Override
    public List<SellerGoods> querySearchGoods(int pageNum, int pageSize, String goodsId, int isPrice, int isNew, int isHot) {
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("goodsId").eq(goodsId));
        if (isPrice > 0) {
            if (isPrice == 1) {
                query.addOrder(Order.desc("sellingPrice"));
            }
            if (isPrice == 2) {
                query.addOrder(Order.asc("sellingPrice"));
            }
        }
        if (isNew > 0) {
            if (isNew == 1) {
                query.addOrder(Order.desc("newTime"));
            }
            if (isNew == 2) {
                query.addOrder(Order.asc("newTime"));
            }
        }
        if (isHot > 0) {
            if (isHot == 1) {
                query.addOrder(Order.desc("soldNum"));
            }
            if (isHot == 2) {
                query.addOrder(Order.asc("soldNum"));
            }
        }
        query.add(Property.forName("isShelf").eq(1));
        query.add(Property.forName("isValid").eq(1));
        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

    @Override
    public void saveSellerGoods(SellerGoods sellerGoods) {
        getHibernateTemplate().save(sellerGoods);
//        getHibernateTemplate().flush();
    }

    @Override
    public void updateSellerGoods(SellerGoods sellerGoods) {
        getHibernateTemplate().update(sellerGoods);
//        getHibernateTemplate().flush();
    }

    @Override
    public SellerGoods getSellerGoods(String sellerGoodsId) {
        SellerGoods entity = this.getHibernateTemplate().get(SellerGoods.class, sellerGoodsId);
        if (entity == null) {
            return entity;
        }

//        String skuInfoJson = entity.getSkuInfo();
//        if (StrUtil.isNotBlank(skuInfoJson)) {
//            TypeReference paramType = new TypeReference<List<SellerGoodsSkuInfoVO>>() {
//            };
//            List<SellerGoodsSkuInfoVO> skuInfoList = (List<SellerGoodsSkuInfoVO>) JsonUtils.readValue(skuInfoJson.trim(), paramType);
//            entity.setSkuInfoList(skuInfoList);
//        }

        return entity;
    }

    public SystemGoods getSystemGoods(String systemGoodsId) {
        return this.getHibernateTemplate().get(SystemGoods.class, systemGoodsId);
    }

    public List<SystemGoods> getSystemGoodsBatch(List<String> systemGoodsIdList) {
        if (CollectionUtil.isEmpty(systemGoodsIdList)) {
            return new ArrayList<>();
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(SystemGoods.class);
        criteria.add(Restrictions.in("id", systemGoodsIdList));
        List<SystemGoods> list = (List<SystemGoods>) this.getHibernateTemplate().findByCriteria(criteria);
        return list;
    }

    @Override
    public List<SellerGoods> getSellerGoodsBatch(List<String> sellerGoodsIdList) {
        if (CollectionUtil.isEmpty(sellerGoodsIdList)) {
            return new ArrayList<>();
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoods.class);
        criteria.add(Restrictions.in("id", sellerGoodsIdList));
        List<SellerGoods> list = (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(criteria);
        return list;
    }

    @Override
    public List<SellerGoods> listBySystemGoodsIds(List<String> systemGoodsIdList) {
        if (CollectionUtil.isEmpty(systemGoodsIdList)) {
            return new ArrayList<>();
        }

        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoods.class);
        criteria.add(Restrictions.in("goodsId", systemGoodsIdList));
        List<SellerGoods> list = (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(criteria);
        return list;
    }

    @Override
    public SellerGoods getSellerGoods(String goodsId, String sellerId) {
        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoods.class);
        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.add(Restrictions.eq("goodsId", goodsId));
        List<SellerGoods> list = (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.get(0);
        }

        return null;
    }

    @Override
    public Integer getSellerGoodsNum(String sellerId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(SellerGoods.class);
        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.add(Restrictions.eq("isValid", 1));
        criteria.add(Restrictions.eq("isShelf", 0));
        criteria.setProjection(Projections.rowCount());
        Integer totalCount = ((Long) criteria.uniqueResult()).intValue();
        return totalCount;
    }

    @Override
    public Long getSoldNumBySellerId(String sellerId) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT IFNULL( SUM( GOODS__COUNT ), 0 ) ")
                .append(" FROM T_MALL_ORDERS_PRIZE ")
                .append(" WHERE SELLER_ID = ?")
                .append(" AND PURCH_STATUS = 1 ");

        return this.jdbcTemplate.queryForObject(sqlStr.toString(), Long.class, sellerId);
    }

    public Map<String, Long> getSoldNumsBySellerIds(List<String> sellerIds) {
        Map<String, Long> soldCounts = new HashMap<>();
        if (Optional.ofNullable(sellerIds).map(List::isEmpty).orElse(true)) {
            return soldCounts;
        }
        String sql = "SELECT SELLER_ID, IFNULL(SUM(GOODS__COUNT), 0) AS sold_count " +
                "FROM T_MALL_ORDERS_PRIZE " +
                "WHERE SELLER_ID IN (:sellerIds) AND PURCH_STATUS = 1 " +
                "GROUP BY SELLER_ID";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sellerIds", sellerIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql, params);
        for (Map<String, Object> result : results) {
            String sellerId = (String) result.get("SELLER_ID");
            Long soldCount = ((Number) result.get("sold_count")).longValue();
            soldCounts.put(sellerId, soldCount);
        }

        return soldCounts;
    }

    @Override
    public List<SoldGoodsCount> getSoldNumBySellerIds(List<String> sellerIdList) {
        List<SoldGoodsCount> statList = new ArrayList<>();
        if (CollectionUtil.isEmpty(sellerIdList)) {
            return statList;
        }
        StringBuffer sellerIds = new StringBuffer(sellerIdList.size() * 32);
        for (String oneSellerId : sellerIdList) {
            sellerIds.append("'").append(oneSellerId).append("'").append(",");
        }
        sellerIds.deleteCharAt(sellerIds.length() - 1);

        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT SELLER_ID AS sellerId, IFNULL(SUM(GOODS__COUNT), 0) AS goodsCount ")
                .append(" FROM T_MALL_ORDERS_PRIZE ")
                .append(" WHERE SELLER_ID in(").append(sellerIds.toString()).append(") ")
                .append(" AND PURCH_STATUS = 1 ")
                .append(" group by SELLER_ID ");

        List list = this.jdbcTemplate.queryForList(sqlStr.toString());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            String sellerId = (String) rowMap.getOrDefault("sellerId", "");
            BigDecimal goodsCount = (BigDecimal) rowMap.getOrDefault("goodsCount", 0);

            SoldGoodsCount oneStat = new SoldGoodsCount();
            oneStat.setSellerId(sellerId);
            oneStat.setGoodsCount(goodsCount.longValue());

            statList.add(oneStat);
        }

        return statList;
    }

    @Override
    public Long getSoldNumByGoodsId(String goodsId) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT ")
                .append(" SOLD_NUM ")
                .append(" FROM ")
                .append("  T_MALL_SELLER_GOODS   ")
                .append("  WHERE   ")
                .append("  GOODS_ID = ?   ");

        return this.jdbcTemplate.queryForObject(sqlStr.toString(), Long.class, goodsId);
    }

    /**
     * 获取上架商品数
     *
     * @param sellerId
     * @return
     */
    @Override
    public Long getOnSelfGoodsNumBySellerId(String sellerId) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT ")
                .append(" IFNULL( COUNT( UUID ), 0 ) ")
                .append(" FROM ")
                .append("  T_MALL_SELLER_GOODS   ")
                .append("  WHERE   ")
                .append("  SELLER_ID = ?   ")
                .append("  and IS_SHELF=1 AND IS_VALID = 1    ");

        return this.jdbcTemplate.queryForObject(sqlStr.toString(), Long.class, sellerId);
    }

    @Override
    public Long getGoodsNumBySellerId(String sellerId) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT ")
                .append(" COUNT( * ) ")
                .append(" FROM ")
                .append("  T_MALL_SELLER_GOODS   ")
                .append("  WHERE   ")
                .append("  SELLER_ID = ?   ");

        return this.jdbcTemplate.queryForObject(sqlStr.toString(), Long.class, sellerId);
    }

    public Map<String, Long> getGoodsNumBySellerIds(List<String> sellerIds) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT SELLER_ID, COUNT(1) FROM T_MALL_SELLER_GOODS WHERE SELLER_ID IN (:sellerIds) AND IS_SHELF=1 AND IS_VALID =1 GROUP BY SELLER_ID");

        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sellerIds", sellerIds);

        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql.toString(), params);
        Map<String, Long> resultMap = new HashMap<>();

        for (Map<String, Object> result : results) {
            String sellerId = (String) result.get("SELLER_ID");
            Long count = ((Number) result.get("COUNT(1)")).longValue();
            resultMap.put(sellerId, count);
        }

        return resultMap;
    }

    @Override
    public Long getGoodsNumBySellerIdAndLang(String sellerId, String lang) {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT  ")
                .append(" COUNT( m.GOODS_ID )   ")
                .append(" FROM  ")
                .append("  T_MALL_SELLER_GOODS m  ")
//                .append(" INNER JOIN T_MALL_SELLER_GOODSSYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID ")
                .append(" WHERE m.IS_SHELF=1 AND m.IS_VALID =1 ")
                .append(" AND m.SELLER_ID = ? ")
//                .append(" AND s.LANG = ? ")
        ;
        List<Object> params = new ArrayList<>();

        params.add(sellerId);
//        params.add(lang);
        return this.jdbcTemplate.queryForObject(sql.toString(), Long.class, params.toArray());
    }

    @Override
    public List<SellerGoodsCount> getGoodsNumBySellersAndLang(List<String> sellerIdList, String lang) {
        List<SellerGoodsCount> statList = new ArrayList<>();
        if (CollectionUtil.isEmpty(sellerIdList)) {
            return statList;
        }
        StringBuffer sellerIds = new StringBuffer(sellerIdList.size() * 32);
        for (String oneSellerId : sellerIdList) {
            sellerIds.append("'").append(oneSellerId).append("'").append(",");
        }
        sellerIds.deleteCharAt(sellerIds.length() - 1);

        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT m.SELLER_ID AS sellerId, COUNT(m.GOODS_ID) AS goodsCount ")
                .append(" FROM T_MALL_SELLER_GOODS m ")
                .append(" WHERE m.IS_SHELF=1 AND m.IS_VALID =1 AND m.SELLER_ID in(").append(sellerIds.toString()).append(") ")
                .append(" group by m.SELLER_ID ");

        List list = this.jdbcTemplate.queryForList(sqlStr.toString());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            String sellerId = (String) rowMap.getOrDefault("sellerId", "");
            Long goodsCount = (Long) rowMap.getOrDefault("goodsCount", 0);

            SellerGoodsCount oneStat = new SellerGoodsCount();
            oneStat.setSellerId(sellerId);
            oneStat.setGoodsCount(goodsCount);

            statList.add(oneStat);
        }

        return statList;
    }

    @Override
    public Long getViewsNumBySellerId(String sellerId) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT ")
                .append(" IFNULL( SUM( VIEWS_NUM  + VIRTUAL_VIEWS_NUM ), 0 ) ")
                .append(" FROM ")
                .append("  T_MALL_SELLER_GOODS_STATISTICS   ")
                .append("  WHERE   ")
                .append("  SELLER_ID = ?   ");

        return this.jdbcTemplate.queryForObject(sqlStr.toString(), Long.class, sellerId);
    }

    @Override
    public List<SellerViewCount> getViewsNumBySellerIds(List<String> sellerIdList) {
        List<SellerViewCount> statList = new ArrayList<>();
        if (CollectionUtil.isEmpty(sellerIdList)) {
            return statList;
        }
        StringBuffer sellerIds = new StringBuffer(sellerIdList.size() * 32);
        for (String oneSellerId : sellerIdList) {
            sellerIds.append("'").append(oneSellerId).append("'").append(",");
        }
        sellerIds.deleteCharAt(sellerIds.length() - 1);

        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT SELLER_ID AS sellerId, IFNULL(SUM(VIEWS_NUM + VIRTUAL_VIEWS_NUM), 0) AS viewNum ")
                .append(" FROM T_MALL_SELLER_GOODS_STATISTICS ")
                .append(" WHERE SELLER_ID in(").append(sellerIds.toString()).append(") ")
                .append(" group by SELLER_ID ");

        List list = this.jdbcTemplate.queryForList(sqlStr.toString());
        Iterator it = list.iterator();
        while (it.hasNext()) {
            Map rowMap = (Map) it.next();
            String sellerId = (String) rowMap.getOrDefault("sellerId", "");
            BigDecimal viewNum = (BigDecimal) rowMap.getOrDefault("viewNum", 0);

            SellerViewCount oneStat = new SellerViewCount();
            oneStat.setSellerId(sellerId);
            oneStat.setViewNum(viewNum.longValue());

            statList.add(oneStat);
        }

        return statList;
    }

    @Override
    public void deleteSellerGoodsBySystemGoodId(String systemGoodIds) {
        if (StringUtils.isEmptyString(systemGoodIds)) {
            throw new RuntimeException("未指定商家商品id");
        }

        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("systemGoodIds", systemGoodIds);

        String sql = "update T_MALL_SELLER_GOODS set IS_SHELF = 0, IS_VALID = 0 where GOODS_ID = :systemGoodIds";
        nameJdbc.update(sql, params);
    }

    @Override
    public Long getSellerGoodsNumBySellerId(String sellerId, String lang) {
        StringBuffer sqlStr = new StringBuffer();
        sqlStr.append(" SELECT DISTINCT  ")
                .append(" COUNT( distinct m.UUID)    ")
                .append(" FROM ")
                .append("  T_MALL_SELLER_GOODS m    ")
                .append("  LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID     ")
                .append("  WHERE   ")
                .append("  s.LANG = ?   ")
                .append("  AND s.type = 0 AND m.IS_VALID = 1   ")
                .append("  AND m.SELLER_ID = ?   ");

        return this.jdbcTemplate.queryForObject(sqlStr.toString(), Long.class, lang, sellerId);
    }


    @Override
    public void deleteSellerGoods(String sellerGoodsId, String sellerId) {
        SellerGoods sellerGoods = this.getSellerGoods(sellerGoodsId);
        if (sellerGoods == null || !sellerGoods.getSellerId().equals(sellerId)) {
            return;
        }
        sellerGoods.setIsShelf(0);
        sellerGoods.setIsValid(0);
        sellerGoods.setDiscountRatio(0D);
        sellerGoods.setProfitRatio(0D);
        sellerGoods.setDiscountStartTime(null);
        sellerGoods.setDiscountEndTime(null);
        sellerGoods.setSellingPrice(0D);
        sellerGoods.setDiscountPrice(0D);
        this.getHibernateTemplate().update(sellerGoods);
        // 删除店铺商品时候  要清理 SellerGoodsSku的缓存 并删除关联的SellerGoodsSku数据
        // 取代 TODO
//        DetachedCriteria query = DetachedCriteria.forClass(SellerGoodsSku.class);
//        query.add(Property.forName("sellerGoodsId").eq(sellerGoods.getId()));
//        List<SellerGoodsSku> sellerGoodsSkuList = (List<SellerGoodsSku>) this.getHibernateTemplate().findByCriteria(query);
//        deleteCachedSellerGoodSku(sellerGoodsSkuList);

        deleteCachedSellerGoodSku1(sellerGoods);

        // 取代 TODO
//        if (Objects.nonNull(sellerGoodsSkuList) && !sellerGoodsSkuList.isEmpty()) {
//            this.getHibernateTemplate().deleteAll(sellerGoodsSkuList);
//        }
    }

    @Override
    public void deleteAllSellerGoods(String sellerId) {
        if (StringUtils.isEmptyString(sellerId)) {
            throw new RuntimeException("未指定商家ID");
        }

        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);

        String sql = "update T_MALL_SELLER_GOODS set IS_SHELF = 0, IS_VALID = 0 where SELLER_ID = :sellerId";
        nameJdbc.update(sql, params);
    }

    @Transactional
    @Override
    public void shelfBatch(List<String> sellerGoodsIdList, String sellerId, Integer isShelf) {
        if (CollectionUtil.isEmpty(sellerGoodsIdList) || isShelf == null || StringUtils.isEmptyString(sellerId)) {
            log.error("批量更新商家状态，传入参数不对:{},{},{}", sellerGoodsIdList, sellerId, isShelf);
            return;
        }
        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        params.put("sellerGoodsIdList", sellerGoodsIdList);
        params.put("isShelf", isShelf);

        String sql = "update T_MALL_SELLER_GOODS set IS_SHELF = :isShelf where UUID in (:sellerGoodsIdList) and SELLER_ID = :sellerId";
        nameJdbc.update(sql, params);
    }

    @Override
    public SellerGoods getRandomSellerGoods(String sellerId) {
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        String sql = "SELECT DISTINCT m.GOODS_ID FROM T_MALL_SELLER_GOODS m LEFT JOIN T_MALL_SYSTEM_GOODS_LANG s ON m.GOODS_ID = s.GOODS_ID WHERE s.TYPE = 0 AND m.IS_SHELF = 1 AND m.SELLER_ID = ?";
        List<String> goodsIds = jdbcTemplate.queryForList(sql, String.class, sellerId);
        if (goodsIds.isEmpty()) {
            return null;
        }
        query.add(Property.forName("goodsId").eq(goodsIds.get((int) (Math.random() * goodsIds.size()))));
        return ((List<SellerGoods>) getHibernateTemplate().findByCriteria(query, 0, 1)).get(0);
    }

    /**
     * 获取卖家当前直通车激活状态的商品
     *
     * @param sellerId
     * @return
     */
    @Override
    public List<SellerGoods> getSellerComboActiveGoods(String sellerId) {
        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        String sql = "select *,UUID as id  from T_MALL_SELLER_GOODS where SELLER_ID = :sellerId and IS_SHELF = 1 AND IS_VALID = 1 and IS_COMBO = 1 and STOP_TIME > unix_timestamp(now())*1000 order by UUID desc";
        return nameJdbc.query(sql, params, new BeanPropertyRowMapper<SellerGoods>(SellerGoods.class));
    }

    @Override
    public List<String> getSellerGoodsId(String sellerId, Integer isCombo) {
        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        String sqlIsShelf = "select UUID from T_MALL_SELLER_GOODS where SELLER_ID = :sellerId and IS_SHELF = 1  order by UUID desc";
        String sqlIsCombo = "select UUID from T_MALL_SELLER_GOODS where SELLER_ID = :sellerId and IS_SHELF = 1 and IS_COMBO = 1 and STOP_TIME > unix_timestamp(now())  order by UUID desc";

        if (isCombo == null) {
            return nameJdbc.queryForList(sqlIsShelf, params, String.class);
        } else if (isCombo == 1) {
            return nameJdbc.queryForList(sqlIsCombo, params, String.class);
        } else if (isCombo == 0) {
            List<String> allShelfIds = nameJdbc.queryForList(sqlIsShelf, params, String.class);
            List<String> isComboIds = nameJdbc.queryForList(sqlIsShelf, params, String.class);
            return CollectionUtil.subtractToList(allShelfIds, isComboIds);
        }
        return new ArrayList<>();
    }

    /**
     * 查看商家所有商品的一级分类id集合.
     *
     * @param sellerId
     * @param onlyOnShelf
     * @return
     */
    @Override
    public List<String> getSellerCategoryList(String sellerId, boolean onlyOnShelf) {
        String sql = "";
        if (onlyOnShelf) {
            // 过滤掉未上架的商品
            sql = "SELECT CATEGORY_ID FROM T_MALL_SELLER_GOODS WHERE SELLER_ID= ? and IS_SHELF = 1 GROUP BY CATEGORY_ID";
        } else {
            sql = "SELECT CATEGORY_ID FROM T_MALL_SELLER_GOODS WHERE SELLER_ID= ? GROUP BY CATEGORY_ID";
        }

        List<String> categoryList = jdbcTemplate.queryForList(sql, String.class, sellerId);

        return categoryList;
    }

    /**
     * 查看商家所有商品二级分类id集合.
     *
     * @param sellerId
     * @param onlyOnShelf
     * @return
     */
    @Override
    public List<String> getSellerSecondaryCategoryList(String sellerId, boolean onlyOnShelf) {
        String sql = "";
        if (onlyOnShelf) {
            // 过滤掉未上架的商品
            sql = "SELECT SECONDARY_CATEGORY_ID FROM T_MALL_SELLER_GOODS WHERE SELLER_ID= ?  and IS_SHELF = 1 GROUP BY SECONDARY_CATEGORY_ID";
        } else {
            sql = "SELECT SECONDARY_CATEGORY_ID FROM T_MALL_SELLER_GOODS WHERE SELLER_ID= ? GROUP BY SECONDARY_CATEGORY_ID";
        }

        List<String> categoryList = jdbcTemplate.queryForList(sql, String.class);

        return categoryList;
    }

    /**
     * 查看商家所有商品所有分类id信息集合.
     *
     * @param sellerId
     * @param onlyOnShelf
     * @return
     */
    @Override
    public List<String> getSellerAllCategoryList(String sellerId, boolean onlyOnShelf) {
        String sql = "";
        if (onlyOnShelf) {
            // 过滤掉未上架的商品
            // 注意：concat 方法组装时，如果有任一列值为 null, 则组装的整行记录为 null
            // concat_ws 方法组装时，列值为 null 的记录，组装时该列对应的位置无值，不会导致整行都为 null
            sql = "SELECT concat_ws(',', CATEGORY_ID, SECONDARY_CATEGORY_ID) FROM T_MALL_SELLER_GOODS WHERE SELLER_ID= ?  and IS_SHELF = 1 and IS_VALID=1 ";
        } else {
            sql = "SELECT concat_ws(',', CATEGORY_ID, SECONDARY_CATEGORY_ID) FROM T_MALL_SELLER_GOODS WHERE SELLER_ID= ? and IS_VALID=1 ";
        }

        List<String> categoryIdInfoList = jdbcTemplate.queryForList(sql, String.class, sellerId);
        Set<String> categoryIds = new HashSet<>();
        if (CollectionUtil.isEmpty(categoryIdInfoList)) {
            return new ArrayList<>();
        }
        for (String oneCategoryIdInfo : categoryIdInfoList) {
            if (StrUtil.isBlank(oneCategoryIdInfo)) {
                continue;
            }
            String[] idArr = oneCategoryIdInfo.split(",");
            for (String oneId : idArr) {
                categoryIds.add(oneId);
            }
        }
        categoryIds.remove("0");

        return new ArrayList<>(categoryIds);
    }

    @Transactional
    @Override
    public void updateVirtualViewsBatch(String sellerId, Map<String, Long> goodsViewAddMap) {
        String dateStr = DateUtil.now().split(":")[0] + ":00";
        // goodId,viewsAdd
        List<GoodsViewAdd> updateDatas = goodsViewAddMap.entrySet()
                .stream()
                .filter(e -> e.getValue() > 0)
                .map(e -> {
                    GoodsViewAdd goodsViewAdd = new GoodsViewAdd();
                    goodsViewAdd.setSellerId(sellerId);
                    goodsViewAdd.setGoodsId(e.getKey());
                    goodsViewAdd.setViewsAdd(e.getValue());
                    goodsViewAdd.setDateStr(dateStr);
                    return goodsViewAdd;

                }).collect(Collectors.toList());

        String sql = "INSERT INTO T_MALL_SELLER_GOODS_STATISTICS ( UUID, SELLER_ID, GOODS_ID, VIEWS_NUM, VIRTUAL_VIEWS_NUM, CREATE_TIME, DATE_STR )" +
                "VALUES" +
                "( ?, ?, ?, 0, ?, NOW(), ?) " +
                "ON DUPLICATE KEY UPDATE VIRTUAL_VIEWS_NUM = VIRTUAL_VIEWS_NUM +?;";
        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, IdUtil.simpleUUID());
                ps.setString(2, updateDatas.get(i).getSellerId());
                ps.setString(3, updateDatas.get(i).getGoodsId());
                ps.setLong(4, updateDatas.get(i).getViewsAdd());
                ps.setString(5, updateDatas.get(i).getDateStr());
                ps.setLong(6, updateDatas.get(i).getViewsAdd());
            }

            @Override
            public int getBatchSize() {
                return updateDatas.size();
            }
        });
    }

    @Override
    public void addRealViews(String sellerId, String goodsId, String viewerId, Integer viewsNum) {
        String isBlack = redisHandler.getString(PartyRedisKeys.PARTY_ID_SELLER_BLACK + sellerId);
        if ("1".equalsIgnoreCase(isBlack)) {
            log.info("{} 店铺已经被拉黑", sellerId);
            return;
        }
        String dateStr = DateUtil.now().split(":")[0] + ":00";

        List<Map<String, Object>> maps = jdbcTemplate.queryForList("select UUID FROM T_MALL_SELLER_GOODS_STATISTICS where  DATE_STR = '" + dateStr + "' AND " + "GOODS_ID = '" + goodsId + "' AND SELLER_ID = '" + sellerId + "'");

        synchronized (this) {
            if (CollectionUtils.isEmpty(maps)) {

                try {
                    //插入
                    String sql = "INSERT INTO T_MALL_SELLER_GOODS_STATISTICS ( UUID, SELLER_ID, GOODS_ID, VIEWS_NUM, VIRTUAL_VIEWS_NUM, CREATE_TIME, DATE_STR )" +
                            "VALUES ( ?, ?, ?, " + viewsNum + ", 0, NOW(), ?) ";
                    jdbcTemplate.update(sql, IdUtil.simpleUUID(), sellerId, goodsId, dateStr);

                } catch (DuplicateKeyException ex) {
                    //更新
                    String sql = "UPDATE T_MALL_SELLER_GOODS_STATISTICS SET VIEWS_NUM = VIEWS_NUM +" + viewsNum + " WHERE DATE_STR = '" + dateStr + "' AND " + "GOODS_ID = '" + goodsId + "' AND SELLER_ID = '" + sellerId + "'";
                    jdbcTemplate.update(sql);
                }
            } else {
                //更新
                String sql = "UPDATE T_MALL_SELLER_GOODS_STATISTICS SET VIEWS_NUM = VIEWS_NUM + " + viewsNum + " WHERE DATE_STR = '" + dateStr + "' AND " + "GOODS_ID = '" + goodsId + "' AND SELLER_ID = '" + sellerId + "'";
                jdbcTemplate.update(sql);
            }
        }
        // 发布一个商品访问量变更的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        int addViewCount = 1;
        SellerGoodsViewCountInfo info = new SellerGoodsViewCountInfo();
        info.setPartyId("");
        info.setSellerGoodsId(goodsId);
        info.setAddViewCount(addViewCount);
        wac.publishEvent(new SellerGoodsViewCountEvent(this, info));
    }

    @Override
    public Long getViewNums(String goodsId) {
        String sql = "select IFNULL(sum(VIEWS_NUM + VIRTUAL_VIEWS_NUM), 0) as c from T_MALL_SELLER_GOODS_STATISTICS where GOODS_ID = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, goodsId);
    }

    @Override
    public Long getRealViewNums(String goodsId) {
        String sql = "select IFNULL(sum(VIEWS_NUM), 0) as c from T_MALL_SELLER_GOODS_STATISTICS where GOODS_ID = ?";
        return jdbcTemplate.queryForObject(sql, Long.class, goodsId);
    }

    @Override
    public Map<String, Long> getViewNums(List<String> goodsId) {
        Map<String, Long> result = new HashMap<>();
        if (CollectionUtil.isEmpty(goodsId)) {
            return result;
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("goodsList", goodsId);
        String sql = "select GOODS_ID as goodsId, sum(VIEWS_NUM + VIRTUAL_VIEWS_NUM) as c from T_MALL_SELLER_GOODS_STATISTICS where GOODS_ID in (:goodsList) GROUP BY GOODS_ID";
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql, params);

        for (Map<String, Object> data : maps) {
            result.put(data.get("goodsId").toString(), Long.parseLong(data.get("c").toString()));
        }
        return result;
    }

    @Override
    public Map<String, Long> getRealViewNums(List<String> goodsId) {
        Map<String, Long> result = new HashMap<>();
        if (CollectionUtil.isEmpty(goodsId)) {
            return result;
        }
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("goodsList", goodsId);
        String sql = "select GOODS_ID as goodsId, sum(VIEWS_NUM) as c from T_MALL_SELLER_GOODS_STATISTICS where GOODS_ID in (:goodsList) GROUP BY GOODS_ID";
        List<Map<String, Object>> maps = namedParameterJdbcTemplate.queryForList(sql, params);

        for (Map<String, Object> data : maps) {
            result.put(data.get("goodsId").toString(), Long.parseLong(data.get("c").toString()));
        }
        return result;
    }

    @Override
    public List<GoodsSellerSalesDto> listGoodsSellerSales(String sellerId, String lang) {
        String sql = "SELECT " +
                " a.GOODS_ID AS goodsId, " +
                "a.GOODS_PRIZE as prizes, " +
                "SUM(GOODS_NUM) AS sellCount  " +
                "FROM " +
                "T_MALL_ORDERS_GOODS a " +
                "LEFT JOIN T_MALL_ORDERS_PRIZE b ON a.ORDER_ID = b.UUID  " +
                "WHERE " +
                "b.SELLER_ID = ? and b.RETURN_STATUS = 0 and b.PURCH_STATUS = 1 " +
                "GROUP BY " +
                "goodsId  " +
                "ORDER BY " +
                "sellCount DESC " +
                "LIMIT 10";
        List<GoodsSellerSalesDto> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(GoodsSellerSalesDto.class), sellerId);
        List<String> goodsIds = list.stream().map(GoodsSellerSalesDto::getGoodsId).collect(Collectors.toList());
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("id").in(goodsIds));
        List<SellerGoods> sellerGoodsList = (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, 0, 10);
        Map<String, String> categoryMap = new HashMap<>();
        Map<String, String> goodsMap = new HashMap<>();
        for (SellerGoods sellerGoods : sellerGoodsList) {
            String key = MallRedisKeys.TYPE_LANG + lang + ":" + sellerGoods.getCategoryId();
            String langStr = redisHandler.getString(key);
            if (StringUtils.isEmptyString(langStr)) {
                continue;
            }
            CategoryLang cLang = JSONArray.parseObject(langStr, CategoryLang.class);
            categoryMap.put(sellerGoods.getId().toString(), cLang.getName());
            String goodsNameStr = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + sellerGoods.getGoodsId());
            if (StringUtils.isEmptyString(goodsNameStr)) {
                continue;
            }
            SystemGoodsLang pLang = JSONArray.parseObject(goodsNameStr, SystemGoodsLang.class);
            if (pLang.getType() == 1) {
                continue;
            }
            goodsMap.put(sellerGoods.getId().toString(), pLang.getName());
        }
        List<GoodsSellerSalesDto> result = new ArrayList<>();

        for (GoodsSellerSalesDto sellerSalesDto : list) {
            String goodsId = sellerSalesDto.getGoodsId();
            sellerSalesDto.setGoodsTypeName(categoryMap.get(goodsId));
            sellerSalesDto.setName(goodsMap.get(goodsId));
            if (StringUtils.isEmptyString(sellerSalesDto.getName())) {
                continue;
            }
            result.add(sellerSalesDto);
        }

        return result;
    }

    /**
     * 优化商铺销售 topN 统计逻辑，允许复用最近的统计结果.
     *
     * @param fromTime
     * @param toTime
     * @param topN
     * @return
     */
    public List<SellerTopNDto> cacheTopNSellers(String fromTime, String toTime, final int topN) {
        Date now = new Date();

        String from = fromTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String to = toTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String funKey = "CACHE_SELLER_TOPN:" + from + "-" + to;
        FunParams funParams = FunParams.newParam()
                .set("sellerGoodsService", this);

        // 基于一个缓存上次执行结果的组件进行数据统计方法的调用，如果上次缓存结果满足条件，则优先使用缓存结果，否则，触发真实的统计执行
        // 执行结果缓存 1 个小时
        SellerTopNListDto topNList = CacheOperation.execute(funKey, true, 3600L * 1000L, funParams, (params) -> {
            SellerGoodsService outSellerGoodsService = params.get("sellerGoodsService").getAs(SellerGoodsService.class);
            List<SellerTopNDto> list = outSellerGoodsService.getTopNSellers(fromTime, toTime, topN);

            SellerTopNListDto dto = new SellerTopNListDto();
            dto.setList(list);
            dto.setTime(now.getTime());
            dto.setTimeRangeKey(from + "-" + to);

            return dto;
        });

        return topNList.getList();
//        if (topNList == null) {
//            // 先将处理时间记录起来，尽量减少重复统计
//            if (lastTime > 0 && lastTime + 5L * 1000L >= now.getTime()) {
//                // 5 秒钟内有查询，为减低服务器压力，此处直接返回空
//                return new ArrayList<>();
//            }
//            lastStatTopNSellerTimeMap.put(from + "-" + to, now.getTime());
//
//            List<SellerTopNDto> list = topNSellers(fromTime, toTime, topN);
//            SellerTopNListDto dto = new SellerTopNListDto();
//            dto.setList(list);
//            dto.setTime(now.getTime());
//            dto.setTimeRangeKey(from + "-" + to);
//
//            redisHandler.setSync(cacheKey, dto);
//
//            return list;
//        } else {
//            lastTime = topNList.getTime();
//            if (now.getTime() >= lastTime + 3600L * 1000L) {
//                log.info("-----> SellerGoodsServiceImpl cacheTopNSellers 缓存中的时间段为:{} - {} 的 top:{} 记录过期，将触发一次异步刷新", from, to, topN);
//                // 一个小时前的排名
//
//
//                GoFun.go(outParams, (params) -> {
//                    SellerGoodsService sellerGoodsService = params.get("sellerGoodsService").getAs(SellerGoodsService.class);
//                    RedisHandler redisHandler = params.get("redisHandler").getAs(RedisHandler.class);
//                    // 先将处理时间记录起来，尽量减少重复统计
//                    lastTime = lastStatTopNSellerTimeMap.get(from + "-" + to);
//                    if (lastTime > 0 && lastTime + 5L * 1000L >= now.getTime()) {
//                        // 5 秒钟内有查询，为减低服务器压力，此处直接返回空
//                        return new ArrayList<>();
//                    }
//                    lastStatTopNSellerTimeMap.put(from + "-" + to, now.getTime());
//
//                    List<SellerTopNDto> list = sellerGoodsService.topNSellers(fromTime, toTime, topN);
//                    SellerTopNListDto dto = new SellerTopNListDto();
//                    dto.setList(list);
//                    dto.setTime(now.getTime());
//                    dto.setTimeRangeKey(from + "-" + to);
//
//                    redisHandler.setSync(cacheKey, dto);
//                });
//            } else {
//                log.info("-----> SellerGoodsServiceImpl cacheTopNSellers 缓存中的时间段为:{} - {} 的 top:{} 记录尚未过期，直接使用缓存中的数据", from, to, topN);
//            }
//
//            return topNList.getList();
//        }
    }

    @Override
    public List<SellerTopNDto> getTopNSellers(String fromTime, String toTime, int topN) {
        // 买家和卖家都是正规账号（flag=0），买家是演示账号+卖家是正规账号（flag=1）两种场景对应的订单都纳入统计
        String sql = "SELECT SELLER_ID as sellerId, max(SELLER_NAME) AS sellerName, SUM(PRIZE_REAL) AS amount, SUM(GOODS__COUNT) AS goodsCount " +
                "FROM  T_MALL_ORDERS_PRIZE " +
                "WHERE " +
                "RETURN_STATUS = 0 and PURCH_STATUS = 1 " +
                "and FLAG in(0,1) ";
        List<Object> params = new ArrayList<>();
        if (StrUtil.isNotBlank(fromTime)) {
            sql = sql + " and CREATE_TIME >=  ? ";
            params.add(fromTime);
        }
        if (StrUtil.isNotBlank(toTime)) {
            sql = sql + " and CREATE_TIME <  ? ";
            params.add(toTime);
        }

        sql = sql + " GROUP BY sellerId " +
                " ORDER BY amount DESC " +
                " LIMIT " + topN;

        logger.info("---> topNSellers top10 sql: " + sql);

        List<SellerTopNDto> list = jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(SellerTopNDto.class), params.toArray());

        return list;
    }

    @Override
    public Map<String, Object> querySumSellerOrdersPrize(String fromTime, String toTime) {
        String sql = "SELECT  CAST(SUM(IFNULL(PRIZE_REAL,0)) AS DECIMAL (19, 3)) AS totalSales FROM  T_MALL_ORDERS_PRIZE  WHERE " +
                "RETURN_STATUS = 0 and PURCH_STATUS = 1 and FLAG in(0,1) ";
        if (StrUtil.isNotBlank(fromTime)) {
            sql = sql + " and CREATE_TIME >= '" + fromTime + "' ";
        }
        if (StrUtil.isNotBlank(toTime)) {
            sql = sql + " and CREATE_TIME < '" + toTime + "' ";
        }
        Map<String, Object> resultMap = jdbcTemplate.queryForMap(sql);

        resultMap.put("totalSales", Objects.isNull(resultMap.get("totalSales")) ? 0 : resultMap.get("totalSales"));

        BigDecimal result = new BigDecimal(String.valueOf(resultMap.get("totalSales"))).setScale(2, BigDecimal.ROUND_DOWN);
        resultMap.put("totalSales", result);

        return resultMap;
    }

    @Override
    public Map<String, Object> queryCacheSumSellerOrdersPrize(String startTime, String endTime) {
        String funKey = this.getKey(startTime, endTime, LocalCacheBucketKey.SumSellerOrdersPrize);

        FunParams funParams = FunParams.newParam().set("sellerGoodsService", this);

        Map<String, Object> result = CacheOperation.execute(funKey, true, 600 * 1000L, funParams, (params) -> {
            SellerGoodsService partyService = params.get("sellerGoodsService").getAs(SellerGoodsService.class);
            Map<String, Object> getResult = partyService.querySumSellerOrdersPrize(startTime, endTime);
            return getResult;
        });
        return result;
    }


    @Override
    public int getCountGoods(String sellerId, int shelfState) {
        if (sellerId == null || sellerId.trim().isEmpty()) {
            throw new RuntimeException("缺失参数");
        }
        List<Object> params = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT COUNT(*) as 'count' FROM T_MALL_SELLER_GOODS WHERE SELLER_ID= ?");
        params.add(sellerId);
        if (shelfState >= 0) {
            sql.append(" and IS_SHELF= ?");
            params.add(shelfState);
        }
        List list = jdbcTemplate.queryForList(sql.toString(), params.toArray());
        Iterator it = list.iterator();
        List<CategoryGoodCountDto> resultList = new ArrayList<>();
        if (it.hasNext()) {
            Map rowrMap = (Map) it.next();
            if (rowrMap.get("count") != null) {
                int count = Integer.parseInt(rowrMap.getOrDefault("count", "0") + "");
                return count;
            }
        }

        return 0;
    }

    @Override
    public void saveSellerGoodsSkus(SellerGoods sellerGoods) {
//        // 先去 sku表查询商品所有的没有被删除的sku。如果没有什么也不敢
//        // 按照商品各种价格计算方法，计算sku价格，然后入库。
//        if (sellerGoods == null) {
//            return;
//        }
//        String systemGoodsId = sellerGoods.getGoodsId();
//        DetachedCriteria skuQuery = DetachedCriteria.forClass(GoodsSku.class);
//        skuQuery.add(Property.forName("goodId").eq(systemGoodsId));
//        skuQuery.add(Property.forName("deleted").eq(0));
//        List<GoodsSku> goodsSkuList = (List<GoodsSku>) getHibernateTemplate().findByCriteria(skuQuery);
//        if (CollectionUtil.isEmpty(goodsSkuList)) {
//            return;
//        }
//
//        for (GoodsSku goodsSku : goodsSkuList) {
//            SellerGoodsSku sellerGoodsSku = new SellerGoodsSku();
//            double systemPrice = sellerGoods.getSystemPrice();
//            BigDecimal goodsSkuPrice = goodsSku.getPrice();
//            if (goodsSkuPrice != null) {
//                systemPrice = goodsSkuPrice.doubleValue();
//            }
//            Double profitRatio = sellerGoods.getProfitRatio();
//            double sellingPrice = Arith.mul(systemPrice, Arith.add(1.00D, profitRatio));
//            sellerGoodsSku.setId(IdUtils.getSellerGoodsSkuId());
//            sellerGoodsSku.setSkuId(goodsSku.getId().toString());
//            sellerGoodsSku.setSellerId(sellerGoods.getSellerId());
//            sellerGoodsSku.setSystemGoodsId(sellerGoods.getGoodsId());
//            sellerGoodsSku.setSellerGoodsId(sellerGoods.getId().toString());
//            sellerGoodsSku.setCategoryId(sellerGoods.getCategoryId());
//            sellerGoodsSku.setSystemPrice(systemPrice);
//            sellerGoodsSku.setSellingPrice(sellingPrice);
//            sellerGoodsSku.setProfitRatio(sellerGoods.getProfitRatio());
//            sellerGoodsSku.setIsDelete(0);
//            if (sellerGoods.getDiscountRatio() != null) {
//                Double discountRatio = sellerGoods.getDiscountRatio();
//                sellerGoodsSku.setDiscountPrice(Arith.mul(sellingPrice, Arith.sub(1.00D, discountRatio)));
//                sellerGoodsSku.setDiscountRatio(discountRatio);
//            }
//            sellerGoodsSku.setSoldNum(0);
//            sellerGoodsSku.setCreateTime(new Date());
//            getHibernateTemplate().save(sellerGoodsSku);
//        }
    }

    @Override
    public void fillSellerGoodsSkus(SellerGoods sellerGoods) {
        // 先去 sku表查询商品所有的没有被删除的sku。如果没有什么也不敢
        // 按照商品各种价格计算方法，计算sku价格，然后入库。
        if (sellerGoods == null) {
            return;
        }
        String systemGoodsId = sellerGoods.getGoodsId();
        DetachedCriteria skuQuery = DetachedCriteria.forClass(GoodsSku.class);
        skuQuery.add(Property.forName("goodId").eq(systemGoodsId));
        skuQuery.add(Property.forName("deleted").eq(0));
        List<GoodsSku> goodsSkuList = (List<GoodsSku>) getHibernateTemplate().findByCriteria(skuQuery);
        if (CollectionUtil.isEmpty(goodsSkuList)) {
            return;
        }

        List<SellerGoodsSkuInfoVO> skuInfoList = new ArrayList();
        for (GoodsSku goodsSku : goodsSkuList) {
            SellerGoodsSkuInfoVO sellerGoodsSku = new SellerGoodsSkuInfoVO();
            sellerGoodsSku.setSkuId(goodsSku.getId().toString());
            double systemPrice = sellerGoods.getSystemPrice();
            BigDecimal goodsSkuPrice = goodsSku.getPrice();
            if (goodsSkuPrice != null) {
                systemPrice = goodsSkuPrice.doubleValue();
            }
            Double profitRatio = sellerGoods.getProfitRatio();
            double sellingPrice = Arith.mul(systemPrice, Arith.add(1.00D, profitRatio));
            sellerGoodsSku.setSystemPrice(systemPrice);
            sellerGoodsSku.setSellingPrice(sellingPrice);
            sellerGoodsSku.setProfitRatio(sellerGoods.getProfitRatio());
            if (sellerGoods.getDiscountRatio() != null) {
                Double discountRatio = sellerGoods.getDiscountRatio();
                sellerGoodsSku.setDiscountPrice(Arith.mul(sellingPrice, Arith.sub(1.00D, discountRatio)));
                sellerGoodsSku.setDiscountRatio(discountRatio);
            }
            skuInfoList.add(sellerGoodsSku);
        }

        //sellerGoods.setSkuInfo(JsonUtils.bean2Json(skuInfoList));
    }

    /**
     * 更新的时候只能删除原有的，在插入
     *
     * @param sellerGoods
     */
    @Override
    public void updateSellerGoodsSkus(SellerGoods sellerGoods) {
        if (sellerGoods == null) {
            return;
        }
//        DetachedCriteria skuQuery = DetachedCriteria.forClass(SellerGoodsSku.class);
//        skuQuery.add(Property.forName("sellerGoodsId").eq(sellerGoods.getId()));
//        List<SellerGoodsSku> goodsSkuList = (List<SellerGoodsSku>) getHibernateTemplate().findByCriteria(skuQuery);
//        if (CollectionUtil.isNotEmpty(goodsSkuList)) {
//            getHibernateTemplate().deleteAll(goodsSkuList);
//            this.deleteCachedSellerGoodSku(goodsSkuList);
//        }
//
//        saveSellerGoodsSkus(sellerGoods);

        // 取代 TODO
        // 无需做任何额外的处理，因为最新版店铺商品 sku 将是基于系统商品 sku 临时组建返回
    }

//    @Override
//    public SellerGoodsSku findSellerGoodSku(SellerGoods sellerGoods, String skuId) {
//        SellerGoodsSku sellerGoodsSku = new SellerGoodsSku();
//        if (StringUtils.isEmptyString(skuId) || "-1".equalsIgnoreCase(skuId)) {
//            BeanUtil.copyProperties(sellerGoods, sellerGoodsSku, "id");
//        }
//        DetachedCriteria skuQuery = DetachedCriteria.forClass(SellerGoodsSku.class);
//        skuQuery.add(Property.forName("sellerGoodsId").eq(sellerGoods.getId()));
//        skuQuery.add(Property.forName("skuId").eq(skuId));
//        skuQuery.add(Property.forName("isDelete").eq(0));
//        List<SellerGoodsSku> goodsSkuList = (List<SellerGoodsSku>) getHibernateTemplate().findByCriteria(skuQuery);
//        if (CollectionUtil.isEmpty(goodsSkuList)) {
//            BeanUtil.copyProperties(sellerGoods, sellerGoodsSku, "id");
//        } else {
//            sellerGoodsSku = goodsSkuList.get(0);
//        }
//        Date discountStartTime = sellerGoods.getDiscountStartTime();
//        Date discountEndTime = sellerGoods.getDiscountEndTime();
//        if (discountStartTime == null || discountEndTime == null
//                || !discountStartTime.before(new Date()) || !discountEndTime.after(new Date())) {
//            sellerGoodsSku.setDiscountPrice(null);
//        }
//
//        return sellerGoodsSku;
//    }

    @Override
    public SellerGoodsSkuInfoVO findSellerGoodSku(SellerGoods sellerGoods, String skuId) {
        SellerGoodsSkuInfoVO sellerGoodsSku = new SellerGoodsSkuInfoVO();
        sellerGoodsSku.setSkuId(skuId);
        GoodsSku goodsSku = null;
        if (StringUtils.isEmptyString(skuId) || "-1".equalsIgnoreCase(skuId)) {
            // do nothing
        } else {
            goodsSku = this.getHibernateTemplate().get(GoodsSku.class, skuId);
        }

        if (goodsSku == null) {
            BeanUtil.copyProperties(sellerGoods, sellerGoodsSku, "id");
        } else {
            double systemPrice = sellerGoods.getSystemPrice();
            // sku 系统价格
            BigDecimal goodsSkuPrice = goodsSku.getPrice();
            if (goodsSkuPrice != null) {
                systemPrice = goodsSkuPrice.doubleValue();
            }
            Double profitRatio = sellerGoods.getProfitRatio();
            // 店铺商品售价，要在系统价格基础上增加利润比例来计算
            double sellingPrice = Arith.mul(systemPrice, Arith.add(1.00D, profitRatio));
            sellerGoodsSku.setSellerId(sellerGoods.getSellerId());
            sellerGoodsSku.setSystemGoodsId(sellerGoods.getGoodsId());
            sellerGoodsSku.setSellerGoodsId(sellerGoods.getId());
            sellerGoodsSku.setCategoryId(sellerGoods.getCategoryId());
            sellerGoodsSku.setSystemPrice(systemPrice);
            sellerGoodsSku.setSellingPrice(sellingPrice);
            sellerGoodsSku.setProfitRatio(sellerGoods.getProfitRatio());
            if (sellerGoods.getDiscountRatio() != null) {
                Double discountRatio = sellerGoods.getDiscountRatio();
                sellerGoodsSku.setDiscountPrice(Arith.mul(sellingPrice, Arith.sub(1.00D, discountRatio)));
                sellerGoodsSku.setDiscountRatio(discountRatio);
            }
        }

        Date now = new Date();
        Date discountStartTime = sellerGoods.getDiscountStartTime();
        Date discountEndTime = sellerGoods.getDiscountEndTime();
        if (discountStartTime == null
                || discountEndTime == null
                || discountStartTime.after(now)
                || discountEndTime.before(now)) {
            // 未配置折扣起止时间，或者当前时间不在折扣期间
            sellerGoodsSku.setDiscountPrice(null);
        }

        return sellerGoodsSku;
    }

//    public SellerGoodsSkuDto findSellerGoodSku1(SellerGoods sellerGoods, String skuId) {
//        SellerGoodsSkuDto sellerGoodsSku = new SellerGoodsSkuDto();
//        sellerGoodsSku.setSkuId(skuId);
//        sellerGoodsSku.setSystemPrice(sellerGoods.getSystemPrice());
//        sellerGoodsSku.setDiscountPrice(sellerGoods.getDiscountPrice());
//        sellerGoodsSku.setSellingPrice(sellerGoods.getSellingPrice());
//
//        String skuInfoJson = sellerGoods.getSkuInfo();
//        if (StrUtil.isNotBlank(skuInfoJson)) {
//            TypeReference paramType = new TypeReference<List<SellerGoodsSkuInfoVO>>() {};
//            List<SellerGoodsSkuInfoVO> skuInfoList = (List<SellerGoodsSkuInfoVO>)JsonUtils.readValue(skuInfoJson.trim(), paramType);
//            if (CollectionUtil.isNotEmpty(skuInfoList)) {
//                SellerGoodsSkuInfoVO oneSkuInfo = skuInfoList.get(0);
//                sellerGoodsSku.setSkuId(oneSkuInfo.getSkuId());
//                sellerGoodsSku.setSystemPrice(oneSkuInfo.getSystemPrice().doubleValue());
//                sellerGoodsSku.setDiscountPrice(oneSkuInfo.getDiscountPrice().doubleValue());
//                sellerGoodsSku.setSellingPrice(oneSkuInfo.getSellingPrice().doubleValue());
//            }
//        }
//
//        Date now = new Date();
//        Date discountStartTime = sellerGoods.getDiscountStartTime();
//        Date discountEndTime = sellerGoods.getDiscountEndTime();
//        if (discountStartTime == null
//                || discountEndTime == null
//                || !discountStartTime.before(now)
//                || !discountEndTime.after(now)) {
//            sellerGoodsSku.setDiscountPrice(null);
//        }
//
//        return sellerGoodsSku;
//    }


    @Override
    public SellerGoodsSkuInfoVO findCachedSellerGoodSku(SellerGoods sellerGoods, String skuId) {
        Cache<String, SellerGoodsSkuInfoVO> sellerGoodsSkuCache = LocalCachePool.buildCache(LocalCacheBucketKey.SellerGoodsSkuCache, 1024, 300, null);
        String cacheKey = sellerGoods.getId() + ":" + skuId;
        SellerGoodsSkuInfoVO cachedValue = sellerGoodsSkuCache.getIfPresent(cacheKey);
        if (cachedValue != null) {
            log.info("----> findCachedSellerGoodSku 参数: sellerGoodsId:" + sellerGoods.getId() + ", skuId:" + skuId + " 命中缓存");
            return cachedValue;
        }

        // 取代 TODO
        cachedValue = findSellerGoodSku(sellerGoods, skuId);
        //cachedValue = findSellerGoodSku(sellerGoods, skuId);
        if (cachedValue != null) {
            sellerGoodsSkuCache.put(cacheKey, cachedValue);
        }

        return cachedValue;
    }

//    @Override
//    public List<SellerGoodsSku> listSellerGoodsSku(String sellerGoodsId) {
//        DetachedCriteria query = DetachedCriteria.forClass(SellerGoodsSku.class);
//        query.add(Property.forName("sellerGoodsId").eq(sellerGoodsId));
//        query.add(Property.forName("isDelete").eq(0));
//        query.addOrder(Order.asc("createTime"));
//        // 查询总条数
//        List<SellerGoodsSku> sellerGoodsSkuList = (List<SellerGoodsSku>) this.getHibernateTemplate().findByCriteria(query);
//        return sellerGoodsSkuList;
//    }

    @Override
    public List<SellerGoodsSkuInfoVO> listSellerGoodsSku(String sellerGoodsId) {
        List<SellerGoodsSkuInfoVO> skuDtoList = new ArrayList<>();
        SellerGoods sellerGoods = this.getSellerGoods(sellerGoodsId);
        if (sellerGoods == null) {
            return skuDtoList;
        }

        String systemGoodsId = sellerGoods.getGoodsId();
        //SystemGoods systemGoods = this.getSystemGoods(systemGoodsId);

        DetachedCriteria query = DetachedCriteria.forClass(GoodsSku.class);
        query.add(Property.forName("goodId").eq(systemGoodsId));
        query.add(Property.forName("deleted").eq(0));
        // 查询总条数
        List<GoodsSku> goodsSkuList = (List<GoodsSku>) this.getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(goodsSkuList)) {
            return skuDtoList;
        }

        for (GoodsSku oneGoodsSku : goodsSkuList) {
            SellerGoodsSkuInfoVO oneSkuDto = new SellerGoodsSkuInfoVO();
            skuDtoList.add(oneSkuDto);

            double systemPrice = sellerGoods.getSystemPrice();
            BigDecimal goodsSkuPrice = oneGoodsSku.getPrice();
            if (goodsSkuPrice != null) {
                systemPrice = goodsSkuPrice.doubleValue();
            }
            Double profitRatio = sellerGoods.getProfitRatio();
            double sellingPrice = Arith.mul(systemPrice, Arith.add(1.00D, profitRatio));
            oneSkuDto.setSkuId(oneGoodsSku.getId().toString());
            oneSkuDto.setSellerId(sellerGoods.getSellerId());
            oneSkuDto.setSystemGoodsId(sellerGoods.getGoodsId());
            oneSkuDto.setSellerGoodsId(sellerGoods.getId().toString());
            oneSkuDto.setCategoryId(sellerGoods.getCategoryId());
            oneSkuDto.setSystemPrice(systemPrice);
            oneSkuDto.setSellingPrice(sellingPrice);
            oneSkuDto.setProfitRatio(sellerGoods.getProfitRatio());
            if (sellerGoods.getDiscountRatio() != null) {
                Double discountRatio = sellerGoods.getDiscountRatio();
                oneSkuDto.setDiscountPrice(Arith.mul(sellingPrice, Arith.sub(1.00D, discountRatio)));
                oneSkuDto.setDiscountRatio(discountRatio);
            }
        }

        return skuDtoList;
    }

    @Override
    public int updateCleanView(String sellerId, String sellerGoodsId, String SellerId,Date limitTime) {
        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        StringBuffer sql =new StringBuffer(" delete from T_MALL_SELLER_GOODS_STATISTICS WHERE CREATE_TIME <=:limitTime");
        Map<String, Object> parameters = new HashMap();
        if (StrUtil.isNotBlank(sellerId)) {
            sql.append(" and SELLER_ID = :sellerId ");
            parameters.put("sellerId",sellerId);
        }
        if (StrUtil.isNotBlank(sellerGoodsId)) {
            sql.append(" and GOODS_ID = :sellerGoodsId ");
            parameters.put("sellerGoodsId",sellerGoodsId);
        }
        String needToClearTime = DateUtils.toString(limitTime);
        parameters.put("limitTime",needToClearTime);
        NativeQuery sqlQuery = currentSession.createSQLQuery(sql.toString());
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            sqlQuery.setParameter(entry.getKey(), entry.getValue());
        }
        return sqlQuery.executeUpdate();
    }

    @Override
    public void updateSellerPriceByGoodsId(String goodsId, Double systemPrice) {
        logger.info("跟新了后台商品:" + goodsId + "进货价数据:" + systemPrice);

        DetachedCriteria goodsQuery = DetachedCriteria.forClass(SellerGoods.class);
        goodsQuery.add(Property.forName("goodsId").eq(goodsId));
        List<SellerGoods> sellerGoodsList = (List<SellerGoods>) getHibernateTemplate().findByCriteria(goodsQuery);

        if (CollectionUtil.isEmpty(sellerGoodsList)) {
            return;
        }

        for (SellerGoods sellerGoods : sellerGoodsList) {

            Double profitRatio = sellerGoods.getProfitRatio();
            if (Objects.nonNull(profitRatio)) {
                double sellingPrice = Arith.mul(systemPrice,
                        Arith.add(1.00D, profitRatio), 2);
                sellerGoods.setSellingPrice(sellingPrice);
            }

            Double discountRatio = sellerGoods.getDiscountRatio();

            if (Objects.nonNull(discountRatio)) {
                Double sellingPrice = sellerGoods.getSellingPrice();
                sellerGoods.setDiscountPrice(Arith.mul(sellingPrice,
                        Arith.sub(1.00, discountRatio), 2));
                sellerGoods.setDiscountRatio(discountRatio);
            }

            sellerGoods.setSystemPrice(systemPrice);

            logger.info("开始更新商家商品信息:" + sellerGoods.getId() +
                    ":售卖价格是:" + sellerGoods.getSellingPrice() + "折扣价格是:" + sellerGoods.getDiscountPrice());
            getHibernateTemplate().update(sellerGoods);
//            getHibernateTemplate().flush();
        }
    }

    @Override
    public void updateSellerGoodsValid(String goodsId, Integer isValid) {
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("goodsId").eq(goodsId));
        List<SellerGoods> results = (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isNotEmpty(results)) {
            for (SellerGoods goods : results) {
                goods.setIsShelf(isValid);
                goods.setIsValid(isValid);
                this.getHibernateTemplate().update(goods);
            }
        }
    }

    @Override
    public void updateSellerGoodsCategory(String goodsId, String newCategoryId, String newSecondaryCategoryId) {
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("goodsId").eq(goodsId));
        List<SellerGoods> results = (List<SellerGoods>) this.getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isNotEmpty(results)) {
            for (SellerGoods sellerGoods : results) {
                sellerGoods.setCategoryId(newCategoryId);
                sellerGoods.setSecondaryCategoryId(newSecondaryCategoryId);
                this.getHibernateTemplate().update(sellerGoods);
            }
        }
    }

    @Override
    public SystemGoodsLang selectGoodsLang(String lang, String goodsId) {
        String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + goodsId);
        SystemGoodsLang goodsLang = null;
        if (StrUtil.isBlank(js) && lang.equals(LanguageEnum.EN.getLang())) {
            js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + LanguageEnum.EN.getLang() + ":" + goodsId);
        }
        if (!StringUtils.isEmptyString(js)) {
            goodsLang = JSONObject.parseObject(js, SystemGoodsLang.class);
        } else {
            DetachedCriteria criteria = DetachedCriteria.forClass(SystemGoodsLang.class);
            criteria.add(Restrictions.eq("goodsId", goodsId));
            criteria.add(Restrictions.eq("lang", lang));
            criteria.add(Restrictions.eq("type", 0));
            List<SystemGoodsLang> result = (List<SystemGoodsLang>) getHibernateTemplate().findByCriteria(criteria);
            if (CollectionUtil.isNotEmpty(result)) {
                goodsLang = result.get(0);
                this.redisHandler.setSyncString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + goodsId, JSONObject.toJSONString(goodsLang));
            }
        }
        return goodsLang;
    }

    @Override
    //@Transactional
    public int[] updateBatchShowWeight1(final List<GoodsShowWeight> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            return new int[0];
        }

        String sql = "UPDATE T_MALL_SELLER_GOODS SET SHOW_WEIGHT1=? WHERE UUID=?";
        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, dataList.get(i).getWeight());
                ps.setString(2, dataList.get(i).getGoodsId());
            }

            @Override
            public int getBatchSize() {
                return dataList.size();
            }
        });

        return batchUpdate;
    }

    @Override
    @Transactional
    public void updateBatchShowWeight2(final List<GoodsShowWeight> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }

        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        currentSession.doWork(connection -> {
            try {
                for (GoodsShowWeight data : dataList) {
                    updateShowWeight2(currentSession, data);
                }
            } catch (Exception e) {
                throw new RuntimeException("updateBatchShowWeight2 failed e ", e);
            }
        });
    }

    private void updateShowWeight2(Session session, GoodsShowWeight data) {
        CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
        CriteriaUpdate<SellerGoods> update = criteriaBuilder.createCriteriaUpdate(SellerGoods.class);
        Root<SellerGoods> root = update.from(SellerGoods.class);

        update.set(root.get("showWeight2"), data.getWeight());
        update.where(criteriaBuilder.equal(root.get("id"), data.getGoodsId()));

        session.createQuery(update).executeUpdate();
    }


    @Override
    public List<SellerGoods> listDiscountSellerGoods(int pageNum, int pageSize) {
        Date now = new Date();
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("isValid").eq(1));
        query.add(Property.forName("isShelf").eq(1));
        query.add(Property.forName("discountRatio").gt(0.0));
        query.add(Property.forName("discountRatio").lt(1.0));
        query.add(Property.forName("discountEndTime").gt(now));

        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

    /**
     * 临时使用.
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<SellerGoods> pagedAllSellerGoods(int pageNum, int pageSize) {
        Date now = new Date();
        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        //query.add(Property.forName("isValid").eq(1));
        //query.add(Property.forName("isShelf").eq(1));

        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

    /**
     *
     * @param limitTime
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public List<SellerGoods> pagedNewSellerGoods(long limitTime, int pageNum, int pageSize) {
        if (pageNum <= 1) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 20;
        }

        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        query.add(Property.forName("isValid").eq(1));
        query.add(Property.forName("isShelf").eq(1));
        query.add(Property.forName("firstShelfTime").gt(limitTime)); // 要使用 gt，有时会设置参数 limitTime = 0

        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

    @Override
    public List<SellerGoods> pagedOldSellerGoods(long limitTime, int pageNum, int pageSize) {
        if (pageNum <= 1) {
            pageNum = 1;
        }
        if (pageSize <= 0) {
            pageSize = 20;
        }

        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        //query.add(Property.forName("isValid").eq(1));
        //query.add(Property.forName("isShelf").eq(1));
        query.add(Property.forName("showWeight1").le(limitTime));
        query.add(Property.forName("showWeight1").gt(0L));

        return (List<SellerGoods>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
    }

//    @Override
//    public void updateCachedSellerGoodSku(List<SellerGoodsSku> sellerGoodsSkuList) {
//        for (SellerGoodsSku sellerGoodsSku : sellerGoodsSkuList) {
//            Cache<String, SellerGoodsSku> sellerGoodsSkuCache = LocalCachePool.buildCache(LocalCacheBucketKey.SellerGoodsSkuCache, 1024, 300, null);
//            String cacheKey = sellerGoodsSku.getSellerGoodsId() + ":" + sellerGoodsSku.getSkuId();
//            sellerGoodsSkuCache.put(cacheKey, sellerGoodsSku);
//        }
//    }

    //    @Override
//    public void deleteCachedSellerGoodSku(List<SellerGoodsSku> sellerGoodsSkuList) {
//        for (SellerGoodsSku sellerGoodsSku : sellerGoodsSkuList) {
//            Cache<String, SellerGoodsSku> sellerGoodsSkuCache = LocalCachePool.buildCache(LocalCacheBucketKey.SellerGoodsSkuCache, 1024, 300, null);
//            String cacheKey = sellerGoodsSku.getSellerGoodsId() + ":" + sellerGoodsSku.getSkuId();
//            sellerGoodsSkuCache.invalidate(cacheKey);
//        }
//    }
    public void deleteCachedSellerGoodSku1(SellerGoods sellerGoods) {
        DetachedCriteria query = DetachedCriteria.forClass(GoodsSku.class);
        query.add(Property.forName("goodId").eq(sellerGoods.getGoodsId()));
        List<GoodsSku> systemGoodsSkuList = (List<GoodsSku>) this.getHibernateTemplate().findByCriteria(query);

        for (GoodsSku goodsSku : systemGoodsSkuList) {
            Cache<String, SellerGoodsSkuInfoVO> sellerGoodsSkuCache = LocalCachePool.buildCache(LocalCacheBucketKey.SellerGoodsSkuCache, 1024, 300, null);
            String cacheKey = sellerGoods.getId().toString() + ":" + goodsSku.getId();
            sellerGoodsSkuCache.invalidate(cacheKey);
        }
    }

    @Override
    public void updateDisProBatchBatch(List<String> goodsIds, String sellerId, boolean has_discount, Date discountStartTime, Date discountEndTime, double discount_ratio, double profit_ratio) {
        if (CollectionUtil.isEmpty(goodsIds) || StringUtils.isEmptyString(sellerId)) {
            log.error("批量更新店铺商品利润率折扣率，传入参数不对:{},{},{}", goodsIds, sellerId);
            return;
        }
        NamedParameterJdbcTemplate nameJdbc = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("sellerId", sellerId);
        params.put("sellerGoodsIdList", goodsIds);
        params.put("discountStartTime", discountStartTime);
        params.put("discountEndTime", discountEndTime);
        params.put("discount_ratio", discount_ratio);
        params.put("profit_ratio", profit_ratio);

        String sql = "";
        if (has_discount) {
            if (discount_ratio > 0.00D && discount_ratio < 1.00) {
                sql = "UPDATE T_MALL_SELLER_GOODS SET DISCOUNT_RATIO=:discount_ratio,DISCOUNT_PRICE=SYSTEM_PRICE*(1+:profit_ratio)*(1-:discount_ratio)," +
                        "DISCOUNT_START_TIME=:discountStartTime,DISCOUNT_END_TIME=:discountEndTime,PROFIT_RATIO=:profit_ratio,SELLING_PRICE=SYSTEM_PRICE*(1+:profit_ratio) WHERE UUID in (:sellerGoodsIdList) and SELLER_ID = :sellerId";
            } else if (discount_ratio == 0) {
                sql = "UPDATE T_MALL_SELLER_GOODS SET DISCOUNT_RATIO=0,DISCOUNT_PRICE=NULL,DISCOUNT_START_TIME=NULL,DISCOUNT_END_TIME=NULL,PROFIT_RATIO=:profit_ratio,SELLING_PRICE=SYSTEM_PRICE*(1+:profit_ratio) " +
                        " WHERE UUID in (:sellerGoodsIdList) and SELLER_ID = :sellerId ";
            }
        } else {
            sql = "UPDATE T_MALL_SELLER_GOODS SET DISCOUNT_RATIO=0,DISCOUNT_PRICE=NULL,DISCOUNT_START_TIME=NULL,DISCOUNT_END_TIME=NULL,PROFIT_RATIO=:profit_ratio,SELLING_PRICE=SYSTEM_PRICE*(1+:profit_ratio) " +
                    " WHERE UUID in (:sellerGoodsIdList) and SELLER_ID = :sellerId ";
        }
        nameJdbc.update(sql, params);
    }

    public int[] updateBatchGoodsTitle(final List<SystemGoods> dataList) {
        if (CollectionUtil.isEmpty(dataList)) {
            return new int[0];
        }

        String sql = "UPDATE T_MALL_SYSTEM_GOODS SET MULTI_LANGS = ?, CN_TITLE = ?, EN_TITLE = ?, ZH_TITLE = ? WHERE UUID=?";
        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, dataList.get(i).getMultiLangs());
                ps.setString(2, dataList.get(i).getCnTitle());
                ps.setString(3, dataList.get(i).getEnTitle());
                ps.setString(4, dataList.get(i).getZhTitle());
                ps.setString(5, dataList.get(i).getId().toString());
            }

            @Override
            public int getBatchSize() {
                return dataList.size();
            }
        });

        return batchUpdate;
    }

    public int[] updateBatchGoodsSku(final List<SellerGoods> dataList) {
        return new int[0];
//        if (CollectionUtil.isEmpty(dataList)) {
//            return new int[0];
//        }
//
//        String sql = "UPDATE T_MALL_SELLER_GOODS SET SKU_INFO = ? WHERE UUID=?";
//        int[] batchUpdate = jdbcTemplate.batchUpdate(sql, new BatchPreparedStatementSetter() {
//            @Override
//            public void setValues(PreparedStatement ps, int i) throws SQLException {
//                ps.setString(1, dataList.get(i).getSkuInfo());
//                ps.setString(2, dataList.get(i).getId().toString());
//            }
//
//            @Override
//            public int getBatchSize() {
//                return dataList.size();
//            }
//        });
//
//        return batchUpdate;
    }

    @Override
    public MallPageInfo tmpPageListAllSystemGoods(int pageNum, int pageSize, String categoryId, String secondaryCategoryId) {
        DetachedCriteria query = DetachedCriteria.forClass(SystemGoods.class);
        if (StringUtils.isNotEmpty(categoryId) && !Objects.equals(categoryId, "0")) {
            query.add(Property.forName("categoryId").eq(categoryId));
        }
        if (StringUtils.isNotEmpty(secondaryCategoryId) && !Objects.equals(secondaryCategoryId, "0")) {
            query.add(Property.forName("secondaryCategoryId").eq(secondaryCategoryId));
        }

        // 不设置总条数
        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, 100000L, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }

    @Override
    public List<SystemGoodsLang> listSystemGoodsLang(List<String> goodsIdList, String lang) {
        DetachedCriteria langCriteria = DetachedCriteria.forClass(SystemGoodsLang.class);
        // 给criteria添加条件，引号中status为实体中的字段，后一个为值
        langCriteria.add(Restrictions.in("goodsId", goodsIdList));
        if (StrUtil.isNotBlank(lang)) {
            langCriteria.add(Restrictions.eq("lang", "en"));
        }
        List<SystemGoodsLang> langList = (List<SystemGoodsLang>) this.getHibernateTemplate().findByCriteria(langCriteria);
        return langList;
    }

//    @Override
//    public List<SellerGoodsSku> listSellerGoodsSku(List<String> sellerGoodsIdList) {
//        if (CollectionUtil.isEmpty(sellerGoodsIdList)) {
//            return new ArrayList<>();
//        }
//        DetachedCriteria criteria = DetachedCriteria.forClass(SellerGoodsSku.class);
//        criteria.add(Restrictions.in("sellerGoodsId", sellerGoodsIdList));
//        List<SellerGoodsSku> sellerSkuList = (List<SellerGoodsSku>) this.getHibernateTemplate().findByCriteria(criteria);
//        return sellerSkuList;
//    }

    //@Override 废弃
    public List<SellerGoods> listSellerGoodsBySku(String systemGoodsId, String skuId) {
        List<SellerGoods> retGoodsList = new ArrayList<>();
        if (StrUtil.isBlank(skuId) || Objects.equals(skuId, "0")) {
            return retGoodsList;
        }

        DetachedCriteria query = DetachedCriteria.forClass(SellerGoods.class);
        // TODO
//        query.add(Restrictions.eq("isShelf", 1));
//        query.add(Restrictions.eq("isValid", 1));

        if (StrUtil.isNotBlank(systemGoodsId) && !Objects.equals(systemGoodsId, "0")) {
            query.add(Property.forName("goodsId").eq(systemGoodsId.trim()));
        }
        query.add(Property.forName("skuInfo").like(skuId.trim(), MatchMode.ANYWHERE));
        List<SellerGoods> oriSellerGoodsList = (List<SellerGoods>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(oriSellerGoodsList)) {
            return retGoodsList;
        }

//        for (SellerGoods oneSellerGoods : oriSellerGoodsList) {
//            String skuInfoJson = oneSellerGoods.getSkuInfo();
//            if (StrUtil.isNotBlank(skuInfoJson)) {
//                TypeReference paramType = new TypeReference<List<SellerGoodsSkuInfoVO>>() {};
//                List<SellerGoodsSkuInfoVO> skuInfoList = (List<SellerGoodsSkuInfoVO>)JsonUtils.readValue(skuInfoJson.trim(), paramType);
//                oneSellerGoods.setSkuInfoList(skuInfoList);
//
//                for (SellerGoodsSkuInfoVO oneSkuInfo : skuInfoList) {
//                    if (oneSkuInfo.getSkuId().equals(skuId)) {
//                        retGoodsList.add(oneSellerGoods);
//                        break;
//                    }
//                }
//            }
//        }

        return retGoodsList;
    }

    @Data
    private static class GoodsViewAdd {
        private String goodsId;
        private Long viewsAdd;
        private String sellerId;
        private String dateStr;
    }


    private String getKey(String fromTime, String toTime, String key) {
        String from = fromTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String to = toTime.replace("-", "")
                .replace(":", "")
                .replace("000000", "")
                .replace(" ", "");

        String funKey = key + ":" + from + "-" + to;

        return funKey;
    }
}
