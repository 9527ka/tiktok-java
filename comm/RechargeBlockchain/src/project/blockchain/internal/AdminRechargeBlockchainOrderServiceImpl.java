package project.blockchain.internal;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;

import kernel.util.DateUtils;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.Constants;
import project.blockchain.AdminRechargeBlockchainOrderService;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.log.Log;
import project.log.LogService;
import project.party.recom.UserRecomService;
import project.tip.TipService;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import security.SecUser;
import security.internal.SecUserService;

public class AdminRechargeBlockchainOrderServiceImpl extends HibernateDaoSupport
        implements AdminRechargeBlockchainOrderService {
    private final Logger debugLogger = LoggerFactory.getLogger(this.getClass());

    private PagedQueryDao pagedQueryDao;
    private UserRecomService userRecomService;
    private PasswordEncoder passwordEncoder;
    private RechargeBlockchainService rechargeBlockchainService;

    private LogService logService;
    private WalletLogService walletLogService;
    private SecUserService secUserService;
    private TipService tipService;

    @Override

    public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer state_para, String loginPartyId,
                           String orderNo, String rolename_para, String startTime, String endTime, String reviewStartTime, String reviewEndTime) {
        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT");
		queryString.append(" party.USERNAME username ,party.ROLENAME rolename,party.USERCODE usercode, party.REMARKS remarks, ");
		queryString.append(" recharge.UUID id,recharge.ORDER_NO order_no,recharge.BLOCKCHAIN_NAME blockchanin_name, "
                + "recharge.IMG img, recharge.TX hash, recharge.CREATED created, recharge.DESCRIPTION description, ");

        queryString.append(" recharge.COIN coin,recharge.REVIEWTIME reviewTime, recharge.AMOUNT amount, "
                + "recharge.SUCCEEDED succeeded,recharge.CHANNEL_AMOUNT channel_amount, recharge.RECHARGE_COMMISSION rechargeCommission,"
                + "recharge.ADDRESS address,recharge.CHANNEL_ADDRESS channel_address, party_parent.USERNAME username_parent,recharge.IS_THIRD_PARTY as isThirdParty");
        queryString.append(" FROM ");
        queryString.append(
                " T_RECHARGE_BLOCKCHAIN_ORDER recharge "
                        + "LEFT JOIN PAT_PARTY party ON recharge.PARTY_ID = party.UUID "
                        + " LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  "
                        + "  LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID   "
                        + "  ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();

        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            List children = this.userRecomService.findChildren(loginPartyId);
            if (children.size() == 0) {
//				return Page.EMPTY_PAGE;
                return new Page();
            }
            queryString.append(" and recharge.PARTY_ID in (:children) ");
            parameters.put("children", children);
        }

//		if (!StringUtils.isNullOrEmpty(name_para)) {
//			queryString.append(" and (party.USERNAME like :name_para or party.USERCODE =:usercode)  ");
//			parameters.put("name_para", "%" + name_para + "%");
//			parameters.put("usercode", name_para);
//
//		}
        if (!StringUtils.isNullOrEmpty(name_para)) {
            queryString.append("AND (party.USERNAME like:username OR party.USERCODE like:username ) ");
            parameters.put("username", "%" + name_para + "%");
        }
        if (!StringUtils.isNullOrEmpty(rolename_para)) {
            queryString.append(" and   party.ROLENAME =:rolename");
            parameters.put("rolename", rolename_para);
        }
        if (!StringUtils.isNullOrEmpty(orderNo)) {
            queryString.append(" and recharge.ORDER_NO = :orderNo  ");
            parameters.put("orderNo", orderNo);

        }
        if (state_para != null) {
            queryString.append(" and recharge.SUCCEEDED = :succeeded  ");
            parameters.put("succeeded", state_para);

        }

        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(recharge.CREATED) >= DATE(:startTime)  ");
            parameters.put("startTime", DateUtils.toDate(startTime));
            queryString.append(" AND DATE(recharge.CREATED) <= DATE(:endTime)  ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }

        if (!StringUtils.isNullOrEmpty(reviewStartTime) && !StringUtils.isNullOrEmpty(reviewEndTime)) {
            queryString.append(" AND DATE(recharge.REVIEWTIME) >= DATE(:reviewStartTime)  ");
            parameters.put("reviewStartTime", DateUtils.toDate(reviewStartTime));

            queryString.append(" AND DATE(recharge.REVIEWTIME) <= DATE(:reviewEndTime)  ");
            parameters.put("reviewEndTime", DateUtils.toDate(reviewEndTime));
        }
        queryString.append(" order by recharge.CREATED desc ");
        Page page = this.pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);

        return page;
    }

    @Override
    public Map saveSucceeded(String order_no, String safeword, String operator_username, String transfer_usdt, String success_amount, double rechargeCommission,String remarks) {
        SecUser sec = this.secUserService.findUserByLoginName(operator_username);
        String sysSafeword = sec.getSafeword();

        String safeword_md5 = passwordEncoder.encodePassword(safeword, operator_username);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("资金密码错误");
        }
        Map map = rechargeBlockchainService.saveSucceeded(order_no, operator_username, transfer_usdt, success_amount, rechargeCommission,remarks);
        try {
            rechargeBlockchainService.updateFirstSuccessRecharge(order_no);
        } catch (Exception e) {
            logger.error("判断首充礼金报错，报错信息为：" , e);
        }
//        首次充值满足条件赠送邀请礼金
        try {
            rechargeBlockchainService.updateFirstSuccessInviteReward(order_no);
        } catch (Exception e) {
            logger.error("判断邀请奖励报错，报错信息为：" , e);
        }
        return map;
    }

    /**
     * 某个时间后未处理订单数量,没有时间则全部
     *
     * @param time
     * @return
     */
    public Long getUntreatedCount(Date time, String loginPartyId) {
        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT COUNT(*) FROM RechargeBlockchain WHERE succeeded=0 ");
        List<Object> para = new ArrayList<Object>();
        if (!StringUtils.isNullOrEmpty(loginPartyId)) {
            String childrensIds = this.userRecomService.findChildrensIds(loginPartyId);
            if (StringUtils.isEmptyString(childrensIds)) {
                return 0L;
            }
            queryString.append(" and partyId in (" + childrensIds + ") ");
        }
        if (null != time) {
            queryString.append("AND created > ?");
            para.add(time);
        }
        List find = this.getHibernateTemplate().find(queryString.toString(), para.toArray());
        return CollectionUtils.isEmpty(find) ? 0L : find.get(0) == null ? 0L : Long.valueOf(find.get(0).toString());
    }

    @Override
    public RechargeBlockchain get(String id) {
        return this.getHibernateTemplate().get(RechargeBlockchain.class, id);
    }

    @Override
    public void saveReject(String id, String failure_msg, String userName, String partyId) {
        RechargeBlockchain recharge = this.get(id);

        if (recharge.getIsThirdParty() == 1) {
            throw new BusinessException("第三方充值订单不可人工审核");
        }

        // 通过后不可驳回
        if (recharge.getSucceeded() == 2 || recharge.getSucceeded() == 1) {
            return;
        }
        Date date = new Date();
        recharge.setReviewTime(date);

        recharge.setSucceeded(2);
        recharge.setDescription(failure_msg);
        this.getHibernateTemplate().update(recharge);

        WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLogService.update(walletLog);

        SecUser sec = this.secUserService.findUserByPartyId(recharge.getPartyId());

        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(recharge.getOrder_no());
        log.setUsername(sec.getUsername());
        log.setOperator(userName);
        log.setPartyId(partyId);
        log.setLog("管理员驳回一笔充值订单。充值订单号[" + recharge.getOrder_no() + "]，驳回理由[" + recharge.getDescription() + "]。");

        logService.saveSync(log);
        tipService.deleteTip(id);
        debugLogger.info("-----> 充值订单:{} 审核拒绝，提交了相关的提示消息删除请求", id);
    }

    @Override
    public void saveRejectRemark(String id, String failure_msg, String userName, String partyId) {
        RechargeBlockchain recharge = this.get(id);
        String before_failure_msg = recharge.getDescription();

        recharge.setDescription(failure_msg);
        this.getHibernateTemplate().update(recharge);

        SecUser sec = this.secUserService.findUserByPartyId(recharge.getPartyId());

        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(recharge.getOrder_no());
        log.setUsername(sec.getUsername());
        log.setOperator(userName);
        log.setPartyId(partyId);
        log.setLog("管理员修改备注信息。充值订单号[" + recharge.getOrder_no() + "]，修改前备注信息[" + before_failure_msg + "]，修改后备注信息[" + recharge.getDescription() + "]。");

        logService.saveSync(log);
    }

    @Override
    public void saveRechargeImg(String id, String img, String safeword, String userName, String partyId) {
        SecUser sec = this.secUserService.findUserByLoginName(userName);
        String sysSafeword = sec.getSafeword();

        String safeword_md5 = passwordEncoder.encodePassword(safeword, userName);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("资金密码错误");
        }

        RechargeBlockchain recharge = this.get(id);
        String before_img = "为空";
        if (!StringUtils.isEmptyString(recharge.getImg())) {
            before_img = recharge.getImg();
        }


        recharge.setImg(img);
        this.getHibernateTemplate().update(recharge);

        SecUser secUser = secUserService.findUserByPartyId(recharge.getPartyId());


        Log log = new Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setExtra(recharge.getOrder_no());
        log.setUsername(secUser.getUsername());
        log.setOperator(userName);
        log.setPartyId(secUser.getPartyId());
        log.setLog("管理员修改用户充值订单上传截图信息。充值订单号[" + recharge.getOrder_no() + "]，修改前图片[" + before_img + "]，修改后图片[" + recharge.getImg() + "]。");

        logService.saveSync(log);
    }


    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setUserRecomService(UserRecomService userRecomService) {
        this.userRecomService = userRecomService;
    }

    public void setRechargeBlockchainService(RechargeBlockchainService rechargeBlockchainService) {
        this.rechargeBlockchainService = rechargeBlockchainService;
    }

    public void setLogService(LogService logService) {
        this.logService = logService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }

    public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }

}
