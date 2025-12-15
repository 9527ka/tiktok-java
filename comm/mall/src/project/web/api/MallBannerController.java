package project.web.api;

import com.alibaba.fastjson.JSONObject;
import kernel.util.PageInfo;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import project.mall.banner.MallBannerService;
import project.mall.banner.model.MallBanner;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@CrossOrigin
public class MallBannerController extends BaseAction {

    private static Log logger = LogFactory.getLog(MallBannerController.class);

    @Resource
    protected MallBannerService mallBannerService;

    private final String action = "/api/banner!";


    @PostMapping( action + "bannerList.action")
    public Object bannerList(HttpServletRequest request){

        ResultObject resultObject = new ResultObject();
        PageInfo pageInfo = getPageInfo(request);

        String type = request.getParameter("type");
        String imgType = request.getParameter("imgType");
        if (null == type || type.equals("")){
            resultObject.setCode("1");
            resultObject.setMsg("缺少必要参数type");
            return resultObject;
        }

        List<MallBanner> bannerList = this.mallBannerService.getBannerList(type,imgType,pageInfo);
        JSONObject object = new JSONObject();
        object.put("result", bannerList);
        resultObject.setData(object);
        return resultObject;
    }


}
