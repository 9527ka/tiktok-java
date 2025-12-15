package project.web.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import project.blockchain.internal.FundChangeService;
import project.mall.area.MallAddressAreaService;
import project.mall.area.model.MallCountry;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.GoodsSkuAtrributionService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.Evaluation;
import project.mall.goods.model.EvaluationVo;
import project.mall.goods.model.GoodsAttributeVo;
import project.mall.goods.model.SellerGoods;
import project.mall.orders.GoodsOrdersService;
import project.mall.orders.model.MallOrdersGoods;
import project.mall.seller.MallLevelService;
import project.mall.seller.SellerService;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.mall.utils.EncryptUtil;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.web.api.dto.CountTypeDto;
import project.web.api.model.EvaluationAddListModel;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

@RestController
@CrossOrigin
public class EvaluationController extends BaseAction {
    @Resource
    protected RedisHandler redisHandler;

    @Resource
    protected EvaluationService evaluationService;
    @Resource
    protected PartyService partyService;
    @Resource
    protected GoodsSkuAtrributionService goodsSkuAtrributionService;
    @Resource
    protected SellerGoodsService sellerGoodsService;
    @Resource
    protected GoodsOrdersService goodsOrdersService;
    @Resource
    protected MallAddressAreaService adminMallCountryService;
    @Resource
    protected SellerService sellerService;
    @Resource
    protected SysparaService sysparaService;
    @Resource
    protected FundChangeService fundChangeService;
    @Resource
    protected MallLevelService mallLevelService;
    private final String action = "/api/evaluation!";

    /**
     * 临时操作，更新团队人数和直属人数
     */
    @GetMapping(action + "tmpTeam.action")
    public Object tmpTeam(HttpServletRequest request){
        // 提取用于店铺升级业务的有效充值用户的充值金额临界值
        double limitRechargeAmount = 100.0;
        // 提取用于店铺升级业务的计算团队人数充值金额临界值
        double limitRechargeAmountOnTeam = 100.0;
        Syspara syspara = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_SELLER_UPGRADE.getCode());
        Syspara sysparaOnTeam = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_TEAM_NUM.getCode());
        if (syspara != null) {
            String validRechargeAmountInfo = syspara.getValue().trim();
            if (StrUtil.isNotBlank(validRechargeAmountInfo)) {
                limitRechargeAmount = Double.parseDouble(validRechargeAmountInfo);
            }
        }
        if (sysparaOnTeam != null) {
            String rechargeAmountOnTeamInfo = sysparaOnTeam.getValue().trim();
            if (StrUtil.isNotBlank(rechargeAmountOnTeamInfo)) {
                limitRechargeAmountOnTeam = Double.parseDouble(rechargeAmountOnTeamInfo);
            }
        }
        // 对等级集合进行排序，方便升级判断
        List<MallLevel> levelEntityList = this.mallLevelService.listLevel();
        Map<String, Integer> levelSortMap = new HashMap<>();

        levelSortMap.put("C", 1);
        levelSortMap.put("B", 2);
        levelSortMap.put("A", 3);
        levelSortMap.put("S", 4);
        levelSortMap.put("SS", 5);
        levelSortMap.put("SSS", 6);

        CollUtil.sort(levelEntityList, new Comparator<MallLevel>() {
            @Override
            public int compare(MallLevel o1, MallLevel o2) {
                Integer seq1 = levelSortMap.get(o1.getLevel());
                Integer seq2 = levelSortMap.get(o2.getLevel());
                seq1 = seq1 == null ? 0 : seq1;
                seq2 = seq2 == null ? 0 : seq2;

                return seq1 - seq2;
            }
        });

        ResultObject resultObject = new ResultObject();
        int pageNum = 1;
        int pageSize = 50;
        // 防止死循环
        int acc = 0;
        while (true) {
            MallPageInfo mallPageInfo = sellerService.listSeller(pageNum, pageSize, null);
            pageNum++;
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                logger.error("tmpTeam迁移团队人数时，发生中断");
            }
            List<Seller> list = mallPageInfo.getElements();
            if (CollectionUtil.isEmpty(list)) {
                logger.info("团队人数更新完毕！！！");
                break;
            }
            if (acc >= mallPageInfo.getTotalElements()) {
                logger.info("团队人数更新完毕！！！");
                break;
            }
            for (Seller seller : list) {
                acc++;
                try {
                    fundChangeService.updateTeamNumAndChildNumOnly(seller.getId().toString(),limitRechargeAmount,limitRechargeAmountOnTeam,
    //                        这里的amount必须要大于0 才会触发团队人数更新
                            levelEntityList, levelSortMap,0.01d);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("处理当前用户的上级团队人数异常，当前商家为："+seller.getName()+" 当前商家的UUID："+seller.getId().toString());
                }
            }
            logger.info("团队人数迁移完成第：{} 页的数据,一页50条数据，总数据量为：{} 条",pageNum,mallPageInfo.getTotalElements());
        }
        return resultObject;
    }

    @PostMapping(action + "list.action")
    public Object list(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        String evaluationType = request.getParameter("evaluationType");
        String lang = request.getParameter("lang");
        List<EvaluationVo>  resultList = new ArrayList<>();
        MallPageInfo mallPageInfo = evaluationService.listEvaluation(pageInfo.getPageNum(), pageInfo.getPageSize(), sellerGoodsId,evaluationType);

        List<Evaluation> list = mallPageInfo.getElements();
        for (Evaluation evaluation : list) {
            EvaluationVo evaluationVo = new EvaluationVo();
            BeanUtils.copyProperties(evaluation, evaluationVo);
            evaluationVo.setAvatar(evaluation.getPartyAvatar());
            try {
                evaluationVo.setCreateTimeStr(DateUtil.convertDateToString(evaluation.getEvaluationTime()));
                evaluationVo.setCommentTime(DateUtil.convertDateToString(evaluation.getEvaluationTime()));
            } catch (Exception e) {

            }
            if (StrUtil.isBlank(evaluation.getPartyAvatar())) {
                // caster 于 2023-3-16 修改
                // 兼容下旧记录
                Party party = partyService.findPartyByUsername(evaluation.getUserName());
                evaluationVo.setCreateTimeStr(DateUtil.formatDate(evaluation.getCreateTime(),DateUtil.DATE_FORMAT));
                evaluationVo.setPartyName(party.getName());
                evaluationVo.setAvatar(party.getAvatar());
                evaluationVo.setPartyAvatar(party.getAvatar());
            }
            if (StringUtils.isNotEmpty(evaluation.getSkuId()) && (!"-1".equals(evaluation.getSkuId()))) {
                List<String> skuIds = Arrays.asList(evaluation.getSkuId());
                Map<String, List<GoodsAttributeVo>> skuIdAttributes = goodsSkuAtrributionService.listGoodsAttributeBySkuIds(skuIds, lang);
                evaluationVo.setAttributes(skuIdAttributes.get(evaluation.getSkuId()));
            }
            Party party = partyService.cachePartyBy(evaluation.getPartyId(), false);
            if (StringUtils.isNotEmpty(evaluation.getUserName())&&(!evaluation.getUserName().contains("+"))) {
                if (evaluation.getUserName().contains("@")) {//邮箱
                    evaluationVo.setUserName(EncryptUtil.encrypt(evaluation.getUserName(), EncryptUtil.EncryptType.EMAIL));
                } else if (StringUtils.isNumeric(evaluation.getUserName())) {//手机号
                    if (Objects.nonNull(party)&& StringUtils.isNotEmpty(party.getPhone())) {
                        evaluationVo.setUserName(EncryptUtil.encrypt(party.getPhone(), EncryptUtil.EncryptType.PHONE));
                    }
                } else {//兼容老数据，虚拟账号的其他名称
                    evaluationVo.setUserName(EncryptUtil.encrypt(evaluation.getUserName(), EncryptUtil.EncryptType.NAME));
                }
            } else {
                evaluationVo.setUserName(EncryptUtil.encrypt(evaluation.getUserName().replace("+",""), EncryptUtil.EncryptType.PHONE));
            }

            // 评论增加属性显示 优化一次性查出来 TODO
            MallCountry mallCountry = adminMallCountryService.findCountryById(Long.valueOf(evaluation.getCountryId()));
            if (Objects.nonNull(mallCountry)) {
                String countryName = "";
                if ("cn".equalsIgnoreCase(lang)) {
                    countryName = mallCountry.getCountryNameCn();
                } else if ("tw".equalsIgnoreCase(lang)) {
                    countryName = mallCountry.getCountryNameTw();
                } else {//其他语言默认显示英文地址
                    countryName = mallCountry.getCountryNameEn();
                }
                evaluationVo.setCountryName(countryName);
            }
            resultList.add(evaluationVo);
        }

        JSONObject object = new JSONObject();
        pageInfo.setTotalElements(mallPageInfo.getTotalElements());
        object.put("pageInfo", pageInfo);
        object.put("pageList", resultList);
        object.put("evaluationNum", evaluationService.getEvaluationNumBySellerGoodsId(sellerGoodsId));
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 临时操作，完善商品评价表结构后，迁移旧评价数据.
     *
     * @param request
     * @return
     */
    @GetMapping(action + "tmpTransfer.action")
    public Object transferEvaluations(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        int pageNum = 1;
        int pageSize = 50;
        // 防止死循环
        int acc = 0;
        while (true) {
            MallPageInfo mallPageInfo = evaluationService.listEvaluation(pageNum, pageSize, null, "");
            pageNum++;
            List<Evaluation> list = mallPageInfo.getElements();
            if (CollectionUtil.isEmpty(list)) {
                break;
            }
            if (acc >= mallPageInfo.getTotalElements()) {
                break;
            }

            for (Evaluation evaluation : list) {
                acc++;
                if (StrUtil.isNotBlank(evaluation.getPartyId())
                        && !Objects.equals(evaluation.getPartyId(), "0")) {
                    // 已经完成了迁移
                    continue;
                }

                Party party = partyService.cachePartyByUsername(evaluation.getUserName(), false);
                if (party == null) {
                    // 可能是虚假用户的评论
                    // 这段 continue 可能造成死循环
                    continue;
                }

                if (StrUtil.isBlank(evaluation.getSkuId())) {
                    MallOrdersGoods mallOrdersGoods = goodsOrdersService.getMallOrdersGoods(evaluation.getOrderId(), evaluation.getSellerGoodsId());
                    // 迁移sku属性值
                    evaluation.setSkuId(Objects.nonNull(mallOrdersGoods) && Objects.nonNull(mallOrdersGoods.getSkuId()) ? mallOrdersGoods.getSkuId():"-1");
                }

                // 迁移属性值
                evaluation.setPartyId(party.getId().toString());
                evaluation.setEvaluationTime(evaluation.getCreateTime());
                evaluation.setPartyAvatar(party.getAvatar());
                evaluation.setPartyName(party.getName());
                evaluation.setSourceType(1);
                if (party.getRolename().equalsIgnoreCase("guest")) {
                    // 演示账户
                    evaluation.setSourceType(2);
                }
                evaluation.setTemplate("0");
                evaluation.setGoodsStatus(1);
                evaluationService.updateEvaluation(evaluation);
            }
        }

        return resultObject;
    }

    /**
     * 临时操作，完善商品评价表结构后，迁移旧评价数据.
     *
     * @param request
     * @return
     */
    @GetMapping(action + "tmpTransferSystemGoodsId.action")
    public Object transferSystemGoodsId(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        int pageNum = 1;
        int pageSize = 50;
        // 防止死循环
        int acc = 0;
        while (true) {
            MallPageInfo mallPageInfo = evaluationService.listEvaluation(pageNum, pageSize, null, "");
            pageNum++;
            List<Evaluation> list = mallPageInfo.getElements();
            if (CollectionUtil.isEmpty(list)) {
                break;
            }
            if (acc >= mallPageInfo.getTotalElements()) {
                break;
            }

            for (Evaluation evaluation : list) {
                acc++;
                if (StrUtil.isNotBlank(evaluation.getSystemGoodsId())
                        && !Objects.equals(evaluation.getSystemGoodsId(), "0")) {
                    // 已经完成了迁移
                    continue;
                }
                if (StrUtil.isBlank(evaluation.getSellerGoodsId()) || Objects.equals(evaluation.getSellerGoodsId(), "0")) {
                    continue;
                }

                SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(evaluation.getSellerGoodsId());
                if (sellerGoods == null) {
                    continue;
                }

                // 迁移属性值
                evaluation.setSystemGoodsId(sellerGoods.getGoodsId());
                evaluationService.updateEvaluation(evaluation);
            }
        }

        return resultObject;
    }

    /**
     * 临时操作，完善商品评价表结构后，迁移旧评价sku数据.
     *
     * @param request
     * @return
     */
    @GetMapping(action + "tmpSkuTransfer.action")
    public Object transferSkuEvaluations(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        int pageNum = 1;
        int pageSize = 50;
        while (true) {
            MallPageInfo mallPageInfo = evaluationService.listEvaluation(pageNum, pageSize, null, "");
            List<Evaluation> list = mallPageInfo.getElements();
            if (CollectionUtil.isEmpty(list)) {
                break;
            }
            for (Evaluation evaluation : list) {
                if (StrUtil.isNotBlank(evaluation.getSkuId())) {
                    // 已经完成了迁移
                    continue;
                }
                MallOrdersGoods mallOrdersGoods = goodsOrdersService.getMallOrdersGoods(evaluation.getOrderId(), evaluation.getSellerGoodsId());
                // 迁移sku属性值
                evaluation.setSkuId(Objects.nonNull(mallOrdersGoods) && Objects.nonNull(mallOrdersGoods.getSkuId()) ? mallOrdersGoods.getSkuId():"-1");
                evaluationService.updateEvaluation(evaluation);
            }
            pageNum++;
        }

        return resultObject;
    }


    @PostMapping(action + "countType.action")
    public ResultObject countType(@RequestParam String goodId) {
        ResultObject resultObject = new ResultObject();
         Map<String,String> map=evaluationService.getEvaluationTypeCountByGoodId(goodId);
        CountTypeDto countTypeDto=new CountTypeDto();
        countTypeDto.setHavePicture(map.getOrDefault("imgCount","0"));
        countTypeDto.setPositiveComments(map.getOrDefault("1","0"));
        countTypeDto.setMediumReview(map.getOrDefault("2","0"));
        countTypeDto.setNegativeComment(map.getOrDefault("3","0"));
        resultObject.setData(countTypeDto);
        return  resultObject;
    }


    /**
     * 添加
     *
     * @return
     */
    @PostMapping(action + "add.action")
    public Object add(HttpServletRequest request, Evaluation evaluation) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = this.getLoginPartyId();

        String sellerGoodsId = request.getParameter("sellerGoodsId");
        String evaluationType = request.getParameter("evaluationType");
        String rating = request.getParameter("rating");
        String content = request.getParameter("content");
        String orderId = request.getParameter("orderId");
        if (StringUtils.isEmptyString(sellerGoodsId) || StringUtils.isEmptyString(evaluationType)
                || StringUtils.isEmptyString(rating) ) {
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }

        try {
            SellerGoods sellerGoods = sellerGoodsService.getSellerGoods(sellerGoodsId);
            evaluationService.addEvaluation(partyId,sellerGoods.getSellerId(), sellerGoodsId, evaluationType, rating, content,orderId, "0", evaluation);
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("添加失败");
        }

        return resultObject;
    }

    /**
     * 添加多个
     *
     * @return
     */
    @PostMapping(action + "addList.action")
    public Object addList(@RequestBody EvaluationAddListModel model) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        try {
            evaluationService.addEvaluation(model.getEvaluationAdds(), partyId, model.getOrderId());
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("添加失败");
        }

        return resultObject;
    }



    /**
     * goodsRate
     *
     * @return
     */
    @PostMapping(action + "goodsRate.action")
    public Object goodsRate(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());

        String sellerGoodsId = request.getParameter("sellerGoodsId");
        if (org.apache.commons.lang3.StringUtils.isBlank(sellerGoodsId)) {
            resultObject.setCode("1");
            resultObject.setMsg("商品ID不能为空");
            return resultObject;
        }
        try {
            resultObject.setData(evaluationService.getHighOpinionByGoodsId(sellerGoodsId));
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("失败");
        }
        return resultObject;
    }

    /**
     * sellerRate
     *
     * @return
     */
    @PostMapping(action + "sellerRate.action")
    public Object sellerRate(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());

        String sellerId = request.getParameter("sellerId");
        if (org.apache.commons.lang3.StringUtils.isBlank(sellerId)) {
            resultObject.setCode("1");
            resultObject.setMsg("商家ID不能为空");
            return resultObject;
        }
        try {
            resultObject.setData(evaluationService.getHighOpinionBySellerId(sellerId));
        } catch (BusinessException e) {
            e.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            e1.printStackTrace();
            resultObject.setCode("1");
            resultObject.setMsg("失败");
        }
        return resultObject;
    }
}
