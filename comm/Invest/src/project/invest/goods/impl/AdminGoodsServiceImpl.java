package project.invest.goods.impl;
import com.alibaba.fastjson.JSON;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.InvestRedisKeys;
import project.invest.goods.AdminGoodsService;
import project.invest.goods.model.Goods;
import project.invest.goods.model.GoodsLang;
import project.redis.RedisHandler;

import java.util.*;

public class AdminGoodsServiceImpl extends HibernateDaoSupport implements AdminGoodsService {

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name, Integer status, String startTime, String endTime) {

        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append("g.UUID id, g.ICON_IMG iconImg, l.NAME name, g.PRIZE prize, g.CREATE_TIME createTime,");
        queryString.append("g.TOTAL total, g.STATUS status, g.LAST_AMOUNT lastAmount, g.EXCHANGE_AMOUNT exchangeAmount");
        queryString.append(" FROM ");
        queryString.append(" T_INVEST_GOODS g LEFT JOIN T_INVEST_GOODS_LANG l ON g.UUID = l.GOODS_ID");
        queryString.append(" WHERE 1=1 and l.LANG = 'cn' and l.TYPE = 0 ");
        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isNullOrEmpty(name)) {
            queryString.append(" AND l.NAME like:name ");
            parameters.put("name", "%" + name + "%");
        }
        if (null != status) {
            queryString.append(" AND g.status =:status ");
            parameters.put("status", status);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(g.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(g.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY g.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Goods findById(String id) {
        return this.getHibernateTemplate().get(Goods.class, id);
    }



    @Override
    public List<GoodsLang> findLanByGoodsId(String goodsId, String lang) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(GoodsLang.class);
        criteria.add( Restrictions.eq("goodsId",  goodsId) );
        if(!StringUtils.isEmptyString(lang)){
            criteria.add( Restrictions.eq("lang",  lang) );
        }
        if(CollectionUtils.isNotEmpty(criteria.list())){
            return  criteria.list();
        }
        return null;
    }


    private List<GoodsLang> findGoodsByName(String name, String lang,int type){
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(GoodsLang.class);
        criteria.add( Restrictions.eq("name",  name) );
        criteria.add( Restrictions.eq("lang", lang ) );
        criteria.add( Restrictions.eq("type", 0 ) );
        return criteria.list();
    }

    @Override
    public void save(String name) {

        List<GoodsLang> goodsList = findGoodsByName(name,"cn",0);
        if(CollectionUtils.isNotEmpty(goodsList)){
            throw new BusinessException("商品名称已存在");
        }
        Goods goods = new Goods();
        goods.setCreateTime(new Date());
        goods.setExchangeAmount(0);
        goods.setLastAmount(0);
        goods.setSort(0);
        goods.setTotal(0);
        goods.setPayWay(0);
        goods.setStatus(0);
        goods.setUpTime(System.currentTimeMillis());
        this.getHibernateTemplate().save(goods);

        GoodsLang goodsLang = new GoodsLang();
        goodsLang.setName(name);
        goodsLang.setLang("cn");
        goodsLang.setGoodsId(goods.getId().toString());
        getHibernateTemplate().save(goodsLang);
        redisHandler.setSyncString(InvestRedisKeys.INVEST_GOODS_LANG+goodsLang.getLang()+":"+goods.getId().toString(), JSON.toJSONString(goodsLang));
    }

    @Override
    public void update(String name, String iconImg, String prize, String goodsId, String goodsLanId, String status, String des, String lang, String sort,  String total, String lastAmount) {
        Goods goods = this.findById(goodsId);
        List<GoodsLang> goodsLangs = findGoodsByName(name,lang,0);
        if(!goodsLangs.isEmpty() && !goodsLangs.get(0).getId().equals(goodsLanId)){
            throw new BusinessException("商品名称已存在");
        }
        if(null == goods){
            throw new BusinessException("此商品不存在");
        }

        GoodsLang goodsLang = new GoodsLang();
        goods.setIconImg(iconImg);
        goods.setStatus(Integer.parseInt(status));
        goods.setPrize(Double.parseDouble(prize));
        goods.setSort(Integer.parseInt(sort));
        goods.setPayWay(0);
        goods.setTotal(Integer.parseInt(total));
        goods.setLastAmount(Integer.parseInt(lastAmount));
        goods.setUpTime(new Date().getTime());
        goodsLang.setDes(des);
        goodsLang.setName(name);
        goodsLang.setLang(lang);
        goodsLang.setGoodsId(goodsId);
        if(StringUtils.isEmptyString(goodsLanId)){
            getHibernateTemplate().save(goodsLang);
        } else {
            goodsLang.setId(goodsLanId);
            getHibernateTemplate().merge(goodsLang);
        }
        redisHandler.setSyncString(InvestRedisKeys.INVEST_GOODS_LANG+goodsLang.getLang()+":"+goods.getId().toString(), JSON.toJSONString(goodsLang));
        this.getHibernateTemplate().update(goods);
    }


    @Override
    public void delete(String id,List<GoodsLang> goodsLangs) {
        Goods goods = this.findById(id);
        goodsLangs.forEach(e ->{
            e.setType(1);
            getHibernateTemplate().update(e);
            redisHandler.setSyncString(InvestRedisKeys.INVEST_GOODS_LANG+e.getLang()+":"+goods.getId().toString(), JSON.toJSONString(e));
        });
    }



    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

}