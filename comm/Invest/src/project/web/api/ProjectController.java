package project.web.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.InvestRedisKeys;
import project.invest.project.ProjectService;
import project.invest.project.model.InvestOrders;
import project.invest.project.model.InvestRebate;
import project.invest.project.model.Project;
import project.invest.project.model.ProjectLang;
import project.party.PartyService;
import project.party.model.Party;
import project.redis.RedisHandler;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class ProjectController extends BaseAction {

    @Resource
    protected ProjectService projectService;
    @Resource
    protected RedisHandler redisHandler;
    @Resource
    protected KycService kycService;

    @Autowired
    private PartyService partyService;

    private static Log logger = LogFactory.getLog(ProjectController.class);

    private final String action = "/api/project!";



    /**
     * 项目分类
     * @return
     */
    @PostMapping( action+"listCategory.action")
    public Object listCategory(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        JSONArray jsonArray = new JSONArray();

//        for(Category category: projectService.listCategorys(this.getLanguage(request))){
//            JSONObject o = new JSONObject();
//            o.put("baseId", category.getBaseId());
//            o.put("name", category.getName());
//            jsonArray.add(o);
//        }

        JSONObject object = new JSONObject();
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 在售项目
     * @return
     */
    @PostMapping( action+"listProject.action")
    public Object listProject(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        String baseId = request.getParameter("baseId");
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);

        JSONArray jsonArray = new JSONArray();
        for(Project pl : projectService.listProjectSell(baseId,pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            String js  = redisHandler.getString(InvestRedisKeys.INVEST_PROJECT_LANG+lang+":"+pl.getId().toString());
            if(StringUtils.isEmptyString(js)){
                continue;
            }
            ProjectLang pLang = JSONArray.parseObject(js, ProjectLang.class);
            o.put("projectId", pLang.getProjectId());
            o.put("type", pl.getType());
            o.put("repeating", pl.isRepeating());
            o.put("name", pLang.getName());
            o.put("investMin", pl.getInvestMin());
            o.put("bonusRate",pl.getBonusRate());
            o.put("bonus",pl.getBonus());
            double progress = Arith.roundDown(pl.getInvestProgressMan(),4);
            o.put("progress", progress>1?1:progress);
            o.put("isSellOut",progress>=1?1:0);
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }


    /**
     * 项目详情
     * @return
     */
    @PostMapping( action+"projectInfo.action")
    public Object projectInfo(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        String projectId = request.getParameter("projectId");
        String lang = this.getLanguage(request);
        Project pl = projectService.getProject(projectId);
        JSONObject o = new JSONObject();
        String js  = redisHandler.getString(InvestRedisKeys.INVEST_PROJECT_LANG+lang+":"+pl.getId().toString());
        ProjectLang pLang = JSONArray.parseObject(js, ProjectLang.class);

        o.put("projectId", pLang.getProjectId());
        o.put("name", pLang.getName());
        o.put("investSize", pl.getInvestSize());
        o.put("investMin", pl.getInvestMin());
        o.put("investMax", pl.getInvestMax());
        o.put("bonusRate",pl.getBonusRate());
        o.put("bonus",pl.getBonus());
        double progress = Arith.roundDown(pl.getInvestProgressMan(),4);
        o.put("progress", progress>1?1:progress);
        o.put("isSellOut",progress>=1?1:0);

        o.put("type",pl.getType());
        o.put("repeating", pl.isRepeating());
        o.put("pointRate",pl.getPointRate());
        o.put("iconImg",pl.getIconImg());
        o.put("guarantyAgency",pLang.getGuarantyAgency());
        o.put("desSettle",pLang.getDesSettle());
        o.put("desUse",pLang.getDesUse());
        o.put("desSafe",pLang.getDesSafe());

        JSONObject object = new JSONObject();
        object.put("project", o);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 项目购买
     * @return
     */
    @PostMapping( action+"buy.action")
    public Object buy(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        String safeword = request.getParameter("safeword");

        Party party = partyService.cachePartyBy(partyId, false);
        String partySafeword = party.getSafeword();
        if(StringUtils.isEmptyString(partySafeword)){
            throw new BusinessException(999, "请设置资金密码");
        }

        if (StringUtils.isEmptyString(safeword)) {
            throw new BusinessException("资金密码不能为空");
        }

        if (safeword.length() < 6 || safeword.length() > 12) {
            throw new BusinessException("资金密码必须6-12位");
        }

        if (!this.partyService.checkSafeword(safeword, partyId)) {
            throw new BusinessException("资金密码错误");
        }

        Kyc kyc = this.kycService.get(partyId);
        if (null == kyc ) {
            resultObject.setCode("800");
            resultObject.setMsg("尚未KYC认证");
            return resultObject;
        }

        if ( kyc.getStatus() != 2) {
            resultObject.setCode("801");
            resultObject.setMsg("KYC认证尚未通过");
            return resultObject;
        }

        String projectId = request.getParameter("projectId");
        double amount = Double.parseDouble(request.getParameter("amount"));
        amount = Arith.roundDown(amount,2);

        if(amount<=0||amount>999999){
            resultObject.setCode("1");
            resultObject.setMsg("非法请求");
            return resultObject;
        }

        String lockKey = InvestRedisKeys.INVEST_ORDER_USER_LOCK+partyId;
        if(!redisHandler.lock(lockKey,10)){
            resultObject.setCode("1");
            resultObject.setMsg("正在购买");
            return resultObject;
        }
        try {
            projectService.updateBuyProject(partyId,projectId,amount);

        }catch (BusinessException e){
            resultObject.setCode(String.valueOf(e.getSign()));
            resultObject.setMsg(e.getMessage());
            return resultObject;
        }catch (Exception e1){
            e1.printStackTrace();
            logger.error("购买失败",e1);
            resultObject.setCode("1");
            resultObject.setMsg("购买失败,联系客服");
        }
        finally {
            redisHandler.remove(lockKey);
        }


        JSONObject object = new JSONObject();
        resultObject.setData(object);
        return resultObject;
    }


    /**
     * 我的投资
     * @return
     */
    @PostMapping( action+"investInfo.action")
    public Object myInvestInfo(){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        JSONObject object = new JSONObject();
        object.put("investInfo", projectService.getMyInvestInfo(this.getLoginPartyId()));
        resultObject.setData(object);
        return resultObject;
    }


    /**
     * 我的投资
     * @return
     */
    @PostMapping( action+"investList.action")
    public Object myInvestList(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);

        JSONArray jsonArray = new JSONArray();
        for(InvestOrders pl : projectService.listProjectMy(partyId,pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            String js  = redisHandler.getString(InvestRedisKeys.INVEST_PROJECT_LANG+lang+":"+pl.getProjectId());
            if(StringUtils.isEmptyString(js)){
                continue;
            }
            ProjectLang pLang = JSONArray.parseObject(js, ProjectLang.class);
            o.put("orderId", pl.getId());
            o.put("name", pLang.getName());
            o.put("status", pl.getStatus());
            o.put("type", pl.getType());
            o.put("createTime", DateUtils.getLongDate(pl.getCreateTime()));
            long pass = System.currentTimeMillis()- pl.getCreateTime().getTime();
            if(pl.getType()<=2){
                pass /= 3600000L;
            }else{
                pass /= 86400000L;
            }
            if(pass>pl.getBonus()){
                pass = pl.getBonus();
            }
            o.put("remainDays",pl.getBonus()-pass) ;
            o.put("passDays",pass) ;
            double valDay = Arith.mul(pl.getAmount(),pl.getBonusRateVip());
            o.put("valDay", valDay);
            o.put("valPassDay", Arith.roundDown(Arith.mul(valDay,pass),2));
            o.put("incomeWill",pl.getIncomeWill());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }


    /**
     * 项目收益信息
     * @return
     */
    @PostMapping( action+"projectIncomeInfo.action")
    public Object projectIncomeInfo(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String orderId = request.getParameter("orderId");
        JSONObject object = new JSONObject();
        object.put("projectIncome", projectService.getProjectIncome(orderId));
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 项目收益列表
     * @return
     */
    @PostMapping( action+"projectIncomeList.action")
    public Object projectIncomeList(HttpServletRequest request){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        PageInfo pageInfo = getPageInfo(request);

        String orderId = request.getParameter("orderId");

        JSONArray jsonArray = new JSONArray();
        for(InvestRebate pl : projectService.listProjectIncome(orderId,pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            o.put("realTime", pl.getRealTime());
            o.put("income",pl.getRebate());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }


}
