package project.web.api.seller;

import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.seller.SellerService;
import project.mall.seller.dto.SellerOrderReportDTO;
import project.mall.utils.DateTypeEnum;
import project.mall.utils.DateTypeToTime;
import project.mall.utils.MallPageInfo;
import project.redis.RedisHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 财务报表
 */
@RestController
@CrossOrigin
public class SellerReportController extends BaseAction {
    private final String action = "/seller/report!";

    @Resource
    protected SellerService sellerService;

    @Resource
    protected RedisHandler redisHandler;

    @PostMapping(action + "head.action")
    public Object head(HttpServletRequest request) {

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        JSONObject o = new JSONObject();

        String sellerId = request.getParameter("sellerId");

        if (StringUtils.isEmptyString(sellerId)) {

            o.put("willIncome", 0);
            o.put("totalSales", 0);
            o.put("totalProfit", 0);
            o.put("orderNum", 0);
            o.put("orderReturns", 0);
            o.put("orderCancel", 0);
            JSONObject object = new JSONObject();
            object.put("head", o);
            resultObject.setData(object);
            return resultObject;
        }

        Integer contentType = 0;
        String statusString = request.getParameter("content_type");
        if (StringUtils.isNotEmpty(statusString)) {
            contentType = Integer.valueOf(statusString);
        }

        Map<String, String> time = DateTypeToTime.convert(DateTypeEnum.fromCode(Integer.valueOf(contentType)));
        String startTime = time.get("startTime");
        String endTime = time.get("endTime");

        Map<String, Object> orderCancel = sellerService.loadReportOrderCancel(sellerId, startTime, endTime);
        Map<String, Object> orderNum = sellerService.loadReportOrderNum(sellerId, startTime, endTime);
        Map<String, Object> orderReturns = sellerService.loadReportOrderReturns(sellerId, startTime, endTime);
        Map<String, Object> totalProfit = sellerService.loadReportTotalProfit(sellerId, startTime, endTime);
        Map<String, Object> willIncome = sellerService.loadReportWillIncome(sellerId, startTime, endTime);
        Map<String, Object> totalSales = sellerService.loadReportTotalSales(sellerId, startTime, endTime);
        o.put("willIncome", Objects.isNull(willIncome.get("willIncome")) ? 0 : willIncome.get("willIncome"));
        o.put("totalSales", Objects.isNull(totalSales.get("totalSales")) ? 0 : totalSales.get("totalSales"));
        o.put("totalProfit", Objects.isNull(totalProfit.get("totalProfit"))? 0: totalProfit.get("totalProfit"));
        o.put("orderNum", Objects.isNull(orderNum.get("orderNum"))? 0:orderNum.get("orderNum") );
        o.put("orderReturns", Objects.isNull(orderReturns.get("orderReturns")) ? 0:orderReturns.get("orderReturns") );
        o.put("orderCancel",  Objects.isNull(orderCancel.get("orderCancel")) ? 0 : orderCancel.get("orderCancel"));

        JSONObject object = new JSONObject();
        object.put("head", o);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 列表
     */
    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        Integer contentType = 0;
        String statusString = request.getParameter("content_type");

        if (StringUtils.isNotEmpty(statusString)) {
            contentType = Integer.valueOf(statusString);
        }

        String sellerId = request.getParameter("sellerId");

        if (StringUtils.isEmptyString(sellerId)) {
            return resultObject;
        }

        Map<String, String> time = DateTypeToTime.convert(DateTypeEnum.fromCode(Integer.valueOf(contentType)));
        PageInfo pageInfo = getPageInfo(request);
        MallPageInfo results = sellerService.loadReportList(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerId, time.get("startTime"), time.get("endTime"));
        List<Map<String, Object>> result = results.getElements();
        List<SellerOrderReportDTO> reportDTOS = new ArrayList<>();
        if (CollectionUtils.isNotEmpty(result)) {

            for (Map<String, Object> dto : result) {

                SellerOrderReportDTO reportDTO = new SellerOrderReportDTO();
                reportDTO.setDayString(String.valueOf(dto.get("dayString")));
                reportDTO.setOrderCancel((Long) dto.get("orderCancel"));
                reportDTO.setOrderNum((Long) dto.get("orderNum"));
                reportDTO.setOrderReturns((Long) dto.get("orderReturns"));
                reportDTO.setTotalProfit((BigDecimal) dto.get("totalProfit"));
                reportDTO.setTotalSales((BigDecimal) dto.get("totalSales"));
                reportDTOS.add(reportDTO);
            }
        }

        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(results.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", reportDTOS);
        resultObject.setData(object);
        return resultObject;
    }

}
