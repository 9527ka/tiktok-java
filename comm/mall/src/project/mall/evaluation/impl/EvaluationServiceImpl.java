package project.mall.evaluation.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.*;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.Constants;
import project.mall.evaluation.EvaluationService;
import project.mall.event.message.EvaluationOrderGoodsEvent;
import project.mall.event.model.OrderGoodsEvaluationInfo;
import project.mall.goods.model.Evaluation;
import project.mall.goods.vo.SoldGoodsCount;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersGoods;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.orders.vo.MallOrderVO;
import project.mall.orders.vo.OrderGoodsVO;
import project.mall.orders.vo.SellerGoodsSkuVO;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;
import project.party.PartyService;
import project.party.model.Party;
import project.web.api.model.EvaluationAddListModel;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

@Slf4j
public class EvaluationServiceImpl extends HibernateDaoSupport implements EvaluationService {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private PartyService partyService;

    private GoodsOrdersService goodsOrdersService;

    private JdbcTemplate jdbcTemplate;

    public void setPartyService(PartyService partyService) {

        this.partyService = partyService;
    }

    public PartyService getPartyService() {

        return partyService;
    }

    public void setGoodsOrdersService(GoodsOrdersService goodsOrdersService) {

        this.goodsOrdersService = goodsOrdersService;
    }

    public GoodsOrdersService getGoodsOrdersService() {

        return goodsOrdersService;
    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * 买家端查看指定商品的评论列表
     *
     * @param pageNum
     * @param pageSize
     * @param sellerGoodsId
     * @param evaluationType
     * @return
     */
    @Override
    public MallPageInfo listEvaluation(int pageNum, int pageSize, String sellerGoodsId, String evaluationType) {
        DetachedCriteria query = DetachedCriteria.forClass(Evaluation.class);
        if (sellerGoodsId != null) {
            query.add(Property.forName("sellerGoodsId").eq(sellerGoodsId));
        }
        if (StrUtil.isNotEmpty(evaluationType)) {
            if (evaluationType.equals("-2")) {
//                query.add(Property.forName("imgUrl1").isNotEmpty());//isNotEmpty为查列表方法,这里查字段使用会报错
                query.add(Restrictions.and(Property.forName("imgUrl1").isNotNull(), Property.forName("imgUrl1").ne("")));
            } else {
                query.add(Property.forName("evaluationType").eq(Integer.valueOf(evaluationType)));
            }
        }
        query.add(Property.forName("status").eq(0));
        query.addOrder(Order.desc("evaluationTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

        return mallPageInfo;
    }


    @Override
    public Map<String, String> getEvaluationTypeCountByGoodId(String goodId) {

        String sql = "SELECT COUNT(*) as 'count',EVALUATION_TYPE as 'evaluationType' FROM T_MALL_EVALUATION   WHERE  SELLER_GOODS_ID='" + goodId + "'  GROUP BY EVALUATION_TYPE";
        List list = this.jdbcTemplate.queryForList(sql);
        Iterator iterable = list.iterator();
        Map<String, String> map = new HashMap<>();
        while (iterable.hasNext()) {
            Map rowMap = (Map) iterable.next();
            Integer evaluationType = (Integer) rowMap.getOrDefault("evaluationType", "0");
            Long count = (Long) rowMap.getOrDefault("count", "0");
            map.put(evaluationType.intValue() + "", count + "");
        }

        String imgSql = "SELECT COUNT(1) as 'count' FROM T_MALL_EVALUATION   WHERE  SELLER_GOODS_ID='" + goodId + "'  AND  IMG_URL_1  IS  NOT NULL  AND  IMG_URL_1!='' AND STATUS = 0  ";
        Integer imgCount = this.jdbcTemplate.queryForObject(imgSql, Integer.class);
        map.put("imgCount", imgCount + "");
        return map;
    }

    /**
     * 针对指定一批商品，统计其各类分数的评论总数.
     *
     * @param goodIdList
     * @return
     */
    public Map<String, Map<String, Integer>> getEvaluationTypeCountByGoodIds(List<String> goodIdList) {
        Map<String, Map<String, Integer>> evaluationScoreMap = new HashMap();
        if (CollectionUtil.isEmpty(goodIdList)) {
            return evaluationScoreMap;
        }

        StringBuffer goodsIdsBuf = new StringBuffer();
        for (String oneGoodsId : goodIdList) {
            goodsIdsBuf.append("'").append(oneGoodsId.trim()).append("',");
        }
        goodsIdsBuf.deleteCharAt(goodsIdsBuf.length() - 1);

        String sql = "SELECT SELLER_GOODS_ID as goodsId, EVALUATION_TYPE as evaluationType, COUNT(*) as totalNum " +
                " FROM T_MALL_EVALUATION " +
                " WHERE SELLER_GOODS_ID in (" + goodsIdsBuf.toString() + ") and STATUS=0 " +
                " GROUP BY SELLER_GOODS_ID, EVALUATION_TYPE";

        List list = this.jdbcTemplate.queryForList(sql);
        Iterator iterable = list.iterator();
        while (iterable.hasNext()) {
            Map rowMap = (Map) iterable.next();
            String sellerGoodsId = (String) rowMap.getOrDefault("goodsId", "");
            Integer evaluationType = (Integer) rowMap.getOrDefault("evaluationType", "0");
            // 使用 integer 接会报转化错误
            Long totalNum = (Long) rowMap.getOrDefault("totalNum", "0");

            Map<String, Integer> oneItemMap = evaluationScoreMap.get(sellerGoodsId);
            if (oneItemMap == null) {
                oneItemMap = new HashMap<>();
                evaluationScoreMap.put(sellerGoodsId, oneItemMap);
            }

            oneItemMap.put(String.valueOf(evaluationType), totalNum.intValue());
        }

        return evaluationScoreMap;
    }


    public MallPageInfo listEvaluations(int pageNum, int pageSize, String sellerGoodsId, String userName, String evaluationType) {
        DetachedCriteria query = DetachedCriteria.forClass(Evaluation.class);
        int evaluationTypeInt = 0;
        if (StrUtil.isNotBlank(evaluationType)) {
            evaluationTypeInt = Integer.valueOf(evaluationType);
        }

        if (sellerGoodsId != null) {
            query.add(Property.forName("sellerGoodsId").eq(sellerGoodsId));
        }
        if (StrUtil.isNotEmpty(userName)) {
            query.add(Property.forName("userName").eq(userName));
        }
        if (evaluationTypeInt > 0) {
            query.add(Property.forName("evaluationType").eq(evaluationTypeInt));
        }

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        List<Evaluation> pageList = (List<Evaluation>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);

        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, pageList);

        return mallPageInfo;
    }

    @Override
    public MallPageInfo listEvaluationBySellerId(int pageNum, int pageSize, String sellerId, String userName, Integer evaluationType, Integer sourceType, String systemGoodsId) {
        DetachedCriteria query = DetachedCriteria.forClass(Evaluation.class);
        if (sellerId != null) {
            query.add(Property.forName("sellerId").eq(sellerId));
        }
        if (StringUtils.isNotEmpty(userName)) {
            query.add(Property.forName("userName").like(userName, MatchMode.ANYWHERE));
        }
        if (evaluationType != null && evaluationType > 0) {
            query.add(Property.forName("evaluationType").eq(evaluationType));
        }
        if (sourceType != null && sourceType > 0) {
            query.add(Property.forName("sourceType").eq(sourceType));
        }
        if (StrUtil.isNotEmpty(systemGoodsId) && !Objects.equals(systemGoodsId, "0")) {
            query.add(Property.forName("systemGoodsId").eq(systemGoodsId));
        }
        query.add(Property.forName("status").eq(0));
        query.addOrder(Order.desc("evaluationTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        List<Evaluation> pageList = (List<Evaluation>) getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize);
        MallPageInfo mallPageInfo = MallPageInfoUtil.getMallPage(pageSize, pageNum, totalCount, pageList);

        return mallPageInfo;
    }

    @Override
    public void addSystemEvaluation(Evaluation evaluation) {
        MallOrdersPrize mallOrdersPrize = goodsOrdersService.getMallOrdersPrize(evaluation.getOrderId());
        if (mallOrdersPrize == null) {
            throw new BusinessException("订单不存在!");
        }
        if (evaluation.getCreateTime() == null) {
            evaluation.setCreateTime(new Date());
        }

        log.info("开始添加评论信息订单ID:" + evaluation.getOrderId() + "商品ID:" + evaluation.getSellerGoodsId());
        getHibernateTemplate().save(evaluation);

        // 需要确认在本方法未返回前执行查询，能否找到该新增的评论记录
        // 发布一个订单评论的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        OrderGoodsEvaluationInfo info = new OrderGoodsEvaluationInfo();
        info.setOrderId(evaluation.getOrderId());
        info.setSellerGoodsId(evaluation.getSellerGoodsId());
        info.setSystemGoodsId(evaluation.getSystemGoodsId());
        info.setScore(evaluation.getRating());
        if (evaluation.getId() != null) {
            info.setEvaluationId(evaluation.getId().toString());
        }
        wac.publishEvent(new EvaluationOrderGoodsEvent(this, info));
    }

    @Override
    public Evaluation getOrderEvaluation(String partyId, String orderId) {

        DetachedCriteria criteria = DetachedCriteria.forClass(Evaluation.class);
        criteria.add(Property.forName("partyId").eq(partyId));
        criteria.add(Property.forName("orderId").eq(orderId));
        List<Evaluation> evaluations = (List<Evaluation>) getHibernateTemplate().findByCriteria(criteria);
        if (!CollectionUtils.isEmpty(evaluations)) {
            return evaluations.get(0);
        }

        return null;
    }

    @Override
    @Transactional
    public void addEvaluation(List<EvaluationAddListModel.EvaluationAddModel> evaluationAdds, String partyId, String orderId) {

        DetachedCriteria criteria = DetachedCriteria.forClass(Evaluation.class);
        criteria.add(Property.forName("partyId").eq(partyId));
        criteria.add(Property.forName("orderId").eq(orderId));
        List<Evaluation> evaluations = (List<Evaluation>) getHibernateTemplate().findByCriteria(criteria);
        if (!CollectionUtils.isEmpty(evaluations)) {
            throw new BusinessException("已提交评论!");
        }
        for (EvaluationAddListModel.EvaluationAddModel model : evaluationAdds) {
            Date now = new Date();
            Evaluation evaluation = new Evaluation();
            evaluation.setImgUrl1(model.getImgUrl1());
            evaluation.setImgUrl2(model.getImgUrl2());
            evaluation.setImgUrl3(model.getImgUrl3());
            evaluation.setImgUrl4(model.getImgUrl4());
            evaluation.setImgUrl5(model.getImgUrl5());
            evaluation.setImgUrl6(model.getImgUrl6());
            evaluation.setImgUrl7(model.getImgUrl7());
            evaluation.setImgUrl8(model.getImgUrl8());
            evaluation.setImgUrl9(model.getImgUrl9());

            evaluation.setEvaluationType(Integer.valueOf(model.getEvaluationType()));
            evaluation.setSellerGoodsId(model.getSellerGoodsId());
            evaluation.setRating(Integer.valueOf(model.getRating()));
//            评论内容如果全为空格去除掉
            evaluation.setContent(StringUtils.isEmptyString(model.getContent()) ? "" : model.getContent().trim());
//        evaluation.setPartyId(partyId);
            Party party = partyService.cachePartyBy(partyId, false);
            MallOrdersPrize mallOrdersPrize = goodsOrdersService.getMallOrdersPrize(orderId);
            if (mallOrdersPrize == null) {
                throw new BusinessException("订单不存在!");
            }
//            评论增加属性配置
            MallOrdersGoods mallOrdersGoods = goodsOrdersService.getMallOrdersGoods(orderId, model.getSellerGoodsId());
            if (Objects.nonNull(mallOrdersGoods) && Objects.nonNull(mallOrdersGoods.getSkuId())) {
                evaluation.setSkuId(mallOrdersGoods.getSkuId());
            }
//            评论增加国家code
            evaluation.setCountryId(mallOrdersPrize.getCountryId());
            evaluation.setOrderId(orderId);
            evaluation.setSellerId(mallOrdersPrize.getSellerId());
            evaluation.setUserName(party.getUsername());
            evaluation.setCreateTime(now);
            if (mallOrdersGoods != null) {
                evaluation.setSystemGoodsId(mallOrdersGoods.getSystemGoodsId());
            }

            // 2023-1-16 caster 添加
            evaluation.setPartyId(party.getId().toString());
            evaluation.setPartyName(party.getName());
            evaluation.setPartyAvatar(party.getAvatar());
            evaluation.setTemplate("0");// TODO
            evaluation.setSourceType(1);
            if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_GUEST)) {
                evaluation.setSourceType(2);
            } else if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_TEST)) {
                evaluation.setSourceType(3);
            }
            evaluation.setEvaluationTime(now);
            evaluation.setGoodsStatus(1);

            getHibernateTemplate().save(evaluation);

            mallOrdersPrize.setStatus(5);
            mallOrdersPrize.setUpTime(System.currentTimeMillis());
            mallOrdersPrize.setHasComment(1);
            getHibernateTemplate().update(mallOrdersPrize);

            // 发布一个订单评论的事件
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            OrderGoodsEvaluationInfo info = new OrderGoodsEvaluationInfo();
            info.setOrderId(evaluation.getOrderId());
            info.setSellerGoodsId(evaluation.getSellerGoodsId());
            info.setSystemGoodsId(evaluation.getSystemGoodsId());
            info.setScore(evaluation.getRating());
            if (evaluation.getId() != null) {
                info.setEvaluationId(evaluation.getId().toString());
            }
            wac.publishEvent(new EvaluationOrderGoodsEvent(this, info));
        }

    }

    @Override
    public Long getEvaluationNumBySellerGoodsIds(List<String> countEvaluation) {
        DetachedCriteria query = DetachedCriteria.forClass(Evaluation.class);
        query.add(Property.forName("sellerGoodsId").in(countEvaluation));
        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        return totalCount;
    }

    @Override
    @Transactional
    public void addEvaluation(String partyId, String sellerId, String sellerGoodsId, String evaluationType, String rating, String content, String orderId, String commentId, Evaluation bean) {
        Evaluation evaluation = new Evaluation();

        Date now = new Date();
        if (!Objects.isNull(bean)) {
            evaluation.setImgUrl1(bean.getImgUrl1());
            evaluation.setImgUrl2(bean.getImgUrl2());
            evaluation.setImgUrl3(bean.getImgUrl3());
            evaluation.setImgUrl4(bean.getImgUrl4());
            evaluation.setImgUrl5(bean.getImgUrl5());
            evaluation.setImgUrl6(bean.getImgUrl6());
            evaluation.setImgUrl7(bean.getImgUrl7());
            evaluation.setImgUrl8(bean.getImgUrl8());
            evaluation.setImgUrl9(bean.getImgUrl9());
        }

        evaluation.setEvaluationType(Integer.valueOf(evaluationType));
        evaluation.setSellerGoodsId(sellerGoodsId);
        evaluation.setRating(Integer.valueOf(rating));
        evaluation.setContent(StringUtils.isEmptyString(content) ? "" : content.trim());
        evaluation.setSellerId(sellerId);
//        evaluation.setPartyId(partyId);
        Party party = partyService.cachePartyBy(partyId, false);
        MallOrdersPrize mallOrdersPrize = goodsOrdersService.getMallOrdersPrize(orderId);
        if (mallOrdersPrize == null) {
            throw new BusinessException("订单不存在!");
        }
//        订单评价增加属性
        MallOrdersGoods mallOrdersGoods = goodsOrdersService.getMallOrdersGoods(orderId, sellerGoodsId);
        if (Objects.nonNull(mallOrdersGoods) && Objects.nonNull(mallOrdersGoods.getSkuId())) {
            evaluation.setSkuId(mallOrdersGoods.getSkuId());
        }
//        订单评价增加国家代码
        evaluation.setCountryId(mallOrdersPrize.getCountryId());
        evaluation.setOrderId(orderId);
        evaluation.setUserName(party.getUsername());
        evaluation.setCreateTime(now);
        if (mallOrdersGoods != null) {
            evaluation.setSystemGoodsId(mallOrdersGoods.getSystemGoodsId());
        }

        // 2023-1-16 caster 添加
        evaluation.setPartyId(party.getId().toString());
        evaluation.setPartyName(party.getName());
        evaluation.setPartyAvatar(party.getAvatar());
        evaluation.setTemplate(StrUtil.isBlank(commentId) ? "0" : commentId);
        evaluation.setSourceType(1);
        if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_GUEST)) {
            evaluation.setSourceType(2);
        } else if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_TEST)) {
            evaluation.setSourceType(3);
        }
        evaluation.setEvaluationTime(now);
        evaluation.setGoodsStatus(1);

        getHibernateTemplate().save(evaluation);

        mallOrdersPrize.setStatus(5);
        mallOrdersPrize.setHasComment(1);
        mallOrdersPrize.setUpTime(System.currentTimeMillis());
        getHibernateTemplate().update(mallOrdersPrize);

        // 发布一个订单评论的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        OrderGoodsEvaluationInfo info = new OrderGoodsEvaluationInfo();
        info.setOrderId(evaluation.getOrderId());
        info.setSellerGoodsId(evaluation.getSellerGoodsId());
        info.setSystemGoodsId(evaluation.getSystemGoodsId());
        info.setScore(evaluation.getRating());
        if (evaluation.getId() != null) {
            info.setEvaluationId(evaluation.getId().toString());
        }
        wac.publishEvent(new EvaluationOrderGoodsEvent(this, info));
    }

    /**
     * 增加虚假评论
     *
     * @param evaluation
     * @return
     */
    @Transactional
    public Evaluation addFakeEvaluation(Evaluation evaluation) {
        Date now = new Date();

        // 构造订单记录
        MallOrderVO fakeOrder = goodsOrdersService.saveFakeOrder("0", evaluation.getUserName(), evaluation.getSellerGoodsId());

//        // 获取 skuid
//        MallOrdersGoods mallOrdersGoods = goodsOrdersService.getMallOrdersGoods(fakeOrder.getId().toString(), evaluation.getSellerGoodsId());
//        if (Objects.nonNull(mallOrdersGoods)
//                && Objects.nonNull(mallOrdersGoods.getSkuId())
//                && (!"-1".equals(evaluation.getSkuId()))
//                && (!"0".equals(evaluation.getSkuId()))) {
//            evaluation.setSkuId(mallOrdersGoods.getSkuId());
//        }
        OrderGoodsVO fakeOrderGooods = null;
        SellerGoodsSkuVO goodsSku = null;
        if (fakeOrder != null && CollectionUtil.isNotEmpty(fakeOrder.getGoodsList())) {
            fakeOrderGooods = fakeOrder.getGoodsList().get(0);
            goodsSku = fakeOrderGooods.getGoodsSku();
        }
        if (Objects.nonNull(goodsSku)
                && Objects.nonNull(goodsSku.getId())
                && (!"-1".equals(goodsSku.getId()))
                && (!"0".equals(goodsSku.getId()))) {
            evaluation.setSkuId(goodsSku.getId());
        }
        if (fakeOrderGooods != null) {
            evaluation.setSystemGoodsId(fakeOrderGooods.getSystemGoodsId());
        }
//        订单评价增加国家属性
        evaluation.setCountryId(fakeOrder.getCountryId());

        // 构造虚假评论记录
        evaluation.setStatus(0);
        evaluation.setCreateTime(now);

        evaluation.setOrderId(fakeOrder.getId());
        evaluation.setPartyId("0");
        evaluation.setTemplate("0");
        evaluation.setSourceType(1);// TODO
        evaluation.setEvaluationTime(now);
        evaluation.setGoodsStatus(1);
        evaluation.setPartyName(evaluation.getUserName());

        getHibernateTemplate().save(evaluation);

        // 发布一个订单评论的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        OrderGoodsEvaluationInfo info = new OrderGoodsEvaluationInfo();
        info.setOrderId(evaluation.getOrderId());
        info.setSellerGoodsId(evaluation.getSellerGoodsId());
        info.setSystemGoodsId(evaluation.getSystemGoodsId());
        info.setScore(evaluation.getRating());
        if (evaluation.getId() != null) {
            info.setEvaluationId(evaluation.getId().toString());
        }
        wac.publishEvent(new EvaluationOrderGoodsEvent(this, info));

        return evaluation;
    }

    @Override
    public Float selectAvgEvaluationBySellerId(String sellerId) {
        Session session = getHibernateTemplate().getSessionFactory().getCurrentSession();
        Criteria criteria = session.createCriteria(Evaluation.class);  //定义Criteria对象
        criteria.add(Property.forName("sellerId").eq(sellerId));
        criteria.setProjection(Projections.avg("rating"));  //计算achievement列的平均值
        Object avg = criteria.uniqueResult();  //获取计算结果
        if (avg == null) {
            return 0f;
        }
        return new BigDecimal(Double.valueOf((Double) avg)).setScale(1, RoundingMode.HALF_UP).floatValue();
    }

    @Override
    public Double getHighOpinionBySellerId(String sellerId) {
//        return this.jdbcTemplate.queryForObject(
//                "SELECT " +
//                        " IFNULL(COUNT(*) / ( SELECT COUNT(*) FROM T_MALL_EVALUATION WHERE SELLER_ID = 1 ),0.00)  " +
//                        " FROM " +
//                        " T_MALL_EVALUATION  " +
//                        " WHERE " +
//                        " SELLER_ID = '" + sellerId +"'" +
//                        " AND EVALUATION_TYPE = 1", Double.class);
        Criteria goodsCriteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Evaluation.class);
        goodsCriteria.add(Restrictions.eq("sellerId", sellerId));
        goodsCriteria.add(Restrictions.eq("evaluationType", 1));
        goodsCriteria.setProjection(Projections.rowCount());
        Integer goodConst = ((Long) goodsCriteria.uniqueResult()).intValue();
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Evaluation.class);
        criteria.add(Restrictions.eq("sellerId", sellerId));
        criteria.setProjection(Projections.rowCount());
        Integer allConst = ((Long) criteria.uniqueResult()).intValue();
        if (goodConst.equals(0) || allConst.equals(0)) {
            return Double.valueOf(0);
        }
        return Arith.div(Double.valueOf(goodConst), Double.valueOf(allConst), 4);
    }

    @Override
    public Map<String, Double> getHighOpinionBySellerIds(List<String> sellerIdList) {
        Map<String, Double> sellerGoodEvaluationStatMap = new HashMap<>();
        if (CollectionUtil.isEmpty(sellerIdList)) {
            return sellerGoodEvaluationStatMap;
        }

        DetachedCriteria query = DetachedCriteria.forClass(Evaluation.class);
        // detachedCriteria.setProjection( Property.forName("evaluationType") )  //对于查询单个字段
        ProjectionList pr = Projections.projectionList()
                .add(Property.forName("id").as("id")) // 不能漏掉 as
                .add(Property.forName("sellerGoodsId").as("sellerGoodsId"))
                .add(Property.forName("sellerId").as("sellerId"))
                .add(Property.forName("evaluationType").as("evaluationType"));

        query.setProjection(pr);
        query.add(Property.forName("sellerId").in(sellerIdList));

        // 不能漏掉
        query.setResultTransformer(Transformers.aliasToBean(Evaluation.class));
        List<Evaluation> evaluationList = (List<Evaluation>)getHibernateTemplate().findByCriteria(query);

        // 商家好评统计
        Map<String, Integer> goodEvaluationCountMap = new HashMap<>();
        // 商家评论总数统计
        Map<String, Integer> allEvaluationCountMap = new HashMap<>();
        for (Evaluation oneEvaluation : evaluationList) {
            Integer allEvaluationCount = allEvaluationCountMap.get(oneEvaluation.getSellerId());
            if (allEvaluationCount == null) {
                allEvaluationCountMap.put(oneEvaluation.getSellerId(), 1);
            } else {
                allEvaluationCountMap.put(oneEvaluation.getSellerId(), allEvaluationCount + 1);
            }

            if (oneEvaluation.getEvaluationType() == 1) {
                Integer goodEvaluationCount = goodEvaluationCountMap.get(oneEvaluation.getSellerId());
                if (goodEvaluationCount == null) {
                    goodEvaluationCountMap.put(oneEvaluation.getSellerId(), 1);
                } else {
                    goodEvaluationCountMap.put(oneEvaluation.getSellerId(), goodEvaluationCount + 1);
                }
            }
        }

        for (String oneSellerId : sellerIdList) {
            Integer allEvaluationCount = allEvaluationCountMap.get(oneSellerId);
            if (allEvaluationCount == null || allEvaluationCount == 0) {
                sellerGoodEvaluationStatMap.put(oneSellerId, 0.0);
            } else {
                Integer goodEvaluationCount = goodEvaluationCountMap.get(oneSellerId);
                if (goodEvaluationCount == null) {
                    sellerGoodEvaluationStatMap.put(oneSellerId, 0.0);
                } else {
                    sellerGoodEvaluationStatMap.put(oneSellerId, Arith.div(Double.valueOf(goodEvaluationCount), Double.valueOf(allEvaluationCount), 4));
                }
            }
        }

        return sellerGoodEvaluationStatMap;
    }

    @Override
    public Double getHighOpinionByGoodsId(String goodsId) {
        Criteria goodsCriteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Evaluation.class);
        goodsCriteria.add(Restrictions.eq("sellerGoodsId", goodsId));
        goodsCriteria.add(Restrictions.eq("evaluationType", 1));
        goodsCriteria.setProjection(Projections.rowCount());
        Integer goodConst = ((Long) goodsCriteria.uniqueResult()).intValue();
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Evaluation.class);
        criteria.add(Restrictions.eq("sellerGoodsId", goodsId));
        criteria.setProjection(Projections.rowCount());
        Integer allConst = ((Long) criteria.uniqueResult()).intValue();

        return Arith.div(Double.valueOf(goodConst), Double.valueOf(allConst), 4);
//        return this.jdbcTemplate.queryForObject(
//                "SELECT " +
//                        " IFNULL(COUNT(*) / ( SELECT COUNT(*) FROM T_MALL_EVALUATION WHERE SELLER_GOODS_ID = 1 ),0.00)  " +
//                        " FROM " +
//                        " T_MALL_EVALUATION  " +
//                        " WHERE " +
//                        " SELLER_GOODS_ID = '" + goodsId +"'" +
//                        " AND EVALUATION_TYPE = 1", Double.class);
    }

    @Override
    public Integer getEvaluationNumBySellerId(String sellerId) {
//        String sql = "SELECT IFNULL(COUNT(me.UUID), 0) " +
//                "FROM `T_MALL_EVALUATION` me " +
////                "LEFT JOIN T_MALL_SELLER_GOODS msg ON msg.UUID = me.SELLER_GOODS_ID " +
//                "WHERE me.SELLER_ID = '" + sellerId + "'";
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT IFNULL(COUNT(me.UUID), 0)  ")
                .append(" FROM T_MALL_EVALUATION me  ")
                .append(" LEFT JOIN T_MALL_SELLER_GOODS msg ON msg.UUID = me.SELLER_GOODS_ID  ")
                .append(" WHERE me.SELLER_ID = ? ")
                .append(" AND msg.SELLER_ID= ? ")
                .append(" AND  me.STATUS = '0' ")
        ;
        List<Object> params = new ArrayList<>();
        params.add(sellerId);
        params.add(sellerId);
        return this.jdbcTemplate.queryForObject(sql.toString(), params.toArray(), Integer.class);
    }

    @Override
    public Integer getEvaluationNumBySellerGoodsId(String sellerGoodsId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(Evaluation.class);
        criteria.add(Restrictions.eq("sellerGoodsId", sellerGoodsId));
        criteria.setProjection(Projections.rowCount());
        Integer totalCount = ((Long) criteria.uniqueResult()).intValue();
//        return this.jdbcTemplate.queryForObject(
//                "SELECT " +
//                        " COUNT( * )  " +
//                        "FROM " +
//                        " T_MALL_EVALUATION  " +
//                        "WHERE " +
//                        " SELLER_GOODS_ID = '" + sellerGoodsId + "'", Integer.class);
        return totalCount;
    }

    @Override
    public Double getSellerFavorableRate(String sellerId) {
        return this.jdbcTemplate.queryForObject("SELECT TRUNCATE(t1.good_count/t2.total_count, 4)" +
                "FROM" +
                "(SELECT COUNT(UUID) good_count FROM T_MALL_EVALUATION WHERE SELLER_ID = '" + sellerId + "' AND RATING >= 4) t1," +
                "(SELECT COUNT(UUID) total_count FROM T_MALL_EVALUATION WHERE SELLER_ID = '" + sellerId + "') t2;", Double.class);
    }

    public Map<String, Double> getSellerFavorableRates(List sellerIds) {
        Map<String, Double> soldCounts = new HashMap<>();
        if (Optional.ofNullable(sellerIds).map(List::isEmpty).orElse(true)) {
            return soldCounts;
        }
        String sql = "SELECT t1.sellerId,TRUNCATE(t1.good_count/t2.total_count, 4) AS rate FROM " +
                "(SELECT SELLER_ID AS sellerId,COUNT(UUID) good_count FROM T_MALL_EVALUATION WHERE SELLER_ID IN (:sellerIds) AND RATING >=4 GROUP BY sellerId) t1 LEFT JOIN " +
                "(SELECT SELLER_ID AS sellerId,COUNT(UUID) total_count FROM T_MALL_EVALUATION WHERE SELLER_ID IN (:sellerIds) GROUP BY sellerId) t2 ON t1.sellerId = t2.sellerId ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("sellerIds", sellerIds);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<Map<String, Object>> results = namedParameterJdbcTemplate.queryForList(sql, params);
        for (Map<String, Object> result : results) {
            String sellerId = (String) result.get("sellerId");
            Double soldCount = ((BigDecimal) result.get("rate")).doubleValue();
            soldCounts.put(sellerId, soldCount);
        }
        return soldCounts;
    }

    @Override
    public void updateEvaluation(Evaluation entity) {
        if (entity == null || entity.getId() == null) {
            throw new BusinessException("记录不存在");
        }

        getHibernateTemplate().update(entity);
    }

    /**
     * 修改指定商铺的所有来自演示账户的评论时间
     *
     * @param sellerId
     * @param fromTime
     * @param toTime
     */
    public void updateEvaluationTime(String sellerId, Date fromTime, Date toTime) {
        if (StrUtil.isBlank(sellerId)
                || fromTime == null
                || toTime == null) {
            throw new BusinessException("请求参数不完整");
        }

        // 列出所有的演示账号评论
        DetachedCriteria query = DetachedCriteria.forClass(Evaluation.class);
        query.add(Property.forName("sourceType").eq(2));
        query.add(Property.forName("sellerId").eq(sellerId));

        List<Evaluation> allEvaluationList = (List<Evaluation>) getHibernateTemplate().findByCriteria(query);
        if (CollectionUtil.isEmpty(allEvaluationList)) {
            logger.info("商品:" + sellerId + " 的来自演示账号的评论数量为0");
            return;
        }
        logger.info("商品:" + sellerId + " 的来自演示账号的评论数量为:" + allEvaluationList.size() + ", fromTime:" + fromTime.getTime() + ", toTime:" + toTime.getTime());

        // 毫秒无法用 int 承接，容易变富值
        int timeDiff = (int) (toTime.getTime() / 1000L - fromTime.getTime() / 1000L); // 秒级别
        Random rdm = new Random(timeDiff);
        for (Evaluation oneEvaluation : allEvaluationList) {
            int rdmTimeOff = rdm.nextInt(timeDiff);

            Date newEvaluationTime = new Date(fromTime.getTime() + (long) rdmTimeOff * 1000L);
            oneEvaluation.setEvaluationTime(newEvaluationTime);
            getHibernateTemplate().update(oneEvaluation);
        }
    }


    public int updateHideEvaluation(String id) {
        if (StrUtil.isBlank(id)
                || Objects.equals(id, "0")) {
            throw new BusinessException("请求参数不完整");
        }

        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = " update T_MALL_EVALUATION set STATUS= :status where UUID= :id ";
        NativeQuery query = currentSession.createSQLQuery(sql);

        query.setParameter("status", 1);
        query.setParameter("id", id);

        return query.executeUpdate();
    }

    public int updateOpenEvaluation(String id) {
        if (StrUtil.isBlank(id)
                || Objects.equals(id, "0")) {
            throw new BusinessException("请求参数不完整");
        }

        Session currentSession = getHibernateTemplate().getSessionFactory().getCurrentSession();
        String sql = " update T_MALL_EVALUATION set STATUS= :status where UUID= :id ";
        NativeQuery query = currentSession.createSQLQuery(sql);

        query.setParameter("status", 0);
        query.setParameter("id", id);

        return query.executeUpdate();
    }

}
