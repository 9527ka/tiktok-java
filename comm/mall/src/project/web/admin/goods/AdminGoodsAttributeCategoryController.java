package project.web.admin.goods;

import kernel.exception.BusinessException;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.mall.goods.GoodsAttributeCategoryService;
import project.mall.goods.dto.GoodsAttributeCategoryDto;
import project.web.api.model.GoodAttrCategoryAddModel;
import project.web.api.model.GoodAttrCategoryListModel;
import security.SecUser;
import security.internal.SecUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 属性
 */

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/mall/goodAttrCategory/")
public class AdminGoodsAttributeCategoryController extends PageActionSupport {

    @Autowired
    GoodsAttributeCategoryService goodsAttributeCategoryService;

    @Resource
    protected SecUserService secUserService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    private static Log logger = LogFactory.getLog(AdminGoodsAttributeCategoryController.class);

    /**
     * 属性分类列表
     *
     * @return
     */
    @RequestMapping("list.action")
    public ModelAndView list(HttpServletRequest request, GoodAttrCategoryListModel model) {

        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String names = request.getParameter("names");

        if (model.getPageNo() == 0) {
            model.setPageNo(1);
        }
        ModelAndView modelAndView = new ModelAndView("admin_attribute_category_list");
        try {
            List<GoodsAttributeCategoryDto> resultList = goodsAttributeCategoryService.list(model.getPageNo(), 20, names);
            Page pageInfo = new Page();
            pageInfo.setElements(resultList);
            pageInfo.setTotalElements(goodsAttributeCategoryService.getCount());
            pageInfo.setThisPageNumber(model.getPageNo());
            pageInfo.setPageSize(model.getPageSize());
            modelAndView.addObject("pageNo", model.getPageNo());
            modelAndView.addObject("message", message);
            modelAndView.addObject("page", pageInfo);
            modelAndView.addObject("error", error);
            modelAndView.addObject("names", names);
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }

//    @RequestMapping("toAdd.action")
//    public ModelAndView toAdd() {
//
//        ModelAndView modelAndView = new ModelAndView("admin_type_list");
//        return modelAndView;
//    }


//    @RequestMapping( "add.action")
//    public ModelAndView add(GoodAttrCategoryAddModel model) {
//
//        goodsAttributeCategoryService.save(model.getName(), model.getSort());
//        ModelAndView modelAndView = new ModelAndView("admin_type_list");
//        modelAndView.addObject("message", "操作成功");
//        return modelAndView;
//    }

    /**
     * 属性分类
     *
     * @param
     * @return
     */
    @RequestMapping(value = "add.action")
    public ModelAndView add(HttpServletRequest request, GoodAttrCategoryAddModel model) {
        String pageNo = request.getParameter("pageNo");
        ModelAndView m = new ModelAndView();
        m.addObject("pageNo", pageNo);
        try {
            goodsAttributeCategoryService.save(model.getName(), model.getSort());
        } catch (BusinessException e) {
            m.addObject("error", e.getMessage());
            m.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
            return m;
        } catch (Exception e) {
            logger.error("error ", e);
            m.setViewName("admin_goods_add");
            m.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
            return m;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
        return modelAndView;
    }

    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value =  "/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();

        try {
            goodsAttributeCategoryService.deleteById(request.getParameter("id"));
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
            return model;
        }
    }


    /**
     * 修改属性分类
     *
     * @param
     * @return
     */
    @RequestMapping("update.action")
    public ModelAndView update(HttpServletRequest request, GoodAttrCategoryAddModel model) {

        String pageNo = request.getParameter("pageNo");
        ModelAndView m = new ModelAndView();
        m.addObject("pageNo", pageNo);
        try {
            goodsAttributeCategoryService.updateById(model.getId(), model.getName(), model.getSort());
        } catch (BusinessException e) {
            m.addObject("error", e.getMessage());
            m.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
            return m;
        } catch (Exception e) {
            logger.error("error ", e);
            m.setViewName("admin_goods_add");
            m.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
            return m;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.setViewName("redirect:/" + "mall/goodAttrCategory/list.action");
        return modelAndView;

    }


    protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
        String sysSafeword = secUser.getSafeword();
        String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("登录人资金密码错误");
        }
    }
}