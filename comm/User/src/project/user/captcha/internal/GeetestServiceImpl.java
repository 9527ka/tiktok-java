package project.user.captcha.internal;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

import project.user.captcha.GeetestService;

/**
 * Geetest滑动图片验证
 */
public class GeetestServiceImpl implements GeetestService {

	// SDK版本编号
	protected final String verName = "3.3.0";
	// SD的语言类型
	protected final String sdkLang = "java";

	//极验验证API URL
	protected final String apiUrl = "http://api.geetest.com"; 
	protected final String baseUrl = "api.geetest.com";
	
	//register url
	protected final String registerUrl = "/register.php";
	//validate url
	protected final String validateUrl = "/validate.php"; 
	
	/**
	 * 调试开关，是否输出调试日志
	 */
	public boolean debugCode = true;
	
	/**
	 * 验证初始化预处理
	 * 用captchaID进行注册，更新challenge： 1/注册成功；0/注册失败；
	 * @throws UnsupportedEncodingException 
	 */
	public HashMap<String, String> preProcess(HashMap<String, String> data) {
		
		try {

			String userId = data.get("user_id");
			String geetestId = data.get("geetest_id");
			String geetestKey = data.get("geetest_key");
				
			String getUrl = this.apiUrl + this.registerUrl + "?";
			
			String param = "gt=" + geetestId;
			
			if (userId != null) {
				param = param + "&user_id=" + userId;
			}
			
			this.gtlog("GET_URL:" + getUrl + param);
			
			String result_str = this.readContentFromGet(getUrl + param);
			
			this.gtlog("register_result:" + result_str);

			if (32 == result_str.length()) {
				return this.getSuccessPreProcessRes(this.md5Encode(result_str + geetestKey), geetestId);
			} else {
				this.gtlog("gtServer register challenge failed");
				return this.getFailPreProcessRes(geetestId);
			}
			
		} catch (Throwable t) {
			this.gtlog(t.toString());
			this.gtlog("exception:preProcess api");
		}
		
		return null;
	}

	/**
	 * 预处理失败后的返回格式串
	 */
	private HashMap<String, String> getFailPreProcessRes(String geetestId) {

		Long rnd1 = Math.round(Math.random() * 100);
		Long rnd2 = Math.round(Math.random() * 100);
		String md5Str1 = md5Encode(rnd1 + "");
		String md5Str2 = md5Encode(rnd2 + "");
		String challenge = md5Str1 + md5Str2.substring(0, 2);
		
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put("success", "0");
		ret.put("gt", geetestId);
		ret.put("challenge", challenge);
		
		return ret;		
	}

	/**
	 * 预处理成功后的标准串
	 */
	private HashMap<String, String> getSuccessPreProcessRes(String challenge, String geetestId) {
		
		this.gtlog("challenge:" + challenge);
		
		HashMap<String, String> ret = new HashMap<String, String>();
		ret.put("success", "1");
		ret.put("gt", geetestId);
		ret.put("challenge", challenge);

		return ret;
	}
	
	/**
	 * 服务正常的情况下使用的验证方式，,向gt-server进行二次验证，获取验证结果
	 * 验证结果：1/验证成功；0/验证失败；
	 */
	public int enhencedValidateRequest(HashMap<String, String> data) throws UnsupportedEncodingException {	
		
		String userId = data.get("user_id");
		String challenge = data.get("challenge");
		String validate = data.get("validate");
		String seccode = data.get("seccode");
		String geetestKey = data.get("geetest_key");
		
		if (!this.resquestIsLegal(challenge, validate, seccode)) {
			return 0;
		}
		
        this.gtlog("request legitimate");
        
        String host = baseUrl;
		String path = validateUrl;
		int port = 80;
		String query = String.format("seccode=%s&sdk=%s", seccode,
				(this.sdkLang + "_" + this.verName));
		
		String response = "";
		
		if (userId != ""){
			query = query + "&user_id=" + userId;
			userId = "";
		}
		
		this.gtlog(query);
		
		try {
			
			if (validate.length() <= 0) {
				return 0;
			}

			if (!this.checkResultByPrivate(challenge, validate, geetestKey)) {
				return 0;
			}
			
			this.gtlog("checkResultByPrivate");
			
			response = this.postValidate(host, path, query, port);

			this.gtlog("response: " + response);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		this.gtlog("md5: " + this.md5Encode(seccode));

		if (response.equals(this.md5Encode(seccode))) {
			return 1;
		} else {
			return 0;
		}	
	}

	/**
	 * 貌似不是Post方式，后面重构时修改名字
	 */
	protected String postValidate(String host, String path, String data, int port) throws Exception {
		
		String response = "error";
		
		InetAddress addr = InetAddress.getByName(host);
		Socket socket = new Socket(addr, port);
		BufferedWriter wr = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF8"));
		wr.write("POST " + path + " HTTP/1.0\r\n");
		wr.write("Host: " + host + "\r\n");
		wr.write("Content-Type: application/x-www-form-urlencoded\r\n");
		wr.write("Content-Length: " + data.length() + "\r\n");
		// 以空行作为分割
		wr.write("\r\n"); 
		
		// 发送数据
		wr.write(data);
		wr.flush();
		
		// 读取返回信息
		BufferedReader rd = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
		String line;
		while ((line = rd.readLine()) != null) {
			response = line;
		}
		wr.close();
		rd.close();
		socket.close();
		return response;
	}
	
	/**
	 * failback使用的验证方式
	 * @return 验证结果,1表示验证成功0表示验证失败
	 */
	public int failbackValidateRequest(HashMap<String, String> data) {

		this.gtlog("in failback validate");
		
		String challenge = data.get("challenge");
		String validate = data.get("validate");
		String seccode = data.get("seccode");

		if (!this.resquestIsLegal(challenge, validate, seccode)) {
			return 0;
		}
		
		this.gtlog("request legitimate");

		String[] validateStr = validate.split("_");
		String encodeAns = validateStr[0];
		String encodeFullBgImgIndex = validateStr[1];
		String encodeImgGrpIndex = validateStr[2];

		this.gtlog(String.format("encode----challenge:%s--ans:%s,bg_idx:%s,grp_idx:%s",
				challenge, encodeAns, encodeFullBgImgIndex, encodeImgGrpIndex));
			
		int decodeAns = this.decodeResponse(challenge, encodeAns);
		int decodeFullBgImgIndex = this.decodeResponse(challenge, encodeFullBgImgIndex);
		int decodeImgGrpIndex = this.decodeResponse(challenge, encodeImgGrpIndex);

		this.gtlog(String.format("decode----ans:%s,bg_idx:%s,grp_idx:%s", 
				decodeAns, decodeFullBgImgIndex, decodeImgGrpIndex));

		int validateResult = this.validateFailImage(decodeAns, decodeFullBgImgIndex, decodeImgGrpIndex);

		return validateResult;
	}
	
	private int validateFailImage(int ans, int full_bg_index, int img_grp_index) {
		
		// 容差值
		final int thread = 3;
		String full_bg_name = this.md5Encode(full_bg_index + "").substring(0, 9);
		String bg_name = this.md5Encode(img_grp_index + "").substring(10, 19);
		String answer_decode = "";

		// 通过两个字符串奇数和偶数位拼接产生答案位
		for (int i = 0; i < 9; i++) {
			if (i % 2 == 0) {
				answer_decode += full_bg_name.charAt(i);
			} else if (i % 2 == 1) {
				answer_decode += bg_name.charAt(i);
			} else {
				this.gtlog("exception");
			}
		}

		String x_decode = answer_decode.substring(4, answer_decode.length());
		
		// 16 to 10
		int x_int = Integer.valueOf(x_decode, 16);

		int result = x_int % 200;
		if (result < 40) {
			result = 40;
		}

		if (Math.abs(ans - result) <= thread) {
			return 1;
		} else {
			return 0;
		}
	}
	
	/**
	 * 解码随机参数
	 */
	private int decodeResponse(String challenge, String string) {
		
		if (string.length() > 100) {
			return 0;
		}

		int[] shuzi = new int[] { 1, 2, 5, 10, 50 };
		String chongfu = "";
		HashMap<String, Integer> key = new HashMap<String, Integer>();
		int count = 0;

		for (int i = 0; i < challenge.length(); i++) {
			String item = challenge.charAt(i) + "";
			if (chongfu.contains(item) == true) {
				continue;
			} else {
				int value = shuzi[count % 5];
				chongfu += item;
				count++;
				key.put(item, value);
			}
		}

		int res = 0;
		for (int j = 0; j < string.length(); j++) {
			res += key.get(string.charAt(j) + "");
		}
		res = res - decodeRandBase(challenge);
		return res;
	}

	/**
	 * 输入的两位的随机数字,解码出偏移量
	 */
	private int decodeRandBase(String challenge) {

		String base = challenge.substring(32, 34);
		ArrayList<Integer> tempArray = new ArrayList<Integer>();

		for (int i = 0; i < base.length(); i++) {
			char tempChar = base.charAt(i);
			Integer tempAscii = (int) (tempChar);
			Integer result = (tempAscii > 57) ? (tempAscii - 87) : (tempAscii - 48);
			tempArray.add(result);
		}

		int decodeRes = tempArray.get(0) * 36 + tempArray.get(1);
		return decodeRes;
	}

	/**
	 * 发送请求，获取服务器返回结果
	 */
	private String readContentFromGet(String getURL) throws IOException {

		URL getUrl = new URL(getURL);
		HttpURLConnection connection = (HttpURLConnection) getUrl.openConnection();
		// 设置连接主机超时（单位：毫秒）
		connection.setConnectTimeout(2000);
		// 设置从主机读取数据超时（单位：毫秒）
		connection.setReadTimeout(2000);

		// 建立与服务器的连接，并未发送数据
		connection.connect();
		
		// 发送数据到服务器并使用Reader读取返回的数据
		StringBuffer sBuffer = new StringBuffer();

		InputStream inStream = null;
		byte[] buf = new byte[1024];
		inStream = connection.getInputStream();
		for (int n; (n = inStream.read(buf)) != -1;) {
			sBuffer.append(new String(buf, 0, n, "UTF-8"));
		}
		inStream.close();
		// 断开连接
		connection.disconnect();

		return sBuffer.toString();
	}

	/**
	 * 输出debug信息，需要开启debugCode
	 */
	public void gtlog(String message) {
		if (this.debugCode) {
			System.out.println("gtlog: " + message);
		}
	}
	
	/**
	 * 判断一个表单对象值是否为空
	 */
	protected boolean objIsEmpty(Object gtObj) {
		if (gtObj == null) {
			return true;
		}
		if (gtObj.toString().trim().length() == 0) {
			return true;
		}
		return false;
	}

	/**
	 * 检查客户端的请求是否合法,三个只要有一个为空，则判断不合法
	 */
	private boolean resquestIsLegal(String challenge, String validate, String seccode) {
		if (objIsEmpty(challenge)) {
			return false;
		}
		if (objIsEmpty(validate)) {
			return false;
		}
		if (objIsEmpty(seccode)) {
			return false;
		}
		return true;
	}

	protected boolean checkResultByPrivate(String challenge, String validate, String privateKey) {
		String encodeStr = this.md5Encode(privateKey + "geetest" + challenge);
		return validate.equals(encodeStr);
	}

	/**
	 * md5 加密
	 * 
	 * @time 2014年7月10日 下午3:30:01
	 * @param plainText
	 * @return
	 */
	private String md5Encode(String plainText) {
		String re_md5 = new String();
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(plainText.getBytes());
			byte b[] = md.digest();
			int i;
			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}

			re_md5 = buf.toString();

		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return re_md5;
	}
	
	/**
	 * 获取版本信息
	 */
	public String getVersionInfo() {
		return this.verName;
	}
		
}
