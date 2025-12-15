package util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class FileUtil {
    
    private static Logger logger = LogManager.getLogger(FileUtil.class);
    
	private static final int BUFFER_SIZE = 16 * 1024;
	
	public static void copy(String src, String dst) {
        File srcFile = new File(src);
        File dstFile = new File(dst);
        if(!dstFile.getParentFile().exists()){
            dstFile.getParentFile().mkdirs();
        }
        copy(srcFile, dstFile);
    }
	
	public static void copy(File src, File dst) {
        try {
            InputStream in = null;
            OutputStream out = null;
          
            try {
                in = new BufferedInputStream(new FileInputStream(src), BUFFER_SIZE);
                out = new BufferedOutputStream(new FileOutputStream(dst), BUFFER_SIZE);
                byte[] buffer = new byte[BUFFER_SIZE];
                while (in.read(buffer) > 0) {
                    out.write(buffer);
                }
            } finally {
                if (null != in) {
                    in.close();
                }
                if (null != out) {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
	
	public static boolean zipFile(String zipFile) {
        return zipFile(zipFile, zipFile + ".zip", true, false);
    }

    /***************************************************************************
     * 压缩文件
     * 
     * @param backupFile
     */
    public static boolean zipFile(String zipFile, String destFile, boolean includeChildren, boolean includeBlankDir) {

        boolean zipFileSuccess = false;
        JarOutputStream zo = null;
        try {
            zo = new JarOutputStream(new BufferedOutputStream(new FileOutputStream(destFile)));
            zip(zipFile, new File(zipFile), zo, includeChildren, includeBlankDir);

            zipFileSuccess = true;
        } catch (FileNotFoundException e) {
            logger.error("压缩文件的时候，发生FileNotFoundException!", e);
        } catch (IOException e) {
            logger.error("压缩文件的时候，发生IOException!", e);
        } finally {
            if (zo != null) {
                IOUtils.closeQuietly(zo);
            }
        }
        return zipFileSuccess;
    }
    
    /**
     * @param path
     *            要压缩的路径, 可以是目录, 也可以是文件.
     * @param basePath
     *            如果path是目录,它一般为new File(path), 作用是:使输出的zip文件以此目录为根目录,
     *            如果为null它只压缩文件, 不解压目录.
     * @param zo
     *            压缩输出流
     * @param isRecursive
     *            是否递归
     * @param isOutBlankDir
     *            是否输出空目录, 要使输出空目录为true,同时baseFile不为null.
     * @throws IOException
     */
    public static void zip(String path, File basePath, JarOutputStream zo, boolean isRecursive, boolean isOutBlankDir)
            throws IOException {
        File inFile = new File(path);

        File[] files = new File[0];
        // try {
        if (inFile.isDirectory()) { // 是目录
            files = inFile.listFiles();
        }
        else if (inFile.isFile()) { // 是文件
            files = new File[1];
            files[0] = inFile;
        }
        byte[] buf = new byte[1024];
        int len;
        for (int i = 0; i < files.length; i++) {
            String pathName = "";
            if (basePath != null) {
                if (basePath.isDirectory()) {
                    pathName = files[i].getPath().substring(basePath.getPath().length() + 1);
                }
                else {// 文件
                    pathName = files[i].getPath().substring(basePath.getParent().length() + 1);
                }
            }
            else {
                pathName = files[i].getName();
            }
            if (files[i].isDirectory()) {
                if (isOutBlankDir && basePath != null) {
                    zo.putNextEntry(new ZipEntry(pathName + File.separator)); // 可以使空目录也放进去
                }
                if (isRecursive) { // 递归
                    zip(files[i].getPath(), basePath, zo, isRecursive, isOutBlankDir);
                }
            }
            else {
                FileInputStream fin = new FileInputStream(files[i]);
                zo.putNextEntry(new ZipEntry(pathName));
                while ((len = fin.read(buf)) > 0) {
                    zo.write(buf, 0, len);
                }
                fin.close();
            }
        }

    }
}
