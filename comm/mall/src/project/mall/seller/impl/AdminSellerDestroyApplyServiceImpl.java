package project.mall.seller.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PagedQueryDao;
import project.mall.seller.AdminSellerDestroyApplyService;
import project.mall.seller.model.SellerDestroyApply;
import project.tip.TipService;
import project.user.UserService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import project.withdraw.Withdraw;
import project.withdraw.WithdrawService;

/**
 * 商家注销申请审核(后台)实现
 */
public class AdminSellerDestroyApplyServiceImpl extends HibernateDaoSupport implements AdminSellerDestroyApplyService {

    private PagedQueryDao pagedQueryDao;
    private UserService userService;
    private WalletService walletService;
    private WithdrawService withdrawService;
    private TipService tipService;

    public void setPagedQueryDao(PagedQueryDao pagedQueryDao) {
        this.pagedQueryDao = pagedQueryDao;
    }

    public void setUserService(UserService userService) {
        this.userService = userService;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setWithdrawService(WithdrawService withdrawService) {
        this.withdrawService = withdrawService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }

    @Override
    public SellerDestroyApply get(String id) {
        return this.getHibernateTemplate().get(SellerDestroyApply.class, id);
    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String name_para, Integer status, String startTime, String endTime) {
        StringBuffer queryString = new StringBuffer();
        queryString.append("SELECT");
        queryString.append(" apply.UUID id, apply.SELLER_ID sellerId, apply.ACCOUNT account, apply.APPLY_REASON applyReason, ");
        queryString.append(" apply.STATUS status, apply.APPLY_TIME applyTime, apply.REVIEW_TIME reviewTime, ");
        queryString.append(" apply.REVIEW_REMARK reviewRemark, apply.REVIEWER_NAME reviewerName, ");
        queryString.append(" party.USERNAME username, party.USERCODE usercode, party.ROLENAME rolename, seller.NAME shopName ");
        queryString.append(" FROM T_SELLER_DESTROY_APPLY apply ");
        queryString.append(" LEFT JOIN PAT_PARTY party ON apply.SELLER_ID = party.UUID ");
        queryString.append(" LEFT JOIN T_MALL_SELLER seller ON apply.SELLER_ID = seller.UUID ");
        queryString.append(" WHERE 1=1 ");

        Map<String, Object> parameters = new HashMap<String, Object>();

        if (status != null) {
            queryString.append(" AND apply.STATUS = :status ");
            parameters.put("status", status);
        }
        if (!StringUtils.isNullOrEmpty(name_para)) {
            queryString.append(" AND (party.USERNAME LIKE :name_para OR party.USERCODE LIKE :name_para OR apply.ACCOUNT LIKE :name_para) ");
            parameters.put("name_para", "%" + name_para + "%");
        }
        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            queryString.append(" AND DATE(apply.APPLY_TIME) >= DATE(:startTime) ");
            parameters.put("startTime", DateUtils.toDate(startTime));
            queryString.append(" AND DATE(apply.APPLY_TIME) <= DATE(:endTime) ");
            parameters.put("endTime", DateUtils.toDate(endTime));
        }

        queryString.append(" ORDER BY apply.APPLY_TIME DESC ");

        return pagedQueryDao.pagedQuerySQL(pageNo, pageSize, queryString.toString(), parameters);
    }

    @Override
    public void saveApprove(String id, String reviewerName) {
        SellerDestroyApply apply = this.get(id);
        if (apply == null) {
            throw new BusinessException("申请不存在");
        }
        if (apply.getStatus() != null && apply.getStatus() != 0) {
            throw new BusinessException("该申请已处理，请勿重复操作");
        }

        String sellerId = apply.getSellerId();

        // 申请到审核之间可能有回款，注销前二次校验，避免误注销导致资金丢失
        Wallet wallet = walletService.saveWalletByPartyId(sellerId);
        if (wallet != null && (wallet.getMoney() > 0 || wallet.getMoneyAfterFrozen() > 0)) {
            throw new BusinessException("该账号存在可用余额，不可注销！");
        }
        List<Withdraw> unfinishedWithdrawList = withdrawService.selectUnFinishedWithdraw(sellerId);
        if (CollectionUtil.isNotEmpty(unfinishedWithdrawList)) {
            throw new BusinessException("该账号有未完成的提现订单，不可注销！");
        }

        apply.setStatus(1);
        apply.setReviewTime(new Date());
        apply.setReviewerName(reviewerName);
        this.getHibernateTemplate().update(apply);

        // 真正注销：复用已上线的注销逻辑(置 SecUser/Party enabled=false、Seller.status=0、清缓存、下架商品、发事件等)
        userService.updateLogoffAccount(sellerId, apply.getApplyReason());

        if (tipService != null) {
            try {
                tipService.deleteTip(id);
            } catch (Exception e) {
                logger.warn("deleteTip for seller destroy apply failed: " + id, e);
            }
        }
    }

    @Override
    public void saveReject(String id, String reviewRemark, String reviewerName) {
        SellerDestroyApply apply = this.get(id);
        if (apply == null) {
            throw new BusinessException("申请不存在");
        }
        if (apply.getStatus() != null && apply.getStatus() != 0) {
            throw new BusinessException("该申请已处理，请勿重复操作");
        }
        apply.setStatus(2);
        apply.setReviewTime(new Date());
        apply.setReviewerName(reviewerName);
        apply.setReviewRemark(reviewRemark);
        this.getHibernateTemplate().update(apply);

        if (tipService != null) {
            try {
                tipService.deleteTip(id);
            } catch (Exception e) {
                logger.warn("deleteTip for seller destroy apply failed: " + id, e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public Long getUntreatedCount() {
        List<Object> find = (List<Object>) this.getHibernateTemplate()
                .find(" SELECT COUNT(*) FROM SellerDestroyApply WHERE status=0 ");
        return (find == null || find.isEmpty() || find.get(0) == null) ? 0L : Long.valueOf(find.get(0).toString());
    }
}
