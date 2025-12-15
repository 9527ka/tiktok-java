package project.web.admin.platform;

import kernel.exception.BusinessException;
import kernel.web.Page;
import kernel.web.PageActionSupport;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.invest.platform.AdminPlatformService;
import project.user.googleauth.GoogleAuthService;
import security.SecUser;
import security.internal.SecUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@Slf4j
@RestController
@RequestMapping("/adminPlatform")
public class AdminPlatformController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminPlatformController.class);

    @Resource
    private AdminPlatformService adminPlatformService;

    @Resource
    private PasswordEncoder passwordEncoder;

    @Resource
    private SecUserService secUserService;

    @Resource
    private GoogleAuthService googleAuthService;
    /**
     * 列表查询
     */
    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request,ModelAndView model) {
        String name = request.getParameter("name");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        model.setViewName("admin_platform_list");
        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.pageSize = 20;
            Page page = adminPlatformService.findPlatformList(pageNo,pageSize,name,startTime,endTime);
            model.addObject("pageNo",pageNo);
            model.addObject("page", page);
            model.addObject("name",name);
            model.addObject("startTime",startTime);
            model.addObject("endTime",endTime);
            return model;
        } catch (BusinessException e) {
            model.addObject("page",page);
            model.addObject("error", e.getMessage());
            return model;
        } catch (Throwable t) {
        	logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
    }

    @RequestMapping("/addOrModify.action")
    public ModelAndView addOrModify(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        String login_safeword = request.getParameter("login_safeword");
        String name = request.getParameter("name");
        Integer status = Integer.valueOf(request.getParameter("status"));
        String createTime = request.getParameter("createTime");
        String id = request.getParameter("id");
        String fileName = request.getParameter("fileName");
        try {
            checkRequest(name);
//            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
//            checkLoginSafeword(sec,this.getUsername_login(),login_safeword);

            adminPlatformService.addOrModify(id,name,createTime,status);
            model.addObject("pageNo",request.getParameter("PageNo"));
            this.message = "操作成功";
            model.addObject("message",message);
            model.setViewName("redirect:/" + "adminPlatform/list.action");
            return model;
        } catch (BusinessException e) {
            model.setViewName("admin_platform_update_add");
            model.addObject("error", e.getMessage());
            model.addObject("name",name);
            model.addObject("status",status);
            model.addObject("id",id);
            model.addObject("fileName",fileName);
            model.addObject("createTime",createTime);
            return model;
        } catch (Throwable t) {
        	logger.error(" error ", t);
            model.setViewName("admin_platform_update_add");
            model.addObject("error", "[ERROR] " + t.getMessage());
            model.addObject("name",name);
            model.addObject("status",status);
            model.addObject("id",id);
            model.addObject("fileName",fileName);
            model.addObject("createTime",createTime);
            return model;
        }
    }

    private void checkRequest(String name) {
        if(StringUtils.isBlank(name)){
            throw new BusinessException("平台名称不能为空");
        }
    }


    /**
     * 跳转修改页面
     */
    @RequestMapping("/toUpdateOrAdd.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        ModelAndView model = new ModelAndView("admin_platform_update_add");
        String name = request.getParameter("name");
        String status = request.getParameter("status");
        String fileName = request.getParameter("fileName");
        String createTime = request.getParameter("createTime");
        String id = request.getParameter("id");
        String pageNo = request.getParameter("pageNo");
        model.addObject("name", name);
        model.addObject("fileName", fileName);
        model.addObject("pageNo", pageNo);
        model.addObject("status", status);
        model.addObject("id", id);
        model.addObject("createTime", createTime);

        // 限制提现
        return model;
    }


    /**
     * 删除
     */
    @RequestMapping("/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        String login_safeword = request.getParameter("login_safeword");
//        String google_auth_code = request.getParameter("google_auth_code");
        model.setViewName("redirect:/" + "adminPlatform/list.action");
        try {
            SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
//            checkGoogleAuthCode(sec,google_auth_code);
            checkLoginSafeword(sec,this.getUsername_login(),login_safeword);

            adminPlatformService.delete(request.getParameter("id"));
            this.message = "操作成功";
            model.addObject("message",message);
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            return model;
        } catch (Throwable t) {
        	logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
        // 限制提现
    }


    /**
     * 验证登录人资金密码
     * @param operatorUsername
     * @param loginSafeword
     */
    protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
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