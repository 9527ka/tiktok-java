package email;

import java.io.File;
import java.util.Map;

/**
 * 短信发送，异步发送，写入SmsMessageQueue队列返回
 * 
 */
public interface EmailSendService {

	/**
	 * 发送EMAIL
	 * 
	 * @param tomail
	 *            目标邮件地址
	 * @param subject
	 *            邮件标题
	 * @param content
	 *            邮件内容
	 */
	public void sendEmail(String tomail, String subject, String senderName,String content);

	/**
	 * 发送Velocity模板EMAIL
	 * 
	 * 
	 * @param tomail
	 *            目标邮件地址
	 * 
	 * @param subject
	 *            邮件标题
	 * @param ftlname
	 *            模板名称, 模板文件需存放到ftl包下
	 *            如果为空，直接发送content内容，否则根据map和ftlname构造content（邮件内容）
	 *
	 * @param map
	 *            模板参数替换值
	 */
	public void sendEmail(String tomail, String subject, String ftlname, Map<String, Object> map);

	/**
	 * 发送Velocity模板EMAIL
	 * 
	 * 
	 * @param tomail
	 *            目标邮件地址
	 * 
	 * @param subject
	 *            邮件标题
	 * @param ftlname
	 *            模板名称, 模板文件需存放到ftl包下
	 *            如果为空，直接发送content内容，否则根据map和ftlname构造content（邮件内容）
	 *
	 * @param map
	 *            模板参数替换值
	 *   @param file
	 *            附件   
	 *   @param filename
	 *            附件名称        
	 */
	public void sendEmail(String tomail, String subject, String content, String ftlname, Map<String, Object> map, File file, String filename);

}
