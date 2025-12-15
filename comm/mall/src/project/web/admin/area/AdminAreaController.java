package project.web.admin.area;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.mall.area.AdminMallCityService;
import project.mall.area.AdminMallCountryService;
import project.mall.area.AdminMallStateService;
import project.mall.area.model.MallCountry;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

;import java.util.ArrayList;
import java.util.List;

/**
 * 区域管理
 */
@Slf4j
@RestController
@RequestMapping("/mall/area")
public class AdminAreaController extends PageActionSupport {
    private static Log logger = LogFactory.getLog(AdminAreaController.class);

    @Resource
    protected AdminMallCountryService adminMallCountryService;

    @Resource
    protected AdminMallStateService adminMallStateService;

    @Resource
    protected AdminMallCityService adminMallCityService;

    @RequestMapping("/listCountry.action")
    public ModelAndView list(HttpServletRequest request) {
        this.pageSize = 20;
        String countryName = request.getParameter("countryName");
        Integer flag = request.getParameter("flag") == null ? null : Integer.valueOf(request.getParameter("flag"));
        ModelAndView model = new ModelAndView("admin_country_list");
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            Page pageData = adminMallCountryService.pagedQueryCountry(this.pageNo, this.pageSize, countryName, flag);
            model.addObject("page", pageData);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("countryName", countryName);
        model.addObject("flag", flag);
        model.addObject("message", message);
        model.addObject("pageNo", this.pageNo);
        return model;
    }

    @RequestMapping("/listState.action")
    public ModelAndView listState(HttpServletRequest request) {
        this.pageSize = 20;
        String stateName = request.getParameter("stateName");
        Integer flag = request.getParameter("flag") == null ? null : Integer.valueOf(request.getParameter("flag"));
        ModelAndView model = new ModelAndView("admin_state_list");
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            Page pageData = adminMallStateService.pagedQueryState(this.pageNo, this.pageSize, stateName, flag);
            model.addObject("page", pageData);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("stateName", stateName);
        model.addObject("flag", flag);
        model.addObject("message", message);
        model.addObject("pageNo", this.pageNo);
        return model;
    }

    @RequestMapping("/listCity.action")
    public ModelAndView listCity(HttpServletRequest request) {
        this.pageSize = 20;
        String cityName = request.getParameter("cityName");
        Integer flag = request.getParameter("flag") == null ? null : Integer.valueOf(request.getParameter("flag"));
        ModelAndView model = new ModelAndView("admin_city_list");
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            Page pageData = adminMallCityService.pagedQueryCity(this.pageNo, this.pageSize, cityName, flag);
            model.addObject("page", pageData);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("cityName", cityName);
        model.addObject("flag", flag);
        model.addObject("message", message);
        model.addObject("pageNo", this.pageNo);
        return model;
    }

    /**
     * 新增国家页面
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/toAddCountry.action")
    public ModelAndView toAdd(HttpServletRequest request) {
        String error = request.getParameter("error");
        String pageNo = request.getParameter("pageNo");
        ModelAndView model = new ModelAndView();
        model.setViewName("admin_country_add");
        model.addObject("error", error);
        model.addObject("pageNo", pageNo);
        return model;
    }

    @RequestMapping(value = "/toAddState.action")
    public ModelAndView toAddState(HttpServletRequest request) {
        String error = request.getParameter("error");
        String pageNo = request.getParameter("pageNo");
        ModelAndView model = new ModelAndView();
        model.setViewName("admin_state_add");
        model.addObject("error", error);
        model.addObject("pageNo", pageNo);
        return model;
    }

    @RequestMapping(value = "/toAddCity.action")
    public ModelAndView toAddCity(HttpServletRequest request) {
        String error = request.getParameter("error");
        String pageNo = request.getParameter("pageNo");
        ModelAndView model = new ModelAndView();
        model.setViewName("admin_city_add");
        model.addObject("error", error);
        model.addObject("pageNo", pageNo);
        return model;
    }
//
//    @RequestMapping(value = "/add.action")
//    public ModelAndView add(HttpServletRequest request) {
//        String countryName = request.getParameter("countryName");
//        Integer areaType = request.getParameter("areaType") == null ? null : Integer.valueOf(request.getParameter("areaType"));
//        Integer areaStatus = request.getParameter("areaStatus") == null ? null : Integer.valueOf(request.getParameter("areaStatus"));
//        String parentId= request.getParameter("parentId");
//        ModelAndView model = new ModelAndView();
//        model.addObject("pageNo", pageNo);
//        try {
//            if (StringUtils.isEmptyString(areaName)) {
//                throw new BusinessException("区域名称不能为空");
//            }
//            adminAreaService.save(areaName, areaStatus == null ? 0 : areaStatus, areaType,parentId);
//        } catch (BusinessException e) {
//            model.addObject("error", e.getMessage());
//            model.setViewName("admin_area_add");
//            return model;
//        } catch (Exception e) {
//            logger.error("error ", e);
//            model.setViewName("admin_area_add");
//            return model;
//        }
//        ModelAndView modelAndView = new ModelAndView();
//        modelAndView.addObject("message", "操作成功");
//        modelAndView.addObject("PageNo", pageNo);
//        modelAndView.setViewName("redirect:/" + "/mall/area/list.action");
//        return modelAndView;
//    }

}