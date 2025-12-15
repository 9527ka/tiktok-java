package project.mall.credit.impl;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate5.support.HibernateDaoSupport;
import org.springframework.security.providers.encoding.PasswordEncoder;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.credit.CreditService;
import project.mall.credit.dto.CreditOrderDto;
import project.mall.credit.dto.CreditPersonalDto;
import project.mall.credit.model.Credit;
import project.mall.loan.model.LoanConfig;
import project.party.PartyService;
import project.party.model.Party;
import project.tip.TipService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import security.SecUser;
import security.internal.SecUserService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class CreditServiceImpl extends HibernateDaoSupport implements CreditService {

    private PartyService partyService;
    private WalletService walletService;
    private JdbcTemplate jdbcTemplate;
    private MoneyLogService moneyLogService;

    private SecUserService secUserService;

    private PasswordEncoder passwordEncoder;

    private TipService tipService;

    @Override
    public LoanConfig queryLoanConfig() {
        StringBuffer queryString = new StringBuffer(" FROM LoanConfig ");
        List<LoanConfig> credits = (List<LoanConfig>) this.getHibernateTemplate().find(queryString.toString());
        return credits.get(0);
    }

    @Override
    public void updateLoanConfig(LoanConfig loanConfig) {
        loanConfig.setId(queryLoanConfig().getId());
        this.getHibernateTemplate().saveOrUpdate(loanConfig);
    }

    public void updateCredit(Credit credit) {
        this.getHibernateTemplate().saveOrUpdate(credit);
    }

    @Override
    public List<Credit> getOnUseCreditById(String partyId) {
        StringBuffer queryString = new StringBuffer(" FROM Credit where partyId = ?0 and status <= 4 ");
        List<Credit> list = new ArrayList<>();
        list = (List<Credit>) this.getHibernateTemplate().find(queryString.toString(), new Object[]{partyId});
        return list;
    }

    @Override
    public void saveCreate(Credit credit) {
//        如果是重新申请但是不传入单号，报错
        if (Objects.isNull(credit.getId())) {
            List<Credit> onUseCredit = getOnUseCreditById(credit.getPartyId());
            if (onUseCredit.size() > 0) {
                throw new BusinessException("已有未完成的贷款单，不能重复申请");
            }
        }
        this.getHibernateTemplate().saveOrUpdate(credit);
    }

    public Credit findCreditsById(String id) {
        if (StringUtils.isNullOrEmpty(id)) {
            throw new BusinessException("参数异常");
        }
        return this.getHibernateTemplate().get(Credit.class, id);
    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String partyId) {
        if (StringUtils.isNullOrEmpty(partyId)) {
            throw new BusinessException("参数异常");
        }
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer(" SELECT UUID,APPLY_AMOUNT,CUSTOMER_SUBMIT_TIME,CREDIT_PERIOD,CREDIT_RATE,STATUS,REJECT_REASON FROM T_MALL_CREDIT ");
        StringBuffer countQueryString = new StringBuffer("  SELECT count(1) FROM T_MALL_CREDIT  ");
        queryString.append(" WHERE PARTY_ID ='" + partyId + "'");
        countQueryString.append(" WHERE PARTY_ID ='" + partyId + "'");
        parameters.put("partyId", partyId);
        queryString.append(" ORDER BY CUSTOMER_SUBMIT_TIME DESC ");
        queryString.append("   limit " + (pageNo - 1) * pageSize + "," + pageSize);
        List list = jdbcTemplate.queryForList(queryString.toString());
        int totalCount = jdbcTemplate.queryForObject(countQueryString.toString(), Integer.class);
        Iterator iterator = list.iterator();
        List<CreditPersonalDto> resultList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map rowMap = (Map) iterator.next();
            String id = (String) rowMap.getOrDefault("UUID", "");
            Double applyAmount = (Double) rowMap.getOrDefault("APPLY_AMOUNT", "0");
            LocalDateTime customerSubmitTime = (LocalDateTime) rowMap.getOrDefault("CUSTOMER_SUBMIT_TIME", "");
            Integer creditPeriod = (Integer) rowMap.getOrDefault("CREDIT_PERIOD", "0");
            Double creditRate = (Double) rowMap.getOrDefault("CREDIT_RATE", "0");
            Integer status = (Integer) rowMap.getOrDefault("STATUS", "0");
            Date date = Date.from(customerSubmitTime.atZone(ZoneId.systemDefault()).toInstant());
            String rejectReason = (String) rowMap.getOrDefault("REJECT_REASON", "");
            CreditPersonalDto creditPersonalDto = CreditPersonalDto.builder()
                    .applyAmount(new BigDecimal(String.valueOf(applyAmount)).setScale(2, BigDecimal.ROUND_DOWN))
                    .id(id)
                    .creditPeriod(creditPeriod)
                    .creditRate(new BigDecimal(String.valueOf(creditRate)).setScale(2, BigDecimal.ROUND_DOWN))
                    .customerSubmitTime(DateUtils.format(date, DateUtils.NORMAL_DATE_FORMAT))
                    .status(status).rejectReason(rejectReason).build();
            String status_str = "";
            switch (status) {
                case 1:
                    status_str = "申请中";
                    break;
                case 2:
                    status_str = "借款成功";
                    break;
                case 3:
                    status_str = "已逾期";
                    break;
                case 4:
                    status_str = "借款失败";
                    break;
                case 5:
                    status_str = "已还款";
                    break;
                default:
                    break;
            }
            creditPersonalDto.setStatusStr(status_str);
            resultList.add(creditPersonalDto);
        }
        Page page = new Page();
        page.setElements(resultList);
        page.setTotalElements(totalCount);
        return page;
    }

    @Override
    public Page pagedQuery(int pageNo, int pageSize, String userCode_para, String username_para,
                           String identification_para, String status_para, String customerSubmitTime_start_para, String customerSubmitTime_end_para, String partyId) {
        Map<String, Object> parameters = new HashMap<>();
        StringBuffer queryString = new StringBuffer("SELECT credit.UUID as creditId,party.USERCODE AS usercode, party_parent.USERNAME AS username_parent, party.ROLENAME AS rolename, party.USERNAME AS username, credit.STATUS AS status,   ");
        queryString.append(" credit.CREDIT_PERIOD AS creditPeriod, credit.APPLY_AMOUNT AS applyAmount, credit.CREDIT_RATE AS creditRate, credit.TOTAL_INTEREST AS totalInterest,   ");
        queryString.append(" credit.TOTAL_REPAYMENT AS totalRepayment, credit.ACTUAL_REPAYMENT AS actualRepayment, credit.REJECT_REASON AS rejectReason,   ");
        queryString.append(" credit.CUSTOMER_SUBMIT_TIME AS customerSubmitTime, credit.SYSTEM_AUDIT_TIME AS systemAuditTime, credit.FINAL_REPAY_TIME AS finalRepayTime,  ");
        queryString.append(" credit.REAL_NAME AS realName, credit.IDENTIFICATION AS identification, credit.COUNTRY_ID AS countryId, credit.IMG_CERTIFICATE_FACE imgCertificateFace,  ");
        queryString.append(" credit.IMG_CERTIFICATE_BACK AS imgCertificateBack, credit.IMG_CERTIFICATE_HAND AS imgCertificateHand ");
        queryString.append(" FROM T_MALL_CREDIT credit  ");
        queryString.append(" LEFT JOIN PAT_PARTY party ON credit.PARTY_ID = party.UUID  ");
        queryString.append(" LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  ");
        queryString.append(" LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID  ");
        queryString.append(" WHERE 1 = 1 ");
        StringBuffer countQueryString = new StringBuffer("SELECT COUNT(1) FROM T_MALL_CREDIT credit  ");
        countQueryString.append(" LEFT JOIN PAT_PARTY party ON credit.PARTY_ID = party.UUID  ");
        countQueryString.append(" LEFT JOIN PAT_USER_RECOM user ON user.PARTY_ID = party.UUID  ");
        countQueryString.append(" LEFT JOIN PAT_PARTY party_parent ON user.RECO_ID = party_parent.UUID  ");
        countQueryString.append(" WHERE 1 = 1 ");
        if (StringUtils.isNotEmpty(userCode_para)) {
            queryString.append(" AND party.USERCODE ='" + userCode_para + "'");
            countQueryString.append(" AND party.USERCODE ='" + userCode_para + "'");
        }
        if (StringUtils.isNotEmpty(username_para)) {
            queryString.append(" AND party.USERNAME='" + username_para + "'");
            countQueryString.append(" AND party.USERNAME='" + username_para + "'");
        }
        if (StringUtils.isNotEmpty(identification_para)) {
            queryString.append(" credit.IDENTIFICATION ='" + identification_para + "'");
            countQueryString.append(" credit.IDENTIFICATION ='" + identification_para + "'");
        }
        if (StringUtils.isNotEmpty(status_para)) {
            queryString.append(" AND credit.STATUS ='" + status_para + "'");
            countQueryString.append(" AND credit.STATUS ='" + status_para + "'");
        }
        if (StringUtils.isNotEmpty(customerSubmitTime_start_para)) {
            queryString.append(" AND credit.CUSTOMER_SUBMIT_TIME >='" + customerSubmitTime_start_para + "'");
            countQueryString.append(" AND credit.CUSTOMER_SUBMIT_TIME >='" + customerSubmitTime_start_para + "'");
        }
        if (StringUtils.isNotEmpty(customerSubmitTime_end_para)) {
            queryString.append(" AND credit.CUSTOMER_SUBMIT_TIME <='" + customerSubmitTime_end_para + "'");
            countQueryString.append(" AND credit.CUSTOMER_SUBMIT_TIME <='" + customerSubmitTime_end_para + "'");
        }
        queryString.append(" ORDER BY CUSTOMER_SUBMIT_TIME DESC ");
        queryString.append("   limit " + (pageNo - 1) * pageSize + "," + pageSize);
        List list = jdbcTemplate.queryForList(queryString.toString());
        int totalCount = jdbcTemplate.queryForObject(countQueryString.toString(), Integer.class);
        Iterator iterator = list.iterator();
        List<CreditOrderDto> resultList = new ArrayList<>();
        while (iterator.hasNext()) {
            Map rowMap = (Map) iterator.next();
            String creditId = (String) rowMap.getOrDefault("creditId", "");
            String usercode = (String) rowMap.getOrDefault("usercode", "");
            String username_parent = (String) rowMap.getOrDefault("username_parent", "");
            String rolename = (String) rowMap.getOrDefault("rolename", "");
            String realName = (String) rowMap.getOrDefault("realName", "");
            String identification = (String) rowMap.getOrDefault("identification", "");
            Integer countryId = (Integer) rowMap.getOrDefault("countryId", "");
            String imgCertificateFace = (String) rowMap.getOrDefault("imgCertificateFace", "");
            String imgCertificateBack = (String) rowMap.getOrDefault("imgCertificateBack", "");
            String imgCertificateHand = (String) rowMap.getOrDefault("imgCertificateHand", "");
            String username = (String) rowMap.getOrDefault("username", "0");
            Integer status = (Integer) rowMap.getOrDefault("status", "0");
            Integer creditPeriod = (Integer) rowMap.getOrDefault("creditPeriod", "");
            Double applyAmount = (Double) rowMap.getOrDefault("applyAmount", "0.00");
            Double creditRate = (Double) rowMap.getOrDefault("creditRate", "0.00");
            Double totalInterest = (Double) rowMap.getOrDefault("totalInterest", "0.00");
            Double totalRepayment = (Double) rowMap.getOrDefault("totalRepayment", "0.00");
            Double actualRepayment = (Double) rowMap.getOrDefault("actualRepayment", "0.00");
            String rejectReason = (String) rowMap.getOrDefault("rejectReason", "");
            LocalDateTime customerSubmitTime = (LocalDateTime) rowMap.getOrDefault("customerSubmitTime", "");
            LocalDateTime systemAuditTime = (LocalDateTime) rowMap.getOrDefault("systemAuditTime", "");
            LocalDateTime finalRepayTime = (LocalDateTime) rowMap.getOrDefault("finalRepayTime", "");
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(DateUtils.NORMAL_DATE_FORMAT);
            CreditOrderDto creditOrderDto = CreditOrderDto.builder().creditId(creditId).usercode(usercode).username_parent(username_parent).rolename(rolename).username(username)
                    .creditPeriod(creditPeriod).applyAmount(new BigDecimal(String.valueOf(applyAmount)).setScale(2, BigDecimal.ROUND_DOWN))
                    .creditRate(new BigDecimal(String.valueOf(creditRate)).setScale(2, BigDecimal.ROUND_DOWN))
                    .totalInterest(new BigDecimal(String.valueOf(totalInterest)).setScale(2, BigDecimal.ROUND_DOWN))
                    .totalRepayment(new BigDecimal(String.valueOf(totalRepayment)).setScale(2, BigDecimal.ROUND_DOWN))
                    .actualRepayment(new BigDecimal(String.valueOf(actualRepayment)).setScale(2, BigDecimal.ROUND_DOWN))
                    .rejectReason(rejectReason)
                    .customerSubmitTime(customerSubmitTime.format(dateTimeFormatter))
//                  未审核时，未审核时间systemAuditTime为null，未还款时还款时间finalRepayTime为null,此时需要判空，并给值
                    .systemAuditTime(Objects.isNull(systemAuditTime) ? "" : systemAuditTime.format(dateTimeFormatter))
                    .finalRepayTime(Objects.isNull(finalRepayTime) ? "" : finalRepayTime.format(dateTimeFormatter))
                    .status(status).realName(realName).identification(identification).countryId(countryId).imgCertificateFace(imgCertificateFace).imgCertificateBack(imgCertificateBack)
                    .imgCertificateHand(imgCertificateHand).build();
            resultList.add(creditOrderDto);
        }
        Page page = new Page();
        page.setElements(resultList);
        page.setTotalElements(totalCount);
        return page;
    }

    @Override
    public List<Credit> queryBillCredit(String partyId) {
        StringBuffer queryString = new StringBuffer(" FROM Credit where partyId = ?0 and status in (2,3) ");
        List<Credit> list = new ArrayList<>();
        list = (List<Credit>) this.getHibernateTemplate().find(queryString.toString(), new Object[]{partyId});
        return list;
    }


    public Integer updateOverDue() {
        String hqlUpdate = "update Credit c set c.status = 3 where c.status = 2 and c.expireTime <:currentTime";
        return getHibernateTemplate().execute(session -> {
            return session.createQuery(hqlUpdate)
                    .setDate("currentTime", new Date())
                    .executeUpdate();
        });
    }

    @Override
    public void updateCreditOrder(String partyId, String creditId, String safeword) {
        Party party = partyService.cachePartyBy(partyId, false);

        String partySafeword = party.getSafeword();
        if (StringUtils.isEmptyString(partySafeword)) {
            throw new BusinessException(999, "请设置资金密码");
        }

        if (!this.partyService.checkSafeword(safeword, partyId)) {
            throw new BusinessException(1, "资金密码错误");
        }
        try {
            Credit credit = this.getHibernateTemplate().get(Credit.class, creditId);
            if (credit == null || !partyId.equals(credit.getPartyId())) {
                throw new BusinessException("贷款单不存在");
            }
            if (!(credit.getStatus() == 2 || credit.getStatus() == 3)) {
                throw new BusinessException("该订单已不是可支付状态");
            }
            Map<String, Object> calculate = this.calculate(new Date(), credit);
            double estimatePayment = ((BigDecimal) calculate.get("estimatePayment")).doubleValue();
            Wallet wallet = walletService.saveWalletByPartyId(partyId);

            int frozenState = wallet.getFrozenState();

            double amount_before = wallet.getMoney();

            if(frozenState == 1){
                amount_before = wallet.getMoneyAfterFrozen();
            }
            if (amount_before < estimatePayment) {
                throw new BusinessException("余额不足");
            }
            //            更新实际还款余额，跟新最后一次还款时间
            credit.setActualRepayment(estimatePayment);
            credit.setFinalRepayTime(new Date());
            credit.setStatus(5);
            this.updateCredit(credit);
//            更新钱包余额
            MoneyLog moneyLog = new MoneyLog();

            walletService.update(wallet.getPartyId().toString(), Arith.sub(0, estimatePayment));
            moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
            moneyLog.setAmount_before(amount_before);
            moneyLog.setAmount(Arith.sub(0, estimatePayment));
            moneyLog.setAmount_after(Arith.sub(amount_before, estimatePayment));

            moneyLog.setLog("偿还贷款");
            moneyLog.setPartyId(partyId);
            moneyLog.setWallettype(Constants.WALLET);
            moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_CREDIT_PAY);
            moneyLogService.save(moneyLog);

        } catch (BusinessException e) {
            logger.error("支付失败", e);
            throw new BusinessException(e.getMessage());
        }

    }

    @Override
    public void updateCreditStatus(String creditId, String operateType, String rejectReason, String manualRepay, String safeword, String operator_username) {
        Integer type = null;
        Double payment = null;
        try {
            type = Integer.valueOf(operateType);
            if (StringUtils.isNotEmpty(manualRepay)) {
                payment = Double.valueOf(manualRepay);
            }
        } catch (BusinessException e) {
            throw new BusinessException("参数错误");
        }
        Credit credit = findCreditsById(creditId);
        if (credit.getStatus() == 5) {
            throw new BusinessException("该贷款已结清，不能执行操作！");
        }
//        驳回操作，其前提条件 当前贷款单是未审核状态
        if (type == 4) {
            if (credit.getStatus() != 1) {
                throw new BusinessException("该贷款已不是未审核状态");
            }
            credit.setStatus(type);
            credit.setRejectReason(rejectReason);
            tipService.deleteTip(credit.getId().toString());
        }
//        手动还款
        if (type == 5) {
            if (credit.getStatus() == 1 || credit.getStatus() == 4) throw new BusinessException("该贷款还未通过审核或者已被驳回");

            SecUser sec = this.secUserService.findUserByLoginName(operator_username);
            String sysSafeword = sec.getSafeword();

            String safeword_md5 = passwordEncoder.encodePassword(safeword, operator_username);
            if (!safeword_md5.equals(sysSafeword)) {
                throw new BusinessException("资金密码错误");
            }

            double beforeActualRepayment = credit.getActualRepayment();
            double actualEstimate = Arith.add(beforeActualRepayment, payment);
            Map<String, Object> calculate = calculate(new Date(), credit);
            BigDecimal shouldPayment = (BigDecimal) calculate.get("estimatePayment");
            if (actualEstimate >= shouldPayment.doubleValue()) {
                credit.setStatus(5);
                credit.setActualRepayment(actualEstimate);
                credit.setFinalRepayTime(new Date());
            } else {
                credit.setActualRepayment(actualEstimate);
                credit.setFinalRepayTime(new Date());
            }
        }
        this.getHibernateTemplate().saveOrUpdate(credit);
    }

    @Override
        public String updateCreditPass(String creditId, String safeword, String operator_username) {

        Credit credit = findCreditsById(creditId);
        if (credit.getStatus() != 1) {
            new BusinessException("该贷款已不是未审核状态");
        }

        Party party = partyService.cachePartyBy(credit.getPartyId(), false);
        SecUser sec = this.secUserService.findUserByLoginName(operator_username);
        String sysSafeword = sec.getSafeword();

        String safeword_md5 = passwordEncoder.encodePassword(safeword, operator_username);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("资金密码错误");
        }

        String partyId = credit.getPartyId();
        credit.setStatus(2);
        Date systemAuditTime = new Date();
        credit.setSystemAuditTime(systemAuditTime);
        credit.setExpireTime(DateUtils.addDay(systemAuditTime, credit.getCreditPeriod()));
        this.updateCredit(credit);
        Wallet wallet = walletService.saveWalletByPartyId(party.getId());
        double amount_before = wallet.getMoney();
//            更新钱包余额
        walletService.updateMoeny(wallet.getPartyId().toString(), credit.getApplyAmount());
        MoneyLog moneyLog = new MoneyLog();
        moneyLog.setCategory(Constants.MONEYLOG_CATEGORY_COIN);
        moneyLog.setAmount_before(amount_before);
        moneyLog.setAmount(credit.getApplyAmount());
        moneyLog.setAmount_after(Arith.add(amount_before, credit.getApplyAmount()));

        moneyLog.setLog("发放贷款");
        moneyLog.setPartyId(partyId);
        moneyLog.setWallettype(Constants.WALLET);
        moneyLog.setContent_type(Constants.MONEYLOG_CONTNET_CREDIT_RELEASE);
        moneyLogService.save(moneyLog);

        tipService.deleteTip(credit.getId().toString());
        return partyId;

    }

    @Override
    public void saveCreditPic(String creditId, String imgId, String img) {
        Credit credit = findCreditsById(creditId);
        if (credit != null) {
            if ("1".equals(imgId)) {
                credit.setImgCertificateFace(img);
            } else if ("2".equals(imgId)) {
                credit.setImgCertificateBack(img);
            } else if ("3".equals(imgId)) {
                credit.setImgCertificateHand(img);
            }
            this.getHibernateTemplate().update(credit);
        }
    }

    public Map<String, Object> calculate(java.util.Date currentTime, Credit credit) {
        HashMap<String, Object> resultMap = new HashMap<>();
        int alreadyCreditDays = (int) Math.ceil((currentTime.getTime() - credit.getSystemAuditTime().getTime()) / (86400 * 1000d));
        resultMap.put("alreadyCreditDays", alreadyCreditDays);
        BigDecimal interest = null;
        BigDecimal estimatePayment = null;
        if (Objects.isNull(credit.getSystemAuditTime())) {
            throw new BusinessException("该贷款单还未通过审核");
        }
//            计算利息
        if (alreadyCreditDays <= credit.getCreditPeriod()) {
            interest = new BigDecimal(String.valueOf(Arith.mul(alreadyCreditDays, Arith.mul(credit.getCreditRate(), credit.getApplyAmount())))).setScale(2, BigDecimal.ROUND_DOWN);
            estimatePayment = new BigDecimal(Arith.add(interest.doubleValue(), credit.getApplyAmount())).setScale(2, BigDecimal.ROUND_DOWN);
        } else {
            double notOverdueInterest = Arith.mul(Arith.mul(credit.getCreditPeriod(), credit.getCreditRate()), credit.getApplyAmount());
            double overdueInterest = Arith.mul(Arith.mul(alreadyCreditDays - credit.getCreditPeriod(), credit.getDefaultRate()), credit.getApplyAmount());
            interest = new BigDecimal(String.valueOf(Arith.add(notOverdueInterest, overdueInterest))).setScale(2, BigDecimal.ROUND_DOWN);
            estimatePayment = new BigDecimal(String.valueOf(Arith.add(interest.doubleValue(), credit.getApplyAmount()))).setScale(2, BigDecimal.ROUND_DOWN);
        }
        resultMap.put("interest", interest);
        resultMap.put("estimatePayment", estimatePayment);
        return resultMap;
    }


    public void setPartyService(PartyService partyService) {
        this.partyService = partyService;
    }

    public void setWalletService(WalletService walletService) {
        this.walletService = walletService;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public void setMoneyLogService(MoneyLogService moneyLogService) {
        this.moneyLogService = moneyLogService;
    }

    public void setPasswordEncoder(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    public void setSecUserService(SecUserService secUserService) {
        this.secUserService = secUserService;
    }

    public void setTipService(TipService tipService) {
        this.tipService = tipService;
    }

}
