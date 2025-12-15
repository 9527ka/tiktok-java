package db.util.jdbc;

import db.util.ConfigUtils;
import kernel.util.StringUtils;

public class DBTools {

    /**    
     * 处理有配置数据库安装目录情况、(通用方法)
     * <pre>说明：不适用于SQL Server</pre>
     * 
     * @param cmd    客户端命令
     * @param suffix 客户端程序后缀
     * @return  客户端命令 with 执行路径
     */
    public static String formatDBClientCmd(String cmd, String suffix) {
        String installedPath = ConfigUtils.getDBInstalledPath();
        if (StringUtils.isNullOrEmpty(installedPath)) {
            return cmd;
        }

        return new StringBuffer(formatRuntimeCmdPath(installedPath)) // 空格处理
                .append("/").append(cmd).append(".").append(suffix).toString();
    }

    /**	
     * Runtime CMD 空格路径处理
     * 
     * @param path   路径
     * @return  空格前后加\" \"
     */
    public static String formatRuntimeCmdPath(String path) {
        if (path.indexOf(" ") != -1) {
            path = path.replaceAll(" ", "\" \""); // 空格处理
        }
        return path;
    }

}
