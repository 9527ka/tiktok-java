package project.web.admin.order;

import cn.hutool.core.collection.CollectionUtil;
import ext.Result;
import ext.Strings;
import ext.TypeConv;
import ext.Types;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.log.LogService;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.model.GoodsAttributeVo;
import project.mall.orders.AdminMallOrderService;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersPrize;
import project.tip.TipConstants;
import project.tip.TipService;
import security.SecUser;
import security.internal.SecUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商城订单
 */
@RestController
@RequestMapping("/mall/order")
@Slf4j
public class AdminMallOrderController extends PageActionSupport {
    private static final Logger logger = LoggerFactory.getLogger(AdminMallOrderController.class);

    @Resource
    protected AdminMallOrderService adminMallOrderService;

    @Resource
    protected GoodsOrdersService goodsOrdersService;

    @Resource
    protected LogService logService;

    @Resource
    protected SecUserService secUserService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected GoodsSkuAtrributionService goodsSkuAtrributionService;

    @Resource
    private TipService tipService;

    /**
     *
     * 列表查询
     */
    @RequestMapping(value = "/list.action")
    public ModelAndView list(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String id = request.getParameter("id");
        String contacts = request.getParameter("contacts");
        String sellerName = request.getParameter("sellerName");
        String phone = request.getParameter("phone");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String orderStatus = request.getParameter("orderStatus");
        String purchTimeOutStatus = request.getParameter("purchTimeOutStatus");
        String isDelete = request.getParameter("isDelete");
        String sellerRoleName = request.getParameter("sellerRoleName");
        String userCode = request.getParameter("userCode");
        String sellerCode = request.getParameter("sellerCode");
        Integer payStatus;
        Integer status;

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_order_list");
        try {
            payStatus = request.getParameter("payStatus") == null ? -2 : Integer.parseInt(request.getParameter("payStatus"));
            status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
        }catch (Exception e){
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        }


        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.page = this.adminMallOrderService.pagedQuery(this.pageNo, this.pageSize,id,contacts,getLoginPartyId(),
                    sellerName,phone,payStatus,startTime,endTime,orderStatus,status,sellerRoleName,userCode,sellerCode,purchTimeOutStatus,isDelete);
            List<Map> list = page.getElements();
            for (int i = 0; i < list.size(); i++) {
                Map map=list.get(i);
                if (Objects.isNull(map.get("purchTime"))){
                    map.put("purchTime","--");
                }
                if (Objects.isNull(map.get("purchTimeOutTime"))){
                    map.put("purchTimeOutTime","--");
                }

                if (Double.compare((Double) map.get("sellerDiscount"), 0D) != 0) {
                    map.put("systemPrice",Arith.roundDown(Arith.mul((Double) map.get("systemPrice"), 1 - (Double) map.get("sellerDiscount")), 2));
                }
            }
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("pageSize", this.pageSize);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("id", id);
        modelAndView.addObject("phone", phone);
        modelAndView.addObject("contacts", contacts);
        modelAndView.addObject("startTime", startTime);
        modelAndView.addObject("endTime", endTime);
        modelAndView.addObject("status", status);
        modelAndView.addObject("sellerName", sellerName);
        modelAndView.addObject("payStatus", payStatus);
        modelAndView.addObject("orderStatus", orderStatus);
        modelAndView.addObject("sellerRoleName", sellerRoleName);
        modelAndView.addObject("userCode", userCode);
        modelAndView.addObject("sellerCode", sellerCode);
        modelAndView.addObject("purchTimeOutStatus", purchTimeOutStatus);
        modelAndView.addObject("isDelete", isDelete);
        return modelAndView;
    }


    @RequestMapping("/shipment.action")
    public Object sendSiteMsg(@RequestParam("orderNo") String orderNo,HttpServletRequest request) {
        if(request.getMethod().equals("GET")) {
            ModelAndView model = new ModelAndView("admin_order_shipment");
            model.addObject("orderNo", orderNo);
            model.addObject("shipmentProvider","");
            model.addObject("shipmentTradeNo","");
            model.addObject("confirmDays",7);
            return model;
        }
        try{
            String shipmentProvider = request.getParameter("shipmentProvider");
            String shipmentTradeNo = request.getParameter("shipmentTradeNo");
            int confirmDays = TypeConv.toInt(Types.orValue(request.getParameter("confirmDays"),"7"));
            if(Strings.isNullOrEmpty(shipmentProvider) || Strings.isNullOrEmpty(shipmentTradeNo)){
                return Result.error(1,"请输入物流信息");
            }

            adminMallOrderService.updateStatus(new String[]{orderNo},shipmentProvider,shipmentTradeNo,confirmDays);

            // 订单执行了发货处理，就移除之前产生的提醒发货记录
            try {
                tipService.deleteTip(orderNo);
                log.info("------> AdminMallOrderController ship 后台完成发货处理，移除订单:{} 的发货提醒记录", orderNo);
            } catch (Exception e) {
                log.error("AdminMallOrderController ship 移除订单:{} 的发货提醒记录失败", orderNo);
            }
            return Result.success();
        }catch(Throwable ex){
            log.error("shipment order {} error", orderNo, ex);
            return Result.error(1, String.format("shipment order %s error", orderNo));
        }
    }

    /**
     *
     * 订单详情商品查询
     */
    @RequestMapping(value = "/detailsList.action")
    public ModelAndView detailsList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String id = request.getParameter("id");
        String sellerRoleName = request.getParameter("sellerRoleName");
        String purchTime = request.getParameter("purchTime");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("order_details_list");

        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            // 待取代 TODO
            //this.page = this.adminMallOrderService.findGoodsDetailsById1(this.pageNo, this.pageSize, id);
            this.page = this.adminMallOrderService.findGoodsDetailsById(this.pageNo, this.pageSize, id);
            List<Map> list = this.page.getElements();

            for (int i = 0; i < list.size(); i++) {
                Map map = list.get(i);
                Double goodsReal = (Double) map.get("goodsReal");
                int goodsNum = (int) map.get("goodsNum");
                String lang = "en";
                String skuId = (String) map.get("skuId");
                String attrs = "";
                if(StringUtils.isNotEmpty(skuId) && !"-1".equalsIgnoreCase(skuId)){
                    List<GoodsAttributeVo> goodsAttributeVos = goodsSkuAtrributionService.findGoodsAttributeBySkuId(skuId, lang);
                    if(CollectionUtil.isNotEmpty(goodsAttributeVos)){
                        attrs = goodsAttributeVos.stream().map(vo -> vo.getAttrName() + ":" + vo.getAttrValue()).collect(Collectors.joining(","));
                    }
                }
                map.put("attrs", attrs);
                double countAmount = Arith.mul(goodsReal, goodsNum);
                map.put("countAmount", countAmount);
            }

            MallOrdersPrize mallOrdersPrize = this.adminMallOrderService.findById(id);
            double add = Arith.add(Arith.add(mallOrdersPrize.getFees(), mallOrdersPrize.getTax()), mallOrdersPrize.getPrizeReal());
            mallOrdersPrize.setPrizeReal(add);
            modelAndView.addObject("data", mallOrdersPrize);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("pageSize", this.pageSize);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("id", id);
        modelAndView.addObject("sellerRoleName", sellerRoleName);
        modelAndView.addObject("purchTime", purchTime);
        return modelAndView;
    }


    /**
     *
     * 退货订单列表查询
     */
    @RequestMapping(value = "/refundList.action")
    public ModelAndView refundList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String id = request.getParameter("id");
        String userCode = request.getParameter("userCode");
        Integer orderStatus = request.getParameter("orderStatus") == null ? -2 : Integer.parseInt(request.getParameter("orderStatus"));
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        Integer returnStatus = request.getParameter("returnStatus") == null ? -2 : Integer.parseInt(request.getParameter("returnStatus"));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("order_refund_list");
        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.page = this.adminMallOrderService.pagedQueryRefundList(this.pageNo, this.pageSize,id,orderStatus, getLoginPartyId(), userCode, returnStatus, startTime, endTime);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("pageSize", this.pageSize);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("id", id);
        modelAndView.addObject("startTime", startTime);
        modelAndView.addObject("endTime", endTime);
        modelAndView.addObject("userCode", userCode);
        modelAndView.addObject("orderStatus", orderStatus);
        modelAndView.addObject("returnStatus", returnStatus);
        return modelAndView;
    }

    /**
     * 单独发货
     * @param id
     * @return
     */
    @RequestMapping(value = "/ship.action")
    public ModelAndView ship(@RequestParam String id, HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo", pageNo);
        try {
            if (StringUtils.isEmptyString(id)) {
                throw new BusinessException("请选择一个订单");
            }
            MallOrdersPrize order = adminMallOrderService.findById(id);
            if(Objects.isNull(order) || order.getStatus() != 2){
                throw new BusinessException("此订单已被处理，请刷新页面");
            }
            String[] ids = {id};
            adminMallOrderService.updateStatus(ids, "", "", 0);

            // 订单执行了发货处理，就移除之前产生的提醒发货记录
            try {
                tipService.deleteTip(id);
                logger.info("------> AdminMallOrderController ship 后台完成发货处理，移除订单:{} 的发货提醒记录", id);
            } catch (Exception e) {
                logger.error("AdminMallOrderController ship 移除订单:{} 的发货提醒记录失败", id);
            }
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/order/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/order/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/order/list.action");
        return modelAndView;
    }
    /**
     * 批量发货
     * @param ids
     * @return
     */
    @RequestMapping(value = "/shipCol.action")
    public Map shipCol(@RequestParam String[] ids) {

        Map map = new HashMap(2);
        try {
            adminMallOrderService.updateStatus(ids, "", "", 0);

            for (String oneOrderId : ids) {
                // 订单执行了发货处理，就移除之前产生的提醒发货记录
                try {
                    if (oneOrderId.equals("on")){
                        continue;
                    }
                    tipService.deleteTip(oneOrderId);
                    logger.info("------> AdminMallOrderController shipCol 后台完成发货处理，移除订单:{} 的发货提醒记录", oneOrderId);
                } catch (Exception e) {
                    logger.error("AdminMallOrderController shipCol 移除订单:{} 的发货提醒记录失败", oneOrderId);
                }
            }

            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }
    /**
     * 批量释放
     * @param ids
     * @return
     */
    @RequestMapping(value = "/freedCol.action")
    public Map freedCol(@RequestParam String[] ids) {

        Map map = new HashMap(2);
        try {
            adminMallOrderService.updateFreedCol(ids,this.getLoginPartyId());
            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }

    /**
     * 批量取消
     * @param ids
     * @return
     */
    @RequestMapping(value = "/cancelOrder.action")
    public Map cancelOrder(@RequestParam String[] ids) {
        Map map = new HashMap(2);
        try {
            String username_login = getUsername_login();
            List<MallOrdersPrize> effectedOrderList = adminMallOrderService.updateCancelOrder(ids, this.getLoginPartyId(), username_login);
            Map<String, MallOrdersPrize> orderMap = new HashMap<>();
            for (MallOrdersPrize oneOrder : effectedOrderList) {
                orderMap.put(oneOrder.getId().toString(), oneOrder);
            }

            for (String oneOrderId : ids) {
                // 订单执行了发货处理，就移除之前产生的提醒发货记录
                try {
                    if (oneOrderId.equals("on")){
                        continue;
                    }

                    MallOrdersPrize currentOrder = orderMap.get(oneOrderId);
                    if (currentOrder == null) {
                        continue;
                    }
                    if (currentOrder.getFlag() == 0 || currentOrder.getFlag() == 1) {
                        // 买家和商家都是真实的，或者，买家是真实的卖家是演示的
                        tipService.saveTip(oneOrderId, TipConstants.GOODS_ORDER_RETURN);
                    }
                } catch (Exception e) {
                    logger.error("AdminMallOrderController cancelOrder 添加订单:{} 的发货提醒记录失败", oneOrderId);
                }
            }

            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }


    @RequestMapping(value = "/getloginPartyId.action")
    public Map getloginPartyId() {

        Map map = new HashMap(3);
        try {
            map.put("code", 200);
            map.put("loginPartyId", this.getLoginPartyId());
            map.put("loginName", this.getUsername_login());
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }

    /**
     * 批量确认收货
     * @param ids
     * @return
     */
    @RequestMapping(value = "/receiptCol.action")
    public Map receiptCol(@RequestParam String[] ids) {

        Map map = new HashMap(2);
        try {
            adminMallOrderService.updateReceiptCol(ids);
            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }

    /**
     * 批量设置手动收货
     * @param ids
     * @return
     */
    @RequestMapping(value = "/manualReceipt.action")
    public Map manualReceipt(@RequestParam String[] ids) {

        Map map = new HashMap(2);
        try {
            adminMallOrderService.updateManualReceipt(ids);
            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }

    /**
     * 批量设置手动发货
     * @param ids
     * @return
     */
    @RequestMapping(value = "/manualShip.action")
    public Map manualShip(@RequestParam String[] ids) {

        Map map = new HashMap(2);
        try {
            adminMallOrderService.updateManualShip(ids);
            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }
    /**
     * 批量删除订单
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteOrders.action")
    public Map deleteOrders(@RequestParam String[] ids, int type) {

        Map map = new HashMap(2);
        try {
            adminMallOrderService.deleteOrders(ids,type);
            map.put("message","操作成功");
            map.put("code", 200);
            return map;
        } catch (BusinessException e) {
            map.put("code", 500);
            map.put("error", e.getMessage());
            return map;
        } catch (Throwable t) {
            logger.error(" error ", t);
            map.put("code", 500);
            map.put("error", t.getMessage());
            return map;
        }
    }

    /**
     * 拒绝订单退款申请
     */
    @RequestMapping("/reject.action")
    public ModelAndView reject(HttpServletRequest request) {
        String id = request.getParameter("orderIds");
        String failure_msg = request.getParameter("failure_msg");
        String pageNo = request.getParameter("pageNo");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageNo",pageNo);
        modelAndView.setViewName("redirect:/"  + "mall/order/refundList.action");

        try {

            MallOrdersPrize order = adminMallOrderService.findById(id);

            goodsOrdersService.updateReturnsOrdersByAdmin(id, false,failure_msg);

            SecUser sec = this.secUserService.findUserByPartyId(order.getPartyId());
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setExtra(order.getId().toString());
            log.setUsername(sec.getUsername());
            log.setOperator(username_login);
            log.setPartyId(order.getPartyId());
            log.setLog("管理员驳回一笔退款订单。订单号[" + id + "]，驳回理由[" + failure_msg + "]。");
            logService.saveSync(log);

            // 退货申请处理成功了，就移除之前产生的退货处理提醒记录
            try {
                tipService.deleteTip(id);
            } catch (Exception e) {
                logger.error("AdminMallOrderController reject 移除订单:{} 的退货处理提醒记录失败", id);
            }
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error("update error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }

        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }

    /**
     * 确认订单退款申请
     */
    @RequestMapping("/refund.action")
    public ModelAndView refund(HttpServletRequest request) {
        String id = request.getParameter("orderId");
        String safeword = request.getParameter("safeword");
        String pageNo = request.getParameter("pageNo");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageNo",pageNo);
        modelAndView.setViewName("redirect:/"  + "mall/order/refundList.action");

        try {
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec,this.getUsername_login(), safeword);

            MallOrdersPrize order = adminMallOrderService.findById(id);
            goodsOrdersService.updateReturnsOrdersByAdmin(id, true,null);

            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setExtra(order.getId().toString());
            log.setUsername(sec.getUsername());
            log.setOperator(username_login);
            log.setPartyId(order.getPartyId());
            log.setLog("管理员确认一笔退款订单。订单号[" + id + "]");
            logService.saveSync(log);

            // 退货申请处理成功了，就移除之前产生的退货处理提醒记录
            try {
                tipService.deleteTip(id);
                logger.info("------> AdminMallOrderController refund 后台完成退款处理，移除订单:{} 的退货提醒记录", id);
            } catch (Exception e) {
                logger.error("AdminMallOrderController refund 移除订单:{} 的退货处理提醒记录失败", id);
            }
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error("update error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }

        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }


    /**
     * 验证登录人资金密码
     * @param operatorUsername
     * @param loginSafeword
     */
    protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
        String sysSafeword = secUser.getSafeword();
        String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("登录人资金密码错误");
        }
    }
}