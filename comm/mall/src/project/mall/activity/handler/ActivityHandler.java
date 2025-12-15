package project.mall.activity.handler;

import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.ActivityInfo;
import project.mall.activity.core.ActivityMultiState;
import project.mall.activity.core.ThreeStateEnum;
import project.mall.activity.core.vo.ActivityResultDataContext;
import project.mall.activity.core.vo.ActivityUserResultInfo;
import project.mall.activity.core.vo.ValueOptional;
import project.mall.activity.dto.ActivityEditInfoDTO;
import project.mall.activity.event.message.BaseActivityMessage;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityUserJoinLog;
import project.mall.activity.model.ActivityUser;
import project.mall.activity.service.*;

import java.util.Date;
import java.util.Objects;

/**
 * 活动处理器，管理活动的整个生命周期的关键事件
 *
 * 一个活动类型，对应一个 ActivityHandler 子类，
 * 注意，注意：活动处理器的子类必须位于包（或更深级的子包）：project.mall.activity  之下 !!!
 *
 */
public abstract class ActivityHandler {
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

	protected ThreadLocal<ActivityResultDataContext> ACTIVITY_RESULT_DATA_CONTEXT = new ThreadLocal<ActivityResultDataContext>(){
		protected synchronized ActivityResultDataContext initialValue() {
			return new ActivityResultDataContext();
		}
	};

	protected ActivityTemplateService activityTemplateService;

	protected ActivityLibraryService activityLibraryService;

	protected ActivityUserJoinLogService activityUserJoinLogService;

	protected ActivityUserService activityUserService;

	protected ActivityPrizePoolService activityPrizePoolService;

	protected ActivityPrizeService activityPrizeService;

	/**
	 * 活动处理器服务于指定活动
	 *
	 * @return
	 */
	public abstract ActivityTypeEnum supportActivityType();

	/**
	 * 创建/修改活动
	 *
	 * @param activityInfo
	 * @return
	 */
	public abstract ActivityLibrary saveActivity(ActivityEditInfoDTO activityInfo);

	/**
	 * 加载活动配置详情
	 *
	 * @param activityId
	 * @return
	 */
	public abstract ActivityEditInfoDTO getActivityDetail(String activityId, String lang);

	/**
	 * 新建活动前，加载活动模板的参数信息
	 *
	 * @param templateId
	 * @return
	 */
	public abstract ActivityEditInfoDTO loadActivityTemplate(String templateId);

	/**
	 * 启动活动的额外操作，例如启动定时任务监控....
	 *
	 * @param activityId
	 */
	public abstract void start(String activityId);

	/**
	 * 判断用户是否已经参加了该活动以及是否允许参加该活动的最基础判断，
	 * 注意：
	 *   本方法仅做最基础的判断，如果本次判断未能返回明确的结果，或者具体业务有更特殊的判断，可由子类复用，或覆盖父类的判断逻辑
	 *
	 * @param activityMessage
	 * @param activityInfo
	 * @return
	 */
	public ActivityMultiState checkJoin(BaseActivityMessage activityMessage, ActivityInfo activityInfo) {
		// 防止线程池问题导致的其他线程处理结果泄露
		ACTIVITY_RESULT_DATA_CONTEXT.remove();

		// 如果本次判断无法明确判定结论，需要设置为 UNKNOW，交由具体业务实现再次判断
		ActivityMultiState retState = new ActivityMultiState();
		// 默认要是 UNKNOW，需要业务代码做二次把关；但是如果返回 false，则代表明确不能加入活动
		retState.can(ThreeStateEnum.UNKNOW);
		// 默认要是 UNKNOW
		retState.has(ThreeStateEnum.UNKNOW);

		if (StrUtil.isNotBlank(activityMessage.getActivityId())) {
			if (!Objects.equals(activityMessage.getActivityId(), activityInfo.getActivityId())) {
				// 指定了活动ID的情况，不匹配则直接退出
				retState.can(ThreeStateEnum.FALSE);
				retState.has(ThreeStateEnum.FALSE);
				retState.setDescription("当前活动不支持该活动事件");

				return retState;
			}
		}

		String userId = activityMessage.getUserId();
		if (StrUtil.isBlank(userId) || Objects.equals(userId, "0")) {
			retState.setDescription("未指定当前参与活动的用户");
			return retState;
		}

		/**
		 * 首先，根据通用逻辑识别当前用户是否已参与过当前活动
		 */
		ActivityUser userJoinRecord = activityUserService.getActivityUser(activityInfo.getActivityId(), userId, activityMessage.getEventType());
		if (userJoinRecord != null && userJoinRecord.getFirstTriggerTime() > 0) {
			// 被动触发的 activityUser 记录，FirstTriggerTime 值为0
			int joinTimes = userJoinRecord.getJoinTimes();
			int maxAllowedTimes = userJoinRecord.getAllowJoinTimes();
			retState.setTimes(joinTimes);
			retState.has(ThreeStateEnum.TRUE);

			Date activityValidBeginTime = userJoinRecord.getValidBeginTime();
			Date activityValidEndTime = userJoinRecord.getValidEndTime();
			if (activityValidBeginTime == null) {
				activityValidBeginTime = activityInfo.getStartTime();
			}
			if (activityValidEndTime == null) {
				activityValidEndTime = activityInfo.getEndTime();
			}
			if (activityValidBeginTime.getTime() > activityMessage.getEventTime()
					|| activityValidEndTime.getTime() < activityMessage.getEventTime()) {
				// 越过了活动的有效时间边界
				retState.can(ThreeStateEnum.FALSE);
				retState.setDescription("已错过活动时间");
				return retState;
			}

			// 有记录不代表当前次数下参与过
			// 可参与一次或多次
			if (maxAllowedTimes == 0) {
				// 代表无限次数
				// 不能武断，需要交给子类进一步判断
				//retState.can(ThreeStateEnum.TRUE);
				return retState;
			}

			if (joinTimes + activityMessage.getBatchJoinTimes() - 1 > maxAllowedTimes) {
				// 已达最大次数，可能当前 join 事件属于上次 join 的一个收尾处理（不累加 joinTimes），所以此处要减 1
				// 例如：允许最多参加活动 10 次，此时观察到历史参加次数达到了 10 次，但是第 10 次是个长期事件，在具体业务中属于刚刚开始还未结束
				// 的状态，所以本次仍然允许 join，可以看到 join 事件有个 status 属性，值为 0 代表该 join 事件并未完结
				retState.can(ThreeStateEnum.FALSE);
				retState.setDescription("超过了参加活动的允许次数");
				return retState;
			} else if (joinTimes + activityMessage.getBatchJoinTimes() - 1 < maxAllowedTimes) {
				// 允许次数还未消耗完，原则上可以继续参与
				// 不能武断，需要交给子类进一步判断
				// retState.can(ThreeStateEnum.TRUE);
				return retState;
			} else {
				// 特殊情况：达到上限次数，但是最后一次还没完成的情况 TODO
				if (userJoinRecord.getStatus().intValue() == 0) {
					// 可以多次参加的活动，并且当前场次还没结束
					// 不能武断，需要交给子类进一步判断
					// retState.can(ThreeStateEnum.TRUE);
				} else {
					// 可以多次参加的活动，并且最近一次的参与已经结束
					// 所以需要注意：即使返回 has 值为 false，也不能代表用户一次都没有参加活动，而是代表当前场次下是否参加过
					retState.can(ThreeStateEnum.FALSE);
					retState.setDescription("超过了参加活动的允许次数");
				}
			}
		} else {
			retState.has(ThreeStateEnum.FALSE);
		}

		return retState;
	}

	public final void initJoin(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState preState) {
		ActivityLibrary activityLibrary = activityLibraryService.findById(activityInfo.getActivityId());
		// 最近一次 joinLog 记录
		ActivityUserJoinLog lastLog = activityUserJoinLogService.lastJoinLog(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());

		// 放进事务
		ActivityUserJoinLog triggerEvent = new ActivityUserJoinLog();
		triggerEvent.setActivityId(activityInfo.getActivityId());
		triggerEvent.setUserId(activityMessage.getUserId());
		triggerEvent.setEventType(activityMessage.getEventType());
		triggerEvent.setEventKey(activityMessage.getEventId());
		// 没有关联业务表
		triggerEvent.setRefId(null);
		triggerEvent.setRefType(0);
		triggerEvent.setStatus(1);
		triggerEvent.setTriggerTime(activityMessage.getEventTime());
		triggerEvent.setExtraInfo(null);
		if (lastLog == null) {
			// 从没产生过当前类型的事件历史记录
			triggerEvent.setTimes(1);
		} else {
			triggerEvent.setTimes(lastLog.getTimes() + 1);
		}
		try {
			activityUserJoinLogService.save(triggerEvent);
			cacheActivityResultData("activity_user_join_log_id", triggerEvent.getId());
		} catch (Exception e) {
			// 如果是并发场景下的索引冲突，则忽略异常 TODO
			logger.error("initJoin error: ", e);
			throw new BusinessException(e);
		}

		// 强制产生一条活动用户记录
		ActivityUser existActivityUser = activityUserService.getActivityUser(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());
		if (existActivityUser != null) {
			cacheActivityResultData("activity_user_id", existActivityUser.getId());

			if (existActivityUser.getJoinTimes() <= 0) {
				existActivityUser.setJoinTimes(1);
				existActivityUser.setStatus(0);
			}
			if (existActivityUser.getStatus() == 1) {
				// 上次已结束，开启新的次数
				existActivityUser.setJoinTimes(existActivityUser.getJoinTimes() + 1);
				existActivityUser.setStatus(0);
			}
			// 调整：考虑到当前业务经常出现管理员在活动运行期间修改活动起止时间的问题，此处默认不填值将代表随着活动记录最新的配置时间来做校验
			//if (existActivityUser.getValidBeginTime() == null) {
			//	existActivityUser.setValidBeginTime(activityLibrary.getStartTime());
			//}
			//if (existActivityUser.getValidEndTime() == null) {
			//	existActivityUser.setValidEndTime(activityLibrary.getEndTime());
			//}
			if (existActivityUser.getFirstTriggerTime() <= 0L) {
				existActivityUser.setFirstTriggerTime(activityMessage.getEventTime());
			}
			if (existActivityUser.getLastTriggerTime() <= 0L) {
				existActivityUser.setLastTriggerTime(activityMessage.getEventTime());
			}

			existActivityUser.setActivityType(activityInfo.getType());
			activityUserService.save(existActivityUser);
		} else {
			ActivityUser activityUser = new ActivityUser();
			activityUser.setUserId(activityMessage.getUserId());
			activityUser.setActivityId(activityInfo.getActivityId());
			activityUser.setActivityType(activityInfo.getType());
			activityUser.setTriggerType(activityMessage.getEventType());
			activityUser.setFirstTriggerTime(activityMessage.getEventTime());
			activityUser.setLastTriggerTime(activityMessage.getEventTime());
			activityUser.setUserType(0);
			// 此处不填充，则在查询时返回的是 null
			activityUser.setUserRegistTime(0L);

			if (activityLibrary.getAllowJoinTimes() == 0) {
				activityUser.setAllowJoinTimes(0);
			} else {
				activityUser.setAllowJoinTimes(activityLibrary.getAllowJoinTimes());
			}
			activityUser.setJoinTimes(1);

			// 默认设置活动有效期，如果具体业务有不同的逻辑，需要子类修正
			// 调整：考虑到当前业务经常出现管理员在活动运行期间修改活动起止时间的问题，此处默认不填值将代表随着活动记录最新的配置时间来做校验
			//activityUser.setValidBeginTime(activityLibrary.getStartTime());
			//activityUser.setValidEndTime(activityLibrary.getEndTime());
			activityUser.setStatus(0);

			try {
				activityUserService.save(activityUser);
				cacheActivityResultData("activity_user_id", activityUser.getId());
			} catch (Exception e) {
				// 如果是并发场景下的索引冲突，则忽略异常 TODO
				logger.error("initJoin error: ", e);
				throw new BusinessException(e);
			}
		}
		//ACTIVITY_CONTEXT.get().setJoinActivityKey(activityUser.getId());
		//ACTIVITY_CONTEXT.get().setTriggerTraceKey(triggerEvent.getId());
	}

	/**
	 * 用户参加活动的相关处理，包括生成：ActivityUser 记录，ActivityTriggerEvent 记录。
	 *
	 * @param activityMessage
	 * @param activityInfo
	 */
	public abstract ActivityMultiState join(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState checkJoinState);

	/**
	 * 判断用户当前相关事件是否能够得到奖励以及是否已经得到了奖励；
	 * 注意：
	 *   有些活动不是每次行为都能触发奖励的，所以此处返回 false 不至于需要外层方法抛异常
	 *
	 * @param activityMessage
	 * @param activityInfo
	 * @return
	 */
	public ActivityMultiState checkAward(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState joinState) {
		String userId = activityMessage.getUserId();

		ActivityMultiState retState = new ActivityMultiState();
		// 是否能够奖励将交由具体业务来做判断，默认为 UNKNOW，此处如果返回 false 则代表明确不能给与奖励
		// 此处如果返回 UNKNOW 则需要业务代码做二次把关
		retState.can(ThreeStateEnum.UNKNOW);
		retState.has(ThreeStateEnum.UNKNOW);
		if (StrUtil.isBlank(userId) || Objects.equals(userId, "0")) {
			retState.setDescription("未指定当前参与活动的用户");
			return retState;
		}
		if (StrUtil.isNotBlank(activityMessage.getActivityId())) {
			if (!Objects.equals(activityMessage.getActivityId(), activityInfo.getActivityId())) {
				// 指定了活动ID的情况，不匹配则直接退出
				retState.can(ThreeStateEnum.FALSE);
				// 默认要是 UNKNOW
				retState.has(ThreeStateEnum.FALSE);

				return retState;
			}
		}

		// 完善：有 ActivityUser 记录不代表已经获取过奖励，或者判断是否能够获取奖励
//		// 首先，检查用户加入该活动的场次
//		ActivityUser userJoinRecord = activityUserService.getActivityUser(activityInfo.getActivityId(), userId, activityMessage.getEventType());
//		if (userJoinRecord == null || userJoinRecord.getFirstTriggerTime() <= 0) {
//			// 如果用户还未参加活动，则一定不能获取奖励
//			retState.has(ThreeStateEnum.FALSE);
//			// 不能武断，需要交给子类进一步判断
//			// retState.can(ThreeStateEnum.FALSE);
//			// retState.setDescription("用户未参与该活动");
//			return retState;
//		} else {
//			retState.has(ThreeStateEnum.TRUE);
//			return retState;
//		}

		return retState;
	}

	/**
	 * 进行奖励处理
	 *
	 * @param activityMessage
	 * @param activityInfo
	 */
	public abstract void award(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState checkAwardState);

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
	 * @param awardState
	 */
	public void postActivity(BaseActivityMessage activityMessage, ActivityInfo activityInfo, ActivityMultiState joinState, ActivityMultiState awardState, ActivityUserResultInfo activityUserResultInfo) {
		ActivityUserJoinLog lastLog = activityUserJoinLogService.lastJoinLog(activityInfo.getActivityId(), activityMessage.getUserId(), activityMessage.getEventType());
		if (lastLog != null) {
			// 没资格参加活动的用户不会产生 ActivityUserJoinLog 记录
			Date now = new Date();
			lastLog.setStatus(2);
			lastLog.setFinishTime(now.getTime());
			if (activityUserResultInfo != null) {
				if (StrUtil.isNotBlank(activityUserResultInfo.getRefId())) {
					lastLog.setRefId(activityUserResultInfo.getRefId());
					lastLog.setRefType(activityUserResultInfo.getRefType());
				}
				if (activityUserResultInfo.getExtraInfo() != null && !activityUserResultInfo.getExtraInfo().isEmpty()) {
					lastLog.setExtraInfo(JsonUtils.bean2Json(activityUserResultInfo.getExtraInfo()));
				}
			}

			activityUserJoinLogService.save(lastLog);
		}

		// 其他扩展，子类实现或覆盖
	}

	/**
	 * 停止活动的额外操作
	 *
	 * @param activityId
	 */
	public abstract void stop(String activityId);

	public void cacheActivityResultData(String key, Object value) {
		ACTIVITY_RESULT_DATA_CONTEXT.get().set(key, value);
	}

	public ValueOptional getActivityResultData(String key) {
		if (StrUtil.isBlank(key)) {
			return (new ValueOptional(null));
		}

		ValueOptional optional = ACTIVITY_RESULT_DATA_CONTEXT.get().get(key);
		if (optional == null) {
			return (new ValueOptional(null));
		}

		return optional;
	}

	public void setActivityUserJoinLogService(ActivityUserJoinLogService activityTriggerEventService) {
		this.activityUserJoinLogService = activityTriggerEventService;
	}

	public void setActivityTemplateService(ActivityTemplateService activityTemplateService) {
		this.activityTemplateService = activityTemplateService;
	}

//	public void setActivityHandleStatService(ActivityHandleStatService activityHandleStatService) {
//		this.activityHandleStatService = activityHandleStatService;
//	}

	public void setActivityLibraryService(ActivityLibraryService activityLibraryService) {
		this.activityLibraryService = activityLibraryService;
	}

	public void setActivityUserService(ActivityUserService activityUserService) {
		this.activityUserService = activityUserService;
	}

	public void setActivityPrizePoolService(ActivityPrizePoolService activityPrizePoolService) {
		this.activityPrizePoolService = activityPrizePoolService;
	}

	public void setActivityPrizeService(ActivityPrizeService activityPrizeService) {
		this.activityPrizeService = activityPrizeService;
	}

}
