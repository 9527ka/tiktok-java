package project.web.admin.controller.upload;

import kernel.util.ImageDispatcher;
import kernel.util.PropertiesLoaderUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import project.Constants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.Properties;

@RestController
@CrossOrigin
public class ShowImgController {

    public final String basePath = "/public/showimg";
    private static Properties properties = PropertiesLoaderUtils.loadProperties("config/system.properties");

    @RequestMapping(basePath+"!showImg.action")
    public void showImg(HttpServletRequest request,
                        HttpServletResponse response, String imagePath) throws Exception {
        responseFile(response, imagePath);
    }

    /**
     * 响应输出图片文件
     * @param response
     * @param imgFile
     *//*
     */
    private void responseFile(HttpServletResponse response, String imagePath) {
        try(InputStream is = getDownloadFile(imagePath);
            OutputStream os = response.getOutputStream();){
            byte [] buffer = new byte[1024]; // 图片文件流缓存池
            while(is.read(buffer) != -1){
                os.write(buffer);
            }
            os.flush();
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
    }
    public InputStream getDownloadFile(String imagePath) throws FileNotFoundException {
        BufferedInputStream bis = null;
        try {
            boolean goback = false;
            File fl = null;
            if ((imagePath == null) || (imagePath.trim().length() <= 0)) {
                fl = new File(Constants.IMAGES_DIR + "noimage.jpg");
                goback = true;
            }
            if (!goback) {
                fl = ImageDispatcher.findFile(imagePath);
                if (fl == null) {
                    fl = new File(Constants.IMAGES_DIR + "noimage.jpg");
                }
                if (!fl.exists()) {
                    fl = new File(Constants.IMAGES_DIR + "noimage.jpg");
                }

            }

            FileInputStream fis = new FileInputStream(fl);
            bis = new BufferedInputStream(fis);
        } catch (Throwable localThrowable) {
        }

        return bis;
    }
}
