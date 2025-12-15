package project.mall.activity.handler;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.type.TypeReference;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.ActivityInfo;
import project.mall.activity.core.ActivityMultiState;
import project.mall.activity.core.ThreeStateEnum;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.core.vo.ActivityUserResultInfo;
import project.mall.activity.dto.ActivityEditInfoDTO;
import project.mall.activity.dto.ActivityI18nContent;
import project.mall.activity.dto.ActivityPrizeDTO;
import project.mall.activity.event.message.ActivityUserLotteryMessage;
import project.mall.activity.event.message.ActivityUserRechargeMessage;
import project.mall.activity.event.message.BaseActivityMessage;
import project.mall.activity.helper.ActivityRechargeAndLotteryHelper;
import project.mall.activity.model.*;
import project.mall.activity.model.lottery.ActivityUserPoints;
import project.mall.activity.rule.FruitDialActivityConfig;
import project.mall.activity.service.ActivityUserPointsService;
import project.party.PartyService;
import project.party.model.Party;
import project.party.model.UserRecom;
import project.party.recom.UserRecomService;
import project.wallet.WalletLog;
import project.wallet.WalletLogService;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 用户首次登录奖励 10 积分活动
 */
public class FirstRechargeFruitDialActivityHandler extends ActivityHandler {

	private WalletLogService walletLogService;

	private UserRecomService userRecomService;

	private PartyService partyService;

	private ActivityRechargeAndLotteryHelper activityRechargeAndLotteryHelper;

	private ActivityUserPointsService activityUserPointsService;

	@Override
	public ActivityTypeEnum supportActivityType() {
		return ActivityTypeEnum.FRUIT_DIAL_LOTTERY;
	}

	@Override
	public void start(String activityId) {
		// do nothing
	}

	@Override
	public ActivityMultiState checkJoin(BaseActivityMessage activityMessage, ActivityInfo activityInfo) {
		// 首先继承父类通用的判断逻辑，识别能否参加该活动，以及是否已经加入该活动；
		// 如果通用逻辑已经不支持参加活动了，则当前首充抽奖活动没必要继续判断，可直接表明不能加入活动
		ActivityMultiState retState = super.checkJoin(activityMessage, activityInfo);
		if (retState.can() == ThreeStateEnum.FALSE) {
			// 不能参加活动，直接跳过
			return retState;
		}
		if (retState.has() == ThreeStateEnum.TRUE) {
			if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
				// 当前抽奖活动类型下，已经参加过首充奖励活动，直接跳过，否则会导致继续执行下面的 join 方法
				return retState;
			}
		}

		/**
		 * 注意：一个活动处理器可能支持多种事件，未必只支持一种事件！
		 */
		if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
			ActivityUserRechargeMessage rechargeMessage = (ActivityUserRechargeMessage)activityMessage;
			retState.setCan(ThreeStateEnum.FALSE);
			// 临时使用，方便调试：
			//retState.setCan(ThreeStateEnum.TRUE);

			// 首先，检查相对于当前的活动，当前用户的首充是否满足参加活动的要求
			Date limitTime = activityInfo.getStartTime();
			WalletLog firstRechargeLog = walletLogService.getFirstRechargeLogInTimeRange(activityMessage.getUserId(), limitTime);
			// 测试虚拟 TODO
			if (firstRechargeLog == null) {
				firstRechargeLog = new WalletLog();
				firstRechargeLog.setId(rechargeMessage.getTransId());
				firstRechargeLog.setUsdtAmount(rechargeMessage.getUsdtAmount());
				firstRechargeLog.setCreateTime(new Date());
			}

			FruitDialActivityConfig activityConfig = (FruitDialActivityConfig)activityInfo.getActivityConfig();
			double rechargeAmountLimit = activityConfig.getFirstRechargeAmountLimit();
			if (firstRechargeLog.getUsdtAmount() < rechargeAmountLimit) {
				// 不满足首充资金条件，不能参加活动
				//logger.info("[FirstRechargeFruitDialActivityHandler checkJoin] 用户:{} 本次充值金额为:{}, 不满足首充资金限额:{} 条件", activityMessage.getUserId(), firstRechargeLog.getUsdtAmount(), rechargeAmountLimit);
				retState.setDescription("首充金额不满足参加活动的条件");
				return retState;
			}

			// 本块逻辑取代下面的判断逻辑
			ActivityUser rechargeActivityUser = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType());
			if (rechargeActivityUser == null) {
				// 尚未产生充值事件的 activityUser 记录，为兼容错过处理这种特殊场景，此处可强化判断：
				// 识别当前活动奖励门槛规则的最新变更时间，判断是否需要补上奖励
				if (!Objects.equals(rechargeMessage.getTransId(), firstRechargeLog.getId().toString())) {
					// 满足资金条件，但是本次充值事件不是首充
					// 为上次有效首充补上 activityUser 记录，并且替换当前充值事件，让它代替触发后续的奖励流程 TODO
					// 此处不必创建 activityUser 记录，交由正常流程中的 initJoin 方法来处理
					//rechargeActivityUser = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType());

					// 考虑到允许管理员在活动进行期间修改参加活动的条件、奖励等配置，暂时屏蔽以下逻辑，如果放开了以下逻辑，则此处不需要 return
					//rechargeMessage.setEventTime(firstRechargeLog.getCreateTime().getTime());
					//rechargeMessage.setTransId(firstRechargeLog.getId().toString());
					//rechargeMessage.setUsdtAmount(firstRechargeLog.getUsdtAmount());

					logger.warn("[FirstRechargeFruitDialActivityHandler checkJoin] 用户:{} 本次充值金额为:{}, 当前充值事件不是首充", activityMessage.getUserId(), firstRechargeLog.getUsdtAmount());
					return retState;
				}
			} else {
				// 已经产生了首充相关的记录了，直接返回
				logger.warn("[FirstRechargeFruitDialActivityHandler checkJoin] 用户:{} 本次充值金额为:{}, 满足首充资金限额:{} 条件但不是首充", activityMessage.getUserId(), firstRechargeLog.getUsdtAmount(), rechargeAmountLimit);
				return retState;
			}

//			if (!Objects.equals(rechargeMessage.getTransId(), firstRechargeLog.getId().toString())) {
//				// 满足资金条件，但是本次充值事件不是首充
//				logger.warn("[FirstRechargeFruitDialActivityHandler checkJoin] 用户:{} 本次充值金额为:{}, 满足首充资金限额:{} 条件但不是首充", activityMessage.getUserId(), firstRechargeLog.getUsdtAmount(), rechargeAmountLimit);
//				return retState;
//			}

			logger.info("[FirstRechargeFruitDialActivityHandler checkJoin] 用户:{} 本次充值金额为:{}, 满足首充抽奖活动的资金限额:{}", activityMessage.getUserId(), firstRechargeLog.getUsdtAmount(), rechargeAmountLimit);
			retState.can(ThreeStateEnum.TRUE);
			return retState;
		} else {
			// 能参与抽奖的前提是要已经产生了有效的充值记录
			ActivityUser userJoinRecord = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType());
			if (userJoinRecord == null) {
				// 如果不存在首充记录，则必定不能参加抽奖活动
				retState.can(ThreeStateEnum.FALSE);
				retState.setDescription("不满足抽奖资格");
				return retState;
			}

			// 父类方法：super.checkJoin 判断了用户没有超过抽奖次数限制，则直接允许抽奖
			retState.can(ThreeStateEnum.TRUE);
			return retState;
		}
	}

	@Override
	public ActivityMultiState join(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState joinState) {
		/**
		 * 当前用户加入活动的判断逻辑通过了 preJoin 的验证，允许用户参加首次登录奖励的活动，此处仅处理用户加入活动的逻辑；
		 * 当前业务特征比较简单，直接录入相关的活动记录即可
		 */
		if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
			// 当前是首充事件，初始化积分奖励将放到 award 方法里执行
			ActivityUserRechargeMessage rechargeMessage = (ActivityUserRechargeMessage) activityMessage;
		} else {
			// 抽奖事件，奖励逻辑写到了 award 方法里
		}

		// 填充 activityUser 记录里的一些辅助业务字段
		ActivityUser activityUser = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());
		if (activityUser.getUserRegistTime().longValue() == 0) {
			// 尚未填充用户相关的辅助信息
			Party party = this.partyService.getById(activityMessage.getUserId());
			activityUser.setUserType(1);
			activityUser.setUserRegistTime(party.getCreateTime().getTime());
			activityUserService.save(activityUser);
		}

		return joinState;
	}

	@Override
	public ActivityMultiState checkAward(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState joinState) {
		ActivityMultiState retState = super.checkAward(activityMessage, activityInfo, joinState);
		if (retState.can() == ThreeStateEnum.FALSE) {
			// 如果明确判断不允许获取奖励，则跳过奖励
			return retState;
		}
		if (joinState.has() == ThreeStateEnum.TRUE) {
			if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
				// 如果判断首充事件不是第一次发生，则不重复给与奖励
				retState.setCan(ThreeStateEnum.FALSE);
				retState.setHas(ThreeStateEnum.TRUE);
				retState.setDescription("已领取过首充积分奖励");
				return retState;
			}
		}

		// 判断是否存在 activityUser 记录，如果不存在对应的 activityUser 记录，则该次事件不能触发奖励
		ActivityUser userFirstRechargeRecord = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType());
		if (userFirstRechargeRecord == null) {
			// 没有充值事件记录，代表当前用户没有资格获取奖励，因为当前业务特征是：首充金额满足条件才能参加活动
			retState.setCan(ThreeStateEnum.FALSE);
			retState.setDescription("不满足首充条件");
			return retState;
		}

		// 过了参加活动资格判断，下面根据不同的业务场景处理不同的奖励判断
		if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
			// 当前用户首充条件满足，但是本次充值未必是本次活动的第一次满足条件的首充事件，其他时间的充值事件不能给与奖励
			// 只有首充才给奖励
			if (joinState.getCan() == ThreeStateEnum.TRUE) {
				retState.setCan(ThreeStateEnum.TRUE);
				return retState;
			} else {
				retState.setCan(ThreeStateEnum.FALSE);
				retState.setDescription("首充金额不满足参加活动的条件");
				return retState;
			}
		} else if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.LOTTERY.getEventType())) {
			// 抽奖事件，因为前面已经判断了存在有效的首充记录，再，复用 join 操作中的 joinState 状态加强判断
			//
			if (joinState.can() == ThreeStateEnum.FALSE) {
				// 本次抽奖事件明确无资格参加，则直接不给奖励
				retState.setCan(ThreeStateEnum.FALSE);
				retState.setDescription(joinState.getDescription());
			} else {
				// 其他情况直接允许给奖励
				retState.setCan(ThreeStateEnum.TRUE);
			}
		}

		return retState;
	}

	@Override
	public void award(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState checkAwardState) {
		if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
			// 发放首充奖励，自己的积分奖励 TODO
			ActivityUserRechargeMessage rechargeMessage = (ActivityUserRechargeMessage) activityMessage;
			FruitDialActivityConfig activityConfig = (FruitDialActivityConfig)activityInfo.getActivityConfig();
			int initActivityScore = activityConfig.getInitLotteryScore();

			ActivityUserPoints userPoints = activityUserPointsService.saveOrGetUserPoints(activityInfo.getType(), activityInfo.getActivityId(), activityMessage.getUserId());
			activityUserPointsService.updatePoints(userPoints.getId().toString(), initActivityScore);
		} else if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.LOTTERY.getEventType())) {
			// 发放抽奖奖励
			ActivityUserLotteryMessage lotteryMessage = (ActivityUserLotteryMessage)activityMessage;
			// 抽奖逻辑写到这里

			String loginPartyId = lotteryMessage.getUserId();
//			// 抽奖结果写到具体活动的相关辅助表里
//			ActivityPrize defaultPrize = new ActivityPrize();
//			defaultPrize.setId("0");
//			defaultPrize.setStatus(1);
//			defaultPrize.setPrizeNameEn("Thank you join activity");
//			defaultPrize.setPrizeNameCn("谢谢惠顾");
//			defaultPrize.setPrizeType(1);
//			defaultPrize.setPrizeAmount(0.0);
//			defaultPrize.setOdds(100.0F);
//			defaultPrize.setActivityId(activityInfo.getActivityId());
			List<ActivityPrize> drawedPrizeList = activityRechargeAndLotteryHelper.draw(lotteryMessage.getActivityId(),
					loginPartyId, lotteryMessage.getLang(), lotteryMessage.getBatchJoinTimes(), null);

			// 写成常量好维护些
			cacheActivityResultData("lottery_activity_drawed_prizes", drawedPrizeList);
		}
	}

	/**
	 * 此处可填写当前尝试参与活动的一段处理流程之后的收尾处理逻辑，此处可放置具体业务模式下的个性化逻辑，包括但不限于：
	 * 1. 修改 activityUserLog 记录的 status 状态
	 * 2. 填充 activityUserLog 里的 extraInfo 信息（可从 ACTIVITY_RESULT_DATA_CONTEXT 上下文里提取结果数据做额外支撑）
	 * 3. 根据具体业务，决定是否更新 activityUser 记录的 status 状态
	 * 4. 有些活动，用户没资格参与活动，或者没资格获取奖励，但是可能会对其他有资格的用户产生影响，这些逻辑也可以写到本方法里
	 * 5. 其他
	 *
	 * @param activityMessage
	 * @param activityInfo
	 * @param joinState
	 */
	@Override
	public void postActivity(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState joinState, ActivityMultiState awardState, ActivityUserResultInfo activityUserResultInfo) {
		if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
			// 提取当前活动用户的上级推荐人信息
			// 充值事件才会影响上级用户
			String parentPartyId = "";
			UserRecom firstRecom = userRecomService.findByPartyId(activityMessage.getUserId());
			if (firstRecom != null) {
				Party parentParty = this.partyService.getById(firstRecom.getReco_id().toString());
				if (parentParty != null) {
					parentPartyId = parentParty.getId().toString();
				}
			}

			if (StrUtil.isBlank(parentPartyId)) {
				// 没有上级用户，则无需处理给参加活动的上级用户充积分的奖励

			} else {
				// 有上级用户，需要判断上级用户是否已经参加了活动，如果上级用户未参加活动，则错过本次奖励
				ActivityUser parentUserRechargeRecord = activityUserService.getActivityUser(activityInfo.getActivityId(), parentPartyId, ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType());
				if (parentUserRechargeRecord == null) {
					// 上级用户未参加首充抽奖活动，则会错过本奖励
				} else {
					// 判断当前用户的充值是否满足给上级用户增加积分奖励的条件
					ActivityUserRechargeMessage rechargeMessage = (ActivityUserRechargeMessage)activityMessage;
					double rechargeAmount = rechargeMessage.getUsdtAmount();

					FruitDialActivityConfig activityConfig = (FruitDialActivityConfig)activityInfo.getActivityConfig();
					double rechargeAmountLimit = activityConfig.getFirstRechargeAmountLimit();
					if (rechargeAmount >= rechargeAmountLimit) {
						// 当前用户充值金额满足临界值，则可以给上级用户增加积分
						logger.info("[FirstRechargeFruitDialActivityHandler postActivity] 用户:{} 本次充值金额为:{}, 满足给上级用户增加积分的资金限额:{} 条件", activityMessage.getUserId(), rechargeAmount, rechargeAmountLimit);
						int inviteAwardScore = activityConfig.getInviteAwardScore();

						// 给上级用户奖励活动积分
						ActivityUserPoints userPoints = activityUserPointsService.saveOrGetUserPoints(activityInfo.getType(), activityInfo.getActivityId(), parentPartyId);
						activityUserPointsService.updatePoints(userPoints.getId().toString(), inviteAwardScore);
					}
				}
			}
		}

		ActivityUserJoinLog lastLog = activityUserJoinLogService.lastJoinLog(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());
		if (lastLog != null) {
			// 没资格参加活动的用户不会产生 ActivityUserJoinLog 记录
			lastLog.setStatus(2);
			lastLog.setFinishTime(System.currentTimeMillis());
			if (activityMessage.getEventType().equalsIgnoreCase(ActivityTouchEventTypeEnum.USER_RECHARGE.getEventType())) {
				// 充值
				ActivityUserRechargeMessage rechargeMessage = (ActivityUserRechargeMessage)activityMessage;
				lastLog.setRefType(1);
				lastLog.setRefId(rechargeMessage.getTransId());
			} else {
				// 抽奖事件才有的返回值
				List<ActivityPrize> lotteryPrizeList = (List<ActivityPrize>) getActivityResultData("lottery_activity_drawed_prizes").getValue();
				Map<String, Object> logExtraInfo = new HashMap<>();
				if (CollectionUtil.isNotEmpty(lotteryPrizeList)) {
					StringBuffer prizeIdBuf = new StringBuffer();
					for (ActivityPrize onePrize : lotteryPrizeList) {
						prizeIdBuf.append(onePrize.getId().toString()).append(",");
					}
					prizeIdBuf.deleteCharAt(prizeIdBuf.length() - 1);

					logExtraInfo.put("prizeIds", prizeIdBuf.toString());
				}

				lastLog.setExtraInfo(JsonUtils.bean2Json(logExtraInfo));
			}

			activityUserJoinLogService.save(lastLog);
		}

		// 更新 activityUser 字段
		ActivityUser currentActivityUserRecord = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());
		if (currentActivityUserRecord != null) {
			// 无论是充值事件，还是抽奖事件，都是一次性耗时短的事件，每次事件完成都可以更新状态
			currentActivityUserRecord.setStatus(1);
			currentActivityUserRecord.setLastTriggerTime(System.currentTimeMillis());
			activityUserService.save(currentActivityUserRecord);
		}
	}

	@Override
	public void stop(String activityId) {

	}

	@Override
	public ActivityLibrary saveActivity(ActivityEditInfoDTO activityInfo) {
		ActivityLibrary existActivityLibrary = null;
		//String activityCode = activityInfo.getActivityCode();
		if (StrUtil.isNotBlank(activityInfo.getId()) && !Objects.equals(activityInfo.getId(), "0")) {
			existActivityLibrary = activityLibraryService.findById(activityInfo.getId());
			if (existActivityLibrary == null) {
				throw new BusinessException("不存在的活动");
			}
			//activityCode = existActivityLibrary.getActivityCode();
		}
		
		if (StrUtil.isBlank(activityInfo.getTemplateId())) {
			throw new BusinessException("未指定活动类型");
		}
		ActivityTemplate activityTemplate = activityTemplateService.getById(activityInfo.getTemplateId());
		if (activityTemplate == null) {
			throw new BusinessException("该类型的活动已下线");
		}

		boolean isNewActivity = false;
		if (existActivityLibrary == null) {
			isNewActivity = true;
			existActivityLibrary = new ActivityLibrary();
			existActivityLibrary.setTemplateId(activityTemplate.getId().toString());
			existActivityLibrary.setType(activityTemplate.getType());
			existActivityLibrary.setLocation(99999);
			existActivityLibrary.setIsShow(0);
		}
		//existActivityLibrary.setActivityCode(activityCode);
		existActivityLibrary.setLastOperator(activityInfo.getCreateBy());
		existActivityLibrary.setDeleted(0);

		TypeReference paramType = new TypeReference<List<ActivityParam>>() {};
		List<ActivityParam> activityTemplateConfigInfoList = (List<ActivityParam>)JsonUtils.readValue(activityTemplate.getActivityConfig(), paramType);
		Map<String, ActivityParam> activityConfigAttrMap = new HashMap<>();
		for (ActivityParam oneConfig : activityTemplateConfigInfoList) {
			activityConfigAttrMap.put(oneConfig.getCode(), oneConfig);
		}

		//ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityTemplate.getType());
		List<ActivityParam> lotteryConfigList = activityInfo.getActivityConfigInfos();
		if (CollectionUtil.isNotEmpty(lotteryConfigList)) {
			for (ActivityParam oneParam : lotteryConfigList) {
				ActivityParam templateConfig = activityConfigAttrMap.get(oneParam.getCode());
				if (templateConfig == null) {
					logger.error("当前提交的活动配置参数:{} 在活动模板中不存在", oneParam.getCode());
					continue;
				}
				oneParam.setTitle(templateConfig.getTitle());
				oneParam.setDescription(templateConfig.getDescription());
			}
		}

		existActivityLibrary.setActivityConfig(JsonUtils.bean2Json(lotteryConfigList));

		existActivityLibrary.setStatus(activityInfo.getStatus());
		// 程序生成 detail 规则？ TODO
		//existActivityLibrary.setDetailUrl();
		existActivityLibrary.setImageUrl(activityInfo.getImageUrl());
		existActivityLibrary.setAllowJoinTimes(0);
		existActivityLibrary.setStartTime(DateUtil.parseDateTime(activityInfo.getStartTime()));
		existActivityLibrary.setEndTime(DateUtil.parseDateTime(activityInfo.getEndTime()));

		List<ActivityI18nContent> i18nContents = activityInfo.getI18nContents();
		if (CollectionUtil.isNotEmpty(i18nContents)) {
			for (ActivityI18nContent oneLangContent : i18nContents) {
				if (oneLangContent.getLang().equalsIgnoreCase("cn")) {
					existActivityLibrary.setTitleCn(oneLangContent.getTitle());
					existActivityLibrary.setDescriptionCn(oneLangContent.getDescription());
				} else if (oneLangContent.getLang().equalsIgnoreCase("en")) {
					existActivityLibrary.setTitleEn(oneLangContent.getTitle());
					existActivityLibrary.setDescriptionEn(oneLangContent.getDescription());
				}
			}
		}

		activityLibraryService.saveActivity(existActivityLibrary);
		if (isNewActivity) {
			// 设置活动详情地址
			activityLibraryService.updateLotteryActivityUrl(existActivityLibrary.getId().toString());
		}

		// 提取所有的配置的奖品记录（包括废弃了的历史记录）
		List<ActivityPrize> oriPrizeList = activityPrizeService.listByActivityId(existActivityLibrary.getId().toString(), -1);
		Map<String, ActivityPrize> oriPrizeMap = oriPrizeList.stream()
				.collect(Collectors.toMap(entity -> entity.getPoolId(), Function.identity(), (key1, key2) -> key2));
        // 本地奖品变更，旧奖品保持下来的 id 集合
		Map<String, String> keepedPrizeIdMap = new HashMap();

		List<String> prizePoolIdList = null;
		List<ActivityPrizeDTO> prizeList = activityInfo.getPrizeList();
		if (CollectionUtil.isNotEmpty(prizeList)) {
			prizePoolIdList = prizeList.stream().map(prize -> prize.getPoolId()).collect(Collectors.toList());
		}
		List<ActivityPrizePool> prizePoolEntityList = null;
		if (CollectionUtil.isNotEmpty(prizePoolIdList)) {
			prizePoolEntityList = activityPrizePoolService.listByIds(prizePoolIdList);
		}
		Map<String, ActivityPrizePool> prizePoolMap = new HashMap();
		if (CollectionUtil.isNotEmpty(prizePoolEntityList)) {
			for (ActivityPrizePool onePrizePool : prizePoolEntityList) {
				prizePoolMap.put(onePrizePool.getId().toString(), onePrizePool);
			}
		}

		// 如果提交的 prizeList 集合为空，则代表历史奖品记录都要废弃
		if (CollectionUtils.isNotEmpty(activityInfo.getPrizeList())) {
//			double totalOdds = 0.0;
			for (ActivityPrizeDTO onePrizeDto : activityInfo.getPrizeList()) {
//				if (onePrizeDto.getOdds() < 0) {
//					throw new BusinessException("中奖几率不能小于 0");
//				}
//				totalOdds = totalOdds + onePrizeDto.getOdds();
//				if (totalOdds > 1.0) {
//					throw new BusinessException("累计中奖几率不能大于 1");
//				}
				if (StrUtil.isBlank(onePrizeDto.getPoolId()) || Objects.equals(onePrizeDto.getPoolId(), "0")) {
					// 防御处理，如果提交了类似：谢谢惠顾 这种记录不存在的奖品，则跳过
					continue;
				}

				ActivityPrizePool prizePool = prizePoolMap.get(onePrizeDto.getPoolId());
				if (prizePool == null || prizePool.getStatus() != 1) {
					// 考虑到创建活动后会复制一份奖品池的记录，允许活动奖品脱离奖品池中的原始记录（奖品池记录可能会被删除），此处允许活动奖品在
					// 奖品池中找不到对应的记录
					ActivityPrize oldActivityPrize = oriPrizeMap.get(onePrizeDto.getPoolId());
					if (oldActivityPrize == null || oldActivityPrize.getStatus() != 1) {
						// 不存在有效的旧记录
						throw new BusinessException("奖品不存在");
					}
				}

				ActivityPrize onePrizeEntity = null;
				ActivityPrize oriPrizeEntity = oriPrizeMap.get(onePrizeDto.getPoolId());
				if (oriPrizeEntity == null) {
					// 新增的奖品
					// 基于前面的校验过滤逻辑，此处必定存在 prizePool 记录
					onePrizeEntity = new ActivityPrize();
					onePrizeEntity.setActivityId(existActivityLibrary.getId().toString());
					// 严谨做法：需要判断奖品池记录是否存在
					onePrizeEntity.setPoolId(onePrizeDto.getPoolId());
					onePrizeEntity.setStatus(1);
					onePrizeEntity.setImage(prizePool.getImage());
					onePrizeEntity.setMaxQuantity(onePrizeDto.getMaxQuantity());
					onePrizeEntity.setLeftQuantity(onePrizeDto.getMaxQuantity());
					onePrizeEntity.setOdds(onePrizeDto.getOdds()); // 不能为负值
					onePrizeEntity.setPrizeAmount(prizePool.getPrizeAmount());
					onePrizeEntity.setPrizeNameCn(prizePool.getPrizeNameCn());
					onePrizeEntity.setPrizeNameEn(prizePool.getPrizeNameEn());
					onePrizeEntity.setPrizeType(prizePool.getPrizeType());
					onePrizeEntity.setRemark(prizePool.getRemark());
					onePrizeEntity.setCreateBy(activityInfo.getCreateBy());
					onePrizeEntity.setDefaultPrize(0);
				} else {
					// 保持的旧奖品记录，至于要不要用对应的奖品池记录的最新奖品值刷新活动奖品属性，看业务，暂时是奖品最新属性值随着奖品池记录的最新配置刷新
					keepedPrizeIdMap.put(oriPrizeEntity.getPoolId(), oriPrizeEntity.getId().toString());

					onePrizeEntity = oriPrizeEntity;
					onePrizeEntity.setStatus(1);

					if (prizePool != null) {
						onePrizeEntity.setImage(prizePool.getImage());
						onePrizeEntity.setPrizeNameCn(prizePool.getPrizeNameCn());
						onePrizeEntity.setPrizeNameEn(prizePool.getPrizeNameEn());
						onePrizeEntity.setPrizeType(prizePool.getPrizeType());
						onePrizeEntity.setRemark(prizePool.getRemark());
						onePrizeEntity.setPrizeAmount(prizePool.getPrizeAmount());
					} else {
						// 根据 oriPrizeEntity 来填充
					}
					onePrizeEntity.setOdds(onePrizeDto.getOdds()); // 不能为负值
					onePrizeEntity.setCreateBy(activityInfo.getCreateBy());
				}

				activityPrizeService.save(onePrizeEntity);
			}
		}

		// 清理被替换掉的奖品
		for (String oneOldPoolId : oriPrizeMap.keySet()) {
			if (keepedPrizeIdMap.containsKey(oneOldPoolId)) {
				// 该奖品被保持了
			} else {
				// 该奖品被清除了，修改状态
				ActivityPrize invalidPrize = oriPrizeMap.get(oneOldPoolId);
				invalidPrize.setStatus(0);
				activityPrizeService.save(invalidPrize);
			}
		}

		return existActivityLibrary;
	}

	/**
	 * 对管理员展示活动信息
	 *
	 * @param activityId
	 * @return
	 */
	@Override
	public ActivityEditInfoDTO getActivityDetail(String activityId, String lang) {
		if (StrUtil.isBlank(activityId) || Objects.equals(activityId, "0")) {
			throw new BusinessException("未指定活动");
		}
		if (StrUtil.isBlank(lang)) {
			lang = "cn";
		}

		ActivityLibrary existActivityLibrary = activityLibraryService.findById(activityId);
		if (existActivityLibrary == null) {
			throw new BusinessException("不存在的活动");
		}
		ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(existActivityLibrary.getType());

		String lotteryConfigJson = existActivityLibrary.getActivityConfig();

		TypeReference paramType = new TypeReference<List<ActivityParam>>() {};
		List<ActivityParam> lotteryConfigList = (List<ActivityParam>)JsonUtils.readValue(lotteryConfigJson, paramType);

		ActivityEditInfoDTO retDto = new ActivityEditInfoDTO();
		retDto.setId(existActivityLibrary.getId().toString());
		retDto.setType(existActivityLibrary.getType());
		retDto.setActivityConfigInfos(lotteryConfigList);

		retDto.setStatus(existActivityLibrary.getStatus());
		retDto.setActivityConfigInfos(lotteryConfigList);
		retDto.setDetailUrl(existActivityLibrary.getDetailUrl());
		retDto.setImageUrl(existActivityLibrary.getImageUrl());
		retDto.setStartTime(DateUtil.formatDateTime(existActivityLibrary.getStartTime()));
		retDto.setEndTime(DateUtil.formatDateTime(existActivityLibrary.getEndTime()));
		retDto.setTemplateId(existActivityLibrary.getTemplateId());
		//retDto.setActivityCode(existActivityLibrary.getActivityCode());

		List<ActivityI18nContent> i18nContents = new ArrayList();
		retDto.setI18nContents(i18nContents);

		ActivityI18nContent i18nContent1 = new ActivityI18nContent();
		ActivityI18nContent i18nContent2 = new ActivityI18nContent();

		i18nContent1.setLang("cn");
		i18nContent1.setTitle(existActivityLibrary.getTitleCn());
		i18nContent1.setDescription(existActivityLibrary.getDescriptionCn());

		i18nContent2.setLang("en");
		i18nContent2.setTitle(existActivityLibrary.getTitleEn());
		i18nContent2.setDescription(existActivityLibrary.getDescriptionEn());

		i18nContents.add(i18nContent1);
		i18nContents.add(i18nContent2);

		List<ActivityPrize> activityPrizeList = activityPrizeService.listByActivityId(existActivityLibrary.getId().toString(), 1);
		if (CollectionUtils.isNotEmpty(activityPrizeList)) {
			retDto.setPrizeList(new ArrayList<>());

//			// 增加一个谢谢惠顾虚拟奖品记录
//			ActivityPrizeDTO mockPrizeDto = new ActivityPrizeDTO();
//			retDto.getPrizeList().add(mockPrizeDto);
//			mockPrizeDto.setOdds(0.0F);
//			mockPrizeDto.setId("0");
//			mockPrizeDto.setDefaultPrize(1);
//			mockPrizeDto.setImage("");
//			mockPrizeDto.setMaxQuantity(0);
//			mockPrizeDto.setPoolId("0");
//			mockPrizeDto.setPrizeType(2);
//			mockPrizeDto.setPrizeAmount(0.0);
//			if (lang.equals("cn")) {
//				mockPrizeDto.setPrizeName("谢谢惠顾");
//			} else {
//				mockPrizeDto.setPrizeName("Thanks");
//			}

			for (ActivityPrize onePrizeEntity : activityPrizeList) {
				ActivityPrizeDTO onePrizeDto = new ActivityPrizeDTO();
				onePrizeDto.setId(onePrizeEntity.getId().toString());
				// 严谨做法：需要判断奖品池记录是否存在
				onePrizeDto.setPoolId(onePrizeEntity.getPoolId());
				onePrizeDto.setMaxQuantity(onePrizeEntity.getMaxQuantity());
				onePrizeDto.setPrizeAmount(onePrizeEntity.getPrizeAmount());
				onePrizeDto.setPrizeName(onePrizeEntity.getPrizeNameCn());
				onePrizeDto.setDefaultPrize(onePrizeEntity.getDefaultPrize());
				onePrizeDto.setPrizeType(onePrizeEntity.getPrizeType());
				onePrizeDto.setImage(onePrizeEntity.getImage());
				onePrizeDto.setOdds(onePrizeEntity.getOdds());
				retDto.getPrizeList().add(onePrizeDto);
			}
		}

		return retDto;
	}

	@Override
	public ActivityEditInfoDTO loadActivityTemplate(String templateId) {
		if (StrUtil.isBlank(templateId) || Objects.equals(templateId, "0")) {
			throw new BusinessException("未指定活动");
		}

		ActivityTemplate existActivityTemplate = activityTemplateService.getById(templateId);
		if (existActivityTemplate == null) {
			throw new BusinessException("不存在的活动");
		}
		ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(existActivityTemplate.getType());

		String lotteryConfigJson = existActivityTemplate.getActivityConfig();

		TypeReference paramType = new TypeReference<List<ActivityParam>>() {};
		List<ActivityParam> lotteryConfigList = (List<ActivityParam>)JsonUtils.readValue(lotteryConfigJson, paramType);

		ActivityEditInfoDTO retDto = new ActivityEditInfoDTO();
		retDto.setId("");
		retDto.setType(existActivityTemplate.getType());
		retDto.setActivityConfigInfos(lotteryConfigList);

		retDto.setTemplateId(existActivityTemplate.getId().toString());
		retDto.setStatus(existActivityTemplate.getStatus());
		retDto.setActivityConfigInfos(lotteryConfigList);
		retDto.setImageUrl("");
		retDto.setTitle("");

		ActivityPrizePool defaultPrize = null;
		List<ActivityPrizePool> defaultPrizeList = activityPrizePoolService.listDefaltPrize(1, 1);
		if (CollectionUtil.isNotEmpty(defaultPrizeList)) {
			defaultPrize = defaultPrizeList.get(0);
		}

		int prizeCount = 9;
		if (defaultPrize != null) {
			prizeCount = 8;
		}
		List<ActivityPrizePool> prizeList0 = activityPrizePoolService.listLotteryPrize(prizeCount);
		List<ActivityPrizePool> prizeList = new ArrayList<>();
		if (defaultPrize != null) {
			prizeList.add(defaultPrize);
		}
		prizeList.addAll(prizeList0);
		if (CollectionUtils.isNotEmpty(prizeList)) {
			retDto.setPrizeList(new ArrayList<>());

			for (ActivityPrizePool prizeObj : prizeList) {
				ActivityPrizeDTO onePrizeDto = new ActivityPrizeDTO();
				onePrizeDto.setId("");
				// 严谨做法：需要判断奖品池记录是否存在
				onePrizeDto.setPoolId(prizeObj.getId().toString());
				onePrizeDto.setMaxQuantity(0);
				onePrizeDto.setOdds(BigDecimal.ZERO); // 不能为负值
				onePrizeDto.setPrizeAmount(prizeObj.getPrizeAmount());
				onePrizeDto.setPrizeName(StrUtil.isBlank(prizeObj.getPrizeNameCn()) ? prizeObj.getPrizeNameEn() : prizeObj.getPrizeNameCn());
				onePrizeDto.setPrizeType(prizeObj.getPrizeType());

				retDto.getPrizeList().add(onePrizeDto);
			}
		}

		return retDto;
	}

	/**
	 * 当前活动内置支持的事件枚举
	 *
	 */
	public static enum ActivityTouchEventTypeEnum {
		USER_RECHARGE("user_recharge", ActivityUserRechargeMessage.class, "用户充值"),
		LOTTERY("lottery", ActivityUserLotteryMessage.class, "抽奖"),

		;

		private String eventType;
		private Class<? extends BaseActivityMessage> eventInfoClazz;
		private String description;

		private ActivityTouchEventTypeEnum(String eventType, Class<? extends BaseActivityMessage> eventInfoClazz, String description) {
			this.eventType = eventType;
			this.eventInfoClazz = eventInfoClazz;
			this.description = description;
		}

		public static ActivityTouchEventTypeEnum typeOf(String eventType) {
			if (StrUtil.isBlank(eventType)) {
				return null;
			}

			ActivityTouchEventTypeEnum values[] = ActivityTouchEventTypeEnum.values();
			for (ActivityTouchEventTypeEnum one : values) {
				if (one.getEventType().equalsIgnoreCase(eventType)) {
					return one;
				}
			}

			return null;
		}

		public String getEventType() {
			return eventType;
		}

		public Class<? extends BaseActivityMessage> getEventInfoClazz() {
			return eventInfoClazz;
		}

		public String getDescription() {
			return description;
		}
	}

	public void setWalletLogService(WalletLogService walletLogService) {
		this.walletLogService = walletLogService;
	}

	public void setUserRecomService(UserRecomService userRecomService) {
		this.userRecomService = userRecomService;
	}

	public void setPartyService(PartyService partyService) {
		this.partyService = partyService;
	}

	public void setActivityRechargeAndLotteryHelper(ActivityRechargeAndLotteryHelper activityLotteryHelper) {
		this.activityRechargeAndLotteryHelper = activityLotteryHelper;
	}

	public void setActivityUserPointsService(ActivityUserPointsService activityUserPointsService) {
		this.activityUserPointsService = activityUserPointsService;
	}

}
