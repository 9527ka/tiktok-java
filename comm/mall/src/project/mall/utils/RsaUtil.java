package project.mall.utils;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import java.io.ByteArrayOutputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class RsaUtil {


    //加密算法RSA
    private static final String KEY_ALGORITHM = "RSA";
    //RSA最大加密明文大小
    private static final int MAX_ENCRYPT_BLOCK = 117;
    //RSA最大解密密文大小
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final String CHARSET = "UTF-8";

    public static Map<String, String> genKeyPair() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        keyPairGen.initialize(1024);
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
        String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
        Map<String, String> retMap = new HashMap<>();
        retMap.put("pubKey", publicKeyString);
        retMap.put("priKey", privateKeyString);
        return retMap;
    }

    public static RSAPrivateKey getPrivateKey(String privateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过PKCS#8编码的Key指令获得私钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        PKCS8EncodedKeySpec pkcs8KeySpec = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
        RSAPrivateKey key = (RSAPrivateKey) keyFactory.generatePrivate(pkcs8KeySpec);
        return key;
    }

    /**
     * 得到公钥
     *
     * @param publicKey 密钥字符串（经过base64编码）
     * @throws Exception
     */
    public static RSAPublicKey getPublicKey(String publicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        //通过X509编码的Key指令获得公钥对象
        KeyFactory keyFactory = KeyFactory.getInstance(KEY_ALGORITHM);
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
        RSAPublicKey key = (RSAPublicKey) keyFactory.generatePublic(x509KeySpec);
        return key;
    }

    /**
     * @param signSrc    源
     * @param cipherText 待解密数据
     * @param publickey  公钥
     * @return
     * @throws Exception
     */
    public static boolean verifySignByPub(String signSrc, String cipherText, String publickey) {
        Boolean verifySign = false;
        try {
            String decryptSign = decryptByPublic(cipherText, getPublicKey(publickey));
            if (signSrc.equalsIgnoreCase(decryptSign)) {
                verifySign = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return verifySign;
    }


    /**
     * 私钥加密
     *
     * @param data       源
     * @param privateKey 私钥
     * @return
     */
    public static String encryptByPrivate(String data, String privateKey) {
        try {
            //获取私钥
            RSAPrivateKey rsaPrivateKey = getPrivateKey(privateKey);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, rsaPrivateKey);
            return Base64.encodeBase64URLSafeString(rsaSplitCodec(cipher, Cipher.ENCRYPT_MODE, data.getBytes(CHARSET), rsaPrivateKey.getModulus().bitLength()));
        } catch (Exception e) {
            throw new RuntimeException("私钥加密字符串[" + data + "]时异常", e);
        }
    }


    /**
     * 公钥解密
     *
     * @param data      源
     * @param publicKey 公钥
     * @return
     */
    public static String decryptByPublic(String data, String publicKey) {
        try {
            RSAPublicKey rsaPublicKey = getPublicKey(publicKey);
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, rsaPublicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), rsaPublicKey.getModulus().bitLength()), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("公钥解密字符串[" + data + "]时异常", e);
        }
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param publicKey
     * @return
     */
    public static String decryptByPublic(String data, RSAPublicKey publicKey) {
        try {
            Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, publicKey);
            return new String(rsaSplitCodec(cipher, Cipher.DECRYPT_MODE, Base64.decodeBase64(data), publicKey.getModulus().bitLength()), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("公钥解密字符串[" + data + "]时异常", e);
        }
    }


    private static byte[] rsaSplitCodec(Cipher cipher, int opmode, byte[] datas, int keySize) throws Exception {
        int maxBlock = 0;
        if (opmode == Cipher.DECRYPT_MODE) {
            maxBlock = keySize / 8;
        } else {
            maxBlock = keySize / 8 - 11;
        }

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;
        byte[] buff;
        int i = 0;
        byte[] resultDatas = null;
        try {
            while (datas.length > offSet) {
                if (datas.length - offSet > maxBlock) {
                    buff = cipher.doFinal(datas, offSet, maxBlock);
                } else {
                    buff = cipher.doFinal(datas, offSet, datas.length - offSet);
                }
                out.write(buff, 0, buff.length);
                i++;
                offSet = i * maxBlock;
            }
            resultDatas = out.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("加解密阀值为[" + maxBlock + "]的数据时发生异常", e);
        } finally {
            out.close();
        }
        return resultDatas;
    }

    public static String buildSignSrc(Map params) {

        TreeMap<String, String> tempMap = new TreeMap<String, String>();
        for (Object key1 : params.keySet()) {
            String key = (String) key1;
            // 空参数不参与签名的参数
            Object o = params.get(key);
            if (o == null) {
                continue;
            }
            String value = o.toString();
            if ("".equals(value) || value == null) {
                continue;
            }
            tempMap.put(key, value);
        }

        StringBuilder buf = new StringBuilder();
        for (String key : tempMap.keySet()) {
            buf.append(key).append("=").append(tempMap.get(key)).append("&");
        }
        String src = "";
        if (buf.toString().length() > 0) {
            src = buf.substring(0, buf.length() - 1);
        }
        return src;
    }

    public static void main(String[] args) throws Exception {
        Map<String, String> retMap = RsaUtil.genKeyPair();
        String pubKey = retMap.get("pubKey");
        String priKey = retMap.get("priKey");

//        pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCa9Y3k6V82ZA5BRfbALLMvNy+ezffVRZ+R0aEQRdL7IK0/MH+0zQagzxP8/rJyvHTIsSjHNjjVeK3JcRuKlYheo+aXkkqdtlgCbwzg4VRsNqN+I9aToScubzZ7WRljIiX9JuK/2FnbXpd/KoouYNUHU+eISfF1scCRpm/iUDw4qwIDAQAB";

//        priKey = "MIICdQIBADANBgkqhkiG9w0BAQEFAASCAl8wggJbAgEAAoGBAJr1jeTpXzZkDkFF9sAssy83L57N99VFn5HRoRBF0vsgrT8wf7TNBqDPE/z+snK8dMixKMc2ONV4rclxG4qViF6j5peSSp22WAJvDODhVGw2o34j1pOhJy5vNntZGWMiJf0m4r/YWdtel38qii5g1QdT54hJ8XWxwJGmb+JQPDirAgMBAAECgYB2iOfl927R2G/fFnnOP2NB9uEChWPTTNLnZIfkPsVJPJHKCHtn/n3XfRA5M0ul4OskqgAbgyqugQXPVipPNFt/eLUhfkxTV0plYiR6pYmqHplItCb5x0qwKn3f+vr4KgxKIS1j50kvjjmKvurCealiugQr7cF6Kw2wgdkRGgCloQJBANA0lC/Q1gd22hWG5G0e+nCdmIT5asZnaZzKvfa8Z0QACS+ufCCwTJ/LVclNqe28bQLmbIGzsRUupKrmHz13nPcCQQC+h+diKPdRlbNDwpYWrRok8jB5jA7m3AAc4ke0iu0tHPbwu0Z97sRd4/5xHsUYkYXvK0dHpubC49tgrhW+DVjtAkBx4GAwqvN1T69GWOo1ON1XnQrfqB+bdtJP/J/cBP4iNjbQCoo/ws2WFLvOB5lqu0WQcYFli2fOox1Tj9wEXZjPAkAeHPCvDPZ5yIU6smhyUQ2OxVBwBXjdd+v0pLbcjBCMTsWgqpirkq8qf7xZOJnCFk3qzRZI9tIF/2lO0HTTQtzJAkAf7sRXJTph90m6UPZ0nQ5P+YQXlrNKBFbEbDM3eejJony/myjcN7T+y4D00siu+0d6+lEFOQyVimY99VVdvn/5";

        System.out.println("生成公钥\n" + pubKey);
        System.out.println("公钥长度\n" + pubKey.length());

        try {
            RSAPublicKey publicKey = RsaUtil.getPublicKey(pubKey);
            if (publicKey != null) {
                System.out.println("校验公钥合法性\n" + true);
            }
        } catch (Exception e) {
            System.out.println("校验公钥合法性\n" + false);
        }

        System.out.println("生成私钥\n" + priKey);
        System.out.println("私钥长度\n" + priKey.length());


        String message = "bankCode=TPB&busi_code=100303&ccy_no=INR&countryCode=IND&goods=goods&mer_no=gm761100000033104&mer_order_no=20210907123407abc&notifyUrl=https://google.com&order_amount=100.00&pemail=test@gmail.com&phone=123&pname=zhangsan";
        System.out.println("明文\n" + message);
        System.out.println("明文长度\n" + message.length());

        String priCipherText = RsaUtil.encryptByPrivate(message, priKey);
        System.out.println("私钥加密后密文\n" + priCipherText);

        priCipherText = URLDecoder.decode(priCipherText, "UTF-8");
        System.out.println("URL解码后" + priCipherText + "\n");

        priCipherText = URLEncoder.encode(priCipherText, "UTF-8");
        System.out.println("URL编码后" + priCipherText + "\n");

        String priPlainText = RsaUtil.decryptByPublic(priCipherText, pubKey);
        System.out.println("公钥解密后明文\n" + priPlainText);

        boolean verifySign = RsaUtil.verifySignByPub(message, priCipherText, pubKey);
        System.out.println("验签结果\n" + verifySign);
    }
}
