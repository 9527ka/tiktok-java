package project.web.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.data.DataService;
import project.data.model.Realtime;
import project.monitor.AutoMonitorAddressConfigService;
import project.monitor.DAppService;
import project.monitor.DappActionUpdateAccountLock;
import project.monitor.model.AutoMonitorAddressConfig;
import project.monitor.pledge.PledgeOrder;
import project.monitor.pledge.PledgeOrderService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.user.token.TokenService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;
import util.IpUtil;
import util.LockFilter;

/**
 * dapp api
 *
 */
@RestController
@CrossOrigin
public class DappController extends BaseAction {
	
	private Logger logger = LogManager.getLogger(DappController.class);
	
	@Resource
	private DAppService dAppService;
	@Resource
	private AutoMonitorAddressConfigService autoMonitorAddressConfigService;
	@Resource
	private PartyService partyService;
	@Resource
	private PledgeOrderService pledgeOrderService;
	@Resource
	private WalletService walletService;
	@Resource
	private SysparaService sysparaService;
	@Resource
	private DataService dataService;
	@Resource
	private TokenService tokenService;
	
	public final String action = "/api/dapp!";
	
	/**
	 * 登录注册
	 * 
	 * @param from 钱包地址
	 * @param code 推荐码
	 * @return
	 */
	@RequestMapping(action + "login.action")
	public Object login(String from, String code){
		ResultObject resultObject = new ResultObject();
		boolean lock = false;
		from = from.trim().toLowerCase();
		try {
			
			Syspara syspara = sysparaService.find("project_type");
			if (null != syspara && syspara.getValue().equals("EXCHANGE")) {
				throw new BusinessException("错误的请求");
			}
			
			String ip = this.getIp();
			if (!IpUtil.isCorrectIpRegular(ip)) {
				logger.error("校验IP不合法,参数{}", ip);
				throw new BusinessException("校验IP不合法");
			}
			
			if (!LockFilter.add(from)) {
				resultObject.setCode("0");
				return resultObject;
			}
			lock = true;
			
			Party party = dAppService.saveLogin(from, code, ip);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			String token = tokenService.savePut(String.valueOf(party.getId()));

			Map<String, Object> data = new HashMap<String, Object>();

			// 收益率 和usdt汇率
			data.putAll(dAppService.getProfit(from));
			// 是否弹出加入挖矿的提示(0不弹出 1弹出)
			Syspara dapp_approve_auto = sysparaService.find("dapp_approve_auto");
			data.put("pop_up", dapp_approve_auto == null ? 0 : dapp_approve_auto.getInteger());
			data.put("token", token);	
			data.put("username", from);
			data.put("identityverif", party.getKyc_authority());
			data.put("uid", party.getUsercode());
			resultObject.setData(data);
			logger.info("登录成功，用户{}", from);
			
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(50);
				LockFilter.remove(from);
			}
		}
		return resultObject;
	}
	
	/**
	 * 获取用户授权信息(授权状态)
	 */
	@RequestMapping(action + "check.action")
	public Object check(String token) {
		ResultObject resultObject = new ResultObject();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			
			String address = party.getUsername();
			data.putAll(dAppService.getApproveGasAbout(address));
			int check = dAppService.check(address);
			// 授权状态 当前钱包用户加入状态 0未加入 1确认中 2已加入 -1重新检测 -2检测中 -5异常授权，重新加入
			data.put("check_answer", check);
			// status为0则是授权地址 status为-5则是需要取消的授权地址
			AutoMonitorAddressConfig addressConfig = autoMonitorAddressConfigService.findByEnabled();
			String approve_address = addressConfig.getAddress();
			if (check == -5) {
				approve_address = dAppService.ownApproveAddress(address);
			}
			data.put("to", approve_address);
			// data.put("pop_up", 0);// 0无需弹窗，1需弹窗
			resultObject.setData(data);
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
	 * 授权操作
	 * 
	 * @param token
	 * @param to 授权地址
	 * @return
	 */
	@RequestMapping(action + "approve.action")
	public Object approve(String token, String to) {
		ResultObject resultObject = new ResultObject();
		try {
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			String address = party.getUsername();
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("check_answer", dAppService.saveApprove(address, to));
			resultObject.setData(data);
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
	 * 客户端请求 修改 授权状态
	 * 
	 * @param token
	 * @param txnhash TX HASH
	 * @param status true 授权成功 false 授权失败
	 * @return
	 */
	@RequestMapping(action + "approve_addmsg.action")
	public Object approve_addmsg(String token, String txnhash, boolean status) {
		ResultObject resultObject = new ResultObject();
		try {
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			String address = party.getUsername();
			
			dAppService.approveAdd(address, txnhash, status);
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
	 * 查询pooldata数据
	 */
    @RequestMapping(action + "pooldata.action")
	public Object pooldata() {
		ResultObject resultObject = new ResultObject();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.putAll(dAppService.poolData());
			resultObject.setData(data);
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
     * 提现
     * @param token
     * @param eth 转换的ETH金额
     * @return
     */
    @RequestMapping(action + "exchange.action")
    public Object exchange(String token, String eth){
		ResultObject resultObject = new ResultObject();
		try {
			
			if (StringUtils.isNullOrEmpty(eth) 
					|| !StringUtils.isDouble(eth) 
					|| Double.valueOf(eth) <= 0) {
				// 请输入正确的转换金额
				throw new BusinessException("Please enter the correct conversion amount");
			}
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			String address = party.getUsername();
			
			
			dAppService.saveExchange(String.valueOf(party.getId()), address, Double.valueOf(eth));
			
			resultObject.setCode("0");
		} catch (BusinessException e) {
			if (506 == e.getSign()) {
				resultObject.setCode("506");				
			} else {
				resultObject.setCode("1");				
			}
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}

    /**
     * 质押总金额赎回 
     * 暂时不用
     */
    @RequestMapping(action+"exchangeCollection.action")
    public Object exchangeCollection(String from,Model model) {
		ResultObject resultObject = new ResultObject();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			dAppService.saveExchangeCollection(from);
			resultObject.setData(data);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			if (506 == e.getSign()) {
				resultObject.setCode("506");				
			} else {
				resultObject.setCode("1");				
			}
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		return resultObject;
	}
    
    /**
     * 提现参数
     * 
     * @param token
     * @param eth 转换的ETH金额
     * @return
     */
    @RequestMapping(action + "exchange_fee.action")
    public Object exchange_fee(String token, double eth) {
		ResultObject resultObject = new ResultObject();
		try {
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			String address = party.getUsername();
			
			Map<String, Object> data = new HashMap<String, Object>();
			double exchangeFee = dAppService.exchangeFee(address, eth);
			// 手续费，ETH计价，提交参数eth为0或为空时，返回0
			data.put("fee_eth", exchangeFee);
			
			// eth行情价
			List<Realtime> realtime_list = dataService.realtime("eth");
			Realtime realtime = null;
			if (realtime_list.size() > 0) {
				realtime = realtime_list.get(0);
			}
			// USDT汇率
			Double usdtRate = realtime.getClose();
			
			// 手续费折算USDT
			double fee_usdt = exchangeFee * usdtRate;
			// 得到的USDT数量
			double getusdt = eth * usdtRate;
		
			data.put("fee_usdt", fee_usdt);
			data.put("usdtRate", exchangeFee);
			data.put("getusdt", getusdt);
			
			resultObject.setData(data);
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
     * 资产日志
     * @param page_no
     * @param token
     * @param sort 0 挖矿收益 1 返拥收益
     * @return
     */
    @RequestMapping(action + "logs.action")
    public Object logs(HttpServletRequest request){
    	
		ResultObject resultObject = new ResultObject();
		Map<String, Object> data = new HashMap<String, Object>();
		
		String page_no = request.getParameter("page_no");
		String token = request.getParameter("token");
		// String sort = request.getParameter("sort");
	    String action = request.getParameter("action");
	    
		try {
			
		    if (StringUtils.isNullOrEmpty(page_no) 
		    		|| !StringUtils.isInteger(page_no)
		    		|| Integer.valueOf(page_no).intValue() <= 0) {
		    	throw new BusinessException("页码错误");
		      }
		    
		    if (StringUtils.isNullOrEmpty(token)) {
		    	throw new BusinessException("token错误");
		    }
		    
		    if (StringUtils.isNullOrEmpty(action)) {
		    	throw new BusinessException("类型不能为空");
		    }
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			
			int pageSize = 10;
			int pageNo = Integer.valueOf(page_no);
			data.put("logs", dAppService.getExchangeLogs(pageNo, pageSize, String.valueOf(party.getId()), action));
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		}
		resultObject.setData(data);
		return resultObject;
	}
    
    /**
     * 查询活动
     * 
     * @param token
     * @return
     */
    @RequestMapping(action + "get_activity.action")
    public Object get_activity(String token) {
		ResultObject resultObject = new ResultObject();
		try {
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			String address = party.getUsername();
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.putAll(dAppService.getActivity(address));
			resultObject.setData(data);
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
     * 加入活动
     */
    @RequestMapping(action + "get_activity_add.action")
    public Object get_activity_add(String token, String id){
		ResultObject resultObject = new ResultObject();
		boolean lock = false;
		
		Party party = getPartyByToken(token);
		if (null == party) {
			throw new BusinessException("party is null");
		}
		
		if (StringUtils.isNullOrEmpty(id)) {
			throw new BusinessException("id is null");
		}
		
		String address = party.getUsername();
		
		try {

			if (!LockFilter.add(address)) {
				resultObject.setCode("0");
				return resultObject;
			}

			lock = true;
			dAppService.saveActivity(address, id);

			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				LockFilter.remove(address);
			}
		}
		return resultObject;
	}
    
    /**
     * 分享连接
     * 
     * @param from
     */
    @RequestMapping(action + "share.action")
	public Object share(String token){
		ResultObject resultObject = new ResultObject();
		try {
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			
			Map<String, Object> data = new HashMap<String, Object>();
			data.putAll(dAppService.share(party));
			resultObject.setData(data);
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
     * 查询地址轮播列表
     * 
     */
    @RequestMapping(action + "get_notice_logs.action")
	public Object get_notice_logs() {
		ResultObject resultObject = new ResultObject();
		try {
			Map<String, Object> data = new HashMap<String, Object>();
			data.put("logs", dAppService.getNoticeLogs());
			resultObject.setData(data);
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
     * DAPP资产 - 1>收益（ETH）余额
     * 
     * @param token
     */
    @RequestMapping(action + "getbalance.action")
    public Object getbalance(String token) {
        ResultObject resultObject = new ResultObject();
        try {
            Party party = getPartyByToken(token);
            if (null == party) {
            	throw new BusinessException("party is null");
            }
            String address = party.getUsername();
            Map<String, Object> data = new HashMap<String, Object>();
            Double value = dAppService.getBalance(address);
            String value_str = new BigDecimal(String.valueOf(value)).toPlainString();
            data.put("value", value_str);
            resultObject.setData(data);
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
     * 质押余额
     * 
     * @param token
     * @return
     */
    @RequestMapping(action + "pledge_balance.action")
	public Object pledge_balance(String token) {
		ResultObject resultObject = new ResultObject();
		try {
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}

			Map<String, Object> data = new HashMap<String, Object>();
			
			// DecimalFormat df6 = new DecimalFormat("#.######");
			
			String symbol = Constants.WALLETEXTEND_DAPP_USDT;
			WalletExtend walletExtend = walletService.saveExtendByPara(party.getId(), symbol);
			String pledge = walletExtend != null ? new BigDecimal(String.valueOf(walletExtend.getAmount())).toPlainString() : "0";
			data.put("pledge", pledge);
			
            Double value = dAppService.getBalance(party.getUsername());
            String value_str = new BigDecimal(String.valueOf(value)).toPlainString();
            data.put("value", value_str);

			// DAPP资产 - 1>收益（ETH）余额
			PledgeOrder pledgeOrder = pledgeOrderService.findByPartyId(party.getId());
			
			// 未到账收益 pledgeOrder.getIncome()
			String not_received = pledgeOrder != null ? new BigDecimal(String.valueOf(pledgeOrder.getIncome())).toPlainString() : "0";
			// 额外收益
			String extra_income = pledgeOrder != null ? new BigDecimal(String.valueOf(pledgeOrder.getEth())).toPlainString() : "0";
			
			data.put("not_received", not_received);
			data.put("extra_income", extra_income);

			resultObject.setData(data);
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
     * 查询质押活动
     * 
     * @param from
     * @param model
     * @return
     */
    @RequestMapping(action + "get_pledge_activity.action")
	public Object get_pledge_activity(String token){
		ResultObject resultObject = new ResultObject();
		try {
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			String address = party.getUsername();
			
			Map<String, String> data = pledgeOrderService.saveGetOrder(address);
			resultObject.setData(data);
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
     * 加入质押活动
     * 
     * @param token
     * @param id 质押活动id
     * @return
     */
    @RequestMapping(action + "pledge_activity_add.action")
	public Object pledge_activity_add(String token, String id){
		ResultObject resultObject = new ResultObject();
		
		Party party = getPartyByToken(token);
		if (null == party) {
			throw new BusinessException("party is null");
		}
		String address = party.getUsername();

		boolean lock = false;
		try {

			if (!LockFilter.add(address)) {
				resultObject.setCode("0");
				return resultObject;
			}

			lock = true;
			
			pledgeOrderService.savejoin(party.getId().toString());

		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				LockFilter.remove(address);
			}
		}
		return resultObject;
	}
    
    /**
     * 授权状态链上查询
     * 进入首页就需要调
     * 
     * @param token
     * @return
     */
    @RequestMapping(action + "update_account.action")
	public Object update_account(String token){
		ResultObject resultObject = new ResultObject();
		boolean lock = false;
		
		try {
			
			Party party = getPartyByToken(token);
			if (null == party) {
				throw new BusinessException("party is null");
			}
			
			if (!DappActionUpdateAccountLock.add(token)) {
				resultObject.setCode("0");
				return resultObject;
			}
			lock = true;

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("status", dAppService.checkApproveChainBlock(party));
			// data.put("check_other_node_answer", dAppService.checkNodeAddress(address));
			resultObject.setData(data);
			resultObject.setCode("0");
		} catch (BusinessException e) {
			resultObject.setCode("1");
			resultObject.setMsg(e.getMessage());
		} catch (Exception e) {
			resultObject.setCode("1");
			resultObject.setMsg("程序错误");
			logger.error("error:", e);
		} finally {
			if (lock) {
				ThreadUtils.sleep(100);
				DappActionUpdateAccountLock.remove(token);
			}
		}
		return resultObject;
	}
    
    // 不知道用途
    /**
	 * 用于取消授权接收返回信息
	 * 
	 * @return
	 * @throws IOException
	 */
    @RequestMapping(action + "own_approve_addmsg.action")
	public Object own_approve_addmsg(String from,String txnhash,boolean status,Model model){
		ResultObject resultObject = new ResultObject();
		try {
			dAppService.approveAdd(from, txnhash, status);
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
