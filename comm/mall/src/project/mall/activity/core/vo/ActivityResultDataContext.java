package project.mall.activity.core.vo;

import cn.hutool.core.util.StrUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 活动处理过程中的返回信息放到这里
 */
public class ActivityResultDataContext {
	private static Logger logger = LoggerFactory.getLogger(ActivityResultDataContext.class);

	private boolean contextDataInited = false;
	private Map<String, ValueOptional> resultData = new HashMap();
	
	public void set(String attrName, Object value) {
		if (StrUtil.isBlank(attrName)) {
			return;
		}

		ValueOptional option = new ValueOptional(value);
		this.resultData.put(attrName, option);
	}
	
	public ValueOptional get(String attrName) {
		if (StrUtil.isBlank(attrName)) {
			return null;
		}

		return this.resultData.get(attrName);
	}
	
	void initContext(Map<String, Object> initData) {
		if (contextDataInited) {
			logger.error("[ActivityResultDataContext initContext] 已经初始化过了");
			return;
		}

		if (initData == null) {
			return;
		}

		Set<Map.Entry<String, Object>> entrySets = initData.entrySet();
		for (Map.Entry<String, Object> oneEntry : entrySets) {
			ValueOptional option = new ValueOptional(oneEntry.getValue());
			this.resultData.put(oneEntry.getKey(), option);
		}
		this.contextDataInited = true;
	}

}
