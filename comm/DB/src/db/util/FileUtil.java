package db.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {

    /**	
     * Member Description
     */
	private static Logger logger = LogManager.getLogger(FileUtil.class); 

    /**	.
     * 文件字符集
     */
    private static final String CHARSET_NAME = "UTF-8";

    /**	
     * <p>Description: 将内容写入指定文件            </p>
     * <p>Create Time:  </p>
     * @param fileContent   内容
     * @param filePath      文件路径
     */
    public static void string2File(String fileContent, String filePath) {
        FileOutputStream fos = null;
        try {
            File outFile = new File(filePath);
            if (!outFile.exists()) {
                outFile.createNewFile();
            }
            fos = new FileOutputStream(outFile);
            fos.write(fileContent.getBytes(CHARSET_NAME), 0, fileContent.getBytes(CHARSET_NAME).length);
        } catch (Exception e) {
        	logger.error("The string2File[" + filePath + "] ERROR", e);
        } finally {
            IOUtil.closeQuietly(fos);
        }
    }

    /**	
     * 读取文件
     * @param ins 文件流
     * @return  文件内容
     * @throws Exception 异常
     */
    public static String readStringAndClose(InputStream ins) throws Exception {
        StringBuilder buf = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(ins, "UTF-8"));
        String lineStr = null;
        while ((lineStr = br.readLine()) != null) {
            buf.append(lineStr).append("\n");
        }
        if (br != null) {
            br.close();
        }
        if (ins != null) {
            ins.close();
        }
        return buf.toString();
    }

    /**
     * 删除目录下的文件
     * @param path 目录名
     * @param isDeleteSelf 是否删除目录自身
     */
    public static void deleteDir(String path, boolean isDeleteSelf) {
        File dir = new File(path);
        // 检查参数
        if (dir == null || !dir.exists() || !dir.isDirectory()) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                // 删除所有文件
                file.delete();
            }
            else if (file.isDirectory()) {
                // 递规的方式删除文件夹
                deleteDir(file.getPath(), true);
            }
        }

        // 删除目录本身
        if (isDeleteSelf) {
            dir.delete();
        }
    }

    /**	
     * 获取文件大小
     * @param filePath  文件路径
     * @return  文件大小
     * @throws FileNotFoundException 文件不存在
     */
    public static long getFileSize(String filePath) throws FileNotFoundException {
        File file = new File(filePath);
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(file);
                size = fis.available();
            } catch (Exception e) {
            } finally {
                IOUtil.closeQuietly(fis);
            }
        } else {
            throw new FileNotFoundException();
        }
        return size;
    }

    /**	
     * 转换文件大小格式
     * @param fileSize  文件大小
     * @param df    格式
     * @return 文件大小格式
     */
    public static String formetFileSize(long fileSize, DecimalFormat df) {
        if (df == null) {
            df = new DecimalFormat("#.000");
        }
        String result = "";
        if (fileSize < 1024) {
            result = df.format((double) fileSize) + "B";
        }
        else if (fileSize < 1048576) { // 1024*1024
            result = df.format((double) fileSize / 1024) + "K";
        }
        else if (fileSize < 1073741824) { // 1024*1024*1024
            result = df.format((double) fileSize / 1048576) + "M";
        }
        else { // 1024*1024*1024*1024
            result = df.format((double) fileSize / 1073741824) + "G";
        }

        return result;
    }

}
