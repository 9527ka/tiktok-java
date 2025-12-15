package db.restore;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.springframework.util.Assert;

import db.Constants;
import db.DBRestore;
import db.OpertProcessListener;
import db.PropertiesUtilDB;
import db.util.CMDUtil;
import db.util.FileUtil;
import db.util.ZipUtils;
import db.util.jdbc.DBTools;
import db.util.jdbc.MysqlTools;

public class MysqlRestoreImpl extends AbstractRestoreImpl {

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
        return params;
    }

    @Override
    protected boolean doResotre(File file, Map<String, Object> params, OpertProcessListener processListener) {
        Assert.notNull(params);
        onOutputAndLog(processListener, "Starting restore...", Level.INFO);

        String filePath = file.getAbsolutePath();
        String restorePath = formatFilePath(filePath);
        FileUtil.deleteDir(restorePath, true);

        // 解压备份文件
        ZipUtils.unZip(filePath, file.getParent() + File.separator);

        // 删除旧数据
//        clearOldData(params, processListener);

        // 还原数据库结构
//        restoreDBStruct(params, restorePath, processListener);

        // 还原数据库表数据
        restoreDBData(params, restorePath, processListener);

        return true;
    }

    @Override
    protected void clearAfterRestore(File file, Map<String, Object> params) {
        // 数据库还原后，删除临时目录
        FileUtil.deleteDir(formatFilePath(file.getAbsolutePath()), true);
    }

    /**	
     * 格式化待还原文件路径
     * @param filePath 待还原文件路径
     * @return 路径
     */
    private String formatFilePath(String filePath) {
        String restorePath = filePath.substring(0, filePath.indexOf(getBackupFileSuffix()));
        restorePath = restorePath.replaceAll(Constants.FILE_SEPARATOR_REGEX, "/");
        return restorePath;
    }

    /** 
     * 清除旧数据（防止数据冲突）
     * @param params   参数
     * @param processListener 监听器
     */
    private void clearOldData(Map<String, Object> params, OpertProcessListener processListener) {
        onOutputAndLog(processListener, "Clear old database data...", Level.INFO);

        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append(DBTools.formatDBClientCmd("mysql", "exe"));
        cmdBuf.append(" -h ").append((String) params.get("ip"));
        cmdBuf.append(" -P ").append((String) params.get("port")); // 端口
        cmdBuf.append(" --user=").append((String) params.get("username")); // 用户名
        cmdBuf.append(" --password=").append((String) params.get("password")); // 密码
        // cmdBuf.append(" --verbose -v "); // 详情信息模式

        String dbName = (String) params.get("databaseName");
        cmdBuf.append(" ").append("-e \"DROP DATABASE IF EXISTS ").append(dbName).append(";");
        cmdBuf.append("CREATE DATABASE ").append(dbName).append(" DEFAULT CHARACTER SET utf8; \" ");
        executeRuntimeCmd(cmdBuf.toString(), processListener);

    }

    /** 
     * 还原数据库结构 
     * @param params        参数 
     * @param restorePath     备份文件目录
     * @param processListener 监听器
     */
    private void restoreDBStruct(Map<String, Object> params, String restorePath, OpertProcessListener processListener) {
        onOutputAndLog(processListener, "Restore database structs...", Level.INFO);

        StringBuffer cmdBuf = new StringBuffer();
        cmdBuf.append(DBTools.formatDBClientCmd("mysql", "exe"));
        cmdBuf.append(" -h ").append((String) params.get("ip"));
        cmdBuf.append(" -P ").append((String) params.get("port")); // 端口
        cmdBuf.append(" --user=").append((String) params.get("username")); // 用户名
        cmdBuf.append(" --password=").append((String) params.get("password")); // 密码
        // cmdBuf.append(" --verbose -v "); // 详情信息模式

        String databaseName = (String) params.get("databaseName");
        cmdBuf.append(" ").append(databaseName);
        cmdBuf.append(" ").append("-e \"SET FOREIGN_KEY_CHECKS=0;");
        cmdBuf.append("SOURCE ").append(restorePath).append("/db_struct.sql ; \" ");

        executeRuntimeCmd(cmdBuf.toString(), processListener); // 执行CMD

        onOutputAndLog(processListener, "restore Structs finished", Level.DEBUG);
    }

    /** 
     * 还原数据库数据 
     * @param params        参数 
     * @param restorePath     备份文件目录
     * @param processListener 监听器
     */
    private void restoreDBData(Map<String, Object> params, String restorePath, OpertProcessListener processListener) {
        onOutputAndLog(processListener, "Importing Data...", Level.INFO);

        String ip = (String) params.get("ip");
        String port = (String) params.get("port");
        String databaseName = (String) params.get("databaseName");
        String username = (String) params.get("username");
        String password = (String) params.get("password");

        StringBuffer preCmd = new StringBuffer();
        preCmd.append(DBTools.formatDBClientCmd("mysql", "exe"));
        preCmd.append(" -h ").append(ip);
        preCmd.append(" -P ").append(port); // 端口
        preCmd.append(" --user=").append(username); // 用户名
        preCmd.append(" --password=").append(password); // 密码
        // preCmd.append(" --verbose -v "); // 详情信息模式
        preCmd.append(" ").append(databaseName);
        preCmd.append(" ").append("-e ");

        // 获取数据库表
        List<String> tables = MysqlTools.findAllDBTables(ip, port, databaseName, username, password);
        for (int index = 0; index < tables.size(); index++) {
            String tableName = tables.get(index);
            onOutputAndLog(processListener, "Table Restored: " + tableName, Level.INFO);

            String tableDataFilePath = restorePath + "/" + tableName + ".txt";
            StringBuilder cmdBuf = new StringBuilder();
            cmdBuf.append(preCmd);
            cmdBuf.append(" \"LOCK TABLES " + tableName + " WRITE;"); // 锁表
            cmdBuf.append("SET FOREIGN_KEY_CHECKS=0;"); // 不进行外键检查

            // 导入表数据
            cmdBuf.append("LOAD DATA INFILE '");
            cmdBuf.append(tableDataFilePath).append("'");
            cmdBuf.append("INTO TABLE ").append(tableName);
            cmdBuf.append(" FIELDS TERMINATED BY ',' ENCLOSED BY '\\\"';");

            cmdBuf.append(" UNLOCK TABLES;").append(" \" "); // 解锁表
            // cmdBuf.append("\n");
            executeRuntimeCmd(cmdBuf.toString(), processListener); // 执行CMD
        }

        onOutputAndLog(processListener, "Importing Data finished", Level.DEBUG);
    }

    /**	
     * 执行CMD
     * @param cmd   cmd命令
     * @param processListener 监听器
     */
    private void executeRuntimeCmd(String cmd, OpertProcessListener processListener) {
        try {
//            if (processListener == null || processListener.getOutputListener() == null) {
//                RuntimeExecutorResult result = RuntimeExecutor.execute(cmd, "GB2312");
//                if (logger.isDebugEnabled()) {
//                    logger.debug(result.getOutput());
//                    logger.debug(result.getError());
//                }
//            }
//            else {
//                MessageOutputListener outputListener = new MessageOutputListener();
//                RuntimeExecutor.execute(cmd, "GB2312", outputListener);
//                while (!outputListener.isFinish()) {
//                    onOutputAndLog(processListener, outputListener.getOutputString(), null);
//                }
//                onOutputAndLog(processListener, outputListener.getOutputString(), null);
//            }
        	logger.info(cmd);
            String cmdResult = CMDUtil.runCMD(cmd);
            logger.info("result:"+cmdResult);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void main(String[] args) throws Exception {   
    	DBRestore dbBack = new MysqlRestoreImpl();
    	String filePath = "C:\\e\\backup\\blockchain_nes_20201030161757-734.zip";
    	dbBack.restore(filePath, null, null);
	}

}