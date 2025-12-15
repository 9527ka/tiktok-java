package project.mall.notification.utils.notify.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 默认的，站内信类消息请求内容载体
 */
public class DefaultNotifyRequest extends BaseNotifyRequest {

    private Map<String, Object> varMap = new HashMap();

    @Override
    public CommonNotifyRequest buildRequest() {
        CommonNotifyRequest request = new CommonNotifyRequest();
        request.setBizType(this.getBizType());
        request.setLanguageType(this.getLanguageType());
        request.setFromUserId(this.getFromUserId());
        request.setTargetUserId(this.getTargetUserId());
        request.setTargetTopic(this.getTargetTopic());
        request.setRefType(this.getRefType());
        request.setRefId(this.getRefId());

        if (varMap.size() > 0) {
            Set<Map.Entry<String, Object>> entrySets = varMap.entrySet();
            for (Map.Entry<String, Object> oneEntry : entrySets) {
                request.setVarValue(oneEntry.getKey(), oneEntry.getValue());
            }
        }

        return request;
    }

//    @Override
//    public String initContent(String templateContent) {
//        return templateContent;
//    }

    public void setValue(String varCode, Object value) {
        if (varCode == null
                || varCode.trim().isEmpty()
                || value == null) {
            throw new RuntimeException("参数错误");
        }

        varMap.put(varCode, value);
    }

    public Map<String, Object> getVarMap() {
        return varMap;
    }
}
