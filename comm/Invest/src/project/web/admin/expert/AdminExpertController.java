package project.web.admin.expert;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.Constants;
import project.invest.expert.AdminExpertService;
import project.invest.expert.model.Expert;
import project.log.LogService;
import project.news.AdminNewsService;
import security.SecUser;
import security.internal.SecUserService;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.MessageFormat;
import java.util.Date;
import java.util.Map;

/**
 * 专家管理
 */
@RestController
@RequestMapping("/invest/expert")
public class AdminExpertController extends PageActionSupport {

    private Logger logger = LogManager.getLogger(AdminExpertController.class);

    @Autowired
    private SecUserService secUserService;
    @Autowired
    private LogService logService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    private AdminExpertService adminExpertService;


    /**
     * 获取专家列表
     */
    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String message = request.getParameter("message");
        String error = request.getParameter("error");
        String name = request.getParameter("name");
        String lang = request.getParameter("lang");
        String startTime = request.getParameter("startTime");
        String endTime = request.getParameter("endTime");
        Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("admin_expert_list");

        try {
            this.checkAndSetPageNo(pageNo);
            this.pageSize = 20;
            this.page = this.adminExpertService.pagedQuery(this.pageNo, this.pageSize, name, lang,startTime,endTime,status);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            return modelAndView;
        }

        modelAndView.addObject("pageNo", this.pageNo);
        modelAndView.addObject("pageSize", this.pageSize);
        modelAndView.addObject("page", this.page);
        modelAndView.addObject("message", message);
        modelAndView.addObject("error", error);
        modelAndView.addObject("name", name);
        modelAndView.addObject("lang", lang);
        return modelAndView;
    }

    /**
     * 新增专家页面
     */
    @RequestMapping( "/toAdd.action")
    public ModelAndView toAdd(HttpServletRequest request) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("pageNo",request.getParameter("pageNo"));
        modelAndView.setViewName("admin_expert_add");
        return modelAndView;
    }

    /**
     * 新增 专家
     *
     */
    @RequestMapping("/add.action")
    public ModelAndView add(HttpServletRequest request, @RequestParam Map<String, String> paramMap) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");
        String lang = request.getParameter("lang");
        String iconImg = request.getParameter("iconImg");
        String status = request.getParameter("status");
        String sort = request.getParameter("sort");
        String summary = request.getParameter("summary");
        String content_text = request.getParameter("content");

        ModelAndView modelAndView = new ModelAndView();

        modelAndView.addObject("pageNo", pageNo);
        modelAndView.addObject("name", name);
        modelAndView.addObject("content", content_text);
        modelAndView.addObject("lang", lang);
        modelAndView.addObject("status", status);
        modelAndView.addObject("sort", sort);
        modelAndView.addObject("iconImg", iconImg);
        try {

            String error = this.verif(name, content_text,iconImg,sort,summary);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }
            String userNameLogin = this.getUsername_login();

            Expert expert = new Expert();
            expert.setName(name);
            expert.setCreateTime(new Date());
            expert.setIconImg(iconImg);
            expert.setStatus(Integer.parseInt(status));
            expert.setContent(content_text);
            expert.setLang(lang);
            expert.setSort(Integer.parseInt(sort));
            expert.setSummary(summary);
            this.adminExpertService.save(expert);
            SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);
            String log = MessageFormat.format("ip:" + this.getIp() + ",管理员新增新闻,id:{0},标题:{1},语言:{2},内容:{3}",
                    expert.getId(), expert.getName(), expert.getLang(),expert.getContent());
            this.saveLog(sec, userNameLogin, log);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("admin_expert_add");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("admin_expert_add");
            return modelAndView;
        }

        modelAndView.addObject("message", "操作成功");
        modelAndView.setViewName("redirect:/" + "invest/expert/list.action");
        return modelAndView;
    }

    private String verif(String name, String content_text, String iconImg, String sort, String summary) {
        if(StringUtils.isEmptyString(name)){
            throw new BusinessException("昵称不能为空");
        }
        if(StringUtils.isEmptyString(content_text)){
            throw new BusinessException("内容不能为空");
        }
        if(StringUtils.isEmptyString(iconImg)){
            throw new BusinessException("请选择照片");
        }
        if(StringUtils.isEmptyString(sort)){
            throw new BusinessException("请输入排序");
        }
        if(StringUtils.isEmptyString(summary)){
            throw new BusinessException("请输入专家简介");
        }
        return null;
    }

    /**
     * 修改专家页面
     *
     */
    @RequestMapping( "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String id = request.getParameter("id");

        ModelAndView modelAndView = new ModelAndView();

        try {

            Expert expert = this.adminExpertService.findById(id);

            modelAndView.addObject("expert", expert);
//			modelAndView.addObject("title", news.getTitle());
//			modelAndView.addObject("content", news.getContent());
//			modelAndView.addObject("lang", news.getLang());
//			modelAndView.addObject("status", news.getStatus());
//			modelAndView.addObject("sort", news.getSort());
//			modelAndView.addObject("iconImg", iconImg);
//			modelAndView.addObject("releaseTime", releaseTime);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.setViewName("redirect:/" +  "invest/expert/list.action");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.setViewName("redirect:/"  + "invest/expert/list.action");
            return modelAndView;
        }

        modelAndView.setViewName("admin_expert_update");
        return modelAndView;
    }

    /**
     * 修改 专家
     *
     */
    @RequestMapping( "/update.action")
    public ModelAndView update(HttpServletRequest request, Expert expert) {

        ModelAndView modelAndView = new ModelAndView();
        String content_text = request.getParameter("content");
        try {
            String error = this.verif(expert.getName(),content_text,expert.getIconImg(),expert.getSort().toString(),expert.getSummary());
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }

            String userNameLogin = this.getUsername_login();

            SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);

            Expert bean = this.adminExpertService.findById(expert.getId().toString());
            expert.setCreateTime(bean.getCreateTime());
            expert.setContent(content_text);

            this.adminExpertService.update(expert);
            String log = MessageFormat.format(",昵称:{0},新语言:{1},新内容:{3}",
                    expert.getName(), expert.getLang(), content_text);
            this.saveLog(sec, userNameLogin, log);

        } catch (BusinessException e) {
            modelAndView.addObject("error", e.getMessage());
            modelAndView.addObject("expert", expert);
            modelAndView.setViewName("admin_expert_update");
            return modelAndView;
        } catch (Throwable t) {
            logger.error(" error ", t);
            modelAndView.addObject("error", "[ERROR] " + t.getMessage());
            modelAndView.addObject("expert", expert);
            modelAndView.setViewName("admin_expert_update");
            return modelAndView;
        }
        modelAndView.addObject("message", "操作成功");
        modelAndView.setViewName("redirect:/" + "invest/expert/list.action");
        return modelAndView;
    }

    /**
     * 删除 专家
     */
    @RequestMapping( "/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        String id = request.getParameter("id");
        String login_safeword = request.getParameter("login_safeword");

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("redirect:/" + "invest/expert/list.action");

        try {

            String userNameLogin = this.getUsername_login();

            SecUser sec = this.secUserService.findUserByLoginName(userNameLogin);
            this.checkLoginSafeword(sec, userNameLogin, login_safeword);

            Expert expert = this.adminExpertService.findById(id);

            String log = MessageFormat.format("ip:" + this.getIp() + ",管理员删除新闻,id:{0},原标题:{1},原语言:{2},原内容:{3}",
                    expert.getId(), expert.getName(), expert.getLang(),  expert.getContent());
            this.saveLog(sec, userNameLogin, log);
            this.adminExpertService.delete(expert);

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


    /**
     * 验证登录人资金密码
     */
    protected void checkLoginSafeword(SecUser secUser, String operatorUsername, String loginSafeword) {
//		SecUser sec = this.secUserService.findUserByLoginName(operatorUsername);
        String sysSafeword = secUser.getSafeword();
        String safeword_md5 = this.passwordEncoder.encodePassword(loginSafeword, operatorUsername);
        if (!safeword_md5.equals(sysSafeword)) {
            throw new BusinessException("登录人资金密码错误");
        }
    }

    public void saveLog(SecUser secUser, String operator, String context) {
        project.log.Log log = new project.log.Log();
        log.setCategory(Constants.LOG_CATEGORY_OPERATION);
        log.setOperator(operator);
        log.setUsername(secUser.getUsername());
        log.setPartyId(secUser.getPartyId());
        log.setLog(context);
        log.setCreateTime(new Date());
        this.logService.saveSync(log);
    }

}