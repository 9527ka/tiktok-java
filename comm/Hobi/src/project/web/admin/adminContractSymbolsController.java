package project.web.admin;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import kernel.exception.BusinessException;
import kernel.util.PropertiesUtil;
import kernel.web.PageActionSupport;
import project.hobi.AdminContractSymbolsService;

@RestController
public class adminContractSymbolsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(adminContractSymbolsController.class);
	
	@Autowired
	private AdminContractSymbolsService adminContractSymbolsService;
	
	private final String action = "normal/adminContractSymbolsAction!";
	
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		
		// 交易对中的报价币种
		String quote_currency = request.getParameter("quote_currency");
		// 交易对中的基础币种
		String base_currency = request.getParameter("base_currency");

		String basePath = PropertiesUtil.getProperty("admin_url");
		basePath = getPath(request);
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		
		this.pageSize = 7;
		this.page = this.adminContractSymbolsService.pagedQuery(this.pageNo, pageSize, quote_currency, base_currency);
		List<String> quoteList = this.adminContractSymbolsService.getQuoteList();
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("pageNo", this.pageNo);
		modelAndView.addObject("pageSize", this.pageSize);
		modelAndView.addObject("page", this.page);
		modelAndView.addObject("basePath", basePath);
		modelAndView.addObject("quoteList", quoteList);
		modelAndView.setViewName("contract_manage_add_symbols_list");
		return modelAndView;
	}
	
	private String getPath(HttpServletRequest request) {
		return String.format("%s://%s:%s%s", request.getScheme(),request.getServerName()
				,(request.getServerPort() == 80?"":request.getServerPort()),request.getContextPath());
	}

	@RequestMapping(action + "reload.action")
	public ModelAndView reload() {
		String message = "";
		String error = "";
		try {
			adminContractSymbolsService.saveReload();
			message = "数据同步完成";
		} catch (BusinessException e) {
			error = e.getMessage();
		} catch (Exception e) {
			logger.error("error ", e);
			error = "程序错误";
		}
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		modelAndView.setViewName("contract_manage_add_symbols_list");
		return modelAndView;
	}

}
