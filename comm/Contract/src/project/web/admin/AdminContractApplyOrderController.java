package project.web.admin;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.contract.AdminContractApplyOrderService;
import project.contract.ContractApplyOrder;
import project.contract.ContractApplyOrderService;

/**
 * 永续合约委托
 */
@RestController
public class AdminContractApplyOrderController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminContractApplyOrderController.class);

	@Autowired
	private AdminContractApplyOrderService adminContractApplyOrderService;
	@Autowired
	private ContractApplyOrderService contractApplyOrderService;

	private final String action = "normal/adminContractApplyOrderAction!";

	/**
	 * 获取 永续合约委托列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String order_no_para = request.getParameter("order_no_para");
		String name_para = request.getParameter("name_para");
		String rolename_para = request.getParameter("rolename_para");
		String status_para = request.getParameter("status_para");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("contract_apply_order_list");

		try {

			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;

			String loginPartyId = this.getLoginPartyId();

			this.page = this.adminContractApplyOrderService.pagedQuery(this.pageNo, this.pageSize, status_para,
					rolename_para, loginPartyId, name_para, order_no_para);

			List<Map> list = this.page.getElements();
			for (int i = 0; i < list.size(); i++) {
				Map map = list.get(i);				
				if (null == map.get("rolename")) {
					map.put("roleNameDesc", "");
				} else {
					String roleName = map.get("rolename").toString();
					map.put("roleNameDesc", Constants.ROLE_MAP.containsKey(roleName) ? Constants.ROLE_MAP.get(roleName) : roleName);
				}
			}

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
		modelAndView.addObject("order_no_para", order_no_para);
		modelAndView.addObject("name_para", name_para);
		modelAndView.addObject("rolename_para", rolename_para);
		modelAndView.addObject("status_para", status_para);
		return modelAndView;
	}

	/**
	 * 平仓或撤单
	 */
	@RequestMapping(action + "close.action")
	public ModelAndView close(HttpServletRequest request) {
		String order_no = request.getParameter("order_no");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("redirect:/" + action + "list.action");
		
		try {
			
			ContractApplyOrder order = this.contractApplyOrderService.findByOrderNo(order_no);
			if (order != null) {
				this.contractApplyOrderService.saveCancel(order.getPartyId().toString(), order_no);
			}
			
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		modelAndView.addObject("message", "操作成功");
		return modelAndView;
	}

}
