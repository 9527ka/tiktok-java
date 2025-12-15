package db;

public interface DBBackupBeanHandler {

    /**
     * @param backupRecordService The backupRecordService to set.
     */
    public void setBackupRecordService(DBBackupRecordService backupRecordService);

    /**
     * @param operatorEvent The operatorEvent to set.
     */
    public void setOperatorEvent(DBOperatorEvent operatorEvent);

}
