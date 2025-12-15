//package project.web.admin;
//
//import java.util.HashMap;
//
//import javax.servlet.http.HttpServletRequest;
//
//import org.apache.logging.log4j.LogManager;
//import org.apache.logging.log4j.Logger;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import kernel.exception.BusinessException;
//import kernel.util.StringUtils;
//import kernel.web.BaseAction;
//import kernel.web.ResultObject;
//import project.syspara.SysparaService;
//import project.user.captcha.GeetestService;
//
///**
// * Geetest滑动图片验证
// */
//@RestController
//public class AdminGeetestController extends BaseAction {
//
//	private Logger logger = LogManager.getLogger(AdminGeetestController.class);
//
//	@Autowired
//	private GeetestService geetestService;
//	@Autowired
//	private SysparaService sysparaService;
//
//	private final String action = "normal/geetest!";
//
//	/**
//	 * 获取 challenge和captcha_id
//	 */
//	@RequestMapping(action + "getChallengeAndCaptchaid.action")
//	public Object getChallengeAndCaptchaid(HttpServletRequest request) {
//
//		ResultObject resultObject = new ResultObject();
//
//		try {
//
//			// 自定义参数，可选择添加
//			HashMap<String, String> param = new HashMap<String, String>();
//
//			// 网站用户id
//			param.put("user_id", "test");
//
//			String geetest_id = this.sysparaService.find("geetest_id").getValue();
//			String geetest_key = this.sysparaService.find("geetest_key").getValue();
//			String new_failback = this.sysparaService.find("geetest_new_failback").getValue();
//			if(StringUtils.isEmptyString(geetest_id) || StringUtils.isEmptyString(geetest_key) || StringUtils.isEmptyString(new_failback)) {
//				throw new BusinessException("系统参数错误");
//			}
//
//			param.put("geetest_id", geetest_id);
//			param.put("geetest_key", geetest_key);
//			param.put("new_failback", new_failback);
//
//			HashMap<String, String> retMap = this.geetestService.preProcess(param);
//			retMap.put("user_id", param.get("user_id"));
//			retMap.put("gt_server_status", retMap.get("success"));
//
//			resultObject.setData(retMap);
//
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Throwable t) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", t);
//		}
//
//		return resultObject;
//	}
//
//	/**
//	 * 返回验证结果, request表单中必须包含challenge, validate, seccode
//	 */
//	@RequestMapping(action + "verify.action")
//	public Object verify(HttpServletRequest request) {
//		String challenge = request.getParameter("geetest_challenge");
//		String validate = request.getParameter("geetest_validate");
//		String seccode = request.getParameter("geetest_seccode");
//		String gt_server_status = request.getParameter("gt_server_status");
////		String user_id = request.getParameter("user_id");
//
//		ResultObject resultObject = new ResultObject();
//
//		try {
//
//			if (StringUtils.isEmptyString(challenge)) {
//				throw new BusinessException("fail, challenge error.");
//			}
//			if (StringUtils.isEmptyString(validate)) {
//				throw new BusinessException("fail, validate error.");
//			}
//			if (StringUtils.isEmptyString(seccode)) {
//				throw new BusinessException("fail, seccode error.");
//			}
//			if (StringUtils.isEmptyString(gt_server_status)) {
//				throw new BusinessException("fail, gt_server_status error.");
//			}
//
//			// 自定义参数，可选择添加
//			HashMap<String, String> param = new HashMap<String, String>();
//
//			// 网站用户id
//			param.put("user_id", "test");
//
//			param.put("challenge", challenge);
//			param.put("validate", validate);
//			param.put("seccode", seccode);
//
//			String geetest_id = this.sysparaService.find("geetest_id").getValue();
//			String geetest_key = this.sysparaService.find("geetest_key").getValue();
//			String new_failback = this.sysparaService.find("geetest_new_failback").getValue();
//			if (StringUtils.isEmptyString(geetest_id) || StringUtils.isEmptyString(geetest_key) || StringUtils.isEmptyString(new_failback)) {
//				throw new BusinessException("系统参数错误");
//			}
//
//			param.put("geetest_id", geetest_id);
//			param.put("geetest_key", geetest_key);
//			param.put("new_failback", new_failback);
//
//			int gtResult = 0;
//			if ("1".equals(gt_server_status)) {
//				// gt-server正常，向gt-server进行二次验证
//				gtResult = this.geetestService.enhencedValidateRequest(param);
//				System.out.println(gtResult);
//			} else {
//				// gt-server非正常情况下，进行failback模式验证
//				System.out.println("failback:use your own server captcha validate");
//				gtResult = this.geetestService.failbackValidateRequest(param);
//				System.out.println(gtResult);
//			}
//
//			HashMap<String, String> retMap = this.geetestService.preProcess(param);
//
//			if (1 == gtResult) {
//				// 验证成功
//				retMap.put("status", "success");
//				retMap.put("version", this.geetestService.getVersionInfo());
//			} else {
//				// 验证失败
//				retMap.put("status", "fail");
//				retMap.put("version", this.geetestService.getVersionInfo());
//			}
//
//			resultObject.setData(retMap);
//
//		} catch (BusinessException e) {
//			resultObject.setCode("1");
//			resultObject.setMsg(e.getMessage());
//		} catch (Throwable t) {
//			resultObject.setCode("1");
//			resultObject.setMsg("程序错误");
//			logger.error("error:", t);
//		}
//
//		return resultObject;
//	}
//
//}
