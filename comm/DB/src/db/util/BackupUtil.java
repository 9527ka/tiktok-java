package db.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.Iterator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import db.Constants;
import db.PropertiesUtilDB;
import db.util.jdbc.DBTools;
import kernel.util.DateUtils;
import kernel.util.Endecrypt;
import kernel.util.StringUtils;
import kernel.util.ThreadUtils;
import project.log.SysLog;
import project.log.SysLogService;
import project.syspara.SysparaService;

public class BackupUtil {
	private static transient Logger log = LoggerFactory.getLogger(BackupUtil.class);
	private static String KEY = "Roj6#@08SDF87323FG00%jjsd";
	/**
	 * 局部备份
	 */
	public static String TYPE_PART = "part";
	/**
	 * 全局备份
	 */
	public static String TYPE_ALL = "all";
	/**
	 * 局部+全局备份
	 */
	public static String TYPE_BOTH = "both";
	private static Endecrypt endecrypt = new Endecrypt();

	public static void main(String[] args) {
//		backup(null);
//		backupPart(null);
	}

	public static String executeLinuxCmd(String cmd) {
//        System.out.println("got cmd job : " + cmd);
        Runtime run = Runtime.getRuntime();
        try {
        	Process process = null;
        	if(cmd.indexOf(">")!=-1||cmd.indexOf("|")!=-1||cmd.indexOf("\\")!=-1) {
        		String[] command = { "/bin/sh", "-c", cmd};
        		process = run.exec(command);
        	}else {
        		process = run.exec(cmd);
        	}
//            Process process = run.exec(cmd);
            InputStream in = process.getInputStream();
//            BufferedReader bs = new BufferedReader(new InputStreamReader(in));
            // System.out.println("[check] now size \n"+bs.readLine());
            StringBuffer out = new StringBuffer();
            byte[] b = new byte[8192];
            for (int n; (n = in.read(b)) != -1;) {
            	out.append(new String(b, 0, n));
            }
//            System.out.println("job result [" + out.toString() + "]");
            in.close();
            // process.waitFor();
            process.destroy();
            return out.toString();
        } catch (IOException e) {
        	log.error("exec cmd fail e:",e);
            e.printStackTrace();
        }
        return null;
    }
	
	public static void backup(SysLogService sysLogService, SysparaService sysparaService) {
		try {
			log.info("进入全局备份 backup...");
			boolean backup_all_button = sysparaService.find("backup_all_button").getBoolean();
			if (!backup_all_button) {
				return;
			}
			/**
			 * 保留n天的备份，之前清除
			 */
			String backup_stay_days = sysparaService.find("backup_stay_days").getValue();
			String backupType = TYPE_ALL;
			log.info("开始备份...");

			DateFormat sdf = DateUtils.createDateFormat(Constants.DB_BACKUP_TIME_FORMAT);
			String backupName = PropertiesUtilDB.getProperty("db.database.name") + "_" + sdf.format(new Date());

			executeLinuxCmd("mkdir " + PropertiesUtilDB.getProperty("db.backup.path"));
			// 开始备份sql
			String backupDB = backupDB(backupName);
			
			String backResult = executeLinuxCmd(backupDB);
			log.info(backResult);
			// 开始压缩zip
			String zip = zip(backupName);
			log.info(zip);
			String zipResult = executeLinuxCmd(zip);
			log.info(zipResult);
			// 保留n天,删除之前的zip

			String clearBackup = clearBackup(backup_stay_days, backupType);
			log.info(clearBackup);
			String clearBackupResult = executeLinuxCmd(clearBackup);
			log.info(clearBackupResult);
			ThreadUtils.sleep(2000);// 等待备份完成

			handleSftp(sysparaService, PropertiesUtilDB.getProperty("db.backup.path") + "/" + backupName + ".zip",
					backupType, backup_stay_days);
			log.info("备份完成...");
		} catch (Exception e) {
			// TODO: handle exception
			log.error(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss) + " backup fail e:", e);
			SysLog entity = new SysLog();
			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setLog("数据库备份失败  e:" + e);
			sysLogService.saveAsyn(entity);
		}
	}

	/**
	 * 备份文件发送到配置的服务
	 * 
	 * @param filePath       指定文件目录
	 * @param sysparaService
	 */
	public static void handleSftp(SysparaService sysparaService, String filePath, String backupType,
			String backupStayDays) throws Exception {
//		String backup_server_param="[{\"ip\":\"127.0.0.1\",\"port\":\"22\",\"username\":\"root\",\"password\":\"g5xkwp8ET2WCTbtb5aAxeq2%2FQsWR3j35MbsW2bpXDhp0NsW%2BNNfzSA%3D%3D\",\"path\":\"/backup\",\"type\":\"part\"},{\"ip\":\"127.0.0.1\",\"port\":\"22\",\"username\":\"root\",\"password\":\"g5xkwp8ET2WCTbtb5aAxeq2%2FQsWR3j35MbsW2bpXDhp0NsW%2BNNfzSA%3D%3D\",\"path\":\"/backup\",\"type\":\"part\"}]";
		String backup_server_param = sysparaService.find("backup_server_param").getValue();
		if (StringUtils.isEmptyString(backup_server_param)) {
			return;
		}
		JSONArray jsonArray = JSON.parseArray(backup_server_param);
		Iterator<Object> iterator = jsonArray.iterator();
		while (iterator.hasNext()) {
			JSONObject next = (JSONObject) iterator.next();
			
			String type = next.getString("type");
			if (!type.equals(TYPE_BOTH) && !backupType.equals(type)) {
				continue;
			}
			String sftpIp = next.getString("ip");
			int sftpPort = next.getInteger("port");
			String sftpUsername = next.getString("username");
			String sftpPassword = endecrypt.get3DESDecrypt(next.getString("password"), KEY);
			String sftpBackupPath = next.getString("path");
			try {
				sftp(filePath, sftpIp, sftpPort, sftpUsername, sftpPassword, sftpBackupPath, backupStayDays, backupType);
			}catch (Exception e) {
				// TODO: handle exception
				log.error("ip:"+sftpIp+" sftp fail. e:",e);
			}
		}
	}

	/**
	 * 备份文件发送到指定服务
	 * 
	 * @param filePath
	 * @param sftpIp
	 * @param sftpPort
	 * @param sftpUsername
	 * @param sftpPassword
	 * @param sftpBackupPath
	 * @throws Exception
	 */
	public static void sftp(String filePath, String sftpIp, int sftpPort, String sftpUsername, String sftpPassword,
			String sftpBackupPath, String backupStayDays, String backupType) throws Exception {
		SFTPUtil sftp = new SFTPUtil(sftpUsername, sftpPassword, sftpIp, sftpPort);
		//1.传输中途中断，2.未连接上
		if(TYPE_ALL.equals(backupType)) {//全局备份
			int times = 0;//次数
			long start = System.currentTimeMillis();
			long end = System.currentTimeMillis();
			long limit = 1000 * 60 * 5;
			while(true) {
				try {
					sftp.login(SFTPUtil.SFTP);
					sftp.upload(sftpBackupPath, filePath);
					break;
				}catch (Exception e) {
					// TODO: handle exception
					times++;
					end = System.currentTimeMillis();
					if(times>5||(end-start)>limit) {//尝试5分钟，或尝试次数大于五
						log.error("尝试时间大于五分钟，或尝试次数大于五，无法传输成功",times);
						throw new RuntimeException(e);
					}else {
						log.error("全局备份传输失败，尝试第{}次。。。",times);
					}
				}finally {
					sftp.logout();
				}
				
			}
		}else {
			sftp.login(SFTPUtil.SFTP);
			sftp.upload(sftpBackupPath, filePath);
			sftp.logout();
		}

		// 保留n天删除之前的zip
		String clearBackup = clearBackup(backupStayDays, backupType);
		log.info("sftp:" + clearBackup);
		String clearBackupResult = SFTPUtil.execCmd(sftpIp, sftpUsername,sftpPort, sftpPassword, clearBackup);
		log.info(clearBackupResult);
	}

	/**
	 * 局部备份
	 * 
	 * @param sysLogService
	 */
	public static void backupPart(SysLogService sysLogService, SysparaService sysparaService) {
		try {
//			log.info("进入局部备份 backupPart...");
			boolean backup_part_button = sysparaService.find("backup_part_button").getBoolean();
			if (!backup_part_button) {
				return;
			}
			/**
			 * 保留n天的备份，之前清除
			 */
			String backup_stay_days = sysparaService.find("backup_stay_days").getValue();
			String backup_part_notable = sysparaService.find("backup_part_notable").getValue();
			String backupType = TYPE_PART;
//			log.info("开始局部备份...");

			DateFormat sdf = DateUtils.createDateFormat(Constants.DB_BACKUP_TIME_FORMAT);
			String backupName = PropertiesUtilDB.getProperty("db.database.name") + "_part_" + sdf.format(new Date());

			executeLinuxCmd("mkdir " + PropertiesUtilDB.getProperty("db.backup.path"));
			// 开始备份sql
			String backupDB = backupPartDB(backupName, backup_part_notable);
			String backResult = executeLinuxCmd(backupDB);
//			log.info(backResult);
			// 开始压缩zip
			String zip = zip(backupName);
//			log.info(zip);
			String zipResult = executeLinuxCmd(zip);
//			log.info(zipResult);
			// 保留n天删除之前的zip
			String clearBackup = clearBackup(backup_stay_days, backupType);
//			log.info(clearBackup);
			String clearBackupResult = executeLinuxCmd(clearBackup);
//			log.info(clearBackupResult);
			ThreadUtils.sleep(2000);// 等待备份完成

			handleSftp(sysparaService, PropertiesUtilDB.getProperty("db.backup.path") + "/" + backupName + ".zip",
					backupType, backup_stay_days);

			log.info("备份完成...");
		} catch (Exception e) {
			// TODO: handle exception
			log.error(DateUtils.format(new Date(), DateUtils.DF_yyyyMMddHHmmss) + " backup fail e:", e);
			SysLog entity = new SysLog();
			entity.setLevel(SysLog.level_error);
			entity.setCreateTime(new Date());
			entity.setLog("数据库备份失败  e:" + e);
			sysLogService.saveAsyn(entity);
		}
	}

	public static String backupDB(String backupName) {
		StringBuffer cmdBuf = new StringBuffer();
		cmdBuf.append(DBTools.formatDBClientCmd("mysqldump", "exe")); // 客户端命令

		cmdBuf.append(" -h ").append(PropertiesUtilDB.getProperty("db.ip"));
		cmdBuf.append(" -P ").append(PropertiesUtilDB.getProperty("db.port"));
		cmdBuf.append(" -u").append(PropertiesUtilDB.getProperty("db.username"));
		cmdBuf.append(" -p")
				.append("'" + endecrypt.get3DESDecrypt(PropertiesUtilDB.getProperty("db.password"), KEY) + "'");
		cmdBuf.append(" ").append(PropertiesUtilDB.getProperty("db.database.name"));
		cmdBuf.append(" > ")//
//        .append(DBTools.formatRuntimeCmdPath(realPath)) // 处理空格
				.append(PropertiesUtilDB.getProperty("db.backup.path")).append("/").append(backupName).append(".sql");
		return cmdBuf.toString();
	}

	public static String backupPartDB(String backupName, String noTable) {

		StringBuffer cmdBuf = new StringBuffer();
		cmdBuf.append(DBTools.formatDBClientCmd("mysqldump", "exe")); // 客户端命令

		cmdBuf.append(" -h ").append(PropertiesUtilDB.getProperty("db.ip"));
		cmdBuf.append(" -P ").append(PropertiesUtilDB.getProperty("db.port"));
		cmdBuf.append(" -u").append(PropertiesUtilDB.getProperty("db.username"));
		cmdBuf.append(" -p")
				.append("'" + endecrypt.get3DESDecrypt(PropertiesUtilDB.getProperty("db.password"), KEY) + "'");
		cmdBuf.append(" --skip-opt");// 运行中的数据库谨慎备份，忽略部分表结构参数
		cmdBuf.append(" ").append(PropertiesUtilDB.getProperty("db.database.name"));

//		String notable = PropertiesUtilDB.getProperty("backup.part.notable");
		if (StringUtils.isNotEmpty(noTable)) {
			String baseCmd = "--ignore-table=" + PropertiesUtilDB.getProperty("db.database.name") + ".";
			for (String t : noTable.split(",")) {
				cmdBuf.append(" ").append(baseCmd + t);
			}
		}

		cmdBuf.append(" > ")//
//        .append(DBTools.formatRuntimeCmdPath(realPath)) // 处理空格
				.append(PropertiesUtilDB.getProperty("db.backup.path")).append("/").append(backupName).append(".sql");
		return cmdBuf.toString();
	}

	public static String zip(String backupName) {
//		String zip="zip -m /home/demo7.zip /home/demo7.sql";
		StringBuffer cmdBuf = new StringBuffer();
		cmdBuf.append("zip"); // 客户端命令

		cmdBuf.append(" -m ").append(PropertiesUtilDB.getProperty("db.backup.path")).append("/").append(backupName)
				.append(".zip");
		cmdBuf.append(" ").append(PropertiesUtilDB.getProperty("db.backup.path")).append("/").append(backupName)
				.append(".sql");
		return cmdBuf.toString();
	}

	/**
	 * 清除超时备份
	 * 
	 * @param days 备份保留时间
	 * @return
	 */
	public static String clearBackup(String days, String backupType) {
//		find /backup/test -mtime +2 -type f -name *.txt -exec rm {} \;
//		StringBuffer cmdBuf = new StringBuffer("find /backup -mmin +120 -type f -name *.zip -exec rm {} \\;");
		StringBuffer cmdBuf = new StringBuffer();
		cmdBuf.append(" find ").append(PropertiesUtilDB.getProperty("db.backup.path"));
		cmdBuf.append(" -mtime ").append("+" + days);
		String dbName = PropertiesUtilDB.getProperty("db.database.name") + "_part_";
		if (backupType.equals(TYPE_ALL)) {// 优先处理part，全局的匹配了再处理
			dbName = PropertiesUtilDB.getProperty("db.database.name") + "_";
		}
		cmdBuf.append(" -type f -name '" + dbName + "*.zip' -exec rm {} \\; ");

		return cmdBuf.toString();
	}

//	public static void sftpPart(String filePath) throws Exception {
//		String sftpBackupPath = PropertiesUtilDB.getProperty("sftp.backup.path");
//		String sftpUsername = PropertiesUtilDB.getProperty("sftp.username");
//		String sftpPassword = endecrypt.get3DESDecrypt(PropertiesUtilDB.getProperty("sftp.password"), KEY);
//		String sftpIp = PropertiesUtilDB.getProperty("sftp.ip");
//		int sftpPort = Integer.valueOf(PropertiesUtilDB.getProperty("sftp.port"));
//		SFTPUtil sftp = new SFTPUtil(sftpUsername, sftpPassword, sftpIp, sftpPort);
//		sftp.login(SFTPUtil.SFTP);
//		sftp.upload(sftpBackupPath, filePath);
//		sftp.logout();
//
//		// 保留n天删除之前的zip
//		String clearBackup = clearBackup(2,);
//		log.info("sftp:" + clearBackup);
//		String clearBackupResult = SFTPUtil.execCmd(sftpIp, sftpUsername, sftpPassword, clearBackup);
//		log.info(clearBackupResult);
//	}
}
