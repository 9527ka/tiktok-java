package project.mall.activity.core;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import kernel.exception.BusinessException;
import kernel.util.JsonUtils;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import project.mall.activity.ActivityTypeEnum;
import project.mall.activity.core.exception.JoinActivityFailException;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.core.vo.ActivityUserResultInfo;
import project.mall.activity.core.vo.ValueOptional;
import project.mall.activity.event.message.BaseActivityMessage;
import project.mall.activity.handler.ActivityHandler;
import project.mall.activity.handler.FirstRechargeFruitDialActivityHandler;
import project.mall.activity.model.ActivityLibrary;
import project.mall.activity.model.ActivityUser;
import project.mall.activity.rule.BaseActivityConfig;
import project.mall.activity.rule.award.BaseActivityAwardRule;
import project.mall.activity.rule.join.BaseActivityJoinRule;
import project.mall.activity.service.ActivityLibraryService;
import project.mall.activity.service.ActivityUserJoinLogService;
import project.mall.activity.service.ActivityUserService;

import java.util.List;
import java.util.Objects;

public class ActivityHelper {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

//	protected ActivityHandleStatService activityHandleStatService;

    protected ActivityLibraryService activityLibraryService;

    protected ActivityUserJoinLogService activityUserJoinLogService;

    protected ActivityUserService activityUserService;

//	@Autowired
//	RedisMQSender redisMQSender;

    @Transactional
    public void joinActivity(String activityId, String userId, String action, BaseActivityMessage extraInfo) {

        ActivityLibrary activityEntity = activityLibraryService.findById(activityId);
        if (activityEntity == null) {
            throw new BusinessException(1, "活动不存在");
        }
        if (extraInfo.getEventTime() <= 0) {
            extraInfo.setEventTime(System.currentTimeMillis());
        }
        if (extraInfo.getRefTime() <= 0) {
            extraInfo.setRefTime(extraInfo.getEventTime());
        }
        if (activityEntity.getStatus() != 1) {
            // 活动无效状态
            throw new BusinessException(4, "禁用活动");
        }
        if (activityEntity.getStartTime().getTime() > extraInfo.getEventTime()) {
            // 越过了活动的有效时间边界
            throw new BusinessException(5, "活动未开始");
        }

        if (activityEntity.getEndTime().getTime() < extraInfo.getEventTime()) {
			throw new BusinessException(6, "活动结束");
        }

        extraInfo.setActivityId(activityId);
        extraInfo.setEventType(action);
        extraInfo.setUserId(userId);

        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityEntity.getType());
        ActivityHandler handler = ActivityHandlerLoader.getInstance().getHandler(activityType);

        ActivityInfo activityInfo = new ActivityInfo();
        activityInfo.setActivityId(activityEntity.getId().toString());
        activityInfo.setTitle(activityEntity.getTitleCn());
        activityInfo.setType(activityEntity.getType());
        activityInfo.setStartTime(activityEntity.getStartTime());
        activityInfo.setEndTime(activityEntity.getEndTime());

        TypeReference paramType = new TypeReference<List<ActivityParam>>() {
        };
        if (StrUtil.isNotBlank(activityEntity.getActivityConfig())) {
            List<ActivityParam> activityConfigParamList = (List<ActivityParam>) JsonUtils.readValue(activityEntity.getActivityConfig(), paramType);
            BaseActivityConfig activityConfig = (BaseActivityConfig) activityType.initActivityConfig(activityConfigParamList);
            activityInfo.setActivityConfig(activityConfig);
        }

        if (StrUtil.isNotBlank(activityEntity.getJoinRule())) {
            List<ActivityParam> joinRuleParamList = (List<ActivityParam>) JsonUtils.readValue(activityEntity.getJoinRule(), paramType);
            BaseActivityJoinRule joinRuleObj = (BaseActivityJoinRule) activityType.initJoinRule(joinRuleParamList);
            activityInfo.setJoinRule(joinRuleObj);
        }

        if (StrUtil.isNotBlank(activityEntity.getAwardRule())) {
            List<ActivityParam> awardRuleParamList = (List<ActivityParam>) JsonUtils.readValue(activityEntity.getAwardRule(), paramType);
            BaseActivityAwardRule awardRuleObj = (BaseActivityAwardRule) activityType.initAwardRule(awardRuleParamList);
            activityInfo.setAwardRule(awardRuleObj);
        }

        // 用户正式参加活动 + 顺利处理奖励逻辑，才算一次完整流程
        boolean canJoin = false;
        String joinLogId = "";
        ActivityMultiState joinCheckState = handler.checkJoin(extraInfo, activityInfo);
        if (joinCheckState.can() == ThreeStateEnum.TRUE) {
            // 录入 activityUser 记录和 joinLog 记录 TODO
            // 如果一个活动用户只能有一次参加记录，那么第二次走本流程，建议此时的 can 返回 false。本处也要做好防御处理
            handler.initJoin(extraInfo, activityInfo, joinCheckState);

            canJoin = true;
            joinCheckState = handler.join(extraInfo, activityInfo, joinCheckState);
        } else {
            // 特殊处理，用户参加指定活动返回 false，则抛异常
            String errMsg = joinCheckState.getDescription();
            if (StrUtil.isBlank(errMsg)) {
                errMsg = "用户参加活动的行为:" + action + " 失败";
            }
            throw new JoinActivityFailException(errMsg);
        }

        ActivityMultiState awardCheckState = handler.checkAward(extraInfo, activityInfo, joinCheckState);
        if (awardCheckState.can() == ThreeStateEnum.TRUE) {
            handler.award(extraInfo, activityInfo, awardCheckState);
        }

        // 有些活动，用户没资格参与活动，或者没资格获取奖励，但是可能会对其他有资格的用户产生影响，这些逻辑也可以写到本方法里
        ActivityUserResultInfo activityUserResultInfo = handler.getActivityResultData("activity_user_result_info").getAs(ActivityUserResultInfo.class);
        handler.postActivity(extraInfo, activityInfo, joinCheckState, awardCheckState, activityUserResultInfo);
    }

    public ValueOptional getActivityResult(String currentActivityType, String resultName) {
        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(currentActivityType);
        ActivityHandler handler = ActivityHandlerLoader.getInstance().getHandler(activityType);
        ValueOptional result = handler.getActivityResultData(resultName);

        return result;
    }


    public ActivityUser getActivityUser(String activityId, String userId, String actionType) {
        ActivityUser activityUser = activityUserService.getActivityUser(activityId, userId, actionType);
        return activityUser;
    }

    public BaseActivityConfig getActivityConfig(ActivityLibrary activityEntity) {
        ActivityTypeEnum activityType = ActivityTypeEnum.typeOf(activityEntity.getType());
        String configJson = activityEntity.getActivityConfig();

        TypeReference paramType = new TypeReference<List<ActivityParam>>() {
        };
        List<ActivityParam> configInfoList = (List<ActivityParam>) JsonUtils.readValue(configJson, paramType);
        return (BaseActivityConfig) activityType.initActivityConfig(configInfoList);
    }

    /**
     * 管理后台创建活动
     *
     * @param activityInfo
     * @return
     */
    public ActivityLibrary createActivity(SaveActivityRequestDTO activityInfo, long tenantId) {
        if (activityInfo == null
                || (StrUtil.isNotBlank(activityInfo.getActivityId())
                && !Objects.equals(activityInfo.getActivityId(), "0"))) {
            throw new BusinessException("新建活动记录不能指定活动ID");
        }

        ActivityLibrary activityEntity = new ActivityLibrary();
        BeanUtil.copyProperties(activityInfo, activityEntity);
        if (StrUtil.isNotBlank(activityInfo.getValidBeginTime())) {
            activityEntity.setStartTime(DateUtil.parseDateTime(activityInfo.getValidBeginTime()));
        }
        if (StrUtil.isNotBlank(activityInfo.getValidEndTime())) {
            activityEntity.setEndTime(DateUtil.parseDateTime(activityInfo.getValidEndTime()));
        }
        activityEntity.setLocation(99999);
        activityEntity.setStatus(0);
        activityEntity.setId(null);
        //activityLibraryService.save(activityEntity);

        return activityEntity;
    }

    /**
     * 管理后台修改活动 或 租户管理员修改活动
     *
     * @param activityInfo
     * @return
     */
    public ActivityLibrary editActivity(SaveActivityRequestDTO activityInfo, long currentUserTenantId) {
        if (activityInfo == null
                || StrUtil.isBlank(activityInfo.getActivityId())
                || Objects.equals(activityInfo.getActivityId(), "0")) {
            throw new BusinessException("未指定活动ID");
        }

        long activityId = Long.parseLong(activityInfo.getActivityId());
        ActivityLibrary activityEntity = null;//activityLibraryService.getById(activityId);
        if (activityEntity == null) {
            throw new BusinessException("活动记录不存在:" + activityInfo.getActivityId());
        }

        // 识别活动是否允许修改相关字段 TODO

        if (StrUtil.isNotBlank(activityInfo.getTitle())) {
            activityEntity.setTitleCn(activityInfo.getTitle());
        }
        activityEntity.setTags(activityInfo.getTags());
        activityEntity.setDescriptionCn(activityInfo.getDescription());
        activityEntity.setImageUrl(activityInfo.getImageUrl());
//		if (activityEntity.getState() == 1 || activityEntity.getState() == 2) {
//
//		} else {
        activityEntity.setStartTime(DateUtil.parseDateTime(activityInfo.getValidBeginTime()));
        activityEntity.setEndTime(DateUtil.parseDateTime(activityInfo.getValidEndTime()));
//		}
        activityEntity.setStatus(activityInfo.getState());
        activityEntity.setLocation(activityInfo.getLocation());
        activityEntity.setDetailUrl(activityInfo.getDetailUrl());
//		if (activityInfo.getInheritMode() != null) {
//			activityEntity.setInheritMode(activityInfo.getInheritMode());
//		}
        //activityLibraryService.updateById(activityEntity);

        return activityEntity;
    }


    /**
     * 加载活动详情
     * 加载活动本身的简单数据
     *
     * @param id
     * @return
     */
    public ActivityLibrary loadActivity(String id) {
        if (StrUtil.isBlank(id) || Objects.equals(id, "0")) {
            throw new BusinessException("未指定活动ID");
        }

        long activityId = Long.parseLong(id);
        ActivityLibrary activityEntity = null;//activityLibraryService.getById(activityId);

        return activityEntity;
    }

    /**
     * 提取活动详情
     * 包含一些统计数据，活动周边数据等
     *
     * @param id
     * @return
     */
    public ActivityLibrary getActivityDetail(String id) {
        if (StrUtil.isBlank(id) || Objects.equals(id, "0")) {
            throw new BusinessException("未指定活动ID");
        }

        // 更多完善。。。 TODO
        long activityId = Long.parseLong(id);
        ActivityLibrary activityEntity = null;//activityLibraryService.getById(activityId);

        return activityEntity;
    }


//	public void setActivityHandleStatService(ActivityHandleStatService activityHandleStatService) {
//		this.activityHandleStatService = activityHandleStatService;
//	}

    public void setActivityLibraryService(ActivityLibraryService activityLibraryService) {
        this.activityLibraryService = activityLibraryService;
    }

    public void setActivityTriggerEventService(ActivityUserJoinLogService activityTriggerEventService) {
        this.activityUserJoinLogService = activityTriggerEventService;
    }

    public void setActivityUserService(ActivityUserService activityUserService) {
        this.activityUserService = activityUserService;
    }

}
