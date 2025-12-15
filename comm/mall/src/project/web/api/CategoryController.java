package project.web.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ext.translate.GoogleTranslateUtil;
import ext.translate.Locales;
import ext.translate.TranslateLocale;
import kernel.util.JsonUtils;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
//import org.apache.commons.logging.Log;
//import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.LanguageEnum;
import project.mall.MallRedisKeys;
import project.mall.goods.SellerGoodsService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.type.vo.CategoryVO;
import project.redis.RedisHandler;
import project.mall.type.CategoryService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
public class CategoryController extends BaseAction {
    // org.apache.commons.logging.Log 和 org.apache.commons.logging.LogFactory 不打印日志
    // private static Log logger = LogFactory.getLog(CategoryController.class);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Resource
    protected RedisHandler redisHandler;

    @Resource
    protected CategoryService categoryService;

    @Resource
    protected SellerGoodsService sellerGoodsService;

    private final String action = "/api/category!";

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);
        JSONArray jsonArray = new JSONArray();
        List<Category> categoryList = categoryService.listCategory(pageInfo.getPageNum(), pageInfo.getPageSize());
        //logger.error("======> 当前商品分类分页记录为: " + JSON.toJSON(categoryList));
        for (Category category : categoryList) {
            JSONObject o = new JSONObject();
            String key = MallRedisKeys.TYPE_LANG + lang + ":" + category.getId().toString();
            String js = redisHandler.getString(key);
            if (StrUtil.isBlank(js)) {
                //logger.error("=====> 商品分类:" + key + " 的在语言:" + lang + " 下的缓存数据为空");
                if (lang.equals("cn")) {
                    // 连中文语言都没有配置，认为该分类是无效分类
                    continue;
                }

                String tmpLangKey = MallRedisKeys.TYPE_LANG + "cn:" + category.getId().toString();
                js = redisHandler.getString(tmpLangKey);
                if (StrUtil.isBlank(js)) {
                    //logger.error("=====> 商品分类:" + key + " 的多语言缓存数据为空");
                    continue;
                }
            }

            CategoryLang pLang = JSONArray.parseObject(js, CategoryLang.class);
            o.put("name", pLang.getName());
            o.put("categoryId", pLang.getCategoryId());
            o.put("iconImg", category.getIconImg());
            o.put("des", pLang.getDes());
            o.put("sort", category.getSort());
            jsonArray.add(o);
        }
        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "recommend.action")
    public Object listRecommend(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
//        String lang = this.getLanguage(request);
        String lang = request.getParameter("lang");
        if (kernel.util.StringUtils.isEmptyString(lang)) {
            lang = project.invest.LanguageEnum.EN.getLang();
        }
        JSONArray jsonArray = new JSONArray();
        List<Category> categoryList = categoryService.listRecommendCategory(pageInfo.getPageNum(), pageInfo.getPageSize());
        for (Category category : categoryList) {
            JSONObject o = new JSONObject();
            String key = MallRedisKeys.TYPE_LANG + lang + ":" + category.getId().toString();
            String js = redisHandler.getString(key);
            if (StringUtils.isEmptyString(js)) {
               //logger.error("=====> 商品分类:" + key + " 的在语言:" + lang + " 下的缓存数据为空");
                if (lang.equals("en")) {
                    // 连中文语言都没有配置，认为该分类是无效分类
                    continue;
                }

                String tmpLangKey = MallRedisKeys.TYPE_LANG + "en:" + category.getId().toString();
                js = redisHandler.getString(tmpLangKey);
                if (StrUtil.isBlank(js)) {
                    //logger.error("=====> 商品分类:" + key + " 的多语言缓存数据为空");
                    continue;
                }
            }

            CategoryLang pLang = JSONArray.parseObject(js, CategoryLang.class);

            o.put("name", pLang.getName());
            o.put("categoryId", pLang.getCategoryId());
            o.put("iconImg", category.getIconImg());
            //o.put("des", pLang.getDes());
            o.put("sort", category.getSort());
            if("en".equals(pLang.getLang()) && !lang.equals(pLang.getLang())){
                // 翻译对应语言
                o.put("name", TranslateLocale.get(pLang.getName(), Locales.LANG_EN,lang));
            }
            jsonArray.add(o);
        }
        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 列出有效的全部一级商品分类信息.
     *
     * @param request
     * @return
     */
    @PostMapping(action + "listTop.action")
    public Object listTopLevelCategory(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String lang = this.getLanguage(request);

        List<CategoryVO> retCategoryList = new ArrayList<>();
        List<Category> categoryList = categoryService.listTopLevelCategorys();
        for (Category category : categoryList) {
            String key = MallRedisKeys.TYPE_LANG + lang + ":" + category.getId().toString();
            String js = redisHandler.getString(key);
            if (StrUtil.isBlank(js)) {
                logger.error("=====> 商品分类:" + key + " 的在语言:" + lang + " 下的缓存数据为空");
                if (lang.equals("cn")) {
                    // 连中文语言都没有配置，认为该分类是无效分类
                    continue;
                }

                // 根据分类多语言配置规律，至少应该配置简体中文语言的，如果中文语言都没有配置，则可以直接忽略掉该分类记录
                String tmpLangKey = MallRedisKeys.TYPE_LANG + "cn:" + category.getId().toString();
                js = redisHandler.getString(tmpLangKey);
                if (StrUtil.isBlank(js)) {
                    logger.error("=====> 商品分类:" + key + " 的多语言缓存数据为空");
                    continue;
                }
            }

            CategoryVO categoryVo = new CategoryVO();
            BeanUtil.copyProperties(category, categoryVo);
            categoryVo.setId(category.getId().toString());

            CategoryLang pLang = JSONArray.parseObject(js, CategoryLang.class);
            categoryVo.setName(pLang.getName());
            categoryVo.setCategoryId(categoryVo.getId());
            categoryVo.setDes(pLang.getDes());
            categoryVo.setSort(category.getRank());

            retCategoryList.add(categoryVo);
        }

        resultObject.setData(retCategoryList);
        return resultObject;
    }

    /**
     * 将所有有效商品分类以树形格式提取出来
     *
     * @param request
     * @return
     */
    @GetMapping(action + "tree.action")
    public Object categoryTree(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
//        String lang = this.getLanguage(request);
        String lang = request.getParameter("lang");
        if (StringUtils.isEmptyString(lang)) {
            lang = LanguageEnum.EN.getLang();
        }
        String showHidden = request.getParameter("showHidden");
        if (StrUtil.isBlank(showHidden)) {
            // status = 0 的分类记录不展示
            // 默认无效分类不展示
            showHidden = "0";
        }
        //logger.info("=====> 商品分类:categoryTreecategoryTreecategoryTreecategoryTree");
        List<CategoryVO> categoryList = categoryService.getCategoryTree(Objects.equals(showHidden, "1"));
        //logger.error("======> 当前商品分类分页记录为: " + JSON.toJSON(categoryList));
        List<CategoryVO> validCategoryList = new ArrayList<>();
        for (CategoryVO category : categoryList) {
            String key = MallRedisKeys.TYPE_LANG + lang + ":" + category.getId().toString();
            String js = redisHandler.getString(key);
            if (StrUtil.isBlank(js)) {
                //logger.error("=====> 商品分类:" + key + " 的在语言:" + lang + " 下的缓存数据为空");
                if (lang.equals("en")) {
                    // 连中文语言都没有配置，认为该分类是无效分类
                    continue;
                }

                // 根据分类多语言配置规律，至少应该配置简体中文语言的，如果中文语言都没有配置，则可以直接忽略掉该分类记录
                String tmpLangKey = MallRedisKeys.TYPE_LANG + "en:" + category.getId().toString();
                js = redisHandler.getString(tmpLangKey);
                if (StrUtil.isBlank(js)) {
                    logger.error("=====> 商品分类:" + key + " 的多语言缓存数据为空");
                    // 连中文语言都没有配置，认为该分类是无效分类
                    continue;
                }
            }

            CategoryLang pLang = JSONArray.parseObject(js, CategoryLang.class);
            category.setName(pLang.getName());
            category.setCategoryId(category.getId());
            //category.setDes(pLang.getDes());
            category.setSort(category.getRank());

            if("en".equals(pLang.getLang()) && !lang.equals(pLang.getLang())){
                // 翻译对应语言
               category.setName(Locales.translate(category.getName(), Locales.LANG_EN,lang));
            }
            validCategoryList.add(category);
            // 二级分类也要处理
            if (CollectionUtil.isEmpty(category.getSubList())) {
                continue;
            }

            List<CategoryVO> oriSubCategoryList = category.getSubList();
            List<CategoryVO> validSubCategoryList = new ArrayList<>();
            category.setSubList(validSubCategoryList);
            for (CategoryVO subCategory : oriSubCategoryList) {
                key = MallRedisKeys.TYPE_LANG + lang + ":" + subCategory.getId().toString();
                js = redisHandler.getString(key);
                if (StrUtil.isBlank(js)) {
                   // logger.error("=====> 商品分类:" + key + " 的在语言:" + lang + " 下的缓存数据为空");
                    if (lang.equals("en")) {
                        // 连中文语言都没有配置，认为该分类是无效分类
                        continue;
                    }

                    // 根据分类多语言配置规律，至少应该配置简体中文语言的，如果中文语言都没有配置，则可以直接忽略掉该分类记录
                    String tmpLangKey = MallRedisKeys.TYPE_LANG + "en:" + subCategory.getId().toString();
                    js = redisHandler.getString(tmpLangKey);
                    if (StrUtil.isBlank(js)) {
                       // logger.error("=====> 商品分类:" + key + " 的多语言缓存数据为空");
                        // 连中文语言都没有配置，认为该分类是无效分类
                        continue;
                    }
                }

                pLang = JSONArray.parseObject(js, CategoryLang.class);
                subCategory.setName(pLang.getName());
                subCategory.setCategoryId(subCategory.getId());
                //subCategory.setDes(pLang.getDes());
                subCategory.setSort(subCategory.getRank());
                if("en".equals(pLang.getLang()) && !lang.equals(pLang.getLang())){
                    // 翻译对应语言
                    subCategory.setName(Locales.translate(subCategory.getName(), Locales.LANG_EN,lang));
                }
                validSubCategoryList.add(subCategory);
            }
        }

        resultObject.setData(validCategoryList);
        return resultObject;
    }

    /**
     * 将指定商铺正在使用的商品分类以树形结构展示.
     *
     * @param request
     * @return
     */
    @GetMapping(action + "sellerTree.action")
    public Object sellerCategoryTree(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String lang = this.getLanguage(request);
        String showHidden = request.getParameter("showHidden");
        if (StrUtil.isBlank(showHidden)) {
            // status = 0 的分类记录不展示
            // 默认无效分类不展示
            showHidden = "0";
        }
        String sellerId = request.getParameter("sellerId");
        if (StrUtil.isEmpty(sellerId)) {
            sellerId = this.getLoginPartyId();
        }
        if (StrUtil.isBlank(sellerId)) {
            resultObject.setCode("1");
            resultObject.setMsg("未指定商家");
            return resultObject;
        }

        // 提取当前商铺用到的分类集合
        boolean onlyOnShelfGoods = true;
        if (showHidden.equals("1")) {
            onlyOnShelfGoods = false;
        }
        List<String> sellerGoodsCategoryIdList = sellerGoodsService.getSellerAllCategoryList(sellerId, onlyOnShelfGoods);
        logger.info("===========> 商家:" + sellerId + " 下的商品分类id集合为:{}" + JsonUtils.getJsonString(sellerGoodsCategoryIdList));
        // 检查前端需求：被禁用的分类是否展示
        List<Category> categoryList = categoryService.listByIds(sellerGoodsCategoryIdList);
        List<String> categoryIdList = new ArrayList();
        for (Category oneCategory : categoryList) {
            if (oneCategory.getType() == 0) {
                continue;
            }
            if (showHidden.equals("1")) {
                // 禁用的分类也展示
                categoryIdList.add(oneCategory.getId().toString());
            } else {
                // 不展示禁用的
                if (oneCategory.getStatus() == 1) {
                    // 只展示有效的分类
                    categoryIdList.add(oneCategory.getId().toString());
                }
            }
        }

        // 构造树形结构
        List<CategoryVO> categoryTreeList = categoryService.loadBuildCategoryTree(categoryIdList);
        List<CategoryVO> validCategoryTreeList = new ArrayList<>();
        //logger.error("======> 当前商品分类分页记录为: " + JSON.toJSON(categoryList));
        for (CategoryVO category : categoryTreeList) {
            String key = MallRedisKeys.TYPE_LANG + lang + ":" + category.getId().toString();
            String js = redisHandler.getString(key);
            if (StrUtil.isBlank(js)) {
                logger.error("=====> 商品分类:" + key + " 的在语言:" + lang + " 下的缓存数据为空");
                if (lang.equals("cn")) {
                    // 连中文语言都没有配置，认为该分类是无效分类
                    continue;
                }
                // 根据分类多语言配置规律，至少应该配置简体中文语言的，如果中文语言都没有配置，则可以直接忽略掉该分类记录
                String tmpLangKey = MallRedisKeys.TYPE_LANG + "cn:" + category.getId().toString();
                js = redisHandler.getString(tmpLangKey);
                if (StrUtil.isBlank(js)) {
                    logger.error("=====> 商品分类:" + key + " 的多语言缓存数据为空");
                    // 连中文语言都没有配置，认为该分类是无效分类
                    continue;
                }
            }

            CategoryLang pLang = JSONArray.parseObject(js, CategoryLang.class);
            category.setName(pLang.getName());
            category.setCategoryId(category.getId());
            category.setDes(pLang.getDes());
            category.setSort(category.getRank());

            validCategoryTreeList.add(category);
            // 二级分类也要处理
            if (CollectionUtil.isEmpty(category.getSubList())) {
                continue;
            }

            List<CategoryVO> oriSubCategoryList = category.getSubList();
            List<CategoryVO> validSubCategoryList = new ArrayList<>();
            category.setSubList(validSubCategoryList);
            for (CategoryVO subCategory : oriSubCategoryList) {
                key = MallRedisKeys.TYPE_LANG + lang + ":" + subCategory.getId().toString();
                js = redisHandler.getString(key);
                if (StrUtil.isBlank(js)) {
                    logger.error("=====> 商品分类:" + key + " 的在语言:" + lang + " 下的缓存数据为空");
                    if (lang.equals("cn")) {
                        // 连中文语言都没有配置，认为该分类是无效分类
                        continue;
                    }
                    // 根据分类多语言配置规律，至少应该配置简体中文语言的，如果中文语言都没有配置，则可以直接忽略掉该分类记录
                    String tmpLangKey = MallRedisKeys.TYPE_LANG + "cn:" + subCategory.getId().toString();
                    js = redisHandler.getString(tmpLangKey);
                    if (StrUtil.isBlank(js)) {
                        logger.error("=====> 商品分类:" + key + " 的多语言缓存数据为空");
                        // 连中文语言都没有配置，认为该分类是无效分类
                        continue;
                    }
                }

                pLang = JSONArray.parseObject(js, CategoryLang.class);
                subCategory.setName(pLang.getName());
                subCategory.setCategoryId(subCategory.getId());
                subCategory.setDes(pLang.getDes());
                subCategory.setSort(subCategory.getRank());

                validSubCategoryList.add(subCategory);
            }
        }

        resultObject.setData(validCategoryTreeList);
        return resultObject;
    }


}
