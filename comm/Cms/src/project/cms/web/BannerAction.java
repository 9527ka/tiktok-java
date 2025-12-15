package project.cms.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// import org.apache.struts2.ServletActionContext;

import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.cms.Banner;
import project.cms.BannerService;

public class BannerAction extends BaseAction {

	private static Log logger = LogFactory.getLog(BannerAction.class);
	/**
	 * 
	 */
	private static final long serialVersionUID = -7047628687006590684L;
	private BannerService bannerService;
	private String id;
	private String model;
	private String language;
	/**
	 * 业务代码， 同种内容 不同语言下的code相同
	 */
	private String content_code;

//	public String get() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		PrintWriter out = response.getWriter();
//		Map<String, Object> data = new HashMap<String, Object>();
//		try {
//			Banner banner = bannerService.cacheByCodeAndLanguage(this.content_code, this.language);
//			resultObject.setData(bannerService.bindOne(banner));
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", e);
//		}
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}
//
//	public String list() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		PrintWriter out = response.getWriter();
//		try {
//			List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();
//			List<Banner> cacheListByModel = bannerService.cacheListByModelAndLanguage(this.model, this.language);
//
//			for (Banner banner : cacheListByModel) {
//				result.add(bannerService.bindOne(banner));
//			}
//			resultObject.setData(result);
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Exception e) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", e);
//		}
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}

	public void setId(String id) {
		this.id = id;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public void setContent_code(String content_code) {
		this.content_code = content_code;
	}

	public void setBannerService(BannerService bannerService) {
		this.bannerService = bannerService;
	}

	
}
