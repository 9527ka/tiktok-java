package db;

import java.util.Date;

import db.util.FileUtil;


public class DBBackupRecord {

    /**	
     * UUID
     */
    private String uuid;

    /**	
     * 备份名称
     */
    private String name;

    /** 
     * 备份时间
     */
    private Date backupTime;

    /**	
     * 备份文件路径
     */
    private String filePath;

    /**	
     * 备份文件大小(B)
     */
    private Long fileSize;

    /**	
     * 备注
     */
    private String description;

    /**
     * Gets the UUID.
     *
     * @return Returns the uuid.
     */
    public String getUuid() {
        return uuid;
    }

    /**
     * Sets the UUID.
     *
     * @param uuid The uuid to set.
     */
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    /**
     * Gets the 备份名称.
     *
     * @return Returns the name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the 备份名称.
     *
     * @param name The name to set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the 备份时间.
     *
     * @return Returns the backupTime.
     */
    public Date getBackupTime() {
        return backupTime;
    }

    /**
     * Sets the 备份时间.
     *
     * @param backupTime The backupTime to set.
     */
    public void setBackupTime(Date backupTime) {
        this.backupTime = backupTime;
    }

    /**
     * Gets the 备份文件路径.
     *
     * @return Returns the filePath.
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * Sets the 备份文件路径.
     *
     * @param filePath The filePath to set.
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * Gets the 备份文件大小(B).
     *
     * @return Returns the fileSize.
     */
    public Long getFileSize() {
        return fileSize;
    }

    /**
     * Sets the 备份文件大小(B).
     *
     * @param fileSize The fileSize to set.
     */
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    /**
     * Gets the 备注.
     *
     * @return Returns the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the 备注.
     *
     * @param description The description to set.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer("\n");
        str.append("===============================================================================");
        str.append("\n");
        str.append("<< ").append(getClass().getSimpleName()).append(" >>");
        str.append("\n");
        str.append("-------------------------------------------------------------------------------");
        str.append("\n");
        str.append(" UUID: ").append(getUuid());
        str.append("\n");
        str.append(" Name: ").append(getName());
        str.append("\n");
        str.append(" BackupTime: ").append(getBackupTime());
        str.append("\n");
        str.append(" FilePath: ").append(getFilePath());
        str.append("\n");
        str.append(" FileSize: ").append(FileUtil.formetFileSize(getFileSize(), null));
        str.append("\n");
        str.append(" Description: ").append(getDescription());
        str.append("\n");
        str.append("-------------------------------------------------------------------------------");
        return str.toString();
    }
}
