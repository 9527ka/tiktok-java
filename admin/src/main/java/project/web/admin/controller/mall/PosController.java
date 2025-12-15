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
import project.syspara.SysparaService;
import project.web.admin.controller.vo.BatchOrderReq;
import project.web.admin.controller.vo.ItemReq;
import project.web.admin.controller.vo.OrderReq;
import project.web.admin.controller.vo.OrderTaskVo;
import project.web.admin.service.mall.PosService;
import security.internal.SecUserService;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
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


        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + "mall/pos/historyList.action");

        try {
            if(!isRolesAccessible("ROLE_ROOT,ROLE_ADMIN")){
                throw new RuntimeException("没有权限删除POS下单记录");
            }
            posService.deleteHistory(id);
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
        //实时
        if(1==orderMode){
            //发请求
            OrderReq orderReq = new OrderReq();
            orderReq.setSecret("test");
            orderReq.setItems(orderItems);
            orderReq.setCreateUser(orderTaskVo.getCreateUser());
            orderReq.setPartyId(partyId);
            response = HttpUtil.post("http://127.0.0.1:8010/api/item/order/single", JSONUtil.toJsonStr(orderReq));
            //插入task

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

        return response;
    }


}
