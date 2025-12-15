package db.util.jdbc;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;

public class MysqlTools {

    /**    
     * 获取所有数据库表(BASE TABLE)
     * @param ip    数据库IP地址
     * @param port  数据库端口
     * @param databaseName  数据库名
     * @param username  用户名
     * @param password  密码
     * @return 数据库中的所有表
     */
    public static List<String> findAllDBTables(String ip, String port, String databaseName, String username,
            String password) {
        final List<String> tables = new ArrayList<String>();

        String sql = "select TABLE_NAME from TABLES where TABLE_SCHEMA = '" + databaseName
                + "' and TABLE_TYPE ='BASE TABLE'";
        JdbcTemplate jt = new JdbcTemplate(createDataSource(ip, port, "information_schema", username, password));
        jt.query(sql, new RowCallbackHandler() {
            public void processRow(ResultSet rs) throws SQLException {
                if (rs != null) {
                    tables.add(rs.getString(1));
                }
            }
        });

        return tables;
    }

    /**
     * 建立数据库连接
     * jdbc:mysql://${db.host}:3306
     * @param ip    数据库IP地址
     * @param port  数据库端口
     * @param databaseName  数据库名
     * @param username  用户名
     * @param password  密码
     * @return 数据库连接
     */
    public static BasicDataSource createDataSource(String ip, String port, String databaseName, String username,
            String password) {
        BasicDataSource ds = new BasicDataSource();
//        ds.setDriverClassName(MySQLConfig.DRIVERCLASSNAME);
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        ds.setUrl("jdbc:mysql://" + ip + ":" + port + "/" + databaseName + "?characterEncoding=utf8");
        ds.setUsername(username);
        ds.setPassword(password);
        ds.setTestOnBorrow(true);
        ds.setValidationQuery("select 1 ");
        return ds;
    }

}
