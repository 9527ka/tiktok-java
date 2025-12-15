package project.pay;


import com.alibaba.fastjson.JSONObject;
import http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;


public class PayCore {
    private static final Logger log = LoggerFactory.getLogger(PayCore.class);
    /**
     * 发起支付
     * @param channel 支付渠道,wxpay 微信支付 alipay 支付宝
     * @param itemName 商品名称
     * @param tradeOrderNo 交易订单号
     * @param amount 支付金额
     * @param notifyUrl 异步通知地址
     * @param returnUrl 同步通知地址
     * @param clientIp 客户端IP
     * @param extParam 扩展参数
     * @return 支付链接或二维码地址
     */
    public static String submitPayment(String channel,
                                     String itemName,
                                     String tradeOrderNo,
                                     BigDecimal amount,
                                     String notifyUrl,
                                     String returnUrl,
                                     String clientIp,
                                     String extParam){



//        mchId 	商户号 	是 	string 	M1623984572 	商户号
//        wayCode 	通道类型 	是 	string 	901 	通道类型，详见 通道编码
//        subject 	商品标题/真实姓名 	是 	string 	商品标题测试 	商品标题/真实姓名
//        body 	商品描述 	否 	string 	商品描述测试 	商品描述
//        outTradeNo 	商户订单号 	是 	string 	20160427210604000490 	商户生成的订单号
//        amount 	支付金额 (单位: 分) 	是 	int 	10000 	支付金额 (单位: 分)，例如: 10000 即为 100.00 元
//        extParam 	扩展参数 	否 	string 	134586944573118714 	商户扩展参数,回调时会原样返回
//        clientIp 	客户端IP 	是 	string 	210.73.10.148 	客户端 IPV4 地址，尽量填写
//        notifyUrl 	异步通知地址 	是 	string 	https://www.test.com/notify.htm 	支付结果异步回调URL，只有传了该值才会发起回调
//        returnUrl 	跳转通知地址 	否 	string 	https://www.test.com/return.htm 	支付结果同步跳转通知URL
//        reqTime 	请求时间 	是 	long 	1622016572190 	请求接口时间，13位时间戳
//        sign 	签名 	是 	string 	694da7a446ab4b1d9ceea7e5614694f4 	签名值，详见 签名算法

        Map<String,Object> data = new HashMap<>();
        data.put("mchId",PayConfig.PAY_MCH_ID);
        if(channel.equals("wxpay")){
            data.put("wayCode","8888");
            if(amount.compareTo(new BigDecimal(100)) < 0){
                throw new RuntimeException("The minimum amount is 100 yuan");
            }
            if(amount.compareTo(new BigDecimal(5000)) > 0){
                throw new RuntimeException("The maximum amount is 5000 yuan");
            }
        }else if("alipay".equals(channel)){
            data.put("wayCode","555");
            if(amount.compareTo(new BigDecimal(300)) < 0){
                throw new RuntimeException("The minimum amount is 300 yuan");
            }
            if(amount.compareTo(new BigDecimal(3000)) > 0){
                throw new RuntimeException("The maximum amount is 3000 yuan");
            }
        }
        data.put("subject",itemName);
        data.put("outTradeNo",tradeOrderNo);
        data.put("amount",amount.multiply(new BigDecimal(100)).intValue());
        data.put("clientIp",clientIp);
        data.put("extParam",extParam);
        data.put("notifyUrl",notifyUrl);
        data.put("returnUrl",returnUrl);
        data.put("reqTime",System.currentTimeMillis());
        String sign = ApiUtil.Sign(data, "&key="+PayConfig.PAY_KEY);
        data.put("sign",sign);

        log.info("发起支付请求参数:{}",data);

        byte[] bytes = HttpUtils.parseJsonBody(data);
        //byte[] post = HttpClient.post(PayConfig.PAY_URL, bytes, 15000);
        HttpRequest req = HttpRequestBuilder.create(PayConfig.PAY_URL,"POST")
                .body(bytes)
                .contentType(ContentType.JSON)
                .timeout(15000)
                .build();
        byte[] post = HttpClient.request(req);
        String retString = new String(post);
        // '{"code":0,
        // "message":"ok",
        // "data":{
        // "mchId":"M1733726846",
        // "outTradeNo":"24121212454449072103",
        // "payUrl":"http://slb.cfpayment.ylpay.net/pay/page/default/P1867068295795675137",
        // "tradeNo":"SM20241212124544392581132",
        // "originTradeNo":1,
        // "amount":10000,
        // "expiredTime":1733979044},
        // "sign":"dd943dbe0a48dc8384a5d6086553c932"}'
        System.out.println(retString);
        JSONObject obj = JSONObject.parseObject(retString);
        if(obj.getInteger("code") == 0){
            return obj.getJSONObject("data").getString("payUrl");
        }else{
            throw new RuntimeException("发起支付失败: "+obj.getString("message"));
        }
    }

    public static boolean checkSign(Map<String, Object> data, String sign) {
        String sumSign = ApiUtil.Sign(data, "&key="+PayConfig.PAY_KEY);
        if(sumSign.equals(sign)){
            return true;
        }
        log.error("支付回调签名验证失败: accept:{}, act: {}",sign,sumSign);
        return false;
    }
}
