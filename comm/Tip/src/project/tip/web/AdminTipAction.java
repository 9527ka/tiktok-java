package project.tip.web;

import java.util.HashMap;
import java.util.Map;

import kernel.util.JsonUtils;
import kernel.util.StringUtils;
import project.tip.TipService;
import security.web.BaseSecurityAction;

public class AdminTipAction extends BaseSecurityAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3710440340895853478L;

	private TipService tipService;

	private String result_make;

	private Long time_stamp;

	private String model;

	public String getTips() {
		if (!StringUtils.isNullOrEmpty(getLoginPartyId())) {
			return "result_make";
		}
		Map<String, Object> result = new HashMap<String, Object>();
		result.put("tipList", tipService.getCacheSumTips(this.getUsername_login()));

		this.result_make = JsonUtils.getJsonString(result);
		return "result_make";
	}

	public String getNewTips() {
		if (!StringUtils.isNullOrEmpty(getLoginPartyId())) {
			return "result_make";
		}
		Map<String, Object> result = new HashMap<String, Object>();
		if (!StringUtils.isNullOrEmpty(model)) {
			result.put("tipList", tipService.cacheNewTipsByModel(this.getUsername_login(), time_stamp, model));
		} else {
			result.put("tipList", tipService.getCacheNewTips(this.getUsername_login(), time_stamp));
		}

		this.result_make = JsonUtils.getJsonString(result);
		return "result_make";
	}

	public String getResult_make() {
		return result_make;
	}

	public void setTipService(TipService tipService) {
		this.tipService = tipService;
	}

	public void setTime_stamp(Long time_stamp) {
		this.time_stamp = time_stamp;
	}

	public void setModel(String model) {
		this.model = model;
	}

}
