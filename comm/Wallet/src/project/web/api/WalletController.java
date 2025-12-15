package project.web.api;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.util.StrUtil;
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
import project.data.DataService;
import project.data.model.Realtime;
import project.item.ItemService;
import project.item.model.Item;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.Wallet;
import project.wallet.WalletExtend;
import project.wallet.WalletLogService;
import project.wallet.WalletService;

/**
 * 钱包
 */
@RestController
@CrossOrigin
public class WalletController extends BaseAction {

	private Logger logger = LogManager.getLogger(WalletController.class);

	@Autowired
	protected WalletService walletService;
	@Autowired
	protected ItemService itemService;
	@Autowired
	private DataService dataService;
	@Autowired
	protected WalletLogService walletLogService;
	@Resource
	protected SysparaService sysparaService;

	private final String action = "/api/wallet!";

	/**
	 * 钱包账户资产（usdt、btc、eth）
	 */
	@RequestMapping(action + "get.action")
	public Object get(HttpServletRequest request) throws IOException {
		return getWalletExtends(request, false);
	}

	/**
	 * 钱包账户资产（所有币种）
	 */
	@RequestMapping(action + "getAll.action")
	public Object getAll(HttpServletRequest request) throws IOException {
		return getWalletExtends(request, true);
	}
	
	/**
	 * all：true/获取全部；false/获取usdt、btc、eth；
	 */
	public Object getWalletExtends(HttpServletRequest request, boolean all) throws IOException {
		String symbol = request.getParameter("symbol");
		
		ResultObject resultObject = new ResultObject();

		try {
			
			Map<String, Object> mapRet = new LinkedHashMap<String, Object>();

			DecimalFormat df2 = new DecimalFormat("#.########");
			// 向下取整
			df2.setRoundingMode(RoundingMode.FLOOR);

			String partyId = this.getLoginPartyId();
			Wallet usdt = null;
			if (StringUtils.isNotEmpty(partyId)) {
				usdt = this.walletService.saveWalletByPartyId(partyId);
			}

			if (null == usdt) {
				usdt = new Wallet();
				usdt.setMoney(0.0);
				mapRet.put("usdt", usdt.getMoney());
			} else {
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.FLOOR);
				usdt.setMoney(Arith.roundDown(Double.valueOf(df.format(usdt.getMoney())),2));
				mapRet.put("usdt", usdt.getMoney());
			}

			// 其他币账户
			List<Item> list_it = this.itemService.cacheGetByMarket("");
			List<String> list_symbol = new ArrayList<String>();
					
			if (!StringUtils.isNotEmpty(symbol)) {
				// symbol为空，获取所有的
				for (int i = 0; i < list_it.size(); i++) {
					Item items = list_it.get(i);
					list_symbol.add(items.getSymbol());
				}
			} else {
				List<String> symbolList = Arrays.asList(symbol.split(","));
				for (int i = 0; i < list_it.size(); i++) {
					Item items = list_it.get(i);
					// 只添加所有币种和参数symbol都有的
					if (symbolList.contains(items.getSymbol())) {
						list_symbol.add(items.getSymbol());
					}
				}
			}

			List<Item> items = this.itemService.cacheGetAll();
			// 按id排序
			Collections.sort(items, new Comparator<Item>() {
				@Override
				public int compare(Item arg0, Item arg1) {
					return arg0.getId().toString().compareTo(arg1.getId().toString());
				}
			});
			
			Map<String, Item> itemMap = new HashMap<String, Item>();
			for (int i = 0; i < items.size(); i++) {
				itemMap.put(items.get(i).getSymbol(), items.get(i));
			}	
			
			List<WalletExtend> walletExtends = null;
			if (StringUtils.isNotEmpty(partyId)) {
				
//				// symbols大写列表
//				List<String> list_symbol_uppercase = new ArrayList<String>();
//				for (int i = 0; i < list_symbol.size(); i++) {
//					list_symbol_uppercase.add(list_symbol.get(i).toUpperCase());
//				}
				
				walletExtends = this.walletService.findExtend(partyId, list_symbol);
			}
			
			if (null == walletExtends) {
				walletExtends = new ArrayList<WalletExtend>();
			}
			
			List<WalletExtend> walletExtendsRet = new ArrayList<WalletExtend>();

			int temp = 0;
			for (int i = 0; i < list_symbol.size(); i++) {
				
				for (int j = 0; j < walletExtends.size(); j++) {
					
					WalletExtend walletExtend = walletExtends.get(j);
					if (walletExtend.getWallettype().equals(list_symbol.get(i))) {
						walletExtend.setAmount(Double.valueOf(df2.format(walletExtend.getAmount())));
						walletExtendsRet.add(walletExtend);
						temp = 1;
					}
				}
				
				if (0 == temp) {
					WalletExtend walletExtend = new WalletExtend();
					if (StringUtils.isNotEmpty(partyId)) {
						walletExtend.setPartyId(partyId);
					}
					walletExtend.setWallettype(list_symbol.get(i));
					walletExtend.setAmount(0);
					walletExtend.setName(itemMap.get(list_symbol.get(i)).getName());
					walletExtendsRet.add(walletExtend);
				}
				
				temp = 0;
			}
			
			String symbolsStr = "";
			for (int i = 0; i < list_symbol.size(); i++) {
				if (i != 0) {
					symbolsStr = symbolsStr + "," + list_symbol.get(i);
				} else {
					symbolsStr = list_symbol.get(i);
				}			
			}
			
			List<Realtime> realtime_all = this.dataService.realtime(symbolsStr);
			if (realtime_all.size() <= 0) {
//				throw new BusinessException("系统错误，请稍后重试");
			}
			
			Map<String, Realtime> realtimeMap = new HashMap<String, Realtime>();
			for (int i = 0; i < realtime_all.size(); i++) {
				realtimeMap.put(realtime_all.get(i).getSymbol(), realtime_all.get(i));
			}
			
			List<Map<String, Object>> extendsList = new ArrayList<Map<String,Object>>();
			for (int i = 0; i < walletExtendsRet.size(); i++) {
				
				if (false == all) {
					// 只要btc、eth
					if (!walletExtendsRet.get(i).getWallettype().equals("btc")
							&& !walletExtendsRet.get(i).getWallettype().equals("eth")) {
						continue;
					}					
				}
				
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("name", walletExtendsRet.get(i).getName());
				map.put("symbol", walletExtendsRet.get(i).getWallettype());
				map.put("volume", walletExtendsRet.get(i).getAmount());
				
				Realtime rt = realtimeMap.get(walletExtendsRet.get(i).getWallettype());
				if (null != rt) {
					map.put("usdt", Arith.mul(rt.getClose(), walletExtendsRet.get(i).getAmount()));
				} else {
					map.put("usdt", 0);
				}
				
				extendsList.add(map);
			}
			
			if (!StringUtils.isNotEmpty(symbol) || symbol.contains("usdt")) {
				// 添加usdt到列表最前面
				Map<String, Object> mapUsdt = new HashMap<String, Object>();
				mapUsdt.put("name", "USDT/USDT");
				mapUsdt.put("symbol", "usdt");
				mapUsdt.put("volume", mapRet.get("usdt"));
				mapUsdt.put("usdt", mapRet.get("usdt"));
				extendsList.add(0, mapUsdt);
			}
			
			mapRet.put("extends", extendsList);
			
			resultObject.setData(mapRet);

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
	 * 钱包日志  充值category=recharge；提现category=withdraw；
	 */
	@RequestMapping(action + "logs.action")
	public Object logs(HttpServletRequest request) throws IOException {
		String page_no = request.getParameter("page_no");
		String category = request.getParameter("category");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {

			if (StringUtils.isNullOrEmpty(page_no)) {
				page_no = "1";
			}
			if (!StringUtils.isInteger(page_no)) {
				throw new BusinessException("页码不是整数");
			}
			if (Integer.valueOf(page_no).intValue() <= 0) {
				throw new BusinessException("页码不能小于等于0");
			}

			int page_no_int = Integer.valueOf(page_no).intValue();

			List<Map<String, Object>> data = this.walletLogService.pagedQuery(page_no_int, 10, this.getLoginPartyId(), category, "1").getElements();
			for (Map<String, Object> log : data) {

				if (null == log.get("wallettype") || !StringUtils.isNotEmpty(log.get("wallettype").toString()))
					log.put("wallettype", Constants.WALLET);
				else {
					log.put("wallettype", log.get("wallettype").toString().toUpperCase());
				}
			}
			
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

	/**
	 * 钱包历史记录
	 */
	@RequestMapping(action + "records.action")
	public Object records(HttpServletRequest request) throws IOException {		
		// 页码
		String page_no = request.getParameter("page_no");
		/**
		 * 资金账变类型，
		 * project.Constants(line:214-250)
		 */
		String category = request.getParameter("category");

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {
			
			if (StringUtils.isNullOrEmpty(page_no)) {
				page_no = "1";
			}
			if (!StringUtils.isInteger(page_no)) {
				throw new BusinessException("页码不是整数");
			}
			if (Integer.valueOf(page_no).intValue() <= 0) {
				throw new BusinessException("页码不能小于等于0");
			}

			int page_no_int = Integer.valueOf(page_no).intValue();

			List<Map<String, Object>> data = this.walletLogService.pagedQueryRecords(page_no_int, 10, this.getLoginPartyId(), 
					category).getElements();
			for (Map<String, Object> log : data) {

				if (null == log.get("wallet_type") || !StringUtils.isNotEmpty(log.get("wallet_type").toString()))
					log.put("wallet_type", Constants.WALLET);
				else {
					log.put("wallet_type", log.get("wallet_type").toString().toUpperCase());
				}
			}
			
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

	/**
	 * 获取币种钱包
	 */
	@RequestMapping(action + "list.action")
	public Object list(HttpServletRequest request) {

		ResultObject resultObject = new ResultObject();
		resultObject = this.readSecurityContextFromSession(resultObject);
		if (!"0".equals(resultObject.getCode())) {
			return resultObject;
		}

		try {

			Map<String, Object> map = new LinkedHashMap<String, Object>();
			
			Wallet usdt = null;
			String partyId = this.getLoginPartyId();	
			if (StringUtils.isNotEmpty(partyId)) {
				usdt = this.walletService.saveWalletByPartyId(partyId);
			}
			
			DecimalFormat df2 = new DecimalFormat("#.########");
			// 向下取整
			df2.setRoundingMode(RoundingMode.FLOOR);
			if (null == usdt) {
				usdt = new Wallet();
				usdt.setMoney(0.0);
				map.put("USDT", usdt);
			} else {
				DecimalFormat df = new DecimalFormat("#.##");
				df.setRoundingMode(RoundingMode.FLOOR);
				usdt.setMoney(Arith.roundDown(Double.valueOf(df.format(usdt.getMoney())),2));
				map.put("USDT", usdt);
			}

			// 其他币账户
			List<Item> list_it = this.itemService.cacheGetByMarket("");
			List<String> list_symbol = new ArrayList<String>();
			for (int i = 0; i < list_it.size(); i++) {
				Item items = list_it.get(i);
				list_symbol.add(items.getSymbol());
			}

			List<Item> items = this.itemService.cacheGetAll();
			Collections.sort(items, new Comparator<Item>() {
				// 按id排序
				@Override
				public int compare(Item arg0, Item arg1) {
					return arg0.getId().toString().compareTo(arg1.getId().toString());
				}
			});
			
			List<WalletExtend> walletExtends = null;
			if (StringUtils.isNotEmpty(partyId)) {
				walletExtends = this.walletService.findExtend(partyId, list_symbol);
			}
			
			WalletExtend walletExtend = new WalletExtend();

			// 如果是空
			if (null == walletExtends) {
				for (int i = 0; i < items.size(); i++) {
					walletExtend.setWallettype(items.get(i).getSymbol().toUpperCase());
					walletExtend.setAmount(0);
					map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
				}
			}
			
			// 如果不为空且2个相同
			if (walletExtends != null && walletExtends.size() == items.size()) {

				for (int i = 0; i < walletExtends.size(); i++) {
					if (null == walletExtends.get(i)) {
						continue;
					}
					walletExtend = walletExtends.get(i);
					usdt.setMoney(Arith.roundDown(Double.valueOf(df2.format(usdt.getMoney())),2));
					walletExtend.setAmount(Double.valueOf(df2.format(walletExtend.getAmount())));

					map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
				}
			}
			
			// 如果不为空 且数据库里的少于币种
			int temp = 0;
			if (walletExtends != null && walletExtends.size() < items.size()) {
				
				for (int i = 0; i < items.size(); i++) {
					
					for (int j = 0; j < walletExtends.size(); j++) {
						
						walletExtend = walletExtends.get(j);
						if (walletExtend.getWallettype().equals(items.get(i).getSymbol())) {
							walletExtend.setAmount(Double.valueOf(df2.format(walletExtend.getAmount())));
							
							map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
							temp = 1;
							break;
						}
					}
					
					if (0 == temp) {
						walletExtend = new WalletExtend();
						walletExtend.setWallettype(items.get(i).getSymbol());
						walletExtend.setAmount(0);

						map.put(walletExtend.getWallettype().toUpperCase(), walletExtend);
					} else {						
						temp = 0;
					}
				}
			}
			
			resultObject.setData(map);

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
	 * 获取usdt余额
	 * 
	 */
	@RequestMapping(action + "getUsdt.action")
	public Object getUsdt(HttpServletRequest request) {
		ResultObject resultObject = new ResultObject();
		// usdt余额
		Map<String, Object> data = new HashMap<String, Object>();
		String partyId = this.getLoginPartyId();
//		String partyId = "ff8080818604394d01860767e945005c";
		if (null == partyId || partyId.equals("")){
			resultObject.setCode("403");
			resultObject.setMsg("登录过期，请重新登录");
			return resultObject;
		}
		//DecimalFormat df2 = new DecimalFormat("#.##");
		// 向下取整
		//df2.setRoundingMode(RoundingMode.FLOOR);

		Wallet wallet = this.walletService.selectOne(partyId);
		if (null != wallet){
			double money = wallet.getMoney();
			double frozenMoney = wallet.getMoneyAfterFrozen();
			// 账户剩余资金
			if(wallet.getFrozenState()==0){
				data.put("money", Arith.round(money,2));
				data.put("frozenMoney", Arith.round(frozenMoney,2));
			}else if (wallet.getFrozenState()==1){
				data.put("money", Arith.round(frozenMoney,2));
				data.put("frozenMoney", Arith.round(money,2));
			}
			double signBonus = Arith.round(wallet.getSignBonus(),2);
			if (signBonus>0) {
				data.put("signBonus",signBonus);
				double sellerSignBonusRatio = 0d;
				Syspara sysparaBonusRatio = sysparaService.find(SysParaCode.MALL_SELLER_SIGN_BONUS_RATIO.getCode());
				if (sysparaBonusRatio != null) {
					String sellerSignBonusRatioInfo = sysparaBonusRatio.getValue().trim();
					try {
						if (StrUtil.isNotBlank(sellerSignBonusRatioInfo)) {
							sellerSignBonusRatio = Double.parseDouble(sellerSignBonusRatioInfo);
						}
						data.put("sellerSignBonusRatio",Arith.round(sellerSignBonusRatio,2));
					} catch (NumberFormatException e) {
						logger.error(SysParaCode.MALL_SELLER_SIGN_BONUS_RATIO.getCode()+"系统参数错误");
					}
				}
				double rechargeAmount = walletLogService.getRechargeAmount(partyId);
				data.put("rechargeAmount",Arith.round(rechargeAmount,2));
			}
			data.put("rebate", Arith.round(wallet.getRebate(),2));
		}
		resultObject.setData(data);
		return resultObject;
	}

}
