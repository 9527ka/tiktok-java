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
import project.mall.activity.event.message.BaseActivityMessage;
import project.mall.activity.helper.ActivityRechargeAndLotteryHelper;
import project.mall.activity.helper.ActivitySimpleLotteryHelper;
import project.mall.activity.model.*;
import project.mall.activity.service.ActivityUserPointsService;
import project.party.PartyService;
import project.party.recom.UserRecomService;
import project.wallet.WalletLogService;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 普通的抽奖活动，该活动为全局单例活动，长期有效，仅仅有一条活动记录
 *
 */
public class SimpleLotteryActivityHandler extends ActivityHandler {

	private WalletLogService walletLogService;

	private UserRecomService userRecomService;

	private PartyService partyService;

	private ActivitySimpleLotteryHelper activitySimpleLotteryHelper;

	private ActivityUserPointsService activityUserPointsService;

	@Override
	public ActivityTypeEnum supportActivityType() {
		return ActivityTypeEnum.SIMPLE_LOTTERY;
	}

	@Override
	public void start(String activityId) {
		// do nothing
	}

	/**
	 * 判断当前事件下的用户是否能参加该活动
	 *
	 * @param activityMessage
	 * @param activityInfo
	 * @return
	 */
	@Override
	public ActivityMultiState checkJoin(BaseActivityMessage activityMessage, ActivityInfo activityInfo) {
		// 首先继承父类通用的判断逻辑，识别能否参加该活动，以及是否已经加入该活动；
		// 如果通用逻辑已经不支持参加活动了，则当前首充抽奖活动没必要继续判断，可直接表明不能加入活动
		ActivityMultiState retState = super.checkJoin(activityMessage, activityInfo);
		if (retState.can() == ThreeStateEnum.FALSE) {
			// 不能参加活动，直接跳过
			return retState;
		}
		if (!activityMessage.getEventType().equals(ActivityTouchEventTypeEnum.LOTTERY.getEventType())) {
			retState.can(ThreeStateEnum.FALSE);
			return retState;
		}

		/**
		 * 只要在活动期间，都允许抽奖，没有其他抽奖资格限制
		 */
		// 父类方法：super.checkJoin 判断了用户没有超过抽奖次数限制，则直接允许抽奖
		retState.can(ThreeStateEnum.TRUE);
		return retState;
	}

	@Override
	public ActivityMultiState join(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState joinState) {
		// initJoin 方法里已经产生了一条 activityUser 记录，此处根据具体业务看看是否还需要补充该记录的某些字段值
		ActivityUser activityUser = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());

		return joinState;
	}

	@Override
	public ActivityMultiState checkAward(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState joinState) {
		ActivityMultiState retState = super.checkAward(activityMessage, activityInfo, joinState);
		if (retState.can() == ThreeStateEnum.FALSE) {
			// 如果明确判断不允许获取奖励，则跳过奖励
			return retState;
		}

		// 能够参与抽奖的，都允许立即计算奖品（转盘奖品）
		// 抽奖事件，因为前面已经判断了当前用户是否允许参与抽奖，复用 join 操作中的 joinState 状态加强判断
		if (joinState.can() == ThreeStateEnum.FALSE) {
			// 本次抽奖事件明确无资格参加，则直接不给奖励
			retState.setCan(ThreeStateEnum.FALSE);
			retState.setDescription(joinState.getDescription());
		} else {
			// 其他情况直接允许给奖励
			retState.setCan(ThreeStateEnum.TRUE);
		}

		return retState;
	}

	@Override
	public void award(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState checkAwardState) {
		// 发放抽奖奖励
		ActivityUserLotteryMessage lotteryMessage = (ActivityUserLotteryMessage)activityMessage;

		// 抽奖逻辑写到这里
		String loginPartyId = lotteryMessage.getUserId();
		List<ActivityPrize> drawedPrizeList = activitySimpleLotteryHelper.draw(lotteryMessage.getActivityId(),
				loginPartyId, lotteryMessage.getLang(), lotteryMessage.getBatchJoinTimes(), null);

		// 写成常量好维护些
		cacheActivityResultData("lottery_activity_drawed_prizes", drawedPrizeList);
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
		ActivityUserJoinLog lastLog = activityUserJoinLogService.lastJoinLog(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());
		if (lastLog != null) {
			// 没资格参加活动的用户不会产生 ActivityUserJoinLog 记录
			lastLog.setStatus(2);
			lastLog.setFinishTime(System.currentTimeMillis());
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
		if (StrUtil.isBlank(activityInfo.getTemplateId())) {
			throw new BusinessException("未指定活动类型");
		}

		ActivityLibrary existActivityLibrary = null;
		//String activityCode = activityInfo.getActivityCode();
		if (StrUtil.isNotBlank(activityInfo.getId()) && !Objects.equals(activityInfo.getId(), "0")) {
			existActivityLibrary = activityLibraryService.findById(activityInfo.getId());
			if (existActivityLibrary == null) {
				throw new BusinessException("不存在的活动");
			}
		} else {
//			existActivityLibrary = activityLibraryService.findByTemplate(activityInfo.getTemplateId());
//			if (existActivityLibrary != null && existActivityLibrary.getDeleted() == 0) {
//				throw new BusinessException("该类型活动只能单例存在");
//			}
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
				if (lang.equals("cn")) {
					onePrizeDto.setPrizeName(onePrizeEntity.getPrizeNameCn());
				} else {
					onePrizeDto.setPrizeName(onePrizeEntity.getPrizeNameEn());
				}
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

	public void setActivitySimpleLotteryHelper(ActivitySimpleLotteryHelper activityLotteryHelper) {
		this.activitySimpleLotteryHelper = activityLotteryHelper;
	}

	public void setActivityUserPointsService(ActivityUserPointsService activityUserPointsService) {
		this.activityUserPointsService = activityUserPointsService;
	}

}
