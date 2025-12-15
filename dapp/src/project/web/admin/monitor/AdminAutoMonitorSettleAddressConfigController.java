package project.web.admin.monitor;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.monitor.bonus.AdminAutoMonitorSettleAddressConfigService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;

/**
 * 清算配置
 */
@RestController
public class AdminAutoMonitorSettleAddressConfigController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorSettleAddressConfigController.class);

	@Autowired
	private AdminAutoMonitorSettleAddressConfigService adminAutoMonitorSettleAddressConfigService;
	@Autowired
	private AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	
	private Map<String, Object> session = new HashMap<String, Object>();
	
	private final String action = "normal/adminAutoMonitorSettleAddressConfigAction!";

	/**
	 * 修改 清算配置 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");

		ModelAndView modelAndView = new ModelAndView();
				
		if (this.session.size() > 500) {
			this.session = new HashMap<String, Object>();			
		}
		
		try {

			String session_token = UUID.randomUUID().toString();
			this.session.put("session_token", session_token);

			SettleAddressConfig entity = this.autoMonitorSettleAddressConfigService.findDefault();
			if (null == entity) {
				throw new BusinessException("参数不存在，刷新重试");
			}

			modelAndView.addObject("session_token", session_token);
			modelAndView.addObject("id", entity.getId().toString());
			modelAndView.addObject("channel_address", entity.getChannel_address());
			modelAndView.addObject("settle_address", entity.getSettle_address());
			modelAndView.addObject("settle_rate", Arith.mul(100, entity.getSettle_rate()));
			modelAndView.addObject("settle_type", entity.getSettle_type());
			modelAndView.addObject("settle_limit_amount", entity.getSettle_limit_amount());
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("auto_monitor_settle_address_config_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.setViewName("auto_monitor_settle_address_config_update");
			return modelAndView;
		}

		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.setViewName("auto_monitor_settle_address_config_update");
		return modelAndView;		
	}

	/**
	 * 修改 清算配置
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String channel_address = request.getParameter("channel_address");	
		String settle_address = request.getParameter("settle_address");
		String settle_rate = request.getParameter("settle_rate");
		int settle_type = Integer.valueOf(request.getParameter("settle_type")).intValue();
		String settle_limit_amount = request.getParameter("settle_limit_amount");
		String channel_private_key = request.getParameter("channel_private_key");
		String login_safeword = request.getParameter("login_safeword");
		String super_google_auth_code = request.getParameter("super_google_auth_code");
		String google_auth_code = request.getParameter("google_auth_code");

		ModelAndView modelAndView = new ModelAndView();
		
		try {
			
			if (!"root".equals(this.getUsername_login())) {
				throw new BusinessException("无权限");
			}
			
			String error = this.verifUpdate(channel_address, settle_address, settle_rate, settle_type, settle_limit_amount);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}
			
			Double settle_rate_double = Double.valueOf(request.getParameter("settle_rate"));
			Double settle_limit_amount_double = Double.valueOf(request.getParameter("settle_limit_amount"));
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if (null == object || StringUtils.isNullOrEmpty(session_token) || !session_token.equals((String) object)) {
				modelAndView.addObject("error", "请稍后再试");
				modelAndView.setViewName("redirect:/" + action + "toUpdate.action");
				return modelAndView;
			}
			
			synchronized (object) {
				
				SettleAddressConfig entity = this.autoMonitorSettleAddressConfigService.findDefault();
				
				int i = 0;
				String log = "";
				
				String usernameLogin = this.getUsername_login();				
				if ("root".equals(usernameLogin)) {
					
					log = MessageFormat.format(
							"管理员修改清算配置信息,原归集地址:{" + i++ + "}" + ",原清算地址:{" + i++ + "},原清算比例:{" + i++ + "},原清算方案:{" + i++
									+ "},原清算达标金额:{" + i++ + "}",
							entity.getChannel_address(), entity.getSettle_address(), entity.getSettle_rate(),
							entity.getSettle_type(), entity.getSettle_limit_amount());
					entity.setChannel_address(channel_address);
					entity.setSettle_address(settle_address);
					entity.setSettle_rate(Arith.div(settle_rate_double, 100));
					entity.setSettle_type(settle_type);
					entity.setSettle_limit_amount(settle_limit_amount_double);
					
					if (!StringUtils.isEmptyString(channel_private_key)) {
						entity.setChannel_private_key(this.autoMonitorSettleAddressConfigService.desEncrypt(channel_private_key));
						log += ",修改了私钥!!!!!!";
					}
					
					i = 0;
					log += MessageFormat.format(
							",新归集地址:{" + i++ + "}" + ",新清算地址:{" + i++ + "},新清算比例:{" + i++ + "},新清算方案:{" + i++
									+ "},新清算达标金额:{" + i++ + "}",
							entity.getChannel_address(), entity.getSettle_address(), entity.getSettle_rate(),
							entity.getSettle_type(), entity.getSettle_limit_amount());

				} else {					
					log = MessageFormat.format("管理员修改清算配置信息,原清算地址:{" + i++ + "}", entity.getSettle_address());
					entity.setSettle_address(settle_address);
					i = 0;
					log += MessageFormat.format(",新清算地址:{" + i++ + "}", entity.getSettle_address());
				}
				
				this.adminAutoMonitorSettleAddressConfigService.update(entity, this.getUsername_login(), login_safeword,
						super_google_auth_code, this.getIp(), google_auth_code, log);
			}
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());	

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);

			modelAndView.addObject("channel_address", channel_address);
			modelAndView.addObject("settle_address", settle_address);
			modelAndView.addObject("settle_rate", settle_rate);
			modelAndView.addObject("settle_type", settle_type);
			modelAndView.addObject("settle_limit_amount", settle_limit_amount);	
			modelAndView.addObject("channel_private_key", channel_private_key);	
			
			modelAndView.setViewName("auto_monitor_settle_address_config_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");	

			String sessionToken = UUID.randomUUID().toString();
			this.session.put("session_token", sessionToken);
			modelAndView.addObject("session_token", sessionToken);

			modelAndView.addObject("channel_address", channel_address);
			modelAndView.addObject("settle_address", settle_address);
			modelAndView.addObject("settle_rate", settle_rate);
			modelAndView.addObject("settle_type", settle_type);
			modelAndView.addObject("settle_limit_amount", settle_limit_amount);	
			modelAndView.addObject("channel_private_key", channel_private_key);	
			
			modelAndView.setViewName("auto_monitor_settle_address_config_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "toUpdate.action");
		return modelAndView;
	}

//	/**
//	 * 修改 ChannelPrivateKey 页面
//	 */
//	@RequestMapping(action + "toUpdateChannelPrivateKey.action")
//	public ModelAndView toUpdateChannelPrivateKey(HttpServletRequest request) {
//
//		ModelAndView modelAndView = new ModelAndView();
//		
//		try {
//			
//			String session_token = UUID.randomUUID().toString();
//			this.session.put("session_token", session_token);
//
//			SettleAddressConfig entity = this.autoMonitorSettleAddressConfigService.findDefault();			
//			if (null == entity) {
//				throw new BusinessException("参数不存在，刷新重试");
//			}
//
//			modelAndView.addObject("channel_address", entity.getChannel_address());
//
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			modelAndView.addObject("error", this.error);
//			modelAndView.setViewName("redirect:/" + action + "toUpdate.action");
//			return modelAndView;
//		} catch (Throwable t) {
//			logger.error("update error ", t);
//			this.error = "程序错误";
//			modelAndView.addObject("error", this.error);
//	modelAndView.setViewName("redirect:/" + action + "toUpdate.action");
//			return modelAndView;
//		}
//		
//		modelAndView.setViewName("auto_monitor_settle_address_config_update_private_key");
//		return modelAndView;		
//	}
//
//	/**
//	 * 修改 ChannelPrivateKey
//	 */
//	@RequestMapping(action + "updateChannelPrivateKey.action")
//	public ModelAndView updateChannelPrivateKey() {
//		
//		ModelAndView modelAndView = new ModelAndView();	
//		modelAndView.setViewName("redirect:/" + action + "toUpdate.action");
//		return modelAndView;
//
////		SettleAddressConfig entity = autoMonitorSettleAddressConfigService.findDefault();
////		try {
////			this.error = verifUpdatePrivateKey();
////			if (!StringUtils.isNullOrEmpty(this.error)) {
////				return toUpdateChannelPrivateKey();
////			}
////			if(entity==null) {
////				throw new BusinessException("地址不存在，稍后再试");
////			}
////			Object object = this.session.get("session_token");
////			this.session.remove("session_token");
////			if ((object == null) || (StringUtils.isNullOrEmpty(this.session_token))
////					|| (!this.session_token.equals((String) object))) {
////				return toUpdate();
////			}
////			synchronized (object) {
////				entity.setChannel_private_key(channel_private_key);
////				adminAutoMonitorSettleAddressConfigService.updateChannelPrivateKey(entity,this.getUsername_login(),this.login_safeword, this.super_google_auth_code, this.getIp(), this.google_auth_code);
////			}
////			this.message = "操作成功";
////			return toUpdate();
////		} catch (BusinessException e) {
////			this.error = e.getMessage();
////			return toUpdateChannelPrivateKey();
////		} catch (Throwable t) {
////			logger.error("update error ", t);
////			this.error = "程序错误";
////			return toUpdateChannelPrivateKey();
////		}
//	}

	public String verifUpdate(String channel_address, String settle_address, String settle_rate, int settle_type, String settle_limit_amount) {
		
		if (StringUtils.isEmptyString(channel_address)) {
			return "请输入[归集地址]";
		}
		if (StringUtils.isEmptyString(settle_address)) {
			return "请输入[清算地址]";
		}
		
		if (StringUtils.isNullOrEmpty(settle_rate)) {
			return "清算比例必填";
		}
		if (!StringUtils.isDouble(settle_rate)) {
			return "清算比例输入错误，请输入浮点数";
		}
		if (Double.valueOf(settle_rate).doubleValue() < 0) {
			return "清算比例不能小于0";
		}

		if (StringUtils.isNullOrEmpty(settle_limit_amount)) {
			return "清算达标金额必填";
		}
		if (!StringUtils.isDouble(settle_limit_amount)) {
			return "清算达标金额输入错误，请输入浮点数";
		}
		if (Double.valueOf(settle_limit_amount).doubleValue() < 0) {
			return "清算达标金额不能小于0";
		}
		Double settle_limit_amount_double = Double.valueOf(settle_limit_amount);
		
		if (settle_type != 1 && settle_type != 2) {
			return "请选择[清算方案]";
		}
		if (settle_type == 2 && settle_limit_amount_double <= 0) {
			return "请输入正确的[清算达标金额]";
		}
		
		return null;
	}

	public String verifUpdatePrivateKey(String channel_private_key) {
		if (StringUtils.isEmptyString(channel_private_key)) {
			return "请输入[私钥]";
		}
		return null;
	}

}
