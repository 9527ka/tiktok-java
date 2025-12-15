package project.web.admin.seller;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.item.model.Item;
import project.log.LogService;
import project.log.MoneyFreeze;
import project.log.MoneyFreezeService;
import project.log.MoneyLogService;
import project.mall.auto.AutoConfig;
import project.mall.goods.SellerGoodsService;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.seller.*;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.model.*;
import project.mall.utils.PlatformNameEnum;
import project.party.PartyService;
import project.party.UserMetricsService;
import project.party.model.Party;
import project.party.model.UserMetrics;
import project.party.recom.UserRecomService;
import project.redis.RedisHandler;
import project.syspara.SysparaService;
import project.tip.TipService;
import project.user.UserRedisKeys;
import project.user.token.Token;
import project.user.token.TokenService;
import project.wallet.WalletService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

;

/**
 * 店铺管理
 */
@Slf4j
@RestController
@RequestMapping("/mall/seller")
public class AdminSellersController extends PageActionSupport {
    private static Log logger = LogFactory.getLog(AdminSellersController.class);

    @Resource
    protected AdminSellerService adminSellerService;
    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerService sellerService;
    @Autowired
    protected SellerCreditService sellerCreditService;
    @Autowired
    protected WalletService walletService;
    @Autowired
    protected MoneyLogService moneyLogService;
    @Autowired
    protected MoneyFreezeService moneyFreezeService;
    @Autowired
    protected SecUserService secUserService;
//    @Autowired
//    protected CommonNotifyService commonNotifyService;
    @Autowired
    protected NotificationHelperClient notificationHelperClient;

    @Resource
    protected FocusSellerService focusSellerService;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    //protected CommonNotifyManager inboxNotifyManager;

    @Resource
    protected LogService logService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected MallLevelService mallLevelService;

    @Autowired
    private PartyService partyService;

    @Resource
    private SysparaService sysparaService;

    @Resource
    protected UserRecomService userRecomService;

    @Resource
    protected UserMetricsService userMetricsService;

    @Resource
    protected ComplaintService complaintService ;

    @Resource
    protected TipService tipService;

    @Autowired
    private TokenService tokenService;

    /**
     * 查询投诉列表
     * @param request
     * @return
     */
    @RequestMapping("/compliants.action")
    public ModelAndView listCompliants(HttpServletRequest request) {

        this.pageSize = 15;
        String userCode = request.getParameter("userCode");
        String storeCode = request.getParameter("storeCode");
        String storeName = request.getParameter("storeName");
        String auditStatus = request.getParameter("auditStatus");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        ModelAndView model = new ModelAndView("admin_complaint_list");

        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = complaintService.pagedQuery(this.pageNo, this.pageSize, userCode, storeCode,
                    storeName, auditStatus, startTime, endTime);
            model.addObject("page",this.page);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("userCode",userCode);
        model.addObject("storeCode",storeCode);
        model.addObject("storeName",storeName);
        model.addObject("auditStatus",auditStatus);
        model.addObject("startTime",startTime);
        model.addObject("endTime",endTime);
        model.addObject("message",message);
        model.addObject("error",error);
        model.addObject("pageNo",this.pageNo);

        return model;
    }

    /**
     * 审核投诉订单
     * @param request
     * @return
     */
    @RequestMapping("/audit.action")
    public ModelAndView audit(HttpServletRequest request) {

        this.pageSize = 15;
        String id = request.getParameter("id");
        String auditStatus_para = request.getParameter("auditStatus");
        String remark = request.getParameter("remark");
        String scoreStr = request.getParameter("score");
        ModelAndView model = new ModelAndView("");

        try {
            Complaint complaint = complaintService.getComplaintById(id);
            if (complaint.getAuditStatus()!=0) {
                throw new BusinessException("该投诉已审核，请勿重复操作！");
            }
            int auditStatus = Integer.valueOf(auditStatus_para);
            complaint.setAuditTime(new Date());
            if (auditStatus==1) {//投诉通过
                Integer eventType = 1;
                String eventKey = complaint.getStoreId() + "_" + System.currentTimeMillis();
                Complaint.ComplaintTypeEnum complaintTypeEnum = Complaint.ComplaintTypeEnum.typeOf(complaint.getComplaintStatus());
                String reason = complaintTypeEnum.getCn_des();

                sellerCreditService.addCredit(complaint.getStoreId(),-Integer.valueOf(scoreStr) , eventType, eventKey, reason);

                notificationHelperClient.notifyBuyerComplaintSuccess(complaint,Integer.valueOf(scoreStr),5);
                notificationHelperClient.notifyStoreComplaintSuccess(complaint,Integer.valueOf(scoreStr),5);
            }else if (auditStatus==-1){//投诉驳回
                complaint.setRemark(remark);
                notificationHelperClient.notifyBuyerComplaintFail(complaint,5);
            }
            complaint.setAuditTime(new Date());
            complaint.setAuditStatus(auditStatus);
            complaintService.saveComplaint(complaint);
            tipService.deleteTip(complaint.getId().toString());
        } catch (BusinessException e) {
            model.addObject("error", error);
            model.setViewName("redirect:/" +  "mall/seller/compliants.action");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/compliants.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/compliants.action");
        return modelAndView;
    }


    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {

        this.pageSize = 15;
        String error = request.getParameter("error");
        String name_para = request.getParameter("name_para");
        String roleName = request.getParameter("roleName");
        String sellerId = request.getParameter("sellerId");
        String levelString = request.getParameter("level");
        String sellerName = request.getParameter("sellerName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String message = request.getParameter("message");
        String username_parent = request.getParameter("username_parent");
        ModelAndView model = new ModelAndView("admin_seller_list");

        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminSellerService.pagedQuery(this.pageNo, this.pageSize, name_para, getLoginPartyId(),
                    sellerId, sellerName, startTime, endTime, roleName,username_parent,levelString);

            List<Map> list = this.page.getElements();
            for (int i = 0; i < list.size(); i++) {

                Map map = list.get(i);
                map.put("reals",focusSellerService.getFocusCount((String) map.get("sellerId")));
                String level = (String) map.get("level");
                if (StringUtils.isEmptyString(level)){
                    map.put("level",0);
                }
                Long recTime = (Long) map.get("recTime");
                if (recTime > 0){
                    map.put("recTime", DateUtils.getLongDate(new Date(recTime)));
                }
            }
            model.addObject("page",this.page);

        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("roleName",roleName);
        model.addObject("sellerId",sellerId);
        model.addObject("sellerName",sellerName);
        model.addObject("name_para",name_para);
        model.addObject("startTime",startTime);
        model.addObject("endTime",endTime);
        model.addObject("message",message);
        model.addObject("pageNo",this.pageNo);
        model.addObject("error",error);
        model.addObject("username_parent",username_parent);
        model.addObject("levels", getLevel().stream().map(MallLevel::getLevel).collect(Collectors.toList()));
        model.addObject("level",levelString);
        model.addObject("platformName",sysparaService.find("platform_name").getValue());

        return model;
    }

    /**
     * 邀请奖金申请列表
     * @param request
     * @return
     */
    @RequestMapping("/invitelist.action")
    public ModelAndView invitelist(HttpServletRequest request) {

        this.pageSize = 15;
        String userName = request.getParameter("userName");
        String userCode = request.getParameter("userCode");
        String sellerName = request.getParameter("sellerName");
        String state = request.getParameter("state");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String lotteryName = request.getParameter("lotteryName");
        ModelAndView model = new ModelAndView("activity_lottery_receive");

        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminSellerService.invitePagedQuery(this.pageNo, this.pageSize, userName, userCode,sellerName,state,startTime, endTime, lotteryName);
            List<Map> list = this.page.getElements();
            for (int i = 0; i < list.size(); i++) {

                Map map = list.get(i);
                Party agentParty = userRecomService.getAgentParty((Serializable) map.get("partyId"));
                if (null != agentParty){
                    map.put("agentName",agentParty.getUsername());
                }
            }
            model.addObject("page",this.page);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("userName",userName);
        model.addObject("userCode",userCode);
        model.addObject("sellerName",sellerName);
        model.addObject("state",state);
        model.addObject("startTime",startTime);
        model.addObject("endTime",endTime);
        model.addObject("message",message);
        model.addObject("lotteryName",lotteryName);
        model.addObject("pageNo",this.pageNo);
        model.addObject("error",error);
        return model;
    }

    /**
     * 邀请奖金派发
     * @param request
     * @return
     */
    @RequestMapping("/distribute.action")
    public ModelAndView distributeBonuses(HttpServletRequest request) {
        String partyid = request.getParameter("partyId");
        String uuid = request.getParameter("uuid");
        String prizeAmount = request.getParameter("prizeAmount");
        String remark = request.getParameter("remark");
        ModelAndView model = new ModelAndView();

        try {
            double prizeAmountD = null == prizeAmount? 0L : Double.parseDouble(prizeAmount);
            adminSellerService.updateDistributeBonuses(partyid, uuid,prizeAmountD,remark,this.getUsername_login());
            model.addObject("page",this.page);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/invitelist.action");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", t.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/invitelist.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/invitelist.action");
        return modelAndView;
    }


    /**
     * 更新店铺等级信息
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateStoreLevel.action")
    public ModelAndView updateStoreLevel(HttpServletRequest request) {
        String partyId = request.getParameter("sellerId");
        String level = request.getParameter("level");
        String rechargeAmount_para = request.getParameter("rechargeAmount");
        ModelAndView model = new ModelAndView();
        String operatorName = this.getUsername_login();
        String ip = this.getIp(getRequest());
        Party party = this.partyService.getById(partyId);
        try {
            double rechargeAmount = Double.parseDouble(rechargeAmount_para);
            adminSellerService.updateStoreLevel(partyId,level,rechargeAmount,operatorName,ip,Objects.nonNull(party)?party.getUsername():"");
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        return modelAndView;
    }


    /**
     * 更新店铺等级信息
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateHighOpinion.action")
    public ModelAndView updateHighOpinion(HttpServletRequest request) {
        String partyId = request.getParameter("id");
        Double highOpinion = Double.valueOf(request.getParameter("recommendDurationh"));
        log.error("===========接收到的修改参数为:"+String.valueOf(highOpinion));
        ModelAndView model = new ModelAndView();
        try {
            adminSellerService.updateHighOpinion(partyId,highOpinion);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        return modelAndView;
    }
    /**
     * 访问量系数
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateUp.action")
    public ModelAndView update(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String seller_Id = request.getParameter("seller_Id");
        String auto_start = request.getParameter("auto_start");
        String auto_end = request.getParameter("auto_end");
        String auto_valid = request.getParameter("auto_valid");
        String base_traffic = request.getParameter("base_traffic");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {

            adminSellerService.update(seller_Id,auto_start,auto_end,base_traffic,auto_valid);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        return modelAndView;
    }
    /**
     * 访问量系数
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateAttention.action")
    public ModelAndView updateAttention(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String seller_Id = request.getParameter("seller_Ids");
        String fakeAttention = request.getParameter("fakeAttention");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {

            adminSellerService.updateAttention(seller_Id,fakeAttention);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        return modelAndView;
    }

    /**
     * 获取今日访问店铺人数
     */
    @RequestMapping( "getViewNumsBySellerIds.action")
    public Map getViewNumsBySellerIds(HttpServletRequest request) {
        String sellerId = request.getParameter("sellerId");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Long viewNums = this.adminSellerService.getViewNumsBySellerId(sellerId);
            Map<String, Object> willIncome = sellerService.loadReportWillIncome(sellerId, null, null);
            resultMap.put("viewsNum", viewNums);
            resultMap.putAll(willIncome);
            resultMap.put("code", 200);

        } catch (BusinessException e) {
            resultMap.put("code", 500);
            resultMap.put("message", e.getMessage());
        } catch (Throwable t) {
            logger.error(" error ", t);
            resultMap.put("code", 500);
            resultMap.put("message", "程序错误");
        }
        return resultMap;
    }

    /**
     * 获取今日访问店铺人数
     */
    @RequestMapping( "getGoodsNumBySellerIds.action")
    public Map getGoodsNumBySellerIds(HttpServletRequest request) {
        String sellerId = request.getParameter("sellerId");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            int goodsNum = this.adminSellerService.getGoodsNumBySellerIds(sellerId);
            resultMap.put("goodsNum", goodsNum);
            resultMap.put("code", 200);

        } catch (BusinessException e) {
            resultMap.put("code", 500);
            resultMap.put("message", e.getMessage());
        } catch (Throwable t) {
            logger.error(" error ", t);
            resultMap.put("code", 500);
            resultMap.put("message", "程序错误");
        }
        return resultMap;
    }

    /**
     * 是否店铺首页推荐
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateStatus.action")
    public ModelAndView updateStatus(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        String recommendDuration = request.getParameter("recommendDuration");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminSellerService.updateStatus(id,Integer.parseInt(status),Integer.parseInt(recommendDuration));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        return modelAndView;
    }

    /**
     * 管理后台更新备注
     */
    @RequestMapping("/updateRemarks.action")
    public ModelAndView updateRemarks(HttpServletRequest request) {
        String partyId = request.getParameter("sellerId");
        String remarks = request.getParameter("remarks");
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        try {
//            this.adminSellerService.updateRemarks(sellerId, remarks);
            partyService.updateUserRemark(partyId,remarks);
        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        return modelAndView;
    }

//    /**
//     * 店铺冻结状态
//     * @param request
//     * @return
//     */
//    @RequestMapping(value =  "/updateFreeze.action")
//    public ModelAndView updateFreeze(HttpServletRequest request) {
//        String pageNo = request.getParameter("pageNo");
//        String id = request.getParameter("id");
//        String freeze = request.getParameter("freeze");
//        ModelAndView model = new ModelAndView();
//        model.addObject("pageNo",pageNo);
//        try {
//            adminSellerService.updateFreeze(id,Integer.parseInt(freeze));
//        } catch (BusinessException e) {
//            model.addObject("error", e.getMessage());
//            model.setViewName("redirect:/" +  "mall/seller/list.action");
//            return model;
//        } catch (Throwable t) {
//            logger.error("update error ", t);
//            model.addObject("error", "程序错误");
//            model.setViewName("redirect:/" +  "mall/seller/list.action");
//            return model;
//        }
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("message", "操作成功");
//        modelAndView.addObject("pageNo", pageNo);
//        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
//        return modelAndView;
//    }


    /**
     * 获取等级列表与商家与当前累计充值金额
     */
    @RequestMapping( "getLevleBySellerId.action")
    public Map getLevleBySellerId(HttpServletRequest request) {
        String sellerId = request.getParameter("sellerId");
        Map<String, Object> resultMap = new HashMap<String, Object>();

        LinkedHashMap<Object, String> levels = new LinkedHashMap<>();
        try {
            Seller seller = sellerService.getSeller(sellerId);
            for (MallLevel mallLevel : getLevel()) {
                MallLevelCondExpr mallLevelCondExpr = JsonUtils.json2Object(mallLevel.getCondExpr(), MallLevelCondExpr.class);
                List<MallLevelCondExpr.Param> params = mallLevelCondExpr.getParams();
                params.forEach(e ->{
                    if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode())){
                        levels.put(mallLevel.getLevel(),e.getValue());
                    }
                });
            }
            UserMetrics userMetrics = userMetricsService.getByPartyId(sellerId);

            resultMap.put("levelMap", levels);
            resultMap.put("sellerLevel", seller.getMallLevel());
            resultMap.put("rechargeAmount", null == userMetrics ? 0 : userMetrics.getStoreMoneyRechargeAcc());
            resultMap.put("code", 200);
        } catch (BusinessException e) {
            resultMap.put("code", 500);
            resultMap.put("message", e.getMessage());
        } catch (Throwable t) {
            logger.error(" error ", t);
            resultMap.put("code", 500);
            resultMap.put("message", "程序错误");
        }
        return resultMap;
    }

    /**
     * 获取等级列表与商家与当前累计充值金额
     */
    @RequestMapping( "getHighOpinionBySellerId.action")
    public Map getHighOpinionBySellerId(HttpServletRequest request) {
        String sellerId = request.getParameter("sellerId");
        Map<String, Object> resultMap = new HashMap<String, Object>();
        try {
            Seller seller = sellerService.getSeller(sellerId);

            resultMap.put("highOpinion", seller.getHighOpinion());
            resultMap.put("code", 200);
        } catch (BusinessException e) {
            resultMap.put("code", 500);
            resultMap.put("message", e.getMessage());
        } catch (Throwable t) {
            logger.error(" error ", t);
            resultMap.put("code", 500);
            resultMap.put("message", "程序错误");
        }
        return resultMap;
    }



    private List<MallLevel> getLevel(){
        List<MallLevel> mallLevels = mallLevelService.listLevel();

        Comparator<MallLevel> idComparator = new IdSerializableComparator();
        mallLevels.sort(idComparator);
        return mallLevels;
    }


    /**
     * 店铺拉黑或者接拉黑状态
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateBlack.action")
    public ModelAndView updateBlack(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String isBlack = request.getParameter("isBlack");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminSellerService.updateBlack(id,Integer.parseInt(isBlack));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        return modelAndView;
    }
    /**
     * 店铺店铺流量开启或关闭
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateFlow.action")
    public ModelAndView updateFlow(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String flow = request.getParameter("isFlow");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminSellerService.updateFlow(id,Integer.parseInt(flow));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
        return modelAndView;
    }


//    @RequestMapping(value =  "/LoginFree.action")
//    public ModelAndView LoginFree(HttpServletRequest request) {
//        String pageNo = request.getParameter("pageNo");
//        String id = request.getParameter("id");
//        ModelAndView model = new ModelAndView();
//        model.addObject("pageNo",pageNo);
//        try {
//
//            String token = adminSellerService.LoginFree(id,this.getUsername_login());
//
//            Desktop desktop = Desktop.getDesktop();
//            desktop.browse(URI.create(Constants.WEB_URL + "/seller/#/login?token=" + token));
//
//        } catch (BusinessException e) {
//            model.addObject("error", e.getMessage());
//            model.setViewName("redirect:/" +  "mall/seller/list.action");
//            return model;
//        } catch (Throwable t) {
//            logger.error("update error ", t);
//            model.addObject("error", "程序错误");
//            model.setViewName("redirect:/" +  "mall/seller/list.action");
//            return model;
//        }
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("message", "操作成功");
//        modelAndView.addObject("pageNo", pageNo);
//        modelAndView.setViewName("redirect:/" +  "mall/seller/list.action");
//        return modelAndView;
//    }


    /**
     * 平台登录商家
     * @param request
     * @return
     */
    @RequestMapping("/LoginFree.action")
    public Map LoginFree(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String id = request.getParameter("id");
        try {
            String token = adminSellerService.getLoginFree(id, this.getUsername_login());
            String loginUrl = AutoConfig.getBaseUrl() + "/seller/#/login?token=" + token;
//            String loginUrl = "localhost:8085/wap/api/user!LoginFree.action?token=" + token;

            resultMap.put("loginUrl",loginUrl);
            resultMap.put("code",200);
            return resultMap;
        } catch (BusinessException e) {
            resultMap.put("code", 500);
            resultMap.put("error", e.getMessage());
            return resultMap;
        } catch (Throwable t) {
            logger.error(" error ", t);
            resultMap.put("code", 500);
            resultMap.put("error", t.getMessage());
            return resultMap;
        }
    }

    /**
     * 平台登录商家
     * @param request
     * @return
     */
    @RequestMapping("/getTokenLink.action")
    public Map getTokenLink(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String id = request.getParameter("id");
        try {
            SecUser user = secUserService.findUserByPartyId(id);
            if (Objects.isNull(user)){
                throw new BusinessException("商家用户未注册");
            }
            String token;
            Token tokenRedis = (Token) redisHandler.get(UserRedisKeys.PLAT_FROM_TOKEN_PARTY_ID + user.getPartyId());
            if (Objects.isNull(tokenRedis)){
                token = tokenService.platFromSavePut(user.getPartyId());
            } else {
                token = tokenRedis.getToken();
            }
            String tokenLink = AutoConfig.getBaseUrl() + "/seller-h5/#/shop?from=shop&token=" + token;
            resultMap.put("url", tokenLink);
            resultMap.put("code", 200);
            return resultMap;
        } catch (BusinessException e) {
            resultMap.put("code", 500);
            resultMap.put("error", e.getMessage());
            return resultMap;
        } catch (Throwable t) {
            logger.error(" error ", t);
            resultMap.put("code", 500);
            resultMap.put("error", t.getMessage());
            return resultMap;
        }
    }

    /**
     * 修改店铺信誉分
     *
     * @param request
     * @return
     */
    @PostMapping("/refreshCredit.action")
    public ModelAndView setSellerCredit(HttpServletRequest request) {
        //String partyId = this.getLoginPartyId();
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        System.out.println("---------------> setSellerCredit 登录用户：" + this.getUsername_login());
        String partyId = sec.getPartyId();

        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);

        String sellerId = request.getParameter("sellerId1");
        // 增减的信誉分，正值
        String scoreStr = request.getParameter("score");
         String reason = request.getParameter("reason");
        // 信誉分  字段type 1-加分， 0-减分
        String typeStr = request.getParameter("type");

        int type = Integer.parseInt(typeStr);

        try {
            if (StringUtils.isEmptyString(scoreStr)){
                throw new BusinessException("操作信誉分不能为空");
            }

            int accScore = Integer.parseInt(scoreStr);
            if (accScore < 0) {
                // 值必须是正数，防止前端错误填写
                throw new BusinessException("信誉分变更值不能填负值");
            }

            if (type == 0) {
                // 通过 type 来修正正负数
                accScore = -accScore;
            }

            Integer eventType = 1;
            String eventKey = partyId + "_" + System.currentTimeMillis();

            SellerCredit accCredit = sellerCreditService.addCredit(sellerId, accScore, eventType, eventKey, reason);

            // 发送站内信
            try {
//                Seller seller = sellerService.getSeller(sellerId);
//                String bizType = NotificationBizTypeEnum.INBOX_SELLER_CREDIT_UPDATED.getBizType();
//                String language = "zh_CN";
//                String targetUserId = sellerId;
//                String targetTopic = "0";
//                Map<String, Object> varDataMap = new HashMap<>();
//                // 根据站内信模板中的占位符设置值 TODO
//                varDataMap.put("creditScore", seller.getCreditScore());
//
//                sendInboxNotify(bizType, language, targetUserId, targetTopic, varDataMap);

                if (type == 0) {
                    // 目前，减分才发送消息
                    notificationHelperClient.notifyUpdateSellerCreditScore(accCredit, 5);
                }
            } catch (Exception e0) {
                logger.error("更新店铺信誉分后发送消息通知报错", e0);
            }
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        model.addObject("message", "操作成功");
        model.setViewName("redirect:/" +  "mall/seller/list.action");
        return model;
    }

    /**
     * 冻结商品资金
     *
     * @param request
     * @return
     */
    @PostMapping("/freezeMoney.action")
    public ModelAndView freezeSellerMoney(HttpServletRequest request) {
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        String operatorId = sec.getPartyId();

        ModelAndView model = new ModelAndView();

        String sellerId = request.getParameter("seller_Id4");
        String amountStr = request.getParameter("amount");
        String reason = request.getParameter("reason");
        String freezeDaysStr = request.getParameter("freezeDays");

        try {
            double freezeAmout;
            int freezeDays;

            // 10-13  TikTokWholesale冻结用户全部金额 并永久冻结
            String platformName = sysparaService.find("platform_name").getValue();
            if (Objects.equals(platformName, PlatformNameEnum.TIKTOK_WHOLESALE.getDescription())){
                freezeAmout = 0.0D;
                freezeDays = 10000;
            } else {
                freezeAmout = Double.parseDouble(amountStr);
//                freezeDays = Integer.parseInt(freezeDaysStr);
            }

            if (freezeAmout < 0) {
                // 值必须是正数，防止前端错误填写
                freezeAmout = -freezeAmout;
            }
//            if (freezeDays < 0) {
//                // 值必须是正数，防止前端错误填写
//                freezeDays = -freezeDays;
//            }

            // 如果一个商家只能同时冻结一次，此处需要做判断 TODO

            // 10/26 业务修改 冻结全部金额，永久冻结
            MoneyFreeze freezeRecord = moneyFreezeService.updateFreezeSeller(sellerId, freezeAmout, 10000, reason, operatorId);

            Party party = partyService.cachePartyBy(freezeRecord.getPartyId(), false);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(party.getUsername());
            log.setOperator(this.getUsername_login());

            log.setLog("管理员手动冻结金额["+freezeRecord.getAmount()+"]" + "冻结时间[" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss") + "]");
            logService.saveSync(log);

            // 发送站内信
            try {
//                String bizType = NotificationBizTypeEnum.INBOX_FREEZE_SELLER_MONEY.getBizType();
//                String language = "en_US";
//                String targetUserId = sellerId;
//                String targetTopic = "0";
//                Map<String, Object> varDataMap = new HashMap<>();
//                // 根据站内信模板中的占位符设置值 TODO
//                varDataMap.put("amount", freezeAmout);
//                varDataMap.put("days", freezeDays);
//                sendInboxNotify(bizType, language, targetUserId, targetTopic, varDataMap);

                notificationHelperClient.notifyFreezeSellerMoney(freezeRecord, 5);
            } catch (Exception e0) {
                logger.error("冻结商家资金后发送消息通知报错", e0);
                model.setViewName("redirect:/" +  "mall/seller/list.action");// TODO
                return model;
            }
        } catch (RuntimeException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");// TODO
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");// TODO
            return model;
        }
        model.addObject("message", "操作成功");
        model.setViewName("redirect:/" +  "mall/seller/list.action");// TODO
        return model;
    }

    /**
     * 解冻结商品资金
     *
     * @param request
     * @return
     */
    @PostMapping("unFreezeMoney.action")
    public ModelAndView unfreezeSellerMoney(HttpServletRequest request) {
        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
        String operatorId = sec.getPartyId();

        ModelAndView model = new ModelAndView();

        //String freezeId = request.getParameter("freezeId");
        String sellerId = request.getParameter("seller_Id2");

        try {
            MoneyFreeze freezeRecord = moneyFreezeService.getLastFreezeRecord(sellerId);
            if (freezeRecord == null) {
                // 没有冻结记录 TODO
                throw new BusinessException("此商铺未有冻结记录，请刷新页面");
            }

            moneyFreezeService.updateUnFreezeSeller(freezeRecord.getId().toString(), operatorId);

            // 发送站内信
            try {
//                String bizType = NotificationBizTypeEnum.INBOX_UNFREEZE_SELLER_MONEY.getBizType();
//                String language = "en_US";
//                String targetUserId = sellerId;
//                String targetTopic = "0";
//                Map<String, Object> varDataMap = new HashMap<>();
//                // 根据站内信模板中的占位符设置值 TODO
//                varDataMap.put("amount", freezeRecord.getAmount());
//
//                sendInboxNotify(bizType, language, targetUserId, targetTopic, varDataMap);

                notificationHelperClient.notifyUnFreezeSellerMoney(freezeRecord, 5);
            } catch (Exception e0) {
                logger.error("解冻商家资金后发送通知消息报错", e0);
            }
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        model.addObject("message", "操作成功");
        model.setViewName("redirect:/" +  "mall/seller/list.action");
        return model;
    }

    /**
     * 获取商铺的销量信息
     *
     * @param request
     * @return
     */
//    @GetMapping("getSoldNum.action")
//    public ModelAndView getSellerSoldNum(HttpServletRequest request) {
//        SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
//
//        ModelAndView model = new ModelAndView();
//        String sellerId = request.getParameter("sellerId");
//        try {
//            Seller seller = sellerService.getSeller(sellerId);
//            if (seller == null) {
//                model.addObject("error", "商铺记录不存在");
//                model.setViewName("redirect:/" +  "mall/seller/list.action");
//                return model;
//            }
//
//            Map<String, Object> soldInfo = new HashMap<>();
//            int realSoldNum = sellerGoodsService.getSoldNumBySellerId(sellerId);
//
//            model.addObject("realSoldNum", realSoldNum);
//            model.addObject("fakeSoldNum", seller.getFakeSoldNum() == null ? 0 : seller.getFakeSoldNum());
//        } catch (BusinessException e) {
//            model.addObject("error", e.getMessage());
//            model.setViewName("redirect:/" +  "mall/seller/list.action");
//            return model;
//        } catch (Throwable t) {
//            logger.error("update error ", t);
//            model.addObject("error", "程序错误");
//            model.setViewName("redirect:/" +  "mall/seller/list.action");
//            return model;
//        }
//        model.addObject("message", "操作成功");
//        model.setViewName("redirect:/" +  "mall/seller/list.action");
//        return model;
//    }

    /**
     * 获取商铺的销量信息
     * @param request
     * @return
     */
    @RequestMapping("/getSoldNum.action")
    public Map getSellerSoldNum(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        String sellerId = request.getParameter("sellerId");
        try {
            Seller seller = sellerService.getSeller(sellerId);
            if (seller == null) {
                throw new BusinessException("商铺记录不存在");
            }

            Long realSoldNum = sellerGoodsService.getSoldNumBySellerId(sellerId);
            resultMap.put("realSoldNums",realSoldNum);
            resultMap.put("fakeSoldNums",seller.getFakeSoldNum() == null ? 0 : seller.getFakeSoldNum());
            resultMap.put("code",200);
            return resultMap;
        } catch (BusinessException e) {
            resultMap.put("code", 500);
            resultMap.put("error", e.getMessage());
            return resultMap;
        } catch (Throwable t) {
            logger.error(" error ", t);
            resultMap.put("code", 500);
            resultMap.put("error", t.getMessage());
            return resultMap;
        }
    }

    /**
     * 设置商铺的虚假销量
     *
     * @param request
     * @return
     */
    @PostMapping("setSoldNum.action")
    public ModelAndView refreshSellerSoldNum(HttpServletRequest request) {

        ModelAndView model = new ModelAndView();

        String fakeSoldNumStr = request.getParameter("fakeSoldNum");
        String sellerId = request.getParameter("sellerId5");

        try {
            int fakeSoldNum = Integer.parseInt(fakeSoldNumStr);
            Seller seller = sellerService.getSeller(sellerId);

            if (seller == null) {
                throw new BusinessException("商铺记录不存在");
            }

//            Integer oldFakeSoldNum = seller.getFakeSoldNum();
//            if (oldFakeSoldNum == null) {
//                oldFakeSoldNum = 0;
//            }
//
//            if (oldFakeSoldNum > fakeSoldNum) {
//                throw new BusinessException("商铺的虚假销量值不能比以前的小，旧的虚假销量值为:" + oldFakeSoldNum);
//            }

            sellerService.updateFakeSoldNum(sellerId, fakeSoldNum);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/seller/list.action");
            return model;
        }
        model.addObject("message", "操作成功");
        model.setViewName("redirect:/" +  "mall/seller/list.action");
        return model;
    }



//    /**
//     * 发送站内信
//     *
//     * @param bizType
//     * @param language
//     * @param targetUserId
//     * @param targetTopic
//     * @param varDataMap
//     */
//    private void sendInboxNotify(String bizType, String language, String targetUserId, String targetTopic, Map<String, Object> varDataMap) {
//        DefaultNotifyRequest notifyRequest = new DefaultNotifyRequest();
//        notifyRequest.setBizType(bizType);
//        notifyRequest.setFromUserId("0");
//        notifyRequest.setLanguageType(language);
//        notifyRequest.setTargetTopic("0");
//        if (targetTopic == null || targetTopic.trim().isEmpty()) {
//            notifyRequest.setTargetTopic("0");
//        } else {
//            notifyRequest.setTargetTopic(targetTopic.trim());
//        }
//
//        logger.info("---> sendInboxNotify 方法收到的消息变量信息为:" + varDataMap);
//        if (varDataMap != null && varDataMap.size() > 0) {
//            Set<Map.Entry<String, Object>> entrySets = varDataMap.entrySet();
//            for (Map.Entry<String, Object> oneEntry : entrySets) {
//                notifyRequest.setValue(oneEntry.getKey(), oneEntry.getValue());
//            }
//        }
//
//        if (targetUserId == null || targetUserId.trim().isEmpty()) {
//            notifyRequest.setTargetUserId("0");
//            // 广播消息
//            commonNotifyService.sendNotify(notifyRequest);
//        } else {
//            notifyRequest.setTargetUserId(targetUserId.trim());
//            // 针对具体用户的消息
//            commonNotifyService.sendNotify(notifyRequest);
//        }
//    }
}