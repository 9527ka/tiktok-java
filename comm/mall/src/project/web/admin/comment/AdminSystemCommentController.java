package project.web.admin.comment;


import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import project.mall.comment.AdminSystemCommentService;
import project.mall.goods.model.SystemComment;
import security.SecUser;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * 评论库库
 */
@RestController
@RequestMapping("/mall/comment")
public class AdminSystemCommentController extends PageActionSupport {

    private static Log logger = LogFactory.getLog(AdminSystemCommentController.class);

    @Resource
    protected AdminSystemCommentService adminSystemCommentService;

    @RequestMapping("/list.action")
    public ModelAndView list(HttpServletRequest request) {

        this.pageSize = 20;
        String error = request.getParameter("error");
        String message = request.getParameter("message");
        Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
        ModelAndView model = new ModelAndView("admin_comment_list");

        try {
            this.checkAndSetPageNo(request.getParameter("pageNo"));
            this.page = adminSystemCommentService.pagedQuery(this.pageNo, this.pageSize, status);
        } catch (BusinessException e) {
            model.addObject("error", error);
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());

        }
        model.addObject("page",page);
        model.addObject("message",message);
        model.addObject("pageNo",this.pageNo);
        model.addObject("status",status);
        return model;
    }

    /**
     * 新增页面
     *
     * @param request
     * @return
     */
    @RequestMapping(value = "/toAdd.action")
    public ModelAndView toAdd(HttpServletRequest request) {
        String error = request.getParameter("error");
        String pageNo = request.getParameter("pageNo");
        String sellerId = request.getParameter("sellerId");
        String sellerGoodsId = request.getParameter("sellerGoodsId");
        ModelAndView model = new ModelAndView();
        model.setViewName("admin_comment_add");
        model.addObject("error", error);
        model.addObject("pageNo",pageNo);
        model.addObject("sellerId",sellerId);
        model.addObject("sellerGoodsId",sellerGoodsId);
        return model;
    }

    @RequestMapping(value =  "/add.action")
    public ModelAndView add(HttpServletRequest request, SystemComment comment) {
        String pageNo = request.getParameter("pageNo");
        String name = request.getParameter("name");

        ModelAndView model = new ModelAndView();
        model.addObject("name", name);
        model.addObject("pageNo", pageNo);
        try {
            adminSystemCommentService.save(comment);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("admin_comment_add");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.setViewName("admin_comment_add");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("PageNo", pageNo);
        modelAndView.addObject("comment", comment);
        modelAndView.setViewName("redirect:/" +  "/mall/comment/list.action");
        return modelAndView;
    }


    @RequestMapping(value = "/toUpdate.action")
    public ModelAndView toUpdate(HttpServletRequest request) {
        String error = request.getParameter("error");
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        ModelAndView model = new ModelAndView();
        try {
            SystemComment comment = adminSystemCommentService.findCommentById(id);
            if (Objects.isNull(comment)){
                throw new BusinessException("此评论已删除");
            }
            model.addObject("comment",comment);
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" +  "/mall/comment/list.action");
            return model;
        } catch (Exception e) {
            logger.error("error ", e);
            model.setViewName("redirect:/" +  "/mall/comment/list.action");
            return model;
        }
        model.setViewName("admin_comment_update");
        model.addObject("error", error);
        model.addObject("pageNo",pageNo);
        return model;
    }

    @RequestMapping("/delete.action")
    public ModelAndView delete(HttpServletRequest request) {
        ModelAndView model = new ModelAndView();
        model.setViewName("redirect:/" + "/mall/comment/list.action");
        try {

            adminSystemCommentService.delete(request.getParameter("id"));
            this.message = "操作成功";
            model.addObject("message",message);
            model.addObject("pageNo",pageNo);
            return model;
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            return model;
        } catch (Throwable t) {
            logger.error(" error ", t);
            model.addObject("error", "[ERROR] " + t.getMessage());
            return model;
        }
    }


    @RequestMapping(value =  "/updateStatus.action")
    public ModelAndView updateStatus(HttpServletRequest request) {
        String pageNo = request.getParameter("pageNo");
        String id = request.getParameter("id");
        String status = request.getParameter("status");
        ModelAndView model = new ModelAndView();
        model.addObject("pageNo",pageNo);
        try {
            adminSystemCommentService.updateStatus(id,Integer.parseInt(status));
        } catch (BusinessException e) {
            model.addObject("error", e.getMessage());
            model.setViewName("redirect:/" + "/mall/comment/list.action");
            return model;
        } catch (Throwable t) {
            logger.error("update error ", t);
            model.addObject("error", "程序错误");
            model.setViewName("redirect:/" + "/mall/comment/list.action");
            return model;
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("message", "操作成功");
        modelAndView.addObject("pageNo", pageNo);
        modelAndView.setViewName("redirect:/" + "/mall/comment/list.action");
        return modelAndView;
    }
}
