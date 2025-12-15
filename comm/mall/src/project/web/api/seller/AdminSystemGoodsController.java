package project.web.api.seller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.LanguageEnum;
import project.mall.MallRedisKeys;
import project.mall.goods.AdminMallGoodsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.*;
import project.mall.type.CategoryService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.utils.MallPageInfo;
import project.redis.RedisHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 商户后台商品管理
 */
@RestController
@CrossOrigin
public class AdminSystemGoodsController extends BaseAction {
    private final String action = "/seller/systemGoods!";

    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    protected AdminMallGoodsService adminMallGoodsService;
    @Resource
    CategoryService categoryService;

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);
        String categoryId = request.getParameter("categoryId");
        String secondaryCategoryId = request.getParameter("secondaryCategoryId");
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        String sellerId = this.getLoginPartyId();
        if (sellerId == null) {
            sellerId = request.getParameter("sellerId");
        }
//        Integer isHot = null;
//        if (isHotStr != null) {
//            isHot = Integer.valueOf(isHotStr);
//        }
//        String categoryId = null;

        if (StrUtil.isNotBlank(categoryId) && !Objects.equals(categoryId, "0")) {
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                if (StrUtil.isBlank(category.getParentId()) || Objects.equals(category.getParentId(), "0")) {
                    // 前端传的是一级分类
                    secondaryCategoryId = "0";
                } else {
                    // 前端传的是二级分类
                    categoryId = category.getParentId();
                    secondaryCategoryId = category.getId().toString();
                }
            }
        }

        JSONArray jsonArray = new JSONArray();
        // 待取代 TODO 新版本无需重复查询
        //MallPageInfo mallPageInfo  = sellerGoodsService.listSystemGoods1(pageInfo.getPageNum(), pageInfo.getPageSize(),  categoryId, secondaryCategoryId, lang,sellerId,name,id);
        MallPageInfo mallPageInfo  = sellerGoodsService.listSystemGoods(pageInfo.getPageNum(), pageInfo.getPageSize(),  categoryId, secondaryCategoryId, lang,sellerId,name,id);
        if (CollectionUtil.isEmpty(mallPageInfo.getElements()) && !lang.equals(LanguageEnum.EN.getLang())){
            mallPageInfo = sellerGoodsService.listSystemGoods(pageInfo.getPageNum(), pageInfo.getPageSize(), categoryId, secondaryCategoryId, LanguageEnum.EN.getLang(), sellerId, name, id);
        }
        List<SystemGoods> list = mallPageInfo.getElements();
        if (CollectionUtil.isNotEmpty(list)){
            for (SystemGoods pl : list) {

                CategoryLang categoryLang = this.categoryService.selectLang(lang, pl.getCategoryId());
                CategoryLang secondaryCategoryLang = this.categoryService.selectLang(lang, pl.getSecondaryCategoryId());
                SystemGoodsLang pLang = this.sellerGoodsService.selectGoodsLang(lang, pl.getId());
                if (null == categoryLang || null == pLang || pLang.getType() == 1) {
                    continue;
                }
                SystemGoodsVo systemGoodsVo = new SystemGoodsVo();
                BeanUtils.copyProperties(pl, systemGoodsVo);
                systemGoodsVo.setSecondaryCategoryName(null == secondaryCategoryLang ? "" :secondaryCategoryLang.getName());
                systemGoodsVo.setName(pLang.getName());
                systemGoodsVo.setUnit(pLang.getUnit());
                systemGoodsVo.setDes(pLang.getDes());
                systemGoodsVo.setImgDes(pLang.getImgDes());
                systemGoodsVo.setCategoryName(categoryLang.getName());
                jsonArray.add(systemGoodsVo);
            }
        }

        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 删除商品库商品
     * @param request
     * @return
     */
    @PostMapping(action+"delete.action")
    public Object delete(HttpServletRequest request){
        ResultObject object = new ResultObject();
        object = this.readSecurityContextFromSession(object);
        if (!"0".equals(object.getCode())) {
            return object;
        }
        String sellerId = request.getParameter("sellerId");
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        if (StringUtils.isEmptyString(sellerId) || StringUtils.isEmptyString(sellerGoodsId)){
            object.setCode("1");
            object.setMsg("缺少必要参数");
        }
        this.sellerGoodsService.deleteSellerGoods(sellerGoodsId, sellerId);
        return object;
    }

    @PostMapping(action + "search-keyword.action")
    public Object searchKeyword(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String lang = this.getLanguage(request);
        PageInfo pageInfo = getPageInfo(request);
        String keyword = request.getParameter("keyword");
//        String categoryId = request.getParameter("categoryId");
        if (org.apache.commons.lang3.StringUtils.isBlank(keyword)) {
            resultObject.setCode("1");
            resultObject.setMsg("请输入搜索关键字");
            return resultObject;
        }
        // 有疑问！ TODO
        List<SystemGoods> goodsList = sellerGoodsService.queryAdminSearchGoods(pageInfo.getPageNum(), pageInfo.getPageSize(),keyword,lang);
//        List<SystemGoodsLang> goodsLongList = sellerGoodsService.searchKeyword(lang, keyword);
        List<SystemGoodsVo> goodsVoList = new ArrayList<>();
        for (SystemGoods systemGoods : goodsList){
//            SystemGoods systemGoods = adminMallGoodsService.findById(systemGoodsLang.getGoodsId());
////            if(!categoryId.equals(systemGoods.getCategoryId())){
////                continue;
////            }
            String key = MallRedisKeys.TYPE_LANG + lang + ":" + systemGoods.getCategoryId();
            String jsx = redisHandler.getString(key);
            if (StringUtils.isEmptyString(jsx)) {
                continue;
            }
            CategoryLang cLang = JSONArray.parseObject(jsx, CategoryLang.class);
            String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + systemGoods.getId());
            if (StringUtils.isEmptyString(js)) {
                continue;
            }
            SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
            if (pLang.getType() == 1) {
                continue;
            }
            SystemGoodsVo systemGoodsVo = new SystemGoodsVo();
            BeanUtils.copyProperties(systemGoods,systemGoodsVo);
            systemGoodsVo.setName(pLang.getName());
            systemGoodsVo.setUnit(pLang.getUnit());
            systemGoodsVo.setDes(pLang.getDes());
            systemGoodsVo.setImgDes(pLang.getImgDes());
            systemGoodsVo.setCategoryName(cLang.getName());
            goodsVoList.add(systemGoodsVo);
        }
        JSONObject object = new JSONObject();
        object.put("goodsList", goodsVoList);
        resultObject.setData(object);
        return resultObject;
    }
}
