package db.backup.internal;

import java.io.FileNotFoundException;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.Constants;
import db.DBBackup;
import db.DBBackupBeanHandler;
import db.DBBackupRecord;
import db.DBBackupRecordService;
import db.DBOperatorEvent;
import db.OpertProcessListener;
import db.support.DBBackupRecordServiceImpl;
import db.util.FileUtil;
import db.util.PathUtil;
import db.util.UUIDGenerator;
import kernel.util.DateUtils;
import kernel.util.StringUtils;


public abstract class AbstractBackupImpl implements DBBackup, DBBackupBeanHandler {

    /** 
     * logger
     */
	private final Logger logger = LoggerFactory.getLogger(this.getClass());

    /** 
     * 默认备份路径
     */
    protected final static String DEFAULT_BACKUP_PATH = PathUtil.getDbBackupPath();

    /**	
     * 事件通知
     */
    protected DBOperatorEvent operatorEvent;

    /**	
     * 数据库备份记录信息管理接口
     */
    protected DBBackupRecordService backupRecordService = new DBBackupRecordServiceImpl();

    @Override
    public boolean backup(Map<String, Object> params, OpertProcessListener processListener) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params = parserMapParams(params); // 解析参数

//        operatorEvent.beforeBackup(params); // 备份前事件通知

        boolean result = false;
        DBBackupRecord record = null;
        backupRecordService = new DBBackupRecordServiceImpl();
        try {
            if (doEnvironmentCheck(params, processListener)) {
                Date backupTime = new Date(); // 备份时间
                params.put("backupTime", backupTime);

                String backupName = (String) params.get("backupName"); // 备份名称
                if (backupName == null) {
                    String databaseName = (String) params.get("databaseName");
                    DateFormat sdf = DateUtils.createDateFormat(Constants.DB_BACKUP_TIME_FORMAT);
                    backupName = databaseName + "_" + sdf.format(backupTime);
                    params.put("backupName", backupName);
                }

                result = doBackup(params, processListener); // 执行备份操作

                record = createNewBackupRecord(params); // 新建备份记录
                backupRecordService.add(record); // 保存备份文件信息
            }
        } catch (Throwable e) {
            result = false;
            logger.error("backup database error", e);
//            throw new FastFailException(e);
            throw new RuntimeException(e);
        } finally {
            String msg = "Finished - " + (result ? "Successfully" : "Failed");
            try {
                onOutputAndLog(processListener, msg, Level.INFO);
            } catch (InterruptedException e) {
                // ignore
            }

            if (processListener != null) {
                processListener.onExit();
            }

            clearAfterBackup(params); // 备份后清理
        }

        params.put("result", result); // 结果
//        operatorEvent.afterBackup(record, params); // 备份后事件通知
        return result;
    }

    /**	
     * <p>Description: 备份清理工作（清除临时目录、不完整备份文件等操作） </p>
     * <p>Create Time: 2013-2-20   </p>
     * @author weiminghua
     * @param params 参数
     */
    protected void clearAfterBackup(Map<String, Object> params) {
        // default do nothing
    }

    /**    
     * 备份文件后缀
     * @return 备份文件后缀名
     */
    protected abstract String getBackupFileSuffix();

    /**	
     * 解析参数
     * 
     * @param params 参数
     * @return 参数
     */
    protected abstract Map<String, Object> parserMapParams(Map<String, Object> params);

    /**   
     * 运行环境检测
     * @param params    参数
     * @param processListener  监听器
     * @return 结果
     */
    protected abstract boolean doEnvironmentCheck(Map<String, Object> params, OpertProcessListener processListener);

    /**    
     * 执行备份
     * @param params    参数
     * @param processListener  监听器
     * @return 结果
     * @throws InterruptedException 中断异常
     */
    protected abstract boolean doBackup(Map<String, Object> params, OpertProcessListener processListener)
            throws InterruptedException;

    /**
     * @param operatorEvent The operatorEvent to set.
     */
    public void setOperatorEvent(DBOperatorEvent operatorEvent) {
        this.operatorEvent = operatorEvent;
    }

    /**
     * @param backupRecordService The backupRecordService to set.
     */
    public void setBackupRecordService(DBBackupRecordService backupRecordService) {
        this.backupRecordService = backupRecordService;
    }

    /**	
     * 生成备份记录
     * @param params 参数
     * @return 备份记录
     */
    protected DBBackupRecord createNewBackupRecord(Map<String, Object> params) {
        DBBackupRecord record = new DBBackupRecord();
        record.setUuid(UUIDGenerator.getUUID());
        String backupName = (String) params.get("backupName");
        record.setName(backupName);
        record.setBackupTime((Date) params.get("backupTime")); // 备份时间
        String description = (String) params.get("description"); // 备份描述
        record.setDescription(description);

        String backupPath = (String) params.get("backupPath"); // 备份存储路径
        String filePath = backupPath + "/" + backupName + getBackupFileSuffix(); // 备份文件路径
        record.setFilePath(filePath);
        try {
            record.setFileSize(FileUtil.getFileSize(filePath));
        } catch (FileNotFoundException e) {
            logger.error("get backup file[" + filePath + "] size error");
            throw new RuntimeException(e);
        }
        return record;
    }

    /**	
     * 输出监听，同时添加日志信息
     * @param processListener 过程监听
     * @param msg         日志信息
     * @param log4jLevel  日志级别
     * @throws InterruptedException 备份中断异常
     */
    protected void onOutputAndLog(OpertProcessListener processListener, String msg, Level log4jLevel)
            throws InterruptedException {
        if (StringUtils.isNullOrEmpty(msg)) {
            return;
        }

        if (processListener != null) {
            if (processListener.isFinish()) {
                throw new InterruptedException("interrupt databse backup"); // 备份中断
            }

            if (processListener.getOutputListener() != null) {
                processListener.getOutputListener().onOutput(msg);
            }
        }

        if (log4jLevel == null) {
            try {
                Thread.sleep(200); // 延迟加载信息
            } catch (InterruptedException e) {
                // ignore
            }
            return;
        }

        // 输出日志信息
        if (Level.ERROR.equals(log4jLevel)) {
            logger.error(msg);
        }
        else if (Level.WARN.equals(log4jLevel)) {
            logger.warn(msg);
        }
        else if (Level.INFO.equals(log4jLevel)) {
            logger.info(msg);
        }
        else if (Level.DEBUG.equals(log4jLevel)) {
            logger.debug(msg);
        }
    }

}
