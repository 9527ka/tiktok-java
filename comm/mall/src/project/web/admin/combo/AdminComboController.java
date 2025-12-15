package project.web.admin.combo;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import jnr.posix.windows.SystemTime;
import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.log.LogService;
import project.mall.combo.AdminComboService;
import project.mall.combo.model.Combo;
import project.mall.combo.model.ComboLang;
import project.party.PartyService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/mall/combo")
public class AdminComboController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminComboController.class);

    @Resource
    protected AdminComboService adminComboService;

    @Resource
    protected PartyService partyService;
    @Resource
    protected LogService logService;
    @Resource
    protected SecUserService secUserService;
    @Resource
    protected PasswordEncoder passwordEncoder;

    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {

        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        ModelAndView model = new ModelAndView("admin_combo_list");


        model.addObject("error",error);
        try {
            this.pageSize = 20;
            this.checkAndSetPageNo(pageNo);
            this.page = adminComboService.pagedQuery(this.pageNo, this.pageSize,name,startTime, endTime);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("page",page);
        model.addObject("pageNo",this.pageNo);
        model.addObject("name",name);
        model.addObject("message",message);
        model.addObject("startTime",startTime);
        model.addObject("endTime",endTime);
        return model;
    }

    /**
     * 新增页面
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/toAdd.action")
    public ModelAndView toAdd(HttpServletRequest request) {
        String error = request.getParameter("error");
        String pageNo = request.getParameter("pageNo");
        ModelAndView model = new ModelAndView();
        model.setViewName("admin_combo_add");
        model.addObject("error", error);
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
                throw new BusinessException("项目分类名称不能为空");
            }
            adminComboService.save(name);

        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_combo_add");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.setViewName("admin_combo_add");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "/mall/combo/list.action");
        return modelAndView;
    }

    @RequestMapping(value =  "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String comboId = request.getParameter("comboId");
        String lang = request.getParameter("lang");
        String message = request.getParameter("message");
        ModelAndView model = new ModelAndView();
        try {
            Combo combo = adminComboService.findById(comboId);
            List<ComboLang> comboLangList = this.adminComboService.findLanByComboId(comboId, lang);
            if(CollectionUtils.isEmpty(comboLangList)){
                model.addObject("categoryLanId",null);
                model.addObject("name",null);
                model.addObject("content",null);
            } else {
                ComboLang comboLang = comboLangList.get(0);

                model.addObject("comboLanId",comboLang.getId());
                model.addObject("name",comboLang.getName());
                model.addObject("content",comboLang.getContent());
            }
            model.addObject("error", error);
            model.addObject("comboId", comboId);
            model.addObject("iconImg", combo.getIconImg());
            model.addObject("promoteNum", combo.getPromoteNum());
            model.addObject("amount", combo.getAmount());
            model.addObject("day", combo.getDay());
            model.addObject("baseAccessNum", combo.getBaseAccessNum());
            model.addObject("autoAccMin", combo.getAutoAccMin());
            model.addObject("autoAccMax", combo.getAutoAccMax());
            model.addObject("lang",lang);
            model.addObject("pageNo",pageNo);
            model.addObject("message",message);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/combo/list");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("redirect:/" +  "/mall/combo/list");
            return model;
        }
        model.setViewName("admin_combo_update");
        return model;
    }



    @RequestMapping(value =  "/update.action")
    public ModelAndView update(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String lang = request.getParameter("lang");
        String promoteNum = request.getParameter("promoteNum");
        String comboId = request.getParameter("comboId");
        String comboLanId = request.getParameter("comboLanId");
        String iconImg = request.getParameter("iconImg");
        String amount = request.getParameter("amount");
        String day = request.getParameter("day");
        String content = request.getParameter("content");
        String baseAccessNum = request.getParameter("baseAccessNum");
        String autoAccMin = request.getParameter("autoAccMin");
        String autoAccMax = request.getParameter("autoAccMax");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("pageNo", pageNo);
        model.addObject("lang", lang);
        model.addObject("comboId", comboId);
        model.addObject("comboLanId", comboLanId);
        model.addObject("iconImg", iconImg);
        model.addObject("promoteNum", promoteNum);
        model.addObject("amount", amount);
        model.addObject("day", day);
        model.addObject("content", content);
        model.addObject("baseAccessNum", baseAccessNum);
        model.addObject("autoAccMin", autoAccMin);
        model.addObject("autoAccMax",autoAccMax);
        try {

            String error = verification(name, promoteNum,iconImg,amount,day);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            Combo bean = adminComboService.findById(comboId);
            if(Objects.isNull(bean)){
                throw new BusinessException("分类已被删除");
            }
            bean.setIconImg(iconImg);
            bean.setAmount(Double.valueOf(amount));
            bean.setDay(Integer.parseInt(day));
            bean.setPromoteNum(Integer.parseInt(promoteNum));
            bean.setBaseAccessNum(Integer.parseInt(baseAccessNum));
            bean.setAutoAccMin(Integer.parseInt(autoAccMin));
            bean.setAutoAccMax(Integer.parseInt(autoAccMax));
            adminComboService.update(bean,name,lang,comboId,comboLanId,content);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_combo_update");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("admin_combo_update");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("lang", lang);
        modelAndView.addObject("comboId", comboId);
        modelAndView.addObject("comboLanId", comboLanId);
        modelAndView.setViewName("redirect:/" +  "mall/combo/toUpdate.action");
        return modelAndView;
    }

    private String verification(String name, String promoteNum, String iconImg, String amount, String day) {
        if(StringUtils.isEmptyString(name)){
            throw new BusinessException("请输入套餐名称");
        }
        if(StringUtils.isEmptyString(promoteNum)){
            throw new BusinessException("请输入推广产品数");
        }
        if(StringUtils.isEmptyString(iconImg)){
            throw new BusinessException("请选择封面图");
        }
        if(StringUtils.isEmptyString(amount)){
            throw new BusinessException("价格不能为空");
        }
        if(StringUtils.isEmptyString(day)){
            throw new BusinessException("有效期不能为空");
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
                model.setViewName("redirect:/" +  "invest/combo/list.action");
                return model;
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec,this.getUsername_login(), login_safeword);

            String id = request.getParameter("baseId");
            List<ComboLang> lanByComboId = adminComboService.findLanByComboId(id, null);
            this.adminComboService.delete(id,lanByComboId);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            lanByComboId.forEach(e ->{
                if(e.getLang().equals("cn")){
                    log.setLog("管理员手动删除直通车套餐["+e.getName() +"] 操作ip:["+this.getIp(getRequest())+"]" + "Time [" + DateUtil.DatetoString(new Date(),"yyyy-MM-dd HH:mm:ss")+ "]");
                }
            });

            logService.saveSync(log);
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "mall/combo/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/combo/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/combo/list.action");
            return model;
        }
    }

    @RequestMapping("/recordList.action")
    public ModelAndView recordList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String userCode = request.getParameter("userCode");
        String sellerName = request.getParameter("sellerName");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_combo_record_list");

        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;

            this.page = adminComboService.pagedQueryRecordList(this.pageNo, this.pageSize, userCode, sellerName, startTime, endTime);
            List<Map> list = page.getElements();
            for (int i = 0; i < list.size(); i++) {
                Map map=list.get(i);
                LocalDateTime createTime = (LocalDateTime)map.get("createTime");


//                SystemTime
//                if(){}

                Date from = Date.from(createTime.atZone(ZoneId.systemDefault()).toInstant());
                long promoteDay = DateUtils.calcTimeBetween("d", from, new Date());
                map.put("promoteDay",promoteDay);
                map.put("stopTimes",DateUtil.DatetoString(new Date((long)map.get("stopTime")),"yyyy-MM-dd HH:mm:ss"));
            }
            modelAndView.addObject("page",this.page);
        } catch (BusinessException e) {
            modelAndView.addObject("error", error);
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo",this.pageNo);
        modelAndView.addObject("message",message);
        modelAndView.addObject("userCode",userCode);
        modelAndView.addObject("sellerName",sellerName);
        modelAndView.addObject("startTime",startTime);
        modelAndView.addObject("endTime",endTime);
        modelAndView.addObject("error",error);
        return modelAndView;
    }

    /**
     * 订单具体商品列表查询
     * @param request
     * @return
     */
    @RequestMapping("/recordGoodsList.action")
    public ModelAndView recordGoodsList(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String partyId = request.getParameter("partyId");
        ModelAndView model = new ModelAndView("admin_combo_goods_list");

        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            // 待替换 TODO
            // this.page = adminComboService.pagedQueryRecordGoodsList1(this.pageNo, this.pageSize, partyId);
            this.page = adminComboService.pagedQueryRecordGoodsList(this.pageNo, this.pageSize, partyId);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("pageNo",this.pageNo);
        model.addObject("message",message);
        model.addObject("error",error);
        model.addObject("page", this.page);
        return model;
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