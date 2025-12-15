package db;

import db.backup.internal.MysqlBackupImpl;
import db.restore.MysqlRestoreImpl;
import db.util.ConfigUtils;

public class DBBeanFactory {

    /**	
     * <p>Description: 创建DBBackup实例   </p>
     * 
     * @param backupRecordService   备份记录信息管理接口
     * @param operatorEvent         数据库操作事件    
     * @return  DBBackup实例
     */
    public static Object buildBackupBean(DBBackupRecordService backupRecordService, //
            DBOperatorEvent operatorEvent) {
        SupportDBTypeEnum dbType = ConfigUtils.getCurrentDBType(); // 当前数据库类型
        DBBackup backupService = null;
        switch (dbType) {
        case mysql:
            backupService = new MysqlBackupImpl();
            break;
//        case oracle:
//            backupService = new OracleBackupImpl();
//            break;
//        case postgre:
//            backupService = new PostgreBackupImpl();
//            break;
//        case sqlserver:
//            backupService = new SQLServerBackupImpl();
//            break;
        }

        if (backupService == null) {
            throw new IllegalArgumentException("The DB backupService can not be NULL");
        }

        if (backupService instanceof DBBackupBeanHandler) {
            ((DBBackupBeanHandler) backupService).setBackupRecordService(backupRecordService);
            ((DBBackupBeanHandler) backupService).setOperatorEvent(operatorEvent);
        }
        return backupService;
    }

    /**    
     * <p>Description: 创建DBRestore实例   </p>
     * 
     * @param operatorEvent         数据库操作事件    
     * @return  DBRestore实例
     */
    public static Object buildRestoreBean(DBOperatorEvent operatorEvent) {
        SupportDBTypeEnum dbType = ConfigUtils.getCurrentDBType(); // 当前数据库类型
        DBRestore restoreService = null;
        switch (dbType) {
        case mysql:
            restoreService = new MysqlRestoreImpl();
            break;
//        case oracle:
//            restoreService = new OracleRestoreImpl();
//            break;
//        case postgre:
//            restoreService = new PostgreRestoreImpl();
//            break;
//        case sqlserver:
//            restoreService = new SQLServerRestoreImpl();
//            break;
        }

        if (restoreService == null) {
            throw new IllegalArgumentException("The DB restoreService can not be NULL");
        }

        if (restoreService instanceof DBRestoreBeanHandler) {
            ((DBRestoreBeanHandler) restoreService).setOperatorEvent(operatorEvent);
        }
        return restoreService;
    }

}
