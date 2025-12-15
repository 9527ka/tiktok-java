package project.web.admin.goods;


import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.invest.goods.AdminGoodsService;
import project.invest.goods.model.Goods;
import project.invest.goods.model.GoodsLang;
import project.invest.platform.AdminPlatformService;
import project.invest.platform.Platfrom;
import project.log.LogService;
import project.party.PartyService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商品管理
 */
@Slf4j
@RestController
@RequestMapping("/invest/goods")
public class AdminGoodsController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminGoodsController.class);


    @Resource
    protected AdminGoodsService adminGoodsService;

    @Resource
    protected PartyService partyService;
    @Resource
    protected LogService logService;
    @Resource
    protected SecUserService secUserService;
    @Resource
    protected PasswordEncoder passwordEncoder;

    /**
     * 商品列表
     */
    private Map<String, String> platforms = new LinkedHashMap<String, String>();

    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {

        this.pageSize = 20;
        Integer status = null;
        String error = request.getParameter("error");
        String name = request.getParameter("name");
        if(StringUtils.isNotEmpty(request.getParameter("status"))){
            status = Integer.valueOf(request.getParameter("status"));
        }
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        String message = request.getParameter("message");
        ModelAndView model = new ModelAndView("admin_goods_list");
        model.addObject("name",name);
        model.addObject("status",status);
        model.addObject("message",message);
        model.addObject("pageNo",pageNo);
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminGoodsService.pagedQuery(this.pageNo, this.pageSize, name , status, startTime, endTime);
            model.addObject("page",this.page);
            return model;
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
        	logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
    }

    /**
     * 新增用户活动订单
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/toAdd.action")
    public ModelAndView toAdd(HttpServletRequest request) {
        String error = request.getParameter("error");
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String pageNo = request.getParameter("pageNo");
        ModelAndView model = new ModelAndView();
        model.setViewName("admin_goods_add");
        model.addObject("error", error);
        model.addObject("platforms",platforms);
        model.addObject("name",name);
        model.addObject("status",status);
        model.addObject("pageNo",pageNo);
        return model;
    }

    @RequestMapping(value =  "/add.action")
    public ModelAndView add(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("pageNo", pageNo);
        try {
            if(StringUtils.isEmptyString(name)){
                throw new BusinessException("商品名称不能为空");
            }
            adminGoodsService.save(name);

        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_goods_add");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.setViewName("admin_goods_add");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "invest/goods/list.action");
        return modelAndView;
    }



    @RequestMapping(value =  "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String goodsId = request.getParameter("goodsId");
        String lang = request.getParameter("lang");
        String message = request.getParameter("message");
        ModelAndView model = new ModelAndView();
        try {
            Goods goods = this.adminGoodsService.findById(goodsId);
            List<GoodsLang> goodsLanList = adminGoodsService.findLanByGoodsId(goodsId, lang);
            if(CollectionUtils.isEmpty(goodsLanList)){
                model.addObject("goodsLanId",null);
                model.addObject("name",null);
                model.addObject("des",null);
            } else {
                GoodsLang goodsLan = goodsLanList.get(0);
                model.addObject("goodsLanId",goodsLan.getId());
                model.addObject("name",goodsLan.getName());
                model.addObject("des",goodsLan.getDes());
            }
            model.addObject("error", error);
            model.addObject("goodsId",goodsId);
            model.addObject("iconImg",goods.getIconImg());
            model.addObject("prize",goods.getPrize());
            model.addObject("status",goods.getStatus());
            model.addObject("sort",goods.getSort());
            model.addObject("payWay",goods.getPayWay());
            model.addObject("total",goods.getTotal());
            model.addObject("lastAmount",goods.getLastAmount());
            model.addObject("exchangeAmount",goods.getExchangeAmount());
            model.addObject("lang",lang);
            model.addObject("pageNo",pageNo);
            model.addObject("message",message);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "invest/goods/list");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("redirect:/" +  "invest/goods/list");
            return model;
        }

        model.setViewName("admin_goods_update");
        return model;
    }



    @RequestMapping(value =  "/update.action")
    public ModelAndView update(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String goodsId = request.getParameter("goodsId");
        String goodsLanId = request.getParameter("goodsLanId");
        String iconImg = request.getParameter("iconImg");
        String prize = request.getParameter("prize");
        String status = request.getParameter("status");
        String lang = request.getParameter("lang");
        String sort = request.getParameter("sort");
        String total = request.getParameter("total");
        String lastAmount = request.getParameter("lastAmount");
        String des_text = request.getParameter("content");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("iconImg", iconImg);
        model.addObject("prize", prize);
        model.addObject("status", status);
        model.addObject("pageNo", pageNo);
        model.addObject("sort", sort);
        model.addObject("total", total);
        model.addObject("lastAmount", lastAmount);
        model.addObject("goodsId", goodsId);
        model.addObject("goodsLanId", goodsLanId);
        model.addObject("lang", lang);
        model.addObject("des", des_text);
        try {
            String error = verification(name, iconImg, prize, des_text, status);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            adminGoodsService.update(name,iconImg,prize,goodsId,goodsLanId,status,des_text,lang,sort,total,lastAmount);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_goods_update");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("admin_goods_update");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("goodsId", goodsId);
        modelAndView.addObject("goodsLanId", goodsLanId);
        modelAndView.addObject("lang", lang);
        modelAndView.setViewName("redirect:/" +  "invest/goods/toUpdate.action");
        return modelAndView;
    }


        private String verification(String name, String iconImg, String prize, String des, String status) {
        if (StringUtils.isEmptyString(name)){
            return "请输入商品名称";
        }
        if (StringUtils.isEmptyString(iconImg)){
            return "请选择商品封面图";
        }
        if (StringUtils.isEmptyString(prize)){
            return "请输入商品价格";
        }
        if (StringUtils.isEmptyString(des)){
            return "请输入商品介绍";
        }
        if (StringUtils.isEmptyString(status)){
            return "请选中商品状态";
        }
        return null;
    }


    @RequestMapping(value =  "/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        try {
            String login_safeword = request.getParameter("login_safeword");
            if (StringUtils.isNullOrEmpty(login_safeword)) {
                model.addObject("error", "请输入登录人资金密码");
                model.setViewName("redirect:/" +  "invest/goods/list.action");
                return model;
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec,this.getUsername_login(), login_safeword);

            String id = request.getParameter("id");
            List<GoodsLang> lanByGoodsId = adminGoodsService.findLanByGoodsId(id, null);
            this.adminGoodsService.delete(id,lanByGoodsId);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            lanByGoodsId.forEach(e ->{
                if(e.getLang().equals("cn")){
                    log.setLog("管理员手动删除商品["+e.getName() +"] 操作ip:["+this.getIp(getRequest())+"]" + "Time [" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss")+ "]");
                }
            });

            logService.saveSync(log);
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "invest/goods/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "invest/goods/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "invest/goods/list.action");
            return model;
        }
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