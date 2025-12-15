package project.web.api;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.invest.InvestRedisKeys;
import project.invest.expert.model.Expert;
import project.invest.project.model.Project;
import project.invest.project.model.ProjectLang;
import project.news.News;
import project.news.NewsService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class NewsController extends BaseAction {

    private final String action = "/api/news!";

    @Resource
    private NewsService newsService;

    /**
     * 新闻分页
     * @return
     */
    @PostMapping( action+"list_news.action")
    public Object listNews(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);
        String lang = this.getLanguage(request);

        JSONArray jsonArray = new JSONArray();
        for(News ns : newsService.listNewsPage(lang,pageInfo.getPageNum(),pageInfo.getPageSize())){
            JSONObject o = new JSONObject();
            o.put("id", ns.getId().toString());
            o.put("title", ns.getTitle());
            o.put("iconImg", ns.getIconImg());
            o.put("releaseTime", ns.getReleaseTime());
            o.put("content",ns.getContent());
            jsonArray.add(o);
        }

        JSONObject object = new JSONObject();
        object.put("pageInfo", pageInfo);
        object.put("pageList", jsonArray);
        resultObject.setData(object);
        return resultObject;
    }

    /**
     * 新闻详情
     * @return
     */
    @PostMapping( action+"detail_news.action")
    public Object detailNews(HttpServletRequest request){
        ResultObject resultObject = new ResultObject();
        String id = request.getParameter("id");
        News ns = newsService.findById(id);
        JSONObject o = new JSONObject();

        o.put("id", ns.getId().toString());
        o.put("title", ns.getTitle());
        o.put("iconImg", ns.getIconImg());
        o.put("releaseTime", ns.getReleaseTime());
        o.put("content",ns.getContent());

        JSONObject object = new JSONObject();
        object.put("news", o);
        resultObject.setData(object);
        return resultObject;
    }


}
