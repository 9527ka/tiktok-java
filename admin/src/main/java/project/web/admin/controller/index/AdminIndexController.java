package project.web.admin.controller.index;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.SellerTopNDto;
import project.mall.utils.DateStringTypeEnum;
import project.mall.utils.DateTypeToTime;
import project.party.PartyService;
import project.user.UserDataService;
import project.wallet.WalletLogService;
import project.wallet.dto.PartySumDataDTO;
import security.web.BaseSecurityAction;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 综合查询
 */
@RestController
public class AdminIndexController extends BaseSecurityAction {


    private Logger logger = LogManager.getLogger(AdminIndexController.class);

    @Autowired
    private PartyService partyService;

    @Autowired
    private UserDataService userDataService;

    @Resource
    protected WalletLogService walletLogService;

    @Resource
    private SellerGoodsService sellerGoodsService;

    private final String action = "normal/adminIndexAction!";
//
//    /**
//     * 综合查询 页面
//     */
//    @RequestMapping(value = action + "view.action")
//    public ModelAndView view(HttpServletRequest request) {
//
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.setViewName("index_admin");
//        String timeType = request.getParameter("timeType");
//        if (StringUtils.isEmptyString(timeType)) {
//            timeType = "day";
//        }
//        Map<String, Object> statistics = new HashMap<String, Object>();
//        Page page = new Page();
//        try {
//
//            String today = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
//
//            String loginPartyId = this.getLoginPartyId();
//
//            // 当日用户增量
//            int todayUserCount = 0;
//
//            // 用户总量
//            int allUserCount = 0;
//
//            List<Party> cacheAll = this.partyService.getAll();
//            for (Party party : cacheAll) {
//                if (Constants.SECURITY_ROLE_MEMBER.equals(party.getRolename())) {
//                    if (today.equals(DateUtils.format(party.getCreateTime(), DateUtils.DF_yyyyMMdd))) {
//                        todayUserCount++;
//                    }
//                    allUserCount++;
//                }
//            }
//            Map<String, Object> daySumData = this.adminAllStatisticsService.daySumData(loginPartyId, today);
////			page = adminOrderService.pagedQuery(
////					1, 10, null, null, null, null, null, null, null, -2);
//            Map daySumData1 = adminMallOrderService.findDaySumData();
//            Map kycSumData = adminKycService.findKycSumData();
//            statistics.putAll(daySumData1);
//            statistics.putAll(kycSumData);
//            statistics.put("recharge", daySumData.get("recharge_usdt"));
//            statistics.put("withdraw", daySumData.get("withdraw"));
//            statistics.put("page", page);
//            // 充提差额
//            statistics.put("balance_amount", daySumData.get("balance_amount"));
//            statistics.put("totle_income", daySumData.get("totle_income"));
//            statistics.put("today_user_count", todayUserCount);
//            statistics.put("all_user_count", allUserCount);
//            statistics.put("today_recharge_user_count", this.userDataService.filterRechargeByDate(today));
//            statistics.put("sum_usdt_amount", this.adminUserMoneyStatisticsService.getSumWalletByMember(loginPartyId));
//
//        } catch (BusinessException e) {
//            modelAndView.addObject("error", e.getMessage());
//            modelAndView.addObject("statistics", statistics);
//            return modelAndView;
//        } catch (Throwable t) {
//            logger.error(" error ", t);
//            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
//            modelAndView.addObject("statistics", statistics);
//            return modelAndView;
//        }
//
//        modelAndView.addObject("statistics", statistics);
//        modelAndView.addObject("page", page);
//        modelAndView.addObject("timeType", timeType);
//        return modelAndView;
//    }


    @RequestMapping(value = action + "viewNew.action")
    public ModelAndView viewNew(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index_admin");

        String timeType = request.getParameter("timeType");
        if (StringUtils.isEmptyString(timeType)) {
            timeType = "day";
        }
        modelAndView.addObject("timeType", timeType);
        return modelAndView;
    }




    @RequestMapping(value = action + "viewHead.action")
    public Map viewHead(HttpServletRequest request) {
        String timeType = request.getParameter("timeType");
        if (StringUtils.isEmptyString(timeType)) {
            timeType = "day";
        }

        DateStringTypeEnum dateStringTypeEnum = DateStringTypeEnum.fromCode(timeType);

        Map<String, String> timeMap = DateTypeToTime.convertTime(dateStringTypeEnum);

        String startTime = timeMap.get("startTime");
        String endTime = timeMap.get("endTime");
        Map<String, Object> statistics = new HashMap<String, Object>();
        try {

            PartySumDataDTO partySumData  = userDataService.getPartyDataBtDay(startTime, endTime);
            //充提差额
            double chargeBalanceAmount = Objects.isNull(partySumData) ? 0.00 : Arith.sub(partySumData.getTotalRecharge(),partySumData.getTotalWithdraw());
            statistics.put("rechargeNum", Objects.isNull(partySumData) ? 0 : partySumData.getRechargeNum());
            statistics.put("withdrawNum", Objects.isNull(partySumData) ? 0 : partySumData.getWithdrawNum());
            statistics.put("rechargeAmount", Objects.isNull(partySumData) ? 0.00 : partySumData.getTotalRecharge());
            statistics.put("withdrawAmount", Objects.isNull(partySumData) ? 0 : partySumData.getTotalWithdraw());
            statistics.put("chargeBalanceAmount", chargeBalanceAmount);

            PartySumDataDTO newPartySumData  = userDataService.getPartyNewDataBtDay(startTime, endTime);
            double newChargeBalanceAmount = Objects.isNull(newPartySumData) ? 0D : Arith.sub(newPartySumData.getTotalRecharge(),newPartySumData.getTotalWithdraw());
            //新充提差额
            statistics.put("newChargeBalanceAmount", newChargeBalanceAmount);
            //新充值人数
            statistics.put("newRechargeNum", Objects.isNull(newPartySumData) ? 0 : newPartySumData.getRechargeNum());
            //新充值金额
            statistics.put("newRechargeAmount", Objects.isNull(newPartySumData) ? 0.00 : newPartySumData.getTotalRecharge());
            //新提现人数
            statistics.put("newWithdrawNum", Objects.isNull(newPartySumData) ? 0 : newPartySumData.getWithdrawNum());
            //新提现金额
            statistics.put("newWithdrawAmount", Objects.isNull(newPartySumData) ? 0 : newPartySumData.getTotalWithdraw());
            return statistics;
        } catch (BusinessException e) {
            statistics.put("code", 500);
            statistics.put("error", e.getMessage());
            return statistics;
        } catch (Throwable t) {
            logger.error(" error ", t);
            statistics.put("code", 500);
            statistics.put("error", t.getMessage());
            return statistics;
        }
    }


    @RequestMapping(value = action + "viewMiddle.action")
    public Map viewMiddle(HttpServletRequest request) {
        String timeType = request.getParameter("timeType");
        if (StringUtils.isEmptyString(timeType)) {
            timeType = "day";
        }
        DateStringTypeEnum dateStringTypeEnum = DateStringTypeEnum.fromCode(timeType);

        Map<String, String> timeMap = DateTypeToTime.convertTime(dateStringTypeEnum);

        String startTime = timeMap.get("startTime");
        String endTime = timeMap.get("endTime");
        Map<String, Object> statistics = new HashMap<String, Object>();
        try {
            //活跃
            Integer loginNum = partyService.getCountLoginByDay(startTime, endTime);

            //新增用户
            Integer registerNum = partyService.getCountRegisterByDay(startTime, endTime);

            //总店铺
            Integer allSellerNum = partyService.getCountAllSeller();

            //总用户
            Integer allUserNum = partyService.getCountAllUser();

            //新增店铺
            Integer registerSellerNum = partyService.getCountRegisterSellerByDay(startTime, endTime);

            //订单
            Integer orderNum = partyService.getCountOrderByDay(startTime, endTime);

            Map<String, Object> profitDataMap = walletLogService.getTotalProfitByDay(startTime, endTime);

            Map<String, Object> totalSales = sellerGoodsService.querySumSellerOrdersPrize(startTime, endTime);

            //返佣金额
            Object profitAmount = profitDataMap.get("profit");
            //销售总额
            Object totalSalesAmount = totalSales.get("totalSales");

            statistics.put("loginNum", loginNum);
            statistics.put("registerNum", registerNum);
            statistics.put("allSellerNum", allSellerNum);
            statistics.put("allUserNum", allUserNum);
            statistics.put("registerSellerNum", registerSellerNum);
            statistics.put("orderNum", orderNum);
            statistics.put("rebateAmount", profitAmount);
            statistics.put("totalSalesAmount", Objects.isNull(totalSalesAmount) ? 0 : totalSalesAmount);
            return statistics;
        } catch (BusinessException e) {
            statistics.put("code", 500);
            statistics.put("error", e.getMessage());
            return statistics;
        } catch (Throwable t) {
            logger.error(" error ", t);
            statistics.put("code", 500);
            statistics.put("error", t.getMessage());
            return statistics;
        }
    }

    @RequestMapping(value = action + "viewSellerTop.action")
    public List<SellerTopNDto> viewSellerTop(HttpServletRequest request) {

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("index_admin");
        String timeType = request.getParameter("timeType");
        if (StringUtils.isEmptyString(timeType)) {
            timeType = "day";
        }
        DateStringTypeEnum dateStringTypeEnum = DateStringTypeEnum.fromCode(timeType);

        Map<String, String> timeMap = DateTypeToTime.convertTime(dateStringTypeEnum);

        String startTime = timeMap.get("startTime");
        String endTime = timeMap.get("endTime");
        // 店铺 TOP10
        List<SellerTopNDto> top10SellerList = sellerGoodsService.cacheTopNSellers(startTime, endTime, 10);
        return top10SellerList;
    }

}
