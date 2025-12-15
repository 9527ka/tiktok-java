package project.web.api;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.InvestRedisKeys;
import project.invest.expert.model.Expert;
import project.invest.project.ProjectService;
import project.invest.project.model.Project;
import project.invest.project.model.ProjectLang;
import project.news.NewsService;
import project.redis.RedisHandler;
import project.syspara.SysparaService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;


@RestController
@CrossOrigin
public class IndexController extends BaseAction {


    @Resource
    private NewsService newsService;

    @Resource
    protected ProjectService projectService;

    @Resource
    protected RedisHandler redisHandler;

    @Resource
    private SysparaService sysparaService;

    private final String action = "api/index!";

    /**
     * 首页
     * @return
     */
    @PostMapping(action +  "home.action")
    public Object index(HttpServletRequest request){
        ResultObject result = new ResultObject();
        String lang = request.getParameter("lang");
        if(StringUtils.isEmptyString(lang)){
            lang = this.getLanguage(request);
        }
        List<String> announcements = this.newsService.selectAnnouncements(lang);
        String links = this.sysparaService.find("index_banner_links").getValue();
        JSONObject object = new JSONObject();
        if(StringUtils.isNotEmpty(links)){
            String[] split = links.split(",");
            object.put("banner", split);
        }
        object.put("service_link", "www.google.com");//todo
        object.put("announcement", announcements);
        object.put("aboutus", sysparaService.find("invest_order_aboutus").getValue());
        result.setData(object);
        return result;
    }

    /**
     * 下载连接
     * @param request
     * @return
     */
    @PostMapping( action+"download-url.action")
    public Object downloadUrl(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        resultObject.setCode("0");
        resultObject.setData(this.sysparaService.find("brush_order_download").getValue());
        return resultObject;
    }

    /**
     * 推荐项目
     * @return
     */
    @PostMapping( action+"homeProject.action")
    public Object homeProject(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);

        JSONArray jsonArray = new JSONArray();
        for(Project pl : projectService.listProjectHome(pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            String js  = redisHandler.getString(InvestRedisKeys.INVEST_PROJECT_LANG+lang+":"+pl.getId().toString());
            if(StringUtils.isEmptyString(js)){
                continue;
            }
            ProjectLang pLang = JSONArray.parseObject(js, ProjectLang.class);
            o.put("projectId", pLang.getProjectId());
            o.put("type", pl.getType());
            o.put("name", pLang.getName());
            o.put("investMin", pl.getInvestMin());
            o.put("bonusRate",pl.getPointRate());
            o.put("bonus",pl.getBonus());
            double progress = Arith.roundDown(pl.getInvestProgressMan(),4);
            o.put("progress", progress);
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
     * 专家分页
     * @return
     */
    @PostMapping( action+"list_expert.action")
    public Object listExpert(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);

        JSONArray jsonArray = new JSONArray();
        for(Expert ns : newsService.listExpertPage(lang,pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            o.put("iconImg", ns.getIconImg());
            o.put("name", ns.getName());
            o.put("summary", ns.getSummary());
            o.put("content",ns.getContent());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }



}
