package project.web.admin;

import java.math.BigDecimal;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import cn.hutool.core.util.StrUtil;
import db.util.BackupUtil;
import kernel.util.Arith;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.mall.notification.utils.notify.client.NotificationHelperClient;
import project.mall.notification.utils.notify.dto.WithdrawData;
import project.mall.utils.CsrfTokenUtil;
import project.syspara.SysParaCode;
import project.syspara.SysparaService;
import project.withdraw.AdminWithdrawService;
import project.withdraw.Withdraw;

/**
 * 提现订单
 */
@Slf4j
@RestController
public class AdminWithdrawController extends PageActionSupport {


	@Autowired
	private AdminWithdrawService adminWithdrawService;

	@Autowired
	private SysparaService sysparaService;

	@Autowired
	private NotificationHelperClient notificationHelperClient;

	@Autowired
	private HttpSession httpSession;


	private final static Object obj = new Object();

	private final String action = "normal/adminWithdrawAction!";

	/**
	 * 获取 提现订单 列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String name_para = request.getParameter("name_para");
		String succeeded_para = request.getParameter("succeeded_para");
		String order_no_para = request.getParameter("order_no_para");
		String rolename_para = request.getParameter("rolename_para");
		String method = request.getParameter("method");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String reviewStartTime = request.getParameter("reviewStartTime");
		String reviewEndTime = request.getParameter("reviewEndTime");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("withdraw_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 20;

			String session_token = CsrfTokenUtil.generateToken();
			CsrfTokenUtil.saveTokenInSession(httpSession,session_token);
			Integer succeeded_para_int = null;
			if (!StringUtils.isEmptyString(succeeded_para)) {
				succeeded_para_int = Integer.valueOf(succeeded_para).intValue();
			}

			String loginPartyId = this.getLoginPartyId();

			this.page = this.adminWithdrawService.pagedQuery(this.pageNo, this.pageSize, name_para, succeeded_para_int,
					loginPartyId, order_no_para, rolename_para,method,start_time, end_time,reviewStartTime,reviewEndTime);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);
				map.put("volume", new BigDecimal(map.get("volume").toString()).toPlainString());
				map.put("amount", new BigDecimal(map.get("amount").toString()).toPlainString());

				Object amountUsdt = map.get("amount");
				String strAmountUsdt = amountUsdt.toString();
				if (strAmountUsdt != null) {
					//Arith.round(money,2);
					// 换算成 USDT 的充值金额
					// 原始充值币种下的金额
					int idx = strAmountUsdt.indexOf(".");
					if (idx < 0) {
						map.put("usdtAmount", strAmountUsdt + ".00");
					} else {
						int len = strAmountUsdt.substring(idx + 1).length();
						if (len <= 10) {
							map.put("usdtAmount", strAmountUsdt);
						} else {
							map.put("usdtAmount", strAmountUsdt.substring(0, idx + 10));
						}
					}
				}

				String coinType = Constants.WALLET;
				if (map.get("method") != null && StrUtil.isNotBlank(map.get("method").toString())) {
					coinType = map.get("method").toString().toUpperCase();
				}
				map.put("method", coinType);

				if (!coinType.equalsIgnoreCase("USDT")
						&& !coinType.equalsIgnoreCase("BANK")
						&& !coinType.equalsIgnoreCase("USDC")) {
					// 后来新增的字段，为了兼容展示早期记录，有汇率换算的币种，实际到账金额使用该字段值
					map.put("amount", new BigDecimal(map.get("arrivalAmount").toString()).toPlainString());
				}
				Object amount = map.get("amount");
				String strAmount = amount.toString();
				if (strAmount != null) {
					//Arith.round(money,2);
					// 换算成 USDT 的充值金额
					// 原始充值币种下的金额
					int idx = strAmount.indexOf(".");
					if (idx < 0) {
						map.put("amount", strAmount + ".00");
					} else {
						int len = strAmount.substring(idx + 1).length();
						if (len <= 10) {
							map.put("amount", strAmount);
						} else {
							map.put("amount", strAmount.substring(0, idx + 10));
						}
					}
				}

				Object volume = map.get("volume");
				String strVolume = volume.toString();
				if (strVolume != null) {
					//Arith.round(money,2);
					// 换算成 USDT 的充值金额
					// 原始充值币种下的金额
					int idx = strVolume.indexOf(".");
					if (idx < 0) {
						map.put("volume", strVolume + ".00");
					} else {
						int len = strVolume.substring(idx + 1).length();
						if (len <= 10) {
							map.put("volume", strVolume);
						} else {
							map.put("volume", strVolume.substring(0, idx + 10));
						}
					}
				}

			if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}
			String isOpen = this.sysparaService.find(SysParaCode.CLERK_IS_OPEN.getCode()).getValue();
			String platformName = sysparaService.find("platform_name").getValue();

			modelAndView.addObject("isOpen", isOpen);
			modelAndView.addObject("platformName", platformName);
			modelAndView.addObject("session_token", session_token);

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			log.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("succeeded_para", succeeded_para);
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("method_map", Constants.WITHDRAW_METHOD);
		modelAndView.addObject("method",method);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("reviewStartTime", reviewStartTime);
		modelAndView.addObject("reviewEndTime", reviewEndTime);
		return modelAndView;
	}

	/**
	 * 修改用户提现地址
	 */
	@RequestMapping(action + "changeAddress.action")
	public ModelAndView changeAddress(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String id = request.getParameter("id");
		String changeAfterAddress = request.getParameter("changeAfterAddress");
		String succeeded_para = request.getParameter("succeeded_para");
		String safeword = request.getParameter("safeword");
		String method = request.getParameter("method");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		modelAndView.addObject("succeeded_para",succeeded_para);

		try {

			String sessionToken = (String) httpSession.getAttribute("session_token");
			CsrfTokenUtil.removeTokenFromSession(httpSession);

			log.info("---> Withdraw changeAddressToken 存储token: sessionToken:{}, 页面传参session_token:{},用户Id:{}", sessionToken,session_token,id);
			if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
				// 令牌无效，显示错误消息
				throw new BusinessException("操作成功，请勿重复点击");
			}

			synchronized (obj) {
				// 统一处理成功接口
				this.adminWithdrawService.saveAddress(id, safeword, this.getUsername_login(), this.getLoginPartyId(), changeAfterAddress,method);

				ThreadUtils.sleep(300);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			log.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	/**
	 * 处理一个代付
	 */
	@RequestMapping(action + "success.action")
	public ModelAndView success(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String id = request.getParameter("id");
		String safeword = request.getParameter("safeword");
		String withdrawCommissionStr = request.getParameter("withdrawCommission");
		String succeeded_para = request.getParameter("succeeded_para");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		modelAndView.addObject("succeeded_para",succeeded_para);

		double withdrawCommission = 0.0;
		if (StrUtil.isNotBlank(withdrawCommissionStr) && !Objects.equals(withdrawCommissionStr, "null")) {
			withdrawCommission = Double.parseDouble(withdrawCommissionStr);
		}

		try {
			String sessionToken = (String) httpSession.getAttribute("session_token");
			CsrfTokenUtil.removeTokenFromSession(httpSession);

			log.info("---> Withdraw successToken 存储token: sessionToken:{}, 页面传参session_token:{},用户Id:{}", sessionToken,session_token,id);
			if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
				// 令牌无效，显示错误消息
				throw new BusinessException("操作成功，请勿重复点击");
			}
			
			synchronized (obj) {
				// 统一处理成功接口
				this.adminWithdrawService.saveSucceeded(id, safeword, this.getUsername_login(), this.getLoginPartyId(), withdrawCommission);
				ThreadUtils.sleep(300);
			}

			// 发送消息提醒
			String applyUserId = "";
			try {
				Withdraw withdrawEntity = adminWithdrawService.get(id);
				applyUserId = withdrawEntity.getPartyId().toString();
				WithdrawData info = new WithdrawData();
				info.setWithdrawUserId(applyUserId);
				info.setAmount(withdrawEntity.getAmount());
				notificationHelperClient.notifyWithdrawSuccess(info, 3);
			} catch (Exception e) {
				log.error("---> 审核用户:{} 的一笔提现订单:{} 通过后，发送提醒消息失败", applyUserId, id);
			}
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			log.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

	/**
	 * 驳回
	 */
	@RequestMapping(action + "reject.action")
	public ModelAndView reject(HttpServletRequest request) {
		String session_token = request.getParameter("session_token");
		String id = request.getParameter("id");
		String failure_msg = request.getParameter("failure_msg");
		String remarks = request.getParameter("remarks");
		String succeeded_para = request.getParameter("succeeded_para");
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		modelAndView.addObject("succeeded_para",succeeded_para);
		try {

			String sessionToken = (String) httpSession.getAttribute("session_token");
			CsrfTokenUtil.removeTokenFromSession(httpSession);

			log.info("---> Withdraw rejectToken 存储token: sessionToken:{}, 页面传参session_token:{},用户Id:{}", sessionToken,session_token,id);
			if (!CsrfTokenUtil.isTokenValid(sessionToken, session_token)) {
				// 令牌无效，显示错误消息
				throw new BusinessException("操作成功，请勿重复点击");
			}
			
			synchronized (obj) {
				// 统一处理失败接口
				this.adminWithdrawService.saveReject(id, failure_msg, this.getUsername_login(), this.getLoginPartyId(),remarks);
				ThreadUtils.sleep(300);
			}

			String applyUserId = "";
			try {
				Withdraw withdrawEntity = adminWithdrawService.get(id);
				applyUserId = withdrawEntity.getPartyId().toString();
				WithdrawData info = new WithdrawData();
				info.setWithdrawUserId(applyUserId);
				info.setAmount(Arith.add(withdrawEntity.getAmount(), withdrawEntity.getAmount_fee()));
				info.setOrderNo(withdrawEntity.getOrder_no());
				if (StringUtils.isNullOrEmpty(withdrawEntity.getFailure_msg())){
				   info.setReason("--");
				} else {
					info.setReason(withdrawEntity.getFailure_msg());
				}
				notificationHelperClient.notifyWithdrawFail(info);
			} catch (Exception e) {
				log.error("---> 审核用户:{} 的一笔提现订单:{} 通过后，发送提醒消息失败", applyUserId, id);
			}
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			log.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

}
