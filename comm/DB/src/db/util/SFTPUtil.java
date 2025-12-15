package db.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Vector;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTPUtil {
	private transient Logger log = LoggerFactory.getLogger(this.getClass());

	private ChannelSftp sftp;
	private ChannelExec exec;
	public static final String SFTP = "sftp";
	public static final String SHELL = "exec";
	private Session session;
	/** FTP 登录用户名 */
	private String username;
	/** FTP 登录密码 */
	private String password;
	/** 私钥 */
	private String privateKey;
	/** FTP 服务器地址IP地址 */
	private String host;
	/** FTP 端口 */
	private int port;

	/**
	 * 构造基于密码认证的sftp对象
	 * 
	 * @param userName
	 * @param password
	 * @param host
	 * @param port
	 */
	public SFTPUtil(String username, String password, String host, int port) {
		this.username = username;
		this.password = password;
		this.host = host;
		this.port = port;
	}

	/**
	 * 构造基于秘钥认证的sftp对象
	 * 
	 * @param userName
	 * @param host
	 * @param port
	 * @param privateKey
	 */
	public SFTPUtil(String username, String host, int port, String privateKey) {
		this.username = username;
		this.host = host;
		this.port = port;
		this.privateKey = privateKey;
	}

	public SFTPUtil() {
	}

	/**
	 * 连接sftp服务器
	 * 
	 * @throws Exception
	 */
	public void login(String type) {
		try {
			JSch jsch = new JSch();
			if (privateKey != null) {
				jsch.addIdentity(privateKey);// 设置私钥
//				log.info("sftp connect,path of private key file：{}" , privateKey);   
			}
//			log.info("sftp connect by host:{} username:{}",host,username);   

			session = jsch.getSession(username, host, port);
//			log.info("Session is build");
			if (password != null) {
				session.setPassword(password);
			}
			Properties config = new Properties();
			config.put("StrictHostKeyChecking", "no");

			session.setConfig(config);
			session.connect();
//			log.info("Session is connected");

			Channel channel = session.openChannel(type);
			channel.connect();
			if (SFTP.equals(type)) {
				log.info("sftp channel is connected");
				sftp = (ChannelSftp) channel;
			} else if (SHELL.equals(type)) {
				log.info("shell channel is connected");
				exec = (ChannelExec) channel;
			}
//			Channel channel = session.openChannel("sftp");   
//			log.info(String.format("sftp server host:[%s] port:[%s] is connect successfull", host, port));   
		} catch (JSchException e) {
//			log.error("Cannot connect to specified sftp server : {}:{} \n Exception message is: {}", new Object[]{host, port, e.getMessage()});     
			log.error("Cannot connect to specified sftp server  \n Exception message is: {}",
					new Object[] { e.getMessage() });
		}
	}

	/**
	 * 关闭连接 server
	 */
	public void logout() {
		if (sftp != null) {
			if (sftp.isConnected()) {
				sftp.disconnect();
				log.info("sftp is closed already");
			}
		}
		if (session != null) {
			if (session.isConnected()) {
				session.disconnect();
				log.info("sshSession is closed already");
			}
		}
	}

	/**
	 * 将输入流的数据上传到sftp作为文件
	 * 
	 * @param directory    上传到该目录
	 * @param sftpFileName sftp端文件名
	 * @param in           输入流
	 * @throws SftpException
	 * @throws Exception
	 */
	public void upload(String directory, String sftpFileName, InputStream input) throws SftpException {
		try {
			sftp.cd(directory);
		} catch (SftpException e) {
			log.warn("directory is not exist");
			sftp.mkdir(directory);
			sftp.cd(directory);
		}
		sftp.put(input, sftpFileName);
		log.info("file:{} is upload successful", sftpFileName);
	}

	/**
	 * 上传单个文件
	 * 
	 * @param directory  上传到sftp目录
	 * @param uploadFile 要上传的文件,包括路径
	 * @throws FileNotFoundException
	 * @throws SftpException
	 * @throws Exception
	 */
	public void upload(String directory, String uploadFile) throws FileNotFoundException, SftpException {
		File file = new File(uploadFile);
		upload(directory, file.getName(), new FileInputStream(file));
	}

	/**
	 * 将byte[]上传到sftp，作为文件。注意:从String生成byte[]是，要指定字符集。
	 * 
	 * @param directory    上传到sftp目录
	 * @param sftpFileName 文件在sftp端的命名
	 * @param byteArr      要上传的字节数组
	 * @throws SftpException
	 * @throws Exception
	 */
	public void upload(String directory, String sftpFileName, byte[] byteArr) throws SftpException {
		upload(directory, sftpFileName, new ByteArrayInputStream(byteArr));
	}

	/**
	 * 将字符串按照指定的字符编码上传到sftp
	 * 
	 * @param directory    上传到sftp目录
	 * @param sftpFileName 文件在sftp端的命名
	 * @param dataStr      待上传的数据
	 * @param charsetName  sftp上的文件，按该字符编码保存
	 * @throws UnsupportedEncodingException
	 * @throws SftpException
	 * @throws Exception
	 */
	public void upload(String directory, String sftpFileName, String dataStr, String charsetName)
			throws UnsupportedEncodingException, SftpException {
		upload(directory, sftpFileName, new ByteArrayInputStream(dataStr.getBytes(charsetName)));
	}

	/**
	 * 下载文件
	 * 
	 * @param directory    下载目录
	 * @param downloadFile 下载的文件
	 * @param saveFile     存在本地的路径
	 * @throws SftpException
	 * @throws FileNotFoundException
	 * @throws Exception
	 */
	public void download(String directory, String downloadFile, String saveFile)
			throws SftpException, FileNotFoundException {
		if (directory != null && !"".equals(directory)) {
			sftp.cd(directory);
		}
		File file = new File(saveFile);
		sftp.get(downloadFile, new FileOutputStream(file));
		log.info("file:{} is download successful", downloadFile);
	}

	/**
	 * 下载文件
	 * 
	 * @param directory    下载目录
	 * @param downloadFile 下载的文件名
	 * @return 字节数组
	 * @throws SftpException
	 * @throws IOException
	 * @throws Exception
	 */
	public byte[] download(String directory, String downloadFile) throws SftpException, IOException {
		if (directory != null && !"".equals(directory)) {
			sftp.cd(directory);
		}
		InputStream is = sftp.get(downloadFile);
		byte[] fileData = IOUtils.toByteArray(is);
		log.info("file:{} is download successful", downloadFile);
		return fileData;
	}

	/**
	 * 删除文件
	 * 
	 * @param directory  要删除文件所在目录
	 * @param deleteFile 要删除的文件
	 * @throws SftpException
	 * @throws Exception
	 */
	public void delete(String directory, String deleteFile) throws SftpException {
		sftp.cd(directory);
		sftp.rm(deleteFile);
	}

	/**
	 * 列出目录下的文件
	 * 
	 * @param directory 要列出的目录
	 * @param sftp
	 * @return
	 * @throws SftpException
	 */
	public Vector<?> listFiles(String directory) throws SftpException {
		return sftp.ls(directory);
	}

	public String execCmd(String cmd) {
		String result = null;
		try {
			// 获取输入流和输出流
			InputStream in = exec.getInputStream();
			exec.setCommand(cmd);
			exec.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					result = new String(tmp, 0, i);
				}
				if (exec.isClosed()) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					result = e.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//        	log.error(e.getMessage(), e);
		}
		return result;
	}

	/**
	 * 执行远程命令
	 *
	 * @param ip
	 * @param user
	 * @param psw
	 * @throws Exception
	 */
	public static String execCmd(String ip, String user, String psw, String cmd) throws Exception {
		// 连接服务器，采用默认端口
		JSch jsch = new JSch();
		Session session = jsch.getSession(user, ip);
		Channel channel = connect(session, psw, SHELL);
		String result = null;

		try {
			ChannelExec channelExec = (ChannelExec) channel;
			// 获取输入流和输出流
			InputStream in = channel.getInputStream();
			channelExec.setCommand(cmd);
			channelExec.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					result = new String(tmp, 0, i);
				}
				if (channel.isClosed()) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					result = e.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//        	log.error(e.getMessage(), e);
		} finally {
			session.disconnect();
			channel.disconnect();
		}

		return result;
	}

	/**
	 * 执行远程命令
	 *
	 * @param ip
	 * @param user
	 * @param psw
	 * @throws Exception
	 */
	public static String execCmd(String ip, String user, int port, String psw, String cmd) throws Exception {
		// 连接服务器，采用默认端口
		JSch jsch = new JSch();
		Session session = jsch.getSession(user, ip, port);
		Channel channel = connect(session, psw, SHELL);
		String result = null;

		try {
			ChannelExec channelExec = (ChannelExec) channel;
			// 获取输入流和输出流
			InputStream in = channel.getInputStream();
			channelExec.setCommand(cmd);
			channelExec.connect();

			byte[] tmp = new byte[1024];
			while (true) {
				while (in.available() > 0) {
					int i = in.read(tmp, 0, 1024);
					if (i < 0)
						break;
					result = new String(tmp, 0, i);
				}
				if (channel.isClosed()) {
					break;
				}
				try {
					Thread.sleep(1000);
				} catch (Exception e) {
					result = e.toString();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
//        	log.error(e.getMessage(), e);
		} finally {
			session.disconnect();
			channel.disconnect();
		}

		return result;
	}

	/**
	 * 连接服务器
	 *
	 * @param session
	 * @param psw
	 * @param type
	 * @return
	 * @throws Exception
	 */
	private static Channel connect(Session session, String psw, String type) throws Exception {
		// 如果服务器连接不上，则抛出异常
		if (session == null) {
			throw new Exception("session is null");
		}

		// 设置登陆主机的密码
		session.setPassword(psw);// 设置密码

		// 设置第一次登陆的时候提示，可选值：(ask | yes | no)
		session.setConfig("StrictHostKeyChecking", "no");

		// 设置登陆超时时间
		session.connect(30000);

		// 创建通信通道
		return session.openChannel(type);
	}

	public static void main(String[] args) throws Exception {

//		 String sftpUsername= PropertiesUtilDB.getProperty("backup.username");
//    	String sftpPassword= PropertiesUtilDB.getProperty("backup.password");
//    	String sftpIp= PropertiesUtilDB.getProperty("backup.ip");
//    	int sftpPort= Integer.valueOf(PropertiesUtilDB.getProperty("backup.port"));

//		SFTPUtil sftp = new SFTPUtil(sftpUsername, sftpPassword, sftpIp, sftpPort);    
//		sftp.login(SFTPUtil.SHELL);  
//		sftp.execCmd(cmd);
//		sftp.logout();
	}
}
