package project.web.api.monitor;


import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.web.ResultObject;
import project.monitor.AutoMonitorPoolDataService;
import project.monitor.model.AutoMonitorPoolData;

/**
 * 首页矿池数据
 *
 */
@RestController
@CrossOrigin
public class AutoMonitorPoolDataController {
	
	@Autowired
	private AutoMonitorPoolDataService autoMonitorPoolDataService;

	@RequestMapping("/api/monitor!getAutoMonitorPoolData.action")
	public Object getAutoMonitorPoolData() {
		
		ResultObject resultObject = new ResultObject();
		Map<String, Object> data = new HashMap<String, Object>();

		AutoMonitorPoolData entity = this.autoMonitorPoolDataService.findDefault();
		if (null != entity) {
			data.put("total_output", entity.getTotal_output());
			data.put("tradingSum", entity.getTradingSum());
			data.put("miningName", entity.getMiningName());
			data.put("dayRateMin", entity.getDayRateMin());
			data.put("dayRateMax", entity.getDayRateMax());
			data.put("miningAmountMin", entity.getMiningAmountMin());
			data.put("miningAmountMax", entity.getMiningAmountMax());
		}
		resultObject.setData(data);
		return resultObject;
	}
}
