package project.web.admin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.data.model.Realtime;
import project.exchange.AdminExchangeApplyOrderService;
import project.exchange.ExchangeApplyOrder;
import project.exchange.ExchangeApplyOrderService;
import project.exchange.job.ExchangeApplyOrderHandleJobService;
import project.log.LogService;
import project.party.PartyService;
import project.party.model.Party;
import security.SecUser;
import security.internal.SecUserService;

/**
 * 管理后台 
 * 币币交易订单
 *
 */
@RestController
public class AdminExchangeApplyOrderController extends PageActionSupport {
	
	private static final Log logger = LogFactory.getLog(AdminExchangeApplyOrderController.class);
	
	@Autowired
	protected AdminExchangeApplyOrderService adminExchangeApplyOrderService;
	@Autowired
	protected ExchangeApplyOrderService exchangeApplyOrderService;
	@Autowired
	private ExchangeApplyOrderHandleJobService exchangeApplyOrderHandleJobService;
	@Autowired
	private PartyService partyService;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private LogService logService;
	@Autowired
	private SecUserService secUserService;
	
	private Map<String, Object> session = new HashMap<String, Object>();
	
	private final String action = "normal/adminExchangeApplyOrderAction!";

	/**
	 * 币币交易订单 列表查询
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		
		String session_token = UUID.randomUUID().toString();
		this.session.put("session_token", session_token);
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		this.pageSize = 30;
		String loginPartyId = getLoginPartyId();
		
		String status_para = request.getParameter("status_para");
		String rolename_para = request.getParameter("rolename_para");
		String name_para = request.getParameter("name_para");
		String order_no_para = request.getParameter("order_no_para");
		
		this.page = this.adminExchangeApplyOrderService.pagedQuery(this.pageNo, this.pageSize, status_para,
				rolename_para, loginPartyId, name_para, order_no_para);

		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		model.addObject("status_para", status_para);
		model.addObject("rolename_para", rolename_para);
		model.addObject("name_para", name_para);
		model.addObject("order_no_para", order_no_para);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("exchange_order_list");
		return model;
	}

	/**
	 * 限价成交
	 */
	@RequestMapping(action + "success.action")
	public ModelAndView success(HttpServletRequest request) {
		String message = "";
		String error = "";
		ModelAndView model = new ModelAndView();
		try {
			String session_token = request.getParameter("session_token");
			String safeword = request.getParameter("safeword");
			String order_no = request.getParameter("order_no");
			
			Object object = this.session.get("session_token");
			this.session.remove("session_token");
			if ((object == null) || (StringUtils.isNullOrEmpty(session_token))
					|| (!session_token.equals((String) object))) {
				model.setViewName("exchange_order_list");
			}

			SecUser sec = this.secUserService.findUserByLoginName(this.getUsername_login());
			String sysSafeword = sec.getSafeword();

			String safeword_md5 = passwordEncoder.encodePassword(safeword, getUsername_login());
			if (!safeword_md5.equals(sysSafeword)) {
				throw new BusinessException("资金密码错误");
			}

			ExchangeApplyOrder order = exchangeApplyOrderService.findByOrderNo(order_no);
			if (order == null) {
				throw new BusinessException("委托单不存在或请稍后再试");
			}
			if (ExchangeApplyOrder.STATE_CREATED.equals(order.getState()))
				throw new BusinessException("委托已完成无法操作");
			if (!"limit".equals(order.getOrder_price_type())) {
				throw new BusinessException("委托并非限价单，无法限价成交");
			}

			Realtime realtime = new Realtime();
			realtime.setClose(order.getPrice());
			exchangeApplyOrderHandleJobService.handle(order, realtime);

			Party party = partyService.cachePartyBy(order.getPartyId().toString(), true);
			project.log.Log log = new project.log.Log();
			log.setCategory(Constants.LOG_CATEGORY_OPERATION);
			log.setExtra(order.getOrder_no());
			log.setOperator(getUsername_login());
			log.setUsername(party.getUsername());
			log.setPartyId(order.getPartyId());
			log.setLog("币币限价单，操作限价成交。订单号[" + order.getOrder_no() + "]。");
			logService.saveSync(log);
			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}

	/**
	 * 平仓或撤单
	 */
	@RequestMapping(action + "close.action")
	public ModelAndView close(HttpServletRequest request) {
		String message = "";
		String error = "";
		try {
			String order_no = request.getParameter("order_no");
			ExchangeApplyOrder order = exchangeApplyOrderService.findByOrderNo(order_no);
			if (ExchangeApplyOrder.STATE_CREATED.equals(order.getState())) {
				throw new BusinessException("委托已完成无法撤销");
			}
			
			exchangeApplyOrderService.saveCancel(order.getPartyId().toString(), order_no);

			message = "操作成功";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}

}
