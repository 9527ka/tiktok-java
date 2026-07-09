package project.user.kyc.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.query.NativeQuery;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.tip.TipService;
import project.user.kyc.AdminKycService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.Wallet;
import project.wallet.WalletService;

public class AdminKycServiceImpl extends HibernateDaoSupport implements AdminKycService {
    protected PagedQueryDao pagedQueryDao;
    protected PartyService partyService;
    protected UserRecomService userRecomService;
    protected KycService kycService;
    protected TipService tipService;
    protected WalletService walletService;
    protected SysparaService sysparaService;
    protected JdbcTemplate jdbcTemplate;
    private NotificationHelperClient notificationHelperClient;

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name_para, String status_para, String rolename_para,
                           String checkedPartyId, String idnumber_para, String email_para, String startTime, String endTime, String sellerName, String username_parent, String roleType_para) {
        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT");
        queryString.append(
                "  party.UUID partyId,party.USERCODE usercode,party.USERNAME username,party.PHONE phone,party.EMAIL email,party.ROLENAME rolename, party.REMARKS remark,");
        queryString.append(
                " kyc.UUID id,kyc.IDNUMBER idnumber,kyc.IDNAME idname,kyc.NAME name,seller.NAME sellerName,seller.AVATAR sellerImg,seller.SHOP_ADDRESS sellerAddress,"
                        + "kyc.IDIMG_1 idimg_1,kyc.IDIMG_2 idimg_2,kyc.IDIMG_3 idimg_3,kyc.APPLY_TIME apply_time,kyc.OPERATION_TIME operation_time, kyc.REMARK remarks,"
                        + "kyc.STATUS status,kyc.MSG msg,kyc.nationality nationality ,party_parent.USERNAME username_parent");
        queryString.append(" FROM");
        queryString.append(" T_KYC kyc LEFT JOIN PAT_PARTY party ON kyc.PARTY_ID = party.UUID   ");
        queryString.append("  LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID   ");
        queryString.append("  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   ");
        queryString.append("  LEFT JOIN T_MALL_SELLER seller ON party.UUID = seller.UUID   ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<>();

        if (!StringUtils.isNullOrEmpty(checkedPartyId)) {

            List<String> checked_list = this.userRecomService.findChildren(checkedPartyId);
            checked_list.add(checkedPartyId);
            if (checked_list.size() == 0) {
                return Page.EMPTY_PAGE;
            }

            queryString.append(" and party.UUID in(:checked_list)");
            parameters.put("checked_list", checked_list);

        }
        if (!StringUtils.isNullOrEmpty(sellerName)) {
            queryString.append(" AND trim(replace(seller.`NAME`,' ','')) like:sellerName ");
            sellerName = sellerName.replace(" ", "");
            parameters.put("sellerName", "%" + sellerName + "%");
        }
        if (!StringUtils.isNullOrEmpty(username_parent)) {
            queryString.append(" AND trim(replace(party_parent.USERNAME,' ','')) like:username_parent ");
            username_parent = username_parent.replace(" ", "");
            parameters.put("username_parent", "%" + username_parent + "%");
        }
        if (!StringUtils.isNullOrEmpty(status_para)) {
            queryString.append(" and kyc.STATUS = :status_para  ");
            parameters.put("status_para", status_para);

        }
        if (!StringUtils.isNullOrEmpty(rolename_para)) {
            queryString.append(" and party.ROLENAME =:rolename");
            parameters.put("rolename", rolename_para);
        }
        // 商家/普通用户筛选: party.ROLE_TYPE 1=商家 0=普通用户(ROLENAME 区分不了, 两类都可为 MEMBER/GUEST)
        if (!StringUtils.isNullOrEmpty(roleType_para) && ("0".equals(roleType_para.trim()) || "1".equals(roleType_para.trim()))) {
            queryString.append(" and party.ROLE_TYPE = :roleType ");
            parameters.put("roleType", Integer.valueOf(roleType_para.trim()));
        }
        if (!StringUtils.isNullOrEmpty(name_para)) {
            queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
            parameters.put("username", "%" + name_para + "%");
        }
        if (!StringUtils.isNullOrEmpty(idnumber_para)) {
            queryString.append("AND  kyc.IDNUMBER =:idnumber_para  ");
            parameters.put("idnumber_para", idnumber_para);
        }
        if (!StringUtils.isNullOrEmpty(email_para)) {
            queryString.append("AND (party.PHONE like:email_para OR party.EMAIL like:email_para ) ");
            parameters.put("email_para", "%" + email_para + "%");
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryString.append(" AND DATE(kyc.APPLY_TIME) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(kyc.APPLY_TIME) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }

        queryString.append(" order by kyc.APPLY_TIME desc ");

        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

        return page;
    }

    @Override
    public Kyc find(Serializable partyId) {
        List<Kyc> list = (List<Kyc>) getHibernateTemplate().find(" FROM Kyc WHERE partyId = ?0", new Object[]{partyId});
        if (list.size() > 0) {
            return (Kyc) list.get(0);
        }
        return null;
    }

    public Seller getSeller(String sellerId) {
        return getHibernateTemplate().get(Seller.class, sellerId);
    }

    @Override
    public void savePassed(String partyId) {
        Kyc kyc = find(partyId);
        if (kyc != null) {
            kyc.setStatus(2);
            kycService.save(kyc);
//			this.getHibernateTemplate().update(kyc);
            tipService.deleteTip(kyc.getId().toString());
        }
        Seller seller = getSeller(partyId);
        if (null == seller) {
            throw new BusinessException("申请通过失败，店铺信息未初始化");
        }
        seller.setCreateTime(new Date());
        seller.setRecTime(0L);
        seller.setStatus(1);
        seller.setCreditScore(100);
        getHibernateTemplate().update(seller);

        Party party = this.partyService.cachePartyBy(partyId, false);
        party.setKyc_authority(true);
        party.setRoleType(1);

        // 获取用户系统等级：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证； 4/用户高级认证；
        int userLevelSystem = this.partyService.getUserLevelByAuth(party);

        // 十进制个位表示系统级别：1/新注册；2/邮箱谷歌手机其中有一个已验证；3/用户实名认证；4/用户高级认证；
        // 十进制十位表示自定义级别：对应在前端显示为如VIP1 VIP2等级、黄金 白银等级；
        // 如：级别11表示：新注册的前端显示为VIP1；
        int userLevel = party.getUser_level();
        party.setUser_level(((int) Math.floor(userLevel / 10)) * 10 + userLevelSystem);

        this.partyService.update(party);
//        TODO 加入注册彩金到字段
        Wallet wallet = walletService.saveWalletByPartyId(partyId);
        Syspara sysparaBonus = sysparaService.find(SysParaCode.MALL_SELLER_SIGN_BONUS.getCode());
//        Syspara sysparaBonusRatio = sysparaService.find(SysParaCode.MALL_SELLER_SIGN_BONUS_RATIO.getCode());
        double sellerSignBonus =0d;
//        double sellerSignBonusRatio =0d;
        try {
            if (sysparaBonus != null) {
                String sellerSignBonusInfo = sysparaBonus.getValue().trim();
                if (StrUtil.isNotBlank(sellerSignBonusInfo)) {
                    sellerSignBonus = Double.parseDouble(sellerSignBonusInfo);
                }
            }
            wallet.setSignBonus(sellerSignBonus);
            walletService.save(wallet);
        } catch (NumberFormatException e) {
            logger.error("系统参数异常",e);
        }
//        if (sysparaBonusRatio != null) {
//            String sellerSignBonusRatioInfo = sysparaBonusRatio.getValue().trim();
//            if (StrUtil.isNotBlank(sellerSignBonusRatioInfo)) {
//                sellerSignBonusRatio = Double.parseDouble(sellerSignBonusRatioInfo);
//            }
//        }

        // 通知商家审核结果
        try {
            notificationHelperClient.notifyStoreAuditByInbox(partyId, 2, seller.getName(), null);
        } catch (Exception e) {
            logger.error("发送通知消息提醒商家下单事件报错:", e);
        }
    }

    @Override
    public void saveFailed(String partyId, String msg) {
        Kyc kyc = find(partyId);
        if (kyc != null) {
            kyc.setStatus(3);
            kyc.setMsg(msg);
            kycService.save(kyc);
//			this.getHibernateTemplate().update(kyc);
            tipService.deleteTip(kyc.getId().toString());

        }
        Seller seller = getSeller(partyId);
        if (null == seller) {
            throw new BusinessException("申请不通过失败，店铺信息未初始化");
        }
        Party party = partyService.cachePartyBy(partyId, false);
        party.setKyc_authority(false);
        partyService.update(party);
        // 通知商家审核结果
        try {
            notificationHelperClient.notifyStoreAuditByInbox(partyId, 3, seller.getName(), msg);
        } catch (Exception e) {
            logger.error("发送通知消息提醒商家下单事件报错:", e);
        }
    }


    @Override
    public void saveFaileds(String partyId, String msg) {
        Kyc kyc = find(partyId);
        if (kyc != null) {
            kyc.setStatus(3);
            kyc.setMsg(msg);
            kycService.save(kyc);
        }

        Seller seller = getSeller(partyId);
        seller.setRecTime(0L);
        seller.setStatus(0);
        seller.setCreditScore(0);
        getHibernateTemplate().update(seller);

        Party party = partyService.cachePartyBy(partyId, false);
        party.setKyc_authority(false);
        party.setRoleType(0);
        partyService.update(party);
//		getHibernateTemplate().flush();
    }

    /**
     * 删除入驻申请：物理清理注册/实名/店铺残留数据，使其可用相同信息重新申请。
     * 仅允许删除“未通过审核”的申请(待审核/已驳回)；已通过启用的店铺禁止从此入口删除。
     * 删除表清单与 LocalUserServiceImpl.deleteByPartyId 保持一致(按子→主顺序)。
     */
    @Override
    public void deleteApply(String partyId) {
        if (StringUtils.isNullOrEmpty(partyId)) {
            throw new BusinessException("参数错误");
        }
        Kyc kyc = find(partyId);
        if (kyc == null) {
            throw new BusinessException("申请信息不存在，可能已被删除");
        }
        // 已通过审核(status=2)的不允许从此入口删除，避免误删活跃商家账号
        if (kyc.getStatus() == 2) {
            throw new BusinessException("该申请已审核通过，不能从此处删除");
        }
        Seller seller = getSeller(partyId);
        if (seller != null && seller.getStatus() != null && seller.getStatus() == 1) {
            throw new BusinessException("该店铺已审核通过并启用，不能删除");
        }
        // 先清审核红点(t_tip)
        if (kyc.getId() != null) {
            tipService.deleteTip(kyc.getId().toString());
        }
        // 与事务在同一连接内执行的 JDBC 删除(adminKycService 已配置为写事务，delete* 前缀)
        jdbcTemplate.update("DELETE FROM t_wallet_extend WHERE PARTY_ID = ?", partyId);
        jdbcTemplate.update("DELETE FROM t_wallet WHERE PARTY_ID = ?", partyId);
        jdbcTemplate.update("DELETE FROM t_userdata WHERE PARTY_ID = ?", partyId);
        jdbcTemplate.update("DELETE FROM t_userdatasum WHERE PARTY_ID = ?", partyId);
        jdbcTemplate.update("DELETE FROM t_token WHERE PARTY_ID = ?", partyId);
        jdbcTemplate.update("DELETE FROM t_kyc WHERE PARTY_ID = ?", partyId);
        jdbcTemplate.update("DELETE FROM t_mall_seller WHERE UUID = ?", partyId);
        jdbcTemplate.update("DELETE FROM pat_user_recom WHERE UUID = ?", partyId);
        jdbcTemplate.update("DELETE FROM pat_user_map WHERE UUID = ?", partyId);
        List<String> secUserIds = jdbcTemplate.queryForList(
                "SELECT UUID FROM sct_user WHERE PARTY_UUID = ?", String.class, partyId);
        for (String secUserId : secUserIds) {
            jdbcTemplate.update("DELETE FROM sct_user_role WHERE USER_UUID = ?", secUserId);
        }
        jdbcTemplate.update("DELETE FROM sct_user WHERE PARTY_UUID = ?", partyId);
        jdbcTemplate.update("DELETE FROM pat_party WHERE UUID = ?", partyId);
    }

    @Override
    public void saveKycPic(String partyId, String imgId, String img) {
        Kyc kyc = find(partyId);
        if (kyc != null) {
            if ("1".equals(imgId)) {
                kyc.setIdimg_1(img);
            } else if ("2".equals(imgId)) {
                kyc.setIdimg_2(img);
            } else if ("3".equals(imgId)) {
                kyc.setIdimg_3(img);
            }
            kycService.save(kyc);
        }
    }

    /**
     * 某个时间后未处理数量,没有时间则全部
     *
     * @param time
     * @return
     */
    public Long getUntreatedCount(Date time, String loginPartyId) {
        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT COUNT(*) FROM Kyc WHERE status in(0,1) ");
        List<Object> para = new ArrayList<Object>();
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            String childrensIds = this.userRecomService.findChildrensIds(loginPartyId);
            if (StringUtils.isEmptyString(childrensIds)) {
                return 0L;
            }
            queryString.append(" and partyId in (" + childrensIds + ") ");
        }
        if (null != time) {
            queryString.append("AND apply_time > ?");
            para.add(time);
        }
        List find = this.getHibernateTemplate().find(queryString.toString(), para.toArray());
        return CollectionUtils.isEmpty(find) ? 0L : find.get(0) == null ? 0L : Long.valueOf(find.get(0).toString());
    }

    public Map<String, Object> findKycSumData() {
        Map<String, Object> sumData = new HashMap<>();
        StringBuffer sql = new StringBuffer(" SELECT ");
        sql.append(" IFNULL(count(k.UUID),0) sellerNum, ");
        sql.append(" MAX(k.UUID) id ");
        sql.append(" FROM ");
        sql.append(" T_KYC k ");
        sql.append(" WHERE to_days(k.OPERATION_TIME) = TO_DAYS(now()) AND k.STATUS = 2 ");
        NativeQuery<Object[]> nativeQuery = this.getHibernateTemplate().getSessionFactory().getCurrentSession().createNativeQuery(sql.toString());
        Object[] results = nativeQuery.getSingleResult();
        sumData.put("todaySellerCount", results[0]);
        sumData.put("id", results[1]);

        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT COUNT(*) FROM Kyc WHERE status = 2 ");
        List<Object> para = new ArrayList<Object>();
        List seller = this.getHibernateTemplate().find(queryString.toString(), para.toArray());
        Long sellerCount = CollectionUtils.isEmpty(seller) ? 0L : seller.get(0) == null ? 0L : Long.valueOf(seller.get(0).toString());
        sumData.put("sellerCount", sellerCount);
        return sumData;
    }

    @Override
    public void updateRemarks(String partyId, String remarks) {
        Kyc kyc = find(partyId);
        if (kyc != null) {
            if (StringUtils.isNotEmpty(remarks)) {
                kyc.setRemark(remarks);
                getHibernateTemplate().update(kyc);
            }
        }
    }

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setKycService(KycService kycService) {
        this.kycService = kycService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }

    public void setNotificationHelperClient(NotificationHelperClient notificationHelperClient) {
        this.notificationHelperClient = notificationHelperClient;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
