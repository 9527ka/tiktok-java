package db;

import java.util.Map;

public interface DBBackup {

    /**	
     * <p>Description: 数据库备份 </p>
     * 
     * @param params    参数(若缺省则使用系统默认配置) <pre>
     *  ip -- IP地址
     *  port -- 端口
     *  databaseName -- 数据库名
     *  username -- 用户名
     *  password -- 密码
     *  backupPath -- 备份存储路径</pre>
     *  
     * @param processListener  监听器
     * @return 结果
     */
    public boolean backup(Map<String, Object> params, OpertProcessListener processListener);

}
