package project.web.admin.category;

import cn.hutool.core.util.StrUtil;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.log.LogService;
import project.mall.goods.AdminMallGoodsService;
import project.mall.event.message.CategoryStatusChangeEvent;
import project.mall.event.model.CategoryStatusInfo;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.type.AdminCategoryService;
import project.party.PartyService;
import security.SecUser;
import security.internal.SecUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商品类型
 */
@Slf4j
@RestController
@RequestMapping("/mall/category")
public class AdminCategoryController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminCategoryController.class);

    @Resource
    protected AdminCategoryService adminCategoryService;

    @Resource
    protected PartyService partyService;
    @Resource
    protected LogService logService;
    @Resource
    protected SecUserService secUserService;
    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected AdminMallGoodsService adminMallGoodsService;

    /**
     * 管理后台商品分类列表
     */

    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {
        this.pageSize = 20;
        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String parentId = request.getParameter("parentId");
        Integer level = request.getParameter("level") == null ? 0 : Integer.parseInt(request.getParameter("level"));
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        ModelAndView model = new ModelAndView("admin_type_list");
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminCategoryService.pagedQuery(this.pageNo, this.pageSize, parentId , level, startTime, endTime);
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
        model.addObject("level",level);
        model.addObject("parentId",parentId);
        model.addObject("startTime",startTime);
        model.addObject("endTime",endTime);
        model.addObject("page",page);
        model.addObject("error",error);
        return model;
    }




//    /**
//     * 基于上级分类，下钻下级分类列表
//     *
//     * @param request
//     * @return
//     */
//    @RequestMapping("/secondary.action")
//    public ModelAndView listSecondaryCategory(HttpServletRequest request) {
//        String error = request.getParameter("error");
//        String message = request.getParameter("message");
//        // 上级分类ID
//        String parentId = request.getParameter("parentId");
//        ModelAndView model = new ModelAndView("admin_secondary_category_list");
//
//        try {
//            List subList = null;
//            if (StrUtil.isNotBlank(parentId) && !Objects.equals(parentId, "0")) {
//                subList = adminCategoryService.listSubCategory(parentId);
//            } else {
//                subList = new ArrayList();
//            }
//
//            model.addObject("subList", subList);
//        } catch (BusinessException e) {
//            model.addObject("error", error);
//            return model;
//        } catch (Throwable t) {
//            logger.error(" error ", t);
//            model.addObject("error", "[ERROR] " + t.getMessage());
//            return model;
//        }
//
//        model.addObject("pageNo",this.pageNo);
//        model.addObject("message",message);
//        model.addObject("page",page);
//        model.addObject("error",error);
//        return model;
//    }

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
        String parentId = request.getParameter("parentId");
        if (StringUtils.isEmptyString(parentId)){
            parentId = "0";
        }
        LinkedHashMap<Object, String> categoryMap = adminCategoryService.getParentCategory(null,1);
        ModelAndView model = new ModelAndView();
        model.setViewName("admin_type_add");
        model.addObject("error", error);
        model.addObject("pageNo",pageNo);
        model.addObject("categoryMap",categoryMap);
        model.addObject("parentId",parentId);
        return model;
    }


    @RequestMapping(value =  "/add.action")
    public ModelAndView add(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String rankStr = request.getParameter("rank");
        String parentId = request.getParameter("parentId");
        String name = request.getParameter("name");
        String des = request.getParameter("des_text");

        int rank = 0;
        if (StrUtil.isNotBlank(rankStr)) {
            rank = Integer.parseInt(rankStr);
        }

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("rank", rank);
        model.addObject("des", des);
        model.addObject("pageNo", pageNo);
        model.addObject("parentId", parentId);
        try {
            if (StringUtils.isEmptyString(name)) {
                throw new BusinessException("项目分类名称不能为空");
            }

            adminCategoryService.save(name, rank, des, parentId);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.addObject("categoryMap", adminCategoryService.getParentCategory(null,1));
            model.setViewName("admin_type_add");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.addObject("categoryMap", adminCategoryService.getParentCategory(null,1));
            model.setViewName("admin_type_add");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "/mall/category/list.action");
        return modelAndView;
    }


    @RequestMapping(value =  "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String categoryId = request.getParameter("categoryId");
        String lang = request.getParameter("lang");
        String message = request.getParameter("message");
        ModelAndView model = new ModelAndView();
        try {
            Category category = adminCategoryService.findById(categoryId);
            List<CategoryLang> categoryLangList = this.adminCategoryService.findLanByCategoryIds(categoryId, lang);
            if (CollectionUtils.isEmpty(categoryLangList)) {
                model.addObject("categoryLanId",null);
                model.addObject("name",null);
                model.addObject("des",null);
            } else {
                CategoryLang categoryLang = categoryLangList.get(0);
                model.addObject("categoryLanId",categoryLang.getId());
                model.addObject("name",categoryLang.getName());
                model.addObject("des",categoryLang.getDes());
            }
            LinkedHashMap<Object, String> categoryMap = adminCategoryService.getParentCategory(categoryId,1);
            model.addObject("error", error);
            model.addObject("categoryId", categoryId);
            model.addObject("parentId", category.getParentId());
            model.addObject("rank", category.getRank());
            model.addObject("recTime", category.getRecTime());
            model.addObject("status", category.getStatus());
            model.addObject("iconImg", category.getIconImg());
            model.addObject("categoryMap", categoryMap);
            model.addObject("lang",lang);
            model.addObject("pageNo",pageNo);
            model.addObject("message",message);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/category/list");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("redirect:/" +  "/mall/category/list");
            return model;
        }
        model.setViewName("admin_type_update");
        return model;
    }


    @RequestMapping(value =  "/update.action")
    public ModelAndView update(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String sort = request.getParameter("sort");
        String lang = request.getParameter("lang");
        String categoryId = request.getParameter("categoryId");
        String categoryLanId = request.getParameter("categoryLanId");
        String iconImg = request.getParameter("iconImg");
        String status = request.getParameter("status");
        String des = request.getParameter("des_text");
        String rankStr = request.getParameter("rank");
        String parentId = request.getParameter("parentId");

        int rank = 0;
        if (StrUtil.isNotBlank(rankStr)) {
            rank = Integer.parseInt(rankStr);
        } else if (StrUtil.isNotBlank(sort)) {
            rank = Integer.parseInt(sort);
        }
        if (StrUtil.isBlank(parentId) || Objects.equals(parentId, "null")) {
            parentId = "0";
        }

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("pageNo", pageNo);
        model.addObject("sort", rank);
        model.addObject("rank", rank); // 用rank取代 sort
        model.addObject("parentId", parentId);
        model.addObject("lang", lang);
        model.addObject("categoryId", categoryId);
        model.addObject("categoryLanId", categoryLanId);
        model.addObject("iconImg", iconImg);
        try {
            String error = verification(name, sort, iconImg, des);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }

            Category bean = adminCategoryService.findById(categoryId);
            if (Objects.isNull(bean)) {
                throw new BusinessException("分类已被删除");
            }
            int oriStatus = bean.getStatus();

            bean.setIconImg(iconImg);
            bean.setStatus(Integer.parseInt(status));
            bean.setSort(rank);
            bean.setRank(rank);
            bean.setParentId(parentId);
            if (parentId.equals("0")) {
                bean.setLevel(1);
            } else {
                int count = adminCategoryService.count(categoryId);
                if (count > 0){
                    throw new BusinessException("已有下级分类，不可修改为二级分类");
                }
                bean.setLevel(2);
                //修改为二级分类后 自动关闭首页推荐
                bean.setRecTime(0L);
            }
            adminCategoryService.update(bean, name, lang, categoryId, categoryLanId, des);

            if (!String.valueOf(oriStatus).equals(status)) {
                // 状态有变化
                // 发布事件，可能其他业务也需要修改相关的字段
                CategoryStatusInfo statusInfo = new CategoryStatusInfo();
                statusInfo.setCategoryId(categoryId);
                statusInfo.setOriStatus(oriStatus);
                statusInfo.setNewStatus(Integer.parseInt(status));
                WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
                wac.publishEvent(new CategoryStatusChangeEvent(this, statusInfo));
            }
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_type_update");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("admin_type_update");
            return model;
        }

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("lang", lang);
        modelAndView.addObject("categoryId", categoryId);
        modelAndView.addObject("sort", rank);
        modelAndView.addObject("rank", rank); // 用rank取代 sort
        modelAndView.addObject("parentId", parentId);
        modelAndView.addObject("categoryLanId", categoryLanId);
        modelAndView.addObject("des", des);
        modelAndView.setViewName("redirect:/" +  "/mall/category/toUpdate.action");
        return modelAndView;
    }


    private String verification(String name, String sort, String img, String des) {
        if (StringUtils.isEmptyString(name)) {
            return "请输入项目名称";
        }
        if (StringUtils.isEmptyString(sort)) {
            sort = "0";
        }
//        if (StringUtils.isEmptyString(img)) {
//            throw new BusinessException("请上传图片");
//        }
//        if (StringUtils.isEmptyString(des)) {
//            throw new BusinessException("描述不能为空");
//        }
        return null;
    }

    /**
     * 是否首页推荐
     * @param request
     * @return
     */
    @RequestMapping(value =  "/updateStatus.action")
    public ModelAndView updateStatus(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminCategoryService.updateStatus(id,Integer.parseInt(status));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/mall/category/list");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/mall/category/list");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/mall/category/list.action");
        return modelAndView;
    }


    @RequestMapping(value =  "/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        try {
            String login_safeword = request.getParameter("login_safeword");
            if (StringUtils.isNullOrEmpty(login_safeword)) {
                model.addObject("error", "请输入登录人资金密码");
                model.setViewName("redirect:/" +  "/mall/category/list.action");
                return model;
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
            checkLoginSafeword(sec, this.getUsername_login(), login_safeword);

            String categoryId = request.getParameter("baseId");
            // List<SystemGoods> systemGoodsList = adminMallGoodsService.findGoodsByCategoryId(categoryId);
            int countUnderCategory = adminMallGoodsService.getGoodsCountByCategoryId(categoryId);
            if (countUnderCategory > 0) {
                throw new BusinessException("此商品分类已被使用，请先解除绑定关系");
            }
            //List<CategoryLang> categoryLang = adminCategoryService.findLanByCategoryIds(categoryId, null);

            // caster 注释于 2023-4-9
            //this.adminCategoryService.delete(categoryLang);
            adminCategoryService.updateHideCategory(categoryId);

            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setPartyId(sec.getPartyId());
            log.setOperator(this.getUsername_login());
            logService.saveSync(log);
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "/mall/category/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/category/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "/mall/category/list.action");
            return model;
        }
    }

    @RequestMapping("/findCategoryList.action")
    public String findCategoryList(HttpServletRequest request) {
        Map<String, Object> resultMap = new HashMap<String, Object>();
        LinkedHashMap<String, String> categoryList = new LinkedHashMap<>();
        List<CategoryLang> categories = adminCategoryService.findLanByCategoryIds(null,"cn" );
        for (CategoryLang categoryLang : categories) {
            categoryList.put((String) categoryLang.getCategoryId(), categoryLang.getName());
        }

        resultMap.put("categoryList",categoryList);
        return JsonUtils.getJsonString(resultMap);
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