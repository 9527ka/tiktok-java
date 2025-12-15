package project.invest.order.impl;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import project.Constants;
import project.invest.order.AdminExchangeOrderService;
import project.invest.project.model.ExchangeOrder;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.recom.UserRecomService;
import project.tip.TipService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import security.SecUser;
import security.internal.SecUserService;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminExchangeOrderServiceImpl extends HibernateDaoSupport implements AdminExchangeOrderService {

    private WalletService walletService;
    private MoneyLogService moneyLogService;
    private PagedQueryDao pagedQueryDao;
    private UserRecomService userRecomService;
    private PasswordEncoder passwordEncoder;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private LogService logService;
    private SecUserService secUserService;

    private TipService tipService;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name_para, String phone, String id, String roleName, Integer status, String startTime, String endTime) {

        StringBuffer queryString = new StringBuffer();

        queryString.append("SELECT");
        queryString.append(
                " party.USERNAME userName, party.ROLENAME roleName, party.USERCODE usercode, party.PHONE phone, ");
        queryString.append(
                " orders.UUID id, orders.SYMBOL symbol, orders.RATA rata, orders.SYMBOL_VALUE symbolValue, orders.REAL_AMOUNT realAmount, ");
        queryString.append(
                " orders.ORDER_PRICE_TYPE orderPriceType, orders.CREATE_TIME createTime, orders.STAUS status, orders.PAY_TYPE payType, ");
        queryString.append(
                " orders.BANK_NAME bankName, orders.BANK_ACCOUNT bankAccount, orders.REVIEW_TIME reviewTime, orders.REMARK remark ");
        queryString.append(" FROM");
        queryString.append(" T_EXCHANGE_ORDER orders  " + " LEFT JOIN PAT_PARTY party ON orders.PARTY_ID = party.UUID ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" and orders.UUID =:id");
            parameters.put("id", id);
        }

        if (!StringUtils.isNullOrEmpty(name_para)) {
            queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
            parameters.put("username", "%" + name_para + "%");
        }

        if (!StringUtils.isNullOrEmpty(roleName)) {
            queryString.append(" and   party.ROLENAME =:roleName");
            parameters.put("roleName", roleName);
        }

        if (!StringUtils.isNullOrEmpty(phone)) {
            queryString.append(" and party.PHONE =:phone");
            parameters.put("phone", phone);
        }
        if (status != null) {
            queryString.append(" and orders.STAUS = :status  ");
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
        queryString.append(" order by orders.CREATE_TIME desc ");

        Page page = pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    /**r
     * 通过申请
     * @param id
     * @param safeword
     */
    @Override
    public void saveSucceeded(String id, String safeword, String userName) {
        SecUser sec = this.secUserService.findUserByLoginName(userName);
        String sysSafeword = sec.getSafeword();

        String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("资金密码错误");
        }

        ExchangeOrder exchangeOrder = this.get(id);

        if(Objects.isNull(exchangeOrder)){
            throw new BusinessException("订单异常");
        }

        if (exchangeOrder.getStaus() != 0 ) {// 通过后不可驳回
            throw new BusinessException("此订单已被处理，请刷新页面");
        }

        Date date = new Date();
        exchangeOrder.setReviewTime(date);

        exchangeOrder.setStaus(1);

        this.getHibernateTemplate().update(exchangeOrder);

        SecUser SecUser = secUserService.findUserByPartyId(exchangeOrder.getPartyId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(exchangeOrder.getId().toString());
        log.setOperator(userName);
        log.setUsername(SecUser.getUsername());
        log.setPartyId(SecUser.getPartyId());
        log.setLog("通过OTC订单申请。订单号[" + exchangeOrder.getId() + "]。");

        logService.saveSync(log);
        tipService.deleteTip(exchangeOrder.getId().toString());
    }

    @Override
    public void saveReject(String id, String failure_msg, String username_login) {
        ExchangeOrder exchangeOrder = this.get(id);

        if(Objects.isNull(exchangeOrder)){
            throw new BusinessException("订单异常");
        }

        if (exchangeOrder.getStaus() != 0 ) {
            throw new BusinessException("此订单已被处理，请刷新页面");
        }
        exchangeOrder.setStaus(2);
        exchangeOrder.setRemark(failure_msg);
        exchangeOrder.setReviewTime(new Date());

        Wallet wallet = walletService.saveWalletByPartyId(exchangeOrder.getPartyId());

        double amount_before = wallet.getMoney();

        walletService.update(wallet.getPartyId().toString(), exchangeOrder.getSymbolValue());

        /*
         * 保存资金日志
         */
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(exchangeOrder.getSymbolValue());
        moneyLog.setAmount_after(Arith.add(amount_before, exchangeOrder.getSymbolValue()));

        moneyLog.setLog("驳回OTC订单申请[" + exchangeOrder.getId() + "]");
        // moneyLog.setExtra(withdraw.getOrder_no());
        moneyLog.setPartyId(exchangeOrder.getPartyId());
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_OTC_OUT);

        moneyLogService.save(moneyLog);

        SecUser SecUser = secUserService.findUserByPartyId(exchangeOrder.getPartyId());
        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(exchangeOrder.getId().toString());
        log.setOperator(username_login);
        log.setPartyId(exchangeOrder.getPartyId());
        log.setUsername(SecUser.getUsername());
        log.setLog("驳回OTC订单申请。原因[" + exchangeOrder.getRemark() + "],订单号[" + exchangeOrder.getId() + "]");

        logService.saveSync(log);
        tipService.deleteTip(exchangeOrder.getId().toString());
    }

    public ExchangeOrder get(String id) {
        return this.getHibernateTemplate().get(ExchangeOrder.class, id);
    }
    public WalletService getWalletService() {
        return walletService;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public MoneyLogService getMoneyLogService() {
        return moneyLogService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public PagedQueryDao getPagedQueryDao() {
        return pagedQueryDao;
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public UserRecomService getUserRecomService() {
        return userRecomService;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }


    public PasswordEncoder getPasswordEncoder() {
        return passwordEncoder;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public NamedParameterJdbcTemplate getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public void setNamedParameterJdbcTemplate(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }



    public LogService getLogService() {
        return logService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public SecUserService getSecUserService() {
        return secUserService;
    }

    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }

    public TipService getTipService() {
        return tipService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }
}