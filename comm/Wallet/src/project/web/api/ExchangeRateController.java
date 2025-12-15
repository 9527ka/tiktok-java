package project.web.api;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.Arith;
import kernel.util.PageInfo;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.web.ResultObject;
import project.Constants;
import project.invest.InvestRedisKeys;
import project.invest.project.model.ExchangeOrder;
import project.invest.project.model.InvestRebate;
import project.invest.project.model.Project;
import project.invest.project.model.ProjectLang;
import project.tip.TipConstants;
import project.tip.TipService;
import project.user.kyc.Kyc;
import project.user.kyc.KycService;
import project.wallet.Wallet;
import project.wallet.WalletService;
import project.wallet.rate.ExchangeRate;
import project.wallet.rate.ExchangeRateService;
import project.wallet.rate.PaymentMethod;
import util.DateUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

@RestController
@CrossOrigin
public class ExchangeRateController extends BaseAction {

	private static Log logger = LogFactory.getLog(ExchangeRateController.class);

	@Resource
	private ExchangeRateService exchangeRateService;

	@Resource
	private WalletService walletService;

	@Resource
	protected KycService kycService;

	@Resource
	private TipService tipService;

	private final String action = "api/exchangerate!";

	@PostMapping(action+"list.action")
	public Object list(HttpServletRequest request) throws IOException {

		ResultObject resultObject = new ResultObject();
		PageInfo pageInfo = getPageInfo(request);

		JSONArray jsonArray = new JSONArray();
		for(ExchangeRate er : exchangeRateService.listExchangeRates(pageInfo.getPageNum(),pageInfo.getPageSize())){
			JSONObject o = new JSONObject();
			o.put("id", er.getId().toString());
			o.put("currency", er.getCurrency());
			o.put("currency_symbol", er.getCurrency_symbol());
			o.put("iconImg", er.getIconImg());
			o.put("rata", er.getRata());
			o.put("excMin", er.getExcMin());
			o.put("excMax", er.getExcMax());
			jsonArray.add(o);
		}

		JSONObject object = new JSONObject();
		object.put("pageInfo", pageInfo);
		object.put("pageList", jsonArray);
		resultObject.setData(object);
		return resultObject;
	}

	/**
	 * 获取 汇率
	 */
	@PostMapping(action+"get.action")
	public Object get(HttpServletRequest request) throws IOException {
		ResultObject resultObject = new ResultObject();

		String partyId = this.getLoginPartyId();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String id = request.getParameter("id");
			ExchangeRate exchangeRate = exchangeRateService.findById(id);
			Wallet wallet = walletService.saveWalletByPartyId(partyId);
			double balance = wallet.getMoney();
			data.put("currency", exchangeRate.getCurrency());
			data.put("currency_symbol", exchangeRate.getCurrency_symbol());
			data.put("rata", exchangeRate.getRata());
			data.put("excMin", exchangeRate.getExcMin());
			data.put("excMax", exchangeRate.getExcMax());
			data.put("balance", balance);

			data.put("payType", "-1");
			data.put("bankName", "");
			data.put("bankAccount", "");

			PaymentMethod pm = exchangeRateService.getDefaultPaymentMethod(partyId);
			if(pm!=null){
				data.put("payType", pm.getPayType());
				data.put("bankName", pm.getBankName());
				data.put("bankAccount", pm.getBankAccount());
			}
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

	/**
	 * 获取 汇率
	 */
	@PostMapping(action+"exchange.action")
	public Object exchange(HttpServletRequest request) throws IOException {
		ResultObject resultObject = new ResultObject();

		String partyId = this.getLoginPartyId();
		Map<String, Object> data = new HashMap<String, Object>();
		try {
			String id = request.getParameter("id");

			double usdt = Double.parseDouble(request.getParameter("usdt"));
			usdt = Arith.roundDown(usdt,2);

			if(usdt<=0||usdt>99999999){
				resultObject.setCode("1");
				resultObject.setMsg("输入金额高于最大出售");
				return resultObject;
			}

			String bankName = request.getParameter("bankName");

			if(StringUtils.isEmptyString(bankName)){
				resultObject.setCode("1");
				resultObject.setMsg("开户行不能为空");
				return resultObject;
			}

			String bankAccount = request.getParameter("bankAccount");

			if(StringUtils.isEmptyString(bankAccount)){
				resultObject.setCode("1");
				resultObject.setMsg("卡号不能为空");
				return resultObject;
			}
			ExchangeRate exchangeRate = exchangeRateService.findById(id);


			double cny = Arith.mul(usdt,exchangeRate.getRata());

			if(cny>exchangeRate.getExcMax()){
				resultObject.setCode("1");
				resultObject.setMsg("输入金额高于最大出售");
				return resultObject;
			}

			if(cny<exchangeRate.getExcMin()){
				resultObject.setCode("1");
				resultObject.setMsg("输入金额低于最小出售");
				return resultObject;
			}

			Kyc kyc = this.kycService.get(partyId);
			if (null == kyc ) {
				resultObject.setCode("800");
				resultObject.setMsg("尚未KYC认证");
				return resultObject;
			}

			if ( kyc.getStatus() != 2) {
				resultObject.setCode("801");
				resultObject.setMsg("KYC认证尚未通过");
				return resultObject;
			}

			String idTip = exchangeRateService.updateExchange(partyId,exchangeRate,usdt,kyc,bankName,bankAccount);
			resultObject.setData(data);

			tipService.saveTip(idTip, TipConstants.EXCHANGE_ORDER);

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
	 * 兑换记录
	 * @return
	 */
	@PostMapping( action+"records.action")
	public Object projectIncomeList(HttpServletRequest request){
		ResultObject resultObject = readSecurityContextFromSession(new ResultObject());
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		PageInfo pageInfo = getPageInfo(request);
		String partyId = this.getLoginPartyId();
		JSONArray jsonArray = new JSONArray();
		for(ExchangeOrder pl : exchangeRateService.listExchangeRecords(partyId,pageInfo.getPageNum(),pageInfo.getPageSize())){
			JSONObject o = new JSONObject();
			o.put("id", pl.getId());
			o.put("symbol", pl.getSymbol());
			o.put("symbolValue",pl.getSymbolValue());
			o.put("rata", pl.getRata());
			o.put("realAmount", pl.getRealAmount());
			o.put("status", pl.getStaus());
			o.put("currency_symbol", pl.getCurrency_symbol());
			o.put("createTime", DateUtils.getLongDate(pl.getCreateTime()));
			o.put("orderPriceType", pl.getOrderPriceType());
			o.put("orderPriceAmount", pl.getOrderPriceAmount());
			o.put("payType", pl.getPayType());
			o.put("bankName", pl.getBankName());
			o.put("bankAccount", pl.getBankAccount());
			jsonArray.add(o);
		}

		JSONObject object = new JSONObject();
		object.put("pageInfo", pageInfo);
		object.put("pageList", jsonArray);
		resultObject.setData(object);
		return resultObject;
	}
}
