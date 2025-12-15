package project.mall.combo.impl;

import com.alibaba.fastjson.JSON;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.MallRedisKeys;
import project.mall.combo.AdminComboService;
import project.mall.combo.model.Combo;
import project.mall.combo.model.ComboLang;
import project.redis.RedisHandler;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description:
 * @author: Winter
 * @date: 2022/11/11
 */
public class AdminComboServiceImpl extends HibernateDaoSupport implements AdminComboService {

    private PagedQueryDao pagedQueryDao;

    protected RedisHandler redisHandler;


    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name, String startTime, String endTime) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" b.UUID id, l.COMBO_ID comboId, b.CREATE_TIME createTime, b.ICON_IMG iconImg, b.PROMOTE_NUM promoteNum, b.DAY day, ");
        queryString.append(" b.AMOUNT amount, l.NAME name, l.LANG lang, l.CONTENT content ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_COMBO b LEFT JOIN T_MALL_COMBO_LANG l ON b.UUID = l.COMBO_ID ");
        queryString.append(" WHERE 1=1 and l.LANG = 'cn' and l.STATUS = 0 ");

        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isNullOrEmpty(name)) {
            queryString.append(" AND l.NAME like:name ");
            parameters.put("name", "%" + name + "%");
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(b.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(b.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY b.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Page pagedQueryRecordList(int pageNo, int pageSize, String userCode, String sellerName, String startTime, String endTime) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" r.UUID id, r.PARTY_ID sellerId, r.PROMOTE_NUM promoteNum, r.AMOUNT amount, r.DAY day, r.NAME name, r.CREATE_TIME createTime, ");
        queryString.append(" r.STOP_TIME stopTime, party.USERCODE userCode,  party.ROLENAME roleName, s.NAME sellerName, count(g.UUID) goodsNum,r.COMBO_ID comboId ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_COMBO_RECORD r  ");
        queryString.append(" LEFT JOIN PAT_PARTY party ON r.PARTY_ID = party.UUID ");
        queryString.append(" LEFT JOIN T_MALL_SELLER s ON r.PARTY_ID = s.UUID ");
        queryString.append(" LEFT JOIN T_MALL_SELLER_GOODS g ON r.PARTY_ID = g.SELLER_ID AND g.STOP_TIME > 0 ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isNullOrEmpty(userCode)) {
            queryString.append(" AND party.USERCODE like:userCode ");
            parameters.put("userCode", "%" + userCode + "%");
        }
        if (!StringUtils.isNullOrEmpty(sellerName)) {
            queryString.append(" AND s.NAME =:sellerName ");
            parameters.put("sellerName", sellerName);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(r.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(r.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" GROUP BY r.UUID ");
        queryString.append(" ORDER BY r.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

//    @Override
//    public Page pagedQueryRecordGoodsList(int pageNo, int pageSize, String partyId) {
//        StringBuffer queryString = new StringBuffer();
//        queryString.append(" SELECT ");
//        queryString.append(" g.UUID id, g.SELLING_PRICE sellingPrice, l.goodsName , s.IMG_URL_1 imgUrl1, s.UUID goodsId ");
//        queryString.append(" FROM ");
//        queryString.append(" T_MALL_SELLER_GOODS g ");
//        queryString.append(" LEFT JOIN T_MALL_SYSTEM_GOODS s ON g.GOODS_ID = s.UUID ");
//        queryString.append(" LEFT JOIN ( SELECT GOODS_ID goodsId, NAME goodsName FROM T_MALL_SYSTEM_GOODS_LANG WHERE LANG = 'en' ) l ON g.GOODS_ID = l.goodsId ");
//        queryString.append(" WHERE 1=1 AND g.STOP_TIME > 0  ");
//
//        Map<String, Object> parameters = new HashMap<String, Object>();
//        if (!StringUtils.isNullOrEmpty(partyId)) {
//            queryString.append(" AND g.SELLER_ID =:partyId ");
//            parameters.put("partyId", partyId);
//        }
//        queryString.append(" ORDER BY g.CREATE_TIME DESC ");
//        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//        return page;
//    }

    @Override
    public Page pagedQueryRecordGoodsList(int pageNo, int pageSize, String partyId) {
        StringBuffer queryString = new StringBuffer();
        queryString.append(" SELECT ");
        queryString.append(" g.UUID id, g.SELLING_PRICE sellingPrice, s.IMG_URL_1 imgUrl1, s.UUID goodsId, ");
        queryString.append(" COALESCE(s.EN_TITLE, s.CN_TITLE, s.ZH_TITLE) as goodsName ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_SELLER_GOODS g, T_MALL_SYSTEM_GOODS s where g.GOODS_ID=s.UUID ");
        queryString.append(" AND g.STOP_TIME > 0  ");

        Map<String, Object> parameters = new HashMap<String, Object>();
        if (!StringUtils.isNullOrEmpty(partyId)) {
            queryString.append(" AND g.SELLER_ID =:partyId ");
            parameters.put("partyId", partyId);
        }
        queryString.append(" ORDER BY g.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public void save(String name) {
        Combo combo = new Combo();
        combo.setCreateTime(new Date());
        combo.setAmount(0D);
        this.getHibernateTemplate().save(combo);

        ComboLang comboLang = new ComboLang();
        comboLang.setComboId(combo.getId().toString());
        comboLang.setLang("cn");
        comboLang.setName(name);
        getHibernateTemplate().save(comboLang);
        redisHandler.setSyncString(MallRedisKeys.MALL_COMBO_LANG+comboLang.getLang()+":"+combo.getId().toString(), JSON.toJSONString(comboLang));
    }

    @Override
    public List<ComboLang> findLanByComboId(String comboId, String lang) {
        Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(ComboLang.class);
        if(!StringUtils.isEmptyString(comboId)){
            criteria.add( Restrictions.eq("comboId",  comboId) );
        }
        if(!StringUtils.isEmptyString(lang)){
            criteria.add( Restrictions.eq("lang",  lang) );
        }
        if(CollectionUtils.isNotEmpty(criteria.list())){
            return  criteria.list();
        }
        return null;
    }

    @Override
    public void update(Combo bean, String name, String lang, String comboId, String comboLanId, String content) {
        List<ComboLang> comboLangs = this.findLanByComboId(bean.getId().toString(), lang);
        ComboLang comboLang = new ComboLang();
        if(CollectionUtils.isEmpty(comboLangs)){
            comboLang.setName(name);
            comboLang.setComboId(bean.getId().toString());
            comboLang.setLang(lang);
            comboLang.setContent(content);
            getHibernateTemplate().save(comboLang);
        } else {
            comboLang = comboLangs.get(0);
            comboLang.setName(name);
            comboLang.setContent(content);
            getHibernateTemplate().update(comboLang);
        }
        redisHandler.setSyncString(MallRedisKeys.MALL_COMBO_LANG+lang+":"+bean.getId().toString(), JSON.toJSONString(comboLang));
        this.getHibernateTemplate().update(bean);
    }


    @Override
    public Combo findById(String id) {
        return this.getHibernateTemplate().get(Combo.class, id);
    }

    @Override
    public void delete(String id, List<ComboLang> comboLangs) {
        Combo combo = this.findById(id);
        comboLangs.forEach(e ->{
            e.setStatus(1);
            getHibernateTemplate().update(e);
            redisHandler.setSyncString(MallRedisKeys.MALL_COMBO_LANG+e.getLang()+":"+combo.getId().toString(), JSON.toJSONString(e));
        });
    }


    public void setRedisHandler(RedisHandler redisHandler) {
        this.redisHandler = redisHandler;
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }
}