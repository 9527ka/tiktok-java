package db.support;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.Element;

import db.Constants;
import db.DBBackupRecord;
import db.DBBackupRecordService;
import db.map.CacheMap;
import db.util.ConfigUtils;
import db.util.HandleXML;
import db.util.IOUtil;
import kernel.util.DateUtils;

public class DBBackupRecordServiceImpl implements DBBackupRecordService {

    /**	
     * LOG
     */
	private Logger logger = LogManager.getLogger(this.getClass().getName()); 

    /**	
     * 备份信息XML存储路径
     */
    private final static String BACKUP_RECORD_XML_PATH = ConfigUtils.getBackupRecordXMLPath();

    /**	
     * 文件锁
     */
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    /**	
     * 备份信息缓存
     */
    private static CacheMap<Serializable, DBBackupRecord> BKRECORD_CACHE = new CacheMap<Serializable, DBBackupRecord>();

    /**	
     * 备份文件保存个数最大限值
     */
    private static Integer MAX_LIMIT_NUM;

    /**   
     * 默认备份文件个数最大限值
     */
    private static Integer DEFAULT_MAX_LIMITNUM = 20;

    @Override
    public Integer getMaxLimitNum() {
        if (MAX_LIMIT_NUM == null) {
            MAX_LIMIT_NUM = DEFAULT_MAX_LIMITNUM;
        }
        return MAX_LIMIT_NUM;
    }

    @Override
    public void setMaxLimitNum(Integer limitNum) {
        if (limitNum == null) {
            throw new RuntimeException("database_maxLimitNum_setting_null");
        }
        MAX_LIMIT_NUM = limitNum;

        lock.writeLock().lock();
        FileInputStream fis = null;
        try {
            File file = new File(BACKUP_RECORD_XML_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            fis = new FileInputStream(BACKUP_RECORD_XML_PATH);
            Document document = HandleXML.getDocument(fis, "DbBackupRecordList");

            Element maxLimitNumE = (Element) document.selectSingleNode("/DbBackupRecordList/maxLimitNum");
            if (maxLimitNumE == null) {
                maxLimitNumE = document.getRootElement().addElement("maxLimitNum");
            }
            maxLimitNumE.setText(String.valueOf(MAX_LIMIT_NUM));

            HandleXML.writeToXML(document, BACKUP_RECORD_XML_PATH);
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            IOUtil.closeQuietly(fis);
            lock.writeLock().unlock();
        }
    }

    @Override
    public Collection<DBBackupRecord> findAll() {
        return BKRECORD_CACHE.values();
    }

    @Override
    public DBBackupRecord findByUuid(String uuid) {
        if (uuid == null) {
        	throw new RuntimeException("database_backuprecord_uuid_null");
//            throw new FastFailException(MessageFormat.format("database_backuprecord_uuid_null"));
        }
        return BKRECORD_CACHE.get(uuid);
    }

    @Override
    public void add(DBBackupRecord record) {
        if (record == null) {
        	throw new RuntimeException("database_backuprecord_add_null");
//            throw new FastFailException(MessageFormat.format("database_backuprecord_add_null"));
        }

        lock.writeLock().lock();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(BACKUP_RECORD_XML_PATH);
            Document document = HandleXML.getDocument(fis, "DbBackupRecordList");
            Element root = document.getRootElement();
            Element recordNode = root.addElement("DbBackupRecord");
            String uuid = record.getUuid();
            recordNode.addAttribute("uuid", uuid != null ? uuid : "");

            Element nameNode = recordNode.addElement("name");
            nameNode.setText(record.getName() != null ? record.getName() : "");

            Element filePathNode = recordNode.addElement("filePath");
            filePathNode.setText(record.getFilePath() != null ? record.getFilePath() : "");

            DateFormat sdf = DateUtils.createDateFormat(Constants.DB_BACKUP_TIME_FORMAT);
            Element timePathNode = recordNode.addElement("backupTime");
            timePathNode.setText(record.getBackupTime() != null ? sdf.format(record.getBackupTime()) : "");

            Element fileSizeNode = recordNode.addElement("fileSize");
            fileSizeNode.setText(String.valueOf(record.getFileSize()));

            Element descriptionNode = recordNode.addElement("description");
            String description = record.getDescription();
            descriptionNode.setText(description == null ? "" : description);

            // 超过最大限制值，去除旧备份
            if (BKRECORD_CACHE.size() >= getMaxLimitNum()) {
                HandleXML.deleteFirstNode("/DbBackupRecordList/DbBackupRecord", document);
            }

            HandleXML.writeToXML(document, BACKUP_RECORD_XML_PATH);
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            IOUtil.closeQuietly(fis);
            lock.writeLock().unlock();
        }

        putBackupRecordCache(record);

    }

    @Override
    public void delete(Serializable uuid) {
        if (uuid == null) {
        	throw new RuntimeException("database_backuprecord_delete_null");
//            throw new FastFailException(MessageFormat.format("database_backuprecord_delete_null"));
        }

        DBBackupRecord record = BKRECORD_CACHE.get(uuid);
        if (record == null) {
        	throw new RuntimeException("database_backuprecord_delete_null");
//            throw new FastFailException(MessageFormat.format("database_backuprecord_delete_null"));
        }

        // 删除备份文件
        File file = new File(record.getFilePath());
        if (file.exists()) {
            if (file.delete()) {
                String msg = MessageFormat.format("database_backuprecord_delete_file_failed", file.getAbsoluteFile());
                throw new RuntimeException(msg);
//                throw new FastFailException(msg);
            }
        }

        lock.writeLock().lock();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(BACKUP_RECORD_XML_PATH);
            Document document = HandleXML.getDocument(fis, "DbBackupRecordList");
            String xpathExpression = "/DbBackupRecordList/DbBackupRecord[@uuid=\"" + uuid + "\"]";
            Element ele = (Element) document.selectSingleNode(xpathExpression);
            if (ele != null) {
                ele.getParent().remove(ele);
            }

            HandleXML.writeToXML(document, BACKUP_RECORD_XML_PATH);
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            IOUtil.closeQuietly(fis);
            lock.writeLock().unlock();
        }
        removeBackupRecordCache(uuid);
    }

    @Override
    public int deleteByIds(Serializable[] ids) {
        int count = 0;
        for (Serializable id : ids) {
            delete(id);
            count++;
        }
        return count;
    }

    /**    
     * <p>Description: 存入缓存             </p>
     * <p>Create Time: 2013-2-5   </p>
     * @author weiminghua
     * @param record 备份记录
     */
    private void putBackupRecordCache(DBBackupRecord record) {
        BKRECORD_CACHE.put(record.getUuid(), record);
    }

    /**
     * <p>Description: 从缓存中删除数据 </p>
     * <p>Create Time: 2013-2-5   </p>
     * @author weiminghua
     * @param uuid 备份记录ID
     */
    private void removeBackupRecordCache(Serializable uuid) {
        BKRECORD_CACHE.remove(uuid);
    }

    @Override
    public void initialize() {

        lock.readLock().lock();
        FileInputStream fis = null;
        try {
            File file = new File(BACKUP_RECORD_XML_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            fis = new FileInputStream(BACKUP_RECORD_XML_PATH);
            Document document = HandleXML.getDocument(fis, "DbBackupRecordList");

            Element maxLimitNumE = (Element) document.selectSingleNode("/DbBackupRecordList/maxLimitNum");
            if (maxLimitNumE != null) {
                MAX_LIMIT_NUM = Integer.parseInt(maxLimitNumE.getText());
            }

            // 读取已有备份信息
            List<?> list = document.selectNodes("/DbBackupRecordList/DbBackupRecord");
            Iterator<?> iter = list.iterator();

            DateFormat sdf = DateUtils.createDateFormat(Constants.DB_BACKUP_TIME_FORMAT);
            while (iter.hasNext()) {
                Element e = (Element) iter.next();
                DBBackupRecord record = new DBBackupRecord();
                record.setUuid(e.attribute("uuid").getValue());

                Element nameE = e.element("name");
                if (nameE != null) {
                    record.setName(nameE.getText());
                }

                Element backupTimeE = e.element("backupTime");
                if (backupTimeE != null) {
                    record.setBackupTime(sdf.parse(backupTimeE.getText()));
                }

                Element filePathE = e.element("filePath");
                if (filePathE != null) {
                    record.setFilePath(filePathE.getText());
                }

                Element fileSizeE = e.element("fileSize");
                if (fileSizeE != null) {
                    record.setFileSize(Long.valueOf(fileSizeE.getText()));
                }

                Element descriptionE = e.element("description");
                if (descriptionE != null) {
                    record.setDescription(descriptionE.getText());
                }

                putBackupRecordCache(record); // 加入缓存
            }
        } catch (Exception e) {
            logger.error("error", e);
        } finally {
            IOUtil.closeQuietly(fis);
            lock.readLock().unlock();
        }
    }

}
