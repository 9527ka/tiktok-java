package project.blockchain.manager;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import project.blockchain.MD5;
import project.blockchain.vo.GCash2OrderQueryVo;
import project.blockchain.vo.GCash2Vo;
import project.blockchain.vo.GCash3Vo;
import project.blockchain.vo.GCashPayVo;
import project.blockchain.vo.ThirdPartyCommonVo;
import project.blockchain.vo.ThirdPartyProductVo;
import project.blockchain.vo.ThirdPartyRangeVo;
import project.mall.utils.RsaUtil;
import project.syspara.SysparaService;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThirdPartyManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ThirdPartyManager.class);

    private SysparaService sysparaService;

    public ThirdPartyCommonVo sendPost(Map<String, Object> params, String method, long timestamp) {
        String thirdPartyUrl = sysparaService.find("third_party_url").getValue();
        String merchant_no = sysparaService.find("third_party_merchant_no").getValue();
        Map<String, Object> requestParams = new HashMap<>();
        String paramsStr = JSONObject.toJSONString(params);
        requestParams.put("sign", sign(paramsStr, timestamp));
        requestParams.put("sign_type", "MD5");
        requestParams.put("timestamp", timestamp);
        requestParams.put("merchant_no", merchant_no);
        requestParams.put("params", paramsStr);
        String post = HttpUtil.post(thirdPartyUrl + method, requestParams, 6000);

        ThirdPartyCommonVo respVo = JSONObject.parseObject(post, ThirdPartyCommonVo.class);
        int code = respVo.getCode();
        if (code == 200) {
            return respVo;
        } else {
            LOGGER.error("请求第三方接口失败：" + post);
            throw new BusinessException("请稍后重试");
        }
    }

    public String sign(String params, long timestamp) {
        String key = sysparaService.find("third_party_api_key").getValue();
        String merchant_no = sysparaService.find("third_party_merchant_no").getValue();
        String text = merchant_no + params + "MD5" + timestamp + key;
        return MD5.sign(text);
    }

    public String sign2(Map<String, Object> params) {
        String key = sysparaService.find("third_party_rsa_key_2").getValue();
        StringBuilder signStr = new StringBuilder();
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            signStr.append(entry.getKey()).append("=").append(entry.getValue()).append("&");

        }
        String src = "";
        if (signStr.toString().length() > 0) {
            src = signStr.substring(0, signStr.length() - 1);
        }
        String sign;
        try {
            sign = RsaUtil.encryptByPrivate(src, key);
        } catch (Exception e) {
            LOGGER.error("签名异常", e);
            throw new BusinessException("请稍后再试");
        }
        return sign;
    }

    public String sign2MD5(Map<String, Object> params, String md5Key) {
        Map<String, Object> signMap = new HashMap<>();
        for (String key : params.keySet()) {
            if (!"sign".equals(key)) {
                signMap.put(key, params.get(key));
            }
        }
        String signSrc = RsaUtil.buildSignSrc(signMap);
        String text = signSrc + "&key=" + md5Key;
        return MD5.sign(text);
    }

    public ThirdPartyProductVo getCoin() {
        String minimum_amount = sysparaService.find("third_party_minimum_amount").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount").getValue();
        ThirdPartyRangeVo rangeVo = new ThirdPartyRangeVo();
        rangeVo.setMin_amount(Double.parseDouble(minimum_amount));
        rangeVo.setMax_amount(Double.parseDouble(maximum_amount));
        rangeVo.setBank_code("PHP");
        ThirdPartyProductVo productVo = new ThirdPartyProductVo();
        productVo.setRange(Collections.singletonList(rangeVo));
        productVo.setProductType("GCash");
        return productVo;
    }

    public ThirdPartyProductVo getCoin2() {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_2").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_2").getValue();
        ThirdPartyRangeVo rangeVo = new ThirdPartyRangeVo();
        rangeVo.setMin_amount(Double.parseDouble(minimum_amount));
        rangeVo.setMax_amount(Double.parseDouble(maximum_amount));
        rangeVo.setBank_code("PHP");
        ThirdPartyProductVo productVo = new ThirdPartyProductVo();
        productVo.setRange(Collections.singletonList(rangeVo));
        productVo.setProductType("GCash2.0");
        return productVo;
    }

    public ThirdPartyProductVo getCoin3() {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_3").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_3").getValue();
        ThirdPartyRangeVo rangeVo = new ThirdPartyRangeVo();
        rangeVo.setMin_amount(Double.parseDouble(minimum_amount));
        rangeVo.setMax_amount(Double.parseDouble(maximum_amount));
        rangeVo.setBank_code("PHP");
        ThirdPartyProductVo productVo = new ThirdPartyProductVo();
        productVo.setProductType("GCash3.0");
        productVo.setRange(Collections.singletonList(rangeVo));
        return productVo;
    }

    public ThirdPartyProductVo getCoin4() {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_4").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_4").getValue();
        ThirdPartyRangeVo rangeVo = new ThirdPartyRangeVo();
        rangeVo.setMin_amount(Double.parseDouble(minimum_amount));
        rangeVo.setMax_amount(Double.parseDouble(maximum_amount));
        rangeVo.setBank_code("PHP");
        ThirdPartyProductVo productVo = new ThirdPartyProductVo();
        productVo.setProductType("Maya");
        productVo.setRange(Collections.singletonList(rangeVo));
        return productVo;
    }

    public ThirdPartyProductVo getCoin5() {
        String minimum_amount = sysparaService.find("third_party_minimum_amount_5").getValue();
        String maximum_amount = sysparaService.find("third_party_maximum_amount_5").getValue();
        ThirdPartyRangeVo rangeVo = new ThirdPartyRangeVo();
        rangeVo.setMin_amount(Double.parseDouble(minimum_amount));
        rangeVo.setMax_amount(Double.parseDouble(maximum_amount));
        rangeVo.setBank_code("PHP");
        ThirdPartyProductVo productVo = new ThirdPartyProductVo();
        productVo.setProductType("GCash pay");
        productVo.setRange(Collections.singletonList(rangeVo));
        return productVo;
    }

    public GCash2Vo sendPost2(Map<String, Object> params, String method) {
        String thirdPartyUrl = sysparaService.find("third_party_url_2").getValue();
        String mer_no = sysparaService.find("third_party_merchant_no_2").getValue();
        params.put("mer_no", mer_no);
        params.put("sign", sign2(params));
        HttpRequest request = HttpUtil.createPost(thirdPartyUrl + method);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=utf-8");
        request.addHeaders(header);
        request.timeout(3000);
        request.body(JSON.toJSONString(params));
        String post = request.execute().body();

        GCash2Vo gCash2Vo = JSONObject.parseObject(post, GCash2Vo.class);
        if (!"SUCCESS".equals(gCash2Vo.getStatus().toUpperCase())) {
            LOGGER.error("请求第三方2.0接口失败:" + post);
            throw new BusinessException("请稍后再试");
        }
        return gCash2Vo;
    }

    public GCash2OrderQueryVo sendPost3(Map<String, Object> params, String method) {
        String thirdPartyUrl = sysparaService.find("third_party_url_2").getValue();
        String mer_no = sysparaService.find("third_party_merchant_no_2").getValue();
        String key = sysparaService.find("third_party_api_key_2").getValue();
        params.put("mer_no", mer_no);
        params.put("sign", sign2MD5(params, key));
        HttpRequest request = HttpUtil.createPost(thirdPartyUrl + method);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=utf-8");
        request.addHeaders(header);
        request.timeout(6000);
        request.body(JSON.toJSONString(params));
        String post = request.execute().body();

        GCash2OrderQueryVo orderQueryVo = JSONObject.parseObject(post, GCash2OrderQueryVo.class);
        if (!"SUCCESS".equals(orderQueryVo.getQuery_status().toUpperCase())) {
            LOGGER.error("请求第三方2.0接口失败:" + post);
            throw new BusinessException("请稍后再试");
        }
        return orderQueryVo;
    }

    public void setSysparaService(SysparaService sysparaService) {
        this.sysparaService = sysparaService;
    }

    public GCash3Vo sendPost4(Map<String, Object> params, String method) {
        String thirdPartyUrl = sysparaService.find("third_party_url_3").getValue();
        String mchId = sysparaService.find("third_party_merchant_no_3").getValue();
        String key = sysparaService.find("third_party_api_key_3").getValue();
        params.put("mchId", mchId);
        params.put("sign", sign2MD5(params, key));
        HttpRequest request = HttpUtil.createPost(thirdPartyUrl + method);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/json;charset=utf-8");
        request.addHeaders(header);
        request.timeout(6000);
        request.body(JSON.toJSONString(params));
        String post = request.execute().body();

        GCash3Vo gCash3Vo = JSONObject.parseObject(post, GCash3Vo.class);
        if (200 == gCash3Vo.getCode()) {
            return gCash3Vo;
        } else {
            LOGGER.error("请求第三方2.0接口失败:" + post);
            throw new BusinessException("请稍后再试");
        }
    }

    public String getSign(Map<String, Object> params, String key)
            throws NoSuchAlgorithmException, InvalidKeyException {
        Map<String, Object> signMap = new HashMap<>();
        for (String k : params.keySet()) {
            if (!"Sign".equals(k)) {
                signMap.put(k, params.get(k));
            }
        }
        String signSrc = RsaUtil.buildSignSrc(signMap);
        Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
        SecretKeySpec secret_key = new SecretKeySpec(key.getBytes(), "HmacSHA256");
        sha256_HMAC.init(secret_key);
        byte[] bytes = sha256_HMAC.doFinal(signSrc.getBytes());
        return Base64.encodeBase64String(bytes);
    }

    public GCashPayVo sendPost5(Map<String, Object> params, String method) throws InvalidKeyException, NoSuchAlgorithmException {
        String thirdPartyUrl = sysparaService.find("third_party_url_5").getValue();
        String mchId = sysparaService.find("third_party_merchant_no_5").getValue();
        String key = sysparaService.find("third_party_api_key_5").getValue();
        params.put("MerchantAccount", mchId);
        params.put("Sign", getSign(params, key));
        HttpRequest request = HttpUtil.createPost(thirdPartyUrl + method);
        Map<String, String> header = new HashMap<>();
        header.put("Content-Type", "application/x-www-form-urlencoded");
        request.addHeaders(header);
        request.timeout(6000);
        request.form(params);
        String post = request.execute().body();
//        String post = HttpUtil.post(thirdPartyUrl + method, params, 3000);

        GCashPayVo gCash3Vo = JSONObject.parseObject(post, GCashPayVo.class);
        if (200 == gCash3Vo.getCode()) {
            return gCash3Vo;
        } else {
            LOGGER.error("请求第三方GCash pay接口失败:" + post);
            throw new BusinessException("请稍后再试");
        }
    }
}
