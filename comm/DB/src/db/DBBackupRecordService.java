package db;

import java.io.Serializable;
import java.util.Collection;

public interface DBBackupRecordService {

    /**	
     * <p>Description: 从缓存中获取所有备份记录信息     </p>
     * @return 备份记录信息集合
     */
    public Collection<DBBackupRecord> findAll();

    /**	
     * <p>Description: 从缓存中获取备份记录   </p>
     * @param uuid  备份记录ID
     * @return 备份记录详情
     */
    public DBBackupRecord findByUuid(String uuid);

    /** 
     * <p>Description: 添加备份记录信息，要同步添加缓存    </p>
     * @param record 备份记录信息
     */
    public void add(DBBackupRecord record);

    /**   
     * <p>Description: 删除备份记录，要同步删除缓存和文件       </p>
     * @param uuid  备份记录ID
     */
    public void delete(Serializable uuid);;

    /**	
     * <p>Description: 批量删除备份，要同步删除缓存和文件  </p>
     * @param ids 备份记录ID数组
     * @return  删除记录条数
     */
    public int deleteByIds(Serializable[] ids);

    /**	
     * <p>Description: 获取最大备份记录保存限值      </p>
     * @return 最大备份记录限值
     */
    public Integer getMaxLimitNum();

    /**	
     * <p>Description: 设置最大备份记录保存限值 </p>
     * @param limitNum 限值
     */
    public void setMaxLimitNum(Integer limitNum);

    /**	
     * <p>Description: 初始化             </p>
     */
    public void initialize();

}
