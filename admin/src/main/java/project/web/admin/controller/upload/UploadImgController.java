package project.web.admin.controller.upload;

import com.alibaba.fastjson.JSONObject;
import kernel.exception.BusinessException;
import kernel.util.ImageDispatcher;
import kernel.util.PropertiesLoaderUtils;
import kernel.web.ResultObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import project.web.admin.controller.vo.FileUploadParamsVo;
import project.web.admin.impl.AwsS3OSSFileService;
import web.FileUploadUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

@RestController
@CrossOrigin
public class UploadImgController {
    private static Log logger = LogFactory.getLog(UploadImgController.class);
    private static Properties properties = PropertiesLoaderUtils.loadProperties("config/system.properties");
    @Autowired
    AwsS3OSSFileService awsS3OSSFileService;
    @Value("${oss.aws.s3.bucketName}")
    private String bucketName;

    @Value("${oss.config.provider:amazon}")
    private String provider;

    @RequestMapping(value = "normal/uploadimg!execute.action")
    public Object execute(FileUploadParamsVo filePrams) {
        ResultObject resultObject = new ResultObject();
        try {
            if (filePrams.getFile() == null || filePrams.getFile().getSize() == 0) {
                resultObject.setCode("1");
                resultObject.setMsg("图片不能为空");
                return resultObject;
            }
            if (StringUtils.isBlank(filePrams.getModuleName())) {
                resultObject.setCode("1");
                resultObject.setMsg("模块名不能为空");
                return resultObject;
            }

            if (!awsS3OSSFileService.isImageFile(filePrams.getFile().getOriginalFilename())) {
                resultObject.setCode("1");
                resultObject.setMsg("请上传图片格式的文件");
                return resultObject;
            }
            if (filePrams.getFile().getSize() / 1024L > 30720L) {
                resultObject.setCode("1");
                resultObject.setMsg("图片大小不能超过30M");
                return resultObject;
            }
            if("local".equals(provider)){
                // 本地文件上传
               String fileName = FileUploadUtils.getAutoFileName(filePrams.getFile());
               resultObject.setData('/'+fileName);
               FileUploadUtils.saveUploadFile(filePrams.getFile(),"./"+fileName);
               return resultObject;
            }
            // oss上传
            String url = String.format("https://%s.s3.amazonaws.com/", bucketName);
            resultObject.setData(url + awsS3OSSFileService.putS3Object(filePrams.getModuleName(), filePrams.getFile(), 0.3f));
        } catch (BusinessException e) {
            resultObject.setCode("1");
            resultObject.setMsg(e.getMessage());
            logger.error("文件上传失败", e);
            return resultObject;
        } catch (Exception e) {
            resultObject.setCode("1");
            resultObject.setMsg("服务器错误");
            logger.error("文件上传失败", e);
            return resultObject;
        }

        return resultObject;
    }

    @RequestMapping(value = "normal/uploadimg!execute1.action")
    public Object execute1(FileUploadParamsVo filePrams) {
        JSONObject resultObject = new JSONObject();
        try {
            if (filePrams.getFile() == null || filePrams.getFile().getSize() == 0) {
                resultObject.put("error", 1);
                resultObject.put("message", "图片不能为空");
                return resultObject;
            }
//            if (StringUtils.isBlank(filePrams.getModuleName())) {
//                resultObject.setCode("1");
//                resultObject.setMsg("模块名不能为空");
//                return resultObject;
//            }
            filePrams.setModuleName("richText");
            if (!awsS3OSSFileService.isImageFile(filePrams.getFile().getOriginalFilename())) {
                resultObject.put("error", 1);
                resultObject.put("message", "请上传图片格式的文件");
                return resultObject;
            }
            if (filePrams.getFile().getSize() / 1024L > 30720L) {
                resultObject.put("error", 1);
                resultObject.put("message", "图片大小不能超过30M");
                return resultObject;
            }

            if("local".equals(provider)){
                // 本地文件上传
                String fileName = FileUploadUtils.getAutoFileName(filePrams.getFile());
                resultObject.put("url","/"+fileName);
                FileUploadUtils.saveUploadFile(filePrams.getFile(),"./"+fileName);
                return resultObject;
            }
            // oss上传
            String url = String.format("https://%s.s3.amazonaws.com/", bucketName);
            resultObject.put("error", 0);
            resultObject.put("url",  url + awsS3OSSFileService.putS3Object(filePrams.getModuleName(), filePrams.getFile(), 0.3f));
        } catch (BusinessException e) {
            resultObject.put("error", 1);
            resultObject.put("message",e.getMessage());
            logger.error("文件上传失败", e);
            return resultObject;
        } catch (Exception e) {
            resultObject.put("error", 1);
            resultObject.put("message", "服务器错误");
            logger.error("文件上传失败", e);
            return resultObject;
        }

        return resultObject;
    }


}
