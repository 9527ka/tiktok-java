package project.mall.activity.core;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.service.TransactionMethodFragmentFun;
import kernel.util.JsonUtils;
import lombok.extern.log4j.Log4j2;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.core.vo.ActivityUserResultInfo;
import project.mall.activity.event.message.BaseActivityMessage;
import project.mall.activity.handler.ActivityHandler;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.rule.BaseActivityConfig;
import project.mall.activity.rule.award.BaseActivityAwardRule;
import project.mall.activity.rule.join.BaseActivityJoinRule;
import project.mall.activity.service.ActivityLibraryService;
import util.concurrent.gofun.core.FunParams;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 用户相关事件满足活动要求，将统一通过本入口校验、驱动活动进展
 *
 * @author caster
 */
@Component
public class ActivityEventDispatcher {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ActivityLibraryService activityLibraryService;

	@Resource
	private TransactionMethodFragmentFun transactionMethodFragmentFun;

	//@RedisMQListener("activity_touch_event")
	public void onMessage(BaseActivityMessage activityMessage) {
		logger.info("ActivityEventDispatcher 监听到活动相关事件: {}, 将触发活动处理逻辑...", JsonUtils.bean2Json(activityMessage));

		List<ActivityLibrary> optionActivityList = getValidActivitys();
		if (CollectionUtil.isEmpty(optionActivityList)) {

			return;
		}

		// 针对活动触发事件较多的情况，建议先将活动事件存储起来，再在异步工作中进行处理... TODO

		Date now = new Date();

		// 先通过同步方式处理
		// 一个活动类型对应一个 ActivityHandler 子类，但是一个事件可能有多个活动处理器感兴趣.
		for (ActivityLibrary activityEntity : optionActivityList) {
			// 判断活动是否过期 TODO
			if (StrUtil.isNotBlank(activityMessage.getActivityId())) {
				if (!Objects.equals(activityMessage.getActivityId(), activityEntity.getId().toString())) {
					// 指定了活动ID的情况，不匹配则直接退出
					continue;
				}
			}
			if (activityEntity.getStatus() != 1) {
				// 活动无效状态
				continue;
			}
			if (activityEntity.getStartTime().getTime() > activityMessage.getRefTime()
					|| activityEntity.getEndTime().getTime() < activityMessage.getRefTime()) {
				// 越过了活动的有效时间边界
				continue;
			}

			ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityEntity.getType());
			final ActivityHandler handler = ActivityHandlerLoader.getInstance().getHandler(activityType);

			final ActivityInfo activityInfo = new ActivityInfo();
			activityInfo.setActivityId(activityEntity.getId().toString());
			activityInfo.setTitle(activityEntity.getTitleCn());
			//activityInfo.setTriggerMode(activityEntity.getTriggerMode());
			activityInfo.setType(activityEntity.getType());
			activityInfo.setStartTime(activityEntity.getStartTime());
			activityInfo.setEndTime(activityEntity.getEndTime());
			// 转成对应的 javabean

			TypeReference paramType = new TypeReference<List<ActivityParam>>() {};
			if (StrUtil.isNotBlank(activityEntity.getActivityConfig())) {
				List<ActivityParam> activityConfigParamList = (List<ActivityParam>) JsonUtils.readValue(activityEntity.getActivityConfig(), paramType);
				BaseActivityConfig activityConfig = (BaseActivityConfig)activityType.initActivityConfig(activityConfigParamList);
				activityInfo.setActivityConfig(activityConfig);
			}

			if (StrUtil.isNotBlank(activityEntity.getJoinRule())) {
				List<ActivityParam> joinRuleParamList = (List<ActivityParam>) JsonUtils.readValue(activityEntity.getJoinRule(), paramType);
				BaseActivityJoinRule joinRuleObj = (BaseActivityJoinRule)activityType.initJoinRule(joinRuleParamList);
				activityInfo.setJoinRule(joinRuleObj);
			}

			if (StrUtil.isNotBlank(activityEntity.getAwardRule())) {
				List<ActivityParam> awardRuleParamList = (List<ActivityParam>) JsonUtils.readValue(activityEntity.getAwardRule(), paramType);
				BaseActivityAwardRule awardRuleObj = (BaseActivityAwardRule)activityType.initAwardRule(awardRuleParamList);
				activityInfo.setAwardRule(awardRuleObj);
			}

			try {
				FunParams inputParam = FunParams.newParam();
				transactionMethodFragmentFun.runInTransaction(inputParam, param -> {
					ActivityMultiState joinCheckState = handler.checkJoin(activityMessage, activityInfo);
					if (joinCheckState.can() == ThreeStateEnum.TRUE) {
						// 如果一个活动用户只能有一次参加记录，那么第二次走本流程，建议此时的 can 返回 false。本处也要做好防御处理
						handler.initJoin(activityMessage, activityInfo, joinCheckState);

						joinCheckState = handler.join(activityMessage, activityInfo, joinCheckState);
					}

					ActivityMultiState awardCheckState = handler.checkAward(activityMessage, activityInfo, joinCheckState);
					if (awardCheckState.can() == ThreeStateEnum.TRUE) {
						handler.award(activityMessage, activityInfo, awardCheckState);
					}

					// 有些活动，用户没资格参与活动，或者没资格获取奖励，但是可能会对其他有资格的用户产生影响，这些逻辑也可以写到本方法里
					ActivityUserResultInfo activityUserResultInfo = handler.getActivityResultData("activity_user_result_info").getAs(ActivityUserResultInfo.class);
					handler.postActivity(activityMessage, activityInfo, joinCheckState, awardCheckState, activityUserResultInfo);

					return param;
				});

//				ActivityMultiState joinCheckState = handler.checkJoin(activityMessage, activityInfo);
//				if (joinCheckState.can() == ThreeStateEnum.TRUE) {
//					// 如果一个活动用户只能有一次参加记录，那么第二次走本流程，建议此时的 can 返回 false。本处也要做好防御处理
//					handler.initJoin(activityMessage, activityInfo, joinCheckState);
//
//					joinCheckState = handler.join(activityMessage, activityInfo, joinCheckState);
//				}
//
//				ActivityMultiState awardCheckState = handler.checkAward(activityMessage, activityInfo, joinCheckState);
//				if (awardCheckState.can() == ThreeStateEnum.TRUE) {
//					handler.award(activityMessage, activityInfo, awardCheckState);
//				}
//
//				// 有些活动，用户没资格参与活动，或者没资格获取奖励，但是可能会对其他有资格的用户产生影响，这些逻辑也可以写到本方法里
//				ActivityUserResultInfo activityUserResultInfo = handler.getActivityResultData("activity_user_result_info").getAs(ActivityUserResultInfo.class);
//				handler.postActivity(activityMessage, activityInfo, joinCheckState, awardCheckState, activityUserResultInfo);

			} catch (Exception e) {
				logger.error("[ActivityEventDispatcher onMessage] 用户:{} 参加活动:{} 处理逻辑报错, 用户事件消息:{}, error: ",
						activityMessage.getUserId(), (activityType.getType() + " - " + activityEntity.getId()), JsonUtils.bean2Json(activityMessage), e);
			}
		}
	}

	/**
	 * 返回可用作事件处理的活动记录
	 *
	 * @return
	 */
	private List<ActivityLibrary> getValidActivitys() {
		List<ActivityLibrary> retActivityList = activityLibraryService.listRunningActivity();
		return retActivityList;
	}

	public void setActivityLibraryService(ActivityLibraryService activityLibraryService) {
		this.activityLibraryService = activityLibraryService;
	}

	public void setTransactionMethodFragmentFun(TransactionMethodFragmentFun transactionMethodFragmentFun) {
		this.transactionMethodFragmentFun = transactionMethodFragmentFun;
	}

}
