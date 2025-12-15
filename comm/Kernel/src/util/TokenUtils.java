package util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.Date;

public class TokenUtils {

    //设置过期时间
    private static final long EXPIRE_DATE=300000;
    //token秘钥
    private static final String TOKEN_SECRET = "BP/s2juqOrD8PLEOKu33eP2AA2v6JcjMuho7UNLtbDa7UziUm8Qe7Tk8Wo1aK15j";

    //实现签名方法
    public static String token (String username, String loginUserName, Boolean type, String partyId){

        String token = "";
        try {
            //过期时间
            Date date = new Date(System.currentTimeMillis()+EXPIRE_DATE);
            //秘钥及加密算法
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            //携带username信息，存入token，生成签名
            if(type){
                token = JWT.create()
                        //存储自己想要留给前端的内容
                        .withClaim("username",username)
                        .withClaim("loginUserName",loginUserName).withExpiresAt(date)
                        .sign(algorithm);
            } else {
                token = JWT.create()
                        //存储自己想要留给前端的内容
                        .withClaim("partyId",partyId).withExpiresAt(date)
                        .sign(algorithm);
            }


        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return token;
    }
    //验证token
    public static boolean verify(String token){
        /**
         * @desc   验证token，通过返回true
         * @params [token]需要校验的串
         **/
        try {
            Algorithm algorithm = Algorithm.HMAC256(TOKEN_SECRET);
            JWTVerifier verifier = JWT.require(algorithm).build();
            DecodedJWT jwt = verifier.verify(token);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            System.out.println("Token超时,需要重新登录");
        }
        return false;
    }

    /**
     * 获取token中信息 userName
     * @param token
     * @return
     */

    public static String getUsername(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("username").asString();

        }catch (JWTDecodeException e){
            e.printStackTrace();
        }
        return null;
    }

    public static String getLoginUserName(String token){
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getClaim("loginUserName").asString();

        }catch (JWTDecodeException e){
            e.printStackTrace();
        }
        return null;
    }
}
