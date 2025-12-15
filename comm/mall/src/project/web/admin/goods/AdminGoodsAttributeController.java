package project.web.admin.goods;

import kernel.exception.BusinessException;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.mall.goods.GoodsAttributeCategoryService;
import project.mall.goods.GoodsAttributeService;
import project.mall.goods.dto.GoodsAttributeDescDto;
import project.mall.goods.dto.GoodsAttributeDto;
import project.mall.goods.model.GoodsAttribute;
import project.mall.goods.model.GoodsAttributeLang;
import project.web.api.model.GoodAttrListModel;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 属性
 */

@Slf4j
@RestController
@CrossOrigin
@RequestMapping("/mall/goodAttr/")
public class AdminGoodsAttributeController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminGoodsAttributeController.class);

    @Autowired
    GoodsAttributeService goodsAttributeService;

    @Autowired
    GoodsAttributeCategoryService goodsAttributeCategoryService;


    /**
     * 属性列表
     */
    @RequestMapping("list.action")
    public ModelAndView list(HttpServletRequest request, GoodAttrListModel model) {

        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String categoryName = request.getParameter("categoryName");

        if (model.getPageNo() == 0) {
            model.setPageNo(1);
        }
        this.pageSize = 20;
        ModelAndView modelAndView = new ModelAndView("admin_attribute_list");
        try {
             List<GoodsAttributeDto> resultList = goodsAttributeService.list(model.getPageNo(), this.pageSize, model.getCategoryId());
            Page pageInfo = new Page();
            pageInfo.setElements(resultList);
            pageInfo.setTotalElements(goodsAttributeService.getCount(model.getCategoryId()));
            pageInfo.setThisPageNumber(model.getPageNo());
            pageInfo.setPageSize(model.getPageSize());
            modelAndView.addObject("pageNo", model.getPageNo());
            modelAndView.addObject("message", message);
            modelAndView.addObject("page", pageInfo);
            modelAndView.addObject("error", error);
            modelAndView.addObject("categoryName", categoryName);
            modelAndView.addObject("categoryId", model.getCategoryId());
        } catch (Exception e) {
            modelAndView.addObject("error", e.getMessage());
        }
        return modelAndView;
    }


    /**
     * 增加属性
     *
     * @return
     */
    @RequestMapping(value =  "/add.action")
    public ModelAndView add(HttpServletRequest request, GoodsAttributeDescDto attributeDescDtoMap) {
        String pageNo = request.getParameter("pageNo");
        String categoryId = request.getParameter("categoryId");

        ModelAndView model = new ModelAndView();
        model.addObject("pageNo", pageNo);
        model.addObject("param", attributeDescDtoMap);
        model.addObject("categoryId", categoryId);
        model.addObject("param", attributeDescDtoMap);
        try {
            if (goodsAttributeService.queryExistBySortAndCategoryId(attributeDescDtoMap.getSort(),attributeDescDtoMap.getCategoryId())){
                throw new BusinessException("排序已存在");
            }
            else {
                goodsAttributeService.saveAndUpdate(null,attributeDescDtoMap.getName(),"en",categoryId,attributeDescDtoMap.getSort());
            }

        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/goodAttr/list.action");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.setViewName("redirect:/" +  "/mall/goodAttr/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.addObject("categoryId", categoryId);
        modelAndView.setViewName("redirect:/" +  "/mall/goodAttr/list.action");
        return modelAndView;
    }


    @RequestMapping(value =  "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String categoryId = request.getParameter("categoryId");
        String lang = request.getParameter("lang");
        String message = request.getParameter("message");
        String attrId = request.getParameter("attrId");
        ModelAndView model = new ModelAndView();
        try {

            GoodsAttribute goodsAttribute = goodsAttributeService.findGoodsAttributeById(attrId);
            GoodsAttributeLang goodsAttributeLang = goodsAttributeService.findAttributeLangById(attrId,lang);
            String categoryName = goodsAttributeService.list(1, 1, categoryId).get(0).getCategoryName();
            if(Objects.isNull(goodsAttributeLang)){
                model.addObject("name",null);
            } else {
                model.addObject("name",goodsAttributeLang.getName());
            }
            model.addObject("lang",lang);
            model.addObject("sort",goodsAttribute.getSort());
            model.addObject("pageNo",pageNo);
            model.addObject("message",message);
            model.addObject("categoryId",categoryId);
            model.addObject("attrId",attrId);
            model.addObject("categoryName",categoryName);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/attribute/list");
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("redirect:/" +  "/mall/attribute/list");
            return model;
        }
        model.setViewName("admin_attribute_update");
        return model;
    }


    @RequestMapping(value =  "/update.action")
    public ModelAndView update(HttpServletRequest request, GoodsAttributeDescDto goodsAttributeDescDto) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String sort = request.getParameter("sort");
        String categoryId = request.getParameter("categoryId");
        String categoryName = request.getParameter("categoryName");
        String lang = request.getParameter("lang");
        String attrId = request.getParameter("attrId");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("pageNo", pageNo);
        model.addObject("sort", sort);
        model.addObject("lang", lang);
        model.addObject("categoryId", categoryId);
        model.addObject("attrId", attrId);
        model.addObject("categoryName", categoryName);
        try {
            goodsAttributeService.saveAndUpdate(attrId,name,lang,categoryId,goodsAttributeDescDto.getSort());
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_attribute_update");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("admin_attribute_update");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        model.setViewName("redirect:/" +  "/mall/goodAttr/toUpdate.action");
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
        try {
            goodsAttributeService.removeById(request.getParameter("id"));
            model.addObject("message", "操作成功");
            model.setViewName("redirect:/" +  "/mall/goodAttr/list.action");
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/goodAttr/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "/mall/goodAttr/list.action");
            return model;
        }
    }

}
