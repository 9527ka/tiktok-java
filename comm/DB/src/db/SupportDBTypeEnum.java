package db;

public enum SupportDBTypeEnum {
    /**	
     * MySQL数据库
     */
    mysql,

    /**	
     * Oracle数据库
     */
    oracle,

    /**	
     * Postgre数据库
     */
    postgre,

    /**	
     * SQLServer数据库
     */
    sqlserver;

    /**	
     * @param type   数据库类型名
     * @return  support DBType
     */
    public static SupportDBTypeEnum getEnum(String type) {
        if (type == null || "".equals(type = type.trim())) {
            throw new IllegalArgumentException("The database type can not be NULL");
        }

        SupportDBTypeEnum result = null;
        try {
            result = SupportDBTypeEnum.valueOf(type);
            return result;
        } catch (Throwable e) {
            // ignore
        }

        for (SupportDBTypeEnum supportType : SupportDBTypeEnum.values()) {
            if (supportType.name().equalsIgnoreCase(type)) {
                return supportType;
            }
        }

        if (result == null) {
            throw new IllegalArgumentException("The database type with name '" + type + "' is not support");
        }
        return result;
    }
}
