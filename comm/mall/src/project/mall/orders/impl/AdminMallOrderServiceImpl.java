package project.mall.orders.impl;

import ext.Times;
import ext.Types;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.hibernate.query.NativeQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.log.Log;
import project.log.LogService;
import project.log.MoneyLogService;
import project.mall.log.OrderLogService;
import project.mall.log.model.OrderLog;
import project.mall.log.model.OrderStatusEnum;
import project.mall.orders.AdminMallOrderService;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.seller.AdminSellerService;
import project.party.PartyService;
import project.party.recom.UserRecomService;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.UserDataService;
import project.wallet.WalletService;
import security.SecUser;
import security.internal.SecUserService;

import java.util.*;

/**
 * @Description:
 * @author: Winter
 * @date: 2022/11/4
 */
public class AdminMallOrderServiceImpl extends HibernateDaoSupport implements AdminMallOrderService {

    protected LogService logService;
    protected SecUserService secUserService;
    protected PartyService partyService;
    protected MoneyLogService moneyLogService;
    protected WalletService walletService;
    protected UserDataService userDataService;
    protected OrderLogService orderLogService;
    Logger logger = LoggerFactory.getLogger(AdminMallOrderServiceImpl.class);
    private PagedQueryDao pagedQueryDao;
    private SysparaService sysParaService;
    private AdminSellerService adminSellerService;
    private UserRecomService userRecomService;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String id, String contacts, String loginPartyId, String sellerName, String phone, Integer payStatus, String startTime,
                           String endTime, String orderStatus, Integer status, String sellerRoleName, String userCode, String sellerCode, String purchTimeOutStatus, String isDelete) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(" SELECT ");
        queryString.append(" orders.UUID id, orders.CONTACTS contacts, orders.prize_real prizeReal, orders.PROFIT profit,  ");
        queryString.append(" party.ROLENAME roleName, orders.PROFIT_STATUS profitStatus,seller.ROLENAME sellerRoleName, ");
        queryString.append(" orders.PAY_STATUS payStatus, orders.ORDER_STATUS orderStatus, orders.CREATE_TIME createTime, ");
        queryString.append(" orders.SELLER_NAME sellerName, orders.STATUS status, orders.UP_TIME upTime, orders.PURCH_TIME purchTime, ");
        queryString.append(" orders.MANUAL_RECEIPT_STATUS manualReceiptStatus,orders.MANUAL_SHIP_STATUS manualShipStatus,  ");
        queryString.append(" party.USERCODE userCode, seller.USERCODE sellerCode, orders.PURCH_TIME_OUT_STATUS purchTimeOutStatus, ");
        queryString.append(" orders.IS_DELETE isDelete, orders.PURCH_TIME_OUT_TIME purchTimeOutTime, orders.SYSTEM_PRICE systemPrice, orders.SELLER_DISCOUNT sellerDiscount ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_ORDERS_PRIZE orders LEFT JOIN PAT_PARTY party ON orders.PARTY_ID = party.UUID ");
        queryString.append(" LEFT JOIN PAT_PARTY seller ON orders.SELLER_ID = seller.UUID ");
        queryString.append(" WHERE orders.RETURN_STATUS IN (0,3) ");

        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" and orders.UUID =:id");
            parameters.put("id", id);
        }

        if (!StringUtils.isNullOrEmpty(userCode)) {
            queryString.append(" and party.USERCODE =:userCode");
            parameters.put("userCode", userCode);
        }

        if (!StringUtils.isNullOrEmpty(sellerCode)) {
            queryString.append(" and seller.USERCODE =:sellerCode");
            parameters.put("sellerCode", sellerCode);
        }

        if (!StringUtils.isNullOrEmpty(contacts)) {
            queryString.append(" and orders.CONTACTS =:contacts");
            parameters.put("contacts", contacts);
        }

        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
                return new Page();
            }
            queryString.append(" and orders.SELLER_ID in (:children) ");
            parameters.put("children", children);
        }

        if (!StringUtils.isNullOrEmpty(sellerName)) {
            queryString.append(" AND trim(replace(orders.SELLER_NAME,' ','')) like:sellerName ");
            sellerName = sellerName.replace(" ", "");
            parameters.put("sellerName", "%" + sellerName + "%");
        }

        if (!StringUtils.isNullOrEmpty(phone)) {
            queryString.append(" and orders.PHONE =:phone");
            parameters.put("phone", phone);
        }

        if (-2 != status) {
            queryString.append(" and orders.STATUS =:status");
            parameters.put("status", status);
        }
        if (-2 != payStatus) {
            queryString.append(" and orders.PAY_STATUS =:payStatus");
            parameters.put("payStatus", payStatus);
        }
        if (!StringUtils.isNullOrEmpty(purchTimeOutStatus)) {
            queryString.append(" and orders.PURCH_TIME_OUT_STATUS =:purchTimeOutStatus");
            parameters.put("purchTimeOutStatus", purchTimeOutStatus);
        }
        if (!StringUtils.isNullOrEmpty(isDelete)) {
            queryString.append(" and orders.IS_DELETE =:isDelete");
            parameters.put("isDelete", Integer.valueOf(isDelete));
        }
        if (!StringUtils.isNullOrEmpty(orderStatus)) {
            queryString.append(" and party.ROLENAME =:orderStatus");
            parameters.put("orderStatus", orderStatus);
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(orders.CREATE_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(orders.CREATE_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }

        if (!StringUtils.isNullOrEmpty(sellerRoleName)) {
            queryString.append(" AND seller.ROLENAME =:sellerRoleName  ");
            parameters.put("sellerRoleName", sellerRoleName);
        }

        queryString.append(" ORDER BY orders.CREATE_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

//    @Override
//    public Page findGoodsDetailsById(int pageNo, int pageSize, String id) {
//        Map<String, Object> parameters = new HashMap<>();
//        StringBuffer queryString = new StringBuffer(" SELECT ");
//        queryString.append(" orders.UUID id, orders.SKU_ID skuId,  orders.GOODS_NUM goodsNum, orders.GOODS_REAL goodsReal, l.goodsName, l.goodsId ");
//        queryString.append(" FROM ");
//        queryString.append(" T_MALL_ORDERS_GOODS orders ");
//        queryString.append(" LEFT JOIN ( SELECT GOODS_ID goodsId, NAME goodsName FROM T_MALL_SYSTEM_GOODS_LANG WHERE LANG = 'en' ) l ON orders.SYSTEM_GOODS_ID = l.goodsId ");
//        queryString.append(" WHERE 1=1 ");
//        if (!StringUtils.isNullOrEmpty(id)) {
//            queryString.append(" and orders.ORDER_ID =:id");
//            parameters.put("id", id);
//        }
//        queryString.append(" ORDER BY orders.GOODS_SORT DESC ");
//        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
//        return page;
//    }

    public Page findGoodsDetailsById(int pageNo, int pageSize, String id) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(" SELECT ");
        queryString.append(" orders.UUID id, orders.SKU_ID skuId, orders.GOODS_NUM goodsNum, orders.GOODS_REAL goodsReal, ");
        queryString.append(" orders.SYSTEM_GOODS_ID goodsId, COALESCE(l.EN_TITLE, l.CN_TITLE, l.ZH_TITLE) as goodsName ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_ORDERS_GOODS orders left join T_MALL_SYSTEM_GOODS l on orders.SYSTEM_GOODS_ID = l.UUID ");
        queryString.append(" WHERE 1=1 ");
        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" and orders.ORDER_ID =:id");
            parameters.put("id", id);
        }
        queryString.append(" ORDER BY orders.GOODS_SORT DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public Page pagedQueryRefundList(int pageNo, int pageSize, String id, Integer orderStatus, String loginPartyId, String userCode, Integer returnStatus, String startTime, String endTime) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(" SELECT ");
        queryString.append(" orders.UUID id, orders.ORDER_STATUS orderStatus, orders.SELLER_NAME sellerName, orders.REFUND_REMARK refundRemark, ");
        queryString.append(" orders.USER_CODE userCode, orders.PRIZE_REAL prizeReal, orders.RETURN_STATUS returnStatus, orders.REFUND_TIME refundTime, orders.REFUND_DEAL_TIME refundDealTime ");
        queryString.append(" FROM ");
        queryString.append(" T_MALL_ORDERS_PRIZE orders ");
        queryString.append(" WHERE 1=1 AND orders.STATUS = 6 ");
        if (!StringUtils.isNullOrEmpty(id)) {
            queryString.append(" and orders.UUID =:id");
            parameters.put("id", id);
        }
        if (!StringUtils.isNullOrEmpty(userCode)) {
            queryString.append(" and orders.USER_CODE =:userCode");
            parameters.put("userCode", userCode);
        }
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
                return new Page();
            }
            queryString.append(" and orders.SELLER_ID in (:children) ");
            parameters.put("children", children);
        }
        if (-2 != orderStatus) {
            queryString.append(" and orders.ORDER_STATUS =:orderStatus");
            parameters.put("orderStatus", orderStatus);
        }
        if (-2 != returnStatus) {
            queryString.append(" and orders.RETURN_STATUS =:returnStatus");
            parameters.put("returnStatus", returnStatus);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(orders.REFUND_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }

        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(orders.REFUND_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }

        queryString.append(" ORDER BY orders.REFUND_TIME DESC ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
        return page;
    }

    @Override
    public void saveReject(String id, String failure_msg, String username_login, String loginPartyId) {
        MallOrdersPrize order = this.findById(id);
        if (order.getReturnStatus() != 1) {
            throw new BusinessException("此订单已被处理，请刷新页面");
        }
        order.setRefundDealTime(new Date());
        order.setRefundRemark(failure_msg);
        order.setReturnStatus(3);
        this.getHibernateTemplate().update(order);

        SecUser sec = this.secUserService.findUserByPartyId(order.getPartyId());

        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(order.getId().toString());
        log.setUsername(sec.getUsername());
        log.setOperator(username_login);
        log.setPartyId(order.getPartyId());
        log.setLog("管理员驳回一笔退款订单。订单号[" + order.getId() + "]，驳回理由[" + order.getRefundRemark() + "]。");
        logService.saveSync(log);
    }

    @Override
    public Map<String, Object> findDaySumData() {
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT ");
        sql.append(" count(orders.UUID) orderNum, ");
        sql.append(" IFNULL(sum(orders.PRIZE_REAL),0) amount ");
        sql.append(" FROM ");
        sql.append(" T_MALL_ORDERS_PRIZE orders ");
        sql.append(" WHERE to_days(orders.CREATE_TIME) = TO_DAYS(now()) ");

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql.toString());
        Object[] results = nativeQuery.getSingleResult();
        sumData.put("orderNum", results[0]);
        sumData.put("amount", results[1]);
        return sumData;
    }

    @Override
    public Map<String, Object> findBySellId(String sellId) {
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT ");
        sql.append(" count(orders.UUID) orderNum, ");
        sql.append(" cast(IFNULL(sum(orders.PRIZE_REAL), 0) AS DECIMAL (19, 2)) AS original, ");
        sql.append(" cast(IFNULL(sum(orders.PROFIT), 0) AS DECIMAL (19, 2)) AS profit ");
        sql.append(" FROM ");
        sql.append(" T_MALL_ORDERS_PRIZE orders ");

        sql.append(" where STATUS IN(1,2,3,4,5)  and PROFIT_STATUS = 1 ");

        if (StringUtils.isNotEmpty(sellId)) {
            sql.append(" and orders.SELLER_ID = ? ");

        }
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(1, sellId);
        }
        Object[] results = nativeQuery.getSingleResult();

        sumData.put("orderNum", results[0]);
        sumData.put("totalSales", results[1]);
        sumData.put("totalProfit", results[2]);
        return sumData;
    }


    @Override
    public Map<String, Object> findProfitBySellId(String sellId) {

        Map<String, Object> sumData = new HashMap<>();

        StringBuffer sql = new StringBuffer(" SELECT ");
        sql.append(" cast(IFNULL(sum(orders.PROFIT), 0) AS DECIMAL (19, 2)) AS profit ");
        sql.append(" FROM ");
        sql.append(" T_MALL_ORDERS_PRIZE orders ");
        sql.append(" where STATUS IN(1,2,3,4,5)  and PROFIT_STATUS = 1 ");

        if (StringUtils.isNotEmpty(sellId)) {
            sql.append(" and orders.SELLER_ID = ? ");

        }

        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession()
                .createNativeQuery(sql.toString());

        if (StringUtils.isNotEmpty(sellId)) {
            nativeQuery.setParameter(1, sellId);
        }

        if (Objects.nonNull(nativeQuery)) {
            Object result = nativeQuery.getSingleResult();
            sumData.put("totalProfit", result);
        } else {
            sumData.put("totalProfit", 0);
        }
        return sumData;
    }


    @Override
    public MallOrdersPrize findById(String id) {
        return this.getHibernateTemplate().get(MallOrdersPrize.class, id);
    }

    @Override
    public void updateStatus(String[] ids, String shipmentProvider, String shipmentTradeNo, int confirmDays) {
        boolean requirePurchase = true;
        Syspara param = this.sysParaService.find("admin_seller_shipment_require_purchase");
        if (param != null) {
            requirePurchase = !"0".equals(param.getValue());
        }
        for (String id : ids) {
            MallOrdersPrize orders = this.findById(id);

            if (Objects.isNull(orders)) {
                continue;
            }

            // 强制发货
            boolean ignorePurchase = !requirePurchase && orders.getStatus() == 1;
            if ((orders.getStatus() == 2 && orders.getPurchStatus() == 1)
                    || ignorePurchase) {
                orders.setShipmentProvider(Types.orValue(shipmentProvider, ""));
                orders.setShipmentTradeNo(Types.orValue(shipmentTradeNo, ""));
                if (confirmDays > 0) {
                    orders.setFinishAutoTime(Times.unix() + (long) confirmDays * 24 * 60 * 60);
                } else {
                    orders.setFinishAutoTime(0L);
                }
                orders.setStatus(3);
                orders.setUpTime(new Date().getTime());
                this.getHibernateTemplate().update(orders);
                this.saveOrderLog(orders.getPartyId(), orders.getId().toString(), OrderStatusEnum.ORDER_SEND_CONFIRM, "订单" + orders.getId().toString() + "已发货，正在运输中");
            } else {
                throw new BusinessException("订单:" + orders.getId() + "发货失败，" + "请先采购订单！");
            }
        }
    }

    private void saveOrderLog(String partyId, String orderId, OrderStatusEnum orderStatusEnum, String log) {

        OrderLog orderLog = new OrderLog();
        orderLog.setOrderId(orderId);
        orderLog.setState(orderStatusEnum.getCode());
        orderLog.setPartyId(partyId);
        orderLog.setLog(log);
        orderLogService.saveSync(orderLog);
    }

    @Override
    public void updateFreedCol(String[] ids, String partyId) {
        int i = 0;
        for (String id : ids) {
            MallOrdersPrize orders = this.findById(id);
            if (Objects.isNull(orders)) {
                continue;
            }
            if (orders.getStatus() >= 4 && orders.getStatus() <= 5 && orders.getProfitStatus() == 0 && orders.getReturnStatus() == 0) {
                Double mall_autoProfit = sysParaService.find("mall_autoProfit").getDouble();
                orders.setUpTime(orders.getUpTime() - (long) (60 * 60 * 1000 * mall_autoProfit));
                this.getHibernateTemplate().update(orders);
                i++;
//                this.getHibernateTemplate().flush();
            } else if (!(orders.getStatus() >= 4 && orders.getStatus() <= 5)) {
                throw new BusinessException("订单：" + orders.getId() + "佣金释放失败，释放条件：已收货或已评价状态！");
            } else if (orders.getProfitStatus() == 1) {
                throw new BusinessException("订单：" + orders.getId() + "佣金已经发放，请勿重复释放拥挤！");
            }
        }
        logger.info("释放佣金成功，共释放{}条记录,订单号:{}", i, ids);
    }

    @Override
    public void updateReceiptCol(String[] ids) {
        for (String id : ids) {
            MallOrdersPrize orders = this.findById(id);
            if (Objects.isNull(orders)) {
                continue;
            }
            if (orders.getStatus() == 3 || orders.getStatus() == 2) {
                orders.setStatus(4);
                orders.setUpTime(System.currentTimeMillis());
                this.getHibernateTemplate().update(orders);
                this.saveOrderLog(orders.getPartyId(), orders.getId().toString(), OrderStatusEnum.ORDER_SEND_CONFIRM, "订单" + orders.getId() + "订单已签收");
            } else {
                throw new BusinessException("订单：" + orders.getId() + "未在待收货状态(" + orders.getStatus() + ")，无法收货");
            }
        }
    }

    public List<MallOrdersPrize> updateCancelOrder(String[] ids, String partyId, String username_login) {
        List<MallOrdersPrize> effectedOrderList = new ArrayList<>();
        for (String id : ids) {
            MallOrdersPrize order = this.findById(id);
            if (Objects.isNull(order)) {
                continue;
            }
            if (order.getPayStatus() != 1) {
                throw new BusinessException("订单号[" + order.getId() + "]未付款,操作失败");
            }
            if (order.getStatus() > 3) {
                throw new BusinessException("订单号[" + order.getId() + "],该订单状态无法操作");
            }
            if (order.getStatus() == 3) {
                Integer mall_returnTimeOut = sysParaService.find("mall_returnTimeOut").getInteger();
                if (Objects.isNull(order.getUpTime()) || order.getUpTime() < System.currentTimeMillis() - (long) (60 * 60 * 1000 * mall_returnTimeOut)) {
                    throw new BusinessException("订单号[" + order.getId().toString() + "],订单已经发货，无法退款");
                }
            }
            order.setStatus(6);
            order.setReturnStatus(1);
            order.setRefundTime(new Date());
            //1-未收到货 2-不喜欢，不想要 3-卖家发错货 4-假冒品牌 5-少发、漏发 6-收到商品破损 7-存在质量问题 8-与商家协商一致退款 9-其他原因
            order.setReturnReason(StringUtils.RandomStr(new String[]{"1", "2", "3", "5", "6", "7", "8", "9"}));
            this.saveOrderLog(partyId, order.getId().toString(), OrderStatusEnum.REFUND, "订单" + order.getId() + "平台人工发起退款");

            SecUser sec = this.secUserService.findUserByPartyId(partyId);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setExtra(order.getId().toString());
            log.setUsername(sec.getUsername());
            log.setOperator(username_login);
            log.setPartyId(order.getPartyId());
            log.setLog("管理员手动发起退款申请，订单号[" + order.getId().toString() + "]");
            logService.saveSync(log);

            getHibernateTemplate().update(order);
            effectedOrderList.add(order);
        }

        return effectedOrderList;
    }

    @Override
    public void updateManualReceipt(String[] ids) {

        for (String id : ids) {
            MallOrdersPrize orders = this.findById(id);
            if (Objects.isNull(orders)) {
                continue;
            }
            if (orders.getStatus() <= 3) {
                orders.setManualReceiptStatus(1);
                orders.setUpTime(System.currentTimeMillis());
                this.getHibernateTemplate().update(orders);
            } else {
                throw new BusinessException("订单：" + orders.getId() + "已在收货状态，无法设置手动收货");
            }
        }
    }

    @Override
    public void updateManualShip(String[] ids) {
        //type 1-删除 0-取消删除
        for (String id : ids) {
            MallOrdersPrize orders = this.findById(id);
            if (Objects.isNull(orders)) {
                continue;
            }
            if (orders.getStatus() <= 2) {
                orders.setManualShipStatus(1);
                orders.setUpTime(System.currentTimeMillis());
                this.getHibernateTemplate().update(orders);
            } else {
                throw new BusinessException("订单：" + orders.getId() + "已在发货状态，无法设置手动发货");
            }
        }
    }

    @Override
    public void deleteOrders(String[] ids, int type) {
        //type 1-删除 0-取消删除
        for (String id : ids) {
            MallOrdersPrize orders = this.findById(id);
            if (Objects.isNull(orders)) {
                continue;
            }
            if (type == 1) {
                if (orders.getIsDelete() == 0) {
                    orders.setIsDelete(type);
                    this.getHibernateTemplate().update(orders);
                }
            } else if (type == 0) {
                if (orders.getIsDelete() == 1) {
                    orders.setIsDelete(type);
                    this.getHibernateTemplate().update(orders);
                }
            }

        }
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }

    public void setOrderLogService(OrderLogService orderLogService) {
        this.orderLogService = orderLogService;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setAdminSellerService(AdminSellerService adminSellerService) {
        this.adminSellerService = adminSellerService;
    }

    public void setSysParaService(SysparaService sysParaService) {
        this.sysParaService = sysParaService;
    }
}