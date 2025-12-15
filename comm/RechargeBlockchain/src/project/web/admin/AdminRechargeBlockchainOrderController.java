package project.web.admin;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.blockchain.AdminRechargeBlockchainOrderService;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.notification.utils.notify.dto.RechargeData;
import project.mall.utils.CsrfTokenUtil;
import project.syspara.SysParaCode;
import project.syspara.SysparaService;
import util.LockFilter;

/**
 * 区块链充值订单
 */
@Slf4j
@RestController
public class AdminRechargeBlockchainOrderController extends PageActionSupport {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AdminRechargeBlockchainOrderService adminRechargeBlockchainOrderService;

	@Autowired
	private SysparaService sysparaService;

	@Autowired
	private NotificationHelperClient notificationHelperClient;

	@Autowired
	private RechargeBlockchainService rechargeBlockchainService;

	@Autowired
	private HttpSession httpSession;


	private final static Object obj = new Object();

	private final String action = "normal/adminRechargeBlockchainOrderAction!";

	/**
	 * 获取 区块链充值订单 列表
	 *
	 * name_para 链名称 
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String state_para = request.getParameter("state_para");
		String order_no_para = request.getParameter("order_no_para");
		String rolename_para = request.getParameter("rolename_para");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String reviewStartTime = request.getParameter("reviewStartTime");
		String reviewEndTime = request.getParameter("reviewEndTime");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("recharge_blockchain_list");

		try {
			this.checkAndSetPageNo(pageNo);

			String session_token = CsrfTokenUtil.generateToken();
			CsrfTokenUtil.saveTokenInSession(httpSession,session_token);

			this.pageSize = 20;

			Integer state_para_int = null;
			if (!StringUtils.isEmptyString(state_para)) {
				state_para_int = Integer.valueOf(state_para);
			}

			String loginPartyId = getLoginPartyId();

			this.page = this.adminRechargeBlockchainOrderService.pagedQuery(this.pageNo, this.pageSize, name_para,
					state_para_int, loginPartyId, order_no_para, rolename_para, start_time, end_time,reviewStartTime,reviewEndTime);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
//				double succeeded =  (int) map.get("succeeded");
				Double oriAmount = (Double)map.get("channel_amount");
				if (oriAmount != null) {
					//Arith.round(money,2);
					// 换算成 USDT 的充值金额
					// 注意科学计数法问题
					String strAmount = BigDecimal.valueOf(oriAmount).toString();

					// 原始充值币种下的金额
					int idx = strAmount.indexOf(".");
					if (idx < 0) {
						map.put("channelAmount", strAmount + ".00");
					} else {
						int len = strAmount.substring(idx + 1).length();
						if (len <= 10) {
							map.put("channelAmount", strAmount);
						} else {
							map.put("channelAmount", strAmount.substring(0, idx + 10));
						}
					}
				}
//				if(succeeded == 0){
//					map.put("amount","--");
//					map.put("channel_amount","--");
//				}
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}
			String clerkOpen = this.sysparaService.find(SysParaCode.CLERK_IS_OPEN.getCode()).getValue();
			String rechargeIsOpen = this.sysparaService.find(SysParaCode.RECHARGE_IS_OPEN.getCode()).getValue();
			modelAndView.addObject("session_token", session_token);
			modelAndView.addObject("isOpen", clerkOpen);
			modelAndView.addObject("rechargeIsOpen", rechargeIsOpen);

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
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("state_para", state_para);
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("reviewStartTime", reviewStartTime);
		modelAndView.addObject("reviewEndTime", reviewEndTime);
		return modelAndView;
	}

	/**
	 * 手动到账
	 */
	@RequestMapping(action + "onsucceeded.action")
	public ModelAndView onsucceeded(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String order_no = request.getParameter("order_no");
		String success_amount = request.getParameter("success_amount");
		String transfer_usdt = request.getParameter("transfer_usdt");
		String safeword = request.getParameter("safeword");
		String rechargeCommissionStr = request.getParameter("rechargeCommission");
		String state_para = request.getParameter("state_para");
		String remarks = request.getParameter("remarks");
		double rechargeCommission = 0.0;
		if (StrUtil.isNotBlank(rechargeCommissionStr) && !Objects.equals(rechargeCommissionStr, "null")) {
			rechargeCommission = Double.parseDouble(rechargeCommissionStr);
		}

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("state_para",state_para);
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {
			String sessionToken = (String) httpSession.getAttribute("session_token");
			CsrfTokenUtil.removeTokenFromSession(httpSession);

			log.info("---> recharge onsucceededToken 存储token: sessionToken:{}, 页面传参session_token:{},订单号order_no:{}", sessionToken,session_token,order_no);
			if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
				// 令牌无效，显示错误消息
				throw new BusinessException("操作成功，请勿重复点击");
			}

			if (StringUtils.isNullOrEmpty(transfer_usdt)) {
				throw new BusinessException("到账金额必填");
			}
			if (!StringUtils.isDouble(transfer_usdt)) {
				throw new BusinessException("到账金额不是浮点数");
			}
			if (Double.valueOf(transfer_usdt).doubleValue() < 0) {
				throw new BusinessException("到账金额不能小于0");
			}

//			double transferAmount = Double.valueOf(request.getParameter("transfer_usdt")).doubleValue();
//			double successAmount = Double.valueOf(request.getParameter("success_amount")).doubleValue();

			synchronized (obj) {
				Map map = this.adminRechargeBlockchainOrderService.saveSucceeded(order_no, safeword, this.getUsername_login(), transfer_usdt, success_amount, rechargeCommission,remarks);
				boolean flag = (boolean) map.get("flag");
				if (flag){
					WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
					RechargeInfo info = (RechargeInfo) map.get("info");
					wac.publishEvent(new RechargeSuccessEvent(this, info));
				}
				ThreadUtils.sleep(300);
			}

			// 发送消息提醒
			String applyUserId = "";
			try {
				RechargeBlockchain recharge = rechargeBlockchainService.findByOrderNo(order_no);
				applyUserId = recharge.getPartyId().toString();
				RechargeData info = new RechargeData();
				info.setRechargeUserId(applyUserId);
				info.setAmount(Double.parseDouble(transfer_usdt));
				notificationHelperClient.notifyRechargeSuccess(info, 3);
			} catch (Exception e) {
				logger.error("---> 审核用户:{} 的一笔充值订单:{} 通过后，发送提醒消息失败", applyUserId, order_no);
			}
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
	 * 手动修改用户充值图片
	 */
	@RequestMapping(action + "onChangeImg.action")
	public ModelAndView onChangeImg(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String order_no = request.getParameter("order_no");
		String img = request.getParameter("img");
		String safeword = request.getParameter("safeword");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		try {

			String sessionToken = (String) httpSession.getAttribute("session_token");
			CsrfTokenUtil.removeTokenFromSession(httpSession);

			log.info("---> recharge onChangeImgToken 存储token: sessionToken:{}, 页面传参session_token:{},订单号order_no:{}", sessionToken,session_token,order_no);
			if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
				// 令牌无效，显示错误消息
				throw new BusinessException("操作成功，请勿重复点击");
			}

			synchronized (obj) {
				this.adminRechargeBlockchainOrderService.saveRechargeImg(order_no, img, safeword, this.getUsername_login(), this.getLoginPartyId());
				ThreadUtils.sleep(300);
			}

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
	 * 驳回充值申请
	 */
	@RequestMapping(action + "reject.action")
	public ModelAndView reject(HttpServletRequest request) {
		String id = request.getParameter("id");
		String failure_msg = request.getParameter("failure_msg");
		String state_para = request.getParameter("state_para");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		modelAndView.addObject("state_para",state_para);
		boolean lock = false;

		try {

			if (!LockFilter.add(id)) {
				throw new BusinessException("系统繁忙，请稍后重试");
			}

			lock = true;

			// 统一处理失败接口
			this.adminRechargeBlockchainOrderService.saveReject(id, failure_msg, this.getUsername_login(), this.getLoginPartyId());

			ThreadUtils.sleep(300);

			String applyUserId = "";
			try {
				RechargeBlockchain recharge = adminRechargeBlockchainOrderService.get(id);
				applyUserId = recharge.getPartyId().toString();
				RechargeData info = new RechargeData();
				info.setRechargeUserId(applyUserId);
				info.setAmount(recharge.getAmount());
				info.setOrderNo(recharge.getOrder_no());
			    if(StringUtils.isEmptyString(failure_msg)){
					info.setReason("--");
				} else {
					info.setReason(failure_msg);
				}
				notificationHelperClient.notifyRechargeFail(info);
			} catch (Exception e) {
				logger.error("---> 审核用户:{} 的一笔充值订单:{} 通过后，发送提醒消息失败", applyUserId, id);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		} finally {
			if (lock) {
				LockFilter.remove(id);
			}
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	/**
	 * 修改备注信息
	 */
	@RequestMapping(action + "reject_remark.action")
	public ModelAndView reject_remark(HttpServletRequest request) {
		String id = request.getParameter("id");
		String failure_msg = request.getParameter("failure_msg");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");

		boolean lock = false;

		try {

			if (!LockFilter.add(id)) {
				throw new BusinessException("系统繁忙，请稍后重试");
			}

			lock = true;

			// 统一处理失败接口
			this.adminRechargeBlockchainOrderService.saveRejectRemark(id, failure_msg, this.getUsername_login(), this.getLoginPartyId());

			ThreadUtils.sleep(300);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		} finally {
			if (lock) {
				LockFilter.remove(id);
			}
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

}
