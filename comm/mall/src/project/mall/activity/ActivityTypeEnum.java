package project.mall.activity;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import kernel.annotation.BeanFieldName;
import kernel.util.ClassTools;
import kernel.util.ObjectTools;
import project.mall.activity.core.vo.ActivityParam;
import project.mall.activity.handler.ActivityHandler;
import project.mall.activity.handler.FirstRechargeFruitDialActivityHandler;
import project.mall.activity.handler.SimpleLotteryActivityHandler;
import project.mall.activity.rule.BaseActivityConfig;
import project.mall.activity.rule.FruitDialActivityConfig;
import project.mall.activity.rule.SimpleLotteryActivityConfig;
import project.mall.activity.rule.award.BaseActivityAwardRule;
import project.mall.activity.rule.award.DemoActivityAwardRule;
import project.mall.activity.rule.join.BaseActivityJoinRule;
import project.mall.activity.rule.join.DemoActivityJoinRule;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 活动枚举类型
 */
public enum ActivityTypeEnum {
	// 首充参加抽奖活动
	FRUIT_DIAL_LOTTERY("fruit_dial_lottery", 2, FruitDialActivityConfig.class, DemoActivityJoinRule.class, DemoActivityAwardRule.class, FirstRechargeFruitDialActivityHandler.class,"首充抽奖活动"),
	SIMPLE_LOTTERY("simple_lottery", 1, SimpleLotteryActivityConfig.class, DemoActivityJoinRule.class, DemoActivityAwardRule.class, SimpleLotteryActivityHandler.class,"简单抽奖活动"),

	;

	// 活动编码
	private String type;

	// 兼容有的地方使用数字代表活动类型
	private int index;

	private Class<? extends BaseActivityConfig> activityConfig;

	// 活动参加规则
	private Class<? extends BaseActivityJoinRule> joinRule;

	// 活动奖励规则
	private Class<? extends BaseActivityAwardRule> awardRule;

	private Class<? extends ActivityHandler> handler;

	private String description;

	private ActivityTypeEnum(String type,
							 int index,
							 Class<? extends BaseActivityConfig> activityConfig,
							 Class<? extends BaseActivityJoinRule> joinRule,
							 Class<? extends BaseActivityAwardRule> awardRule,
							 Class<? extends ActivityHandler> handler,
							 String description) {
		this.type = type;
		this.index = index;
		this.activityConfig = activityConfig;
		this.joinRule = joinRule;
		this.awardRule = awardRule;
		this.handler = handler;
		this.description = description;
	}

	public static ActivityTypeEnum typeOf(String type) {
		if (StrUtil.isBlank(type)) {
			return null;
		}

		ActivityTypeEnum values[] = ActivityTypeEnum.values();
		for (ActivityTypeEnum one : values) {
			if (Objects.equals(one.getType(), type)) {
				return one;
			}
		}

		return null;
	}

	public static ActivityTypeEnum indexOf(int inputIdx) {
		ActivityTypeEnum values[] = ActivityTypeEnum.values();
		for (ActivityTypeEnum one : values) {
			if (one.getIndex() == inputIdx) {
				return one;
			}
		}

		return null;
	}

	public Object initActivityConfig(List<ActivityParam> paramList) {
		try {
			final Class unsafeClass = Class.forName("sun.misc.Unsafe");
			final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);

			final Object unsafe = theUnsafeField.get(null);
			final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);

			Object obj = allocateInstance.invoke(unsafe, this.activityConfig);

			Map<String, ActivityParam> paramMap = new HashMap<>();
			if (CollectionUtil.isNotEmpty(paramList)) {
				for (ActivityParam oneParam : paramList) {
					paramMap.put(oneParam.getCode(), oneParam);
				}
			}

			if (!paramMap.isEmpty()) {
				List<Field> allFields = ClassTools.getAllFields(this.activityConfig);
				for (Field oneField : allFields) {
					String fieldName = oneField.getName();
					BeanFieldName ann = oneField.getAnnotation(BeanFieldName.class);
					if (ann != null) {
						fieldName = ann.value();
					}

					ActivityParam paramValue = paramMap.get(fieldName);
					if (paramValue != null) {
						ObjectTools.setValue(obj, fieldName, paramValue.getValue());
					}
				}
			}

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object initJoinRule(List<ActivityParam> paramList) {
		try {
			final Class unsafeClass = Class.forName("sun.misc.Unsafe");
			final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);

			final Object unsafe = theUnsafeField.get(null);
			final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);

			Object obj = allocateInstance.invoke(unsafe, this.joinRule);

			Map<String, ActivityParam> paramMap = new HashMap<>();
			if (CollectionUtil.isNotEmpty(paramList)) {
				for (ActivityParam oneParam : paramList) {
					paramMap.put(oneParam.getCode(), oneParam);
				}
			}

			if (!paramMap.isEmpty()) {
				List<Field> allFields = ClassTools.getAllFields(this.joinRule);
				for (Field oneField : allFields) {
					String fieldName = oneField.getName();
					BeanFieldName ann = oneField.getAnnotation(BeanFieldName.class);
					if (ann != null) {
						fieldName = ann.value();
					}

					ActivityParam paramValue = paramMap.get(fieldName);
					if (paramValue != null) {
						ObjectTools.setValue(obj, fieldName, paramValue.getValue());
					}
				}
			}

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object initAwardRule(List<ActivityParam> paramList) {
		try {
			final Class unsafeClass = Class.forName("sun.misc.Unsafe");
			final Field theUnsafeField = unsafeClass.getDeclaredField("theUnsafe");
			theUnsafeField.setAccessible(true);

			final Object unsafe = theUnsafeField.get(null);
			final Method allocateInstance = unsafeClass.getMethod("allocateInstance", Class.class);

			Object obj = allocateInstance.invoke(unsafe, this.awardRule);

			Map<String, ActivityParam> paramMap = new HashMap<>();
			if (CollectionUtil.isNotEmpty(paramList)) {
				for (ActivityParam oneParam : paramList) {
					paramMap.put(oneParam.getCode(), oneParam);
				}
			}

			if (!paramMap.isEmpty()) {
				List<Field> allFields = ClassTools.getAllFields(this.awardRule);
				for (Field oneField : allFields) {
					String fieldName = oneField.getName();
					BeanFieldName ann = oneField.getAnnotation(BeanFieldName.class);
					if (ann != null) {
						fieldName = ann.value();
					}

					ActivityParam paramValue = paramMap.get(fieldName);
					if (paramValue != null) {
						ObjectTools.setValue(obj, fieldName, paramValue.getValue());
					}
				}
			}

			return obj;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public String getType() {
		return type;
	}

	public int getIndex() {
		return index;
	}

	public Class<? extends BaseActivityConfig> getActivityConfig() {
		return activityConfig;
	}

	public Class<? extends BaseActivityJoinRule> getJoinRule() {
		return joinRule;
	}

	public Class<? extends BaseActivityAwardRule> getAwardRule() {
		return awardRule;
	}

	public Class<? extends ActivityHandler> getHandler() {
		return handler;
	}

	public String getDescription() {
		return description;
	}
}
