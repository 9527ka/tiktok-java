package project.web.api;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import ext.Strings;
import ext.Types;
import ext.translate.Locales;
import ext.translate.TranslateLocale;
import kernel.util.DateUtils;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.goods.GoodsService;
import project.mall.LanguageEnum;
import project.mall.MallRedisKeys;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.KeepGoodsService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.dto.CategoryGoodCountDto;
import project.mall.goods.dto.GoodSkuAttrDto;
import project.mall.goods.model.*;
import project.mall.goods.vo.SellerGoodsCount;
import project.mall.goods.vo.SellerViewCount;
import project.mall.goods.vo.SoldGoodsCount;
import project.mall.seller.FocusSellerService;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.seller.model.SellerVo;
import project.mall.type.CategoryService;
import project.mall.type.model.Category;
import project.mall.utils.MallPageInfo;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
public class SellerGoodsController extends BaseAction {
    private final Logger logger = LoggerFactory.getLogger(SellerGoodsController.class);

    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    private GoodsSkuAtrributionService goodsSkuAtrributionService;

    @Resource
    protected SellerService sellerService;

    @Resource
    EvaluationService evaluationService;
    @Resource
    FocusSellerService focusSellerService;
    @Resource
    KeepGoodsService keepGoodsService;
    @Resource
    CategoryService categoryService;

    @Resource
    SysparaService sysparaService;

    @Resource
    GoodsService goodsService;


    private final String action = "/api/sellerGoods!";


    @PostMapping(action + "recommend_new.action")
    public Object recommendAndNew(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        /**
         * type=1 : 推荐商品
         * type=0 : 每日新商品
         * type=2 : 热销商品
         */
        String type_str = request.getParameter("type");
        PageInfo pageInfo = getPageInfo(request);
        String lang = Types.orValue(request.getParameter("lang"),"en");
        String partyId = getLoginPartyId();
        int type = 0;
        if (null != type_str) {
            try {
                type = Integer.parseInt(type_str);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        List<SellerGoods> sellerGoods = this.sellerGoodsService.listRecommendAndNewGoods(type, pageInfo);
        JSONArray jsonArray = this.assemble(sellerGoods, lang, partyId);
        JSONObject object = new JSONObject();
        object.put("result", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "recommend_like.action")
    public Object sellerRecommendAndLike(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String sellerId = request.getParameter("sellerId");
        String partyId = this.getLoginPartyId();
        String type_str = request.getParameter("type");
        String lang = request.getParameter("lang");
        if (null == partyId) {
            resultObject.setCode("403");
            resultObject.setMsg("请重新登录");
            return resultObject;
        }
        if (null == sellerId || sellerId.equals("")) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数sellerId");
            return resultObject;
        }
        int type = 0;
        if (null != type_str) {
            try {
                type = Integer.parseInt(type_str);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        List<SellerGoods> sellerGoods = this.sellerGoodsService.listRecommendAndLikeGoods(partyId, sellerId, type);
        if (CollectionUtil.isNotEmpty(sellerGoods)) {
            JSONArray assemble = this.assemble(sellerGoods, lang, partyId);
            resultObject.setData(assemble);
        }
        resultObject.setCode("0");
        return resultObject;
    }


    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang =request.getParameter("lang");
        String sellerId = request.getParameter("sellerId");
        // 为简化前端传参，无论是一级分类还是二级分类，前端都使用 categoryId 传参，后台自行分析
        String categoryId = request.getParameter("categoryId");
        String secondaryCategoryId = "";//request.getParameter("secondaryCategoryId");
        String isNewStr = request.getParameter("isNew");
        String isHotStr = request.getParameter("isHot");
        String isPriceStr = request.getParameter("isPrice");
        String isRecStr = request.getParameter("isRec");
        String recTime = request.getParameter("recTime");
        String discount = request.getParameter("discount");
        String partyId = getLoginPartyId();
        Integer isNew = null;
        Integer is_discount = 0;
        Integer rec = 0;
        if (null != discount) {
            is_discount = Integer.valueOf(discount);
        }
        if (recTime != null) {
            rec = Long.parseLong(recTime) > 0 ? 1 : 0;
        }
        if (isNewStr != null) {
            isNew = Integer.valueOf(isNewStr);
        }
        Integer isRec = null;
        if (isRecStr != null) {
            isRec = Integer.valueOf(isRecStr);
        }
        Integer isPrice = null;
        if (isPriceStr != null) {
            isPrice = Integer.valueOf(isPriceStr);
        }
        Integer isHot = null;
        if (isHotStr != null) {
            isHot = Integer.valueOf(isHotStr);
        }

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

        MallPageInfo mallPageInfo = sellerGoodsService.listGoodsSell(pageInfo.getPageNum(), pageInfo.getPageSize(),
                sellerId, categoryId, secondaryCategoryId, isNew, rec, isRec, isHot, isPrice, lang, is_discount);

        List<SellerGoods> list = mallPageInfo.getElements();
        JSONArray jsonArray = this.assemble(list, lang, partyId);

        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    private JSONArray assemble(List<SellerGoods> sellerGoods, String lang, String partyId) {
        JSONArray jsonArray = new JSONArray();
        if (CollectionUtil.isNotEmpty(sellerGoods)) {
          //  List<String> sellerGoodsId = sellerGoods.stream().map(s -> s.getId()).collect(Collectors.toList());
//            Map<String, Long> viewNums = sellerGoodsService.getViewNums(sellerGoodsId);
            for (SellerGoods pl : sellerGoods) {
                String js = getGoodsLang(lang, pl);
                if (js == null) continue;
                SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
                if (pLang.getType() == 1) {
                    continue;
                }
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(pl.getSystemGoods(), goodsVo);
                goodsVo.setId(pl.getId());
                goodsVo.setSellerId(pl.getSellerId());
                goodsVo.setGoodsId(pl.getSystemGoods().getId().toString());
                goodsVo.setSellingPrice(pl.getSellingPrice());
                //设置折扣信息
                setDiscount(pl, goodsVo);

//                goodsVo.setViewsNum(viewNums.getOrDefault(pl.getId(), 0L));
                goodsVo.setCategoryId(pl.getCategoryId());
                goodsVo.setSoldNum(pl.getSoldNum());
                goodsVo.setName(pLang.getName());
                goodsVo.setUnit(pLang.getUnit());
                goodsVo.setDes(pLang.getDes());
                goodsVo.setImgDes(pLang.getImgDes());
                goodsVo.setIsShelf(pl.getIsShelf());
                goodsVo.setShowWeight1(pl.getShowWeight1());
                goodsVo.setShowWeight2(pl.getShowWeight2());
                if (null != partyId) {
                    goodsVo.setIsKeep(keepGoodsService.queryIsKeep(pl.getId().toString(), partyId));
                }

                Long time = pl.getRecTime();
                goodsVo.setRecTime(time);
                goodsVo.setNewTime(pl.getNewTime());

                if("en".equals(pLang.getLang()) && !"en".equals(lang)){
                    // 翻译对应语言
                    goodsVo.setName(Locales.translate(goodsVo.getName(), Locales.LANG_EN,lang));
                }

                jsonArray.add(goodsVo);
            }
        }
        return jsonArray;
    }

    @Nullable
    private String getGoodsLang(String lang, SellerGoods pl) {
        if(!("tw".equals(lang) || "cn".equals(lang) || "en.".equals(lang))){
            // 其他语种默认为en
            lang = "en";
        }
        // 读取指定语言的商品设置
        String key = MallRedisKeys.MALL_GOODS_LANG + lang + ":" + pl.getGoodsId();
        String js = redisHandler.getString(key);
        if (!Strings.isNullOrEmpty(js)) {
            return js;
        }
        if (!lang.equals("en")) {
            // 如果不存在非英语的商品设置，则尝试获取英语商品设置
            key = MallRedisKeys.MALL_GOODS_LANG + "en:" + pl.getGoodsId();
            js = redisHandler.getString(key);
            if(!Strings.isNullOrEmpty(js)){
                return js;
            }
        }
        List<SystemGoodsLang> gl =  goodsService.getSystemGoodsLang(pl.getGoodsId());
        for(SystemGoodsLang sgl : gl){
            key = MallRedisKeys.MALL_GOODS_LANG +sgl.getLang() +":" + pl.getGoodsId();
            String json = JSONArray.toJSONString(sgl);
            if(sgl.getLang().equals(lang)){
                // 获取对应语言
                js = json;
            }else{
                // 使用英语填充
                if(sgl.getLang().equals("en") && Strings.isNullOrEmpty(js)){
                    js = json;
                }
            }
            redisHandler.setSyncString(key,json);
        }
        return js;
    }

    /**
     * 设置折扣信息
     *
     * @param pl      商品
     * @param goodsVo 返回信息
     */
    private void setDiscount(SellerGoods pl, GoodsVo goodsVo) {
        Date discountStartTime = pl.getDiscountStartTime();
        Date discountEndTime = pl.getDiscountEndTime();
        if (discountStartTime != null && discountEndTime != null
                && discountStartTime.before(new Date()) && discountEndTime.after(new Date())) {
            goodsVo.setDiscountPrice(pl.getDiscountPrice());
            goodsVo.setDiscountRatio(pl.getDiscountRatio());
        }
    }

    @PostMapping(action + "search-keyword.action")
    public Object searchKeyword(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String lang = this.getLanguage(request);
        String keyword = request.getParameter("keyword");
        String isNewStr = request.getParameter("isNew");
        String isHotStr = request.getParameter("isHot");
        String isPriceStr = request.getParameter("isPrice");
        int isNew = 0;
        int isPrice = 0;
        int isHot = 0;
        try {
            if (isNewStr != null) {
                isNew = Integer.valueOf(isNewStr).intValue();
            }
            if (isPriceStr != null) {
                isPrice = Integer.valueOf(isPriceStr).intValue();
            }
            if (isHotStr != null) {
                isHot = Integer.valueOf(isHotStr).intValue();
            }
        } catch (Exception ex) {

        }
        if (StringUtils.isBlank(keyword)) {
            resultObject.setCode("1");
            resultObject.setMsg("请输入搜索关键字");
            return resultObject;
        }

        long searchBeginTime = System.currentTimeMillis();
        List<SystemGoods> goodsList = sellerGoodsService.querySearchKeyword(lang, keyword, isNew, isHot, isPrice);
        if (CollectionUtil.isEmpty(goodsList) && !lang.equals(LanguageEnum.EN.getLang())) {
            goodsList = this.sellerGoodsService.querySearchKeyword(LanguageEnum.EN.getLang(), keyword, isNew, isHot, isPrice);
        }
        long searchEndTime = System.currentTimeMillis();
        logger.info("-----> [SellerGoodsController searchKeyword] 基于关键词搜索系统商品耗时:{} ms", (searchEndTime - searchBeginTime));
        searchBeginTime = searchEndTime;

        List<Seller> sellerList = sellerService.querySearchKeyword(keyword);
        searchEndTime = System.currentTimeMillis();
        logger.info("-----> [SellerGoodsController searchKeyword] 基于关键词搜索店铺耗时:{} ms", (searchEndTime - searchBeginTime));
        searchBeginTime = searchEndTime;

        List<SellerVo> sellerVoList = new ArrayList<>();
        Set<String> sellerIds = new HashSet<>();
        for (Seller seller : sellerList) {
            sellerIds.add(seller.getId());

            SellerVo sellerVo = new SellerVo();
            BeanUtils.copyProperties(seller, sellerVo);
            sellerVoList.add(sellerVo);
        }

        searchEndTime = System.currentTimeMillis();
        logger.info("-----> [SellerGoodsController searchKeyword] 统计当前店铺列表下的销量、流量、好评、商品等数据耗时:{} ms", (searchEndTime - searchBeginTime));

        for (SellerVo sellerVo : sellerVoList) {
            sellerVo.setSoldNum(
                    (Objects.nonNull(sellerVo.getSoldNum())?sellerVo.getSoldNum():0)+
                            (Objects.nonNull(sellerVo.getFakeSoldNum())?sellerVo.getFakeSoldNum():0));
            sellerVo.setSellerGoodsNum(Objects.nonNull(sellerVo.getSellerGoodsNum())?sellerVo.getSellerGoodsNum():0);
        }

        List<Map<String, Object>> goodsInfoList = new ArrayList<>();
        for (SystemGoods oneGoods : goodsList) {
            Map<String, Object> oneGoodsInfo = new HashMap<>();
            goodsInfoList.add(oneGoodsInfo);

            oneGoodsInfo.put("goodsId", oneGoods.getId());
            String langName = "";
            if (lang.equals(LanguageEnum.CN.getLang())) {
                langName = oneGoods.getCnTitle();
            } else if (lang.equals(LanguageEnum.EN.getLang())) {
                langName = oneGoods.getEnTitle();
            } else if (lang.equals(LanguageEnum.TW.getLang())) {
                langName = oneGoods.getZhTitle();
            }
            if (StrUtil.isBlank(langName)) {
                langName = oneGoods.getEnTitle();
            }

            oneGoodsInfo.put("name", langName);
        }

        JSONObject object = new JSONObject();
        object.put("sellerList", sellerVoList);
        object.put("goodsList", goodsInfoList);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "search-goods.action")
    public Object searchGoods(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String goodsId = request.getParameter("goodsId");
        String isNewStr = request.getParameter("isNew");
        String isHotStr = request.getParameter("isHot");
        String isPriceStr = request.getParameter("isPrice");
        int isNew = 0;
        int isPrice = 0;
        int isHot = 0;
        try {
            if (isNewStr != null) {
                isNew = Integer.valueOf(isNewStr).intValue();
            }
            if (isPriceStr != null) {
                isPrice = Integer.valueOf(isPriceStr).intValue();
            }
            if (isHotStr != null) {
                isHot = Integer.valueOf(isHotStr).intValue();
            }
        } catch (Exception ex) {

        }
        if (StringUtils.isBlank(goodsId)) {
            resultObject.setCode("1");
            resultObject.setMsg("请选择商品");
            return resultObject;
        }
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);
        JSONArray jsonArray = new JSONArray();
        List<SellerGoods> list = sellerGoodsService.querySearchGoods(pageInfo.getPageNum(), pageInfo.getPageSize(), goodsId, isPrice, isNew, isHot);
        List<String> sellerGoodsId = list.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        Map<String, Long> viewNums = sellerGoodsService.getViewNums(sellerGoodsId);
        for (SellerGoods pl : list) {
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(pl.getSystemGoods(), goodsVo);
            goodsVo.setId(pl.getId());
            goodsVo.setGoodsId(pl.getSystemGoods().getId().toString());
            goodsVo.setSellingPrice(pl.getSellingPrice());
            goodsVo.setViewsNum(viewNums.getOrDefault(pl.getId().toString(), 0L));
            goodsVo.setCategoryId(pl.getCategoryId());
            goodsVo.setSoldNum(pl.getSoldNum());
            //设置折扣信息
            setDiscount(pl, goodsVo);

            String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + pl.getGoodsId());
            if (kernel.util.StringUtils.isEmptyString(js)) {
                continue;
            }
            SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
            if (pLang.getType() == 1) {
                continue;
            }
            goodsVo.setName(pLang.getName());
            jsonArray.add(goodsVo);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "info.action")
    public Object info(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String userId = getLoginPartyId();
//        String lang = this.getLanguage(request);
        String lang = request.getParameter("lang");
        if (kernel.util.StringUtils.isEmptyString(lang)) {
            lang = project.invest.LanguageEnum.EN.getLang();
        }
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        String skuId = request.getParameter("skuId");

        SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(sellerGoodsId);
        if (sellerGoods == null) {
            resultObject.setCode("1");
            resultObject.setMsg("无此sellerGoods");
            return resultObject;
        }
        if (sellerGoods.getIsShelf() == 0) {
            resultObject.setCode("1");
            resultObject.setMsg("该商品已下架");
            return resultObject;
        }
        String key = MallRedisKeys.MALL_GOODS_LANG + lang + ":" + sellerGoods.getGoodsId();
        String js = redisHandler.getString(key);

        if (kernel.util.StringUtils.isEmptyString(js)) {
            String l = !lang.equals("en")? "en":lang;
            key = MallRedisKeys.MALL_GOODS_LANG + l + ":" + sellerGoods.getGoodsId();
            js = redisHandler.getString(key);
            if (kernel.util.StringUtils.isEmptyString(js)) {
                resultObject.setCode("1");
                resultObject.setMsg("非法请求");
                return resultObject;
            }
        }
        SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
        if (pLang.getType() == 1) {
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }
        Seller seller = sellerService.getSeller(sellerGoods.getSellerId());
        if (seller == null || seller.getStatus() != 1) {
            resultObject.setCode("1");
            resultObject.setMsg("暂无店铺");
            resultObject.setData(seller);
            return resultObject;
        }

        SellerVo sellerVo = new SellerVo();
        BeanUtils.copyProperties(seller, sellerVo);
        int fakeSoldNum = 0;
        // 没有拉黑
        if (seller != null) {
            fakeSoldNum = seller.getFakeSoldNum() == null ? 0 : seller.getFakeSoldNum();
            if (seller.getBlack() == 0) {
                // 此举会触发事件在另外一个地方更新 sellerGoods 的一些字段值，所以需要重新查询一下，否则会覆盖部分字段值
                sellerGoodsService.addRealViews(seller.getId().toString(), sellerGoodsId, userId, 1);

                // 奇怪的是，此处查询返回的 showWeight2 值仍然是旧的值，但是后面的 update 不会覆盖
                SellerGoods tmpSellerGoods = sellerGoodsService.getSellerGoods(sellerGoodsId);
                //logger.info("=================> tmpSellerGoods:{}", JsonUtils.bean2Json(tmpSellerGoods));
                sellerGoods.setViewsNum(tmpSellerGoods.getViewsNum());
                sellerGoods.setShowWeight1(tmpSellerGoods.getShowWeight1());
                sellerGoods.setShowWeight2(tmpSellerGoods.getShowWeight2());
                // sellerGoods.setViewsNum(sellerGoods.getViewsNum() + 1);
            }
        }
        if (null != userId) {
            this.sellerGoodsService.insertBrowsHistory(userId, sellerGoodsId, sellerGoods.getSellerId());
        }
        sellerGoodsService.updateSellerGoods(sellerGoods);
//        sellerVo.setSellerGoodsNum(sellerGoodsService.getGoodsNumBySellerId(seller.getId().toString()));
//        sellerVo.setFocusNum(focusSellerService.getFocusCount(seller.getId().toString()));
//        sellerVo.setHighOpinion(evaluationService.getHighOpinionBySellerId(sellerGoods.getSellerId()));
        // 添加虚假销量
        sellerVo.setSoldNum(fakeSoldNum + seller.getSoldNum());
        GoodsVo goodsVo = new GoodsVo();
        BeanUtils.copyProperties(sellerGoods.getSystemGoods(), goodsVo);
        goodsVo.setId(sellerGoods.getId());
        goodsVo.setSellerId(sellerGoods.getSellerId());
        goodsVo.setGoodsId(sellerGoods.getGoodsId());
        goodsVo.setSellingPrice(Objects.nonNull(sellerGoods.getSellingPrice())?BigDecimal.valueOf(sellerGoods.getSellingPrice()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue():null);
        Date discountStartTime = sellerGoods.getDiscountStartTime();
        Date discountEndTime = sellerGoods.getDiscountEndTime();
        if (discountStartTime != null && discountEndTime != null
                && discountStartTime.compareTo(DateUtils.getDayStart(new Date())) < 1 && discountEndTime.compareTo(DateUtils.getDayStart(new Date())) > -1) {
            goodsVo.setDiscountPrice(Objects.nonNull(sellerGoods.getDiscountPrice())?BigDecimal.valueOf(sellerGoods.getDiscountPrice()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue():null);
            goodsVo.setDiscountRatio(sellerGoods.getDiscountRatio());
        }
        goodsVo.setViewsNum(sellerGoodsService.getViewNums(sellerGoods.getId().toString()));
        goodsVo.setCategoryId(sellerGoods.getCategoryId());
        goodsVo.setSoldNum(sellerGoods.getSoldNum());
        goodsVo.setName(pLang.getName());
        goodsVo.setUnit(pLang.getUnit());
        goodsVo.setDes(pLang.getDes());
        String imgDes = pLang.getImgDes();
        if (StringUtils.isEmpty(imgDes)) {
            key = MallRedisKeys.MALL_GOODS_LANG + "en" + ":" + sellerGoods.getGoodsId();
            js = redisHandler.getString(key);
            SystemGoodsLang enLang = JSONArray.parseObject(js, SystemGoodsLang.class);
            imgDes = enLang.getImgDes();
        }
        SystemGoods systemGoods = sellerGoodsService.getSystemGoods(sellerGoods.getGoodsId());
        goodsVo.setImgDes(imgDes);
        goodsVo.setStopTime(sellerGoods.getStopTime());
        goodsVo.setIsShelf(sellerGoods.getIsShelf());
        goodsVo.setShowWeight1(sellerGoods.getShowWeight1());
        goodsVo.setShowWeight2(sellerGoods.getShowWeight2());

        int buyMin = Objects.nonNull(systemGoods) ? systemGoods.getBuyMin() : 1;
        Syspara globalBuyMinInfo = this.sysparaService.find(SysParaCode.GOODS_BUY_MIN.getCode());
        if (globalBuyMinInfo != null && StrUtil.isNotBlank(globalBuyMinInfo.getValue())) {
            // 选取最大值
            Integer globalBuyMin = Integer.parseInt(globalBuyMinInfo.getValue());
            if (globalBuyMin > buyMin) {
                buyMin = globalBuyMin;
            }
        }
        goodsVo.setBuyMin(buyMin);
        // 如果不为空且不为-1 表示有属性的
        if (StringUtils.isNotEmpty(skuId) && !"-1".equalsIgnoreCase(skuId)) {
            // caster 优化
            // List<GoodsAttributeVo> attributes = goodsSkuAtrributionService.findGoodsAttributeBySkuId(skuId, lang);
            List<GoodsAttributeVo> attributes = goodsSkuAtrributionService.findCachedGoodsAttributeBySkuId(skuId, lang);
            goodsVo.setAttributes(attributes);
        } else {
            // 当前商品可选的属性值
            // caster 优化
            // GoodSkuAttrDto goodsAttrListSku = goodsSkuAtrributionService.getGoodsAttrListSkuBySellerGoods(sellerGoods, lang);
            GoodSkuAttrDto goodsAttrListSku = goodsSkuAtrributionService.getCachedGoodsAttrListSkuBySellerGoods(sellerGoods, lang);
            goodsVo.setCanSelectAttributes(goodsAttrListSku);
        }

//        goodsVo.setFreightAmount(sellerGoods.getSystemGoods().getFreightAmount());
        goodsVo.setSeller(sellerVo);
        if (userId != null) {
            goodsVo.setIsKeep(keepGoodsService.queryIsKeep(sellerGoodsId, userId));
        }

        if("en".equals(pLang.getLang()) && !lang.equals(pLang.getLang())){
            // 翻译对应语言
            goodsVo.setName(TranslateLocale.get(goodsVo.getName(), Locales.LANG_EN,lang));
            goodsVo.setDes(TranslateLocale.get(goodsVo.getDes(), Locales.LANG_EN,lang));
        }
        resultObject.setData(goodsVo);
        return resultObject;
    }


    /**
     * 搜索店铺下的商品
     *
     * @param request
     * @return
     */

    @PostMapping(action + "searchSellerGoods.action")
    public Object searchSellerGoods(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String lang = this.getLanguage(request);
        String keyword = request.getParameter("keyword");
        if (org.apache.commons.lang3.StringUtils.isBlank(keyword)) {
            resultObject.setCode("1");
            resultObject.setMsg("请输入搜索关键字");
            return resultObject;
        }
        String sellerId = request.getParameter("sellerId");
        if (org.apache.commons.lang3.StringUtils.isBlank(sellerId)) {
            resultObject.setCode("1");
            resultObject.setMsg("商家id不能为空");
            return resultObject;
        }
        PageInfo pageInfo = getPageInfo(request);

        JSONArray jsonArray = new JSONArray();
        // 待取代 TODO
        // List<SellerGoods> list = sellerGoodsService.querySearchsellerGoods1(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerId, keyword, lang);
        List<SellerGoods> list = sellerGoodsService.querySearchsellerGoods(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerId, keyword, lang);
        List<String> sellerGoodsId = list.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        Map<String, Long> viewNums = sellerGoodsService.getViewNums(sellerGoodsId);
        for (SellerGoods pl : list) {
            String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + pl.getGoodsId());
            if (kernel.util.StringUtils.isEmptyString(js)) {
                continue;
            }
            SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
            if (pLang.getType() == 1) {
                continue;
            }
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(pl.getSystemGoods(), goodsVo);
            goodsVo.setId(pl.getId());
            goodsVo.setGoodsId(pl.getSystemGoods().getId().toString());
            goodsVo.setSellingPrice(pl.getSellingPrice());
            //设置折扣信息
            setDiscount(pl, goodsVo);

            goodsVo.setViewsNum(viewNums.getOrDefault(pl.getId().toString(), 0L));
            goodsVo.setCategoryId(pl.getCategoryId());
            goodsVo.setSoldNum(pl.getSoldNum());
            goodsVo.setName(pLang.getName());
            goodsVo.setUnit(pLang.getUnit());
            goodsVo.setDes(pLang.getDes());
            goodsVo.setImgDes(pLang.getImgDes());
            goodsVo.setIsShelf(pl.getIsShelf());
            goodsVo.setStopTime(pl.getStopTime());
            Long time = pl.getRecTime();
            goodsVo.setRecTime(time);
            goodsVo.setNewTime(pl.getNewTime());
            jsonArray.add(goodsVo);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "search.action")
    public Object search(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String lang = this.getLanguage(request);

        String keyword = request.getParameter("keyword");
        String partyId = this.getLoginPartyId();
        String isNewStr = request.getParameter("isNew");
        String isHotStr = request.getParameter("isHot");
        String isPriceStr = request.getParameter("isPrice");
        String isRecStr = request.getParameter("isRec");
        String discount = request.getParameter("discount");

        Integer isNew = null;
        if (isNewStr != null) {
            isNew = Integer.valueOf(isNewStr);
        }
        Integer isRec = null;
        if (isRecStr != null) {
            isRec = Integer.valueOf(isRecStr);
        }
        Integer isPrice = null;
        if (isPriceStr != null) {
            isPrice = Integer.valueOf(isPriceStr);
        }
        Integer isHot = null;
        if (isHotStr != null) {
            isHot = Integer.valueOf(isHotStr);
        }
        Integer is_discount = 0;
        if (null != discount) {
            is_discount = Integer.valueOf(discount);
        }
        if (org.apache.commons.lang3.StringUtils.isBlank(keyword)) {
            resultObject.setCode("1");
            resultObject.setMsg("请输入搜索关键字");
            return resultObject;
        }
        PageInfo pageInfo = getPageInfo(request);

        JSONArray jsonArray = new JSONArray();
        // 待取代
        //PageInfo searchsellerGoodsPage = sellerGoodsService.pagedSearchsellerGoods1(pageInfo.getPageNum(), pageInfo.getPageSize(), keyword, lang, isNew, isRec, isHot, isPrice, is_discount);
        PageInfo searchsellerGoodsPage = sellerGoodsService.pagedSearchsellerGoods(pageInfo.getPageNum(), pageInfo.getPageSize(), keyword, lang, isNew, isRec, isHot, isPrice, is_discount);

        List<SellerGoods> list = searchsellerGoodsPage.getElements();
        pageInfo.setTotalElements(searchsellerGoodsPage.getTotalElements());
        List<String> sellerGoodsId = list.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        Map<String, Long> viewNums = sellerGoodsService.getViewNums(sellerGoodsId);
        for (SellerGoods pl : list) {
            String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + pl.getGoodsId());
            if (kernel.util.StringUtils.isEmptyString(js)) {
                continue;
            }
            SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
            if (pLang.getType() == 1) {
                continue;
            }
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(pl.getSystemGoods(), goodsVo);
            goodsVo.setId(pl.getId());
            goodsVo.setGoodsId(pl.getSystemGoods().getId().toString());
            goodsVo.setSellingPrice(pl.getSellingPrice());
            //设置折扣信息
            setDiscount(pl, goodsVo);

            goodsVo.setViewsNum(viewNums.getOrDefault(pl.getId().toString(), 0L));
            goodsVo.setCategoryId(pl.getCategoryId());
            goodsVo.setSoldNum(pl.getSoldNum());
            goodsVo.setName(pLang.getName());
            goodsVo.setUnit(pLang.getUnit());
            goodsVo.setDes(pLang.getDes());
            goodsVo.setImgDes(pLang.getImgDes());
            goodsVo.setIsShelf(pl.getIsShelf());
            goodsVo.setStopTime(pl.getStopTime());
            goodsVo.setShowWeight1(pl.getShowWeight1());
            goodsVo.setShowWeight2(pl.getShowWeight2());
            if (null != partyId) {
                goodsVo.setIsKeep(keepGoodsService.queryIsKeep(pl.getId().toString(), partyId));
            }
            Long time = pl.getRecTime();
            goodsVo.setRecTime(time);
            goodsVo.setNewTime(pl.getNewTime());
            jsonArray.add(goodsVo);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 搜索折扣商品
     *
     * @param request
     * @return
     */
    @PostMapping(action + "search-discount.action")
    public Object search_discount(HttpServletRequest request) {

        return null;
    }

    /**
     * 获取店铺首页分类数
     *
     * @param request
     * @return
     */
    @PostMapping(action + "categoryGoodCount.action")
    public ResultObject categoryGoodCount(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String sellerId = request.getParameter("sellerId");
        String lang = this.getLanguage(request);
        List<CategoryGoodCountDto> resultData = sellerGoodsService.getCategoryGoodCount(sellerId, lang);
        resultObject.setData(resultData);
        resultObject.setCode("0");
        resultObject.setMsg("");
        return resultObject;
    }


    /**
     * 获取分类下的商品
     *
     * @param request
     * @return
     */
    @PostMapping(action + "categoryGoodList.action")
    public ResultObject categoryGoodList(HttpServletRequest request) {
        PageInfo pageInfo = getPageInfo(request);
        String sellerId = request.getParameter("sellerId");
        String categoryId = request.getParameter("categoryId");
        List<SellerGoods> sellerGoods = sellerGoodsService.getCategoryGoodList(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerId, categoryId);
        List<String> sellerGoodsId = sellerGoods.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        Map<String, Long> viewNums = sellerGoodsService.getViewNums(sellerGoodsId);
        List<GoodsVo> list = new ArrayList<>();
        String lang = this.getLanguage(request);
        for (SellerGoods pl : sellerGoods) {
            String key = MallRedisKeys.MALL_GOODS_LANG + lang + ":" + pl.getGoodsId();
            String js = redisHandler.getString(key);
            if (kernel.util.StringUtils.isEmptyString(js)) {
                continue;
            }
            SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
            if (pLang.getType() == 1) {
                continue;
            }
            GoodsVo goodsVo = new GoodsVo();
            BeanUtils.copyProperties(pl.getSystemGoods(), goodsVo);
            goodsVo.setId(pl.getId());
            goodsVo.setGoodsId(pl.getSystemGoods().getId().toString());
            goodsVo.setSellingPrice(pl.getSellingPrice());
            goodsVo.setViewsNum(viewNums.getOrDefault(pl.getId().toString(), 0L));
            goodsVo.setCategoryId(pl.getCategoryId());
            goodsVo.setSoldNum(pl.getSoldNum());
            goodsVo.setName(pLang.getName());
            goodsVo.setUnit(pLang.getUnit());
            goodsVo.setDes(pLang.getDes());
            goodsVo.setImgDes(pLang.getImgDes());
            goodsVo.setIsShelf(pl.getIsShelf());
            goodsVo.setStopTime(pl.getStopTime());
            Long time = pl.getRecTime();
            goodsVo.setRecTime(time);
            goodsVo.setNewTime(pl.getNewTime());
            list.add(goodsVo);
        }
        ResultObject resultObject = new ResultObject();
        Map<String, Object> data = new HashMap<>();
        data.put("pageInfo", pageInfo);
        data.put("pageList", list);
        resultObject.setData(data);
        return resultObject;
    }
}
