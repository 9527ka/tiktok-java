package db;

import java.io.File;
import java.util.Map;

public interface DBOperatorEvent {

    /**
     * 开始备份前
     * @param params 参数
     */
    public void beforeBackup(Map<String, Object> params);

    /**
     * 备份后事件
     * @param record 备份记录
     * @param params 参数
     */
    public void afterBackup(DBBackupRecord record, Map<String, Object> params);

    /**
     * 数据库还原操作前
     * @param file   备份文件
     * @param params 参数
     */
    public void beforeRestore(File file, Map<String, Object> params);

    /**
     * 数据库还原操作后
     * @param file   备份文件
     * @param params 参数
     */
    public void afterRestore(File file, Map<String, Object> params);

}
