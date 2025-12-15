package project.user;

import java.util.List;
import java.util.Map;

/**
 * 
 * 二维码图片生产者
 *
 */
public interface QRGenerateService {
	/**
	 * 生成二维码图片
	 * 
	 * @param content 二维码内容
	 * @return 图片url地址
	 */
	public String generate(String content);
	
	public String generate(String content,String imgName);

	public String generate185(String content);

	public void generate_poster(String image_name, String usercode);

	public String generateWithdraw(String image_name, String address);

	public List<Map<String, String>> generate_poster_base64(String image_name, String usercode, String img_language);

}
