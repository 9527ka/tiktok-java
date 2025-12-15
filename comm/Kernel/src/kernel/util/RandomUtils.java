package kernel.util;

import java.util.Random;
import java.util.regex.Pattern;

/**
 * random utils
 */
public class RandomUtils {

    /**
     * character table
     */
    private static final char[] CHAR_TABLE = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * number table
     */
    private static final char[] NUM_TABLE = "0123456789".toCharArray();

    /**
     * number table
     */
    private static final char[] NUM_CHAR_TABLE = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    /**
     * genernate random string	
     * @param length length
     * @return random string
     */
    public static String randomString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int pos = random.nextInt(CHAR_TABLE.length);
            sb.append(CHAR_TABLE[pos]);
        }

        return sb.toString();
    }

    /**
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }

    /**
     * genernate random num  
     * @param length length
     * @return random string
     */
    public static String randomNum(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int pos = random.nextInt(NUM_TABLE.length);
            sb.append(NUM_TABLE[pos]);
        }

        return sb.toString();
    }

    /**
     * random byte	
     * @return random byte
     */
    public static byte randomByte() {
        Random random = new Random();
        return (byte) random.nextInt();
    }

    /**
     * genernate random code  
     * @param length length
     * @return random string
     */
    public static String randomCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            int pos = random.nextInt(NUM_CHAR_TABLE.length);
            sb.append(NUM_CHAR_TABLE[pos]);
        }

        return sb.toString();
    }
    
    /**
     * 生成32位唯一的UUID字符串
     * @return
     */
    public static String uuid32() {
    	return java.util.UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 返回min-max之间的随机数
     * @param min(包含)
     * @param max(包含)
     * @return
     */
    public static int randomIntWithMax(int min, int max){
        if(min >= max){
            return min;
        }
        int random = (int) (Math.random()*(max-min+1)+min);
        return random;
    }

}
