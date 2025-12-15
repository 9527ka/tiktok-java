package project.web.admin;

import cn.hutool.core.bean.BeanUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.JsonUtils;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.checkerframework.checker.units.qual.A;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.invest.goods.model.GoodsLang;
import project.invest.vip.AdminVipService;
import project.invest.vip.model.Vip;
import project.log.LogService;
import project.mall.seller.constant.UpgradeMallLevelCondParamTypeEnum;
import project.mall.seller.dto.MallLevelCondExpr;
import project.mall.seller.dto.MallLevelDTO;
import project.mall.seller.dto.QueryMallLevelDTO;
import project.mall.seller.model.MallLevel;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;
import util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * vip配置
 */
@Slf4j
@RestController
@RequestMapping("/brush/vip")
public class AdminVipController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminVipController.class);

    @Resource
    protected AdminVipService adminVipService;

    @Resource
    private SecUserService secUserService;

    @Resource
    protected LogService logService;

    @Resource
    protected PasswordEncoder passwordEncoder;

    @Resource
    protected GoogleAuthService googleAuthService;

    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {

        this.checkAndSetPageNo(request.getParameter("pageNo"));
        this.pageSize = 30;
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        this.page = this.adminVipService.pagedQuery();

        ModelAndView model = new ModelAndView();
        List<QueryMallLevelDTO> levelInfoList = new ArrayList();
        List<MallLevel> list = page.getElements();
        for (MallLevel mallLevel : list) {
            mallLevel.setProfitRationMin(Arith.mul(mallLevel.getProfitRationMin(),100));
            mallLevel.setProfitRationMax(Arith.mul(mallLevel.getProfitRationMax(),100));
            mallLevel.setSellerDiscount(Arith.mul(mallLevel.getSellerDiscount(),100));

            MallLevelCondExpr mallLevelCondExpr = JsonUtils.json2Object(mallLevel.getCondExpr(), MallLevelCondExpr.class);
            List<MallLevelCondExpr.Param> params = mallLevelCondExpr.getParams();

            QueryMallLevelDTO oneDto = new QueryMallLevelDTO();
            BeanUtil.copyProperties(mallLevel, oneDto);
            params.forEach(e ->{
                if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode())){
                    oneDto.setRechargeAmount(Long.parseLong(e.getValue()));
                }
                if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode())){
                    oneDto.setPopularizeUserCount(Long.parseLong(e.getValue()));
                }
            });
            levelInfoList.add(oneDto);
        }
        model.addObject("page", levelInfoList);
        model.addObject("message", message);
        model.addObject("error", error);
        model.setViewName("admin_vip_list");
        return model;
    }

    /**
     * 跳转修改页面
     */
    @RequestMapping(value = "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {

        ModelAndView model = new ModelAndView();
        MallLevel mallLevel;
        model.setViewName("admin_vip_update");
        try {
            String id = request.getParameter("id");
            if( id == null ){
                throw new BusinessException("系统错误");
            }
            mallLevel = adminVipService.findById(id);
            if(mallLevel == null) {
                throw new BusinessException("刷新重试");
            }
            mallLevel.setProfitRationMin(Arith.mul(mallLevel.getProfitRationMin(),100));
            mallLevel.setProfitRationMax(Arith.mul(mallLevel.getProfitRationMax(),100));
            mallLevel.setSellerDiscount(Arith.mul(mallLevel.getSellerDiscount(),100));
            if (null !=mallLevel.getCondExpr()){
                MallLevelCondExpr mallLevelCondExpr = JsonUtils.json2Object(mallLevel.getCondExpr(), MallLevelCondExpr.class);
                List<MallLevelCondExpr.Param> params = mallLevelCondExpr.getParams();
                params.forEach(e ->{
                    if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode())){
                        model.addObject("rechargeAmount",e.getValue());
                    }
                    if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode())){
                        model.addObject("popularizeUserCount",e.getValue());
                    }
                    if (e.getCode().equals(UpgradeMallLevelCondParamTypeEnum.TEAM_NUM.getCode())){
                        model.addObject("teamNum",e.getValue());
                    }
                });
            }
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("mallLevel",mallLevel);
        return model;
    }

    /**
     * 修改游戏
     * @return
     */
    @RequestMapping(value = "/update.action")
    public ModelAndView update(@RequestParam Map<String, String> paramMap, MallLevel mallLevel) {

        String login_safeword = paramMap.get("login_safeword");
        ModelAndView model = new ModelAndView();
        String rechargeAmount = paramMap.get("rechargeAmount");
        String popularizeUserCount = paramMap.get("popularizeUserCount");
        String teamNum = paramMap.get("teamNum");
        try {
            if(null == mallLevel ||mallLevel.getId()==null) {
                throw new BusinessException("参数错误");
            }
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
//            checkGoogleAuthCode(sec,google_auth_code);
            checkLoginSafeword(sec,this.getUsername_login(),login_safeword);
            MallLevel data = adminVipService.findById(mallLevel.getId().toString());
            if(null == data){
                throw new BusinessException("此会员等级不存在，请刷新页面");
            }

            rechargeAmount = rechargeAmount == null ? "1" : rechargeAmount;
            popularizeUserCount = popularizeUserCount == null ? "1" : popularizeUserCount;

            MallLevelCondExpr mallLevelCondExpr = new MallLevelCondExpr();
            List<MallLevelCondExpr.Param> paramList = new ArrayList<>();
            paramList.add(new MallLevelCondExpr.Param(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode(),UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getTitle(),rechargeAmount));
            paramList.add(new MallLevelCondExpr.Param(UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode(),UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getTitle(),popularizeUserCount));
            paramList.add(new MallLevelCondExpr.Param(UpgradeMallLevelCondParamTypeEnum.TEAM_NUM.getCode(),UpgradeMallLevelCondParamTypeEnum.TEAM_NUM.getTitle(),teamNum));
            mallLevelCondExpr.setParams(paramList);

            StringBuilder expression = new StringBuilder();
            if ("0".equals(teamNum)) {//如果团队人数为0，就未开启团队人数校验
                //popularizeUserCount >= 3 || rechargeAmount >= 5000
                expression.append("#"+UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode());
                expression.append("  >= ").append(popularizeUserCount).append(" || ");
                expression.append("#"+UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode());
                expression.append("  >= ").append(rechargeAmount);
            }else {
//                ( #popularizeUserCount  >= 1 && #teamNum >= 2 ) || #rechargeAmount >= 5000
                expression.append("(#"+UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode()).append(" >= ").append(popularizeUserCount)
                        .append(" && ").append("#"+UpgradeMallLevelCondParamTypeEnum.TEAM_NUM.getCode()).append(" >= ").append(teamNum).append(")");
                expression.append(" || ");
                expression.append("#"+UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode());
                expression.append("  >= ").append(rechargeAmount);
            }

            mallLevelCondExpr.setExpression(expression.toString());
            mallLevel.setTeamNum(Integer.parseInt(teamNum));
            mallLevel.setUpdateBy(getUsername_login());
            mallLevel.setUpdateTime(new Date());
            mallLevel.setProfitRationMin(Arith.div(mallLevel.getProfitRationMin(),100));
            mallLevel.setProfitRationMax(Arith.div(mallLevel.getProfitRationMax(),100));
            mallLevel.setSellerDiscount(Arith.div(mallLevel.getSellerDiscount(),100));
            mallLevel.setCondExpr(JsonUtils.bean2Json(mallLevelCondExpr));

            adminVipService.update(mallLevel);
            project.log.Log log = new project.log.Log();
            log.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log.setUsername(sec.getUsername());
            log.setOperator(this.getUsername_login());

            double rechargeAmounts = 0;
            Long popularizeUserCounts = null;
            Long teamNums = null;
            if (null != data.getCondExpr()){
                MallLevelCondExpr mallLevelCondExprs = JsonUtils.json2Object(mallLevel.getCondExpr(), MallLevelCondExpr.class);
                List<MallLevelCondExpr.Param> params = mallLevelCondExprs.getParams();


                for (MallLevelCondExpr.Param param : params) {
                    if (param.getCode().equals(UpgradeMallLevelCondParamTypeEnum.RECHARGE_AMOUNT.getCode())){
                        rechargeAmounts = Double.parseDouble(param.getValue());
                    }
                    if (param.getCode().equals(UpgradeMallLevelCondParamTypeEnum.POPULARIZE_UNDERLING_NUMBER.getCode())){
                        popularizeUserCounts = Long.parseLong(param.getValue());
                    }
                    if (param.getCode().equals(UpgradeMallLevelCondParamTypeEnum.TEAM_NUM.getCode())){
                        teamNums = Long.parseLong(param.getValue());
                    }
                }
            }

            String logs = MessageFormat.format("ip:" + this.getIp() + ",修改会员等级:{0}前参数，累计充值:{1},推广有效人数:{2},团队人数:{3},全球到货时间:{4}," +
                            "卖家优惠折扣:{5},利润比例:{6},每日流量:{7},每小时最小流量:{8},每小时流量波动范围:{9},升级礼金:{10},专属客服:{11},首页推荐:{12},可售商品数量：{13},等级售价：{14}",
                    data.getLevel(), rechargeAmounts, popularizeUserCounts,teamNums,data.getDeliveryDays(),Arith.mul(data.getSellerDiscount(),100),
                    Arith.mul(data.getProfitRationMin(),100)-Arith.mul(data.getProfitRationMax(),100),data.getPromoteViewDaily(),data.getAwardBaseView(),
                    data.getAwardViewMin()-data.getAwardViewMax(),data.getUpgradeCash(),data.getHasExclusiveService(),data.getRecommendAtFirstPage(),data.getGoodsNum(),data.getPrice());
            log.setLog(logs);
            logService.saveSync(log);

            project.log.Log log2 = new project.log.Log();
            log2.setCategory(Constants.LOG_CATEGORY_OPERATION);
            log2.setUsername(sec.getUsername());
            log2.setOperator(this.getUsername_login());
            String logs2 = MessageFormat.format("ip:" + this.getIp() + ",修改会员等级:{0}后参数，累计充值:{1},推广有效人数:{2},团队人数:{3},全球到货时间:{4}," +
                            "卖家优惠折扣:{5},利润比例:{6},每日流量:{7},每小时最小流量:{8},每小时流量波动范围:{9},升级礼金:{10},专属客服:{11},首页推荐:{12},可售商品数量：{13},等级售价：{14}",
                    mallLevel.getLevel(), rechargeAmount, popularizeUserCount,teamNum,mallLevel.getDeliveryDays(),Arith.mul(mallLevel.getSellerDiscount(),100),
                    Arith.mul(mallLevel.getProfitRationMin(),100)-Arith.mul(mallLevel.getProfitRationMax(),100),mallLevel.getPromoteViewDaily(),mallLevel.getAwardBaseView(),
                    mallLevel.getAwardViewMin()-mallLevel.getAwardViewMax(),mallLevel.getUpgradeCash(),mallLevel.getHasExclusiveService(),
                    mallLevel.getRecommendAtFirstPage(),mallLevel.getGoodsNum(),mallLevel.getPrice());
            log2.setLog(logs2);
            logService.saveSync(log2);
        } catch (BusinessException e) {

            model.addObject("error", e.getMessage());
            model.setViewName("admin_vip_update");
            model.addObject("mallLevel",mallLevel);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.setViewName("admin_vip_update");
            model.addObject("mallLevel",mallLevel);
            model.addObject("mallLevel",mallLevel);
            model.addObject("mallLevel",mallLevel);
            return model;
        }
        String message = "操作成功";
        model.addObject("message", message);
        model.setViewName("redirect:/" +  "brush/vip/list.action");
        return model;
    }

    /**
     * 验证登录人资金密码
     * @param operatorUsername
     * @param loginSafeword
     */
    protected void checkLoginSafeword(SecUser secUser,String operatorUsername,String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
        String sysSafeword = secUser.getSafeword();
        String safeword_md5 = passwordEncoder.encodePassword(loginSafeword, operatorUsername);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("登录人资金密码错误");
        }
    }
    /**
     * 验证谷歌验证码
     * @param code
     */
    protected void checkGoogleAuthCode(SecUser secUser,String code) {
        if(!secUser.isGoogle_auth_bind()) {
            throw new BusinessException("请先绑定谷歌验证器");
        }
        boolean checkCode = googleAuthService.checkCode(secUser.getGoogle_auth_secret(), code);
        if(!checkCode) {
            throw new BusinessException("谷歌验证码错误");
        }
    }
}