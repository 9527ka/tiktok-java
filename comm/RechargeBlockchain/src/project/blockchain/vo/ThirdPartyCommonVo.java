package project.blockchain.vo;

import lombok.Data;

@Data
public class ThirdPartyCommonVo {
    /**
     * 商户号
     */
    private String merchant_no;
    /**
     * 响应码
     * 200-请求成功，400-请求失败
     */
    private int code;
    /**
     * 错误消息说明，只有code为400时才有值
     */
    private String message;
    /**
     * 业务响应信息
     */
    private String params;
    /**
     * 响应请求的时间戳（秒）
     */
    private int timestamp;

    /**
     * 签名 MD5(merchant_no+params+sign_type+timestamp+Key)
     */
    private String sign;

}
