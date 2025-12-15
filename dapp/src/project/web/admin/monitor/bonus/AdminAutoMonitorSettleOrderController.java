package project.web.admin.monitor.bonus;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
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
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.monitor.AutoMonitorTransferAddressConfigService;
import project.monitor.bonus.AdminAutoMonitorSettleOrderService;
import project.monitor.model.AutoMonitorTransferAddressConfig;

/**
 * 清算订单记录
 *
 */
@RestController
public class AdminAutoMonitorSettleOrderController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorSettleOrderController.class);

	@Autowired
	private AdminAutoMonitorSettleOrderService adminAutoMonitorSettleOrderService;
	@Autowired
	private AutoMonitorTransferAddressConfigService autoMonitorTransferAddressConfigService;
	
	private Map<String, Object> session = new HashMap();
	private final static Object obj = new Object();
	
	private final String action = "normal/adminAutoMonitorSettleOrderAction!";
	
	/**
	 * 清算订单记录-列表
	 */
	@RequestMapping(value = action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 20;
		String loginPartyId = getLoginPartyId();
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		String usename_para = request.getParameter("usename_para");
		String succeeded_para = request.getParameter("succeeded_para");
		// 订单号
		String order_para = request.getParameter("order_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		
		this.page = this.adminAutoMonitorSettleOrderService.pagedQuery(pageNo, pageSize,
				usename_para, succeeded_para, order_para, start_time, end_time, loginPartyId);
		
		for(Map<String,Object> map : (List<Map<String,Object>>)page.getElements()) {
			map.put("txn_hash_hide", map.get("txn_hash")==null?null:hideAddress(map.get("txn_hash").toString(),4));
			map.put("from_address_hide", map.get("from_address")==null?null:hideAddress(map.get("from_address").toString(),4));
			map.put("to_address_hide", map.get("to_address")==null?null:hideAddress(map.get("to_address").toString(),4));
			map.put("volume", new BigDecimal(map.get("volume").toString()).toPlainString());
		}
		
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		
		model.addObject("session_token", session_token);
		model.addObject("order_para", order_para);
		
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("auto_monitor_settle_order_list");
		return model;
		
	}

	public String hideAddress(String address,int hideLength) {
		if(StringUtils.isEmptyString(address)) {
			return address;
		}
		if(address.length()>hideLength*2) {
			return address.substring(0, hideLength)+"****"+address.substring(address.length()-hideLength);
		}
		return address;
	}
	
	/**
	 * 所有转账地址处理
	 * @return
	 */
	public Map<String,String> allTransferAddress(){
		List<AutoMonitorTransferAddressConfig> findAll = autoMonitorTransferAddressConfigService.findAll();
		Map<String,String> map = new HashMap<>();
		for(AutoMonitorTransferAddressConfig add:findAll) {
			map.put(add.getId().toString(), add.getAddress());
		}
		return map;
	}
	
	@RequestMapping(value = action + "transferOne.action")
	public ModelAndView transferOne(HttpServletRequest request) {
		
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		
		try {
			String session_token = request.getParameter("session_token");
			String id = request.getParameter("id");
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
					|| (!session_token.equals((String) object))) {
				return list(request);
			}
			
			synchronized (obj) {
				adminAutoMonitorSettleOrderService.updateToTransfer(id, this.getUsername_login(), this.getIp(getRequest()));
				ThreadUtils.sleep(200);
			}
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		}
		
		model.addObject("error", error);
		model.addObject("message", message);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;

	}
	
	/**
	 * 清算订单记录-清算剩余未结算订单
	 */
	@RequestMapping(value = action + "transferLast.action")
	public ModelAndView transferLast(HttpServletRequest request) {
		ModelAndView model = new ModelAndView();
		String message = "";
		String error = "";
		
		try {
			String session_token = request.getParameter("session_token");
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
					|| (!session_token.equals((String) object))) {
				return list(request);
			}
			String safeword = request.getParameter("safeword");
			String super_google_auth_code = request.getParameter("super_google_auth_code");
			String google_auth_code = request.getParameter("google_auth_code");
			synchronized (obj) {
				adminAutoMonitorSettleOrderService.transferLast(this.getUsername_login(), this.getIp(getRequest()), 
						safeword, super_google_auth_code, google_auth_code);
				ThreadUtils.sleep(200);
			}
			this.message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Throwable t) {
			logger.error(" error ", t);
			error = ("[ERROR] 服务器错误");
		} 
		
		model.addObject("error", error);
		model.addObject("message", message);
		model.setViewName("redirect:/" + action + "list.action");
	    return model;
	}

	public void setSession(Map<String, Object> session) {
		this.session = session;
	}

}
