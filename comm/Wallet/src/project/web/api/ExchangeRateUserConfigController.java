package project.web.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mysql.cj.util.StringUtils;

import kernel.exception.BusinessException;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.wallet.rate.ExchangeRate;
import project.wallet.rate.UserRateConfigService;

/**
 * API 计价方式
 *
 */
@RestController
@CrossOrigin
public class ExchangeRateUserConfigController extends BaseAction {

	private static Log logger = LogFactory.getLog(ExchangeRateUserConfigController.class);

	@Autowired
	private UserRateConfigService userRateConfigService;
	
	private final String action = "api/exchangerateuserconfig!";

	/**
	 * 设置计价方式
	 */
	@RequestMapping(action + "userSetRate.action")
	public Object userSetRate(HttpServletRequest request) throws IOException {

		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		String partyId = this.getLoginPartyId();
		try {
			String rateId = request.getParameter("rateId");
			if (StringUtils.isNullOrEmpty(rateId)) {
				throw new BusinessException("rateId is null");
			}
			this.userRateConfigService.update(rateId, partyId);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

	/**
	 * 获取 汇率
	 */
	@RequestMapping(action + "get.action")
	public Object get() throws IOException {
		ResultObject resultObject = new ResultObject();

		String partyId = this.getLoginPartyId();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			ExchangeRate exchangeRate = this.userRateConfigService.findUserConfig(partyId);
			data.put("currency", exchangeRate.getCurrency());
			data.put("name", exchangeRate.getName());
			data.put("currency_symbol", exchangeRate.getCurrency_symbol());
			data.put("rate", exchangeRate.getRata());
			resultObject.setData(data);
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}
	
}
