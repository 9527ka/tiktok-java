package db.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ZipUtils {

    /**	
     * logger
     */
	private  static Logger logger = LogManager.getLogger(ZipUtils.class); 

    /**	
     * <p>Description: 压缩文件     </p>
     * 
     * @param zname   生成文件名
     * @param source  待压缩文件
     */
    public static void createZip(String zname, String source) {
        logger.info("start compressing backup files...");
        try {
            File filezip = new File(zname);
            if (!filezip.exists()) {
                filezip.createNewFile();
            }
          
            ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zname));
            compressFile(zout, "", new File(source));
            zout.close();
            logger.info("compress backup files finished");
        } catch (Exception e) {
            logger.error("compress backup files error", e);
        }
    }

    /**	
     * <p>Description: 压缩文件             </p>
     * 
     * @param zout     ZipOutputStream
     * @param dir      相对路径
     * @param source   源文件
     * @throws IOException ioException
     */
    private static void compressFile(ZipOutputStream zout, String dir, File source) throws IOException {
        if (logger.isDebugEnabled()) {
            logger.debug("do compressing file:" + source);
        }
        if (source.isDirectory()) {
            File[] fs = source.listFiles();
            dir = dir + (dir.length() == 0 ? "" : "/") + source.getName();
            for (File f : fs) {
                compressFile(zout, dir, f);
            }
        }
        else {
            dir = dir.length() == 0 ? "" : dir + "/" + source.getName();
            ZipEntry entry = new ZipEntry(dir);

            // ZipEntry entry = new ZipEntry(source.getName());
            zout.putNextEntry(entry);
            FileInputStream fin = new FileInputStream(source);
            int size = 0;
            byte[] buf = new byte[10240];
            while ((size = fin.read(buf, 0, 10240)) != -1) {
                zout.write(buf, 0, size);
            }
            fin.close();
        }
    }

    /**	
     * <p>Description: 解压文件             </p>
     * 
     * @param source    源文件
     * @param destdir   目的目录
     */
    public static void unZip(String source, String destdir) {
        try {
            logger.info("start decompressing backup files...");
            FileInputStream fin = new FileInputStream(source);
            ZipInputStream zin = new ZipInputStream(fin);

            ZipEntry entry = null;
            while ((entry = zin.getNextEntry()) != null) {

                File filename = new File(destdir + entry.getName());
                if (logger.isDebugEnabled()) {
                    // System.out.println("正在解压" + entry.getName());
                    logger.debug("do decompressing file:" + entry.getName());
                }

                File path = new File(filename.getParentFile().getPath()); // 确认目标目录存在、解压文件夹存在
                if (entry.isDirectory()) {
                    if (!filename.exists()) {
                        filename.mkdirs();
                    }
                    zin.closeEntry();
                    continue;
                }
                if (!path.exists()) {
                    path.mkdirs();
                }

                FileOutputStream fout = new FileOutputStream(filename); // 得到目标文件流

                int size = 0;
                byte[] buf = new byte[256];
                while ((size = zin.read(buf)) != -1) { // 每个entry都有一个文件末尾的标识
                    fout.write(buf, 0, size);
                }
                zin.closeEntry();
                fout.close();
            }

            fin.close();
            zin.close();
            // System.out.println("解压完成!");
            logger.info("decompress backup files finished");
        } catch (Exception e) {
            logger.error("decompress backup files error", e);
        }
    }

}
