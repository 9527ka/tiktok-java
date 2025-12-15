package project.web.admin.banner;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.mall.banner.AdminMallBannerService;
import project.mall.banner.model.MallBanner;
import project.news.News;
import security.SecUser;
import security.internal.SecUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping("/mall/banner/")
public class AdminMallBannerController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminMallBannerController.class);

    @Resource
    private AdminMallBannerService mallBannerService;

    @Resource
    private SecUserService secUserService;

    @Resource
    private PasswordEncoder passwordEncoder;



    /**
     * 轮播图列表
     */
    @RequestMapping("list.action")
    public ModelAndView list(HttpServletRequest request) {

        String error = request.getParameter("error");
        String message = request.getParameter("message");
        String type = request.getParameter("type");
        String endTime = request.getParameter("endTime");
        String startTime = request.getParameter("startTime");
        ModelAndView model = new ModelAndView("admin_banner_list");

        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = mallBannerService.pagedQuery(this.pageNo, this.pageSize,type,endTime,startTime);

        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        model.addObject("pageNo",this.pageNo);
        model.addObject("message",message);
        model.addObject("page",page);
        model.addObject("type",type);
        model.addObject("error",error);
        return model;
    }


    @RequestMapping( "toAdd.action")
    public ModelAndView toAdd(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        String pageNo = request.getParameter("pageNo");
        String type = request.getParameter("type");
        modelAndView.addObject("pageNo",pageNo);
        modelAndView.addObject("type",type);
        modelAndView.setViewName("admin_banner_add");
        return modelAndView;
    }

    /**
     * 新增 banner
     *
     */
    @RequestMapping( "add.action")
    public ModelAndView add(HttpServletRequest request, MallBanner banner) {
        String pageNo = request.getParameter("pageNo");

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("type", banner.getType());

        try {
            banner.setCreateTime(new Date());
            this.mallBannerService.save(banner);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("admin_banner_add");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("admin_banner_add");
            return modelAndView;
        }

        modelAndView.addObject("message", "操作成功");
        modelAndView.setViewName("redirect:/" +  "/mall/banner/list.action");
        return modelAndView;
    }

    /**
     * 修改 banner
     */
    @RequestMapping("toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String id = request.getParameter("id");
        String type = request.getParameter("type");
        String pageNo = request.getParameter("pageNo");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("type",type);
        modelAndView.addObject("pageNo", pageNo);
        try {

            MallBanner banner = this.mallBannerService.findById(id);
            modelAndView.addObject("banner", banner);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "/mall/banner/list.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/" +  "/mall/banner/list.action");
            return modelAndView;
        }

        modelAndView.setViewName("admin_banner_update");
        return modelAndView;
    }


    /**
     * 修改 banner
     */
    @RequestMapping("update.action")
    public ModelAndView update(HttpServletRequest request, MallBanner banner) {

        ModelAndView modelAndView = new ModelAndView();
        String type = request.getParameter("type");
        String pageNo = request.getParameter("pageNo");
        modelAndView.addObject("type",type);
        modelAndView.addObject("pageNo",pageNo);
        try {

            MallBanner bean = this.mallBannerService.findById(banner.getId().toString());
            banner.setCreateTime(bean.getCreateTime());
            this.mallBannerService.update(banner);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("banner", banner);
            modelAndView.setViewName("admin_banner_update");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.addObject("banner", banner);
            modelAndView.setViewName("redirect:/" +  "/mall/banner/list.action");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        modelAndView.setViewName("redirect:/" +  "/mall/banner/list.action");
        return modelAndView;
    }


    /**
     * 删除 banner
     */
    @RequestMapping("delete.action")
    public ModelAndView delete(HttpServletRequest request) {

        String id = request.getParameter("bannerId");
        String type = request.getParameter("type");
        String pageNo = request.getParameter("pageNo");
        String login_safeword = request.getParameter("login_safeword");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("type",type);
        modelAndView.addObject("pageNo",pageNo);
        modelAndView.setViewName("redirect:/" +  "/mall/banner/list.action");

        try {

            String userNameLogin = this.getUsername_login();
            SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);
            this.checkLoginSafeword(sec, userNameLogin, login_safeword);

            MallBanner banner = this.mallBannerService.findById(id);
            this.mallBannerService.delete(banner);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error("update error ", t);
            modelAndView.addObject("error", "程序错误");
            return modelAndView;
        }

        modelAndView.addObject("message", "操作成功");
        return modelAndView;
    }

    protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
        String sysSafeword = secUser.getSafeword();
        String safeword_md5 = this.passwordEncoder.encodePassword(loginSafeword, operatorUsername);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("登录人资金密码错误");
        }
    }
}
