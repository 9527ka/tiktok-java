package db.util;

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import db.PropertiesUtilDB;

public class PathUtil {

    /**	
     * logger
     */
	private static Logger logger = LogManager.getLogger(PathUtil.class); 

    /**	
     * database组件操作路径
     */
    private static String DATABASE_PATH;

    /**	
     * 数据库备份文件存储路径
     */
    private static String DB_BACKUP_PATH;

    /** 
     * 数据库备份的工具路径
     */
    private static String DB_TOOLS_PATH;

    /** 
     * database组件操作目录下tools路径
     * 
     * @return Returns the databasePath.
     */
    public static String getDatabasePath() {
        if (DATABASE_PATH == null) {
//            DATABASE_PATH = getAppRootPath() + File.separator + "database";
//            DATABASE_PATH = File.separator + "database";
//            DATABASE_PATH = "C:\\e";
            DATABASE_PATH = PropertiesUtilDB.getProperty("db.backup.path");

            File f = new File(DATABASE_PATH);
            if (!f.exists()) {
                f.mkdirs();
            }

            if (logger.isDebugEnabled()) {
                logger.debug("DatabasePath = " + DATABASE_PATH);
            }
        }
        return DATABASE_PATH;
    }

    /** 
     * 数据库备份文件存储路径
     * 
     * @return Returns the dbBackupPath.
     */
    public static String getDbBackupPath() {
        if (DB_BACKUP_PATH == null) {
            DB_BACKUP_PATH = getDatabasePath() + File.separator + "backup";
            File f = new File(DB_BACKUP_PATH);
            if (!f.exists()) {
                f.mkdirs();
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Database backupPath = " + DB_BACKUP_PATH);
            }
        }
        return DB_BACKUP_PATH;
    }

    /**    
     * database组件操作目录下tools路径
     * 
     * @return dbToolsPath
     */
    public static String getDbToolsPath() {
        if (DB_TOOLS_PATH == null) {
            DB_TOOLS_PATH = getDatabasePath() + File.separator + "tools";
            File f = new File(DB_TOOLS_PATH);
            if (!f.exists()) {
                f.mkdir();
            }

            if (logger.isDebugEnabled()) {
                logger.debug("Database toolsPath = " + DB_TOOLS_PATH);
            }
        }
        return DB_TOOLS_PATH;
    }

}
