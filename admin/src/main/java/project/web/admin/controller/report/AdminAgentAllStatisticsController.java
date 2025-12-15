package project.web.admin.controller.report;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

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
import project.web.admin.service.report.AdminAgentAllStatisticsService;

/**
 * DAPP_代理商充提报表
 */
@RestController
public class AdminAgentAllStatisticsController extends PageActionSupport {

	private Logger logger = LogManager.getLogger(AdminAgentAllStatisticsController.class);

	@Autowired
	private AdminAgentAllStatisticsService adminAgentAllStatisticsService;
	
	private final String action = "normal/adminAgentAllStatisticsAction!";

	/**
	 * 代理商充提报表
	 */
	@RequestMapping(action + "list.action")
	public ModelAndView list(HttpServletRequest request) {
		String pageNo = request.getParameter("pageNo");
		String para_party_id = request.getParameter("para_party_id");
		String all_party_id = request.getParameter("all_party_id");
		String start_time = request.getParameter("start_time");
		String end_time = request.getParameter("end_time");
		String para_username = request.getParameter("para_username");

		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("auto_monitor_statistics_agent_all_list");
		
		try {
			
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

			this.page = this.adminAgentAllStatisticsService.pagedQuery(this.pageNo, this.pageSize, start_time, end_time,
					this.getLoginPartyId(), para_username, Constants.SECURITY_ROLE_AGENT, para_party_id,all_party_id,null);

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
		modelAndView.setViewName("auto_monitor_statistics_agent_all_list");

		try {
			
			this.checkAndSetPageNo(pageNo);

			this.pageSize = 30;
//			this.initTime(start_time, end_time);
			
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

//	private void initTime(String start_time, String end_time) {
////		if(null==start_time &&null==end_time &&null==para_time) para_time = "all";//默认一周
////		if("day".equals(para_time)) {//当天
////			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
////			this.start_time = end_time;
////		}else if("week".equals(para_time)) {//往前推7天
////			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
////			this.start_time = DateUtils.format(DateUtils.addDate(new Date(), -7),DateUtils.DF_yyyyMMdd);
////		}else if("month".equals(para_time)) {//往前推一月
////			this.end_time = DateUtils.format(new Date(),DateUtils.DF_yyyyMMdd);
////			this.start_time = DateUtils.format(DateUtils.addMonth(new Date(), -1),DateUtils.DF_yyyyMMdd);
////		}else if("all".equals(para_time)) {//所有数据
////			this.end_time = null;
////			this.start_time = null;
////		}
//		// start_time,end_time都为空时开始初始化
//		if (null == start_time && null == end_time) {
//			end_time = DateUtils.format(new Date(), DateUtils.DF_yyyyMMdd);
//			start_time = DateUtils.format(DateUtils.getMonthStart(new Date()), DateUtils.DF_yyyyMMdd);
//		}
//	}

}
