package project.mall.goods.impl;

import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.mall.event.message.KeepSellerGoodsEvent;
import project.mall.event.model.KeepSellerGoodsInfo;
import project.mall.goods.KeepGoodsService;
import project.mall.goods.model.KeepGoods;
import project.mall.utils.MallPageInfo;
import project.mall.utils.MallPageInfoUtil;

import java.math.BigDecimal;
import java.util.*;

@Slf4j
public class KeepGoodsServiceImpl extends HibernateDaoSupport implements KeepGoodsService {
    private JdbcTemplate jdbcTemplate;

    @Override
    public MallPageInfo listKeepGoods(int pageNum, int pageSize, String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(KeepGoods.class);
        query.add(Restrictions.eq("partyId", partyId));
        query.addOrder(Order.desc("createTime"));

        // 查询总条数
        Long totalCount = (Long) query.setProjection(Projections.rowCount()).getExecutableCriteria(this.getHibernateTemplate().getSessionFactory().getCurrentSession()).uniqueResult();
        query.setProjection(null);

        MallPageInfo mallPageInfo=  MallPageInfoUtil.getMallPage(pageSize,pageNum,totalCount,getHibernateTemplate().findByCriteria(query, (pageNum - 1) * pageSize, pageSize));

       return mallPageInfo;
    }

    @Override
    public Integer getKeepGoodsCount(String partyId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(KeepGoods.class);
        criteria.add(Restrictions.eq("partyId", partyId));
        criteria.setProjection(Projections.rowCount());
        Integer totalCount = ((Long) criteria.uniqueResult()).intValue();
        return totalCount;
    }


    @Override
    public Integer getKeepCount(String sellerGoodsId) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(KeepGoods.class);
        criteria.add(Restrictions.eq("sellerGoodsId", sellerGoodsId));
        criteria.setProjection(Projections.rowCount());
        Integer totalCount = ((Long) criteria.uniqueResult()).intValue();
//        return this.jdbcTemplate.queryForObject(
//                "SELECT " +
//                        " COUNT( * )  " +
//                        "FROM " +
//                        " T_MALL_KEEP_GOODS  " +
//                        "WHERE " +
//                        " SELLER_GOODS_ID = '" + sellerGoodsId + "'", Integer.class);
        return totalCount;
    }

    @Override
    public Map<String, Integer> getSellerGoodsKeepCount(List<String> sellerGoodsIdList) {
        Map<String, Integer> sellerGoodsKeepNumMap = new HashMap();
        if (CollectionUtil.isEmpty(sellerGoodsIdList)) {
            return sellerGoodsKeepNumMap;
        }

        StringBuffer goodsIdsBuf = new StringBuffer();
        for (String oneGoodsId : sellerGoodsIdList) {
            goodsIdsBuf.append("'").append(oneGoodsId.trim()).append("',");
        }
        goodsIdsBuf.deleteCharAt(goodsIdsBuf.length() - 1);

        String sql = "SELECT SELLER_GOODS_ID as goodsId, COUNT(*) as totalNum " +
                " FROM T_MALL_KEEP_GOODS " +
                " WHERE SELLER_GOODS_ID in (" + goodsIdsBuf.toString() +") " +
                " GROUP BY SELLER_GOODS_ID ";

        List list=  this.jdbcTemplate.queryForList(sql);
        Iterator iterable = list.iterator();
        while (iterable.hasNext()) {
            Map rowMap = (Map) iterable.next();
            String sellerGoodsId = (String) rowMap.getOrDefault("goodsId", "");
            Long totalNum = (Long) rowMap.getOrDefault("totalNum","0");

            sellerGoodsKeepNumMap.put(sellerGoodsId, totalNum.intValue());
        }

        return sellerGoodsKeepNumMap;
    }

    @Override
    public void addKeep(String partyId, String sellerGoodsId) {
        KeepGoods keepGoods = new KeepGoods();
        keepGoods.setPartyId(partyId);
        keepGoods.setSellerGoodsId(sellerGoodsId);
        keepGoods.setCreateTime(new Date());
        getHibernateTemplate().save(keepGoods);

        // 发布一个收藏商品的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        KeepSellerGoodsInfo info = new KeepSellerGoodsInfo();
        info.setPartyId(partyId);
        info.setSellerGoodsId(sellerGoodsId);
        info.setAddKeep(true);
        wac.publishEvent(new KeepSellerGoodsEvent(this, info));
    }

    @Override
    public void deleteKeep(String sellerGoodsId, String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(KeepGoods.class);
        query.add(Restrictions.eq("sellerGoodsId", sellerGoodsId));
        query.add(Restrictions.eq("partyId", partyId));
        List<KeepGoods> list = (List<KeepGoods>) getHibernateTemplate().findByCriteria(query);
//        KeepGoods keepGoods = getHibernateTemplate().get(KeepGoods.class, sellerGoodsId);
        if (list == null || list.isEmpty()) {
            return;
        }
        for (KeepGoods keepGoods : list) {
            getHibernateTemplate().delete(keepGoods);
        }

        // 发布一个取消收藏商品的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        KeepSellerGoodsInfo info = new KeepSellerGoodsInfo();
        info.setPartyId(partyId);
        info.setSellerGoodsId(sellerGoodsId);
        info.setAddKeep(false);
        wac.publishEvent(new KeepSellerGoodsEvent(this, info));
    }

    @Override
    public Integer queryIsKeep(String sellerGoodsId, String partyId) {
        DetachedCriteria query = DetachedCriteria.forClass(KeepGoods.class);
        query.add(Restrictions.eq("partyId", partyId));
        query.add(Restrictions.eq("sellerGoodsId", sellerGoodsId));
        List<KeepGoods> list = (List<KeepGoods>) getHibernateTemplate().findByCriteria(query);
        if (list != null && !list.isEmpty()) {
            return 1;
        }
        return 0;

    }

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

}
