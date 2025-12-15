package project.web.api.controller;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.AssetService;

/**
 * 资产
 */
@RestController
@CrossOrigin
public class AssetsController extends BaseAction {

	private Logger logger = LogManager.getLogger(AssetsController.class);
	
	@Autowired
	private AssetService assetService;
	@Autowired
	private KycService kycService;

	private final String action = "/api/assets!";

	/**
	 * 总账户资产 所有币种，订单资产转换到Usdt余额
	 */
	@RequestMapping(action + "getAll.action")
	public Object getAll() throws IOException {
		
		Map<String, Object> data = new HashMap<String, Object>();

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {
			
			DecimalFormat df2 = new DecimalFormat("#.##");
			// 向下取整
			df2.setRoundingMode(RoundingMode.FLOOR);

			String partyId = this.getLoginPartyId();
			if ("".equals(partyId) || null == partyId) {

				data.put("total", df2.format(0));
				data.put("money_wallet", df2.format(0));
				data.put("money_coin", df2.format(0));
				data.put("money_all_coin", df2.format(0));
				data.put("money_miner", df2.format(0));
				data.put("money_finance", df2.format(0));
				data.put("money_contract", df2.format(0));
				data.put("money_contract_deposit", df2.format(0));
				data.put("money_contract_profit", df2.format(0));
				data.put("money_futures", df2.format(0));
				data.put("money_futures_profit", df2.format(0));
			} else {
				data = this.assetService.getMoneyAll(partyId);
			}
				
			Kyc kyc = this.kycService.get(partyId);
			data.put("status", kyc.getStatus());
			
			resultObject.setData(data);
			
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

}
