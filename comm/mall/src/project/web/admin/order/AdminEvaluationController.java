package project.web.admin.order;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.mall.MallRedisKeys;
import project.mall.evaluation.EvaluationService;
import project.mall.goods.SellerGoodsService;
import project.mall.goods.model.*;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.mall.utils.MallPageInfo;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * 管理后台商品评论管理
 */
@RestController
@CrossOrigin
public class AdminEvaluationController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminEvaluationController.class);

    @Resource
    protected EvaluationService evaluationService;

    @Autowired
    private SellerGoodsService sellerGoodsService;

    @Autowired
    private SellerService sellerService;

    @Autowired
    private PartyService partyService;

    @Autowired
    private RedisHandler redisHandler;

    /**
     *
     * 列表查询
     */
    @CrossOrigin
    @RequestMapping(value = "/systemGoods/evaluation/list.action")
    public ResultObject list(HttpServletRequest request) {
        String lang = this.getLanguage(request);
        String pageNoStr = request.getParameter("pageNo");
        String pageSizeStr = request.getParameter("pageSize");
        String error = request.getParameter("error");
        String userName = request.getParameter("username");
        String systemGoodsId = request.getParameter("systemGoodsId");
        String accountType = request.getParameter("accountType");
        String evaluationTypeStr = request.getParameter("evaluationType");
        Integer evaluationType = 0;
        int pageSize = 20;
        int pageNo = 1;

        ResultObject resultObject = new ResultObject();
        try {
            if (StringUtils.isNotEmpty(evaluationTypeStr) && StringUtils.isNumeric(evaluationTypeStr)) {
                evaluationType = Integer.parseInt(evaluationTypeStr);
            }
            if (StrUtil.isNotBlank(pageSizeStr)) {
                pageSize = Integer.parseInt(pageSizeStr);
            }
            if (StrUtil.isBlank(pageNoStr)) {
                pageNoStr = "1";
            }
            pageNo = Integer.parseInt(pageNoStr);
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("参数值无法正确解析");
            return resultObject;
        }

        try {
            /**
             * 用评论来源类型囊括账号类型，可以支持后续更多的评论分类
             */
            int sourceType = 0;
            if (StrUtil.isBlank(accountType) || accountType.equals("0")) {
                // 所有的账号类型
                sourceType = 0;
            } else if (Objects.equals(accountType, "1")) {
                // 来自普通账号的评论
                sourceType = 1;
            } else if (Objects.equals(accountType, "2")) {
                // 来自演示账号的评论
                sourceType = 2;
            } else if (Objects.equals(accountType, "3")) {
                // 来自演示账号的评论
                sourceType = 3;
            }

            MallPageInfo mallPageInfo = evaluationService.listEvaluationBySellerId(pageNo, pageSize, null, userName, evaluationType, sourceType, systemGoodsId);
            List<Evaluation> list = mallPageInfo.getElements();

            List<String> sellerGoodsIdList = new ArrayList<>();
            List<String> sellerIdList = new ArrayList();
            List<String> buyerIdList = new ArrayList();
            for (Evaluation evaluation : list) {
                sellerGoodsIdList.add(evaluation.getSellerGoodsId());
                sellerIdList.add(evaluation.getSellerId());
                buyerIdList.add(StrUtil.isBlank(evaluation.getPartyId()) ? "0" : evaluation.getPartyId());
            }
            List<SellerGoods> sellerGoodsList = sellerGoodsService.getSellerGoodsBatch(sellerGoodsIdList);
            Map<String, SellerGoods> goodsMap = new HashMap<>();
            for (SellerGoods oneGoods : sellerGoodsList) {
                goodsMap.put(oneGoods.getId().toString(), oneGoods);
            }
            List<Seller> sellerList = sellerService.getSellerBatch(sellerIdList);
            Map<String, Seller> sellerMap = new HashMap<>();
            for (Seller oneSeller : sellerList) {
                sellerMap.put(oneSeller.getId().toString(), oneSeller);
            }
            List<Party> partyList = partyService.getPartyBatch(buyerIdList);
            Map<String, Party> partyMap = new HashMap<>();
            for (Party oneParty : partyList) {
                partyMap.put(oneParty.getId().toString(), oneParty);
            }

            List<AdminEvaluationVo> pageDtoList = new ArrayList<>();
            for (Evaluation evaluation : list) {
                AdminEvaluationVo adminEvaluationVo = new AdminEvaluationVo();
                pageDtoList.add(adminEvaluationVo);

                BeanUtils.copyProperties(evaluation, adminEvaluationVo);
                adminEvaluationVo.setCommentTime(DateUtil.formatDateTime(evaluation.getEvaluationTime()));
                adminEvaluationVo.setAvatar(evaluation.getPartyAvatar());

                Seller seller = sellerMap.get(evaluation.getSellerId());
                if (seller != null) {
                    adminEvaluationVo.setSellerName(seller.getName());
                }

                // 注意：虚假评论对应的 partyId = 0, 目前将其归类到普通用户里去了。
                Party party = partyMap.get(StrUtil.isBlank(evaluation.getPartyId()) ? "0" : evaluation.getPartyId());
                adminEvaluationVo.setAccountType(1);
                if (party != null) {
                    if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_MEMBER)) {
                        adminEvaluationVo.setAccountType(1);
                    } else if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_GUEST)) {
                        adminEvaluationVo.setAccountType(2);
                    } else if (party.getRolename().equalsIgnoreCase(Constants.SECURITY_ROLE_TEST)) {
                        adminEvaluationVo.setAccountType(3);
                    }
                }

                SellerGoods sellerGoods = goodsMap.get(evaluation.getSellerGoodsId());
                if (sellerGoods != null) {
                    String js = redisHandler.getString(MallRedisKeys.MALL_GOODS_LANG + lang + ":" + sellerGoods.getGoodsId());
                    if (StrUtil.isNotBlank(js)) {
                        SystemGoodsLang pLang = JSONArray.parseObject(js, SystemGoodsLang.class);

                        GoodsVo goodsVo = new GoodsVo();
                        BeanUtils.copyProperties(sellerGoods, goodsVo);
                        goodsVo.setName(pLang.getName());
                        goodsVo.setUnit(pLang.getUnit());
                        goodsVo.setDes(pLang.getDes());
                        goodsVo.setImgDes(pLang.getImgDes());
                        adminEvaluationVo.setGoodsVo(goodsVo);
                    }
                }
            }

            Page pageInfo = new Page(pageNo, pageSize, mallPageInfo.getTotalElements());
            pageInfo.setElements(pageDtoList);

            resultObject.setData(pageInfo);
            return resultObject;
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("操作失败");
            return resultObject;
        }
    }

    @PostMapping("/admin/evaluation/addFakeComment.action")
    public ModelAndView addFakeComment(HttpServletRequest request,  Evaluation evaluation) {
        // 手机号或邮箱
        String pageNo = request.getParameter("pageNo");

        Integer evaluationType = 1;
        int rating = 5;

        rating = evaluation.getRating();
        if (rating >= 4) {
            evaluationType = 1;
        } else if (rating >= 3) {
            evaluationType = 2;
        } else {
            evaluationType = 3;
        }

        // 随机选择图标
        Random random = new Random();
        int avatarNum = (1 + random.nextInt(20));
        String avatar = avatarNum + "";

        evaluation.setPartyAvatar(avatar);
        evaluation.setEvaluationType(evaluationType);
        ModelAndView modeView = new ModelAndView();
        modeView.setViewName("redirect:/" +  "/mall/goods/sellerGoodsList.action");
        try {
            evaluationService.addFakeEvaluation(evaluation);
        } catch (BusinessException e) {
            modeView.addObject("error", e.getMessage());
        } catch (Throwable t) {
            logger.error(" error ", t);
            modeView.addObject("error", "[ERROR] " + t.getMessage());
        }
        modeView.addObject("message", "操作成功");
        modeView.addObject("pageNo", pageNo);
        return modeView;
    }

    /**
     * 批量统一修改演示账号的商品评论时间
     *
     * @param request
     * @return
     */
    @CrossOrigin
    @PostMapping("/systemGoods/evaluation/changeTime.action")
    public ResultObject changeEvaluaTime(HttpServletRequest request) {
        // 手机号或邮箱
        String fromTimeStr = request.getParameter("fromTime");
        String toTimeStr = request.getParameter("toTime");
        String sellerId = request.getParameter("sellerId");

        ResultObject resultObject = new ResultObject();
        try {
            Date fromTime = DateUtil.parseDateTime(fromTimeStr);
            Date toTime = DateUtil.parseDateTime(toTimeStr);
            if (toTime.before(fromTime)) {
                throw new BusinessException("开始时间不能大于截止时间");
            }
            if (toTime.getTime() > System.currentTimeMillis()) {
                throw new BusinessException("截止时间不能设置为未来时间");
            }

            evaluationService.updateEvaluationTime(sellerId, fromTime, toTime);

            resultObject.setCode("0");
            resultObject.setMsg("ok");
            return resultObject;
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e) {
            logger.error("更新商铺:" + sellerId + " 的商品评论时间失败", e);
            resultObject.setCode("1");
            resultObject.setMsg("操作失败");
            return resultObject;
        }
    }

    /**
     * 隐藏（逻辑删除）商品评论；
     * 能够删除任何人的评论
     *
     * @param request
     * @return
     */
    @CrossOrigin
    @PostMapping("/systemGoods/evaluation/hide.action")
    public ResultObject hideEvaluaTime(HttpServletRequest request) {
        // 手机号或邮箱
        String id = request.getParameter("id");

        ResultObject resultObject = new ResultObject();
        evaluationService.updateHideEvaluation(id);

        resultObject.setCode("0");
        resultObject.setMsg("ok");
        return resultObject;
    }

    /**
     * 公开评论.
     *
     * @param request
     * @return
     */
    @CrossOrigin
    @PostMapping("/systemGoods/evaluation/open.action")
    public ResultObject openEvaluaTime(HttpServletRequest request) {
        // 手机号或邮箱
        String id = request.getParameter("id");

        ResultObject resultObject = new ResultObject();
        evaluationService.updateOpenEvaluation(id);

        resultObject.setCode("0");
        resultObject.setMsg("ok");
        return resultObject;
    }

}