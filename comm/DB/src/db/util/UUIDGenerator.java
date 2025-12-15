package db.util;

import org.hibernate.id.UUIDHexGenerator;

public class UUIDGenerator {
    
    /**	
     * UUID生成器
     */
    private static UUIDHexGenerator UUID_GENERATOR = new UUIDHexGenerator();

    /**	
     * <p>Description: 生成UUID </p>
     * @return UUID 
     */
    public static String getUUID() {
        return (String) UUID_GENERATOR.generate(null, null);
    }

}