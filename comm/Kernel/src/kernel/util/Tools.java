package kernel.util;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Tools {

	/**
	 * 验证手机号码
	 * @param phone true 是对的，false 是错误
	 * @return
	 */
	public static boolean isPhone(String phone){
		
		Pattern p = Pattern.compile("^((13[0-9])|(14[0-9])|(15[0-9])||(17[0-9])||(18[0-9]))\\d{8}$");  
		  
		Matcher m = p.matcher(phone);  

		return m.matches();  
	}
	

	/**
	 * 验证邮箱地址
	 * @param phone true 是对的，false 是错误
	 * @return
	 */
	public static boolean isEmail(String email){
		
		 Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

    	 Matcher matcher = pattern.matcher(email);

    	 return matcher.matches();   
	}


  	//保留两位小数点
	public static double roundHalfUp(double f){
	
		BigDecimal   b   =   new   BigDecimal(f);  
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  

	}
	
	/**
	 * 美元转人民币
	 * @param dollar
	 * @return
	 */
	public static Double DollarToRMB(Double dollar){
		
//		return roundHalfUp(dollar* Constants.RMB_DOLLAR_CONVERSION_RATE);
		return 0.0;
	}
	
	public static boolean isEmpty(String string) {
		if("null".equals(string))return true;
		return string == null || string.trim().length() == 0;
	}
    /**
     * 用来判断是否为数字
     * @param   str   String   
     * @return  true 匹配，false 不匹配
     */
	public static boolean  verifeNum(String str){
		try{ 
			Double.valueOf(str);
		}catch(NumberFormatException nb){
			return false;
			
		}
		return true;
 }
}


