package project.mall.activity.event;

import cn.hutool.core.collection.CollectionUtil;
import kernel.util.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import project.blockchain.event.message.RechargeSuccessEvent;
import project.blockchain.event.model.RechargeInfo;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.*;
import project.mall.activity.event.message.ActivityUserRechargeMessage;
import project.mall.activity.event.message.BaseActivityMessage;
import project.mall.activity.handler.FirstRechargeFruitDialActivityHandler;
import project.mall.activity.model.ActivityLibrary;
import project.mall.seller.SellerService;
import project.mall.seller.model.Seller;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 监听用户充值，触发活动
 *
 * @author caster
 */
//@Log4j2 无效
//@Component
public class ActivityUserRechargeListener implements ApplicationListener<RechargeSuccessEvent> {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	private ActivityEventDispatcher activityEventDispatcher;

	private SellerService sellerService;

	private WalletLogService walletLogService;

	@Override
	public void onApplicationEvent(RechargeSuccessEvent event) {
		logger.info("ActivityUserRechargeListener 监听到活动相关事件: {}, 将触发活动处理逻辑...", JsonUtils.bean2Json(event.getRechargeInfo()));

		RechargeInfo rechargeInfo = event.getRechargeInfo();
		Seller sellerEntity = sellerService.getSeller(rechargeInfo.getApplyUserId());
		if (sellerEntity == null) {
			// 非商家用户，充值不参加水果转盘活动
			return;
		}

		WalletLog walletLog = walletLogService.findById(rechargeInfo.getWalletLogId());
		if (walletLog == null) {
			logger.error("ActivityUserRechargeListener 监听到的充值记录: {} 不存在", rechargeInfo.getWalletLogId());
			return;
		}

		ActivityUserRechargeMessage activityMessage = new ActivityUserRechargeMessage();

		Date now = new Date();
		String eventId = rechargeInfo.getWalletLogId() + ":" + now.getTime();
		activityMessage.setEventId(eventId);
		activityMessage.setEventTime(now.getTime());
		activityMessage.setRefTime(walletLog.getCreateTime().getTime());
		activityMessage.setEventType(FirstRechargeFruitDialActivityHandler.ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType());
		activityMessage.setUserId(rechargeInfo.getApplyUserId());
		activityMessage.setTransId(rechargeInfo.getWalletLogId());
		activityMessage.setUsdtAmount(rechargeInfo.getAmount());

		activityEventDispatcher.onMessage(activityMessage);
	}

	public void setActivityEventDispatcher(ActivityEventDispatcher activityEventDispatcher) {
		this.activityEventDispatcher = activityEventDispatcher;
	}

	public void setSellerService(SellerService sellerService) {
		this.sellerService = sellerService;
	}

	public void setWalletLogService(WalletLogService walletLogService) {
		this.walletLogService = walletLogService;
	}

}
