package db.restore;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import db.DBBackupRecord;
import db.DBOperatorEvent;
import db.DBRestore;
import db.DBRestoreBeanHandler;
import db.OpertProcessListener;
import kernel.util.StringUtils;

public abstract class AbstractRestoreImpl implements DBRestore, DBRestoreBeanHandler {

    /** 
     * logger
     */
	protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    /**    
     * 事件通知
     */
    protected DBOperatorEvent operatorEvent;

    /**    
     * 备份文件后缀
     * @return 备份文件后缀名
     */
    protected abstract String getBackupFileSuffix();

    @Override
    public boolean restore(File file, Map<String, Object> params, OpertProcessListener processListener) {
        if (params == null) {
            params = new HashMap<String, Object>();
        }
        params = parserMapParams(params); // 解析参数

//        operatorEvent.beforeRestore(file, params); // 操作前事件通知

        boolean result = false;
        try {
            if (doEnvironmentCheck(file, params, processListener)) {
                result = doResotre(file, params, processListener); // 执行还原操作
            }
        } catch (Throwable e) {
            result = false;
            logger.error("restore database error", e);
            throw new RuntimeException(e);
        } finally {
            if (processListener != null) {
                processListener.onExit();
            }

            clearAfterRestore(file, params); // 还原后清理
        }

        String msg = "Finished - " + (result ? "Successfully" : "Failed");
        onOutputAndLog(processListener, msg, (result ? Level.INFO : Level.ERROR));

        params.put("result", result); // 结果
//        operatorEvent.afterRestore(file, params);
        return result;
    }

    /**   
     * 还原清理工作（清除临时目录、临时文件等操作
     * @param file   备份文件
     * @param params 参数
     */
    protected void clearAfterRestore(File file, Map<String, Object> params) {
        // default do nothing
    }

    @Override
    public boolean restore(String filePath, Map<String, Object> params, OpertProcessListener processListener) {
        return restore(new File(filePath), params, processListener);
    }

    @Override
    public boolean restore(DBBackupRecord record, Map<String, Object> params, OpertProcessListener processListener) {
        Assert.isTrue(record != null && record.getFilePath() != null);
        return restore(new File(record.getFilePath()), params, processListener);
    }

    /** 
     * <p>Description: 解析参数            </p>
     * <p>Create Time: 2013-2-7   </p>
     * @author weiminghua
     * @param params 参数
     * @return 参数
     */
    protected abstract Map<String, Object> parserMapParams(Map<String, Object> params);

    /**   
     * 运行环境检测
     * @param file      待还原的文件
     * @param params    参数
     * @param processListener  监听器
     * @return 结果
     */
    protected boolean doEnvironmentCheck(File file, Map<String, Object> params, OpertProcessListener processListener) {
        Assert.isTrue(params != null && file != null);

        // TODO:doEnvironmentCheck
        if (!file.exists()) {
            logger.error("restore database failed for file[" + file.getPath() + "] not exists");
            return false;
        }

        String suffix = getBackupFileSuffix();
        if (-1 == file.getPath().indexOf(suffix)) {
            logger.error("The file[" + file.getPath() + "] not end of [" + suffix + "]");
            return false;
        }
        return true;
    }

    /**    
     * 执行备份
     * @param file      待还原的文件
     * @param params    参数
     * @param processListener  监听器
     * @return 结果
     */
    protected abstract boolean doResotre(File file, Map<String, Object> params, OpertProcessListener processListener);

    /**
     * @param operatorEvent The operatorEvent to set.
     */
    public void setOperatorEvent(DBOperatorEvent operatorEvent) {
        this.operatorEvent = operatorEvent;
    }

    /**    
     * 输出监听，同时添加日志信息
     * @param processListener 过程监听
     * @param msg         日志信息
     * @param log4jLevel  日志级别
     */
    protected void onOutputAndLog(OpertProcessListener processListener, String msg, Level log4jLevel) {
        if (StringUtils.isNullOrEmpty(msg)) {
            return;
        }

        if (processListener != null) {
            // 不支持中断
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
