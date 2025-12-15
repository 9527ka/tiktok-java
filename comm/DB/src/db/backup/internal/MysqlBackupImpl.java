package db.backup.internal;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.util.Assert;

import com.jcraft.jsch.SftpException;

import db.Constants;
import db.DBBackup;
import db.OpertProcessListener;
import db.PropertiesUtilDB;
import db.util.CMDUtil;
import db.util.FileUtil;
import db.util.SFTPUtil;
import db.util.ZipUtils;
import db.util.jdbc.DBTools;
import db.util.jdbc.MysqlTools;

public class MysqlBackupImpl extends AbstractBackupImpl {
	private final Logger logger = LoggerFactory.getLogger(this.getClass());
    @Override
    protected String getBackupFileSuffix() {
        return ".zip";
    }

    @Override
    protected Map<String, Object> parserMapParams(Map<String, Object> params) {
        Assert.notNull(params);
        // 读取参数
        if (params.get("ip") == null) {
//            String ip = System.getProperty(DBPorperties.ENV_IP, MySQLConfig.DEFAULT_IP);
            String ip = PropertiesUtilDB.getProperty("db.ip");
            params.put("ip", ip);
        }

        if (params.get("port") == null) {
//            String port = System.getProperty(DBPorperties.ENV_PORT, MySQLConfig.DEFAULT_PORT);
            String port = PropertiesUtilDB.getProperty("db.port");
            params.put("port", port);
        }

        if (params.get("databaseName") == null) {
//            String databaseName = System.getProperty(DBPorperties.ENV_DBNAME, MySQLConfig.DEFAULT_DBNAME);
            String databaseName = PropertiesUtilDB.getProperty("db.databaseName");
            params.put("databaseName", databaseName);
        }

        if (params.get("username") == null) {
//            String username = System.getProperty(DBPorperties.RUIJIE_JDBC_USERNAME);
            String username = PropertiesUtilDB.getProperty("db.username");
            params.put("username", username);
        }

        if (params.get("password") == null) {
//            String password = System.getProperty(DBPorperties.RUIJIE_JDBC_PASSWORD);
            String password = PropertiesUtilDB.getProperty("db.password");
            params.put("password", password);
        }

        // 备份存储路径
        if (params.get("backupPath") == null) {
            String backupPath = DEFAULT_BACKUP_PATH;
            params.put("backupPath", backupPath);
        }

        return params;
    }

    @Override
    protected boolean doEnvironmentCheck(Map<String, Object> params, OpertProcessListener processListener) {
        Assert.notNull(params);
        // TODO:doEnvironmentCheck
        return true;
    }

    @Override
    protected boolean doBackup(Map<String, Object> params, OpertProcessListener processListener)
            throws InterruptedException {
        Assert.notNull(params);
        onOutputAndLog(processListener, "Starting backup...", Level.INFO);

        String backupPath = (String) params.get("backupPath"); // 备份路径
        String backupName = (String) params.get("backupName");
        String realBackupPath = backupPath + "/" + backupName; // 实际备份路径
        FileUtil.deleteDir(realBackupPath, false); // 删除旧备份
        File backUpdir = new File(realBackupPath);
        if (!backUpdir.exists()) {
            backUpdir.mkdirs();
        }

        // 备份数据库结构
//        backupDBStruct(params, realBackupPath, processListener);

        // 备份数据库表数据
//        backupDBData(params, realBackupPath, processListener);
        
		backupDBDataByShell(params, realBackupPath, processListener);
		
        // 压缩备份文件
        String zipName = backupName + getBackupFileSuffix(); // 备份文件名
        String filePath = backupPath + "/" + zipName;
        ZipUtils.createZip(filePath, realBackupPath);
//        String zip="zip -m "+filePath+" "+realBackupPath;
        sftp(filePath);
        // onOutputAndLog(processListener, "Finished - Successfully", Level.INFO);
        return true;
    }

    @Override
    protected void clearAfterBackup(Map<String, Object> params) {
        String backupPath = (String) params.get("backupPath"); // 备份路径
        String backupName = (String) params.get("backupName"); // 备份名称
        String realBackupPath = backupPath + "/" + backupName;

        // 生成压缩文件后，删除临时目录
        FileUtil.deleteDir(realBackupPath, true);
    }

    /**	
     * 导出数据库结构 
     * @param params        参数 
     * @param realPath      实际备份路径
     * @param processListener 监听器
     * @throws InterruptedException 中断异常
     */
    private void backupDBStruct(Map<String, Object> params, String realPath, OpertProcessListener processListener)
            throws InterruptedException {
        onOutputAndLog(processListener, "backup database struct...", Level.INFO);

        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append(DBTools.formatDBClientCmd("mysqldump", "exe")); // 客户端命令

        cmdBuf.append(" -h ").append((String) params.get("ip"));
        cmdBuf.append(" -P ").append((String) params.get("port"));
        cmdBuf.append(" --user=").append((String) params.get("username"));
        cmdBuf.append(" --password=").append((String) params.get("password"));
        cmdBuf.append(" --opt "); // 同--quick --add-drop-table --add-locks --extended-insert --lock-tables。
        cmdBuf.append(" --no-data "); // 不备份表数据
        // cmdBuf.append(" --add-drop-database "); //

        cmdBuf.append(" --result-file=")//
                .append(DBTools.formatRuntimeCmdPath(realPath)) // 处理空格
                .append("/db_struct.sql");

        String databaseName = (String) params.get("databaseName");
        cmdBuf.append(" ").append(databaseName);

        try {
//            if (processListener == null || processListener.getOutputListener() == null) {
//                RuntimeExecutorResult result = RuntimeExecutor.execute(cmdBuf.toString(), "GB2312");
//                if (logger.isDebugEnabled()) {
//                    logger.debug(result.getOutput());
//                    logger.debug(result.getError());
//                }
//            }
//            else {
//                MessageOutputListener outputListener = new MessageOutputListener();
//                RuntimeExecutor.execute(cmdBuf.toString(), "GB2312", outputListener);
//                while (!outputListener.isFinish()) {
//                    onOutputAndLog(processListener, outputListener.getOutputString(), null);
//                }
//                onOutputAndLog(processListener, outputListener.getOutputString(), null);
//            }
            logger.info(cmdBuf.toString());
            String cmdResult = CMDUtil.runCMD(cmdBuf.toString());
            logger.info("result:"+cmdResult);
        } catch (Exception e) {
            logger.error("backup DataBase Struct error");
//            throw new RuntimeException(e);
        }
    }
    /**	
     * 导出数据库结构 
     * @param params        参数 
     * @param realPath      实际备份路径
     * @param processListener 监听器
     * @throws InterruptedException 中断异常
     */
    private String backupDBDataByShell(Map<String, Object> params, String realPath, OpertProcessListener processListener)
            throws InterruptedException {
        onOutputAndLog(processListener, "backup database struct...", Level.INFO);

        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append(DBTools.formatDBClientCmd("mysqldump", "exe")); // 客户端命令

        cmdBuf.append(" -h ").append((String) params.get("ip"));
        cmdBuf.append(" -P ").append((String) params.get("port"));
        cmdBuf.append(" -u").append((String) params.get("username"));
        cmdBuf.append(" -p").append("'"+(String) params.get("password")+"'");
        cmdBuf.append(" ").append((String) params.get("databaseName"));
//        cmdBuf.append(" --opt "); // 同--quick --add-drop-table --add-locks --extended-insert --lock-tables。
//        cmdBuf.append(" --no-data "); // 不备份表数据
        // cmdBuf.append(" --add-drop-database "); //

        cmdBuf.append(" > ")//
                .append(DBTools.formatRuntimeCmdPath(realPath)) // 处理空格
                .append("/home") // 处理空格
                .append("//")
                .append((String) params.get("databaseName"))
                .append(".sql");

//        String databaseName = (String) params.get("databaseName");
//        cmdBuf.append(" ").append(databaseName);

        try {
//            if (processListener == null || processListener.getOutputListener() == null) {
//                RuntimeExecutorResult result = RuntimeExecutor.execute(cmdBuf.toString(), "GB2312");
//                if (logger.isDebugEnabled()) {
//                    logger.debug(result.getOutput());
//                    logger.debug(result.getError());
//                }
//            }
//            else {
//                MessageOutputListener outputListener = new MessageOutputListener();
//                RuntimeExecutor.execute(cmdBuf.toString(), "GB2312", outputListener);
//                while (!outputListener.isFinish()) {
//                    onOutputAndLog(processListener, outputListener.getOutputString(), null);
//                }
//                onOutputAndLog(processListener, outputListener.getOutputString(), null);
//            }
        	 logger.info(cmdBuf.toString());
             String cmdResult = CMDUtil.runCMD(cmdBuf.toString());
             logger.info("result:"+cmdResult);
        } catch (Exception e) {
            logger.error("backup DataBase error");
            throw new RuntimeException(e);
        }
        return cmdBuf.toString();
    }
    /**    
     * 导出数据
     * @param params        参数 
     * @param realPath      实际备份路径
     * @param processListener 监听器
     * @throws InterruptedException 中断异常
     */
    private void backupDBData(Map<String, Object> params, String realPath, OpertProcessListener processListener)
            throws InterruptedException {
        onOutputAndLog(processListener, "Prepare writing data...", Level.INFO);

        String ip = (String) params.get("ip");
        String port = (String) params.get("port");
        String databaseName = (String) params.get("databaseName");
        String username = (String) params.get("username");
        String password = (String) params.get("password");

        // 获取所有数据库表
        List<String> tables = MysqlTools.findAllDBTables(ip, port, databaseName, username, password);

        onOutputAndLog(processListener, "Writing data...", Level.INFO);
        String rootPath = realPath.replaceAll(Constants.FILE_SEPARATOR_REGEX, "/");

        BasicDataSource ds = MysqlTools.createDataSource(ip, port, databaseName, username, password);
        JdbcTemplate jt = new JdbcTemplate(ds);
        for (int index = 0; index < tables.size(); index++) {
            String tableName = tables.get(index);
            String fileName = rootPath + "/" + tableName + ".txt";

            onOutputAndLog(processListener, "Writing table " + tableName + " data...", Level.INFO);
            StringBuffer sqlBuf = new StringBuffer();
            sqlBuf.append("select * from ").append(tableName);
            sqlBuf.append(" INTO OUTFILE \"").append(fileName).append("\" ");
            sqlBuf.append(" FIELDS TERMINATED BY ',' ENCLOSED BY '\\\"'");
            jt.execute(sqlBuf.toString());
            
            
        }
    }

    public void sftp(String filePath) {
    	String sftpBackupPath= PropertiesUtilDB.getProperty("sftp.backup.path");
    	String sftpUsername= PropertiesUtilDB.getProperty("sftp.username");
    	String sftpPassword= PropertiesUtilDB.getProperty("sftp.password");
    	String sftpIp= PropertiesUtilDB.getProperty("sftp.ip");
    	int sftpPort= Integer.valueOf(PropertiesUtilDB.getProperty("sftp.port"));
		SFTPUtil sftp = new SFTPUtil(sftpUsername, sftpPassword, sftpIp, sftpPort);   
		sftp.login(SFTPUtil.SFTP);   
		try {
			sftp.upload(sftpBackupPath, filePath);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SftpException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}   
		sftp.logout();   
    }
    public static void main(String[] args) throws Exception {   
    	DBBackup dbBack = new MysqlBackupImpl();
    	dbBack.backup(null, null);
	}   
}
