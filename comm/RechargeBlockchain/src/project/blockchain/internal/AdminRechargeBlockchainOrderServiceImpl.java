package project.blockchain.internal;

import java.util.*;

import cn.hutool.core.bean.BeanUtil;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
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
import project.mall.seller.AdminSellerService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.dto.QueryMallLevelDTO;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.party.UserMetricsService;
import project.party.model.UserMetrics;
import project.party.recom.UserRecomService;
import project.tip.TipService;
import project.wallet.Wallet;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;
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
    private WalletService walletService;
    private AdminSellerService adminSellerService;
    private UserMetricsService userMetricsService;

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
            //订单详情
            RechargeBlockchain rechargeBlockchain = findByOrderNo(order_no);
            //钱包信息
            Wallet wallet = walletService.saveWalletByPartyId(rechargeBlockchain.getPartyId());

            //店铺信息
            Seller seller = adminSellerService.findSellerById(rechargeBlockchain.getPartyId());
            //当前店铺等级
            String sellerMallLevel = seller.getMallLevel()==null ? "D" : seller.getMallLevel();

            //当前充值的时间到账金额
            double amount = Double.valueOf(transfer_usdt);
            UserMetrics userMetrics = userMetricsService.getByPartyId(rechargeBlockchain.getPartyId());
            userMetrics.setUpdateTime(new Date());
            Double storeMoneyRechargeAcc = userMetrics.getStoreMoneyRechargeAcc()==null ? 0d : userMetrics.getStoreMoneyRechargeAcc();
            double totalAddMoney = Arith.add(storeMoneyRechargeAcc, amount);
            userMetrics.setStoreMoneyRechargeAcc(totalAddMoney);//更新累计充值金额
            userMetricsService.update(userMetrics);

            //店铺等级配置信息
            Criteria criteria = getHibernateTemplate().getSessionFactory().getCurrentSession().createCriteria(MallLevel.class);
            List<MallLevel> list = criteria.list();
            List<QueryMallLevelDTO> mallLevelDTOList = new ArrayList<>();
            for (MallLevel mallLevel : list) {
                mallLevel.setProfitRationMin(Arith.mul(mallLevel.getProfitRationMin(), 100));
                mallLevel.setProfitRationMax(Arith.mul(mallLevel.getProfitRationMax(), 100));
                mallLevel.setSellerDiscount(Arith.mul(mallLevel.getSellerDiscount(), 100));

                MallLevelCondExpr mallLevelCondExpr = JsonUtils.json2Object(mallLevel.getCondExpr(), MallLevelCondExpr.class);
                List<MallLevelCondExpr.Param> params = mallLevelCondExpr.getParams();

                QueryMallLevelDTO oneDto = new QueryMallLevelDTO();
                BeanUtil.copyProperties(mallLevel, oneDto);
                params.forEach(e -> {
                    if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode())) {
                        oneDto.setRechargeAmount(Long.parseLong(e.getValue()));
                    }
                    if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode())) {
                        oneDto.setPopularizeUserCount(Long.parseLong(e.getValue()));
                    }
                });

                //配置的即将升级的店铺等级，脏数据不处理，店铺等级D不处理（最低级是默认的，不需要处理）
                String level = mallLevel.getLevel();
                if (StringUtils.isNullOrEmpty(level) || "D".equals(level)){
                    continue;
                }

                //升级店铺所需累计充值金额，脏数据不处理
                if (oneDto.getRechargeAmount()==null || oneDto.getRechargeAmount()<=0){
                    continue;
                }

                mallLevelDTOList.add(oneDto);
            }

            String upLevel = sellerMallLevel;//即将升级的店铺等级
            double upgradeCash = 0;//升级礼金
            //累计充值金额是否满足店铺升级条件
            for (QueryMallLevelDTO oneDto : mallLevelDTOList){
                if(totalAddMoney>=oneDto.getRechargeAmount().doubleValue()){
                    upLevel = oneDto.getLevel();
                    upgradeCash = oneDto.getUpgradeCash().doubleValue();
                }
            }

            //店铺等级升级逻辑，最低级不用升级，预计升级等级等于当前店铺等级不用升级，最高级不用升级
            if ("0".equals(sellerMallLevel) && !"D".equals(sellerMallLevel) && !"SSS".equals(upLevel) && !sellerMallLevel.equals(upLevel)){
                //修改店铺等级逻辑
                adminSellerService.autoUpdateStoreLevel(rechargeBlockchain.getPartyId(),upLevel,amount,operator_username,"",remarks);
                //升级礼金逻辑
                if(upgradeCash > 0){
                    wallet.setMoney(Arith.add(wallet.getMoney(),upgradeCash));
                    wallet.setTimestamp(new Date());
                    walletService.update( wallet);
                }
            }

        }catch (Exception e){
            logger.error("更新充值后店铺等级，报错信息为：" , e);
        }

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
     * 判断店铺等级是否可以升级
     */
    private boolean canUpLevel(String sellerMallLevel, String level) {
        //店铺等级索引
        Map<String, Integer> levelSortMap = new HashMap<>();
        levelSortMap.put("D", 0);
        levelSortMap.put("C", 1);
        levelSortMap.put("B", 2);
        levelSortMap.put("A", 3);
        levelSortMap.put("S", 4);
        levelSortMap.put("SS", 5);
        levelSortMap.put("SSS", 6);

        return levelSortMap.get(sellerMallLevel) < levelSortMap.get(level);

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

    public RechargeBlockchain findByOrderNo(String order_no) {
        StringBuffer queryString = new StringBuffer(" FROM RechargeBlockchain where order_no=?0");
        List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(), new Object[] { order_no });
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
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
    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }
    public void setAdminSellerService(AdminSellerService adminSellerService) {
        this.adminSellerService = adminSellerService;
    }
    public void setUserMetricsService(UserMetricsService userMetricsService) {
        this.userMetricsService = userMetricsService;
    }

}
