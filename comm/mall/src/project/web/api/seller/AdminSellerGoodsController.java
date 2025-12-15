package project.web.api.seller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.dubbo.common.utils.CollectionUtils;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;
import project.blockchain.RechargeBlockchainService;
import project.mall.LanguageEnum;
import project.mall.MallRedisKeys;
import project.mall.combo.ComboService;
import project.mall.combo.model.ComboUser;
import project.mall.evaluation.EvaluationService;
import project.mall.event.message.SellerGoodsUpdateEvent;
import project.mall.event.model.SellerGoodsUpdateInfo;
import project.mall.goods.AdminMallGoodsService;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.*;
import project.mall.goods.vo.SellerGoodsSkuInfoVO;
import project.mall.orders.GoodsOrdersService;
import project.mall.seller.MallLevelService;
import project.mall.seller.SellerService;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.mall.type.CategoryService;
import project.mall.type.model.Category;
import project.mall.type.model.CategoryLang;
import project.mall.utils.IdUtils;
import project.mall.utils.MallPageInfo;
import project.party.UserMetricsService;
import project.party.model.UserMetrics;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.web.api.SellerGoodsQuery;
import util.DateUtil;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 商户后台商品管理
 */
@RestController
@CrossOrigin
public class AdminSellerGoodsController extends BaseAction {
    private final String action = "/seller/goods!";

    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    protected SellerService sellerService;
    @Resource
    protected AdminMallGoodsService adminMallGoodsService;
    @Resource
    protected GoodsOrdersService goodsOrdersService;
    @Resource
    EvaluationService evaluationService;
    @Resource
    ComboService comboService;
    @Resource
    CategoryService categoryService;

    @Resource
    SysparaService sysparaService;

    @Resource
    private RechargeBlockchainService rechargeBlockchainService;
    @Resource
    private UserMetricsService userMetricsService;

    @Resource
    private GoodsSkuAtrributionService goodsSkuAtrributionService;

    @Resource
    private MallLevelService mallLevelService;

    private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//    @GetMapping(action + "testSkuInsert.action")
//    public Object testSkuInsert(HttpServletRequest request) {
//        String goodsId = request.getParameter("goodsId");
//        SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(goodsId);
//        sellerGoodsService.saveSellerGoodsSkus(sellerGoods);
//        return new ResultObject();
//    }
//
//    @GetMapping(action + "updateSkuInsert.action")
//    public Object updateSkuInsert(HttpServletRequest request) {
//        String goodsId = request.getParameter("goodsId");
//        SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(goodsId);
//        sellerGoodsService.updateSellerGoodsSkus(sellerGoods);
//        return new ResultObject();
//    }

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);
        String partyId = this.getLoginPartyId();
        ResultObject resultObject = this.readSecurityContextFromSession(new ResultObject());
        if (!resultObject.getCode().equals("0")) {
            return resultObject;
        }
        //String partyId = "ff808081849f91c10184a08b70220029";
        String isHotStr = request.getParameter("isHot");
        String name = request.getParameter("name");
        String id = request.getParameter("id");
        String categoryId = request.getParameter("categoryId");
        ;
        String isShelf = request.getParameter("isShelf");
        ;

        SellerGoodsQuery sellerGoodsQuery = new SellerGoodsQuery();
        sellerGoodsQuery.setName(name);
        sellerGoodsQuery.setCategoryId(categoryId);
        sellerGoodsQuery.setGoodsId(id);
        sellerGoodsQuery.setIsShelf(isShelf);
        sellerGoodsQuery.setLang(lang);
        sellerGoodsQuery.setSellerId(partyId);

        if (StrUtil.isNotBlank(categoryId) && !Objects.equals(categoryId, "0")) {
            Category category = categoryService.getById(categoryId);
            if (category != null) {
                if (StrUtil.isBlank(category.getParentId()) || Objects.equals(category.getParentId(), "0")) {
                    // 当前分类是一级分类
                    sellerGoodsQuery.setCategoryId(categoryId);
                } else {
                    sellerGoodsQuery.setCategoryId(category.getParentId());
                    sellerGoodsQuery.setSecondaryCategoryId(categoryId);
                }
            }
        }

        Integer isHot = null;
        if (isHotStr != null) {
            isHot = Integer.valueOf(isHotStr);
        }
        JSONArray jsonArray = new JSONArray();

        // 待取代 TODO 新写法无需再重复查询一次
        //MallPageInfo mallPageInfo = sellerGoodsService.listGoodsSellAdmin1(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerGoodsQuery, null, null, isHot, null);
        MallPageInfo mallPageInfo = sellerGoodsService.listGoodsSellAdmin(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerGoodsQuery, null, null, isHot, null);
        if (CollectionUtil.isEmpty(mallPageInfo.getElements()) && !lang.equals(LanguageEnum.EN.getLang())) {
            lang = LanguageEnum.EN.getLang();
            sellerGoodsQuery.setLang(lang);
            mallPageInfo = sellerGoodsService.listGoodsSellAdmin(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerGoodsQuery, null, null, isHot, null);
        }
        List<SellerGoods> list = mallPageInfo.getElements();

        Set<String> goodsIds = new HashSet<>();
        JSONObject object = new JSONObject();
        if (CollectionUtil.isEmpty(list)) {
            pageInfo.setTotalElements(0);
            object.put("pageInfo", pageInfo);
            object.put("pageList", jsonArray);
        } else {
            List<String> sellerGoodsId = list.stream().map(s -> s.getId()).collect(Collectors.toList());
            Map<String, Long> viewNums = sellerGoodsService.getViewNums(sellerGoodsId);
            for (SellerGoods pl : list) {

                CategoryLang secondCategoryLang = this.categoryService.selectLang(lang, pl.getSecondaryCategoryId());
                CategoryLang cLang = this.categoryService.selectLang(lang, pl.getCategoryId());
                String key = MallRedisKeys.MALL_GOODS_LANG + lang + ":" + pl.getGoodsId();
                String js = redisHandler.getString(key);
                if (StringUtils.isEmptyString(js)) {
                    if (lang.equals("en")) {
                        continue;
                    }
                    key = MallRedisKeys.MALL_GOODS_LANG + "en:" + pl.getGoodsId();
                    js = redisHandler.getString(key);
                    if (StringUtils.isEmptyString(js)) {
                        continue;
                    }
                }
                SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);
                if (pLang.getType() == 1) {
                    continue;
                }
                GoodsVo goodsVo = new GoodsVo();
                BeanUtils.copyProperties(pl.getSystemGoods(), goodsVo);
                goodsVo.setId(pl.getId());
                goodsVo.setGoodsId(pl.getSystemGoods().getId());
                goodsVo.setSecondaryCategoryId(pl.getSecondaryCategoryId());
                goodsVo.setSecondaryCateName(null == secondCategoryLang ? "" : secondCategoryLang.getName());
                goodsVo.setSellingPrice(pl.getSellingPrice());
                goodsVo.setSystemPrice(pl.getSystemPrice());
                goodsVo.setProfitRatio(pl.getProfitRatio());
                goodsVo.setDiscountPrice(pl.getDiscountPrice());
                if (pl.getDiscountStartTime() != null && pl.getDiscountEndTime() != null) {
                    goodsVo.setDiscountStartTime(DateUtils.getLongDate(pl.getDiscountStartTime()));
                    goodsVo.setDiscountEndTime(DateUtils.getLongDate(pl.getDiscountEndTime()));
                    goodsVo.setDiscountRatio(pl.getDiscountRatio());
                    if (DateUtil.compare(pl.getDiscountEndTime(), DateUtil.minDate(new Date()))) {
                        goodsVo.setDiscountPrice(null);
                        goodsVo.setDiscountEndTime(null);
                        goodsVo.setDiscountStartTime(null);
                        goodsVo.setDiscountRatio(0d);
                    }
                }
                goodsVo.setViewsNum(viewNums.getOrDefault(pl.getId(), 0L));
                goodsVo.setCategoryId(pl.getCategoryId());
                goodsVo.setSecondaryCategoryId(pl.getSecondaryCategoryId());
                goodsVo.setSoldNum(pl.getSoldNum());
                goodsVo.setName(pLang.getName());
                goodsVo.setUnit(pLang.getUnit());
                goodsVo.setDes(pLang.getDes());
                goodsVo.setImgDes(pLang.getImgDes());
                goodsVo.setCategoryName(cLang.getName());
                goodsVo.setIsShelf(pl.getIsShelf());
                goodsVo.setIsCombo(pl.getIsCombo());
                Long time = pl.getRecTime();
                goodsVo.setRecTime(time);
                goodsVo.setNewTime(pl.getNewTime());
                goodsVo.setIsCombo(pl.getIsCombo());
                if (pl.getStopTime() == null || System.currentTimeMillis() > pl.getStopTime()) {
                    pl.setStopTime(0L);
                    sellerGoodsService.updateSellerGoods(pl);
                }
                goodsVo.setStopTime(pl.getStopTime());
                jsonArray.add(goodsVo);
                goodsIds.add(pl.getId());

            }
        }
//        Long evaluationNum = 0l;
//        if (CollectionUtil.isEmpty(goodsIds)){
//            evaluationNum = 0l;
//        }else {
//            List<String> countEvaluation = goodsIds.stream().collect(Collectors.toList());
//            evaluationNum = evaluationService.getEvaluationNumBySellerGoodsIds(countEvaluation);
//        }

        ComboUser comboUser = comboService.findComboUserByPartyId(partyId);
        // 剩下的直通车名额
        int leftPromoteNum = 0;
        // 当前总的直通车商品名额
        int promoteNum = 0;
        // 当前已经激活的直通车商品
        Set<String> comboActiveGoods = null;
        // 如果已经开通了直通车
        if (comboUser != null) {
            promoteNum = comboUser.getPromoteNum();
            comboActiveGoods = sellerGoodsService.getSellerComboActiveGoods(partyId).stream().map(s -> s.getId().toString()).collect(Collectors.toSet());
            leftPromoteNum = promoteNum - comboActiveGoods.size();

        }
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        object.put("systemGoodsNum", adminMallGoodsService.getSystemGoodsNum(lang));
        object.put("sellerGoodsNum", sellerGoodsService.getSellerGoodsNumBySellerId(partyId, lang));
        object.put("evaluations", evaluationService.getEvaluationNumBySellerId(partyId));
//        object.put("evaluations", evaluationNum);
        object.put("leftPromoteNum", leftPromoteNum);

        resultObject.setData(object);
        return resultObject;
    }

    @PostMapping(action + "addOrUpdate.action")
    public Object addOrUpdate(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String partyId = this.getLoginPartyId();
        if (StringUtils.isEmptyString(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("登录已过期");
            return resultObject;
        }
        Seller seller = this.sellerService.getSeller(partyId);
        if (null == seller || null == seller.getStatus() || seller.getStatus() == 0) {
            resultObject.setCode("1");
            resultObject.setMsg("店铺未审核通过暂不能上架商品");
            return resultObject;
        }
        String goodsIds = request.getParameter("goodsIds");
        if (goodsIds == null) {
            resultObject.setCode("1");
            resultObject.setMsg("商品id不能为空");
            return resultObject;
        }

        Date now = new Date();
        // 判断上架金额限制
        double minRechargeAmount = 500;
        Syspara contentParam = this.sysparaService.find(SysParaCode.ONSHELF_RECHARGE_AMOUNT_LIMIT.getCode());
        if (contentParam != null) {
            minRechargeAmount = Double.parseDouble(contentParam.getValue().trim());
        }
        UserMetrics userMetrics = userMetricsService.getByPartyId(partyId);
        double userRechargeTotal = 0.0;
        if (userMetrics != null) {
            userRechargeTotal = userMetrics.getMoneyRechargeAcc();
        }
        if (userRechargeTotal < minRechargeAmount) {
            resultObject.setCode("1");
            resultObject.setMsg("首次上架账户资金≥{_$1}，请充值后再试");

            Map<String, String> data = new HashMap<>();
            data.put("_$1", String.valueOf(minRechargeAmount));
            resultObject.setData(data);

            return resultObject;
        }

        // 利润比
        String percent_str = request.getParameter("percent"); //
        String profit = request.getParameter("profit");
        if (StringUtils.isEmptyString(percent_str)) {
            percent_str = profit;
        }

        percent_str = null == percent_str ? "0.00" : percent_str;
        // 折扣比
        String discount_str = request.getParameter("discount");
        double profit_ratio = 0.00D;
        double discount_ratio = 0.00;
        try {
            profit_ratio = Double.valueOf(percent_str).doubleValue();
            discount_ratio = Double.valueOf(discount_str).doubleValue();
        } catch (Exception ex) {
            resultObject.setCode("1");
            resultObject.setMsg("输入的利润比或者折扣比，不符合规范，请重新填写");
            return resultObject;
        }
        Date discountStartTime = null;
        Date discountEndTime = null;
        boolean has_discount = false;
        if (discount_ratio > 0.0D && discount_ratio < 1.0D) {
            try {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String startTime = request.getParameter("startTime");
                String endTime = request.getParameter("endTime");
                discountStartTime = format.parse(startTime);
                discountEndTime = format.parse(endTime);
                has_discount = true;
            } catch (Exception ex) {
                resultObject.setCode("1");
                resultObject.setMsg("请正确填写活动开启时间和结束时间");
                return resultObject;
            }
        }


        String[] goodsIdsArray = goodsIds.split(",");

        Integer sellerGoodsNum = this.sellerGoodsService.getSellerGoodsNum(partyId);
        Long noLevelGoodsNum = sysparaService.find("no_level_goods_num").getLong();
        if (Objects.isNull(seller.getMallLevel())
                && Objects.nonNull(noLevelGoodsNum)
                && noLevelGoodsNum > 0
                && sellerGoodsNum + goodsIdsArray.length > noLevelGoodsNum){
            resultObject.setCode("1");
            resultObject.setMsg("添加失败，当前等级可售商品数量："+noLevelGoodsNum);
            Map<String, String> data = new HashMap<>();
            data.put("noLevelGoodsNum", String.valueOf(noLevelGoodsNum));
            resultObject.setData(data);
            return resultObject;
        }

        MallLevel level = mallLevelService.findByLevel(seller.getMallLevel());
        if (Objects.nonNull(level) && level.getGoodsNum() > 0
                && sellerGoodsNum + goodsIdsArray.length > level.getGoodsNum()){
            resultObject.setCode("1");
            resultObject.setMsg("添加失败，当前等级可售商品数量："+noLevelGoodsNum);
            Map<String, String> data = new HashMap<>();
            data.put("noLevelGoodsNum", String.valueOf(level.getGoodsNum()));
            resultObject.setData(data);
            return resultObject;
        }
        for (String goodsId : goodsIdsArray) {
            SystemGoods systemGoods = adminMallGoodsService.findById(goodsId);
            if (systemGoods == null) {
                resultObject.setCode("1");
                resultObject.setMsg("商品id错误");
                return resultObject;
            }
//            2023-10-28 此处需保留俩位小数，钱包取俩位小数，如果实际sellingPrice为3.142，但是前端展示位3.14，钱包一直为3.14俩位会导致前端看起来钱够但是报错余额不足
            double sellingPrice = Arith.roundDown(Arith.mul(systemGoods.getSystemPrice(),Arith.add(1.00D, profit_ratio)),2);

            double orderPrice = sellingPrice;
            if (sellerService.getSeller(partyId) == null) {
                resultObject.setCode("1");
                resultObject.setMsg("您还没有通过商户审核无权操作");
                return resultObject;
            }

            SellerGoods sellerGoods;
            boolean update = false;
            try {
                sellerGoods = this.sellerGoodsService.getSellerGoods(goodsId, partyId);
                if (null != sellerGoods) {
                    if (sellerGoods.getIsValid() == 1) {
                        throw new BusinessException("不能重复添加商品");
                    }
                    update = true;
                } else {
                    sellerGoods = new SellerGoods();
                    sellerGoods.setId(IdUtils.getSellerGoodsId());
                    sellerGoods.setShowWeight1(0L);
                    sellerGoods.setShowWeight2(0L);
                }
            } catch (BusinessException ex) {
                resultObject.setCode("1");
                resultObject.setMsg(ex.getMessage());
                return resultObject;
            }
            sellerGoods.setGoodsId(goodsId);
            sellerGoods.setSellerId(partyId);
//        sellerGoods.setSystemGoods(systemGoods);
            sellerGoods.setCategoryId(systemGoods.getCategoryId());
            sellerGoods.setSecondaryCategoryId(systemGoods.getSecondaryCategoryId());
            sellerGoods.setIsShelf(1);
            sellerGoods.setIsValid(1);
            sellerGoods.setRecTime(0L);
            sellerGoods.setSystemRecTime(0L);
            sellerGoods.setSellWellTime(0L);
            sellerGoods.setSystemPrice(systemGoods.getSystemPrice());
            sellerGoods.setSellingPrice(sellingPrice);
            sellerGoods.setProfitRatio(profit_ratio);
            if (has_discount) {
//              2023-10-28 统一保留俩位小数，直接舍去
                double discountPrice = Arith.roundDown(Arith.mul(sellingPrice,Arith.sub(1.00D, discount_ratio)),2);
                sellerGoods.setDiscountPrice(discountPrice);
                sellerGoods.setDiscountRatio(discount_ratio);
                sellerGoods.setDiscountStartTime(discountStartTime);
                sellerGoods.setDiscountEndTime(discountEndTime);
                orderPrice = sellerGoods.getDiscountPrice();
            }
            sellerGoods.setOrderPrice(orderPrice);
            sellerGoods.setViewsNum(0);
            sellerGoods.setNewTime(0L);
            sellerGoods.setSystemNewTime(0L);
            sellerGoods.setSoldNum(0);
            sellerGoods.setUpTime(now.getTime());
            // 第一次上架时间，不能再改
            sellerGoods.setFirstShelfTime(now.getTime());
            sellerGoods.setCreateTime(now);
            sellerGoods.setIsShelf(1);
            sellerGoods.setBuyMin(systemGoods.getBuyMin() == null ? 1 : systemGoods.getBuyMin());

            // 2023-12-16 新增代码
            sellerGoodsService.fillSellerGoodsSkus(sellerGoods);

            if (update) {
                sellerGoodsService.updateSellerGoods(sellerGoods);
            } else {
                sellerGoodsService.saveSellerGoods(sellerGoods);
            }
            // 将废弃 TODO
            //sellerGoodsService.saveSellerGoodsSkus(sellerGoods);

            // 发布一个商品信息变更的事件
            WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
            SellerGoodsUpdateInfo info = new SellerGoodsUpdateInfo();
            info.setSellerGoodsId(sellerGoods.getId().toString());
            info.setUpdateTime(System.currentTimeMillis());
            wac.publishEvent(new SellerGoodsUpdateEvent(this, info));
        }

        resultObject.setMsg("操作成功");
        return resultObject;
    }

    @PostMapping(action + "delete.action")
    public Object delete(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        if (sellerGoodsId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("商品id不能为空");
            return resultObject;
        }

        String partyId = getLoginPartyId();
        String[] splitGoodsIds = sellerGoodsId.split(",");
        List<String> goodsIds = Arrays.stream(splitGoodsIds).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(goodsIds)) {
            Syspara syspara = sysparaService.findByDB("seller_min_goods_off_number");
            if (null != syspara) {
                long goodNum = sellerGoodsService.getOnSelfGoodsNumBySellerId(partyId);
                long remain = goodNum - goodsIds.size();
                if (remain < Integer.valueOf(syspara.getValue()).intValue()) {
                    resultObject.setCode("1");
                    resultObject.setMsg("剩余商品少于店铺设置最小下架商品数");
                    return resultObject;
                }
            }
        }
        for (String goodsId : splitGoodsIds) {
            if (!StringUtils.isEmptyString(goodsId)) {
                sellerGoodsService.deleteSellerGoods(goodsId, partyId);
            }
        }

        resultObject.setMsg("操作成功");
        return resultObject;
    }


    @PostMapping(action + "shelfBatch.action")
    public Object shelfBatch(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        if (sellerGoodsId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("商品id不能为空");
            return resultObject;
        }
        String isShelfStr = request.getParameter("isShelf");
        if (isShelfStr == null) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数isShelf");
            return resultObject;
        }

        int isShelf = Integer.parseInt(isShelfStr);
        String partyId = getLoginPartyId();
//        String partyId = "976288bf82df802b0182df92f8420003";
        List<String> goodsIds = Arrays.stream(sellerGoodsId.split(",")).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        if (isShelf == 0 && CollectionUtil.isNotEmpty(goodsIds)) {
            Syspara syspara = sysparaService.findByDB("seller_min_goods_off_number");
            if (null != syspara) {
                long goodNum = sellerGoodsService.getOnSelfGoodsNumBySellerId(partyId);
                long remain = goodNum - goodsIds.size();
                if (remain < Integer.valueOf(syspara.getValue()).intValue()) {
                    resultObject.setCode("1");
                    resultObject.setMsg("剩余商品少于店铺设置最小下架商品数");
                    return resultObject;
                }
            }
        }

        sellerGoodsService.shelfBatch(goodsIds, partyId, isShelf);

        // 发布商品信息变更的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        for (String oneSellerGoodId : goodsIds) {
            SellerGoodsUpdateInfo info = new SellerGoodsUpdateInfo();
            info.setSellerGoodsId(oneSellerGoodId);
            info.setUpdateTime(System.currentTimeMillis());
            wac.publishEvent(new SellerGoodsUpdateEvent(this, info));
        }

        resultObject.setMsg("操作成功");
        return resultObject;
    }

    @GetMapping(action + "listSellerComboSellerGoods.action")
    public Object listSellerComboSellerGoods(HttpServletRequest request) {
        String sellerId = request.getParameter("sellerId");
        ResultObject resultObject = new ResultObject();
        resultObject.setCode("0");
        resultObject.setData(sellerGoodsService.getSellerComboActiveGoods(sellerId));
        return resultObject;
    }

    @GetMapping(action + "viewsAdd.action")
    public Object viewsAdd(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        goodsOrdersService.updateAutoIncreaseViewCount();
        resultObject.setCode("0");
        return resultObject;
    }


    @PostMapping(action + "update.action")
    public Object update(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String partyId = getLoginPartyId();
        if (StringUtils.isEmptyString(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("登录已过期");
            return resultObject;
        }
        //String partyId = "ff808081849f91c10184a08b70220029";
        String isShelfStr = request.getParameter("isShelf");
        String recTimeStr = request.getParameter("recTime");
        String isCombo = request.getParameter("isCombo");
        String discount_str = request.getParameter("discount");
        String percent_str = request.getParameter("percent");
        String profit_str = request.getParameter("profit");

        Seller seller = this.sellerService.getSeller(partyId);
        if (null == seller || null == seller.getStatus() || seller.getStatus() == 0) {
            resultObject.setCode("1");
            resultObject.setMsg("店铺未审核通过暂不能上架商品");
            return resultObject;
        }
        if (StrUtil.isBlank(isShelfStr)) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数isShelf");
            return resultObject;
        }
        Integer shelf = Integer.valueOf(isShelfStr);

        if (StrUtil.isBlank(recTimeStr)) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数recTime");
            return resultObject;
        }
        if (StrUtil.isBlank(isCombo)) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数isCombo");
            return resultObject;
        }

        if (shelf == 1) {
            // 上架操作
            // 判断上架金额限制
            double minRechargeAmount = 500;
            Syspara contentParam = this.sysparaService.find(SysParaCode.ONSHELF_RECHARGE_AMOUNT_LIMIT.getCode());
            if (contentParam != null) {
                minRechargeAmount = Double.parseDouble(contentParam.getValue().trim());
            }
            UserMetrics userMetrics = userMetricsService.getByPartyId(partyId);
            double userRechargeTotal = 0.0;
            if (userMetrics != null) {
                userRechargeTotal = userMetrics.getMoneyRechargeAcc();
            }
            if (userRechargeTotal < minRechargeAmount) {
                resultObject.setCode("1");
                resultObject.setMsg("首次上架账户资金≥{_$1}，请充值后再试");

                Map<String, String> data = new HashMap<>();
                data.put("_$1", String.valueOf(minRechargeAmount));
                resultObject.setData(data);

                return resultObject;
            }
        }

        Date discountStartTime = null;
        Date discountEndTime = null;
        double profit_ratio = 0.00;
        double discount_ratio = 0.00;
        boolean has_discount = false;
        try {
            profit_ratio = Double.valueOf(percent_str);
            discount_ratio = Double.valueOf(discount_str);
        } catch (Exception ex) {
            resultObject.setCode("1");
            resultObject.setMsg("输入的利润比或者折扣比，不符合规范，请重新填写");
            return resultObject;
        }

        if (discount_ratio >= 0.00 && discount_ratio < 1.00) {
            try {
                if (discount_ratio > 0.00) {
                    String startTime = request.getParameter("startTime");
                    String endTime = request.getParameter("endTime");
                    discountStartTime = format.parse(startTime);
                    discountEndTime = format.parse(endTime);
                }
                has_discount = true;
            } catch (Exception ex) {
                resultObject.setCode("1");
                resultObject.setMsg("请正确填写活动开启时间和结束时间");
                return resultObject;
            }
        }

        String sellerGoodsId = request.getParameter("sellerGoodsId");
        if (sellerGoodsId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数sellerGoodsId");
            return resultObject;
        }

        SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(sellerGoodsId);
        if (sellerGoods == null) {
            resultObject.setCode("1");
            resultObject.setMsg("参数错误");
            return resultObject;
        }
        if (Integer.valueOf(isShelfStr).equals(1)) {
            sellerGoods.setCreateTime(new Date());
        }
        sellerGoods.setIsCombo(Integer.valueOf(isCombo));

        Integer isShelf = sellerGoods.getIsShelf();
        if (shelf == 0 && null != isShelf && isShelf.intValue() == 1) {
            Syspara syspara = sysparaService.findByDB("seller_min_goods_off_number");
            if (syspara != null) {
                long goodNum = sellerGoodsService.getOnSelfGoodsNumBySellerId(sellerGoods.getSellerId()); //上架商品数
                int minGoodNum = Integer.parseInt(syspara.getValue()); //最小下架商品数
                if ((goodNum - 1) < minGoodNum) {
                    resultObject.setCode("1");
                    resultObject.setMsg("少于店铺设置最小下架商品数");
                    return resultObject;
                }
            }
        }
        sellerGoods.setIsShelf(shelf);
        if (sellerGoods.getIsShelf().intValue() == 1) {
            // upTime 不代表最近上架时间
            sellerGoods.setUpTime(System.currentTimeMillis());
        }
        long rec_time = 0;
        try {
            rec_time = Long.valueOf(recTimeStr);
        } catch (Exception ex) {

        }

        sellerGoods.setRecTime(rec_time);
        ComboUser comboUser = comboService.findComboUserByPartyId(partyId);
        // 剩下的直通车名额
        int leftPromoteNum = 0;
        // 当前总的直通车商品名额
        int promoteNum = 0;
        // 当前已经激活的直通车商品
        Set<String> comboActiveGoods = null;
        // 如果已经开通了直通车
        if (comboUser != null) {
            promoteNum = comboUser.getPromoteNum();
            // 提取有效的，使用了流量套餐的商品记录
            comboActiveGoods = sellerGoodsService.getSellerComboActiveGoods(partyId).stream().map(s -> s.getId()).collect(Collectors.toSet());
            leftPromoteNum = promoteNum - comboActiveGoods.size();
        }


//        if(recTimeStr.equals("1")) {
//
//            if(Objects.nonNull(comboUser)) {
//                if (comboUser.getStopTime() < System.currentTimeMillis()) {
//                    resultObject.setCode("1");
//                    resultObject.setMsg("您的直通车已到期");
//                    return resultObject;
//                }
//            }
//        }


        if (isCombo.equals("1")) {
            if (comboUser == null) {
                resultObject.setCode("1");
                resultObject.setMsg("您暂未购买直通车套餐，请购买再试试");
                return resultObject;
            }
            if (comboUser.getStopTime() < System.currentTimeMillis()) {
                resultObject.setCode("1");
                resultObject.setMsg("您的直通车已到期");
                return resultObject;
            }
            // 如果当前商品在激活的直通车中，什么也不做。如果不在，当前已经激活的份额已经满了，则报错
            if (!comboActiveGoods.contains(sellerGoodsId)) {
                if (promoteNum <= comboActiveGoods.size()) {
                    resultObject.setCode("1");
                    Map<String, String> data = new HashMap<>(1);
                    data.put("_$1", String.valueOf(promoteNum));
                    resultObject.setMsg("最多推广商品数量为{_$1}");
                    resultObject.setData(data);
                    return resultObject;
                }
                // 如果当前新加入直通车的商品不在已经有的范围之内，可用的直通车商品名额还要减去1
                leftPromoteNum--;
            }

            sellerGoods.setStopTime(comboUser.getStopTime());
        } else {
            sellerGoods.setStopTime(0l);
        }
        sellerGoods.setSellingPrice(Arith.mul(sellerGoods.getSystemPrice(),
                Arith.add(1.00, profit_ratio)));
        sellerGoods.setProfitRatio(profit_ratio);
        if (has_discount) {
            if (discount_ratio == 0.00) {
                sellerGoods.setDiscountPrice(0.00);
                sellerGoods.setDiscountStartTime(null);
                sellerGoods.setDiscountEndTime(null);

            } else {
                sellerGoods.setDiscountPrice(Arith.mul(sellerGoods.getSellingPrice(),
                        Arith.sub(1.00, discount_ratio)));
                sellerGoods.setDiscountStartTime(discountStartTime);
                sellerGoods.setDiscountEndTime(discountEndTime);
            }
            sellerGoods.setDiscountRatio(discount_ratio);
        }

        // 2023-12-16 新增代码，最新逻辑，又废弃
        // sellerGoodsService.fillSellerGoodsSkus(sellerGoods);
        if(isShelfStr.equals("0")){
            sellerGoods.setIsShelf(1);
            sellerGoods.setCheckFlag("0");
        }
        sellerGoodsService.updateSellerGoods(sellerGoods);
        sellerGoodsService.updateSellerGoodsSkus(sellerGoods);

        // 发布一个商品信息变更的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        SellerGoodsUpdateInfo info = new SellerGoodsUpdateInfo();
        info.setSellerGoodsId(sellerGoodsId);
        info.setUpdateTime(System.currentTimeMillis());
        wac.publishEvent(new SellerGoodsUpdateEvent(this, info));

        JSONObject object = new JSONObject();
        object.put("leftPromoteNum", leftPromoteNum);
//        object.put("pageInfo", null);
//        object.put("pageList", null);
        resultObject.setData(object);
        resultObject.setMsg("操作成功");
        return resultObject;
    }
    /**
     * 批量更新折扣和利润比例
     *
     * @param request
     * @return
     */
    @PostMapping(action + "updateCheckFlag.action")
    public ModelAndView updateCheckFlag(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String checkFlag = request.getParameter("checkFlag");

        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(id);
            sellerGoods.setCheckFlag(checkFlag);
            if(checkFlag.equals("1")){
                sellerGoods.setIsShelf(0);
            }
            sellerGoodsService.updateSellerGoods(sellerGoods);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" +  "mall/goods/sellerGoodsList.action");
        return modelAndView;
    }

    /**
     * 批量更新折扣和利润比例
     *
     * @param request
     * @return
     */
    @PostMapping(action + "updateDisProBatch.action")
    public Object updateDiscountProfitBatch(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        String partyId = getLoginPartyId();
        if (StringUtils.isEmptyString(partyId)) {
            resultObject.setCode("403");
            resultObject.setMsg("登录已过期");
            return resultObject;
        }
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        String discount_str = request.getParameter("discount");
        String percent_str = request.getParameter("percent");

        Seller seller = this.sellerService.getSeller(partyId);
        if (null == seller || null == seller.getStatus() || seller.getStatus() == 0) {
            resultObject.setCode("1");
            resultObject.setMsg("店铺未审核通过暂不能上架商品");
            return resultObject;
        }

        List<String> goodsIds = Arrays.stream(sellerGoodsId.split(",")).filter(StringUtils::isNotEmpty).collect(Collectors.toList());
        Date discountStartTime = null;
        Date discountEndTime = null;
        double profit_ratio = 0.00;
        double discount_ratio = 0.00;
        boolean has_discount = false;
        try {
            profit_ratio = Double.valueOf(percent_str);
            discount_ratio = Double.valueOf(discount_str);
        } catch (Exception ex) {
            resultObject.setCode("1");
            resultObject.setMsg("输入的利润比或者折扣比，不符合规范，请重新填写");
            return resultObject;
        }

        if (discount_ratio >= 0.00 && discount_ratio < 1.00) {
            try {
                if (discount_ratio > 0.00) {
                    String startTime = request.getParameter("startTime");
                    String endTime = request.getParameter("endTime");
                    discountStartTime = format.parse(startTime);
                    discountEndTime = format.parse(endTime);
                }
                has_discount = true;
            } catch (Exception ex) {
                resultObject.setCode("1");
                resultObject.setMsg("请正确填写活动开启时间和结束时间");
                return resultObject;
            }
        }
        if (sellerGoodsId == null) {
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数sellerGoodsId");
            return resultObject;
        }
        sellerGoodsService.updateDisProBatchBatch(goodsIds, partyId, has_discount, discountStartTime, discountEndTime, discount_ratio, profit_ratio);
        List<SellerGoods> sellerGoodsBatch = sellerGoodsService.getSellerGoodsBatch(goodsIds);
        sellerGoodsBatch.forEach(sellerGoods -> {
            if (sellerGoods == null) {
                return;
            }

            // 2023-12-16 新增代码，最新逻辑，又废弃
            //sellerGoodsService.fillSellerGoodsSkus(sellerGoods);
            sellerGoodsService.updateSellerGoods(sellerGoods);

            // 将废弃 TODO
            sellerGoodsService.updateSellerGoodsSkus(sellerGoods);
        });

        // 发布商品信息变更的事件
        WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
        for (String oneSellerGoodId : goodsIds) {
            SellerGoodsUpdateInfo info = new SellerGoodsUpdateInfo();
            info.setSellerGoodsId(oneSellerGoodId);
            info.setUpdateTime(System.currentTimeMillis());
            wac.publishEvent(new SellerGoodsUpdateEvent(this, info));
        }

        resultObject.setMsg("操作成功");
        return resultObject;
    }


    @PostMapping( action+"sellerGoodsSkuInfo.action")
    public Object sellerGoodsSkuInfo(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        JSONObject o = new JSONObject();
        String lang = this.getLanguage(request);
        String sellerGoodsId = request.getParameter("sellerGoodsId");

        // 取代 TODO
        List<SellerGoodsSkuInfoVO> sellerGoodsSkuList = sellerGoodsService.listSellerGoodsSku(sellerGoodsId);
        List<SellerGoodsSkuDto> sellerGoodsSkuDtoList = new ArrayList<>();
        List<GoodsAttributeVo> goodsAttributeList = new ArrayList<>();
        for (SellerGoodsSkuInfoVO sellerGoodsSku : sellerGoodsSkuList) {
            SellerGoodsSkuDto sellerGoodsSkuDto = new SellerGoodsSkuDto();
            BeanUtil.copyProperties(sellerGoodsSku, sellerGoodsSkuDto);
            List<GoodsAttributeVo> cachedGoodsAttributeBySkuId = goodsSkuAtrributionService.findCachedGoodsAttributeBySkuId(sellerGoodsSku.getSkuId(), lang);
            if (CollectionUtils.isNotEmpty(cachedGoodsAttributeBySkuId)){
                sellerGoodsSkuDto.setGoodsAttributeVos(cachedGoodsAttributeBySkuId);
                sellerGoodsSkuDto.setSkuId(sellerGoodsSku.getSkuId());
                sellerGoodsSkuDtoList.add(sellerGoodsSkuDto);
                goodsAttributeList.addAll(cachedGoodsAttributeBySkuId);
            }
        }
        List<String> attrNames = goodsAttributeList.stream()
                .filter(goodsAttributeVo -> goodsAttributeVo.getAttrName() != null)
                .map(GoodsAttributeVo::getAttrName)
                .distinct() // 去重
                .collect(Collectors.toList());
        o.put("sellerGoodsSkuDtoList", sellerGoodsSkuDtoList);
        o.put("attrNames", attrNames);
        resultObject.setData(o);
        return resultObject;
    }

    @PostMapping(action + "search-goods.action")
    public Object searchGoods(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String lang = this.getLanguage(request);

        String keyword = request.getParameter("keyword");
        if (org.apache.commons.lang3.StringUtils.isBlank(keyword)) {
            resultObject.setCode("1");
            resultObject.setMsg("请输入搜索关键字");
            return resultObject;
        }
        String sellerId = getLoginPartyId();
        if (org.apache.commons.lang3.StringUtils.isBlank(sellerId)) {
            resultObject.setCode("1");
            resultObject.setMsg("商家id不能为空");
            return resultObject;
        }
        PageInfo pageInfo = getPageInfo(request);

        JSONArray jsonArray = new JSONArray();
        // 待取代 TODO
        //List<SellerGoods> list = sellerGoodsService.querySearchsellerGoods1(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerId, keyword, lang);
        List<SellerGoods> list = sellerGoodsService.querySearchsellerGoods(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerId, keyword, lang);
        List<String> sellerGoodsId = list.stream().map(s -> s.getId().toString()).collect(Collectors.toList());
        Map<String, Long> viewNums = sellerGoodsService.getViewNums(sellerGoodsId);
        for (SellerGoods pl : list) {
            String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + pl.getGoodsId());
            if (StringUtils.isEmptyString(js)) {
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
            Date discountEndTime = pl.getDiscountEndTime();
            Date discountStartTime = pl.getDiscountStartTime();
            Double discountRatio = pl.getDiscountRatio();
            if (null != discountStartTime && null != discountEndTime && discountRatio != discountRatio
                    && discountStartTime.before(new Date()) && discountEndTime.after(new Date()) && discountRatio > 0.0D) {
                goodsVo.setDiscountPrice(pl.getDiscountPrice());
            }
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
            goodsVo.setProfitRatio(pl.getProfitRatio());
            jsonArray.add(goodsVo);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }
}
