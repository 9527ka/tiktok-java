package db.util;

import java.io.File;

import db.SupportDBTypeEnum;


public class ConfigUtils {

    /**	
     * 当前数据库类型
     */
    private static SupportDBTypeEnum CURRENT_DB_TYPE = null;

    /**  
     * 当前数据库类型(默认mysql)
     * 
     * @return The current database type
     */
    public static SupportDBTypeEnum getCurrentDBType() {
        if (CURRENT_DB_TYPE == null) {
            String dbName = System.getProperty("components.ha.database.DBType", //
                    SupportDBTypeEnum.mysql.name()); // 默认mysql
            CURRENT_DB_TYPE = SupportDBTypeEnum.getEnum(dbName);
        }
        return CURRENT_DB_TYPE;
    }

    /**	
     * 数据库备份信息XML路径(文件数据库)
     * @return 文件数据库路径
     */
    public static String getBackupRecordXMLPath() {
        String databasePath = PathUtil.getDatabasePath();
        String backupRecordXML = System.getProperty("components.ha.database.backupRecordXML", "");
        if ("".equals(backupRecordXML)) {
            backupRecordXML = databasePath + File.separator + "backupRecords.xml";
        }

        return backupRecordXML;
    }

    /**	
     * 数据库客户端程序安装bin目录（支持空格）
     * 
     * @return The installation directory
     */
    public static String getDBInstalledPath() {
        return System.getProperty("components.ha.database.installedPath", null);
    }

}
