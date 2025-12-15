package project.blockchain;

/**
 * 
 * 二维码图片生产者
 *
 */
public interface QRProducerService {
	/**
	 * 生成二维码图片
	 * 
	 * @param content
	 *            二维码内容
	 * @return 图片url地址
	 */
	public String generate(String content);

}
