package project.invest.order.impl;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.hibernate.query.NativeQuery;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.invest.order.AdminOrderService;
import project.invest.project.model.InvestOrders;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.wallet.Wallet;
import project.wallet.WalletService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AdminOrderServiceImpl extends HibernateDaoSupport implements AdminOrderService {

    private PagedQueryDao pagedQueryDao;

    private WalletService walletService;

    protected MoneyLogService moneyLogService;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String id, String userCode, String userName, String phone, String roleName, String startTime, String endTime, Integer status) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(" SELECT ");
        queryString.append(" orders.UUID id,  orders.AMOUNT amount, orders.BONUS_RATE bonusRate, orders.BONUS_RATE_VIP bonusRateVip, orders.STATUS status, ");
        queryString.append(" orders.BONUS bonus, orders.TYPE type, orders.INCOME income, orders.CREATE_TIME createTIME, orders.SUCESS_WILL sucessWill, ");
        queryString.append(" p.USERCODE userCode, p.USERNAME userName, p.PHONE phone, p.ROLENAME roleName, t.projectName ");
        queryString.append(" FROM ");
        queryString.append(" T_INVEST_ORDERS orders ");
        queryString.append(" LEFT JOIN PAT_PARTY p ON orders.PARTY_ID = p.UUID ");
        queryString.append(" LEFT JOIN ( SELECT PROJECT_ID projectId, NAME projectName FROM T_INVEST_PROJECT_LANG  WHERE LANG = 'cn' ) t ON orders.PROJECT_ID = t.projectId ");
        queryString.append(" WHERE 1=1 ");
        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" and orders.UUID =:id");
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

        if (!StringUtils.isNullOrEmpty(roleName)) {
            queryString.append(" and p.ROLENAME =:roleName");
            parameters.put("roleName", roleName);
        }

        if (!StringUtils.isNullOrEmpty(phone)) {
            queryString.append(" and p.PHONE =:phone");
            parameters.put("phone", phone);
        }

        if (-2 != status) {
            queryString.append(" and orders.STATUS =:status");
            parameters.put("status", status);
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(orders.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(orders.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }

        queryString.append(" ORDER BY orders.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    /**
     * 订单取消
     * @param id
     */
    @Override
    public void updateCancel(String id) {
        InvestOrders order = this.findOrdersById(id);
        if(order.getIncome() >0){
            throw new BusinessException("该用户已产生收益金额，无法取消订单!");
        }
        Wallet wallet = this.walletService.saveWalletByPartyId(order.getPartyId());
        double amount_before = wallet.getMoney();
        double amount = Arith.add(0, order.getAmount());
        walletService.update(order.getPartyId(),order.getAmount());

        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(amount);
        moneyLog.setAmount_after(Arith.add(amount_before,amount));

        moneyLog.setLog("投资订单取消[" + order.getId() + "]");
        moneyLog.setPartyId(order.getPartyId());
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.ORDER_MONEY_CANCEL);
        moneyLogService.save(moneyLog);

        order.setStatus(-1);
        order.setAuditsTime(new Date());
        this.getHibernateTemplate().update(order);
    }


    @Override
    public InvestOrders findOrdersById(String id) {
        return this.getHibernateTemplate().get(InvestOrders.class, id);
    }

    @Override
    public void updateClosure(InvestOrders order) {
        order.setStatus(1);
        order.setAuditsTime(new Date());
        this.getHibernateTemplate().update(order);
    }

    @Override
    public Map<String,Object> findDaySumData() {
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT ");
        sql.append(" count(orders.UUID) orderNum, ");
        sql.append(" count( DISTINCT orders.PROJECT_ID) projectNum, ");
        sql.append(" IFNULL(sum(orders.AMOUNT),0) amount, ");
        sql.append(" IFNULL(sum(orders.INCOME),0) orderIncome ");
        sql.append(" FROM ");
        sql.append(" T_INVEST_ORDERS orders ");
        sql.append(" WHERE to_days(orders.CREATE_TIME) = TO_DAYS(now()) ");

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql.toString());
        Object[] results = nativeQuery.getSingleResult();
        sumData.put("orderNum",results[0]);
        sumData.put("projectNum",results[1]);
        sumData.put("amount",results[2]);
        sumData.put("orderIncome",results[3]);

        StringBuffer sql1 = new StringBuffer(" SELECT ");
        sql1.append(" IFNULL(sum(r.REBATE),0) rebateNum, " );
        sql1.append(" r.UUID id FROM ");
        sql1.append(" T_INVEST_REBATE r ");
        sql1.append(" WHERE r.STATUS = 0 and to_days(r.CREATE_TIME) = TO_DAYS(now()) ");
        NativeQuery<Object[]> nativeQuery1 = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql1.toString());
        Object[] result = nativeQuery1.getSingleResult();
        Object o = result[0];
        sumData.put("rebateNum",o);
        return sumData;
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }
}