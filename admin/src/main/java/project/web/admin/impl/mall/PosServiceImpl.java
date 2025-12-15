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
import java.util.List;
import java.util.Map;

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
                                "AMOUNT amount,`STATUS` status, create_time," +
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
     * 删除POS任务
     *
     * @param id
     */
    @Override
    public void deleteHistory(String id) {
        namedParameterJdbcTemplate.update("delete from T_MALL_ORDER_TASK where id = :id", MapUtil.of("id", id));
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
