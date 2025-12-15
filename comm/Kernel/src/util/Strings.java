package util;

import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Strings {


	/**
	 * 正则表达式：验证数字
	 */
	public static final String REGEX_NUMBER = "^-?\\d+$";
	/**
	 * 正则表达式：验证用户名
	 */
	public static final String REGEX_USERNAME = "^[a-zA-Z]\\w{5,17}$";

	/**
	 * 正则表达式：验证密码
	 */
	public static final String REGEX_PASSWORD = "^[a-zA-Z0-9]{6,16}$";

	/**
	 * 正则表达式：验证手机号
	 */
	public static final String REGEX_MOBILE = "^((13[0-9])|(15[^4,\\D])|(14[0-9])|(18[0-9])|(17[0-9]))\\d{8}$";

	public static final String REGEX_TEL_PHONE = "^((13[3])|(15[3])|(18[0])|(18[1])|(18[9])|(17[7]))\\d{8}$";
	public static final String REGEX_UNICOM_PHONE = "^((13[0])|(13[1])|(13[2])|(15[5])|(15[6])|(18[5])|(18[6])|(14[5])|(17[7]))\\d{8}$";
	public static final String REGEX_MOBILE_PHONE = "^((13[4])|(13[5])|(13[6])|(13[7])|(13[8])|(13[9])|(14[7])|(15[0])|(15[1])|(15[2])|(15[7])|(15[8])|(15[9])|(17[8])|(18[2])|(18[3])|(18[4])|(18[7])|(18[8]))\\d{8}$";


	/**
	 * 正则表达式：验证邮箱
	 */
	//public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
	public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-_\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

	/**
	 * 正则表达式：验证汉字
	 * "[//u4E00-//u9FA5//uF900-//uFA2D]";
	 */
	public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

	/**
	 * 正则表达式：验证身份证
	 */
	public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

	/**
	 * 正则表达式：验证URL
	 */
	public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

	/**
	 * 正则表达式：验证IP地址
	 */
	public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";

	public static String unqualify(String name) {
		return unqualify(name, '.');
	}

	public static String unqualify(String name, char sep) {
		return name.substring(name.lastIndexOf(sep) + 1, name.length());
	}
	
	/**list是否为空
	 * true 空；false 不空
	 * @param list
	 * @return
	 */
	public static boolean ListIsNull(List list){
		if(null!=list&&list.size()>=1)
			return false;
		else return true;
	}
	
	public static boolean isEmpty(String string) {
		if("null".equals(string))return true;
		return string == null || string.trim().length() == 0;
	}

	public static boolean isEmpty(Integer integer) {
		
		return integer==null;
	}

	public static String nullIfEmpty(String string) {
		return isEmpty(string) ? null : string;
	}

	public static String emptyIfNull(String string) {
		return (string == null || "null".equals(string)) ? "" : string.trim();
	}
	public static String trimString(String string){
		
		return string != null ? string.trim() : string;
	}

	public static String toString(Object component) {
		try {
			PropertyDescriptor[] props = Introspector.getBeanInfo(
					component.getClass()).getPropertyDescriptors();
			StringBuilder builder = new StringBuilder();
			for (PropertyDescriptor descriptor : props) {
				builder.append(descriptor.getName()).append("=").append(
						descriptor.getReadMethod().invoke(component)).append(
						"; ");
			}
			return builder.toString();
		} catch (Exception e) {
			return "";
		}
	}

	public static String[] split(String strings, String delims) {
		if (strings == null) {
			return new String[0];
		} else {
			StringTokenizer tokens = new StringTokenizer(strings, delims);
			String[] result = new String[tokens.countTokens()];
			int i = 0;
			while (tokens.hasMoreTokens()) {
				result[i++] = tokens.nextToken();
			}
			return result;
		}
	}

	public static String toString(Object... objects) {
		return toString(" ", objects);
	}

	public static String toString(String sep, Object... objects) {
		if (objects.length == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) {
			builder.append(sep).append(object);
		}
		return builder.substring(2);
	}

	public static String toClassNameString(String sep, Object... objects) {
		if (objects.length == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		for (Object object : objects) {
			builder.append(sep);
			if (object == null) {
				builder.append("null");
			} else {
				builder.append(object.getClass().getName());
			}
		}
		return builder.substring(2);
	}

	public static String toString(String sep, Class... classes) {
		if (classes.length == 0)
			return "";
		StringBuilder builder = new StringBuilder();
		for (Class clazz : classes) {
			builder.append(sep).append(clazz.getName());
		}
		return builder.substring(2);
	}

	public static String toString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] b = new byte[4096];
		for (int n; (n = in.read(b)) != -1;) {
			out.append(new String(b, 0, n));
		}
		return out.toString();
	}

	public static String capfirstChar(String msg) {
		char[] msgs = msg.toCharArray();
		msgs[0] = (char) (msgs[0] - 32);
		return String.valueOf(msgs);
	}
	
	/**
	 * 截取字符串
	 * @param str
	 * @param length
	 * @return
	 */
	public static String getStr(String str,int length) {
		if(isEmpty(str)) {
			return "";
		} else {
			if(str.length()<=length) {
				return str;
			} else {
				return str.substring(0,length);
			}
		}
			
	}
	
	/**
	 * 截取字符串
	 * @param str
	 * @param length
	 * @return
	 */
	public static String getStrMore(String str,int length,String more) {
		if (isEmpty(str)) {
			return "";
		} else {
			if(str.length()<=length) {
				return str;
			} else {
				return str.substring(0,length)+more;
			}
		}
			
	}


	
   /**
    * 校验搜索关键字是否含有非法字符
    * @param s
    * @return true 含有非法字符  false 没有含有非法字符
    */

	public static boolean isSearch(String s) {
		//用来关键字搜索。匹配由数字、26个英文字母或者下划线组成的字符串 
		

		if (!Strings.isEmpty(s)) {
			//return RegexUtil.match(s.trim(),"^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1,6}$");
			//String all  = "^[\\u4E00-\\u9FA5\\uF900-\\uFA2D\\w]{1,6}$";   
			//Pattern pattern = Pattern.compile(all);   
			//return pattern.matcher(s).matches();   

		}
		return true;
	}
	
	
	  /*
	   * 按字节长度截取字符串
	   * @param str 将要截取的字符串参数
	   * @param toCount 截取的字节长度
	   * @return 返回截取后的字符串
	   * @日期 2008-01-25 cwj
	   */
	  public static String substring(String str, int size) {
		  if(Strings.isEmpty(str))return "";
		  int n=0;
		  int k=0;
		  String t="";;
		  for (int i=0; i<str.length(); i++) {
			  if (k < size) {
				    t=t+String.valueOf(str.charAt(i));
					n = (int)str.charAt(i);
					if ((19968 <= n && n <40623)) {//是中文
						
					    k=k+2;
					} else {
						k=k+1;
					}
			  }
		  }

		  return t.length()<str.length() ? t+".." : t;
	  }
	
	  
	/**
	  * 用来判断是否为数字
	  * @param   str   String
	  * @return  true 匹配，false 不匹配
	  */
      public static boolean verifeNum(String str) {
     		try {
     			Double.valueOf(str);
     		} catch(NumberFormatException nb){
     			return false;
     		}
     		return true;
      }

      /**
	      * 用来判断是否为数字
	      * @param   str   String   
	      * @return  true 匹配，false 不匹配
	      */
   public static boolean  verifeLong(String str){
  		try{ 
  			Long.valueOf(str);
  		}catch(NumberFormatException nb){
  			return false;
  			
  		}
  		return true;
   }
	/**
	  * 用来判断是否为Float数
	  * @param   str   String
	  * @return  true 匹配，false 不匹配
	  */
   public static boolean  verifeFloat(String str){
  		try{ 
  			Float.valueOf(str);
  		}catch(NumberFormatException nb){
  			return false;
  			
  		}
  		return true;
   }
      /**
       * 随机数
       * @param num
       * @return
       */
      public  static String getRandomNum(String beginNum,int count){
		
		  	for(int i=0;i<count;i++){
		  		int j = (int)(java.lang.Math.random()*10);
		  		beginNum+=String.valueOf(j);
		  	}
		  	return beginNum;
	} 
      

  	//保留两位小数点
	public static float roundHalfUp(float f){
	
		BigDecimal   b   =   new   BigDecimal(f);  
		return b.setScale(4,   BigDecimal.ROUND_HALF_UP).floatValue();  

	}
  	//保留两位小数点
	public static double roundHalfUp(double f){
	
		BigDecimal   b   =   new   BigDecimal(f);  
		return b.setScale(2,   BigDecimal.ROUND_HALF_UP).doubleValue();  

	}
    /**   
      * 用来判断邮箱是否合法   
      * @param   email   String   
      * @return   boolean   
      */   
     public static boolean verifyEmail(String   email){   
    	 Pattern pattern = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

    	 Matcher matcher = pattern.matcher(email);

    	 return matcher.matches();   
      }
     
     /**   
      * 帐号名称必须为3-30位的英文、数字、下划线或小数点
      * @param   email   String   
      * @return   boolean    true 合法，false 不合法
      */   
     public static boolean verifyUserName(String userName){
          String  patten = "^[a-z0-9_.]{3,30}$";   
          if(userName.length()!=0)   
              if(!userName.matches(patten)){   
                return   false;   
            }   
           return true;   
      }
     
//     //过滤关键字
//     public static String htmlEncoding(String input){
//    	 if(!Strings.isEmpty(input)){
//    		 input = HtmlUtils.htmlEscapeDecimal(input);
//    		 input = StringEscapeUtils.escapeSql(input);
//    		 return input;
//    	 }
//    	 return "";
//     }
//     
 	public static String split(String value){
		if(value==null){
			return null;
		}
		StringBuffer result=new StringBuffer(value.length());
		for(int i=0;i<value.length();++i){
			switch(value.charAt(i)){
			case '|':
				result.append("");
				break;
			case '&':
				result.append("");
				break;
			case ';':
				result.append("");
				break;
			case '$':
				result.append("");
				break;
			case '%':
				result.append("");
				break;
			case '\'':
				result.append("");
				break;
			case '"':
				result.append("");
				break;
			case '\\':
				result.append("");
				break;
			case '<':
				result.append("");
				break;
			case '>':
				result.append("");
				break;
			case '+':
				result.append("");
				break;
			case '\r':
				result.append("");
				break;
			case '\n':
				result.append("");
				break;
			default:
				result.append(value.charAt(i));
				break;
			}
			
		}
		return result.toString();


		
	}
    public static Integer[] convertionToInt(String[] strs){
    	// 将String数组转换为Long类型数组 
    	Integer[] ints = new Integer[strs.length]; 
    	//声明long类型的数组 
    	for(int i = 0;i<strs.length;i++){   
    		String str = strs[i];       
    		//将strs字符串数组中的第i个值赋值给str   
    		int theint = Integer.valueOf(str);
    		//将str转换为long类型，并赋值给thelong   
    		ints[i] = theint;//将thelong赋值给 longs数组中对应的地方  }
    	}
    	return ints;
    }
 	
    public static String fullZero(String str){
    	if(str.length()==1){
    		return "00"+str;
    	}else if(str.length()==2){
    		return "0"+str;
    	}
    	return str;
    	
    }

    /**
     * 校验数字
     * 
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isNumber(String number) {
    	if(Strings.isEmpty(number))return true;
        return Pattern.matches(REGEX_NUMBER, number);
    }
    /**
     * 校验用户名
     * 
     * @param username
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUsername(String username) {
        return Pattern.matches(REGEX_USERNAME, username);
    }
 
    /**
     * 校验密码
     * 
     * @param password
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isPassword(String password) {
        return Pattern.matches(REGEX_PASSWORD, password);
    }
 
    /**
     * 校验手机号
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }
    /**
     * 校验是否符合号码格式
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobileForm(String mobile) {
        return mobile.startsWith("1")&&mobile.length()==11&&isNumber(mobile);
    }
    /**
     * 校验电信手机号码
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isTelPhone(String phone) {
        return Pattern.matches(REGEX_TEL_PHONE, phone);
    }
    /**
     * 校验联通手机号码
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUnicomPhone(String phone) {
        return Pattern.matches(REGEX_UNICOM_PHONE, phone);
    }
    /**
     * 校验移动手机号码
     * 
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobilePhone(String phone) {
        return Pattern.matches(REGEX_MOBILE_PHONE, phone);
    }
    /**
     * 校验邮箱
     * 
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }
 
    /**
     * 校验汉字
     * 
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }
 
    /**
     * 校验身份证
     * 
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }
 
    /**
     * 校验URL
     * 
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }
 
    /**
     * 校验IP地址
     * 
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }
    
    /**
     * 验证时间格式
     * true 正确 flase 错误
     */
    public static boolean isDate(String dateStr,String pattern){
     	try{
            
            SimpleDateFormat sdf = new SimpleDateFormat ( pattern ) ;
            sdf.parse ( dateStr ) ;
            return true;
    	} catch (ParseException e) {
			// TODO Auto-generated catch block
			return false;
			
		}
    }

	//根据文件的后缀来显示对应的图标
	public static String getFileIcon(String filePath){
		if(filePath.indexOf("doc")!=-1){
			return "fa-file-word-o";
		}else if(filePath.indexOf("xls")!=-1){
			return "fa-file-excel-o";
		}else if(filePath.indexOf("pdf")!=-1){
			return "fa-file-pdf-o";
		}else if(filePath.indexOf("ppt")!=-1){
			return "fa-file-powerpoint-o";
		}else if(filePath.indexOf("txt")!=-1){
			return "fa-file-text-o";
		}
		return "";
	}

	public static void main(String[] args) {

		System.out.print((int)(1.2/0.5));
//"^([a-z0-9A-Z]+[-_\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";
		String str = "maix222lxm@otlook.com";
		boolean checkEmail = isEmail(str);
		System.out.println("====> checkEmail: " + checkEmail);

//		for(int i=0;i<20;i++)
//			//System.out.println(RandomUtil.randomInt(0,1));

		//System.out.println(Strings.verifyEmail("a1123"));

		// TODO Auto-generated method stub
//		//System.out.println(HtmlUtils.htmlEscape(""));
//		//System.out.println(HtmlUtils.htmlEscape(HtmlUtils.htmlEscape("<asd>")));
//
//
//		//System.out.println(JavaScriptUtils.javaScriptEscape("753+Main+Street%27%22%3E%3Ciframe+src%3Dhttp%3A%2F%2Fdemo.testfire.net%3E"));
//		//System.out.println(StringEscapeUtils.escapeHtml("753+Main+Street%27%22%3E%3Ciframe+src%3Dhttp%3A%2F%2Fdemo.testfire.net%3E"));
	}
}
