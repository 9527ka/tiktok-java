package project.web.api;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.alibaba.fastjson.JSONObject;
import jnr.posix.windows.CommonFileInformation;
import kernel.concurrent.ConcurrentQequestHandleStrategy;
import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.mall.MallRedisKeys;
import project.mall.banner.MallBannerService;
import project.mall.banner.model.MallBanner;
import project.mall.event.message.PurchaseOrderGoodsEvent;
import project.mall.event.model.PurchaseOrderInfo;
import project.mall.seller.MallLevelService;
import project.mall.seller.SellerService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.dto.MallLevelDTO;
import project.mall.seller.model.IdSerializableComparator;
import project.mall.seller.model.MallLevel;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.*;

@RestController
@CrossOrigin
public class MallLevelController extends BaseAction {

    private static Log logger = LogFactory.getLog(MallLevelController.class);

    @Resource
    protected MallLevelService mallLevelService;

    @Resource
    protected SellerService sellerService;

    @Resource
    protected SysparaService sysparaService;

    @Resource
    protected PartyService partyService;

    @Resource
    protected RedisHandler redisHandler;

    @Resource
    protected KycService kycService;


    private final String action = "/api/malllevel!";

    @GetMapping( action + "levelList.action")
    public Object mallLevelList(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        String currentLevel = "";
        String sellerId = this.getLoginPartyId();
        if (StrUtil.isNotBlank(sellerId) && !Objects.equals(sellerId, "0")) {
            Seller sellerEntity = sellerService.getSeller(sellerId);
            if (StrUtil.isNotBlank(sellerEntity.getMallLevel()) && !Objects.equals(sellerEntity.getMallLevel(), "0")) {
                currentLevel = sellerEntity.getMallLevel().trim();
            }
        }

        List<MallLevel> levelEntityList = this.mallLevelService.listLevel();
        Map<String, Integer> levelSortMap = new HashMap<>();
        levelSortMap.put("C", 1);
        levelSortMap.put("B", 2);
        levelSortMap.put("A", 3);
        levelSortMap.put("S", 4);
        levelSortMap.put("SS",5);
        levelSortMap.put("SSS",6);

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
        List<MallLevelDTO> levelInfoList = new ArrayList();

        for (MallLevel oneLevelEntity : levelEntityList) {
            MallLevelDTO oneDto = new MallLevelDTO();
            BeanUtil.copyProperties(oneLevelEntity, oneDto);
            oneDto.setCreateTime(DateUtil.formatDateTime(oneLevelEntity.getCreateTime()));
            oneDto.setUpdateTime(DateUtil.formatDateTime(oneLevelEntity.getUpdateTime()));
            levelInfoList.add(oneDto);
            oneDto.setSellerDiscount(Objects.nonNull(oneDto.getSellerDiscount())?BigDecimal.valueOf(oneDto.getSellerDiscount()).setScale(2,BigDecimal.ROUND_DOWN).doubleValue():0D);

            if (Objects.equals(oneLevelEntity.getLevel(), currentLevel)) {
                // 当前用户处于该等级
                oneDto.setMyLevel(1);
            }

            String cndJson = oneLevelEntity.getCondExpr();
            if (StrUtil.isNotBlank(cndJson)) {
                MallLevelCondExpr cndObj = JsonUtils.json2Object(cndJson, MallLevelCondExpr.class);
                List<MallLevelCondExpr.Param> params = cndObj.getParams();
                if (CollectionUtil.isNotEmpty(params)) {
                    for (MallLevelCondExpr.Param oneCndParam : params) {
                        UpgradeMallLevelCondParamTypeEnum cndType = UpgradeMallLevelCondParamTypeEnum.codeOf(oneCndParam.getCode().trim());
                        if (cndType == UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT) {
                            oneDto.setRechargeAmountCnd(Integer.parseInt(oneCndParam.getValue().trim()));
                        } else if (cndType == UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER) {
                            oneDto.setPopularizeUserCountCnd(Integer.parseInt(oneCndParam.getValue().trim()));
                        }
                    }
                }
            }
        }

        JSONObject object = new JSONObject();
        object.put("result", levelInfoList);
        resultObject.setData(object);

        return resultObject;
    }


    @GetMapping( action + "config.action")
    public ResultObject getMallLevelConfig(HttpServletRequest request) {
        ResultObject resultObject = new ResultObject();

        // 提取用于店铺升级业务的有效充值用户的充值金额临界值
        double limitRechargeAmount = 100.0;
        Syspara syspara = sysparaService.find(SysParaCode.VALID_RECHARGE_AMOUNT_FOR_SELLER_UPGRADE.getCode());
        if (syspara != null) {
            String validRechargeAmountInfo = syspara.getValue().trim();
            if (StrUtil.isNotBlank(validRechargeAmountInfo)) {
                limitRechargeAmount = Double.parseDouble(validRechargeAmountInfo);
            }
        }

        Map<String, Object> dataMap = new HashMap<>();
        dataMap.put("limitRechargeAmount", limitRechargeAmount);

        resultObject.setMsg("success");
        resultObject.setData(dataMap);
        return resultObject;
    }

    /**
     * 购买等级
     *
     * @return
     */
    @PostMapping(action + "levelBuy.action")
    public Object levelBuy(HttpServletRequest request) {
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String level = request.getParameter("level");
        String partyId = this.getLoginPartyId();

        String safeword = request.getParameter("safeword");

        if (StringUtils.isEmptyString(safeword)) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码不能为空");
            return resultObject;
        }

        if (safeword.length() < 6 || safeword.length() > 12) {
            resultObject.setCode("1");
            resultObject.setMsg("资金密码必须6-12位");
            return resultObject;
        }
        Party party = partyService.cachePartyBy(partyId, false);

        String partySafeword = party.getSafeword();
        if (StringUtils.isEmptyString(partySafeword)) {
            resultObject.setCode("1");
            resultObject.setMsg("请设置资金密码");
            return resultObject;
        }

        if (!party.getEnabled()) {
            resultObject.setCode("1");
            resultObject.setMsg("业务已锁定，请联系客服！");
            return resultObject;
        }

        try {
//            设置资金密码校验次数限制，根据系统参数 number_of_wrong_passwords 来变化
            String errorPassCount = sysparaService.find("number_of_wrong_passwords").getValue();
            if (Objects.isNull(errorPassCount)) {
                logger.error("number_of_wrong_passwords 系统参数未配置！");
                throw new BusinessException("参数异常");
            }
            String lockPassworkErrorKey = MallRedisKeys.MALL_PASSWORD_ERROR_LOCK + partyId;
            int needSeconds = util.DateUtils.getTomorrowStartSeconds();
            boolean exit = redisHandler.exists(lockPassworkErrorKey);//是否已经错误过
            if (exit && ("true".equals(redisHandler.getString(lockPassworkErrorKey)))) {//已经尝试错误过且次数已经超过number_of_wrong_passwords配置的次数
                throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
            } else if (exit && errorPassCount.equals(redisHandler.getString(lockPassworkErrorKey))) {//已经尝试密码错误过且次数刚好等于number_of_wrong_passwords配置的次数
                redisHandler.setSyncStringEx(lockPassworkErrorKey, "true", needSeconds);
                throw new BusinessException(1, "密码输入错误次数过多，请明天再试");
            } else {//失败次数小于配置次数或者未失败
                boolean checkSafeWord = this.partyService.checkSafeword(safeword, partyId);
                if (checkSafeWord) {//交易密码校验成功
                    redisHandler.remove(lockPassworkErrorKey);
                } else {//交易密码校验失败
                    if (exit) {//已经失败过，执行加1操作
                        redisHandler.incr(lockPassworkErrorKey);
                    } else {//未失败，set值，并计1
                        redisHandler.setSyncStringEx(lockPassworkErrorKey, "1", needSeconds);
                    }
                    throw new BusinessException(1, "资金密码错误");
                }
            }

            Integer isSupportLevel = sysparaService.find("level_is_support_purchase").getInteger();
            if (null == isSupportLevel || isSupportLevel != 1){
                throw new BusinessException("无法购买等级，请联系客服");
            }

            Kyc kyc = kycService.get(partyId);
            if (kyc.getStatus() != 2){
                throw new BusinessException("您没有认证商家，无法操作");
            }

            MallLevel buyLevel = mallLevelService.findByLevel(level);
            if (Objects.isNull(buyLevel)){
                throw new BusinessException("所购买的等级不存在");
            }

            Seller seller = sellerService.getSeller(partyId);
            if (null != seller.getMallLevel()){
                //当前用户所处等级
                MallLevel sellerLevel = mallLevelService.findByLevel(seller.getMallLevel());
                if (Integer.parseInt((String) sellerLevel.getId()) >= Integer.parseInt((String) buyLevel.getId())){
                    throw new BusinessException("无法购买低于当前的等级");
                }
            }
            sellerService.updateSellerLevel(seller,buyLevel);

        } catch (BusinessException e) {
            logger.error("采购业务异常", e);
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            return resultObject;
        } catch (Exception e1) {
            logger.error("采购未知异常", e1);
            resultObject.setCode("1");
            resultObject.setMsg("采购失败");
        }
        return resultObject;
    }



}
