package project.web.api.monitor;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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
import kernel.util.Arith;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.monitor.AutoMonitorWalletService;
import project.monitor.model.AutoMonitorWallet;
import project.monitor.pledgegalaxy.PledgeGalaxyConfig;
import project.monitor.pledgegalaxy.PledgeGalaxyConfigService;
import project.monitor.pledgegalaxy.PledgeGalaxyOrder;
import project.monitor.pledgegalaxy.PledgeGalaxyOrderService;
import project.monitor.pledgegalaxy.PledgeGalaxyStatusConstants;
import project.monitor.pledgegalaxy.job.PledgeGalaxyTeamProfitCreateJob;
import project.party.PartyService;
import project.party.model.Party;
import project.party.recom.UserRecomService;
import project.syspara.Syspara;
import project.syspara.SysparaService;

/**
 * 质押2.0
 * 银河数码-AI量化交易投资收益
 *
 */
@RestController
@CrossOrigin
public class PledgeGalaxyOrderController extends BaseAction {
	
	private Logger logger = LogManager.getLogger(PledgeGalaxyOrderController.class);
	
	@Autowired
	PledgeGalaxyOrderService pledgeGalaxyOrderService;
	@Autowired
	PartyService partyService;
	@Autowired
	AutoMonitorWalletService autoMonitorWalletService;
	@Autowired
	PledgeGalaxyConfigService pledgeGalaxyConfigService;
	@Autowired
	UserRecomService userRecomService;
	@Autowired
	SysparaService sysparaService;
	
	
	public final String action = "/api/pledgeGalaxy!";
	
	/**
	 * 质押2.0概况
	 */
	@RequestMapping(action + "getData.action")
	public Object getData(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		String partyId = getLoginPartyId();
		Map<String, Double> data = pledgeGalaxyOrderService.getData(partyId);
		resultObject.setData(data);
		return resultObject;
	}
	
	/**
	 * 根据质押天数获取利率
	 */
	@RequestMapping(action + "getRateByDays.action")
	public Object getRateByDays(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		// 质押金额
		String amount_temp = request.getParameter("amount");
		// 质押天数
		String days = request.getParameter("days");
		String partyId = getLoginPartyId();
		double amount = StringUtils.isNullOrEmpty(amount_temp) ? 0D : Double.valueOf(amount_temp);
		
		Map<String, String> map = pledgeGalaxyConfigService.getRateMap(partyId, Integer.valueOf(days), amount);
		String dynamicRate = "0";
		if (map.containsKey("dynamicRate")) {
			dynamicRate = map.get("dynamicRate");
		}
		
		String staticRate = "0";
		if (map.containsKey("staticRate")) {
			staticRate = map.get("staticRate");
		}
		
		PledgeGalaxyConfig config = pledgeGalaxyConfigService.getConfig(partyId);
		String staticIncome = config.getStaticIncomeForceValue();
		double pledgeAmountMin = config.getPledgeAmountMin();
		int pledgeDayMax = 0;
		String projectType = this.sysparaService.find("project_type").getValue();
		if (projectType.equals("DAPP_EXCHANGE_SAFEPAL5")) {
			String[] minSplit = staticIncome.split("&")[1].split(";");
			for (int i = 0; i < minSplit.length; i++) {
				String value = minSplit[i];
				String[] valueSplit = value.split("#");
				int day = Integer.valueOf(valueSplit[0]);
				int amountMin = Integer.valueOf(valueSplit[1]);
				if (Integer.valueOf(days) == day) {
					pledgeAmountMin = amountMin;
				}
				if (amount >= amountMin) {
					pledgeDayMax = day;
				}
			}
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		data.put("staticRateMin", 0);
		data.put("staticRateMax", Arith.mul(Double.valueOf(staticRate), 100));
		data.put("dynamicRateMin", 0);
		data.put("dynamicRateMax", Arith.mul(Double.valueOf(dynamicRate), 100));
		data.put("pledgeAmountMin", pledgeAmountMin);
		data.put("pledgeDayMax", pledgeDayMax);
		
		resultObject.setData(data);
		return resultObject;
	}
	
	/**
	 * 新增质押2.0订单
	 */
	@RequestMapping(action + "add.action")
	public Object add(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		String token = request.getParameter("token");
		// 质押金额
		String amount_temp = request.getParameter("amount");
		// 质押天数
		String days = request.getParameter("days");
		
		try {
			if (StringUtils.isNullOrEmpty(amount_temp) 
					|| !StringUtils.isDouble(amount_temp) 
					|| Double.valueOf(amount_temp) <= 0) {
				throw new BusinessException("请输入正确的质押金额");
			}
			
			if (StringUtils.isNullOrEmpty(days) 
					|| !StringUtils.isInteger(days) 
					|| Integer.valueOf(days) <= 0) {
				throw new BusinessException("请输入正确的质押天数");
			}
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			
			Syspara syspara = sysparaService.find("project_type");
			if (null == syspara || !syspara.getValue().equals("DAPP_EXCHANGE_SAFEPAL5")) {
				String address = party.getUsername().toLowerCase();
				AutoMonitorWallet entity = autoMonitorWalletService.findBy(address);
				if (null == entity ) {
					logger.error("新增质押2.0订单失败，未发现授权记录，当前地址{}", address);
					throw new BusinessException("未发现授权记录");
				}
				if (entity.getSucceeded() != 1) {
					logger.error("新增质押2.0订单失败，当前状态{}", entity.getSucceeded());
					throw new BusinessException("授权状态未成功");
				}
			}
			
			String partyId = String.valueOf(party.getId());
			
			PledgeGalaxyConfig config = pledgeGalaxyConfigService.getConfig(partyId);
			
			double amount = Double.valueOf(amount_temp);
			if (amount < config.getPledgeAmountMin() || amount > config.getPledgeAmountMax()) {
				throw new BusinessException("请输入正确的质押金额");
			}
			
			String staticIncome = config.getStaticIncomeForceValue();

			if (staticIncome.contains("&")) {
				int pledgeAmountMin = 0;
				int pledgeDayMax = 0;
				String[] minSplit = staticIncome.split("&")[1].split(";");
				for (int i = 0; i < minSplit.length; i++) {
					String value = minSplit[i];
					String[] valueSplit = value.split("#");
					int day = Integer.valueOf(valueSplit[0]);
					int amountMin = Integer.valueOf(valueSplit[1]);
					if (Integer.valueOf(days) == day) {
						pledgeAmountMin = amountMin;
					}
					if (amount >= amountMin) {
						pledgeDayMax = day;
					}
				}
				
				if (amount < pledgeAmountMin || Integer.valueOf(days) > pledgeDayMax) {
					throw new BusinessException("amount and days do not match");
				}
				
			}
			
			Date date = new Date();
			Calendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			calendar.add(calendar.DATE, Integer.valueOf(days));
			Date expireTime = calendar.getTime(); 
			
			PledgeGalaxyOrder order = new PledgeGalaxyOrder();
			order.setPartyId(partyId);
			order.setAmount(amount);
			order.setDays(Integer.valueOf(days));
			order.setStartTime(date);
			order.setExpireTime(expireTime);
			order.setCreateTime(date);
			pledgeGalaxyOrderService.save(order, party.getRolename(), syspara);
			
			resultObject.setMsg("加入成功");
			resultObject.setCode("0");
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
	 * 质押2.0订单列表
	 */
	@RequestMapping(action + "queryList.action")
	public Object list(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		
		String page_no = request.getParameter("page_no");
		String state = request.getParameter("state");
		
		try {
			String partyId = getLoginPartyId();
			int pageNo = Integer.valueOf(page_no);
			datas = pledgeGalaxyOrderService.pagedQuery(pageNo, 40, partyId).getElements();
			
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		}
		resultObject.setData(datas);
		return resultObject;
	}
	
	/**
	 * 质押2.0订单详情
	 */
	@RequestMapping(action + "getInfo.action")
	public Object getInfo(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		String id = request.getParameter("id");
		
		Map<String, Object> data = new HashMap<String, Object>();
		
		PledgeGalaxyOrder order = pledgeGalaxyOrderService.findById(id);
		if (null != order) {
			data.put("id", order.getId());
			data.put("amount", order.getAmount());
			data.put("days", order.getDays());
			data.put("return_time", order.getCloseTime());
			data.put("status",order.getStatus());
		}

		resultObject.setData(data);
		return resultObject;
	}
	
	/**
	 * 质押2.0配置选择
	 */
	@RequestMapping(action + "getConfig.action")
	public Object getConfig(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		
		String partyId = getLoginPartyId();
		PledgeGalaxyConfig config = pledgeGalaxyConfigService.getConfig(partyId);
		String staticIncome = config.getStaticIncomeForceValue();
		if (staticIncome.contains("&")) {
			staticIncome = staticIncome.split("&")[0];
		}
		String[] split = staticIncome.split("\\|");
		String valueSplit = split[0].split(":")[1];
		String[] daySplit = valueSplit.split(";");
		List<String> list = new ArrayList<>();
		for (int i = 0; i < daySplit.length; i++) {
			String day = daySplit[i].split("#")[0];
			list.add(day);
		}
		
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("days", list);
		data.put("pledgeAmountMin", config.getPledgeAmountMin());
		data.put("pledgeAmountMax", config.getPledgeAmountMax());
		resultObject.setData(data);
		return resultObject;
	}
	
	/**
	 * 赎回
	 */
	@RequestMapping(action + "close.action")
	public Object close(HttpServletRequest request) {
		
		ResultObject resultObject = new ResultObject();
		resultObject = readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}
		List<Map<String, Object>> datas = new ArrayList<Map<String, Object>>();
		
		try {
			String id = request.getParameter("id");
			PledgeGalaxyOrder order = pledgeGalaxyOrderService.findById(id);
			int status = order.getStatus();
			if (PledgeGalaxyStatusConstants.RETURN_APPLY == status || PledgeGalaxyStatusConstants.RETURN_SUCCESS == status) {
				throw new BusinessException("赎回状态异常");
			}
			pledgeGalaxyOrderService.updateCloseApply(order);
			resultObject.setMsg("申请成功");
			resultObject.setCode("0");
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e.fillInStackTrace());
		}
		resultObject.setData(datas);
		return resultObject;
	}
	
	/**
	 * 测团队收益
	 */
//	@RequestMapping(action + "testTeamProfit.action")
//	public void testTeamProfit() {
//		PledgeGalaxyTeamProfitCreateJob job = new PledgeGalaxyTeamProfitCreateJob();
//		job.setPledgeGalaxyOrderService(pledgeGalaxyOrderService);
//		job.setSysparaService(sysparaService);
//		job.taskJob();
//	}
	
	
    /**
     * 通过token获取party
     * @param token
     * @return
     */
	public Party getPartyByToken(String token) {
		
		String partyId = this.getLoginPartyId();
		
		if (StringUtils.isNullOrEmpty(partyId)) {
			logger.error("partyId is null");
			return null;
		}
		
		Party party = partyService.cachePartyBy(partyId, false);
		if(StringUtils.isNullOrEmpty(partyId)) {
			logger.error("party is null,partyId:{}", partyId);
			return null;
		}
		return party;
	}
}
