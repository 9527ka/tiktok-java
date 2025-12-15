package project.user.internal;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;

import kernel.http.HttpHelper;
import kernel.util.ImageUtils;
import project.Constants;
import project.syspara.SysparaService;
import project.user.QRGenerateService;

public class QRGenerateServiceImpl implements QRGenerateService {

	private SysparaService sysparaService;
	@Override
	public String generate(String content) {
		String image_name = "/qr/" + content + ".png";
		content = Constants.WEB_URL + "/register.html?usercode=" + content;
		boolean openButton = sysparaService.find("short_url_open_button").getBoolean() ;
		if(openButton) {
			content = sysparaService.find("agent_qr_url").getValue() + "/register.html?usercode=" + content;
			boolean isCn = sysparaService.find("short_url_cn_button").getBoolean() ;
			if(isCn) {
				content = shortUrlCn(content);
			}else {
				content = shortUrl(content);
			}
		}
		
		String filepath = Constants.IMAGES_DIR + image_name;
		File file = new File(filepath);
		int width = 260;
		int height = 260;
		String format = "png";
		Map hints = new HashMap();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(bitMatrix, format, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return image_name;
	}
	
	public String shortUrl(String longUrl) {
		String url = "https://cutt.ly/scripts/shortenUrl.php";
        Map<String,Object> param = new HashMap<>();
        param.put("url",longUrl);
        String s = HttpHelper.sendPostHttp(url, param, false);
        return s;
	}
	public String shortUrlCn(String longUrl) {
		String url = "https://www.xyixy.com/api/";
        Map<String,Object> param = new HashMap<>();
        param.put("url",longUrl);
        param.put("key",sysparaService.find("cn_short_url_key").getValue());
        String s = HttpHelper.sendGetHttp(url, param);
        return s;
	}
	public String generate(String content,String imgName) {
		String image_uri = "/qr/" + imgName + ".png";

		String filepath = Constants.IMAGES_DIR + image_uri;
		File file = new File(filepath);
//		if(file.exists()) {//存在则删了重新建，保证内容最新，不删除则内容不会覆盖
//			file.delete();
//			file = new File(filepath);
//		}
		int width = 260;
		int height = 260;
		String format = "png";
		Map hints = new HashMap();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(bitMatrix, format, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return image_uri;
	}
	@Override
	public String generate185(String content) {
		String image_name = "/qr/" + content + "2.png";
		content = Constants.WEB_URL + "/register.html?usercode=" + content;
		// String image_name = "/qr/" + UUIDGenerator.getUUID() + ".png";
		String filepath = Constants.IMAGES_DIR + image_name;
		File file = new File(filepath);
		int width = 185;
		int height = 185;
		String format = "png";
		Map hints = new HashMap();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		hints.put(EncodeHintType.MARGIN, 1);// 二维码空白区域,最小为0也有白边,只是很小,最小是6像素左右

		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(bitMatrix, format, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return image_name;
	}

	public void generate_poster(String image_name, String usercode) {
		String backgroundPath = "";
		String smallPath = Constants.IMAGES_DIR + image_name;
		String resultPaht = "";
		for (int i = 0; i < 5; i++) {
			backgroundPath = Constants.IMAGES_DIR + "/poster/poster_" + i + "_zh-CN.png";
			resultPaht = Constants.IMAGES_DIR + "/qr/" + usercode + "_poster_" + i + "_zh-CN.png";
			ImageUtils.image_usercode(backgroundPath, smallPath, "png", resultPaht);
		}
		for (int i = 0; i < 5; i++) {
			backgroundPath = Constants.IMAGES_DIR + "/poster/poster_" + i + "_CN.png";
			resultPaht = Constants.IMAGES_DIR + "/qr/" + usercode + "_poster_" + i + "_CN.png";
			ImageUtils.image_usercode(backgroundPath, smallPath, "png", resultPaht);
		}
		for (int i = 0; i < 5; i++) {
			backgroundPath = Constants.IMAGES_DIR + "/poster/poster_" + i + "_en.png";
			resultPaht = Constants.IMAGES_DIR + "/qr/" + usercode + "_poster_" + i + "_en.png";
			ImageUtils.image_usercode(backgroundPath, smallPath, "png", resultPaht);
		}

	}

	@Override
	public String generateWithdraw(String content, String address) {
		String image_name = "/qr/" + content + ".png";
		String filepath = Constants.IMAGES_DIR + image_name;
		File file = new File(filepath);
		int width = 260;
		int height = 260;
		String format = "png";
		Map hints = new HashMap();
		hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
		try {
			BitMatrix bitMatrix = new MultiFormatWriter().encode(address, BarcodeFormat.QR_CODE, width, height, hints);
			MatrixToImageWriter.writeToFile(bitMatrix, format, file);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return image_name;
	}

	public List<Map<String, String>> generate_poster_base64(String image_name, String usercode, String img_language) {

		List<Map<String, String>> list_image = new ArrayList<Map<String, String>>();

		String backgroundPath = "";
		String smallPath = Constants.IMAGES_DIR + image_name;
		String resultPaht = "";

		for (int i = 0; i < 5; i++) {
			backgroundPath = Constants.IMAGES_DIR + "/poster/poster_" + i + "_" + img_language + ".png";
			resultPaht = usercode + "_poster_" + i + "_" + img_language;
			Map<String, String> map_image = new HashMap<String, String>();
			map_image.put(resultPaht, ImageUtils.image_usercodeBase64(backgroundPath, smallPath, "png", resultPaht));
			list_image.add(map_image);
		}

		return list_image;

	}
	public void setSysparaService(SysparaService sysparaService) {
		this.sysparaService = sysparaService;
	}

	
}
