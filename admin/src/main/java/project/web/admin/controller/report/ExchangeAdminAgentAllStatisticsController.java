package project.web.admin.controller.report;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import kernel.util.Arith;
import org.apache.curator.shaded.com.google.common.collect.Maps;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import kernel.exception.BusinessException;
import kernel.util.DateUtils;
import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import kernel.web.PageActionSupport;
import project.Constants;
import project.mall.utils.PlatformNameEnum;
import project.syspara.SysParaCode;
import project.syspara.SysparaService;
import project.web.admin.service.report.AdminAgentAllStatisticsService;

/**
 * 交易所_代理商报表
 */
@RestController
public class ExchangeAdminAgentAllStatisticsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(ExchangeAdminAgentAllStatisticsController.class);

	@Autowired
	private AdminAgentAllStatisticsService adminAgentAllStatisticsService;
	@Autowired
	protected SysparaService sysparaService;
	private final String action = "normal/exchangeAdminAgentAllStatisticsAction!";

	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String para_party_id = request.getParameter("para_party_id");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_username = request.getParameter("para_username");
		String all_party_id = request.getParameter("all_party_id");

		ModelAndView modelAndView = new ModelAndView();
		try {

			String platformName = sysparaService.find("platform_name").getValue();
			if(PlatformNameEnum.WORTEN.getDescription().equals(platformName)){
				modelAndView.setViewName("statistics_agent_worten_all_list");
			} else {
				modelAndView.setViewName("statistics_agent_all_list");
			}
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
//			this.initTime(start_time, end_time);
			
			// start_time,end_time都为空时开始初始化
			if (null == start_time && null == end_time) {
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
			}

			// 如果知道某个代理商的id，进入后也不可查
			if (!StringUtils.isNotEmpty(para_party_id)) {
				para_party_id = this.getLoginPartyId();
			}
			String clerkOpen = this.sysparaService.find(SysParaCode.CLERK_IS_OPEN.getCode()).getValue();
			this.page = this.adminAgentAllStatisticsService.pagedQuery(this.pageNo, this.pageSize, start_time, end_time,
					this.getLoginPartyId(), para_username, Constants.SECURITY_ROLE_AGENT, para_party_id,all_party_id,platformName);

			double withdrawUsdt = 0d;
			double withdrawUsdc = 0d;
			double withdrawEth = 0d;
			double withdrawBtc = 0d;

			double rechargUsdt = 0d;
			double rechargeUsdc = 0d;
			double rechargeEth = 0d;
			double rechargeBtc = 0d;

			double totalUsdt = 0d;
			double totalUsdc = 0d;
			double totalBtc = 0d;
			double totalEth = 0d;

			List<Map> list = page.getElements();
			HashMap<Object, Object> sumData = Maps.newHashMap();
			for (int i = 0; i < list.size(); i++) {
				Map map=list.get(i);
				Double recharge_usdt = (Double) map.get("recharge_usdt");
				Double withdraw = (Double) map.get("withdraw");
				double difference = Arith.sub(recharge_usdt, withdraw);

				Double rechargeCommission = (Double) map.get("rechargeCommission");
				Double withdrawCommission = (Double) map.get("withdrawCommission");
				map.put("commission", Arith.sub(rechargeCommission,withdrawCommission));
				map.put("difference",difference);

				if(PlatformNameEnum.WORTEN.getDescription().equals(platformName)){
					rechargUsdt = Arith.add((Double) map.get("recharge_usdt"),rechargUsdt);
					rechargeUsdc = Arith.add((Double) map.get("recharge_usdc"),rechargeUsdc);
					rechargeEth = Arith.add((Double) map.get("recharge_eth"),rechargeEth);
					rechargeBtc = Arith.add((Double) map.get("recharge_btc"),rechargeBtc);

					withdrawUsdt = Arith.add((Double) map.get("withdraw_usdt"),withdrawUsdt);
					withdrawUsdc = Arith.add((Double) map.get("withdraw_usdc"),withdrawUsdc);
					withdrawEth = Arith.add((Double) map.get("withdraw_eth"),withdrawEth);
					withdrawBtc = Arith.add((Double) map.get("withdraw_btc"),withdrawBtc);

					totalUsdt = Arith.add((Double) map.get("totalUsdt"),totalUsdt);
					totalUsdc = Arith.add((Double) map.get("totalUsdc"),totalUsdc);
					totalBtc = Arith.add((Double) map.get("totalBtc"),totalBtc);
					totalEth = Arith.add((Double) map.get("totalEth"),totalEth);
				}

			}
			sumData.put("rechargUsdt",rechargUsdt);
			sumData.put("rechargeUsdc",rechargeUsdc);
			sumData.put("rechargeEth",rechargeEth);
			sumData.put("rechargeBtc",rechargeBtc);
			sumData.put("withdrawUsdt",withdrawUsdt);
			sumData.put("withdrawUsdc",withdrawUsdc);
			sumData.put("withdrawEth",withdrawEth);
			sumData.put("withdrawBtc",withdrawBtc);
			sumData.put("totalUsdt",totalUsdt);
			sumData.put("totalUsdc",totalUsdc);
			sumData.put("totalEth",totalEth);
			sumData.put("totalBtc",totalBtc);
			modelAndView.addObject("isOpen", clerkOpen);
			modelAndView.addObject("sumData", sumData);
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
		modelAndView.addObject("para_party_id", para_party_id);
		modelAndView.addObject("start_time", start_time);
		modelAndView.addObject("end_time", end_time);
		modelAndView.addObject("para_username", para_username);

		return modelAndView;
	}

	/**
	 * 导出数据到文件
	 */
	@RequestMapping(action + "exportData.action")
	public ModelAndView exportData(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_username = request.getParameter("para_username");
		String para_party_id = request.getParameter("para_party_id");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("statistics_agent_all_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
			
			// start_time,end_time都为空时开始初始化
			if (null == start_time && null == end_time) {
				end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
				start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
			}

			String error = this.adminAgentAllStatisticsService.loadExportData(this.getResponse(), this.pageSize,
					start_time, end_time, this.getLoginPartyId(), para_username, Constants.SECURITY_ROLE_AGENT,
					para_party_id);
			if (!StringUtils.isNullOrEmpty(error)) {
				throw new BusinessException(error);
			}

		} catch (BusinessException e) {
			modelAndView.addObject("error", e.getMessage());
			return modelAndView;
		} catch (IOException e) {
			logger.error("export fail:{}", e);
			modelAndView.addObject("error", "程序错误,导出异常");
			return modelAndView;
		} catch (Throwable t) {
			logger.error(" error ", t);
			modelAndView.addObject("error", "[ERROR] " + t.getMessage());
			return modelAndView;
		}

		return modelAndView;
	}

	/**
	 * 获取推荐网络
	 */
	@RequestMapping(action + "getReconNumNet.action")
	public String getReconNumNet(HttpServletRequest request) {
		String net_party_id = request.getParameter("net_party_id");
		
		Map<String, Object> resultMap = new HashMap<String, Object>();
		
		try {
			
			resultMap.put("code", 200);
			resultMap.put("user_reco_net", this.adminAgentAllStatisticsService.getRecoNumNetList(net_party_id));
			
		} catch (BusinessException e) {
			resultMap.put("code", 500);
			resultMap.put("message", e.getMessage());
		} catch (Throwable t) {
			logger.error(" error ", t);
			resultMap.put("code", 500);
			resultMap.put("message", "程序错误");
		}

		return JsonUtils.getJsonString(resultMap);
	}

}
