package project.web.admin.controller.vo;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

/**
 * 文件上传参数
 *
 * @Author: shy
 * @Description:
 * @Date: create in 2022/10/21 10:34
 */
@Data
public class FileUploadParamsVo implements Serializable {
    protected static final long serialVersionUID = 1L;

    private MultipartFile file;

    private String moduleName;
}
