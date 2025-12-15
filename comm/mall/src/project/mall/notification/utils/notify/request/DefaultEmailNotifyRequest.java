package project.mall.notification.utils.notify.request;

import kernel.util.ObjectTools;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import project.mall.notification.utils.notify.handler.DefaultEmailNotifyHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 默认的，邮件类消息请求内容载体
 */
public class DefaultEmailNotifyRequest extends BaseNotifyRequest {
    private String fromEmail;

    private String targetEmail;

    // 短信验证码
    private String captchCode;

    // 图片验证码
    private String imageCaptchCode;

    // 客户端id
    private String clientId;

    // 短信验证码有效时长，单位秒
    private int captchValidTimeout = 5 * 60;

    // 其他的业务参数，可以不包含短信验证码占位符变量
    private Map<String, Object> varMap = new HashMap();

    @Override
    public CommonNotifyRequest buildRequest() {
        if (StringUtils.isBlank(this.getTargetEmail())) {
            throw new RuntimeException("未指定邮件接收者");
        }

        CommonNotifyRequest request = new CommonNotifyRequest();
        request.setBizType(this.getBizType());
        request.setLanguageType(this.getLanguageType());
        request.setFromUserId(this.getFromUserId());
        request.setTargetUserId(this.getTargetUserId());
        request.setTargetTopic(this.getTargetTopic());
        request.setTargetExtra(this.getTargetEmail());
        request.setFromExtra(this.getFromEmail());
        request.setRefType(this.getRefType());
        request.setRefId(this.getRefId());

        if (this.getCaptchCode() != null && !this.getCaptchCode().trim().isEmpty()) {
            // 短信验证码专用
            request.setCaptchCodeValue(this.getCaptchCode().trim());
        }

        if (varMap.size() > 0) {
            Set<Map.Entry<String, Object>> entrySets = varMap.entrySet();
            for (Map.Entry<String, Object> oneEntry : entrySets) {
                request.setVarValue(oneEntry.getKey(), oneEntry.getValue());
            }
        }

        return request;
    }

    @Override
    protected Map<String, Object> listBizAttrbuteValue() {
        Map<String, Object> ftlDataMap = new HashMap<>();
        if (this.getVarMap() != null) {
            ftlDataMap.putAll(this.getVarMap());
        }
        Map<String, Object> beanMap = ObjectTools.beanToMap(this);
        beanMap.remove("varMap");
        beanMap.remove("bizType");
        beanMap.remove("languageType");
        beanMap.remove("customeMessage");
        beanMap.remove("customeTitle");
        beanMap.remove("fromUserId");
        beanMap.remove("targetUserId");
        beanMap.remove("targetTopic");

        ftlDataMap.putAll(beanMap);

        return ftlDataMap;
    }

//    private Map<String, Object> object2Map(Object object) {
//        JSONObject jsonObject = (JSONObject) JSON.toJSON(object);
//        Set<Map.Entry<String,Object>> entrySet = jsonObject.entrySet();
//        Map<String, Object> map=new HashMap<String,Object>();
//        for (Map.Entry<String, Object> entry : entrySet) {
//            map.put(entry.getKey(), entry.getValue());
//        }
//
//        return map;
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

    public String getImageCaptchCode() {
        return imageCaptchCode;
    }

    public void setImageCaptchCode(String imageCaptchCode) {
        this.imageCaptchCode = imageCaptchCode;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getFromEmail() {
        return fromEmail;
    }

    public void setFromEmail(String fromEmail) {
        this.fromEmail = fromEmail;
    }

    public String getTargetEmail() {
        return targetEmail;
    }

    public void setTargetEmail(String targetEmail) {
        this.targetEmail = targetEmail;
    }

    public String getCaptchCode() {
        return captchCode;
    }

    public void setCaptchCode(String captchCode) {
        this.captchCode = captchCode;
    }

    public int getCaptchValidTimeout() {
        return captchValidTimeout;
    }

    public void setCaptchValidTimeout(int captchValidTimeout) {
        this.captchValidTimeout = captchValidTimeout;
    }
}
