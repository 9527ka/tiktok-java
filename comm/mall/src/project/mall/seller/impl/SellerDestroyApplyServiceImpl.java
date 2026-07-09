package project.mall.seller.impl;

import java.util.Date;
import java.util.List;

import org.springframework.orm.hibernate5.support.HibernateDaoSupport;

import kernel.exception.BusinessException;
import project.mall.seller.SellerDestroyApplyService;
import project.mall.seller.model.SellerDestroyApply;
import project.tip.TipConstants;
import project.tip.TipService;

/**
 * 商家注销申请(商家端)实现
 */
public class SellerDestroyApplyServiceImpl extends HibernateDaoSupport implements SellerDestroyApplyService {

    private TipService tipService;

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }

    @Override
    public void saveApply(String sellerId, String account, String reason) {
        // 已有待审核申请则不允许重复提交
        SellerDestroyApply latest = this.findLatestBySellerId(sellerId);
        if (latest != null && latest.getStatus() != null && latest.getStatus() == 0) {
            throw new BusinessException("您已提交注销申请，正在审核中");
        }

        SellerDestroyApply apply = new SellerDestroyApply();
        apply.setSellerId(sellerId);
        apply.setAccount(account);
        apply.setApplyReason(reason);
        apply.setStatus(0);
        apply.setApplyTime(new Date());

        this.getHibernateTemplate().save(apply);

        // 后台未处理红点
        if (tipService != null) {
            try {
                tipService.saveTip(apply.getId(), TipConstants.SELLER_DESTROY_APPLY);
            } catch (Exception e) {
                // 红点提示失败不影响申请提交
                logger.warn("saveTip for seller destroy apply failed: " + apply.getId(), e);
            }
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    public SellerDestroyApply findLatestBySellerId(String sellerId) {
        List<SellerDestroyApply> list = (List<SellerDestroyApply>) this.getHibernateTemplate()
                .find(" FROM SellerDestroyApply WHERE sellerId=?0 ORDER BY applyTime DESC ", new Object[] { sellerId });
        if (list != null && !list.isEmpty()) {
            return list.get(0);
        }
        return null;
    }
}
