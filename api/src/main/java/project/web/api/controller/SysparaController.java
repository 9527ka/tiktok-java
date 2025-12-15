package project.web.api.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import ext.Strings;
import ext.translate.Locales;
import ext.translate.TranslateLocale;
import kernel.exception.BusinessException;
import kernel.web.ResultObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.web.api.service.LocalSysparaService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class SysparaController {

	private static final Logger log = LoggerFactory.getLogger(SysparaController.class);
	@Autowired
	private LocalSysparaService localSysparaService;

	/**
	 * 可逗号相隔，查询多个参数值。 exchange_rate_out 兑出货币和汇率; exchange_rate_in
	 * 兑入货币和汇率;withdraw_fee 提现手续费，type=fixed是单笔固定金额，=rate是百分比，结果到小数点2位。
	 * index_top_symbols 首页显示的4个品种。customer_service_url 在线客服URL
	 */

	@RequestMapping("api/syspara!getSyspara.action")
	public Object getSyspara(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		try {
			String code = request.getParameter("code");
			Map<String, Object> data = localSysparaService.find(code);
			resultObject.setData(data);
		} catch (BusinessException e) {
			resultObject.setCode("402");
			resultObject.setMsg(e.getMessage());

		} catch (Throwable e) {
			resultObject.setCode("500");
			resultObject.setMsg("服务器错误");
		}
		return resultObject;
	}



@RequestMapping("api/syspara!getSiteParams.action")
public Object getSiteParams(HttpServletRequest request) {
		String lang = request.getParameter("lang");
	ResultObject resultObject = new ResultObject();
	try {
		List<String> codes = new ArrayList();
		codes.add("mall_site_name");
		//codes.add("mall_site_domain");
		codes.add("mall_site_logo");
		codes.add("mall_site_owner");
		codes.add("mall_site_reverse_logo");
		codes.add("mall_site_icon");
		codes.add("mall_site_summary");
	//	codes.add("mall_default_lang");
		Map<String, Object> data = localSysparaService.find(Strings.join(codes,","));
		if(!Strings.isNullOrEmpty(lang)){
			data.put("mall_site_summary", TranslateLocale.get(data.get("mall_site_summary").toString(), Locales.LANG_ZH_CN,lang));
		}
		resultObject.setData(data);
	} catch (BusinessException e) {
		log.error("获取参数错误",e);
		resultObject.setCode("402");
		resultObject.setMsg(e.getMessage());
	} catch (Throwable e) {
		log.error("获取站点信息报错",e);
		resultObject.setCode("500");
		resultObject.setMsg("服务器错误");
	}
	return resultObject;
}
}
