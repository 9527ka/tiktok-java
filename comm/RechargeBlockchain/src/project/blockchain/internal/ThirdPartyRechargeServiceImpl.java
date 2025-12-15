package project.blockchain.internal;

import com.alibaba.fastjson.JSONObject;
import ext.Types;
import http.HttpUtils;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import project.Constants;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.ThirdPartyRechargeService;
import project.blockchain.event.model.RechargeInfo;
import project.blockchain.manager.ThirdPartyManager;
import project.blockchain.vo.GCash2NotifyVo;
import project.blockchain.vo.GCash2Vo;
import project.blockchain.vo.GCash3NotifyVo;
import project.blockchain.vo.GCash3Vo;
import project.blockchain.vo.GCashPayNotifyVo;
import project.blockchain.vo.GCashPayVo;
import project.blockchain.vo.ThirdPartyCommonVo;
import project.blockchain.vo.ThirdPartyPayRespVo;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.party.PartyService;
import project.party.model.Party;
import project.pay.PayCore;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.UserDataService;
import project.wallet.Wallet;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;
import project.wallet.WalletService;
import project.wallet.rate.ExchangeRate;
import util.DateUtil;
import util.RandomUtil;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.*;

public class ThirdPartyRechargeServiceImpl extends HibernateDaoSupport implements ThirdPartyRechargeService {
    private final Logger debugLogger = LoggerFactory.getLogger(this.getClass());

    private ThirdPartyManager thirdPartyManager;

    private PartyService partyService;

    private SysparaService sysparaService;

    private TipService tipService;

    private WalletLogService walletLogService;

    private WalletService walletService;

    private MoneyLogService moneyLogService;

    private RechargeBlockchainService rechargeBlockchainService;

    private UserDataService userDataService;


    @Override
    public String saveApplyRecharge(String partyId, String amount, String frenchCurrency) {
        Party party = this.partyService.cachePartyBy(partyId, false);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
            throw new BusinessException("无权限");
        }

//        List<RechargeBlockchain> oreders = null;
//        StringBuffer queryString = new StringBuffer(" FROM RechargeBlockchain where partyId=?0 AND succeeded=?1 ");
//        List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(),
//                new Object[]{partyId, 0});
//        if (list.size() > 0) {
//            oreders = list;
//        }

//        Double recharge_only_one = Double.valueOf(sysparaService.find("recharge_only_one").getValue());
//        if (oreders != null && recharge_only_one == 1) {
//            throw new BusinessException("提交失败，当前有未处理订单");
//        }
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_ref", orderNo);
        params.put("product", "TRC20Buy");
        params.put("amount", amount);
        Map<String, Object> extraParams = new HashMap<>();
        extraParams.put("fiat_currency", frenchCurrency);
        params.put("extra", extraParams);
        long timestamp = Instant.now().getEpochSecond();
        ThirdPartyCommonVo commonVo = thirdPartyManager.sendPost(params, "/pay", timestamp);
        ThirdPartyPayRespVo respVo = JSONObject.parseObject(commonVo.getParams(), ThirdPartyPayRespVo.class);

        double amount_double = Double.parseDouble(respVo.getAmount());
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(amount_double);
        recharge.setSymbol(frenchCurrency);
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(1);
        recharge.setAmount(0.00);
        recharge.setPayUrl(respVo.getPayurl());
        this.getHibernateTemplate().save(recharge);

        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getVolume());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);

        tipService.saveTip(recharge.getId().toString(), TipConstants.RECHARGE_BLOCKCHAIN);


        return respVo.getPayurl();
    }

    @Override
    public RechargeInfo saveSucceeded(ThirdPartyPayRespVo payRespVo) {
        //订单号
        String order_no = payRespVo.getMerchant_ref();
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("order_no").eq(order_no));
        criteria.add(Property.forName("isThirdParty").eq(1));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtils.isEmpty(rechargeBlockchains)) {
            logger.error("第三方充值订单不存在");
            throw new BusinessException("订单不存在");
        }
        RechargeBlockchain recharge = rechargeBlockchains.get(0);

        double amount = Double.parseDouble(payRespVo.getPay_amount());

        String product = payRespVo.getProduct();
        if (!"TRC20Buy".equals(product)) {
            double gcashRate = 58.00;
            Syspara gcashRateSys = sysparaService.find("thirdParty_Gcash_rate");
            if (gcashRateSys != null) {
                gcashRate = Double.parseDouble(gcashRateSys.getValue());
            }
            amount = Arith.div(amount, gcashRate, 2);

        }
        if (recharge.getSucceeded() == 1) {
            return null;
        }
        Date date = new Date();
        recharge.setReviewTime(date);

        recharge.setSucceeded(1);
        recharge.setAddress(payRespVo.getTo());
        recharge.setBlockchain_name(product);
        recharge.setChannel_address(payRespVo.getFrom());
        recharge.setTx(payRespVo.getBlock_hash());
        recharge.setAmount(amount);


        /**
         * 如果是usdt则加入wallet，否则寻找walletExtend里相同币种
         */

        Party party = this.partyService.cachePartyBy(recharge.getPartyId(), false);

        Wallet wallet = walletService.saveWalletByPartyId(recharge.getPartyId());

        double amount_before = wallet.getMoney();
        // 2023-7-15 调整，将充值提成记到充值用户身上
        walletService.update(wallet.getPartyId().toString(), amount, 0.0);


        /*
         * 保存资金日志
         */
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(amount);
        moneyLog.setAmount_after(Arith.add(wallet.getMoney(), amount));

        moneyLog.setLog("充值订单[" + recharge.getOrder_no() + "]");
        moneyLog.setPartyId(recharge.getPartyId());
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
        moneyLog.setCreateTime(date);
        moneyLogService.save(moneyLog);


        WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
        walletLog.setUsdtAmount(amount);
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setStatus(recharge.getSucceeded());
        walletLogService.update(walletLog);

//        recharge.setVolume(Double.valueOf(success_amount));
        getHibernateTemplate().update(recharge);


        // 发布一个充值审核成功的事件
//        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        RechargeInfo info = new RechargeInfo();
        info.setApplyUserId(recharge.getPartyId().toString());
        info.setOrderNo(order_no);
        info.setWalletLogId(walletLog.getId().toString());
        info.setEventTime(date);
        info.setAmount(recharge.getAmount());
//        wac.publishEvent(new RechargeSuccessEvent(this, info));

        //记录首充时间
        if (Objects.isNull(party.getFirstRechargeTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)) {
            debugLogger.info("-----> 首充用户id", party.getId());
            party.setFirstRechargeTime(new Date());
        }

        /**
         * 给他的代理添加充值记录
         */
//        userDataService.saveRechargeHandle(recharge.getPartyId(), amount, "usdt",0D);

        /**
         * 若已开启充值奖励 ，则充值到账后给他的代理用户添加奖金
         */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, 1);
//			}

        // 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，
        // 1不重置，2重置
        String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw").getValue();
        if ("1".equals(recharge_sucess_reset_withdraw)) {
            party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
                party.setWithdraw_limit_now_amount(0);
            }
        }
        if ("2".equals(recharge_sucess_reset_withdraw)) {
            double withdraw_limit_turnover_percent = Double
                    .valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
            double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);

            if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
                party.setWithdraw_limit_amount(amount);
                party.setWithdraw_limit_now_amount(0);
            } else {
                party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            }
        }

        partyService.update(party);

        tipService.deleteTip(recharge.getId().toString());

        try {
            rechargeBlockchainService.updateFirstSuccessRecharge(order_no);
        } catch (Exception e) {
            logger.error("判断首充礼金报错，报错信息为：" + e.getMessage(), e);
        }
        getHibernateTemplate().flush();
        return info;
    }

    @Override
    public void updateThirdPartyOrder() {
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("succeeded").eq(0));
        criteria.add(Property.forName("isThirdParty").eq(1));
        criteria.add(Restrictions.or(Restrictions.eq("blockchain_name", "CPayYsH5"), Restrictions.isNull("blockchain_name")));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria, 0, 20);
        List<String> orderNos = new ArrayList<>();
        for (RechargeBlockchain rechargeBlockchain : rechargeBlockchains) {
            orderNos.add(rechargeBlockchain.getOrder_no());
        }
        if (CollectionUtils.isNotEmpty(orderNos)) {
            Map<String, Object> params = new HashMap<>();
            params.put("merchant_refs", orderNos);
            ThirdPartyCommonVo commonVo = thirdPartyManager.sendPost(params, "/batch-query/order", Instant.now().getEpochSecond());
            List<ThirdPartyPayRespVo> respVos = JSONObject.parseArray(commonVo.getParams(), ThirdPartyPayRespVo.class);
            for (ThirdPartyPayRespVo respVo : respVos) {
                if (respVo.getStatus() == 0) {
                    for (RechargeBlockchain rechargeBlockchain : rechargeBlockchains) {
                        if (rechargeBlockchain.getOrder_no().equals(respVo.getMerchant_ref())) {
                            Date created = rechargeBlockchain.getCreated();
                            if (DateUtils.addMinute(created, 31).before(new Date())) {
                                rechargeBlockchain.setSucceeded(2);
                                rechargeBlockchainService.update(rechargeBlockchain);
                                tipService.deleteTip(rechargeBlockchain.getId().toString());
                            }
                        }
                    }
                }
            }
        }


    }

    @Override
    public String saveApplyRecharge(String partyId, String amount) {
        String minimum_amount = sysparaService.find("third_party_minimum_amount").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount").getValue();
        if (Double.parseDouble(amount) < Double.parseDouble(minimum_amount)) {
            throw new BusinessException("充值数量不能小于" + minimum_amount);
        }
        if (Double.parseDouble(amount) > Double.parseDouble(maximum_amount)) {
            throw new BusinessException("充值数量不能大于" + maximum_amount);
        }
        Party party = this.partyService.cachePartyBy(partyId, false);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
            throw new BusinessException("无权限");
        }

//        List<RechargeBlockchain> oreders = null;
//        StringBuffer queryString = new StringBuffer(" FROM RechargeBlockchain where partyId=?0 AND succeeded=?1 ");
//        List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(),
//                new Object[]{partyId, 0});
//        if (list.size() > 0) {
//            oreders = list;
//        }

//        Double recharge_only_one = Double.valueOf(sysparaService.find("recharge_only_one").getValue());
//        if (oreders != null && recharge_only_one == 1) {
//            throw new BusinessException("提交失败，当前有未处理订单");
//        }
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        Map<String, Object> params = new HashMap<>();
        params.put("merchant_ref", orderNo);
        params.put("product", "CPayYsH5");
        params.put("amount", amount);
        Map<String, Object> extraParams = new HashMap<>();
        params.put("extra", extraParams);
        long timestamp = Instant.now().getEpochSecond();
        ThirdPartyCommonVo commonVo = thirdPartyManager.sendPost(params, "/pay", timestamp);
        ThirdPartyPayRespVo respVo = JSONObject.parseObject(commonVo.getParams(), ThirdPartyPayRespVo.class);

        double amount_double = Double.parseDouble(respVo.getAmount());
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(amount_double);
        recharge.setSymbol("PHP");
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(1);
        recharge.setAmount(0.00);
        recharge.setPayUrl(respVo.getPayurl());
        recharge.setBlockchain_name("CPayYsH5");
        this.getHibernateTemplate().save(recharge);

        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getVolume());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);

        tipService.saveTip(recharge.getId().toString(), TipConstants.RECHARGE_BLOCKCHAIN);


        return respVo.getPayurl();
    }

    @Override
    public String saveApplyRecharge2(String partyId, String amount, String pageUrl) {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_2").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_2").getValue();
        if (Double.parseDouble(amount) < Double.parseDouble(minimum_amount)) {
            throw new BusinessException("充值数量不能小于" + minimum_amount);
        }
        if (Double.parseDouble(amount) > Double.parseDouble(maximum_amount)) {
            throw new BusinessException("充值数量不能大于" + maximum_amount);
        }
        Party party = this.partyService.cachePartyBy(partyId, false);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
            throw new BusinessException("无权限");
        }

//        List<RechargeBlockchain> oreders = null;
//        StringBuffer queryString = new StringBuffer(" FROM RechargeBlockchain where partyId=?0 AND succeeded=?1 ");
//        List<RechargeBlockchain> list = (List<RechargeBlockchain>) getHibernateTemplate().find(queryString.toString(),
//                new Object[]{partyId, 0});
//        if (list.size() > 0) {
//            oreders = list;
//        }

//        Double recharge_only_one = Double.valueOf(sysparaService.find("recharge_only_one").getValue());
//        if (oreders != null && recharge_only_one == 1) {
//            throw new BusinessException("提交失败，当前有未处理订单");
//        }
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        Map<String, Object> params = new TreeMap<>();
        params.put("mer_order_no", orderNo);
        params.put("order_amount", amount);
        params.put("phone", "9852146882");
        params.put("pname", "ZhangSan");
        params.put("pageUrl", pageUrl);
        params.put("ccy_no", "PHP");
        params.put("pemail", "test@mail.com");
        String payCode = sysparaService.find("third_party_pay_code").getValue();
        params.put("busi_code", payCode);
        String notifyUrl = sysparaService.find("third_party_notify_Url_2").getValue();
        params.put("notifyUrl", notifyUrl);
        GCash2Vo gCash2Vo = thirdPartyManager.sendPost2(params, "/orderPay");
        double amount_double = Double.parseDouble(gCash2Vo.getOrder_amount());
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(amount_double);
        recharge.setSymbol("PHP");
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(1);
        recharge.setAmount(0.00);
        recharge.setPayUrl(gCash2Vo.getOrder_data());
        recharge.setBlockchain_name("GCash2.0");
        this.getHibernateTemplate().save(recharge);

        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getVolume());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);

        tipService.saveTip(recharge.getId().toString(), TipConstants.RECHARGE_BLOCKCHAIN);


        return gCash2Vo.getOrder_data();
    }

    @Override
    public RechargeInfo saveSucceeded2(GCash2NotifyVo request) {
        //订单号
        String order_no = request.getMer_order_no();
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("order_no").eq(order_no));
        criteria.add(Property.forName("isThirdParty").eq(1));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtils.isEmpty(rechargeBlockchains)) {
            logger.error("第三方充值订单不存在");
            throw new BusinessException("订单不存在");
        }
        RechargeBlockchain recharge = rechargeBlockchains.get(0);

        double amount = Double.parseDouble(request.getPay_amount());

//        String product = payRespVo.getProduct();
//        if (!"TRC20Buy".equals(product)) {
        double gcashRate = 58.00;
        Syspara gcashRateSys = sysparaService.find("thirdParty_Gcash_rate");
        if (gcashRateSys != null) {
            gcashRate = Double.parseDouble(gcashRateSys.getValue());
        }
        amount = Arith.div(amount, gcashRate, 2);

//        }
        if (recharge.getSucceeded() == 1) {
            return null;
        }
        Date date = new Date();
        recharge.setReviewTime(date);
        recharge.setSucceeded(1);
        recharge.setAmount(amount);


        /**
         * 如果是usdt则加入wallet，否则寻找walletExtend里相同币种
         */

        Party party = this.partyService.cachePartyBy(recharge.getPartyId(), false);

        Wallet wallet = walletService.saveWalletByPartyId(recharge.getPartyId());

        double amount_before = wallet.getMoney();
        // 2023-7-15 调整，将充值提成记到充值用户身上
        walletService.update(wallet.getPartyId().toString(), amount, 0.0);


        /*
         * 保存资金日志
         */
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(amount);
        moneyLog.setAmount_after(Arith.add(wallet.getMoney(), amount));

        moneyLog.setLog("充值订单[" + recharge.getOrder_no() + "]");
        moneyLog.setPartyId(recharge.getPartyId());
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
        moneyLog.setCreateTime(date);
        moneyLogService.save(moneyLog);


        WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
        walletLog.setUsdtAmount(amount);
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setStatus(recharge.getSucceeded());
        walletLogService.update(walletLog);

//        recharge.setVolume(Double.valueOf(success_amount));
        getHibernateTemplate().update(recharge);


        // 发布一个充值审核成功的事件
//        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        RechargeInfo info = new RechargeInfo();
        info.setApplyUserId(recharge.getPartyId().toString());
        info.setOrderNo(order_no);
        info.setWalletLogId(walletLog.getId().toString());
        info.setEventTime(date);
        info.setAmount(recharge.getAmount());
//        wac.publishEvent(new RechargeSuccessEvent(this, info));

        //记录首充时间
        if (Objects.isNull(party.getFirstRechargeTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)) {
            debugLogger.info("-----> 首充用户id", party.getId());
            party.setFirstRechargeTime(new Date());
        }

        /**
         * 给他的代理添加充值记录
         */
//        userDataService.saveRechargeHandle(recharge.getPartyId(), amount, "usdt");

        /**
         * 若已开启充值奖励 ，则充值到账后给他的代理用户添加奖金
         */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, 1);
//			}

        // 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，
        // 1不重置，2重置
        String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw").getValue();
        if ("1".equals(recharge_sucess_reset_withdraw)) {
            party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
                party.setWithdraw_limit_now_amount(0);
            }
        }
        if ("2".equals(recharge_sucess_reset_withdraw)) {
            double withdraw_limit_turnover_percent = Double
                    .valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
            double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);

            if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
                party.setWithdraw_limit_amount(amount);
                party.setWithdraw_limit_now_amount(0);
            } else {
                party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            }
        }

        partyService.update(party);

        tipService.deleteTip(recharge.getId().toString());

        try {
            rechargeBlockchainService.updateFirstSuccessRecharge(order_no);
        } catch (Exception e) {
            logger.error("判断首充礼金报错，报错信息为：" + e.getMessage(), e);
        }
        getHibernateTemplate().flush();
        return info;
    }

    @Override
    public void updateThirdPartyOrder2() {
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("succeeded").eq(0));
        criteria.add(Property.forName("isThirdParty").eq(1));
        criteria.add(Property.forName("blockchain_name").eq("GCash2.0"));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria, 0, 20);
        for (RechargeBlockchain rechargeBlockchain : rechargeBlockchains) {
//            Map<String, Object> params = new HashMap<>();
//            params.put("request_time", DateUtils.format(new Date(), "yyyyMMddHHmmss", TimeZone.getTimeZone("GMT+7")));
//            params.put("request_no", System.currentTimeMillis() + "");
//            params.put("mer_order_no", rechargeBlockchain.getOrder_no());
//            GCash2OrderQueryVo orderQueryVo = thirdPartyManager.sendPost3(params, "/orderQuery");
//            if (rechargeBlockchain.getOrder_no().equals(orderQueryVo.getMer_order_no())) {
            Date created = rechargeBlockchain.getCreated();
            if (DateUtils.addMinute(created, 16).before(new Date())) {
                rechargeBlockchain.setSucceeded(2);
                rechargeBlockchainService.update(rechargeBlockchain);
                tipService.deleteTip(rechargeBlockchain.getId().toString());
            }
//            }
        }


    }

    @Override
    public String saveApplyRecharge3(String partyId, String amount, String callBackUrl) {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_3").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_3").getValue();
        if (Double.parseDouble(amount) < Double.parseDouble(minimum_amount)) {
            throw new BusinessException("充值数量不能小于" + minimum_amount);
        }
        if (Double.parseDouble(amount) > Double.parseDouble(maximum_amount)) {
            throw new BusinessException("充值数量不能大于" + maximum_amount);
        }
        Party party = this.partyService.cachePartyBy(partyId, false);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
            throw new BusinessException("无权限");
        }
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        Map<String, Object> params = new TreeMap<>();
        params.put("orderNo", orderNo);
        params.put("orderAmount", amount);
        params.put("callBackUrl", callBackUrl);
        String passageId = sysparaService.find("third_party_pay_code_3").getValue();
        params.put("passageId", Integer.parseInt(passageId));
        String notifyUrl = sysparaService.find("third_party_notify_Url_3").getValue();
        params.put("notifyUrl", notifyUrl);
        GCash3Vo gCash3Vo = thirdPartyManager.sendPost4(params, "/collect/create");
        Map<String, Object> data = gCash3Vo.getData();
        String payUrl = (String) data.get("payUrl");
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(Double.parseDouble(amount));
        recharge.setSymbol("PHP");
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(1);
        recharge.setAmount(0.00);
        recharge.setPayUrl(payUrl);
        recharge.setBlockchain_name("GCash3.0");
        this.getHibernateTemplate().save(recharge);

        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getVolume());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);

        tipService.saveTip(recharge.getId().toString(), TipConstants.RECHARGE_BLOCKCHAIN);


        return payUrl;
    }

    @Override
    public String saveApplyRecharge4(String partyId, String amount, String callBackUrl) {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_4").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_4").getValue();
        if (Double.parseDouble(amount) < Double.parseDouble(minimum_amount)) {
            throw new BusinessException("充值数量不能小于" + minimum_amount);
        }
        if (Double.parseDouble(amount) > Double.parseDouble(maximum_amount)) {
            throw new BusinessException("充值数量不能大于" + maximum_amount);
        }
        Party party = this.partyService.cachePartyBy(partyId, false);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
            throw new BusinessException("无权限");
        }
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        Map<String, Object> params = new TreeMap<>();
        params.put("orderNo", orderNo);
        params.put("orderAmount", amount);
        params.put("callBackUrl", callBackUrl);
        String passageId = sysparaService.find("third_party_pay_code_2").getValue();
        params.put("passageId", Integer.parseInt(passageId));
        String notifyUrl = sysparaService.find("third_party_notify_Url_3").getValue();
        params.put("notifyUrl", notifyUrl);
        GCash3Vo gCash3Vo = thirdPartyManager.sendPost4(params, "/collect/create");
        Map<String, Object> data = gCash3Vo.getData();
        String payUrl = (String) data.get("payUrl");
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(Double.parseDouble(amount));
        recharge.setSymbol("PHP");
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(1);
        recharge.setAmount(0.00);
        recharge.setPayUrl(payUrl);
        recharge.setBlockchain_name("Maya");
        this.getHibernateTemplate().save(recharge);

        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getVolume());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);

        tipService.saveTip(recharge.getId().toString(), TipConstants.RECHARGE_BLOCKCHAIN);


        return payUrl;
    }

    @Override
    public RechargeInfo saveSucceeded3(GCash3NotifyVo notifyVo) {
        //订单号
        String order_no = notifyVo.getOrderNo();
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("order_no").eq(order_no));
        criteria.add(Property.forName("isThirdParty").eq(1));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtils.isEmpty(rechargeBlockchains)) {
            logger.error("第三方充值订单不存在");
            throw new BusinessException("订单不存在");
        }
        RechargeBlockchain recharge = rechargeBlockchains.get(0);

        double amount = notifyVo.getRealAmount().doubleValue();

//        String product = payRespVo.getProduct();
//        if (!"TRC20Buy".equals(product)) {
        double gcashRate = 58.00;
        Syspara gcashRateSys = sysparaService.find("thirdParty_Gcash_rate");
        if (gcashRateSys != null) {
            gcashRate = Double.parseDouble(gcashRateSys.getValue());
        }
        amount = Arith.div(amount, gcashRate, 2);

//        }
        if (recharge.getSucceeded() == 1) {
            return null;
        }
        Date date = new Date();
        recharge.setReviewTime(date);
        recharge.setSucceeded(1);
        recharge.setAmount(amount);


        /**
         * 如果是usdt则加入wallet，否则寻找walletExtend里相同币种
         */

        Party party = this.partyService.cachePartyBy(recharge.getPartyId(), false);

        Wallet wallet = walletService.saveWalletByPartyId(recharge.getPartyId());

        double amount_before = wallet.getMoney();
        // 2023-7-15 调整，将充值提成记到充值用户身上
        walletService.update(wallet.getPartyId().toString(), amount, 0.0);


        /*
         * 保存资金日志
         */
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(amount);
        moneyLog.setAmount_after(Arith.add(wallet.getMoney(), amount));

        moneyLog.setLog("充值订单[" + recharge.getOrder_no() + "]");
        moneyLog.setPartyId(recharge.getPartyId());
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
        moneyLog.setCreateTime(date);
        moneyLogService.save(moneyLog);


        WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
        walletLog.setUsdtAmount(amount);
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setStatus(recharge.getSucceeded());
        walletLogService.update(walletLog);

//        recharge.setVolume(Double.valueOf(success_amount));
        getHibernateTemplate().update(recharge);


        // 发布一个充值审核成功的事件
//        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        RechargeInfo info = new RechargeInfo();
        info.setApplyUserId(recharge.getPartyId().toString());
        info.setOrderNo(order_no);
        info.setWalletLogId(walletLog.getId().toString());
        info.setEventTime(date);
        info.setAmount(recharge.getAmount());
//        wac.publishEvent(new RechargeSuccessEvent(this, info));

        //记录首充时间
        if (Objects.isNull(party.getFirstRechargeTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)) {
            debugLogger.info("-----> 首充用户id", party.getId());
            party.setFirstRechargeTime(new Date());
        }

        /**
         * 给他的代理添加充值记录
         */
//        userDataService.saveRechargeHandle(recharge.getPartyId(), amount, "usdt");

        /**
         * 若已开启充值奖励 ，则充值到账后给他的代理用户添加奖金
         */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, 1);
//			}

        // 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，
        // 1不重置，2重置
        String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw").getValue();
        if ("1".equals(recharge_sucess_reset_withdraw)) {
            party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
                party.setWithdraw_limit_now_amount(0);
            }
        }
        if ("2".equals(recharge_sucess_reset_withdraw)) {
            double withdraw_limit_turnover_percent = Double
                    .valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
            double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);

            if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
                party.setWithdraw_limit_amount(amount);
                party.setWithdraw_limit_now_amount(0);
            } else {
                party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            }
        }

        partyService.update(party);

        tipService.deleteTip(recharge.getId().toString());

        try {
            rechargeBlockchainService.updateFirstSuccessRecharge(order_no);
        } catch (Exception e) {
            logger.error("判断首充礼金报错，报错信息为：" + e.getMessage(), e);
        }
        getHibernateTemplate().flush();
        return info;
    }

    @Override
    public void updateThirdPartyOrder3() {
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("succeeded").eq(0));
        criteria.add(Property.forName("isThirdParty").eq(1));
        criteria.add(Restrictions.or(Property.forName("blockchain_name").eq("GCash3.0"), Property.forName("blockchain_name").eq("Maya")));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria, 0, 20);
        for (RechargeBlockchain rechargeBlockchain : rechargeBlockchains) {
//            Map<String, Object> params = new HashMap<>();
//            params.put("orderNo", rechargeBlockchain.getOrder_no());
//            GCash3Vo gCash3Vo = thirdPartyManager.sendPost4(params, "/order/query");
//            Map<String, Object> data = gCash3Vo.getData();
//            String orderNo = (String) data.get("orderNo");
//            if (rechargeBlockchain.getOrder_no().equals(orderNo)) {
            Date created = rechargeBlockchain.getCreated();
            if (DateUtils.addMinute(created, 11).before(new Date())) {
                rechargeBlockchain.setSucceeded(2);
                rechargeBlockchainService.update(rechargeBlockchain);
                tipService.deleteTip(rechargeBlockchain.getId().toString());
            }
//            }
        }
    }

    @Override
    public String saveApplyRecharge5(String partyId, String amount, String callBackUrl) throws NoSuchAlgorithmException, InvalidKeyException, UnknownHostException {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_5").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_5").getValue();
        if (Double.parseDouble(amount) < Double.parseDouble(minimum_amount)) {
            throw new BusinessException("充值数量不能小于" + minimum_amount);
        }
        if (Double.parseDouble(amount) > Double.parseDouble(maximum_amount)) {
            throw new BusinessException("充值数量不能大于" + maximum_amount);
        }
        Party party = this.partyService.cachePartyBy(partyId, false);
        if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
            throw new BusinessException("无权限");
        }
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        Map<String, Object> params = new TreeMap<>();
        params.put("MerchantOrderNo", orderNo);
        params.put("PaymentAmount", Arith.mul(Double.parseDouble(amount), 100) + "");
        params.put("PaymentCurrency", "PHP");
        params.put("MemberIP", InetAddress.getLocalHost().getHostAddress());
        params.put("SuccessUrl", callBackUrl);
        params.put("TimeStart", (System.currentTimeMillis() / 1000) + "");
        String passageId = sysparaService.find("third_party_pay_code_5").getValue();
        params.put("ChannelCode", passageId);
        String notifyUrl = sysparaService.find("third_party_notify_Url_5").getValue();
        params.put("NotifyUrl", notifyUrl);
        GCashPayVo cashPayVo = thirdPartyManager.sendPost5(params, "/SendTransaction/unifiedorder");
        Map<String, Object> data = cashPayVo.getData();
        String payUrl = (String) data.get("qrcode");
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(Double.parseDouble(amount));
        recharge.setSymbol("PHP");
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(1);
        recharge.setAmount(0.00);
        recharge.setPayUrl(payUrl);
        recharge.setBlockchain_name("GCash pay");
        this.getHibernateTemplate().save(recharge);

        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getVolume());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);

        tipService.saveTip(recharge.getId().toString(), TipConstants.RECHARGE_BLOCKCHAIN);


        return payUrl;
    }

    @Override
    public RechargeInfo saveSucceeded4(GCashPayNotifyVo notifyVo) {
        //订单号
        String order_no = notifyVo.getMerchantOrderNo();
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("order_no").eq(order_no));
        criteria.add(Property.forName("isThirdParty").eq(1));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtils.isEmpty(rechargeBlockchains)) {
            logger.error("第三方充值订单不存在");
            throw new BusinessException("订单不存在");
        }
        RechargeBlockchain recharge = rechargeBlockchains.get(0);

        double amount = Double.parseDouble(notifyVo.getAmount());

//        String product = payRespVo.getProduct();
//        if (!"TRC20Buy".equals(product)) {
        double gcashRate = 58.00;
        Syspara gcashRateSys = sysparaService.find("thirdParty_Gcash_rate");
        if (gcashRateSys != null) {
            gcashRate = Double.parseDouble(gcashRateSys.getValue());
        }
        amount = Arith.div(amount, gcashRate, 2);

//        }
        if (recharge.getSucceeded() == 1) {
            return null;
        }
        Date date = new Date();
        recharge.setReviewTime(date);
        recharge.setSucceeded(1);
        recharge.setAmount(amount);


        /**
         * 如果是usdt则加入wallet，否则寻找walletExtend里相同币种
         */

        Party party = this.partyService.cachePartyBy(recharge.getPartyId(), false);

        Wallet wallet = walletService.saveWalletByPartyId(recharge.getPartyId());

        double amount_before = wallet.getMoney();
        // 2023-7-15 调整，将充值提成记到充值用户身上
        walletService.update(wallet.getPartyId().toString(), amount, 0.0);


        /*
         * 保存资金日志
         */
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(amount);
        moneyLog.setAmount_after(Arith.add(wallet.getMoney(), amount));

        moneyLog.setLog("充值订单[" + recharge.getOrder_no() + "]");
        moneyLog.setPartyId(recharge.getPartyId());
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
        moneyLog.setCreateTime(date);
        moneyLogService.save(moneyLog);


        WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
        walletLog.setUsdtAmount(amount);
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setStatus(recharge.getSucceeded());
        walletLogService.update(walletLog);

//        recharge.setVolume(Double.valueOf(success_amount));
        getHibernateTemplate().update(recharge);


        // 发布一个充值审核成功的事件
//        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        RechargeInfo info = new RechargeInfo();
        info.setApplyUserId(recharge.getPartyId().toString());
        info.setOrderNo(order_no);
        info.setWalletLogId(walletLog.getId().toString());
        info.setEventTime(date);
        info.setAmount(recharge.getAmount());
//        wac.publishEvent(new RechargeSuccessEvent(this, info));

        //记录首充时间
        if (Objects.isNull(party.getFirstRechargeTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)) {
            debugLogger.info("-----> 首充用户id", party.getId());
            party.setFirstRechargeTime(new Date());
        }

        /**
         * 给他的代理添加充值记录
         */
//        userDataService.saveRechargeHandle(recharge.getPartyId(), amount, "usdt");

        /**
         * 若已开启充值奖励 ，则充值到账后给他的代理用户添加奖金
         */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, 1);
//			}

        // 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，
        // 1不重置，2重置
        String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw").getValue();
        if ("1".equals(recharge_sucess_reset_withdraw)) {
            party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
                party.setWithdraw_limit_now_amount(0);
            }
        }
        if ("2".equals(recharge_sucess_reset_withdraw)) {
            double withdraw_limit_turnover_percent = Double
                    .valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
            double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);

            if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
                party.setWithdraw_limit_amount(amount);
                party.setWithdraw_limit_now_amount(0);
            } else {
                party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
            }
        }

        partyService.update(party);

        tipService.deleteTip(recharge.getId().toString());

        try {
            rechargeBlockchainService.updateFirstSuccessRecharge(order_no);
        } catch (Exception e) {
            logger.error("判断首充礼金报错，报错信息为：" + e.getMessage(), e);
        }
        getHibernateTemplate().flush();
        return info;
    }

    @Override
    public void updateThirdPartyOrder4() {
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("succeeded").eq(0));
        criteria.add(Property.forName("isThirdParty").eq(1));
        criteria.add(Property.forName("blockchain_name").eq("GCash pay"));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria, 0, 20);
        for (RechargeBlockchain rechargeBlockchain : rechargeBlockchains) {
            Date created = rechargeBlockchain.getCreated();
            if (DateUtils.addMinute(created, 16).before(new Date())) {
                rechargeBlockchain.setSucceeded(2);
                rechargeBlockchainService.update(rechargeBlockchain);
                tipService.deleteTip(rechargeBlockchain.getId().toString());
            }
//            }
        }
    }


    public void setThirdPartyManager(ThirdPartyManager thirdPartyManager) {
        this.thirdPartyManager = thirdPartyManager;
    }

    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }

    public void setWalletLogService(WalletLogService walletLogService) {
        this.walletLogService = walletLogService;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setRechargeBlockchainService(RechargeBlockchainService rechargeBlockchainService) {
        this.rechargeBlockchainService = rechargeBlockchainService;
    }

    public void setUserDataService(UserDataService userDataService) {
        this.userDataService = userDataService;
    }


    @Override
    public String submitTopUpRequest(HttpServletRequest request, String partyId, String channel, String amount) {
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);
        // 保存充值记录
        double amount_double = Double.parseDouble(amount);
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(amount_double);
        recharge.setSymbol(Constants.WALLET);
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(1);
        recharge.setAmount(Double.valueOf(amount));
        recharge.setPayUrl("");
        recharge.setBlockchain_name("PayV11");
        this.getHibernateTemplate().setCheckWriteOperations(false);
        this.getHibernateTemplate().save(recharge);

        String baseUrl = HttpUtils.getBaseURL(request.getRequestURI(), request::getHeader);
        logger.info("提交第三方充值请求，URL："+baseUrl+"/"+request.getRequestURI());
        //baseUrl = "";  // 如果获取不到需要手工设置
        String notifyUrl = String.format("%s/%s",baseUrl,"wap/api/thirdPartyRecharge!payment_v11_notify.action");
        // 支付成功返回页面
        String returnUrl = String.format("%s/%s",baseUrl,"www/passsuess?id="+ recharge.getId());
        String userIp = HttpUtils.getRealIP(request::getHeader);

        Map<String,String> extra = new HashMap<>();
        extra.put("orderNo",orderNo);
        String payUrl = PayCore.submitPayment(channel,"用户充值",orderNo,new BigDecimal(amount),
                notifyUrl,returnUrl,
                userIp,
                Types.toJson(extra));



        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getVolume());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);

        tipService.saveTip(recharge.getId(), TipConstants.RECHARGE_BLOCKCHAIN);
        return payUrl;
    }

    @Override
    public RechargeInfo updatePaymentSuccessStatus(String orderNo) {
        //订单号
        DetachedCriteria criteria = DetachedCriteria.forClass(RechargeBlockchain.class);
        criteria.add(Property.forName("order_no").eq(orderNo));
        List<RechargeBlockchain> rechargeBlockchains = (List<RechargeBlockchain>) getHibernateTemplate().findByCriteria(criteria);
        if (CollectionUtils.isEmpty(rechargeBlockchains)) {
            logger.error("第三方充值订单不存在");
            throw new BusinessException("订单不存在");
        }
        RechargeBlockchain recharge = rechargeBlockchains.get(0);

        double amount = recharge.getAmount();

//        String product = payRespVo.getProduct();
//        if (!"TRC20Buy".equals(product)) {
//        double gcashRate = 58.00;
//        Syspara gcashRateSys = sysparaService.find("thirdParty_Gcash_rate");
//        if (gcashRateSys != null) {
//            gcashRate = Double.parseDouble(gcashRateSys.getValue());
//        }
//        amount = Arith.div(amount, gcashRate, 2);
//
////        }
        if (recharge.getSucceeded() == 1) {
            return null;
        }
        Date date = new Date();
        recharge.setReviewTime(date);
        recharge.setSucceeded(1);
        recharge.setAmount(amount);


        /**
         * 如果是usdt则加入wallet，否则寻找walletExtend里相同币种
         */

       // Party party = this.partyService.cachePartyBy(recharge.getPartyId(), false);

        Wallet wallet = walletService.saveWalletByPartyId(recharge.getPartyId());

        double amount_before = wallet.getMoney();

        double exchangeAmount = this.getExchangeAmount(amount);
        // ，将充值提成记到充值用户身上
        walletService.update(wallet.getPartyId().toString(), exchangeAmount, 0.0);


        /*
         * 保存资金日志
         */
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(exchangeAmount);
        moneyLog.setAmount_after(Arith.add(wallet.getMoney(), exchangeAmount));

        moneyLog.setLog("充值订单[" + recharge.getOrder_no() + "]");
        moneyLog.setPartyId(recharge.getPartyId());
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTENT_RECHARGE);
        moneyLog.setCreateTime(date);
        moneyLogService.save(moneyLog);
        logger.info("充值成功，用户id:" + recharge.getPartyId() + ",金额:" + exchangeAmount+"(CNY:"+amount+")，充值后金额:"+moneyLog.getAmount_after());

        WalletLog walletLog = walletLogService.find(Constants.MONEYLOG_CATEGORY_RECHARGE, recharge.getOrder_no());
        walletLog.setUsdtAmount(exchangeAmount);
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setStatus(recharge.getSucceeded());
        walletLogService.update(walletLog);

//        recharge.setVolume(Double.valueOf(success_amount));
        getHibernateTemplate().update(recharge);


        // 发布一个充值审核成功的事件
//        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        RechargeInfo info = new RechargeInfo();
        info.setApplyUserId(recharge.getPartyId().toString());
        info.setOrderNo(orderNo);
        info.setWalletLogId(walletLog.getId().toString());
        info.setEventTime(date);
        info.setAmount(recharge.getAmount());
////        wac.publishEvent(new RechargeSuccessEvent(this, info));
//
//        //记录首充时间
//        if (Objects.isNull(party.getFirstRechargeTime()) && party.getRolename().equals(Constants.SECURITY_ROLE_MEMBER)) {
//            debugLogger.info("-----> 首充用户id", party.getId());
//            party.setFirstRechargeTime(new Date());
//        }

        /**
         * 给他的代理添加充值记录
         */
//        userDataService.saveRechargeHandle(recharge.getPartyId(), amount, "usdt");

        /**
         * 若已开启充值奖励 ，则充值到账后给他的代理用户添加奖金
         */
//			if ("true".equals(user_recom_bonus_open.getValue())) {
//				rechargeBonusService.saveBounsHandle(recharge, 1);
//			}

        // 充值到账后给他增加提现流水限制金额 充值到账后，当前流水大于提现限制流水时是否重置提现限制流水并将Party表里的当前流水设置清零，
        // 1不重置，2重置
//        String recharge_sucess_reset_withdraw = this.sysparaService.find("recharge_sucess_reset_withdraw").getValue();
//        if ("1".equals(recharge_sucess_reset_withdraw)) {
//            party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
//            if (party.getWithdraw_limit_now_amount() > party.getWithdraw_limit_amount()) {
//                party.setWithdraw_limit_now_amount(0);
//            }
//        }
//        if ("2".equals(recharge_sucess_reset_withdraw)) {
//            double withdraw_limit_turnover_percent = Double
//                    .valueOf(sysparaService.find("withdraw_limit_turnover_percent").getValue());
//            double party_withdraw = Arith.mul(party.getWithdraw_limit_amount(), withdraw_limit_turnover_percent);
//
//            if (party.getWithdraw_limit_now_amount() >= party_withdraw) {
//                party.setWithdraw_limit_amount(amount);
//                party.setWithdraw_limit_now_amount(0);
//            } else {
//                party.setWithdraw_limit_amount(Arith.add(party.getWithdraw_limit_amount(), amount));
//            }
//        }
//
//        partyService.update(party);

        tipService.deleteTip(recharge.getId().toString());

        try {
            rechargeBlockchainService.updateFirstSuccessRecharge(orderNo);
        } catch (Exception e) {
            logger.error("判断首充礼金报错，报错信息为：" + e.getMessage(), e);
        }
        getHibernateTemplate().flush();
        return info;
    }

    /**
     * 兑换美元价格
     * @param amount CNY价格
     * @return 美元价格
     */
    private double getExchangeAmount(double amount) {
        StringBuffer queryString = new StringBuffer(" FROM ExchangeRate WHERE currency = 'CNY'");
        List<ExchangeRate> list =   (List<ExchangeRate>) this.getHibernateTemplate().find(queryString.toString());
        if (list != null && !list.isEmpty()) {
            return Arith.div(amount,list.get(0).getRata());
        }
        return 0;
    }

    @Override
    public void submitManualRequest(String partyId, String channel, String amount) {
        String orderNo = DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8);

        double amount_double = Double.parseDouble(amount);

        // 如果支付宝或微信，则为人民币，否则为USDT
        String symbol = "wxpay".equals(channel) || "alipay".equals(channel) ? "CNY" : Constants.WALLET;
        double exchangeAmount = this.getExchangeAmount(amount_double);
        // 保存充值记录
        RechargeBlockchain recharge = new RechargeBlockchain();
        recharge.setVolume(amount_double);
        recharge.setSymbol(Types.orValue(symbol,Constants.WALLET));
        recharge.setPartyId(partyId);
        recharge.setSucceeded(0);
        recharge.setCreated(new Date());
        recharge.setOrder_no(orderNo);
        recharge.setIsThirdParty(0);
        recharge.setAmount(exchangeAmount);
        recharge.setPayUrl("");
        recharge.setBlockchain_name(channel.toUpperCase()+"-MANUAL");
        this.getHibernateTemplate().setCheckWriteOperations(false);
        this.getHibernateTemplate().save(recharge);

        /*
         * 保存资金日志
         */
        WalletLog walletLog = new WalletLog();
        walletLog.setCategory(Constants.MONEYLOG_CATEGORY_RECHARGE);
        walletLog.setPartyId(recharge.getPartyId());
        walletLog.setOrder_no(recharge.getOrder_no());
        walletLog.setStatus(recharge.getSucceeded());
        walletLog.setAmount(recharge.getAmount());
        // 换算成USDT单位 TODO
        walletLog.setWallettype(recharge.getSymbol());
        walletLog.setCreateTime(new Date());
        walletLogService.save(walletLog);
        tipService.saveTip(recharge.getId(), TipConstants.RECHARGE_BLOCKCHAIN);
    }

}
