package project.web.api.seller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.goods.SellerGoodsService;
import project.mall.orders.AdminMallOrderService;
import project.mall.orders.GoodsOrdersService;
import project.mall.seller.SellerService;
import project.mall.seller.dto.SellerOrderLineDTO;
import project.mall.seller.model.Seller;
import project.mall.utils.LocalDateTimeUtils;
import project.redis.RedisHandler;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.web.api.dto.IntegratedScoreDto;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 商户后台-仪表盘
 */
@RestController
@CrossOrigin
public class SellerInstrumentPanelController extends BaseAction {

    private final String action = "/seller/instrument-panel!";

    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerService sellerService;

    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    protected GoodsOrdersService goodsOrdersService;
    @Resource
    protected AdminMallOrderService adminMallOrderService;
    @Resource
    private KycService kycService;

    @PostMapping(action + "head.action")
    public Object head(HttpServletRequest request) {

        String sellerId = request.getParameter("sellerId");

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        JSONObject o = new JSONObject();

        if(StringUtils.isEmptyString(sellerId)) {
            JSONObject object = new JSONObject();

            o.put("goodsNum", 0);
            o.put("totalSales", 0);
            o.put("orderNum", 0);
            o.put("totalProfit", 0);
            o.put("creditScore",0);
            o.put("focusCount", 0);
            o.put("rating", 0);
            o.put("visits1Today", 0);
            o.put("visits7Today", 0);
            o.put("storeCheckState", 0);
            object.put("head", o);
            resultObject.setData(object);

            return resultObject;
        }

        Map<String, Object> sellerResult = sellerService.findBySellId(sellerId);

        Map<String, Object> goodResult = goodsOrdersService.findBySellId(sellerId);

        Map<String, Object> adminResult = adminMallOrderService.findBySellId(sellerId);

        Map<String, Object> adminProfitResult =  adminMallOrderService.findProfitBySellId(sellerId);

        o.put("goodsNum", goodResult.get("goodsNum"));
        o.put("totalSales", adminResult.get("totalSales"));

        o.put("orderNum", adminResult.get("orderNum"));
        o.put("totalProfit", adminProfitResult.get("totalProfit"));
        // 店铺信誉分
        o.put("creditScore", sellerResult.get("creditScore"));
        // 店铺关注数
        o.put("focusCount", Integer.parseInt(String.valueOf(sellerResult.get("focusCount"))) + Integer.parseInt(String.valueOf(sellerResult.get("focusCountReals"))));
        float avgRating = goodsOrdersService.selectAvgEvaluationBySellerId(sellerId);
        //评分，暂时没有
        o.put("rating", avgRating);
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        long visits1Today = sellerGoodsService.getNumberOfVisitorsByDate(sellerId, now, now);
        o.put("visits1Today", visits1Today);//今天访客量
        calendar.add(Calendar.DAY_OF_MONTH, -7);
        long visits7Today = sellerGoodsService.getNumberOfVisitorsByDate(sellerId, calendar.getTime() , now);
        o.put("visits7Today", visits7Today); //7天访客量
        calendar.add(Calendar.DAY_OF_MONTH, -30);
        long visits30Today = sellerGoodsService.getNumberOfVisitorsByDate(sellerId, calendar.getTime() , now );
        o.put("visits30Today", visits30Today); //30天访客量
        IntegratedScoreDto integratedScoreDto = goodsOrdersService.getIntegratedScoreCount(sellerId);
        o.put("todayOrder", integratedScoreDto.getTodayOrder()); //今日订单
        o.put("todaySales", integratedScoreDto.getTodaySales()); //今日销售额
        o.put("todayProfit", integratedScoreDto.getTodayProfit());  //今日利润

        // 店铺认证状态: 0-未设置，1-完成基础设置，2-完成店铺认证，3-完成商品上架
        //String partyId = this.getLoginPartyId();
        // 注意：当数据库中没有 kyc 记录时，也会 new 出一个记录返回
        Kyc kyc = this.kycService.get(sellerId);
        Seller seller = sellerService.getSeller(sellerId);
        // 0-冻结 1-正常
        o.put("freeze", seller.getFreeze());
        if (kyc == null || StrUtil.isBlank(kyc.getName())) {
            o.put("storeCheckState", 0);
        } else {
            if (kyc.getStatus() == 2) {
                // 店铺已认证
                // 但是未必上传了商品
                int goodsCount = sellerGoodsService.getCountGoods(sellerId, 1);
                if (goodsCount > 0) {
                    o.put("storeCheckState", 3);
                } else {
                    // 完成认证，未上传商品
                    o.put("storeCheckState", 2);
                }
            } else {
                o.put("storeCheckState", 1);
            }
        }

        JSONObject object = new JSONObject();
        object.put("head", o);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "line.action")
    public Object line(HttpServletRequest request) {

        String sellerId = request.getParameter("sellerId");

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        if (StringUtils.isEmptyString(sellerId)) {
            return resultObject;
        }

        Integer contentType = 2;
        String statusString = request.getParameter("type");
        if (statusString != null) {
            contentType = Integer.valueOf(statusString);
        }

        String startTime = null;
        String endTime = null;

        if (contentType == 0) {
            startTime = DateUtils.dateToStr(DateUtils.getDayStart(new Date()), DateUtils.NORMAL_DATE_FORMAT);
            endTime = DateUtils.getDayEndString(new Date());
        } else if (contentType == 1) {
            startTime = LocalDateTimeUtils.getWeekBegin();
            endTime = DateUtils.getDayEndString(new Date());
        } else if (contentType == 2) {
            startTime = LocalDateTimeUtils.getMonthBegin();
            endTime = DateUtils.getDayEndString(new Date());
        }

        // List<Object[]> results = null;
        List<SellerOrderLineDTO> results = null;
        if (contentType == 0) {
            // 当天记录，基于小时分组
            results = sellerService.findLineBySellIdAndHour(sellerId, startTime, endTime);
            // 以 24 小时制填充所有时刻的记录
            String initDayStr = DateUtils.dateToStr(DateUtils.getDayStart(new Date()), DateUtils.DEFAULT_DATE_FORMAT);
            if (results == null) {
                results = new ArrayList();
            }
            // 构造 24 小时的刻度
            List<String> hour24List = new ArrayList<>();

            int hour = LocalDateTime.now().getHour() + 1;

            for (int i = 0; i < hour; i++) {
                if (i < 10) {
                    hour24List.add(initDayStr + " 0" + i);
                } else {
                    hour24List.add(initDayStr + " " + i);
                }
            }
            Map<String, SellerOrderLineDTO> dataMap = new HashMap<>();
            for (SellerOrderLineDTO oneItem : results) {
                dataMap.put(oneItem.getDayString(), oneItem);
            }
            // 使用 24 刻度填充缺失的数据
            for (String hourStr : hour24List) {
                if (dataMap.containsKey(hourStr)) {
                    // 当前时刻有真实的统计数据
                    continue;
                }

                // 当前时刻缺失统计数据，构造 0 值数据
                SellerOrderLineDTO mockLineData = new SellerOrderLineDTO();
                mockLineData.setDayString(hourStr);
                mockLineData.setCountSales("0");
                mockLineData.setCountVisits("0");

                dataMap.put(hourStr, mockLineData);
            }
            results.clear();
            results.addAll(dataMap.values());

            Collections.sort(results, new Comparator<SellerOrderLineDTO>() {
                public int compare(SellerOrderLineDTO o1, SellerOrderLineDTO o2) {
                    return o1.getDayString().compareTo(o2.getDayString());
                }
            });
        } else {
            results = sellerService.findLineBySellId(sellerId, startTime, endTime);
        }

        for (SellerOrderLineDTO obj : results) {
            if (contentType == 0) {
                // 当天记录，基于小时分组
                String hourTimeStr = obj.getDayString();
                String hour = hourTimeStr.substring(11, 13);
                obj.setDayString(hour);
            }
        }

        JSONObject object = new JSONObject();
        object.put("line", results);
        resultObject.setData(object);
        return resultObject;
    }


    @PostMapping(action + "goods.action")
    public Object goods(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        String lang = this.getLanguage(request);

        JSONObject object = new JSONObject();
        object.put("goods", sellerGoodsService.listGoodsSellerSales(partyId, lang));
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "stats.action")
    public Object stats(HttpServletRequest request) {

        String sellerId = request.getParameter("sellerId");

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        if(StringUtils.isEmptyString(sellerId)) {

            JSONObject o = new JSONObject();
            o.put("orderNum", 0);
            o.put("orderIng", 0);
            o.put("orderFinish", 0);
            o.put("orderCancel", 0);
            JSONObject object = new JSONObject();
            object.put("stats", o);
            resultObject.setData(object);
            return resultObject;
        }

        Map<String, Object> results = sellerService.loadReportStatus(sellerId);

        JSONObject o = new JSONObject();

        o.put("orderNum", results.get("orderNum"));
        o.put("orderIng", results.get("orderIng"));
        o.put("orderFinish", results.get("orderFinish"));
        o.put("orderCancel", results.get("orderCancel"));
        o.put("orderRefund", results.get("orderRefund"));

        JSONObject object = new JSONObject();
        object.put("stats", o);
        resultObject.setData(object);
        return resultObject;
    }
}
