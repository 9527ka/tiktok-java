package db;

import java.io.File;
import java.util.Map;

public interface DBRestore {

    /**   
     * <p>Description: 从文件还原数据库 </p>
     * @param file      待还原的文件
     * @param params    参数(若缺省则使用系统默认配置)<pre>
     *  ip -- IP地址
     *  port -- 端口
     *  databaseName -- 数据库名
     *  username -- 用户名
     *  password -- 密码</pre>
     *  
     * @param processListener  监听器
     * @return 结果
     */
    public boolean restore(File file, Map<String, Object> params, OpertProcessListener processListener);

    /**
     * <p>Description: 从文件还原数据库 </p>
     * @param filePath  待还原的文件路径
     * @param params    参数
     * @param processListener  监听器
     * @return 结果
     */
    public boolean restore(String filePath, Map<String, Object> params, OpertProcessListener processListener);

    /**	
      <p>Description: 从备份记录还原数据库 </p>
     * @param record    备份记录
     * @param params    参数
     * @param processListener  监听器
     * @return 结果
     */
    public boolean restore(DBBackupRecord record, Map<String, Object> params, OpertProcessListener processListener);

}
