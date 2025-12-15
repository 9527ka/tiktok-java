package security.filter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.RedirectUtils;

import com.alibaba.fastjson.JSON;

import kernel.web.ResultObject;

public class AjaxableAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

	/**
	 * If true, causes any redirection URLs to be calculated minus the protocol and
	 * context path (defaults to false).
	 */
	private boolean useRelativeContext = false;

	public void setUseRelativeContext(boolean useRelativeContext) {
		this.useRelativeContext = useRelativeContext;
	}

	protected void onSuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			Authentication authResult) throws IOException {
		super.onSuccessfulAuthentication(request, response, authResult);
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			ResultObject resultObject = new ResultObject();
			sendResponse(response, JSON.toJSONString(resultObject));
		}
	}

	private void sendResponse(HttpServletResponse response, String jsonStr)
			throws UnsupportedEncodingException, IOException {
		response.setContentType("application/json");
		response.setHeader("Access-Control-Allow-Origin", "*");
		ByteArrayInputStream input = new ByteArrayInputStream(jsonStr.getBytes("UTF-8"));
		ServletOutputStream output = response.getOutputStream();
		IOUtils.copy(input, output);
		IOUtils.closeQuietly(input);
	}

	protected void onUnsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException failed) throws IOException {
		super.onUnsuccessfulAuthentication(request, response, failed);
		if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			ResultObject resultObject = new ResultObject();
			resultObject.setCode("1");
			resultObject.setMsg(failed.getMessage());
			sendResponse(response, JSON.toJSONString(resultObject));
		}
	}

	protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
			throws IOException {
		// ignore redirect when request via ajax
		if (!"XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
			RedirectUtils.sendRedirect(request, response, url, useRelativeContext);
		}
	}
}