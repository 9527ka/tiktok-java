package project.web.api;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.web.ResultObject;
import project.Constants;
import project.finance.Finance;
import project.finance.FinanceService;

/**
 * 理财产品
 *
 */
@RestController
@CrossOrigin
public class FinanceController {

	private Logger logger = LogManager.getLogger(FinanceController.class);
	
	@Autowired
	protected FinanceService financeService;
	
	private final String action = "/api/finance!";

	/**
	 * 理财产品列表
	 */
	@RequestMapping(action + "list.action")
	public Object list() {
		
		ResultObject resultObject = new ResultObject();

		try {
			
			List<Finance> data = this.financeService.findAllState_1();
			resultObject.setData(bulidData(data));
			resultObject.setCode("0");
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	/**
	 * 理财产品详情
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) {
		String id = request.getParameter("id");
		
		ResultObject resultObject = new ResultObject();
		
		try {
			
			Finance data = this.financeService.findById(id);
			if (!StringUtils.isNullOrEmpty(data.getImg())) {
				String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + data.getImg();
				data.setImg(path);
			}
			
			resultObject.setData(data);
			resultObject.setCode("0");
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Throwable t) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", t);
		}

		return resultObject;
	}

	protected List<Map<String, Object>> bulidData(List<Finance> finances) throws ParseException {

		List<Map<String, Object>> result_finances = new ArrayList<Map<String, Object>>();

		for (int i = 0; i < finances.size(); i++) {
			
			Map<String, Object> map = new HashMap<String, Object>();
			
			Finance finance = finances.get(i);
			
			String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + finance.getImg();
			map.put("img", path);
			map.put("id", finance.getId());

			map.put("name", finance.getName());
			map.put("name_en", finance.getName_en());
			map.put("name_cn", finance.getName_cn());
			map.put("name_kn", finance.getName_kn());
			map.put("name_jn", finance.getName_jn());
			map.put("cycle", finance.getCycle());
			map.put("daily_rate", finance.getDaily_rate() + " ~ " + finance.getDaily_rate_max());
			map.put("daily_rate_max", finance.getDaily_rate_max());
			map.put("today_rate", finance.getToday_rate());
			map.put("default_ratio", finance.getDefault_ratio());
			map.put("investment_min", finance.getInvestment_min());
			map.put("investment_max", finance.getInvestment_max());
			map.put("state", finance.getState());

			result_finances.add(map);
		}

		return result_finances;
	}

	private Map<String, Object> bulidData(Finance finance) throws ParseException {

		Map<String, Object> map = new HashMap<String, Object>();
		
		String path = Constants.WEB_URL + "/public/showimg!showImg.action?imagePath=" + finance.getImg();
		map.put("img", path);
		map.put("id", finance.getId());

		map.put("name", finance.getName());
		map.put("name_en", finance.getName_en());
		map.put("name_cn", finance.getName_cn());
		map.put("name_kn", finance.getName_kn());
		map.put("name_jn", finance.getName_jn());
		map.put("cycle", finance.getCycle());
		map.put("daily_rate", finance.getDaily_rate());
		map.put("daily_rate_max", finance.getDaily_rate_max());
		map.put("today_rate", finance.getToday_rate());
		map.put("default_ratio", finance.getDefault_ratio());
		map.put("investment_min", finance.getInvestment_min());
		map.put("investment_max", finance.getInvestment_max());
		map.put("state", finance.getState());

		return map;
	}
	
}
