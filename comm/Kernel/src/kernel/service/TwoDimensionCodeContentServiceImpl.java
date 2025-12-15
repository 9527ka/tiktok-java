package kernel.service;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import util.DateUtil;
import util.RandomUtil;

public class TwoDimensionCodeContentServiceImpl implements TwoDimensionCodeContentService {
    private String imgPath;
    @Override
    public String encoder(String content) {
        String imagename = "/" + DateUtil.getToday("yyMMddHHmmss") + RandomUtil.getRandomNum(8) + ".png"; // 生成二维码图片相对
                                                                                                          // 地址和名称
        String pathname = imgPath + imagename; // 生成二维码图片存放的地址和名称

        File file = new File(pathname);
        int width = 691; // 图像宽度
        int height = 691; // 图像高度
        String format = "png";// 图像类型
        Map<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        BitMatrix bitMatrix;
        try {
            bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
            MatrixToImageWriter.writeToFile(bitMatrix, format, file);// 输出图像
        } catch (Exception e) {

            e.printStackTrace();
        } // 生成矩阵

        return imagename;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }
}
