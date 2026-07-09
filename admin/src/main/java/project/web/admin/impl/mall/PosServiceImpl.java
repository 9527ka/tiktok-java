package project.web.admin.impl.mall;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import ext.Strings;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.mall.orders.model.MallAddress;
import project.web.admin.service.mall.PosService;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @BelongsProject: code
 * @BelongsPackage: project.web.admin.impl.mall
 * @Author: tangpeng
 * @CreateTime: 2024-11-04  21:51
 * @Description: TODO
 * @Version: 1.0
 */
public class PosServiceImpl  extends HibernateDaoSupport implements PosService {
    private PagedQueryDao pagedQueryDao;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private NamedParameterJdbcOperations namedParameterJdbcTemplate;
    /**
     * POS下单商品查询
     *
     * @param pageNo
     * @param pageSize
     * @param goodsName
     * @param goodsId
     * @param sellerId
     * @param sellerName
     */
    @Override
    public Page pagedQuery(int pageNo, int pageSize, String goodsName, String goodsId, String sellerId, String sellerName) {
        StringBuffer queryString = new StringBuffer("        SELECT\n" +
                "        tmsg.IMG_URL_1 imgUrl, msg.GOODS_ID goodsId, p.USERCODE sellerId,\n" +
                "        msg.SELLING_PRICE sellingPrice, msgl.NAME goodsName, msg.UUID id ,ms.`NAME` sellerName\n" +
                "        FROM T_MALL_SELLER_GOODS msg\n" +
                "        LEFT JOIN T_MALL_SYSTEM_GOODS tmsg ON tmsg.UUID = msg.GOODS_ID\n" +
                "        LEFT JOIN T_MALL_SYSTEM_GOODS_LANG msgl ON msg.GOODS_ID = msgl.GOODS_ID\n" +
                "        LEFT JOIN T_MALL_SELLER ms ON msg.SELLER_ID = ms.UUID\n" +
                "        LEFT JOIN PAT_PARTY p ON msg.SELLER_ID = p.UUID\n" +
                "        where " +
                "       msg.IS_SHELF = '1' and msg.IS_VALID = 1");
               /// "            msgl.LANG = 'en' and msg.IS_SHELF = '1' and msg.IS_VALID = 1");

        if (StrUtil.isNotBlank(goodsName)) {
            queryString.append(StrUtil.format("  and msgl.NAME like concat('%','{}','%')",goodsName));
        }
        if (StrUtil.isNotBlank(goodsId)) {
            queryString.append(StrUtil.format("  and msg.GOODS_ID = '{}' ",goodsId));
        }
        if (StrUtil.isNotBlank(sellerId)) {
            queryString.append(StrUtil.format("  and p.USERCODE = '{}' ",sellerId));
        }
        if (StrUtil.isNotBlank(sellerName)) {
            queryString.append(StrUtil.format("  and ms.NAME like concat('%','{}','%')",sellerName));
        }
        queryString.append(" GROUP BY msg.UUID\n" +
                        "ORDER BY msg.CREATE_TIME");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), null);
        return page;
    }

    /**
     * POS下单记录查询
     *
     * @param pageNo
     * @param pageSize
     * @param agentPartyId
     * @param sellerName
     */
    @Override
    public Page historyPagedQuery(int pageNo, int pageSize, String agentPartyId, String sellerName) {
        StringBuffer queryString = new StringBuffer(
                        "SELECT t.*,msg.GOODS_ID goodsId,SELLING_PRICE price,s.`NAME` sellerName," +
                                "(SELECT IFNULL(USERNAME,'-') FROM pat_party WHERE uuid=t.partyId LIMIT 1) as username," +
                                "(SELECT IFNULL(USERCODE,'-') FROM pat_party WHERE uuid=s.uuid LIMIT 1) as sellerUserCode," +
                                "(SELECT IFNULL(EN_TITLE,'-') FROM t_mall_system_goods where uuid=msg.GOODS_ID LIMIT 1) as goodsName" +
                                " FROM (SELECT id,DELAY delay,PARTY_ID partyId ,SELLER_ID sellerId,COUNT count," +
                                "AMOUNT amount,`STATUS` status, create_time, IFNULL(REFUND_STATUS,0) refundStatus," +
                                "SUBSTRING_INDEX(GOOD_INFO,',',1) goodId \n" +
                                "FROM t_mall_order_task) t \n" +
                                "LEFT  JOIN t_mall_seller_goods msg ON msg.UUID = t.goodId \n" +
                                "LEFT  JOIN t_mall_seller s on s.UUID = t.sellerId \n" +
                                "where 1=1\n"
                      );
        if (StrUtil.isNotBlank(sellerName)) {
            queryString.append(StrUtil.format("  AND POSITION('{}' in s.NAME)",
                    sellerName.trim()));
        }
        if(!Strings.isNullOrEmpty(agentPartyId)){
            queryString.append(" AND s.uuid IN (select PARTY_ID FROM pat_user_recom WHERE RECO_ID='").append(agentPartyId).append("')");
        }
        queryString.append(" order by create_time desc,delay desc");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), null);
        return page;
    }

    /**
     * POS下单买家查询
     */
    @Override
    public List<MallAddress> getPosUserList() {
        String queryString = "        SELECT\n" +
                "               u.CONTACTS contacts,u.UUID addressId,u.COUNTRY  country,u.PHONE  phone, p.UUID partyId\n" +
                "        FROM T_MALL_USERADDRESS u LEFT JOIN PAT_PARTY p ON u.PARTY_ID = p.UUID\n" +
                "        WHERE p.ROLE_TYPE = 0 AND u.COUNTRY_ID != 0 AND p.ROLENAME = 'GUEST' " +
                "        AND u.CITY is not null and u.POSTCODE is not null and u.PROVINCE is not null and u.PHONE is not null \n" +
                "        AND u.STATUS = 1\n" +
                "        AND p.AUTO_COMMENT = 'Y'\n" +
                "        AND u.COUNTRY_ID is not  null\n" +
                "        AND u.COUNTRY is not null\n" +
                "        ORDER BY u.CREATE_TIME limit 5000";
        List<Map<String, Object>> queryForList = namedParameterJdbcTemplate.queryForList(queryString, new HashMap<>());
        return BeanUtil.copyToList(queryForList, MallAddress.class);
    }

    /**
     * 用户地址查询
     *
     * @param partyId
     */
    @Override
    public List<MallAddress> getAddressByPartyId(String partyId) {
        String queryString = "        SELECT\n" +
                "               u.CONTACTS contacts,u.COUNTRY  country,u.PHONE  phone, u.UUID id, p.UUID partyId\n" +
                "        FROM T_MALL_USERADDRESS u LEFT JOIN PAT_PARTY p ON u.PARTY_ID = p.UUID\n" +
                "        WHERE u.PARTY_ID = '" + partyId + "'" +
                "        AND p.ROLE_TYPE = 0 AND u.COUNTRY_ID != 0 AND p.ROLENAME = 'GUEST'\n" +
                "        AND u.STATUS = 1\n" +
                "        AND p.AUTO_COMMENT = 'Y'\n" +
                "        AND u.COUNTRY_ID is not  null\n" +
                "        AND u.COUNTRY is not null\n" +
                "        ORDER BY u.CREATE_TIME";
        List<Map<String, Object>> queryForList = namedParameterJdbcTemplate.queryForList(queryString, new HashMap<>());
        return BeanUtil.copyToList(queryForList, MallAddress.class);
    }

    /**
     * 用户地址详情查询
     *
     * @param addressId
     */
    @Override
    public MallAddress getAddressById(String addressId) {
            List list = getHibernateTemplate().find("FROM MallAddress WHERE id=?0 ",
                    new Object[] { addressId });
            if (list.size() > 0) {
                return (MallAddress) list.get(0);
            }
            return null;

    }

    /**
     * 删除POS任务: 退钱 + 退销量 + 软删真实订单(仅有关联的未来单) + 删除POS日志.
     * 用 REFUND_STATUS 原子认领保证退款至多一次(防重复退/重复点击).
     *
     * @param id
     */
    @Override
    public void deleteHistory(String id, String reason) {
        // 退单: 退钱+退销量+给真实订单打退款标记, POS日志行保留并标记 REFUND_STATUS=1(已退单), 不删除.
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(
                "SELECT PARTY_ID, SELLER_ID, `COUNT` cnt, AMOUNT, ORDER_ID, GOOD_INFO FROM t_mall_order_task WHERE id = :id",
                MapUtil.of("id", id));
        if (rows.isEmpty()) {
            return;
        }
        // 原子认领: 仅当 REFUND_STATUS=0 时本次才执行退款, 防重复退; 认领后该行永久标记为已退单, 并记录退货原因
        Map<String, Object> claimParam = new HashMap<>();
        claimParam.put("id", id);
        claimParam.put("reason", reason == null ? "" : reason);
        int claimed = namedParameterJdbcTemplate.update(
                "UPDATE t_mall_order_task SET REFUND_STATUS = 1, REFUND_REASON = :reason WHERE id = :id AND IFNULL(REFUND_STATUS,0) = 0",
                claimParam);
        if (claimed == 1) {
            refundOrderTask(rows.get(0), reason);
        }
    }

    /** 退钱给买家 + 退销量给卖家 + 给关联真实订单打退款标记(保留在卖家待采购, 不隐藏) */
    private void refundOrderTask(Map<String, Object> t, String reason) {
        String partyId = asStr(t.get("PARTY_ID"));
        String sellerId = asStr(t.get("SELLER_ID"));
        String orderId = asStr(t.get("ORDER_ID"));
        String goodInfo = asStr(t.get("GOOD_INFO"));
        int count = asInt(t.get("cnt"));
        double amount = asDouble(t.get("AMOUNT"));
        // 1) 退钱给买家(下单时扣的)
        if (StrUtil.isNotBlank(partyId) && amount != 0) {
            Map<String, Object> p = new HashMap<>();
            p.put("amt", amount);
            p.put("pid", partyId);
            namedParameterJdbcTemplate.update(
                    "UPDATE T_WALLET SET MONEY = ROUND(IFNULL(MONEY,0) + :amt, 8) WHERE PARTY_ID = :pid", p);
        }
        // 2a) 退店铺总销量 T_MALL_SELLER.SOLD_NUM(收货时加的), 最低 0
        if (StrUtil.isNotBlank(sellerId) && count > 0) {
            Map<String, Object> p = new HashMap<>();
            p.put("cnt", count);
            p.put("sid", sellerId);
            namedParameterJdbcTemplate.update(
                    "UPDATE T_MALL_SELLER SET SOLD_NUM = GREATEST(IFNULL(SOLD_NUM,0) - :cnt, 0) WHERE UUID = :sid", p);
        }
        // 2b) 退商品级销量 T_MALL_SELLER_GOODS.SOLD_NUM(商家确认时加的, 卖家商品列表看的就是它)
        if (count > 0) {
            adjustGoodsSoldNum(goodInfo, -count);
        }
        // 3) 给关联真实订单打退款标记 RETURN_STATUS=2(退款成功), 保持 IS_DELETE=0 不隐藏、STATUS/采购状态不变,
        //    使订单仍保留在卖家"待采购"列表并显示退单信息(仅未来单回写了 ORDER_ID 才有关联)
        if (StrUtil.isNotBlank(orderId)) {
            for (String oid : orderId.split(",")) {
                if (StrUtil.isBlank(oid)) continue;
                Map<String, Object> p = new HashMap<>();
                p.put("uuid", oid.trim());
                p.put("reason", reason == null ? "" : reason);
                namedParameterJdbcTemplate.update(
                        "UPDATE T_MALL_ORDERS_PRIZE SET RETURN_STATUS = 2, RETURN_REASON = :reason WHERE UUID = :uuid",
                        p);
            }
        }
    }

    /**
     * 批量退货: 找出"超过48小时仍未采购"的POS订单(关联真实订单 PURCH_STATUS=0 且下单>48h, 未退单),
     * 逐单退款退销量(复用 refundOrderTask), 并给对应卖家发系统信息提醒。
     */
    @Override
    public List<Map<String, Object>> listTimeoutRefundPreview() {
        // 与 batchRefundTimeout 完全相同的过滤条件, 保证预览 = 实际将退订单
        return namedParameterJdbcTemplate.queryForList(
                "SELECT t.id taskId, t.ORDER_ID orderId, t.AMOUNT amount, t.`COUNT` cnt, " +
                        "o.CREATE_TIME orderTime, TIMESTAMPDIFF(HOUR, o.CREATE_TIME, NOW()) hours, " +
                        "(SELECT s.NAME FROM t_mall_seller s WHERE s.UUID = t.SELLER_ID) sellerName " +
                        "FROM t_mall_order_task t " +
                        "JOIN T_MALL_ORDERS_PRIZE o ON o.UUID = t.ORDER_ID " +
                        "WHERE IFNULL(t.REFUND_STATUS,0) = 0 AND t.ORDER_ID IS NOT NULL AND t.ORDER_ID <> '' " +
                        "AND IFNULL(o.PURCH_STATUS,0) = 0 AND o.CREATE_TIME < DATE_SUB(NOW(), INTERVAL 48 HOUR) " +
                        "ORDER BY o.CREATE_TIME",
                new HashMap<String, Object>());
    }

    @Override
    public int batchRefundTimeout() {
        // 仅实时单回写了 ORDER_ID 才能关联到真实订单的采购状态; 按真实订单 PURCH_STATUS=0 + 下单>48h 过滤
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(
                "SELECT t.id, t.PARTY_ID, t.SELLER_ID, t.`COUNT` cnt, t.AMOUNT, t.ORDER_ID, t.GOOD_INFO " +
                        "FROM t_mall_order_task t " +
                        "JOIN T_MALL_ORDERS_PRIZE o ON o.UUID = t.ORDER_ID " +
                        "WHERE IFNULL(t.REFUND_STATUS,0) = 0 AND t.ORDER_ID IS NOT NULL AND t.ORDER_ID <> '' " +
                        "AND IFNULL(o.PURCH_STATUS,0) = 0 AND o.CREATE_TIME < DATE_SUB(NOW(), INTERVAL 48 HOUR)",
                new HashMap<String, Object>());
        int n = 0;
        Set<String> sellers = new LinkedHashSet<>();
        String reason = "Not purchased within 48 hours";
        for (Map<String, Object> t : rows) {
            String id = asStr(t.get("id"));
            Map<String, Object> cp = new HashMap<>();
            cp.put("id", id);
            cp.put("reason", reason);
            // 原子认领, 防重复退
            int claimed = namedParameterJdbcTemplate.update(
                    "UPDATE t_mall_order_task SET REFUND_STATUS = 1, REFUND_REASON = :reason WHERE id = :id AND IFNULL(REFUND_STATUS,0) = 0",
                    cp);
            if (claimed == 1) {
                refundOrderTask(t, reason);
                n++;
                String sid = asStr(t.get("SELLER_ID"));
                if (StrUtil.isNotBlank(sid)) {
                    sellers.add(sid);
                }
            }
        }
        // 给每个受影响的卖家发一条系统信息提醒(站内信)
        for (String sid : sellers) {
            notifySellerInbox(sid,
                    "Order auto-refunded",
                    "Your order(s) not purchased within 48 hours have been returned and refunded to the buyer.");
        }
        return n;
    }

    /** 直接写入站内信(T_NOTIFICATION, TYPE=3 站内信, STATUS=1 未读), 商家中心铃铛即可看到 */
    private void notifySellerInbox(String sellerPartyId, String title, String content) {
        try {
            Map<String, Object> p = new HashMap<>();
            p.put("uuid", UUID.randomUUID().toString().replace("-", ""));
            p.put("title", title);
            p.put("target", sellerPartyId);
            p.put("content", content);
            p.put("loc", System.currentTimeMillis());
            namedParameterJdbcTemplate.update(
                    "INSERT INTO T_NOTIFICATION (UUID,TITLE,TYPE,LANGUAGE,LOCATION,FROM_USER_ID,TARGET_USER_ID,TARGET_TOPIC,BIZ_TYPE,HANDLER,MODULE,REF_TYPE,CONTENT,STATUS,SEND_TIME,RESERVE_SEND_TIME,VAR_INFO) " +
                            "VALUES (:uuid,:title,3,'en_US',:loc,'0',:target,'0','inbox_pos_batch_refund','default',1,0,:content,1,NOW(),NOW(),'[]')",
                    p);
        } catch (Exception e) {
            logger.error("发送卖家批量退货站内信失败 sellerId={}: {}", sellerPartyId, e.getMessage());
        }
    }

    /**
     * 修改POS任务: 改数量+金额, 按差额补退补扣(钱包按金额差, 销量按数量差).
     */
    @Override
    public void updateOrderTask(String id, Integer count, java.math.BigDecimal amount) {
        List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(
                "SELECT PARTY_ID, SELLER_ID, `COUNT` cnt, AMOUNT, GOOD_INFO FROM t_mall_order_task WHERE id = :id",
                MapUtil.of("id", id));
        if (rows.isEmpty()) {
            return;
        }
        Map<String, Object> t = rows.get(0);
        String partyId = asStr(t.get("PARTY_ID"));
        String sellerId = asStr(t.get("SELLER_ID"));
        String goodInfo = asStr(t.get("GOOD_INFO"));
        int oldCount = asInt(t.get("cnt"));
        double oldAmount = asDouble(t.get("AMOUNT"));
        int newCount = (count == null) ? oldCount : count;
        double newAmount = (amount == null) ? oldAmount : amount.doubleValue();

        // 金额差: 旧-新. 金额变小→退钱给买家(+), 金额变大→从买家扣(-)
        double walletDelta = oldAmount - newAmount;
        if (StrUtil.isNotBlank(partyId) && walletDelta != 0) {
            Map<String, Object> p = new HashMap<>();
            p.put("amt", walletDelta);
            p.put("pid", partyId);
            namedParameterJdbcTemplate.update(
                    "UPDATE T_WALLET SET MONEY = ROUND(IFNULL(MONEY,0) + :amt, 8) WHERE PARTY_ID = :pid", p);
        }
        // 数量差: 新-旧, 同步调整店铺总销量 + 商品级销量, 最低 0
        int soldDelta = newCount - oldCount;
        if (soldDelta != 0) {
            if (StrUtil.isNotBlank(sellerId)) {
                Map<String, Object> p2 = new HashMap<>();
                p2.put("cnt", soldDelta);
                p2.put("sid", sellerId);
                namedParameterJdbcTemplate.update(
                        "UPDATE T_MALL_SELLER SET SOLD_NUM = GREATEST(IFNULL(SOLD_NUM,0) + :cnt, 0) WHERE UUID = :sid", p2);
            }
            adjustGoodsSoldNum(goodInfo, soldDelta);
        }
        // 更新任务记录
        Map<String, Object> p = new HashMap<>();
        p.put("id", id);
        p.put("cnt", newCount);
        p.put("amt", newAmount);
        namedParameterJdbcTemplate.update(
                "UPDATE t_mall_order_task SET `COUNT` = :cnt, AMOUNT = :amt WHERE id = :id", p);
    }

    /**
     * 下单前调用: 取该买家当前最后一单的 CREATE_TIME(字符串), 作为本次下单的"分界点".
     * 下单后用它过滤, 只关联晚于此刻的新订单, 避免把同买家之前的订单也关联进来.
     */
    @Override
    public String maxOrderTime(String partyId) {
        if (StrUtil.isBlank(partyId)) {
            return null;
        }
        try {
            List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(
                    "SELECT MAX(CREATE_TIME) mt FROM T_MALL_ORDERS_PRIZE WHERE PARTY_ID = :pid",
                    MapUtil.of("pid", partyId));
            if (rows.isEmpty()) return null;
            Object mt = rows.get(0).get("mt");
            return mt == null ? null : mt.toString();
        } catch (Exception e) {
            logger.error("maxOrderTime error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 下单后查该买家本次新生成的真实订单 UUID(逗号分隔), 用于回写关联(仅实时单可靠).
     * @param sinceExclusive 下单前的分界时间(maxOrderTime 返回值); 为空则回退到近2分钟.
     */
    @Override
    public String findRecentOrderIds(String partyId, String sinceExclusive) {
        if (StrUtil.isBlank(partyId)) {
            return null;
        }
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("pid", partyId);
            String timeCond;
            if (StrUtil.isNotBlank(sinceExclusive)) {
                timeCond = "AND CREATE_TIME > :since ";
                params.put("since", sinceExclusive);
            } else {
                timeCond = "AND CREATE_TIME >= DATE_SUB(NOW(), INTERVAL 2 MINUTE) ";
            }
            List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(
                    "SELECT UUID FROM T_MALL_ORDERS_PRIZE WHERE PARTY_ID = :pid " +
                            timeCond + "AND IFNULL(IS_DELETE,0) = 0 " +
                            "ORDER BY CREATE_TIME DESC LIMIT 50",
                    params);
            StringBuilder sb = new StringBuilder();
            for (Map<String, Object> r : rows) {
                Object u = r.get("UUID");
                if (u != null) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(u.toString());
                }
            }
            return sb.length() == 0 ? null : sb.toString();
        } catch (Exception e) {
            logger.error("findRecentOrderIds error: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 调整商品级销量 T_MALL_SELLER_GOODS.SOLD_NUM(卖家商品列表看的字段).
     * delta 为有符号总量(删除时传负数回退, 修改时传数量差), 按 GOOD_INFO 内商品均摊(余数归首个商品),
     * 单商品时即精确等于 delta. 最低 0.
     */
    private void adjustGoodsSoldNum(String goodInfo, int delta) {
        if (StrUtil.isBlank(goodInfo) || delta == 0) {
            return;
        }
        java.util.List<String> ids = new java.util.ArrayList<>();
        for (String g : goodInfo.split(",")) {
            if (StrUtil.isNotBlank(g)) ids.add(g.trim());
        }
        int n = ids.size();
        if (n == 0) return;
        int per = delta / n;
        int rem = delta - per * n; // 余数(与 delta 同号), 全部计入第一个商品
        for (int i = 0; i < n; i++) {
            int qty = per + (i == 0 ? rem : 0);
            if (qty == 0) continue;
            Map<String, Object> p = new HashMap<>();
            p.put("qty", qty);
            p.put("gid", ids.get(i));
            namedParameterJdbcTemplate.update(
                    "UPDATE T_MALL_SELLER_GOODS SET SOLD_NUM = GREATEST(IFNULL(SOLD_NUM,0) + :qty, 0) WHERE UUID = :gid", p);
        }
    }

    private static String asStr(Object o) {
        return o == null ? null : o.toString();
    }

    private static int asInt(Object o) {
        if (o == null) return 0;
        if (o instanceof Number) return ((Number) o).intValue();
        try { return Integer.parseInt(o.toString().trim()); } catch (Exception e) { return 0; }
    }

    private static double asDouble(Object o) {
        if (o == null) return 0d;
        if (o instanceof Number) return ((Number) o).doubleValue();
        try { return Double.parseDouble(o.toString().trim()); } catch (Exception e) { return 0d; }
    }

    @Override
    public void saveOrderTaskLog(String partyId, String sellerId, String goodInfo, int count, java.math.BigDecimal amount, int status, String orderId) {
        try {
            // 若未传 sellerId, 用第一个商品反查 T_MALL_SELLER_GOODS.SELLER_ID
            if (StrUtil.isBlank(sellerId) && StrUtil.isNotBlank(goodInfo)) {
                String firstGoodId = goodInfo.split(",")[0];
                try {
                    List<Map<String, Object>> rows = namedParameterJdbcTemplate.queryForList(
                            "SELECT SELLER_ID FROM T_MALL_SELLER_GOODS WHERE UUID = :uuid LIMIT 1",
                            MapUtil.of("uuid", firstGoodId));
                    if (!rows.isEmpty()) {
                        Object sid = rows.get(0).get("SELLER_ID");
                        if (sid != null) sellerId = sid.toString();
                    }
                } catch (Exception ignore) {}
            }

            String sql = "INSERT INTO t_mall_order_task (ID, ORDER_ID, DELAY, PARTY_ID, GOOD_INFO, SELLER_ID, COUNT, AMOUNT, STATUS, create_time) " +
                    "VALUES (:id, :orderId, NULL, :partyId, :goodInfo, :sellerId, :cnt, :amt, :status, NOW())";
            Map<String, Object> params = new HashMap<>();
            params.put("id", cn.hutool.core.util.IdUtil.simpleUUID());
            params.put("orderId", orderId);
            params.put("partyId", partyId);
            params.put("goodInfo", goodInfo);
            params.put("sellerId", sellerId);
            params.put("cnt", count);
            params.put("amt", amount);
            params.put("status", status);
            namedParameterJdbcTemplate.update(sql, params);
        } catch (Exception ex) {
            logger.error("POS 日志写入失败: {}", ex.getMessage());
        }
    }

    public PagedQueryDao getPagedQueryDao() {
        return pagedQueryDao;
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public NamedParameterJdbcOperations getNamedParameterJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    public void setNamedParameterJdbcTemplate(NamedParameterJdbcOperations namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }
}
