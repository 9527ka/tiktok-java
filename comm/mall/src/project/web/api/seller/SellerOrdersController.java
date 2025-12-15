package project.web.api.seller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.crypto.digest.MD5;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.concurrent.ConcurrentQequestHandleStrategy;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.Constants;
import project.log.MoneyLog;
import project.log.MoneyLogService;
import project.mall.MallRedisKeys;
import project.mall.event.message.PurchaseOrderGoodsEvent;
import project.mall.event.model.PurchaseOrderInfo;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersGoods;
import project.mall.orders.model.MallOrdersPrize;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.tip.TipService;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商户后台-订单
 */
@RestController
@CrossOrigin
public class SellerOrdersController extends BaseAction {
    private final String action = "/seller/orders!";

    private Logger logger = LogManager.getLogger(this.getClass().getName());
    @Resource
    protected RedisHandler redisHandler;
    @Resource
    private GoodsOrdersService goodsOrdersService;
    @Resource
    private MoneyLogService moneyLogService;
    @Resource
    private SysparaService sysparaService;
    @Resource
    private PartyService partyService;
    @Resource
    private TipService tipService;

    /**
     * 列表
     */
    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String status = request.getParameter("status");
        String orderId = request.getParameter("orderId");
        String payStatus = request.getParameter("payStatus");
        String purchStatus = request.getParameter("purchStatus");
        String begin = request.getParameter("begin");
        String end = request.getParameter("end");

        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        JSONArray jsonArray = new JSONArray();

        MallPageInfo mallPageInfo = goodsOrdersService.listSellerOrdersInfo(partyId, status, orderId, payStatus, purchStatus, begin, end, pageInfo.getPageNum(), pageInfo.getPageSize());

        List<MallOrdersPrize> mallOrdersPrizes = mallPageInfo.getElements();

        for (MallOrdersPrize address : mallOrdersPrizes) {
            JSONObject o = new JSONObject();
            o.put("id", address.getId().toString());
            o.put("contacts", address.getContacts());
            o.put("prizeReal", address.getPrizeReal());
            o.put("profit", address.getProfit());
            o.put("purchStatus", address.getPurchStatus());
            o.put("payStatus", payStatus != null && Integer.parseInt(payStatus) == -1 ? address.getStatus() : address.getPayStatus());
            o.put("status", address.getStatus());
            o.put("createTime", DateUtils.getLongDate(address.getCreateTime()));
            o.put("purchTime",Objects.isNull(address.getPurchTime())?"":DateUtils.getLongDate(address.getPurchTime()));
            Party party = this.partyService.cachePartyBy(address.getPartyId(), false);
            o.put("partyId", address.getPartyId());
            o.put("username", Objects.nonNull(party) ? party.getUsername() : "");
            // totalCost = profit + fee + tax
            List<MallOrdersGoods> mallOrdersGoods = goodsOrdersService.listMallOrdersGoods(String.valueOf(address.getId()), 1, 100);

            double totalCost = 0;
            for (MallOrdersGoods mallOrdersGood : mallOrdersGoods) {
                totalCost += mallOrdersGood.getGoodsNum() * mallOrdersGood.getSystemPrice();
            }
            o.put("totalCost", totalCost);
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();

        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }


    /**
     * 详情
     */
    @PostMapping(action + "details-returns.action")
    public Object detailsReturns(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String orderId = request.getParameter("orderId");
        MallOrdersPrize mallOrdersPrize = goodsOrdersService.getMallOrdersPrize(orderId);
        JSONObject o = new JSONObject();
        o.put("refundTime", DateUtils.getLongDate(mallOrdersPrize.getRefundTime()));
        o.put("id", mallOrdersPrize.getId().toString());
        o.put("returnPrice", Arith.roundDown(mallOrdersPrize.getFees() + mallOrdersPrize.getTax() + mallOrdersPrize.getPrizeReal(), 2));
        o.put("returnStatus", mallOrdersPrize.getReturnStatus());
        o.put("returnReason", mallOrdersPrize.getReturnReason());
        o.put("returnDetail", mallOrdersPrize.getReturnDetail());
        Party party = this.partyService.cachePartyBy(mallOrdersPrize.getPartyId(), false);
        o.put("partyId", mallOrdersPrize.getPartyId());
        o.put("username", party.getUsername());
        resultObject.setData(o);
        return resultObject;
    }

    /**
     * 详情
     */
    @PostMapping(action + "details.action")
    public Object details(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        String orderId = request.getParameter("orderId");
        MallOrdersPrize mallOrdersPrize = goodsOrdersService.getMallOrdersPrize(orderId);
        if (!partyId.equalsIgnoreCase(mallOrdersPrize.getSellerId())) {
            resultObject.setCode("1");
            resultObject.setMsg("商家只允许查看自己的订单");
            return resultObject;
        }

        JSONObject o = new JSONObject();
        // 订单号
        o.put("id", mallOrdersPrize.getId().toString());
        // 下单时间
        o.put("createTime", DateUtils.getLongDate(mallOrdersPrize.getCreateTime()));
        // 采购时间
        String log = "采购订单[" + orderId + "]";
        List<MoneyLog> byLogs = moneyLogService.findByLog(Constants.MONEYLOG_CONTNET_PUSH_ORDER, log);
        if (CollectionUtil.isEmpty(byLogs)) {
            Date date = DateUtil.date(mallOrdersPrize.getUpTime()).toJdkDate();
            o.put("pushTime", DateUtils.getLongDate(date));
        } else {
            o.put("pushTime", DateUtils.getLongDate(byLogs.get(0).getCreateTime()));
        }
        o.put("returnPrice", Arith.roundDown(mallOrdersPrize.getFees() + mallOrdersPrize.getTax() + mallOrdersPrize.getPrizeReal(), 2));
        // 物流状态
        o.put("status", mallOrdersPrize.getPayStatus());
        // 采购金额
        o.put("systemPrice", mallOrdersPrize.getSystemPrice());
        // 销售金额
        o.put("prizeReal", mallOrdersPrize.getPrizeReal());
        // 收货地址
        o.put("address", mallOrdersPrize.getAddress());
        // 姓名
        o.put("contacts", mallOrdersPrize.getContacts());
        // 邮箱
        o.put("email", mallOrdersPrize.getEmail());
        // 电话
        o.put("phone", mallOrdersPrize.getPhone());
        // 国家
        o.put("country", mallOrdersPrize.getCountry());
        // 州
        o.put("province", mallOrdersPrize.getProvince());
        // 城市
        o.put("city", mallOrdersPrize.getCity());
        // 邮编
        o.put("postcode", mallOrdersPrize.getPostcode());
        Party party = this.partyService.cachePartyBy(mallOrdersPrize.getPartyId(), false);
        o.put("partyId", mallOrdersPrize.getPartyId());
        o.put("username", null == party ? "" : party.getUsername());
        resultObject.setData(o);
        return resultObject;
    }

    /**
     *
     */
    @PostMapping(action + "list-returns.action")
    public Object listReturns(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String returnStatus = request.getParameter("returnStatus");
        String begin = request.getParameter("begin");
        String end = request.getParameter("end");

        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        JSONArray jsonArray = new JSONArray();

        MallPageInfo mallPageInfo = goodsOrdersService.listSellerReturns(partyId, returnStatus, begin, end, pageInfo.getPageNum(), pageInfo.getPageSize());

        List<MallOrdersPrize> list = mallPageInfo.getElements();

        for (MallOrdersPrize address : list) {
            JSONObject o = new JSONObject();
            o.put("refundTime", Objects.isNull(address.getRefundTime()) ? "" : DateUtils.getLongDate(address.getRefundTime()));
            o.put("id", address.getId().toString());
            o.put("createTime", Objects.isNull(address.getCreateTime()) ? "" : DateUtils.getLongDate(address.getCreateTime()));
            o.put("returnPrice", Arith.roundDown(address.getFees() + address.getTax() + address.getPrizeReal(), 2));
            o.put("returnStatus", address.getReturnStatus());
            o.put("returnReason", address.getReturnReason());
            o.put("returnDetail", address.getReturnDetail());
            Party party = this.partyService.cachePartyBy(address.getPartyId(), false);
            o.put("partyId", address.getPartyId());
            o.put("username", party.getUsername());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 采购
     *
     * @return
     */
    @PostMapping(action + "push.action")
    public Object push(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String orderId = request.getParameter("orderId");
        String partyId = this.getLoginPartyId();

        String safeword = request.getParameter("safeword");

        // 处理并发请求
        String requestKey = DigestUtil.md5Hex(orderId);
        checkConcurrentRequest(redisHandler, requestKey, 1, ConcurrentQequestHandleStrategy.SLEEP_THEN_RETURN);

        if (StringUtils.isEmptyString(safeword)) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码不能为空");
            return resultObject;
        }

        if (safeword.length() < 6 || safeword.length() > 12) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码必须6-12位");
            return resultObject;
        }
        Party party = partyService.cachePartyBy(partyId, false);

        String partySafeword = party.getSafeword();
        if (StringUtils.isEmptyString(partySafeword)) {
            resultObject.setCode("1");
            resultObject.setMsg("请设置资金密码");
            return resultObject;
        }

        if (!party.getEnabled()) {
            resultObject.setCode("1");
            resultObject.setMsg("业务已锁定，请联系客服！");
            return resultObject;
        }

        /*if (!this.partyService.checkSafeword(safeword, partyId)) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码错误");
            return resultObject;
        }*/

        try {
//            设置资金密码校验次数限制，根据系统参数 number_of_wrong_passwords 来变化
            String errorPassCount = sysparaService.find("number_of_wrong_passwords").getValue();
            if (Objects.isNull(errorPassCount)) {
                logger.error("number_of_wrong_passwords 系统参数未配置！");
                throw new BusinessException("参数异常");
            }
            String lockPassworkErrorKey = MallRedisKeys.MALL_PASSWORD_ERROR_LOCK + partyId;
            int needSeconds = util.DateUtils.getTomorrowStartSeconds();
            boolean exit = redisHandler.exists(lockPassworkErrorKey);//是否已经错误过
            if (exit && ("true".equals(redisHandler.getString(lockPassworkErrorKey)))) {//已经尝试错误过且次数已经超过number_of_wrong_passwords配置的次数
                throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
            } else if (exit && errorPassCount.equals(redisHandler.getString(lockPassworkErrorKey))) {//已经尝试密码错误过且次数刚好等于number_of_wrong_passwords配置的次数
                redisHandler.setSyncStringEx(lockPassworkErrorKey, "true", needSeconds);
                throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
            } else {//失败次数小于配置次数或者未失败
                boolean checkSafeWord = this.partyService.checkSafeword(safeword, partyId);
                if (checkSafeWord) {//交易密码校验成功
                    redisHandler.remove(lockPassworkErrorKey);
                } else {//交易密码校验失败
                    if (exit) {//已经失败过，执行加1操作
                        redisHandler.incr(lockPassworkErrorKey);
                    } else {//未失败，set值，并计1
                        redisHandler.setSyncStringEx(lockPassworkErrorKey, "1", needSeconds);
                    }
                    throw new BusinessException(1, "资金密码错误");
                }
            }

            List<String> orderIdList = new ArrayList<>();
            if (StrUtil.isNotBlank(orderId)) {
                String[] orderIdArr = orderId.split(",");
                for (String oneOrderId : orderIdArr) {
                    oneOrderId = oneOrderId.trim();
                    if (StrUtil.isBlank(oneOrderId)) {
                        continue;
                    }
                    if (orderIdList.contains(oneOrderId)) {
                        continue;
                    }
                    orderIdList.add(oneOrderId);
                }
            }

            for (String oneOrderId : orderIdList) {
                goodsOrdersService.updatePushOrders(partyId, oneOrderId);

                // 注意：管理后台 pos下单（虚假单），不用提醒
                // 管理后台处理发货，不再这里移除提醒记录
//                // 订单采购成功了，就移除之前产生的订单未发货提醒记录
//                try {
//                    tipService.deleteTip(oneOrderId);
//                } catch (Exception e) {
//                    logger.error("SellerOrdersController push 移除订单:{} 的未发货提醒记录失败", oneOrderId);
//                }

                // 发布一个采购订单商品的事件
                WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
                PurchaseOrderInfo info = new PurchaseOrderInfo();
                info.setOrderId(orderId);
                info.setTraceId(UUID.randomUUID().toString());
                wac.publishEvent(new PurchaseOrderGoodsEvent(this, info));
            }
        } catch (BusinessException e) {
            logger.error("采购业务异常", e);
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            logger.error("采购未知异常", e1);
            resultObject.setCode("1");
            resultObject.setMsg("采购失败");
        }
        return resultObject;
    }


    /**
     * 统计未采购数量
     */
    @PostMapping(action + "noPushNum.action")
    public Object noPushNum() {

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());

        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = this.getLoginPartyId();

        Map<String, Object> results = goodsOrdersService.selectNoPushNum(partyId);

        JSONObject object = new JSONObject();

        object.put("noPushNum", results.get("noPushNum"));
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 手动释放佣金
     */
    @PostMapping(action + "manualProfileOrder.action")
    public Object profileOrder(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String orderId = request.getParameter("orderId");
        goodsOrdersService.updateAutoProfit(orderId);
        JSONObject object = new JSONObject();
        object.put("result","success");
        resultObject.setData(object);
        return resultObject;
    }
}
