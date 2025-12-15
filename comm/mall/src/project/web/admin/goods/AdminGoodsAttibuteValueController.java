package project.web.admin.goods;

import kernel.exception.BusinessException;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.mall.goods.GoodsAttributeService;
import project.mall.goods.GoodsAttributeValueService;
import project.mall.goods.dto.GoodsAttributeDescDto;
import project.mall.goods.dto.GoodsAttributeValueDto;
import project.mall.goods.model.GoodsAttributeValueLang;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;


@RestController
@RequestMapping("/mall/goodAttr/value")
public class AdminGoodsAttibuteValueController extends PageActionSupport {

    @Resource
    private GoodsAttributeValueService goodsAttributeValueService;

    @Autowired
    GoodsAttributeService goodsAttributeService;

    private static Log logger = LogFactory.getLog(AdminGoodsAttibuteValueController.class);


    /**
     * 属性列表
     */
    @RequestMapping("list.action")
    public ModelAndView list(HttpServletRequest request) {

        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String categoryId = request.getParameter("categoryId");
        String categoryName = request.getParameter("categoryName");
        String attrId = request.getParameter("attrId");

        this.pageSize = 20;
        ModelAndView modelAndView = new ModelAndView("admin_attribute_value_list");
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            List<GoodsAttributeValueDto> resultList = goodsAttributeValueService.list(this.pageNo, this.pageSize, attrId);
            Page pageInfo = new Page();
            pageInfo.setElements(resultList);
            modelAndView.addObject("pageNo",this.pageNo);
            modelAndView.addObject("message", message);
            modelAndView.addObject("page", pageInfo);
            modelAndView.addObject("error", error);
            modelAndView.addObject("categoryId", categoryId);
            modelAndView.addObject("attrId", attrId);
            modelAndView.addObject("categoryName", categoryName);
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }


    /**
     * 增加规格项
     *
     * @return
     */
    @RequestMapping(value =  "/add.action")
    public ModelAndView add(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String categoryId = request.getParameter("categoryId");
        String name = request.getParameter("name");
        String attrId = request.getParameter("attrId");
        String categoryName = request.getParameter("categoryName");

        ModelAndView model = new ModelAndView();
        model.addObject("pageNo", pageNo);
        model.addObject("categoryId", categoryId);
        model.addObject("attrId", attrId);
        model.addObject("categoryName", categoryName);
        try {
            goodsAttributeValueService.saveOrUpdate(name,"en",attrId,null);

        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/goodAttr/value/list.action");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.setViewName("redirect:/" +  "/mall/goodAttr/value/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.addObject("categoryId", categoryId);
        modelAndView.addObject("categoryName", categoryName);
        modelAndView.addObject("attrId", attrId);
        modelAndView.setViewName("redirect:/" +  "/mall/goodAttr/value/list.action");
        return modelAndView;
    }


    @RequestMapping(value =  "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String categoryId = request.getParameter("categoryId");
        String lang = request.getParameter("lang");
        String message = request.getParameter("message");
        String attrId = request.getParameter("attrId");
        String attrValueId = request.getParameter("attrValueId");
        ModelAndView model = new ModelAndView();
        try {

            GoodsAttributeValueLang goodsAttributeValueLang = goodsAttributeValueService.findLangData(attrValueId,lang);
            String categoryName = goodsAttributeService.list(1, 1, categoryId).get(0).getCategoryName();
            if(Objects.isNull(goodsAttributeValueLang)){
                model.addObject("name",null);
            } else {
                model.addObject("name",goodsAttributeValueLang.getName());
            }
            model.addObject("lang",lang);
            model.addObject("pageNo",pageNo);
            model.addObject("message",message);
            model.addObject("categoryId",categoryId);
            model.addObject("attrId",attrId);
            model.addObject("attrValueId",attrValueId);
            model.addObject("categoryName",categoryName);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/attribute/value/list");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("redirect:/" +  "/mall/attribute/value/list");
            return model;
        }
        model.setViewName("admin_attribute_value_update");
        return model;
    }


    @RequestMapping(value =  "/update.action")
    public ModelAndView update(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String categoryId = request.getParameter("categoryId");
        String lang = request.getParameter("lang");
        String attrId = request.getParameter("attrId");
        String attrValueId = request.getParameter("attrValueId");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("pageNo", pageNo);
        model.addObject("lang", lang);
        model.addObject("categoryId", categoryId);
        model.addObject("attrId", attrId);
        model.addObject("attrValueId", attrValueId);
        try {
            goodsAttributeValueService.saveOrUpdate(name,lang,attrId,attrValueId);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_attribute_value_update");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("admin_attribute_value_update");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        model.setViewName("redirect:/" +  "/mall/goodAttr/value/toUpdate.action");
        return model;
    }


    /**
     * 删除
     *
     * @return
     */
    @RequestMapping(value =  "/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.addObject("categoryId",request.getParameter("categoryId"));
        model.addObject("attrId",request.getParameter("attrId"));
        model.addObject("categoryName",request.getParameter("categoryName"));
        try {
            goodsAttributeValueService.delete(request.getParameter("id"));
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "/mall/goodAttr/value/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/goodAttr/value/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "/mall/goodAttr/value/list.action");
            return model;
        }
    }

}
