package project.web.api;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.platform.BrushClient;
import project.invest.project.ProjectService;
import project.invest.project.model.InvestRebate;
import project.invest.vip.VipService;
import project.invest.vip.model.Vip;
import project.mall.auto.AutoConfig;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;
import util.TwoValues;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
public class PromoteController extends BaseAction {
    private static Log logger = LogFactory.getLog(PromoteController.class);
    private final String action = "/api/promote!";

    @Resource
    private PartyService partyService;
    @Resource
    private SysparaService sysparaService;

    @Resource
    protected ProjectService projectService;

    @Resource
    private VipService vipService;


    /**
     * 我的推广
     * @return
     */
    @PostMapping( action+"my.action")
    public Object stats(){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        Party party = partyService.cachePartyBy(partyId,false);

        JSONObject object = new JSONObject();

        object.put("code", party.getUsercode());

        object.put("rebate1", 0);
        object.put("rebate2", 0);
        Vip v = vipService.selectById(party.getVip_level());

        if(v!=null){

            object.put("rebate1", v.getRebate1());
            object.put("rebate2", v.getRebate2());
        }
        object.put("download", sysparaService.find("invest_order_share").getValue());

        String prefix = AutoConfig.getBaseUrl();
        object.put("download", prefix);

        resultObject.setData(object);
        return resultObject;

    }



    /**
     * vip 进度
     * @return
     */
    @PostMapping( action+"my_vip.action")
    public Object myVip(){
        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();

        Party party = partyService.cachePartyBy(partyId,false);

        JSONObject object = new JSONObject();

        object.put("vip_level", party.getVip_level());

        Vip v = vipService.selectById(party.getVip_level());

        object.put("vipName", v.getName());

        object.put("subSales", 999999999);
        object.put("subCount", 999999999);

        object.put("rebate1", 0);
        object.put("rebate2", 0);

        object.put("rebate0", 0);

        if(v!=null){
            TwoValues<Integer,Double> rn = vipService.getInvestPromotion(partyId);
            object.put("subSales", rn.getTwo());
            object.put("subCount", rn.getOne());

            object.put("rebate1", v.getRebate1());
            object.put("rebate2", v.getRebate2());
            object.put("rebate0", v.getRebate0());
        }



        resultObject.setData(object);
        return resultObject;
    }

    /**
     * vip 配置
     */
    @PostMapping( action+"vip_configs.action")
    public Object vipConfigs(){

        ResultObject resultObject = new ResultObject();

        JSONArray jsonArray = new JSONArray();

        for(Vip vip : vipService.listVip()){
            JSONObject o = new JSONObject();
            o.put("vip", vip.getId());
            o.put("name", vip.getName());

            o.put("subSales", vip.getSubSales());
            o.put("subCount", vip.getSubCount());

            o.put("rebate1", vip.getRebate1());
            o.put("rebate2", vip.getRebate2());

            o.put("rebate0", vip.getRebate0());

            jsonArray.add(o);
        }


        JSONObject object = new JSONObject();
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;

    }

    /**
     * 佣金列表
     * @return
     */
    @PostMapping( action+"rebate_list.action")
    public Object listRebate(HttpServletRequest request){

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);

       List<InvestRebate> list =  projectService.listInvestRebate(partyId,pageInfo.getPageNum(),pageInfo.getPageSize());
        JSONArray jsonArray = new JSONArray();
        for(InvestRebate rebate : list){
            JSONObject o = new JSONObject();
            o.put("orderId", rebate.getOrderId());
            o.put("level", rebate.getLevel());
            o.put("realTime",rebate.getRealTime());
            o.put("rebate",rebate.getRebate());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 我的团队
     * @return
     */
    @PostMapping( action+"team_info.action")
    public Object teamInfo(){

        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }
        String partyId = this.getLoginPartyId();
        resultObject.setData(projectService.getTeamInfo(partyId));
        return resultObject;

    }

    /**
     * 三级推广
     * @return
     */
    @PostMapping( action+"team_level.action")
    public Object teamLevel(HttpServletRequest request){


        ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        String levelString = request.getParameter("level");
        if(levelString==null){
            levelString = "0";
        }
        Integer level = Integer.parseInt(levelString);
        if(level==null||level<1||level>2){
            level  = 1;
        }

        String partyId = this.getLoginPartyId();
        PageInfo pageInfo = getPageInfo(request);
        if(pageInfo.getPageSize()>10){
            pageInfo.setPageSize(10);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", projectService.listRebateByLevel(partyId,level,pageInfo.getPageNum(),pageInfo.getPageSize()));
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 客户端信息
     * @return
     */
    @PostMapping( action+"client.action")
    public Object client(HttpServletRequest request){

        ResultObject resultObject = new ResultObject();
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        JSONObject object = new JSONObject();

        object.put("plantform", "0");

        String id = request.getParameter("plantform");
        if(id==null||!id.equals("1")&&!id.equals("2")){
            resultObject.setData(object);
            return resultObject;
        }

        String lang = request.getParameter("lang");
        if(lang==null){
            lang =  "en";
        }

        BrushClient client =vipService.getBrushClient(id+lang);
        if(client==null){
            resultObject.setData(object);
            return resultObject;
        }

        object.put("plantform", id);
        object.put("latestVersion", client.getLatestVersion());
        object.put("title", client.getTitle());
        object.put("content", client.getContent());
        object.put("downloadlink", client.getDownloadlink());
        object.put("status", client.getStatus());
        resultObject.setData(object);
        return resultObject;

    }

    /**
     * 学习
     * @return
     */
    @PostMapping( action+"learn.action")
    public Object learn(HttpServletRequest request){

        ResultObject resultObject = new ResultObject();
        JSONObject object = new JSONObject();
        object.put("lockTime", sysparaService.find("brush_order_luckminute").getInteger());
        resultObject.setData(object);
        return resultObject;

    }


}
