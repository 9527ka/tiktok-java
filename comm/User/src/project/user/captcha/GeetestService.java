package project.user.captcha;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

/**
 * Geetest滑动图片验证
 */
public interface GeetestService {
	
	/**
	 * 验证初始化预处理
	 */
	public HashMap<String, String> preProcess(HashMap<String, String> data);
	
	/**
	 * 获取版本信息
	 */
	public String getVersionInfo();
	
	/**
	 * 服务正常的情况下使用的验证方式，,向gt-server进行二次验证，获取验证结果
	 * 验证结果：1/验证成功；0/验证失败；
	 */
	public int enhencedValidateRequest(HashMap<String, String> data) throws UnsupportedEncodingException;
	
	/**
	 * failback使用的验证方式
	 * 验证结果：1/验证成功；0/验证失败；
	 */
	public int failbackValidateRequest(HashMap<String, String> data);

}
