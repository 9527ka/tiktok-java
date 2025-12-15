package project.invest.goods.impl;

import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.invest.goods.AdminGoodsBuyService;
import project.invest.goods.model.GoodsBuy;
import project.invest.project.model.InvestOrders;

import java.util.HashMap;
import java.util.Map;

public class AdminGoodsBuyServiceImpl extends HibernateDaoSupport implements AdminGoodsBuyService {

    private PagedQueryDao pagedQueryDao;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String id, String userCode, String userName, String phone, Integer status, String startTime, String endTime) {

        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(" SELECT ");
        queryString.append(" b.UUID id, b.NUM num, b.PAY_POINT payPoint, b.STATUS status, b.PHONE phone, ");
        queryString.append(" b.CONTACTS contacts, b.ADDRESS address, b.REMARK remark, b.CREATE_TIME createTime, ");
        queryString.append(" p.USERCODE userCode, p.USERNAME userName, p.PHONE Uphone, g.goodsName ");
        queryString.append(" FROM ");
        queryString.append(" T_INVEST_GOODS_BUY b ");
        queryString.append(" LEFT JOIN PAT_PARTY p ON b.PARTY_ID = p.UUID ");
        queryString.append(" LEFT JOIN ( SELECT GOODS_ID goodsId, NAME goodsName FROM T_INVEST_GOODS_LANG  WHERE LANG = 'cn' ) g ON b.GOODS_ID = g.goodsId ");
        queryString.append(" WHERE 1=1 ");

        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" and b.UUID =:id");
            parameters.put("id", id);
        }

        if (!StringUtils.isNullOrEmpty(userCode)) {
            queryString.append(" and p.USERCODE =:userCode");
            parameters.put("userCode", userCode);
        }

        if (!StringUtils.isNullOrEmpty(userName)) {
            queryString.append(" and p.USERNAME =:userName");
            parameters.put("userName", userName);
        }

        if (!StringUtils.isNullOrEmpty(phone)) {
            queryString.append(" and p.PHONE =:phone");
            parameters.put("phone", phone);
        }

        if (-2 != status) {
            queryString.append(" and b.STATUS =:status");
            parameters.put("status", status);
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
    public Page pagedQueryExchange(int pageNo, int pageSize, String id, String userCode, String userName, String phone, String startTime, String endTime) {

        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(" SELECT ");
        queryString.append(" e.UUID id, e.NUM num, e.USDT usdt, e.PAY_POINT payPoint, e.SCALE scale, e.CREATE_TIME createTime, ");
        queryString.append(" p.USERCODE userCode, p.USERNAME userName, p.PHONE phone, g.goodsName, goods.PRIZE prize ");
        queryString.append(" FROM ");
        queryString.append(" T_INVEST_POINT_EXCHANGE e ");
        queryString.append(" LEFT JOIN PAT_PARTY p ON e.PARTY_ID = p.UUID ");
        queryString.append(" LEFT JOIN T_INVEST_GOODS goods ON e.GOODS_ID = goods.UUID ");
        queryString.append(" LEFT JOIN ( SELECT GOODS_ID goodsId, NAME goodsName FROM T_INVEST_GOODS_LANG  WHERE LANG = 'cn' ) g ON e.GOODS_ID = g.goodsId ");
        queryString.append(" WHERE 1=1 ");

        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" and e.UUID =:id");
            parameters.put("id", id);
        }

        if (!StringUtils.isNullOrEmpty(userCode)) {
            queryString.append(" and p.USERCODE =:userCode");
            parameters.put("userCode", userCode);
        }

        if (!StringUtils.isNullOrEmpty(userName)) {
            queryString.append(" and p.USERNAME =:userName");
            parameters.put("userName", userName);
        }

        if (!StringUtils.isNullOrEmpty(phone)) {
            queryString.append(" and p.PHONE =:phone");
            parameters.put("phone", phone);
        }

//        if (-2 != status) {
//            queryString.append(" and e.STATUS =:status");
//            parameters.put("status", status);
//        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(e.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(e.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }
        queryString.append(" ORDER BY e.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public GoodsBuy findGoodsBuyById(String id) {
        return this.getHibernateTemplate().get(GoodsBuy.class, id);
    }

    @Override
    public void updateStatus(String id, String type, String remark) {
        GoodsBuy goodsBuy = this.findGoodsBuyById(id);
        goodsBuy.setRemark(remark);
        if(Integer.parseInt(type) == 1){
            goodsBuy.setStatus(1);
        }
        if(Integer.parseInt(type) == 2){
            goodsBuy.setStatus(-1);
        }
        this.getHibernateTemplate().update(goodsBuy);
    }


    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }


}