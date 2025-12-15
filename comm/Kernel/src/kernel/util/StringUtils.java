/**
 * 
 */
package kernel.util;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 *
 */
public abstract class StringUtils extends org.springframework.util.StringUtils {

	private static final Log logger = LogFactory.getLog(StringUtils.class);

	public static boolean isNullOrEmpty(String string) {
		return string == null || string.trim().length() == 0;
	}

	public static boolean isEmptyString(String str) {
		return isNullOrEmpty(str);
	}

	public static boolean isNotEmpty(String str) {
		return !isNullOrEmpty(str);
	}

	public static String getBlank(int blankNum) {
		StringBuffer blanks = new StringBuffer();
		for (int i = 0; i < blankNum; i++) {
			blanks.append(" ");
		}
		return blanks.toString();
	}

	public static boolean containInArray(String target, String[] list) {
		for (String dest : list) {
			if (dest.equals(target)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Description:简单的字段串连接

	 */
	public static String join(String... strs) {
		return org.apache.commons.lang3.StringUtils.join(strs);
	}

	public static String truncateMessage(String description, int length) {
		Assert.state(length > 0);
		if (description != null && description.length() > length) {
			logger.debug("Truncating long message, original message is: " + description);
			return description.substring(0, length);
		} else {
			return description;
		}
	}

	/**
	 * 将字符串转义为带格式的HTML
	 * 
	 * @param tempMsg
	 * @author Song Lihua
	 * @return 带格式的HTML
	 */
	// public static String wrapForHtml(String tempMsg) {
	// if (isNullOrEmpty(tempMsg)) {
	// return tempMsg;
	// }
	// tempMsg = HtmlUtils.htmlEscape(tempMsg);
	// tempMsg = tempMsg.replaceAll("\r\n", "<br/>");
	// tempMsg = tempMsg.replaceAll("\n", "<br/>");
	// tempMsg = tempMsg.replaceAll("\r", "<br/>");
	// return tempMsg.replaceAll(" ", "&nbsp;");
	// }

	/**
	 * 将日期转化为字符串
	 * 
	 * @param str
	 * @param pattern
	 * @return
	 */
	public static String dateToStr(Date date, String pattern) {
		SimpleDateFormat formater = new SimpleDateFormat(DateUtils.NORMAL_DATE_FORMAT);
		if (pattern != null)
			formater.applyPattern(pattern);
		return formater.format(date);
	}

	/**
	 * 分割参数
	 * 
	 * @param paraSrc String
	 * @param sepa    String
	 * @return Map sample : "a=b,c=d,..."
	 */
	public static Map<String, String> splitPara(String paraSrc, String sepa) {
		if (paraSrc == null || paraSrc.trim().length() == 0) {
			return null;
		}

		LinkedHashMap<String, String> paraMap = new LinkedHashMap<String, String>();
		if (sepa == null || sepa.equals("")) { // 默认分割参数的分隔符为 ","
			sepa = ",";
		}

		/**
		 * 
		 */
		String[] paras = paraSrc.split(sepa);
		for (int i = 0, j = 0; i < paras.length; i++) {
			String tmpResult[] = paras[i].split("=");
			if (tmpResult.length >= 2) { // 2 a=b
				paraMap.put(tmpResult[0].trim(), tmpResult[1]);
			} else if (tmpResult.length == 1) {
				if (paras[i].indexOf("=") >= 0) { // 1 a=
					paraMap.put(tmpResult[0].trim(), "");
				} else { // 0 a
					paraMap.put("TEXT." + j, paras[i]);
					j++;
				}
			}
		}

		return paraMap;
	}

	/**
	 * return String basename
	 * 
	 * @param name  String
	 * @param split String
	 * @return String com.xxx.ne --> ne
	 */
	public static String pathname(String name, String split) {
		if (name == null || name.equals("")) {
			return "";
		}
		if (split == null || split.equals("")) {
			split = ".";
		}

		int index = name.lastIndexOf(split);
		if (index >= 0) {
			return name.substring(0, index);
		}

		return name;
	}

	/**
	 * return String basename
	 * 
	 * @param name  String
	 * @param split String
	 * @return String com.xxx.ne --> ne
	 */
	public static String basename(String name, String split) {
		if (name == null || name.equals("")) {
			return "";
		}
		if (split == null || split.equals("")) {
			split = ".";
		}

		int index = name.lastIndexOf(split);
		if (index >= 0) {
			return name.substring(index + split.length());
		}

		return "";
	}

	/**
	 * 替换符合正则表达式的所有子字符串为新字符串
	 * 
	 * @param src     String
	 * @param pattern String
	 * @param to      String
	 * @return String
	 */
	public static String replaceAll(String src, String pattern, String to) {
		if (src == null) {
			return null;
		}
		if (pattern == null) {
			return src;
		}

		StringBuffer sb = new StringBuffer();
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(src);

		int i = 1;
		while (m.find()) {
			// System.out.println("找到第" + i + "个匹配:" + m.group() +
			// " 位置为:" + m.start() + "-" + (m.end() - 1));
			m.appendReplacement(sb, to);
			i++;
		}
		m.appendTail(sb);
		return sb.toString();
	}

	/**
	 * 返回某字符串中所有符合正则表达式的子字符串，以字符串数组形式返回
	 * 
	 * @param src     String
	 * @param pattern String
	 * @return String[]
	 */
	public static String[] findAll(String src, String pattern) {
		return findAll(src, pattern, 0);
	}

	/**
	 * <p>
	 * Description:
	 * </p>

	 */
	public static String[] findAll(String src, String pattern, int flags) {

		if (src == null) {
			return new String[0];
		}
		if (pattern == null) {
			return new String[0];
		}

		Pattern p = Pattern.compile(pattern, flags);
		Matcher m = p.matcher(src);
		Collection<String> l = new ArrayList<String>();
		while (m.find()) {
			l.add(m.group());
		}

		return l.toArray(new String[] {});

	}

	/**
	 * 是否字符串匹配
	 * 
	 * @param src    String
	 * @param regexp String
	 * @return boolean
	 */
	public static boolean match(String src, String regexp) {
		Pattern p = Pattern.compile(regexp);
		Matcher m = p.matcher(src);
		return m.find();
		// return m.matches(); 090413 modified

	}


	public static Locale toLocale(String locale) {
		if (StringUtils.hasText(locale)) {
			String[] values = null;
			if (locale.indexOf("_") > 0) {
				values = split(locale, "_");
			} else if (locale.indexOf("-") > 0) {
				values = split(locale, "-");
			} else {
				values = new String[0];
			}
			if (values.length == 1) {
				return new Locale(values[0]);
			}
			if (values.length == 2) {
				return new Locale(values[0], values[1]);
			}
			if (values.length == 3) {
				return new Locale(values[0], values[1], values[2]);
			}
		}
		return null;
	}

	// public static void main(String args[]) {
	// System.out.println(StringUtils.match("SMP#", "#"));// false ?
	// System.out.println(StringUtils.match("SMP#", ".*?#"));// true
	// System.out.println(StringUtils.match("\r\nff P720 login:",
	// ".*?name:|.*?ogin:"));// false
	// System.out.println(StringUtils.verifyEmail("x@.com"));
	// // ?
	// }

	/**
	 * Stringת����UTF-8�����ַ�����
	 * 
	 * @param str
	 * @return byte[]
	 */
	public static byte[] getUtf8Bytes(String str) {
		try {
			return str.getBytes("UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return str.getBytes();
		}
	}

	/**
	 * UTF-8��ʽ�ַ�����ת����String
	 * 
	 * @param utf8
	 * @return String
	 */
	public static String getStringFromUtf8(byte[] utf8) {
		try {
			return new String(utf8, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			return new String(utf8);
		}
	}

	/**
	 * ��list�е�ֵʹ�ö���ƴ����4����
	 * 
	 * @param list
	 * @return String
	 */
	public static String toString(List<?> list) {
		if (list == null) {
			return "";
		}
		StringBuffer stringBuffer = new StringBuffer(256);
		for (Iterator<?> iter = list.iterator(); iter.hasNext();) {
			stringBuffer.append((String) iter.next());
			if (iter.hasNext()) {
				stringBuffer.append(",");
			}
		}
		return stringBuffer.toString();
	}

	/**
	 * �������е�ֵʹ�ö���ƴ����4����
	 * 
	 * @param list
	 * @return String
	 */
	public static String toString(Object[] objs) {
		if (objs == null || objs.length == 0) {
			return "";
		}
		return toString(Arrays.asList(objs));
	}

	/**
	 * by yangxw in 2008.03.06 �ַ��滻����
	 * 
	 * @param con ��Ҫ�����滻��ԭ4�ַ�
	 * @param tag Ҫ���滻���ַ�
	 * @param rep �滻�ɵ��ַ�
	 * @return �滻�������µĵ��ַ�
	 */
	@Deprecated
	public static String str_replace(String con, String tag, String rep) {
		int j = 0;
		int i = 0;
		String RETU = "";
		String temp = con;
		int tagc = tag.length();
		while (i < con.length()) {
			if (con.substring(i).startsWith(tag)) {
				temp = con.substring(j, i) + rep;
				RETU += temp;
				i += tagc;
				j = i;
			} else {
				i += 1;
			}
		}
		RETU += con.substring(j);
		return RETU;
	}

	/**
	 * by mengrj 2008-08-01 �ַ�a���Ƿ�����ַ�b���е��ַ�
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	public static boolean isContainChar(String a, String b) {
		if (a == null || b == null)
			return false;
		char[] bset = b.toCharArray();
		for (int i = 0; i < bset.length; i++) {
			for (int j = 0; j < a.length(); j++) {
				if (bset[i] == a.charAt(j))
					return true;
			}
		}
		return false;
	}

	/**
	 * ȥ��HTML��ǣ����ش��ı�
	 * 
	 * @author chenhh
	 * @param htmlStr Ҫת��HTML�ı�
	 * @return String ת����Ĵ��ı�
	 */
	@Deprecated
	public static String htmlToStr(String htmlStr) {
		String regEx = "<\\s*img\\s+([^>]+)\\s*>";
		Pattern p = Pattern.compile(regEx);
		Matcher m = p.matcher(htmlStr);
		return m.replaceAll("").replace("&nbsp;", "");
	}

	public static String toUpperCase(String value) {
		return value == null ? null : value.toUpperCase();
	}

	@Deprecated
	public static String upperCase(String s) {
		return toUpperCase(s);
	}

	public static boolean isSameByTrim(String str1, String str2) {
		if (str1 == null || str1 == null) {
			return false;
		}
		return str1.trim().equals(str2.trim());
	}

	// add by zhoubengang ��ȡ�����ַ�
	@Deprecated
	public static String substring(String str, int toCount, String more) {
		int reInt = 0;
		String reStr = "";
		if (str == null)
			return "";
		char[] tempChar = str.toCharArray();
		for (int kk = 0; (kk < tempChar.length && toCount > reInt); kk++) {
			String s1 = String.valueOf(tempChar[kk]);
			byte[] b = s1.getBytes();
			reInt += b.length;
			reStr += tempChar[kk];
		}
		if (toCount == reInt || (toCount == reInt - 1))
			reStr += more;
		return reStr;
	}

	@Deprecated
	public static String mbToTGM(BigDecimal mb) {
		return NumberUtils.mbToTGM(mb);
	}

	@Deprecated
	public static boolean isNumeric(String str) {
		return NumberUtils.isNumeric(str);
	}

	@Deprecated
	public static String bytesToHexString(byte[] ba) {
		return ByteUtils.bytes2hex(ba);
	}

	@Deprecated
	public static String bytesToHexString(byte[] ba, String prefix) {
		if ((ba == null) || (prefix == null)) {
			throw new NullPointerException();
		}

		StringBuffer sb = new StringBuffer();
		sb.append(prefix);
		sb.append(ByteUtils.bytes2hex(ba));
		return sb.toString();
	}

	@Deprecated
	public static String bytesToHexStringForDebug(byte[] ba, String prefix) {
		return bytesToHexString(ba, prefix);
	}

	@Deprecated
	public static byte[] hexStringToBytes(String hexStr) {
		return ByteUtils.hex2bytes(hexStr);
	}

	@Deprecated
	public static byte[] hexStringToBytes(String hexStr, String prefix) {
		if ((hexStr == null) || (prefix == null)) {
			throw new NullPointerException();
		}
		String myHexStr = hexStr.trim();
		if (myHexStr.startsWith(prefix)) {
			myHexStr = myHexStr.substring(prefix.length());
		}

		return ByteUtils.hex2bytes(myHexStr);
	}

	@Deprecated
	public static byte[] intToByte4(int i) {
		return ByteUtils.int2bytes(i);
	}

	@Deprecated
	public static byte[] shortToByte2(short i) {
		return ByteUtils.short2bytes(i);
	}

	@Deprecated
	public static int byte4ToInt(byte[] b) {
		return ByteUtils.bytes2int(b);
	}

	@Deprecated
	public static short byte2ToShort(byte[] b) {
		return (short) ByteUtils.byte2short(b);
	}

	public static String emptyIfNull(String str) {
		if (isNullOrEmpty(str)) {
			return "";
		}
		return str;
	}
	
	/**
	  * 判断是否为整数 
	  * @param str 传入的字符串 
	  * @return 是整数返回true,否则返回false 
	*/
	public static boolean isInteger(String str) {  
		Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");  
		return pattern.matcher(str).matches();  
	}
	
	/** 
	  * 判断是否为浮点数，包括double和float 
	  * @param str 传入的字符串 
	  * @return 是浮点数返回true,否则返回false 
	*/  
	public static boolean isDouble(String str) {  
		Pattern pattern = Pattern.compile("^[-\\+]?[.\\d]*$");  
		return pattern.matcher(str).matches();  
	}

	/** 
	  * 判断是否为日期格式
	  * @param str 传入的字符串 
	  * @return 是日期格式返回true,否则返回false 
	*/
	public static boolean isValidDate(String str) {	
	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
	    	// 设置lenient为false. 否则SimpleDateFormat会比较宽松地验证日期，比如2007/02/29会被接受，并转换成2007/03/01
	        format.setLenient(false);
	        format.parse(str);
	        return true;
	    } catch (ParseException e) {
	        // 如果throw java.text.ParseException或者NullPointerException，就说明格式不对
	        //e.printStackTrace();        
	    } 
	    return false;
	}

	/**
	 *  对参数进行解码，并对特殊符号进行处理
	 * @param outBuffer
	 * @return
	 */
	public static String replacer(StringBuffer outBuffer) {
		String data = outBuffer.toString();
		try {
			data = data.replaceAll("%(?![0-9a-fA-F]{2})", "%25");
			data = data.replaceAll("\\+", "%2B");
			data = URLDecoder.decode(data, "utf-8");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return data;
	}

	//随机返回字符串数组中的字符串
	public static String RandomStr(String[] strs){
		int random_index = (int) (Math.random()*strs.length);
		return strs[random_index];
	}
}
