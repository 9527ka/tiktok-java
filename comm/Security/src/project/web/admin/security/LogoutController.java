package project.web.admin.security;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ext.Types;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.web.BaseAction;

import java.io.IOException;

/**
 * 后台管理系统退出登录
 *
 */
@RestController
public class LogoutController extends BaseAction {

	@RequestMapping(value = "public/logout.action")
	public void Logout(HttpServletRequest request, HttpServletResponse rsp) throws IOException {
		HttpSession session = request.getSession();
		session.setAttribute("SPRING_SECURITY_CONTEXT", null);
		String contextPath = Types.orValue( request.getContextPath(),"/");
		rsp.getWriter().write("<script>window.parent.location.href='"+contextPath+"';</script>");
		rsp.setContentType("text/html; charset=utf-8");
	}
}
