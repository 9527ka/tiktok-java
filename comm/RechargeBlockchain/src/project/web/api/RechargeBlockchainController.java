package project.web.api;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import kernel.util.Arith;
import kernel.web.Page;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kernel.exception.BusinessException;
import kernel.sessiontoken.SessionTokenService;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import kernel.web.BaseAction;
import kernel.web.ResultObject;
import project.Constants;
import project.blockchain.RechargeBlockchain;
import project.blockchain.RechargeBlockchainService;
import project.hobi.HobiDataService;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysParaCode;
import project.syspara.Syspara;
import project.syspara.SysparaService;
import project.wallet.WalletLogService;

/**
 * 充值
 */
@RestController
@CrossOrigin
public class RechargeBlockchainController extends BaseAction {

    private Logger logger = LogManager.getLogger(RechargeBlockchainController.class);

    @Autowired
    private RechargeBlockchainService rechargeBlockchainService;
    @Autowired
    private SessionTokenService sessionTokenService;
    @Autowired
    private SysparaService sysparaService;
    @Autowired
    protected PartyService partyService;
    @Autowired
    protected WalletLogService walletLogService;

    @Resource
    private HobiDataService hobiDataService;

    private final String action = "/api/rechargeBlockchain!";

    /**
     * 首次进入页面，传递session_token
     */
    @RequestMapping(action + "recharge_open.action")
    public Object recharge_open() throws IOException {

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            String partyId = this.getLoginPartyId();
            String session_token = this.sessionTokenService.savePut(partyId);

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("session_token", session_token);

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
     * 提取充值相关的限制配置信息
     */
    @RequestMapping(action + "rechargeLimitConfig.action")
    public Object getRechargeLimitConfig() {
        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            double recharge_limit_min = Double.valueOf(sysparaService.find("recharge_limit_min").getValue());
            double recharge_limit_max = Double.valueOf(sysparaService.find("recharge_limit_max").getValue());

            Map<String, Object> data = new HashMap<String, Object>();
            data.put("rechargeAmountMin", recharge_limit_min);
            data.put("rechargeAmountMax", recharge_limit_max);

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
     * 充值申请
     * <p>
     * from 客户自己的区块链地址
     * blockchain_name 充值链名称
     * amount 充值数量
     * img 已充值的上传图片
     * coin 充值币种
     * channel_address 通道充值地址
     * tx 转账hash
     */
    @RequestMapping(action + "recharge.action")
    public Object recharge(HttpServletRequest request) throws IOException {
        String session_token = request.getParameter("session_token");
        String amount = request.getParameter("amount");
        String from = request.getParameter("from");
        String blockchain_name = request.getParameter("blockchain_name");
        String img = request.getParameter("img");
        String coin = request.getParameter("coin");
        String channel_address = request.getParameter("channel_address");
        String tx = request.getParameter("tx");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            String error = this.verif(amount, coin.toLowerCase(), img, from, tx);
            if (!StringUtils.isNullOrEmpty(error)) {
                throw new BusinessException(error);
            }

            double amount_double = Double.valueOf(amount).doubleValue();

            Object object = this.sessionTokenService.cacheGet(session_token);
            this.sessionTokenService.delete(session_token);
            if (null == object || !this.getLoginPartyId().equals((String) object)) {
                throw new BusinessException("请稍后再试");
            }

            Party party = this.partyService.cachePartyBy(this.getLoginPartyId(), false);
            if (Constants.SECURITY_ROLE_TEST.equals(party.getRolename())) {
                throw new BusinessException("无权限");
            }
            if (!party.getEnabled()) {
                throw new BusinessException("业务已锁定，请联系客服！");
            }

            double exchangeRate;
            if (coin.equalsIgnoreCase("BTC")) {
                exchangeRate = Double.parseDouble(hobiDataService.getSymbolRealPrize("btc"));
            } else if (coin.equalsIgnoreCase("ETH")) {
                exchangeRate = Double.parseDouble(hobiDataService.getSymbolRealPrize("eth"));
            } else {
                // USDT、USDC 币种也支持 ERC20 类型的链，经同事确认也是比率 1:1
                exchangeRate = 1.0;
            }

            // 充值提成比例
            double rechargeCommissionRate = 0.0;
            Syspara rechargeCommissionPara = sysparaService.find(SysParaCode.RECHARGE_COMMISSION_RATE.getCode());
            if (rechargeCommissionPara != null) {
                String rechargeCommissionRateStr = rechargeCommissionPara.getValue().trim();
                if (StrUtil.isNotBlank(rechargeCommissionRateStr)) {
                    rechargeCommissionRate = Double.parseDouble(rechargeCommissionRateStr.trim());
                }
            }

            RechargeBlockchain recharge = new RechargeBlockchain();
            recharge.setAddress(from);
            recharge.setBlockchain_name(blockchain_name);
            recharge.setVolume(amount_double);
            recharge.setImg(img);
            recharge.setSymbol(coin.toLowerCase());
            recharge.setPartyId(this.getLoginPartyId());
            recharge.setSucceeded(0);
            recharge.setChannel_address(channel_address);
            recharge.setTx(StringUtils.isEmptyString(tx) ? "" : tx);
            recharge.setAmount(Arith.roundDown(Arith.mul(amount_double, exchangeRate), 2));
            recharge.setRechargeCommission(Arith.roundDown(Arith.mul(recharge.getAmount(), rechargeCommissionRate), 2));

            this.rechargeBlockchainService.save(recharge, exchangeRate);

            Map<String, Object> data = new HashMap<String, Object>();
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
     * 充值订单详情
     * <p>
     * order_no 订单号
     */
    @RequestMapping(action + "get.action")
    public Object get(HttpServletRequest request) throws IOException {
        String order_no = request.getParameter("order_no");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {

            Map<String, Object> map = new HashMap<String, Object>();

            RechargeBlockchain order = this.rechargeBlockchainService.findByOrderNo(order_no);

            map.put("order_no", order.getOrder_no());
            map.put("volume", order.getVolume());
            map.put("amount", order.getAmount());
            map.put("create_time", DateUtils.format(order.getCreated(), DateUtils.DF_yyyyMMddHHmmss));
            map.put("from", order.getAddress());
            map.put("coin", order.getSymbol().toUpperCase());
            map.put("coin_blockchain",
                    order.getSymbol().toUpperCase().contains("BTC")
                            || order.getSymbol().toUpperCase().contains("ETH") ? order.getSymbol().toUpperCase()
                            : order.getBlockchain_name() == null ? null : "USDT_" + order.getBlockchain_name().toUpperCase());
            map.put("fee", 0);
            map.put("state", order.getSucceeded());
            map.put("tx", order.getTx());
            map.put("img", order.getImg());
            map.put("channel_address", order.getChannel_address());
            map.put("payUrl", order.getPayUrl());
            map.put("isThirdParty", order.getIsThirdParty());

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
     * 充值记录
     */
    @RequestMapping(action + "list.action")
    public Object list(HttpServletRequest request) throws IOException {
        String page_no = request.getParameter("page_no");
        String page_size = request.getParameter("page_size");

        ResultObject resultObject = new ResultObject();
        resultObject = this.readSecurityContextFromSession(resultObject);
        if (!"0".equals(resultObject.getCode())) {
            return resultObject;
        }

        try {
            if (StringUtils.isNullOrEmpty(page_no)) {
                page_no = "1";
            }
            if (StringUtils.isNullOrEmpty(page_size)) {
                page_size = "10";
            }
            if (!StringUtils.isInteger(page_no)) {
                throw new BusinessException("页码不是整数");
            }
            if (Integer.valueOf(page_no).intValue() <= 0) {
                throw new BusinessException("页码不能小于等于0");
            }

            int page_no_int = Integer.valueOf(page_no).intValue();

            Page pageInfo = this.walletLogService.pagedQueryRecharge(page_no_int, Integer.parseInt(page_size), this.getLoginPartyId(), "1");
            List<Map<String, Object>> data = pageInfo.getElements();
            for (Map<String, Object> log : data) {
                if (null == log.get("coin") || !StringUtils.isNotEmpty(log.get("coin").toString())) {
                    log.put("coin", Constants.WALLET);
                } else {
                    log.put("coin", log.get("coin").toString().toUpperCase());
                }
                logger.info("-----> rechargeBlockchain!list.action 方法中, log:" + log);

                Object currentSymbol = log.get("symbol");
                if (currentSymbol == null) {
                    log.put("coin_blockchain", "unknow");
                } else {
                    if (currentSymbol.toString().toUpperCase().indexOf("BTC") != -1
                            || currentSymbol.toString().toUpperCase().indexOf("ETH") != -1) {
                        log.put("coin_blockchain", currentSymbol.toString().toUpperCase());
                    } else {
                        Object chainName = log.get("blockchain_name");
                        if (chainName == null) {
                            chainName = "unknow";
                        }
                        log.put("coin_blockchain", "USDT_" + chainName.toString().toUpperCase());
                    }
                }

                log.put("fee", 0);
                log.put("from", log.get("address") == null ? null : log.get("address").toString());

                Object oriAmount = log.get("amount");
                Object oriVolume = log.get("volume");
                if (oriAmount != null) {
                    //Arith.round(money,2);
                    // 换算成 USDT 的充值金额
                    String strAmount = String.valueOf(oriAmount);
                    strAmount = (new BigDecimal(strAmount)).toPlainString();
                    int idx = strAmount.indexOf(".");
                    if (idx < 0) {
                        log.put("amount", strAmount + ".00");
                    } else {
                        int len = strAmount.substring(idx + 1).length();
                        /*if (len == 1) {
                            log.put("amount", strAmount + "0");
                        } else {
                            log.put("amount", strAmount.substring(0, idx + 3));
                        }*/
                        if (len <= 10) {
                            log.put("amount", strAmount);
                        } else {
                            log.put("amount", strAmount.substring(0, idx + 10));
                        }
                    }


                }
                if (oriVolume != null) {
                    // 原始充值币种下的金额
                    // 注意科学计数法问题
                    String strVolume = String.valueOf(oriVolume);
                    strVolume = (new BigDecimal(strVolume)).toPlainString();
                    int idx = strVolume.indexOf(".");
                    if (idx < 0) {
                        log.put("volume", strVolume + ".00");
                    } else {
                        int len = strVolume.substring(idx + 1).length();
                        if (len <= 10) {
                            log.put("volume", strVolume);
                        } else {
                            log.put("volume", strVolume.substring(0, idx + 10));
                        }
                    }
                }
            }

            resultObject.setData(pageInfo);
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

    private String verif(String amount, String coin, String img, String from, String tx) {

        if (StringUtils.isNullOrEmpty(amount)) {
            return "充值数量必填";
        }
        if (!StringUtils.isDouble(amount)) {
            return "充值数量输入错误，请输入浮点数";
        }
        if (Double.valueOf(amount).doubleValue() <= 0) {
            return "充值数量不能小于等于0";
        }

        if (StringUtils.isEmptyString(coin)) {
            return "请输入充值币种";
        }

//		if (1 == this.sysparaService.find("can_recharge").getInteger()) {
//			// 允许在线充值
//			boolean recharge_must_need_qr = this.sysparaService.find("recharge_must_need_qr").getBoolean();
//			if (recharge_must_need_qr) {
//				if (StringUtils.isEmptyString(img))
//					return "请上传图片";
//			}
//		}

        double recharge_limit_min = Double.valueOf(sysparaService.find("recharge_limit_min").getValue());
        double recharge_limit_max = Double.valueOf(sysparaService.find("recharge_limit_max").getValue());
        double amountVal = Double.valueOf(amount);
        if ("usdt".equals(coin)) {
            if (amountVal < recharge_limit_min) {
                throw new BusinessException("充值价值不得小于最小限额");
            }

            if (amountVal > recharge_limit_max) {
                throw new BusinessException("充值价值不得大于最大限额");
            }

        } else {

            double fee = 1;

            if (coin.equalsIgnoreCase("BTC")) {
                fee = sysparaService.find("btc_exchange_usdt").getDouble();
            } else if (coin.equalsIgnoreCase("ETH")) {
                fee = sysparaService.find("eth_exchange_usdt").getDouble();
            }

            double transfer_usdt = Arith.mul(fee, amountVal);// 对应usdt价格
            if (transfer_usdt < recharge_limit_min) {

                throw new BusinessException("充值价值不得小于最小限额");
            }

            if (transfer_usdt > recharge_limit_max) {
                throw new BusinessException("充值价值不得大于最大限额");
            }

        }

        if (StringUtils.isEmptyString(from)) {
            return "请输入地址";
        }

//		if (StringUtils.isEmptyString(tx)) {
//			return "请输入转账hash";
//		}

        return null;
    }

    /**
     * 获取最新价格(仅仅充值用)
     *
     * @return
     */
    @RequestMapping(action + "fee.action")
    public Object getRealtimeBySymbol(HttpServletRequest request) {

        ResultObject resultObject = new ResultObject();
        String symbol = request.getParameter("symbol");

        JSONObject object = new JSONObject();

        if (symbol == null) {
            object.put("price", "0");
        } else if (symbol.equalsIgnoreCase("btc")) {
            object.put("price", hobiDataService.getSymbolRealPrize("btc"));
        } else if (symbol.equalsIgnoreCase("eth")) {
            object.put("price", hobiDataService.getSymbolRealPrize("eth"));
        } else {
            object.put("price", "1");
        }
        resultObject.setData(object);

        return resultObject;

    }

}
