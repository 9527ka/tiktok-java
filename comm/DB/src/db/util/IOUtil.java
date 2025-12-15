package db.util;

import java.io.InputStream;
import java.io.OutputStream;

public class IOUtil {

    /**	
     * <p>Description: 关闭输入流             </p>
     * @param is   inputStream 
     */
    public static void closeQuietly(InputStream is) {
        if (is == null) {
            return;
        }
        
        try {
            is.close();
        } catch (Throwable e) {
            // do nothing
        }
    }

    /**	
     * <p>Description: 关闭输出流             </p>
     * @param os    outputStream
     */
    public static void closeQuietly(OutputStream os) {
        if (os == null) {
            return;
        }
        
        try {
            os.close();
        } catch (Throwable e) {
            // do nothing
        }
    }

}
