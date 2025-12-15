package email.sender;

import java.io.File;
import java.io.Serializable;
import java.util.Map;

/**
 * 
 * <p>Description: 邮件消息类 </p>
 */
public class EmailMessage implements Serializable{
	private static final long serialVersionUID = 3402051115688556553L;

	/**
     * 目标邮件地址
     */
    private String tomail;
    
    /**
     * 邮件标题
     */
    private String subject;

    /**
     * 邮件内容
     */
    private String content;
    
    /**
     * 模板名称,
     * 模板文件需存放到ftl包下
     * 如果为空，直接发送content内容，否则根据map和ftlname构造content（邮件内容）
     */
    private  String ftlname;
    
    /**
     * 模板参数替换值
     */
    private Map<String, Object> map;
    
    /**
     * 附件
     */
    private  File file;
    /**
     * 附件名称
     */
    private  String filename;

	private String senderName;
    /**
     * 无参构造函数
     */
    public EmailMessage() {
    }

    /**
     * 构造函数
     */
    public EmailMessage(String tomail, String subject,String senderName, String content,String ftlname,Map<String, Object> map,File file,String filename) {
        this.tomail = tomail;
        this.subject = subject;
		this.senderName = senderName;
        this.content = content;
        this.ftlname = ftlname;
        this.map = map;
        this.file = file;
        this.filename = filename;
    }

	public String getTomail() {
		return tomail;
	}

	public void setTomail(String tomail) {
		this.tomail = tomail;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getFtlname() {
		return ftlname;
	}

	public void setFtlname(String ftlname) {
		this.ftlname = ftlname;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}


    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }
}
