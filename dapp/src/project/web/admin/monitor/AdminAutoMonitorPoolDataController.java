package project.web.admin.monitor;

import java.math.BigDecimal;
import java.text.MessageFormat;

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
import project.monitor.AdminAutoMonitorPoolDataService;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.model.AutoMonitorPoolData;

/**
 * 矿池产出数据
 */
@RestController
public class AdminAutoMonitorPoolDataController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAutoMonitorPoolDataController.class);

	@Autowired
	private AdminAutoMonitorPoolDataService adminAutoMonitorPoolDataService;
	@Autowired
	private AutoMonitorPoolDataService autoMonitorPoolDataService;
	
	private final String action = "normal/adminAutoMonitorPoolDataAction!";

//	/**
//	 * 获取矿池产出数据列表
//	 */
//	@RequestMapping(action + "list.action")
//	public ModelAndView list(HttpServletRequest request) {
//
//		ModelAndView modelAndView = new ModelAndView();
//		modelAndView.setViewName("auto_monitor_pool_data_list");
//
//		try {
//			
//			this.pageSize = 30;
//			this.page = this.adminAutoMonitorPoolDataService.pagedQuery(this.pageNo, this.pageSize);
//
//		} catch (BusinessException e) {
//			this.error = e.getMessage();
//			modelAndView.addObject("error", this.error);
//			return modelAndView;
//		} catch (Throwable t) {
//			logger.error(" error ", t);
//			this.error = ("[ERROR] " + t.getMessage());
//			modelAndView.addObject("error", this.error);
//			return modelAndView;
//		}
//
//		modelAndView.addObject("pageNo", this.pageNo);
//		modelAndView.addObject("pageSize", this.pageSize);
//		modelAndView.addObject("page", this.page);
//		return modelAndView;
//	}

//	/**
//	 * 新增矿池产出数据 页面
//	 */
//	@RequestMapping(action + "toAdd.action")
//	public String toAdd() {
//		return "add";
//	}

	/**
	 * 修改 矿池产出数据 页面
	 */
	@RequestMapping(action + "toUpdate.action")
	public ModelAndView toUpdate(HttpServletRequest request) {
		String message = request.getParameter("message");
		String error = request.getParameter("error");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_pool_data_update");

		try {

			AutoMonitorPoolData entity = this.autoMonitorPoolDataService.findDefault();

			modelAndView.addObject("rate", entity.getRate());
			modelAndView.addObject("total_output", entity.getTotal_output());
			modelAndView.addObject("user_revenue", new BigDecimal(String.valueOf(entity.getUser_revenue())).toPlainString());
			modelAndView.addObject("verifier", entity.getVerifier());
			modelAndView.addObject("notice_logs", entity.getNotice_logs());
			
			modelAndView.addObject("miningName", entity.getMiningName());
			modelAndView.addObject("tradingSum", entity.getTradingSum());
			modelAndView.addObject("dayRateMin", entity.getDayRateMin());
			modelAndView.addObject("dayRateMax", entity.getDayRateMax());
			modelAndView.addObject("miningAmountMin", entity.getMiningAmountMin());
			modelAndView.addObject("miningAmountMax", entity.getMiningAmountMax());
			modelAndView.addObject("node_num", entity.getNode_num());
			modelAndView.addObject("mining_total", entity.getMining_total());

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			return modelAndView;
		}

		modelAndView.addObject("message", message);
		modelAndView.addObject("error", error);
		return modelAndView;
	}

	/**
	 * 修改 矿池产出数据
	 */
	@RequestMapping(action + "update.action")
	public ModelAndView update(HttpServletRequest request) {
		String total_output = request.getParameter("total_output");
		String user_revenue = request.getParameter("user_revenue");
		String verifier = request.getParameter("verifier");
		String rate = request.getParameter("rate");
		String notice_logs = request.getParameter("notice_logs");
		
		String miningName = request.getParameter("miningName");
		String tradingSum_temp = request.getParameter("tradingSum");
		String dayRateMin_temp = request.getParameter("dayRateMin");
		String dayRateMax_temp = request.getParameter("dayRateMax");
		String miningAmountMin_temp = request.getParameter("miningAmountMin");
		String miningAmountMax_temp = request.getParameter("miningAmountMax");
		String node_num_temp = request.getParameter("node_num");
		String mining_total_temp = request.getParameter("mining_total");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject("total_output", total_output);
		modelAndView.addObject("user_revenue", user_revenue);
		modelAndView.addObject("verifier", verifier);
		modelAndView.addObject("rate", rate);
		modelAndView.addObject("notice_logs", notice_logs);
		
		modelAndView.addObject("miningName", miningName);
		modelAndView.addObject("tradingSum", tradingSum_temp);
		modelAndView.addObject("dayRateMin", dayRateMin_temp);
		modelAndView.addObject("dayRateMax", dayRateMax_temp);
		modelAndView.addObject("miningAmountMin", miningAmountMin_temp);
		modelAndView.addObject("miningAmountMax", miningAmountMax_temp);
		modelAndView.addObject("node_num", node_num_temp);
		modelAndView.addObject("mining_total", mining_total_temp);

		try {
			
			String error = this.verification(total_output, user_revenue, verifier, rate, 
					dayRateMin_temp, dayRateMax_temp, miningAmountMin_temp, miningAmountMax_temp, node_num_temp,mining_total_temp,tradingSum_temp, miningName);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}			

			double total_output_double = Double.valueOf(request.getParameter("total_output"));
			double user_revenue_double = Double.valueOf(request.getParameter("user_revenue"));
			double verifier_double = Double.valueOf(request.getParameter("verifier"));
			double rate_double = Double.valueOf(request.getParameter("rate"));
			
			double tradingSum = Double.valueOf(tradingSum_temp);
			double dayRateMin = Double.valueOf(dayRateMin_temp);
			double dayRateMax = Double.valueOf(dayRateMax_temp);
			double miningAmountMin = Double.valueOf(miningAmountMin_temp);
			double miningAmountMax = Double.valueOf(miningAmountMax_temp);
			double node_num = Double.valueOf(node_num_temp);
			double mining_total = Double.valueOf(mining_total_temp);
			
			AutoMonitorPoolData entity = autoMonitorPoolDataService.findDefault();

			String log = MessageFormat.format(
					"管理员修改矿池产出数据配置,原total_output:{0},原user_revenue:{1}," + "原verifier:{2},原rate:{3},原notice_logs{4}",
					entity.getTotal_output(), entity.getUser_revenue(), entity.getVerifier(), entity.getRate(),
					entity.getNotice_logs());

			entity.setTotal_output(total_output_double);
			entity.setUser_revenue(user_revenue_double);
			entity.setVerifier(verifier_double);
			entity.setRate(rate_double);
			entity.setNotice_logs(notice_logs);
			entity.setMiningName(miningName);
			entity.setTradingSum(tradingSum);
			entity.setDayRateMin(dayRateMin);
			entity.setDayRateMax(dayRateMax);
			entity.setMiningAmountMin(miningAmountMin);
			entity.setMiningAmountMax(miningAmountMax);
			entity.setNode_num(node_num);
			entity.setMining_total(mining_total);

			log += MessageFormat.format(
					",新total_output:{0},新user_revenue:{1}," + "新verifier:{2},新rate:{3},新notice_logs{4}",
					entity.getTotal_output(), entity.getUser_revenue(), entity.getVerifier(), entity.getRate(),
					entity.getNotice_logs());
			
			this.adminAutoMonitorPoolDataService.update(entity, this.getUsername_login(), this.getIp(), log);
						
		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			modelAndView.setViewName("auto_monitor_pool_data_update");
			return modelAndView;
		} catch (Throwable t) {
			logger.error("update error ", t);
			modelAndView.addObject("error", "程序错误");
			modelAndView.setViewName("auto_monitor_pool_data_update");
			return modelAndView;
		}
		
		modelAndView.addObject("message", "操作成功");
		modelAndView.setViewName("redirect:/" + action + "toUpdate.action");
		return modelAndView;
	}

	protected String verification(String total_output, String user_revenue, String verifier, String rate,
			String dayRateMin_temp, String dayRateMax_temp, String miningAmountMin_temp, 
			String miningAmountMax_temp,String node_num_temp, String mining_total_temp, String tradingSum_temp, String miningName) {

		if (StringUtils.isNullOrEmpty(total_output)) {
			return "总产量必填";
		}
		if (!StringUtils.isDouble(total_output)) {
			return "总产量输入错误，请输入浮点数";
		}
		if (Double.valueOf(total_output).doubleValue() < 0) {
			return "总产量不能小于0";
		}

		if (StringUtils.isNullOrEmpty(verifier)) {
			return "参与者必填";
		}
		if (!StringUtils.isDouble(verifier)) {
			return "参与者输入错误，请输入浮点数";
		}
		if (Double.valueOf(verifier).doubleValue() < 0) {
			return "参与者不能小于0";
		}

		if (StringUtils.isNullOrEmpty(user_revenue)) {
			return "用户收益必填";
		}
		if (!StringUtils.isDouble(user_revenue)) {
			return "用户收益输入错误，请输入浮点数";
		}
		if (Double.valueOf(user_revenue).doubleValue() < 0) {
			return "用户收益不能小于0";
		}

		if (StringUtils.isNullOrEmpty(rate)) {
			return "参与者增加倍率必填";
		}
		if (!StringUtils.isDouble(rate)) {
			return "参与者增加倍率输入错误，请输入浮点数";
		}
		if (Double.valueOf(rate).doubleValue() < 0) {
			return "参与者增加倍率不能小于0";
		}
		if (Double.valueOf(node_num_temp).doubleValue() < 0) {
			return "节点数量不能小于0";
		}
		if (StringUtils.isNullOrEmpty(miningName)) {
			return "挖矿项目名称必填";
		}
		if (!StringUtils.isDouble(mining_total_temp) || Double.valueOf(mining_total_temp).doubleValue() < 0) {
			return "请输入正确的总流动性挖矿合约资金";
		}
		if (!StringUtils.isDouble(tradingSum_temp) || Double.valueOf(tradingSum_temp).doubleValue() < 0) {
			return "请输入正确的总交易量";
		}
		if (!StringUtils.isDouble(dayRateMin_temp) || Double.valueOf(dayRateMin_temp).doubleValue() < 0) {
			return "请输入正确的日收益率下限";
		}
		if (!StringUtils.isDouble(dayRateMax_temp) || Double.valueOf(dayRateMax_temp).doubleValue() < 0) {
			return "请输入正确的日收益率上限";
		}
		if (!StringUtils.isDouble(miningAmountMin_temp) || Double.valueOf(miningAmountMin_temp).doubleValue() < 0) {
			return "请输入正确的挖矿金额下限";
		}
		if (!StringUtils.isDouble(miningAmountMax_temp) || Double.valueOf(miningAmountMax_temp).doubleValue() < 0) {
			return "请输入正确的挖矿金额上限";
		}
		
		return null;
	}

}
