package project.web.admin;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.wallet.rate.ExchangeRate;
import project.wallet.rate.ExchangeRateService;

/**
 * 货币汇率配置
 *
 */
@RestController
public class AdminExchangeRateController extends PageActionSupport {

	private static final Log logger = LogFactory.getLog(AdminExchangeRateController.class);
	
	@Autowired
	private ExchangeRateService exchangeRateService;

	private final String action = "normal/adminExchangeRateAction!";
	
	/**
	 * 货币汇率配置-列表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		this.checkAndSetPageNo(request.getParameter("pageNo"));
		String name = request.getParameter("name");
		String startTime = request.getParameter("startTime");
		String endTime = request.getParameter("endTime");
		Integer status = request.getParameter("status") == null ? -2 : Integer.parseInt(request.getParameter("status"));
		this.pageSize = 10;
		this.page = this.exchangeRateService.pagedQuery(this.pageNo, this.pageSize, name,startTime, endTime, status);
		ModelAndView model = new ModelAndView();
		model.addObject("pageNo", this.pageNo);
		model.addObject("pageSize", this.pageSize);
		model.addObject("page", this.page);
		model.addObject("message", message);
		model.addObject("error", error);
		model.setViewName("exchangerate_list");
		return model;
	}

	/**
	 * 货币汇率配置-更新
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");
		String id = request.getParameter("id");
		ExchangeRate exchangeRate = this.exchangeRateService.findById(id);

		ModelAndView model = new ModelAndView();
		model.addObject("message", message);
		model.addObject("error", error);
		model.addObject("exchangeRate", exchangeRate);
		model.setViewName("exchangerate_update");
		return model;
	}

	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request, ExchangeRate exchangeRate) {
		if (exchangeRate.getRata() <= 0) {
			throw new BusinessException("汇率错误");
		}

		ModelAndView model = new ModelAndView();
		try {
			String id = request.getParameter("id");
			ExchangeRate bean = exchangeRateService.findById(id);
			bean.setRata(exchangeRate.getRata());
			bean.setSort(exchangeRate.getSort());
			bean.setExcMax(exchangeRate.getExcMax());
			bean.setExcMin(exchangeRate.getExcMin());
			bean.setStatus(exchangeRate.getStatus());
			bean.setIconImg(exchangeRate.getIconImg());
			exchangeRateService.update(bean);
		} catch (BusinessException e) {
			model.addObject("error", e.getMessage());
			model.addObject("exchangeRate", exchangeRate);
			model.setViewName("exchangerate_update");
			return model;
		} catch (Throwable t) {
			logger.error("update error ", t);
			model.addObject("error", "程序错误");
			model.addObject("exchangeRate", exchangeRate);
			model.setViewName("exchangerate_update");
		}
		model.addObject("message", "操作成功");
		model.setViewName("redirect:/" + action + "list.action");
		return model;
	}

}
