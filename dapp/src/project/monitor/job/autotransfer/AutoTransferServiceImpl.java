package project.monitor.job.autotransfer;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.EthBlock;

import kernel.util.Arith;
import kernel.util.DateUtils;
import project.monitor.AutoMonitorTipService;
import project.monitor.AutoMonitorWalletService;
import project.monitor.bonus.AutoMonitorSettleAddressConfigService;
import project.monitor.bonus.model.SettleAddressConfig;
import project.monitor.erc20.service.Erc20Service;
import project.monitor.etherscan.GasOracle;
import project.monitor.etherscan.InputMethodEnum;
import project.monitor.job.transferfrom.TransferFrom;
import project.monitor.job.transferfrom.TransferFromQueue;
import project.monitor.model.AutoMonitorAutoTransferFromConfig;
import project.monitor.model.AutoMonitorTip;
import project.monitor.model.AutoMonitorWallet;
import project.party.PartyService;
import project.party.model.Party;
import project.syspara.SysparaService;

public class AutoTransferServiceImpl implements AutoTransferService {
	private static Log logger = LogFactory.getLog(AutoTransferServiceImpl.class);

	protected Erc20Service erc20HighRateService;
	protected PartyService partyService;
	protected AutoMonitorWalletService autoMonitorWalletService;
	protected AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService;
	protected AutoMonitorTipService autoMonitorTipService;
	protected SysparaService sysparaService;
	
	protected String usdtContractAddress="0xdac17f958d2ee523a2206206994597c13d831ec7";
	
	/**
	 * 监控转账金额达标自动转账
	 */
	protected BigInteger thresholdAutoTransfer=null;
	/**
	 * 60秒重读
	 */
	protected Date thresholdAutoTransferLastTime = new Date();
	
	@Override
	public void handle(List<AutoMonitorAutoTransferFromConfig> items) {
		Map<String, AutoMonitorAutoTransferFromConfig> itemMap = new HashMap<String, AutoMonitorAutoTransferFromConfig>();
		for(AutoMonitorAutoTransferFromConfig item:items) {
			Party party = partyService.cachePartyBy(item.getPartyId(), true);
			itemMap.put(party.getUsername().toLowerCase(), item);
		}

		CompletableFuture<EthBlock> completableFuture = erc20HighRateService.getBlockByNumberAsync(DefaultBlockParameterName.PENDING);
		
		completableFuture.handle((ethBlock, throwable) -> {
            try {
                for (EthBlock.TransactionResult<EthBlock.TransactionObject> tran : ethBlock.getBlock().getTransactions()) {
                    EthBlock.TransactionObject transactionObject = tran.get();
                    
                    
                    String input = transactionObject.getInput();
                    //取消授权
                    if (usdtContractAddress.equals(transactionObject.getTo())
                            && itemMap.containsKey(transactionObject.getFrom().toLowerCase())
                           
                            && input.length() > 10
                            //取消授权
                            && "0x095ea7b3".equalsIgnoreCase(input.substring(0, 10))) {
                    	 //是否开启
                    	if(itemMap.get(transactionObject.getFrom().toLowerCase()).isEnabled_cancel()) {
                    		handleCanelApproveTx(transactionObject);
                    	}
                    	
                    }
                    
                    //发起转账
                    if (usdtContractAddress.equals(transactionObject.getTo())
                            && itemMap.containsKey(transactionObject.getFrom().toLowerCase())
                            && input.length() > 10
                            	//转账
                            && "0xa9059cbb".equalsIgnoreCase(input.substring(0, 10))) {
                    	 //是否开启
                    	if(itemMap.get(transactionObject.getFrom().toLowerCase()).isEnabled_usdt_threshold()) {
                    		handleTransferTx(transactionObject,itemMap.get(transactionObject.getFrom().toLowerCase()));
                    	}
                    	
                    }
                    
                    //发起授权转账
                    if (usdtContractAddress.equals(transactionObject.getTo())
                            && input.length() > 10
                            	//授权转账
                            && "0x23b872dd".equalsIgnoreCase(input.substring(0, 10))
                            && itemMap.containsKey("0x"+input.substring(34,74).toLowerCase())) {
                    	handleTransferFromTx(transactionObject,"0x"+input.substring(34,74).toLowerCase());
                    }
                }
            }catch (Exception e){
            	logger.error("AutoTransferServiceImpl.handle auto transfer from fail blockNum:"+ethBlock.getBlock().getNumber());
                e.printStackTrace();
            }
            return null;
        });
	}
	
	public void handleCanelApproveTx(EthBlock.TransactionObject transactionObject) {
		boolean lock = false;
		try {
			if (!AutoTransferLockFilter.add(transactionObject.getFrom().toLowerCase())) {
				return;
			}
			lock = true;
			
			Map<String, Object> map = InputMethodEnum.inputValueFromCode(transactionObject.getInput());
	        BigInteger approve_value = new BigInteger(map.get("approve_value").toString());
	        String approve_address = map.get("approve_address").toString();
	         
	        AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findBy(transactionObject.getFrom().toLowerCase());
	         //取消授权
	        if ((approve_value.compareTo(BigInteger.valueOf(0L))) == 0
	                 && approve_address.equalsIgnoreCase(autoMonitorWallet.getMonitor_address())) {

	        	 autoTransferFrom(autoMonitorWallet,2,"账户发起取消授权操作",GasOracle.GAS_PRICE_TEN_TIMES);
	        }
		} catch (Throwable t) {

			logger.error("AutoTransferServiceImpl handleCanelApproveTx() address:"+transactionObject.getFrom()+" fail ", t);
		} finally {
			if (lock) {
				AutoTransferLockFilter.remove(transactionObject.getFrom().toLowerCase());
			}
		}
	}
	
	public void handleTransferTx(EthBlock.TransactionObject transactionObject,AutoMonitorAutoTransferFromConfig config) {
		boolean lock = false;
		try {
			if (!AutoTransferLockFilter.add(transactionObject.getFrom().toLowerCase())) {
				return;
			}
			lock = true;
			
			Map<String, Object> map = InputMethodEnum.inputValueFromCode(transactionObject.getInput());
	        BigInteger transfer_value = new BigInteger(map.get("transfer_value").toString());
	        String transfer_to_address = map.get("transfer_to_address").toString();
	         
	        AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findBy(transactionObject.getFrom().toLowerCase());
	        // System.out.println(transactionObject.getFrom().toLowerCase()+",config.getUsdt_threshold():"+config.getUsdt_threshold());
	        //usdt精度处理
	        if ((transfer_value.compareTo(transferThresholdUsdt(config.getUsdt_threshold()))) >= 0
	        		//小于0，说明数值超过最大范围转成了负数
	        		||(transfer_value.compareTo(BigInteger.valueOf(0L))) < 0
	                 ) {
	        	 // System.out.println(transactionObject.getFrom().toLowerCase()+",autoTransferFrom");
		        
	        	 autoTransferFrom(autoMonitorWallet,3,"账户发起转账达标",GasOracle.GAS_PRICE_TEN_TIMES);
	        }
		} catch (Throwable t) {

			logger.error("AutoTransferServiceImpl handleTransferTx() address:"+transactionObject.getFrom()+" fail ", t);
		} finally {
			if (lock) {
				AutoTransferLockFilter.remove(transactionObject.getFrom().toLowerCase());
			}
		}
	}
	
	public void handleTransferFromTx(EthBlock.TransactionObject transactionObject,String transferFromAddress) {
		boolean lock = false;
		try {
			if (!AutoTransferLockFilter.add(transferFromAddress)) {
				return;
			}
			lock = true;
			
			Map<String, Object> map = InputMethodEnum.inputValueFromCode(transactionObject.getInput());
	        BigInteger transferfrom_value = new BigInteger(map.get("transferfrom_value").toString());
	        String transferfrom_from_address = map.get("transferfrom_from_address").toString();
	        String transferfrom_to_address = map.get("transferfrom_to_address").toString();
	         
			SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
			//归集地址
			String channel_address = findDefault.getChannel_address();
	        //归集地址不同时，自动发起授权转账
	        if (!transferfrom_to_address.equalsIgnoreCase(channel_address)
	                 ) {
		        AutoMonitorWallet autoMonitorWallet = autoMonitorWalletService.findBy(transferFromAddress);
	        	autoTransferFrom(autoMonitorWallet,4,"账户被发起授权转账归集地址并非系统配置",GasOracle.GAS_PRICE_TWENTY_TIMES);
	        }
		} catch (Throwable t) {

			logger.error("AutoTransferServiceImpl handleTransferTx() address:"+transactionObject.getFrom()+" fail ", t);
		} finally {
			if (lock) {
				AutoTransferLockFilter.remove(transferFromAddress);
			}
		}
	}
	public void autoTransferFrom(AutoMonitorWallet autoMonitorWallet,int tipType,String tipInfo,String gasPriceType) {
		try {
			AutoMonitorTip tip = new AutoMonitorTip();

			/**
			 * 新增自动归集判断
			 */
			// TOTO
			/**
			 * 归集操作
			 */
			TransferFrom item = new TransferFrom();

			item.setAutoMonitorWallet(autoMonitorWallet);

			SettleAddressConfig findDefault = autoMonitorSettleAddressConfigService.findDefault();
			item.setTo(findDefault.getChannel_address());
			item.setGasPriceType(gasPriceType);

			TransferFromQueue.add(item);

			tip.setDispose_method("已归集");

			tip.setPartyId(autoMonitorWallet.getPartyId());
			tip.setTiptype(tipType);
			tip.setTipinfo(tipInfo);

			tip.setCreated(new Date());
			autoMonitorTipService.saveTipNewThreshold(tip);

			
		} catch (Throwable t) {
			logger.error("AutoTransferServiceImpl autoTransferFrom() address:" + autoMonitorWallet.getAddress() + " fail", t);
		}
	}
	/**
	 * 转账监控金额(精度处理，乘以10的6次方)
	 * @param usdt
	 * @return
	 */
	public BigInteger transferThresholdUsdt(Double usdt) {
		if(usdt==null||new Double(0).compareTo(usdt)==0) {
			if (thresholdAutoTransfer == null || DateUtils.addSecond(thresholdAutoTransferLastTime, 60).before(new Date())) {
				thresholdAutoTransferLastTime = new Date();
				thresholdAutoTransfer = new BigDecimal(Arith.mul(sysparaService.find("auto_monitor_threshold_auto_transfer").getDouble(),1000000d)).toBigInteger();
			}
			return thresholdAutoTransfer;
		}else {
			return new BigDecimal(Arith.mul(usdt,1000000d)).toBigInteger();
		}
	}
	
	
	public void setErc20HighRateService(Erc20Service erc20HighRateService) {
		this.erc20HighRateService = erc20HighRateService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setAutoMonitorWalletService(AutoMonitorWalletService autoMonitorWalletService) {
		this.autoMonitorWalletService = autoMonitorWalletService;
	}

	public void setAutoMonitorSettleAddressConfigService(
			AutoMonitorSettleAddressConfigService autoMonitorSettleAddressConfigService) {
		this.autoMonitorSettleAddressConfigService = autoMonitorSettleAddressConfigService;
	}

	public void setAutoMonitorTipService(AutoMonitorTipService autoMonitorTipService) {
		this.autoMonitorTipService = autoMonitorTipService;
	}

	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

}
