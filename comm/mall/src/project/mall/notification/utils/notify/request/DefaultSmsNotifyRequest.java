package project.mall.notification.utils.notify.request;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 默认的，手机短信类消息请求内容载体
 */
public class DefaultSmsNotifyRequest extends BaseNotifyRequest {
    private String mobileInfo;

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
        CommonNotifyRequest request = new CommonNotifyRequest();
        request.setBizType(this.getBizType());
        request.setLanguageType(this.getLanguageType());
        request.setFromUserId(this.getFromUserId());
        request.setTargetUserId(this.getTargetUserId());
        request.setTargetTopic(this.getTargetTopic());
        request.setTargetExtra(this.getMobileInfo());
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

    public String getMobileInfo() {
        return mobileInfo;
    }

    public void setMobileInfo(String mobileInfo) {
        this.mobileInfo = mobileInfo;
    }
}
