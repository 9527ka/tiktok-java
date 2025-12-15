package project.web.admin.goods;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.util.LinkedCaseInsensitiveMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.log.LogService;
import project.mall.auto.AutoConfig;
import project.mall.comment.AdminSystemCommentService;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.*;
import project.mall.goods.dto.GoodAttrDto;
import project.mall.goods.model.*;
import project.mall.type.AdminCategoryService;
import project.mall.type.model.CategoryLang;
import project.mall.utils.IdModel;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.web.admin.dto.DeleteSku;
import project.web.admin.dto.SystemGoodsDto;
import project.web.admin.model.AttrCategoryListModel;
import project.web.admin.model.SystemCommentModel;
import project.web.admin.model.SystemGoodModel;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 商品库管理
 */
@RestController
@RequestMapping("/systemGoods")
@CrossOrigin
public class AdminSystemGoodsController extends PageActionSupport {
    private static Log logger = LogFactory.getLog(AdminSystemGoodsController.class);

    @Resource
    protected AdminMallGoodsService adminMallGoodsService;

    @Resource
    protected AdminCategoryService adminCategoryService;

    @Resource
    protected GoodsAttributeCategoryService goodsAttributeCategoryService;

    @Resource
    protected GoodsAttributeService goodsAttributeService;

    @Resource
    protected PartyService partyService;
    @Resource
    protected LogService logService;
    @Resource
    protected SecUserService secUserService;
    @Resource
    protected GoodsSkuAtrributionService goodsSkuAtrributionService;

    @Resource
    AdminSystemCommentService adminSystemCommentService;

    @Resource
    EvaluationService evaluationService;

    @Resource
    GoodsAttributeValueService goodsAttributeValueService;

    @Resource
    SellerGoodsService sellerGoodsService ;

    /**
     * 商品列表
     *
     * @param request
     * @return
     */
    @PostMapping("/list.action")
    public ResultObject list(HttpServletRequest request) {

        this.pageSize = 20;
        Integer isShelf = null;
        Integer updateStatus = null;
        ResultObject resultObject = new ResultObject();
        String name = request.getParameter("name");
        String categoryId = request.getParameter("categoryId");
        String secondaryCategoryId = request.getParameter("secondaryCategoryId");
        String updateStatusPara = request.getParameter("updateStatus");
        PageInfo pageInfo = getPageInfo(request);
        String id = request.getParameter("id");
        if (StringUtils.isNotEmpty(request.getParameter("isShelf"))) {
            isShelf = Integer.valueOf(request.getParameter("isShelf"));
        }
        if (StringUtils.isNotEmpty(updateStatusPara)) {
            updateStatus = Integer.valueOf(request.getParameter("updateStatus"));
        }
        try {
            // 待取代 TODO
            // Page page = adminMallGoodsService.pageQuery1(pageInfo.getPageNum(), pageInfo.getPageSize(), name, isShelf, categoryId, secondaryCategoryId, id, updateStatus);
            Page page = adminMallGoodsService.pageQuery(pageInfo.getPageNum(), pageInfo.getPageSize(), name, isShelf, categoryId, secondaryCategoryId, id, updateStatus);
            resultObject.setData(page);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    @PostMapping("/delete.action")
    public ResultObject delete(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        try {
            String id = request.getParameter("id");
            if (StrUtil.isEmpty(id)) {
                resultObject.setCode("1");
                resultObject.setMsg("参数错误!");
            }
            List<String> list = StrUtil.split(id, ',');
            for (String d : list) {
                List<SystemGoodsLang> lanByGoodsId = adminMallGoodsService.findLanByGoodsId(d, null);
                this.adminMallGoodsService.delete(d, lanByGoodsId);
//                关联店铺商品设置为无效
                this.sellerGoodsService.deleteSellerGoodsBySystemGoodId(d);
            }
            resultObject.setData(page);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    /**
     * 更新上架状态
     *
     * @param request
     * @return
     */
    @PostMapping("/updateShelf.action")
    public ResultObject updateShelf(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        try {
            String id = request.getParameter("id");
            Integer isShelf = null;
            if (StringUtils.isNotEmpty(request.getParameter("isShelf"))) {
                isShelf = Integer.valueOf(request.getParameter("isShelf"));
            }
            adminMallGoodsService.updateShelf(id, isShelf);
            resultObject.setData(page);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }


    @PostMapping("/updateUpdateStatus.action")
    public ResultObject updateUpdateStatus(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        try {
            String id = request.getParameter("id");
            Integer updateStatus = null;
            if (StringUtils.isNotEmpty(request.getParameter("updateStatus"))) {
                updateStatus = Integer.valueOf(request.getParameter("updateStatus"));
            }
            adminMallGoodsService.updateUpdateStatus(id, updateStatus);
            resultObject.setData(page);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    /**
     * 获取商品详情
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/getDesc.action")
    @ResponseBody
    public ResultObject getDesc(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String goodsId = request.getParameter("goodsId");
        String lang = request.getParameter("lang");
        if (StringUtils.isEmpty(lang) || StringUtils.isEmpty(goodsId)) {
            resultObject.setCode("1");
            resultObject.setMsg("参数错误");
            return resultObject;
        }

        SystemGoods goods = this.adminMallGoodsService.findById(goodsId);
        List<SystemGoodsLang> goodsLanList = adminMallGoodsService.findLanByGoodsId(goodsId, lang);
        SystemGoodsDto systemGoodsDto = new SystemGoodsDto();
        systemGoodsDto.setId(goods.getId());
        BeanUtils.copyProperties(goods, systemGoodsDto);
        if (CollectionUtils.isNotEmpty(goodsLanList)) {
            SystemGoodsLang systemGoodsLang = goodsLanList.get(0);
            systemGoodsDto.setName(systemGoodsLang.getName());
            systemGoodsDto.setDes(systemGoodsLang.getDes());
            systemGoodsDto.setImgDes(systemGoodsLang.getImgDes());
            systemGoodsDto.setUnit(systemGoodsLang.getUnit());
        }

        systemGoodsDto.setGoodSkuAttrDto(goodsSkuAtrributionService.getGoodsAttrListSku(goods.getId().toString(), lang));
        resultObject.setData(systemGoodsDto);
        return resultObject;
    }

    /**
     * 获取商品分类
     *
     * @param request
     * @return
     */
    @PostMapping(value = "/getGoodCategory.action")
    @ResponseBody
    public ResultObject getGoodCategory(HttpServletRequest request) {

        String lang = request.getParameter("lang");
        ResultObject resultObject = new ResultObject();
        if (StringUtils.isEmpty(lang)) {
            resultObject.setCode("1");
            resultObject.setMsg("参数错误");
            return resultObject;
        }
        List<CategoryLang> langs = adminCategoryService.findLanByCategoryId(null, lang);
        resultObject.setData(langs);
        return resultObject;
    }

    @PostMapping("/delete/sku")
    @ResponseBody
    public ResultObject deleteSku(String skuId){
        ResultObject object = new ResultObject();
        this.adminMallGoodsService.deleteSku(skuId);
        return object;
    }

    /**
     * 更新商品
     *
     * @param systemGoodModel
     * @return
     */
    @PostMapping(value = "/update.action")
    @ResponseBody
    public ResultObject update(@RequestBody SystemGoodModel systemGoodModel) {

        ResultObject resultObject = new ResultObject();
        try {
            adminMallGoodsService.update(systemGoodModel);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("更新系统失败失败",e);
            return resultObject;
        }
        return resultObject;
    }

    /**
     * 根据属性分类获取规则
     *
     * @return
     */
    @PostMapping(value = "/getAttrCategoryList.action")
    @ResponseBody
    public ResultObject getAttrCategoryList(AttrCategoryListModel model) {

        ResultObject resultObject = new ResultObject();
        try {
            List<GoodAttrDto> goodAttrDtoList = goodsAttributeService.findByCategoryId(model.getCategoryId(), model.getLang());
            resultObject.setData(goodAttrDtoList);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    /**
     * 根据属性分类
     *
     * @return
     */
    @PostMapping(value = "/getAllAttributeCategory.action")
    @ResponseBody
    public ResultObject getAllAttributeCategory() {

        ResultObject resultObject = new ResultObject();
        try {
            List<GoodsAttributeCategory> list = goodsAttributeCategoryService.findAllAttributeCategory();
            resultObject.setData(list);
        } catch (Exception e) {
            resultObject.setCode("1");
            logger.error(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    /**
     * 增加属性参数接口
     *
     * @return
     */
    @PostMapping(value = "/addAttributeValue.action")
    @ResponseBody
    public ResultObject addAttributeValue(@RequestParam String attrId, @RequestParam String name, @RequestParam String lang) {

        ResultObject resultObject = new ResultObject();
        try {
            String id = goodsAttributeService.saveAttrValue(attrId, name, lang);
            Map<String, String> map = new HashMap<>();
            map.put("id", id);
            resultObject.setData(map);
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e) {
            resultObject.setCode("1");
            logger.error(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    /***
     * 获取系统评价
     * @return
     */
    @PostMapping(value = "/listSystemComment.action")
    public ResultObject listSystemComment(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        try {
            PageInfo pageInfo = getPageInfo(request);
            Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
            String systemGoodId = request.getParameter("systemGoodId");
            if (StrUtil.isEmpty(systemGoodId)){
                resultObject.setCode("1");
                resultObject.setMsg("参数错误!");
                return  resultObject;
            }

            Page page = adminSystemCommentService.listComment(pageInfo.getPageNum(), pageInfo.getPageSize(), status,systemGoodId);
            resultObject.setData(page);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }



    @PostMapping(value = "/addUpdateSystemComment.action")
    public ResultObject addUpdateSystemComment(SystemCommentModel model) {

        ResultObject resultObject = new ResultObject();
        try {
             adminSystemCommentService.saveUpdate(model);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }


    @PostMapping(value = "/updateSystemCommentStatus.action")
    public ResultObject updateSystemCommentStatus(@RequestParam  String id ,@RequestParam int status) {

        ResultObject resultObject = new ResultObject();
        try {
            adminSystemCommentService.updateStatus(id,status);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }

    @PostMapping(value = "/deleteSystemComment.action")
    public ResultObject deleteSystemComment(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        try {
            String id = request.getParameter("id");
            if (StrUtil.isEmpty(id)) {
                resultObject.setCode("1");
                resultObject.setMsg("参数错误!");
                return  resultObject;
            }
            adminSystemCommentService.deleteAll(id);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }


    /***
     * 获取会员评价
     * @return
     */
    @PostMapping(value = "/listGoodComment.action")
    @ResponseBody
    public ResultObject listGoodComment(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        try {
            PageInfo pageInfo = getPageInfo(request);
            String sellerGoodsId = request.getParameter("sellerGoodsId");
            if (StrUtil.isEmpty(sellerGoodsId)) {
                resultObject.setCode("1");
                resultObject.setMsg("参数错误!");
                return  resultObject;
            }
            String userName = request.getParameter("userName");
            String evaluationType = request.getParameter("evaluationType");
            MallPageInfo mallPageInfo = evaluationService.listEvaluations(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerGoodsId, userName, evaluationType);
            Page page = new Page();
            page.setElements(mallPageInfo.getElements());
            page.setTotalElements(mallPageInfo.getTotalElements());
            resultObject.setData(page);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }




    @PostMapping(value = "/deleteAttrValue.action")
    public ResultObject deleteAttrValue(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        try {
            String id = request.getParameter("id");
            if (StrUtil.isEmpty(id)) {
                resultObject.setCode("1");
                resultObject.setMsg("参数错误!");
                return  resultObject;
            }
            goodsAttributeValueService.delete(id);
        } catch (Exception e) {
            resultObject.setCode("1");
            e.printStackTrace();
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }
        return resultObject;
    }




}
