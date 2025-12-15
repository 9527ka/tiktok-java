package project.pay;



import ext.TypeConv;
import project.blockchain.MD5;

import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Map;

/* API工具类 */
public class ApiUtil {
    // 签名
    public static String Sign(Map<String, Object> r, String secret) {
        String data = SortedParamsString(r, secret);
        return MD5.sign(data);
    }

    /**
     * 将字典转为字节
     */
    public static String SortedParamsString(Map<String, Object> r, String secret) {
        String[] keys = new String[r.size()];
        int i = 0;
        for (String k : r.keySet()) {
            keys[i++] = k;
        }
        i = 0;
        Arrays.sort(keys);
        StringBuilder b = new StringBuilder();
        for (String key : keys) {
            if (key.equals("sign") || key.equals("sign_type")) {
                continue;
            }
            if (i++ > 0) {
                b.append("&");
            }
            try {
                Object v = r.get(key);
                if (v == null) {
                    throw new Exception("参数值为空:" + key);
                }
                b.append(key).append("=").append(URLDecoder.decode(TypeConv.toString(v), "utf-8"));
            } catch (Throwable ex) {
                ex.printStackTrace();
            }
        }
        b.append(secret);
        return b.toString();
    }
}
