package web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;
import java.util.UUID;


public class FileUploadUtils {

    private static final Logger log = LoggerFactory.getLogger(FileUploadUtils.class);


    public static String getAutoFileName(MultipartFile file){
        String fileName = file.getOriginalFilename();
        assert fileName != null;
        String fileExtensionName = fileName.substring(fileName.lastIndexOf(".") + 1);
        return "uploads/"+UUID.randomUUID() + "." + fileExtensionName;
    }

    /**
     * 保存上传文件
     * @param file 文件
     * @param path 路径
     */
    public static void saveUploadFile(MultipartFile file, String path) throws IOException {
        // 文件上传后的路径
        String rootPath = System.getProperty("user.dir");
        File dst = new File(rootPath + "/" + path);

        // 检测是否存在目录
        if (!dst.getParentFile().exists()) {
            dst.getParentFile().mkdirs();
        }

        InputStream stream = file.getInputStream();
        FileOutputStream bos = new FileOutputStream(dst);
        boolean success = false;
        int n = 0;
        int bufferSize = 100;
        byte[] buffer = new byte[bufferSize];
        try {
            while (true) {
                n = stream.read(buffer, 0, bufferSize);
                if (n == -1) break;
                bos.write(buffer, 0, n);
            }
            success = true;
        } catch (Throwable ex) {
            log.error("上传文件异常", ex);
        } finally {
            bos.flush();
            bos.close();
        }
        if(success){
            // 设置文件权限为777
            try {
                Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
                Files.setPosixFilePermissions(dst.toPath(), permissions);
            } catch (UnsupportedOperationException e) {
                log.error("当前系统不支持POSIX权限设置", e);
            }
        }

    }

}
