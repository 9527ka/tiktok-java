package project.web.admin.controller.mall;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import ext.Strings;
import ext.Types;
import ext.utils.OsUtils;
import kernel.exception.BusinessException;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.mall.auto.AutoConfig;
import project.mall.orders.model.MallAddress;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.SellerGoods;
import project.mall.goods.dto.GoodSkuAttrDto;
import project.mall.goods.dto.SkuDto;
import project.mall.goods.dto.SkuAttrDto;
import project.syspara.SysparaService;
import project.web.admin.controller.vo.BatchOrderReq;
import project.web.admin.controller.vo.ItemReq;
import project.web.admin.controller.vo.OrderReq;
import project.web.admin.controller.vo.OrderTaskVo;
import project.web.admin.service.mall.PosService;
import security.internal.SecUserService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @BelongsProject: code
 * @BelongsPackage: project.web.admin.controller.mall
 * @Author: tangpeng
 * @CreateTime: 2024-11-04  19:16
 * @Description: TODO
 * @Version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/mall/pos/")
public class PosController  extends PageActionSupport {


    @Autowired
    private PosService posService;

    @Autowired
    protected SecUserService secUserService;

    @Autowired
    private SysparaService sysparaService;

    @Autowired(required = false)
    private GoodsSkuAtrributionService goodsSkuAtrributionService;

    @Autowired(required = false)
    private SellerGoodsService sellerGoodsService;

    /**
     * POS下单: 查询某商品的规格/颜色(SKU)可选项, 供下单弹窗选择
     * 返回 [{skuId, label, price}], label = 各规格/颜色值拼接
     */
    @RequestMapping("sku_list.action")
    @ResponseBody
    public String skuList(HttpServletRequest request) {
        String goodsId = request.getParameter("goodsId");
        try {
            if (!Strings.isNullOrEmpty(goodsId) && sellerGoodsService != null && goodsSkuAtrributionService != null) {
                SellerGoods sg = sellerGoodsService.getSellerGoods(goodsId);
                if (sg != null) {
                    // 规格/颜色名称国际化: 库里 LANG 为 cn/en/tw (非 en_US)。
                    // 按 cn->en->tw 顺序取第一个能查出非空名称的语言, 否则名称为空会回退显示 skuId。
                    GoodSkuAttrDto dto = null;
                    for (String lang : new String[]{"cn", "en", "tw"}) {
                        GoodSkuAttrDto d = goodsSkuAtrributionService.getGoodsAttrListSkuBySellerGoods(sg, lang);
                        if (dto == null) {
                            dto = d; // 兜底: 即使名称为空也至少保留 sku 列表
                        }
                        boolean hasName = false;
                        if (d != null && d.getSkus() != null) {
                            for (SkuDto s : d.getSkus()) {
                                if (s.getAttrs() != null) {
                                    for (SkuAttrDto a : s.getAttrs()) {
                                        if (a.getAttrValueName() != null && !a.getAttrValueName().isEmpty()) {
                                            hasName = true;
                                            break;
                                        }
                                    }
                                }
                                if (hasName) break;
                            }
                        }
                        if (hasName) {
                            dto = d;
                            break;
                        }
                    }
                    if (dto != null) {
                        // 返回完整结构(goodAttrs + skus + skuImg), 前端按属性分开渲染选择器(颜色/尺码), 选完组合匹配 skuId
                        return JSONUtil.toJsonStr(dto);
                    }
                }
            }
        } catch (Exception e) {
            log.error("POS sku_list 查询失败: {}", e.getMessage());
        }
        return "{}";
    }

    /**
     * POS下单商品查询
     */
    @RequestMapping("list.action")
    public ModelAndView list(HttpServletRequest request) {

        if(OsUtils.detectPort("localhost",8011)){
            return this.v3PosList(request);
        }


        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        String goodsName = request.getParameter("goodsName");
        String goodsId = request.getParameter("goodsId");
        String sellerId = request.getParameter("sellerId");
        String sellerName = request.getParameter("sellerName");


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pos_list");

        try {

            this.checkAndSetPageNo(pageNo);
            this.setPageSize(Integer.parseInt(StrUtil.isBlank(pageSize)?"15":pageSize));
            this.page = posService.pagedQuery(this.pageNo,this.pageSize,goodsName,goodsId,sellerId,sellerName);
            logger.info("pos list page = {}", this.page);

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
        modelAndView.addObject("goodsName", goodsName);
        modelAndView.addObject("goodsId", goodsId);
        modelAndView.addObject("sellerId", sellerId);
        modelAndView.addObject("sellerName", sellerName);
        return modelAndView;
    }

    /**
     * V3 Pos
     * @param request
     * @return
     */
    private ModelAndView v3PosList(HttpServletRequest request) {
        String host = request.getHeader("HOST");
        ModelAndView modelAndView = new ModelAndView();
        String roles = Strings.join( this.readSecurityContextFromSession().getRoles(),",");
        String accessToken = this.getUsername_login()+ ":"+roles;
        String url = String.format("tks/index.html?token=ag001&accessToken=%s#/shop-cart",accessToken);
        if (host.startsWith("localhost")) {
            modelAndView.setViewName("redirect:http://localhost:8011/" + url);
        } else {
            String prefix = AutoConfig.getBaseUrl();
            if (!prefix.endsWith("/")){
                prefix = prefix + "/";
            }
            modelAndView.setViewName("redirect:" + prefix + url);
        }
        return modelAndView;
    }

    /**
     * POS下单记录查询
     */
    @RequestMapping("historyList.action")
    public ModelAndView historyList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        String sellerName = request.getParameter("sellerName");


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("pos_history_list");

        try {
            String agentPartyId = getLoginPartyId();
            this.checkAndSetPageNo(pageNo);
            this.setPageSize(Integer.parseInt(StrUtil.isBlank(pageSize)?"20":pageSize));
            logger.info("history pos list page = {}", this.page);
            this.page = posService.historyPagedQuery(this.pageNo,this.pageSize, agentPartyId,sellerName);

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
        modelAndView.addObject("sellerName", sellerName);
        return modelAndView;
    }

    /**
     * POS下单记录查询
     */
    @RequestMapping("history!toDelete.action")
    public ModelAndView deleteHistory(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        String id = request.getParameter("id");
        String reason = request.getParameter("reason");


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + "mall/pos/historyList.action");

        try {
            if(!isRolesAccessible("ROLE_ROOT,ROLE_ADMIN")){
                throw new RuntimeException("没有权限删除POS下单记录");
            }
            posService.deleteHistory(id, reason);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("pageSize", pageSize);
        return modelAndView;
    }

    /**
     * 修改POS下单记录(改数量+金额, 按差额补退补扣)
     */
    @RequestMapping("history!update.action")
    public ModelAndView updateHistory(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String pageSize = request.getParameter("pageSize");
        String id = request.getParameter("id");
        String count = request.getParameter("count");
        String amount = request.getParameter("amount");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + "mall/pos/historyList.action");

        try {
            if(!isRolesAccessible("ROLE_ROOT,ROLE_ADMIN")){
                throw new RuntimeException("没有权限修改POS下单记录");
            }
            Integer cnt = Strings.isNullOrEmpty(count) ? null : Integer.valueOf(count.trim());
            BigDecimal amt = Strings.isNullOrEmpty(amount) ? null : new BigDecimal(amount.trim());
            posService.updateOrderTask(id, cnt, amt);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("pageSize", pageSize);
        return modelAndView;
    }


    /**
     * POS下单买家查询
     */
    @RequestMapping("userList.action")
    @ResponseBody
    public ResultObject getPosUserList(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        List<MallAddress> posUserList = posService.getPosUserList();
        resultObject.setData(posUserList);
        return resultObject;
    }

    /**
     * 查询用户地址
     */
    @RequestMapping("address.action")
    @ResponseBody
    public ResultObject getAddressByPartyId(HttpServletRequest request,@RequestParam String partyId) {
        ResultObject resultObject = new ResultObject();
        List<MallAddress> addressList = posService.getAddressByPartyId(partyId);
        resultObject.setData(addressList);
        return resultObject;
    }

    /**
     * 查询用户地址详情
     */
    @RequestMapping("address_info.action")
    @ResponseBody
    public ResultObject getAddressById(HttpServletRequest request, @RequestParam String id) {
        ResultObject resultObject = new ResultObject();
        MallAddress address = posService.getAddressById(id);
        resultObject.setData(address);
        return resultObject;
    }

    /**
     * 批量退货: 对"超过48小时仍未采购"的POS订单批量退款退销量, 并给对应卖家发系统信息提醒
     */
    /**
     * 预览: 返回"超过48小时仍未采购"将被批量退货的POS订单(供确认弹窗展示)
     */
    @RequestMapping("history!previewTimeoutRefund.action")
    @ResponseBody
    public String previewTimeoutRefund(HttpServletRequest request) {
        try {
            if (!isRolesAccessible("ROLE_ROOT,ROLE_ADMIN")) {
                return "[]";
            }
            return JSONUtil.toJsonStr(posService.listTimeoutRefundPreview());
        } catch (Throwable t) {
            log.error(" previewTimeoutRefund error ", t);
            return "[]";
        }
    }

    @RequestMapping("history!batchRefundTimeout.action")
    public ModelAndView batchRefundTimeout(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + "mall/pos/historyList.action");
        try {
            if (!isRolesAccessible("ROLE_ROOT,ROLE_ADMIN")) {
                throw new RuntimeException("没有权限");
            }
            int n = posService.batchRefundTimeout();
            modelAndView.addObject("message", "批量退货完成, 共退 " + n + " 单(超48小时未采购), 已通知对应卖家");
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
        } catch (Throwable t) {
            logger.error(" batchRefundTimeout error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
        }
        return modelAndView;
    }

    /**
     * 创建任务
     */
    @RequestMapping("create_task.action")
    @ResponseBody
    public String createTask(HttpServletRequest request, @RequestBody OrderTaskVo orderTaskVo) {
       String response;
        Integer orderMode = orderTaskVo.getOrderMode();
        String partyId = orderTaskVo.getPartyId();
        List<ItemReq> orderItems = orderTaskVo.getOrder();
        String datePicker = orderTaskVo.getDatePicker();
        // 实时单: 下单前记录该买家最后一单时间, 作为本次新订单的分界点(避免关联到之前的订单)
        String beforeMark = null;
        if (null != orderMode && 1 == orderMode) {
            beforeMark = posService.maxOrderTime(partyId);
        }
        //实时
        if(1==orderMode){
            //发请求
            OrderReq orderReq = new OrderReq();
            orderReq.setSecret("test");
            orderReq.setItems(orderItems);
            orderReq.setCreateUser(orderTaskVo.getCreateUser());
            orderReq.setPartyId(partyId);
            response = HttpUtil.post("http://127.0.0.1:8010/api/item/order/single", JSONUtil.toJsonStr(orderReq));

        }else {
            BatchOrderReq batchOrderReq = new BatchOrderReq();
            Integer orderCount = orderTaskVo.getOrderCount();
            batchOrderReq.setTotal_count(null==orderCount?orderItems.size():orderCount);
            Date date = DateUtil.parse(datePicker, "yyyy-MM-dd'T'HH:mm");
            batchOrderReq.setCreateUser(orderTaskVo.getCreateUser());
            batchOrderReq.setDatetime(new String[]{DateUtil.formatDateTime(date),DateUtil.offsetMinute(date,orderCount).toString()});
            orderItems.sort(Comparator.comparing(ItemReq::getPrice));
            batchOrderReq.setPrice_limit(new BigDecimal[]{orderItems.get(0).getPrice(),orderItems.get(orderItems.size()-1).getPrice()});
            batchOrderReq.setItems(orderItems.stream().map(ItemReq::getItemId).collect(Collectors.toList()));
            response = HttpUtil.post("http://127.0.0.1:8010/api/item/order", JSONUtil.toJsonStr(batchOrderReq));
        }

        // 记录 POS 日志到 t_mall_order_task (供 admin 历史列表展示)
        try {
            String goodInfo = orderItems.stream().map(ItemReq::getItemId).collect(Collectors.joining(","));
            if (goodInfo.length() > 120) {
                goodInfo = goodInfo.substring(0, 120);
            }
            int totalCount = orderItems.stream().mapToInt(i -> i.getCount() == null ? 1 : i.getCount()).sum();
            BigDecimal amount = orderItems.stream()
                    .map(i -> (i.getPrice() == null ? BigDecimal.ZERO : i.getPrice())
                            .multiply(BigDecimal.valueOf(i.getCount() == null ? 1 : i.getCount())))
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            // 解析 mall-tools 返回的 orderId (如有)
            String orderId = null;
            try {
                cn.hutool.json.JSONObject json = JSONUtil.parseObj(response);
                if (json != null && json.containsKey("data")) {
                    Object data = json.get("data");
                    if (data instanceof cn.hutool.json.JSONObject) {
                        Object oid = ((cn.hutool.json.JSONObject) data).get("orderId");
                        if (oid == null) oid = ((cn.hutool.json.JSONObject) data).get("id");
                        if (oid != null) orderId = oid.toString();
                    } else if (data != null) {
                        orderId = data.toString();
                    }
                }
            } catch (Exception ignore) {}
            // 实时单: 8010 返回通常无订单号, 回查该买家刚生成的真实订单 UUID 建立关联(供删除时软删真实订单)
            if (Strings.isNullOrEmpty(orderId) && null != orderMode && 1 == orderMode) {
                orderId = posService.findRecentOrderIds(partyId, beforeMark);
            }
            posService.saveOrderTaskLog(partyId, null, goodInfo, totalCount, amount, 1, orderId);
        } catch (Exception ex) {
            log.error("POS 日志写入失败: {}", ex.getMessage());
        }

        return response;
    }


}
