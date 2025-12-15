package kernel.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.xml.bind.DatatypeConverter;

import cn.hutool.core.codec.Base64;
import org.springframework.util.Assert;
import java.awt.Image;

public class ImageUtils {
	/**
	 * 图片合成
	 *
	 * @param backgroundPath 底图
	 * @param smallPath      小图
	 * @param type           生成图片类型jpg,png...
	 * @param resultPaht     生成图片保存路径
	 */
	public static void image(String backgroundPath, String smallPath, String type, String resultPaht) {
		try {
			Assert.hasText(backgroundPath, "底图路径为空");
			Assert.hasText(smallPath, "小图路径为空");
			BufferedImage small = getBufferedImageFromUrl(smallPath);
			BufferedImage image = getBufferedImageFromUrl(backgroundPath);
			// 生成画笔
			Graphics g = image.getGraphics();
			g.drawImage(small, image.getWidth() - small.getWidth(), image.getHeight() - small.getHeight(),
					image.getWidth(), image.getHeight(), null);
			ImageIO.write(image, type, new File(resultPaht));
		} catch (IOException e) {
			throw new RuntimeException("合成图片失败", e);
		}
	}

	public static void image_usercode(String backgroundPath, String smallPath, String type, String resultPaht) {
		try {
			Assert.hasText(backgroundPath, "底图路径为空");
			Assert.hasText(smallPath, "小图路径为空");
			BufferedImage small = getBufferedImageFromUrl(smallPath);
			BufferedImage image = getBufferedImageFromUrl(backgroundPath);
			// 生成画笔
			Graphics g = image.getGraphics();
			int x = (int) Arith.sub(Arith.div(image.getWidth(), 2), 92);
			int y = (int) Arith.sub(image.getHeight(), Arith.add(small.getHeight(), 92));
			g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);
			ImageIO.write(image, type, new File(resultPaht));
		} catch (IOException e) {
			throw new RuntimeException("合成图片失败", e);
		}
	}

	/**
	 * 根据图片url获取图片
	 *
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private static BufferedImage getBufferedImageFromUrl(String url) throws IOException {
		if (url.startsWith("https://") || url.startsWith("http://")) {
			return ImageIO.read(new URL(url));
		} else {
			return ImageIO.read(new File(url));
		}
	}

	/**
	 * 合成图片并返回base64字符串
	 *
	 */
	public static String image_usercodeBase64(String backgroundPath, String smallPath, String type, String resultPaht) {
		try {
			Assert.hasText(backgroundPath, "底图路径为空");
			Assert.hasText(smallPath, "小图路径为空");
			BufferedImage small = getBufferedImageFromUrl(smallPath);
			BufferedImage image = getBufferedImageFromUrl(backgroundPath);
			// 生成画笔
			Graphics g = image.getGraphics();
			int x = (int) Arith.sub(Arith.div(image.getWidth(), 2), 92);
			int y = (int) Arith.sub(image.getHeight(), Arith.add(small.getHeight(), 40));

			g.drawImage(small, x, y, small.getWidth(), small.getHeight(), null);

			ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
			ImageIO.write(image, type, outputStream);
			String data = DatatypeConverter.printBase64Binary(outputStream.toByteArray());

			return data;

//			ImageIO.write(image, type, new File(resultPaht));
		} catch (IOException e) {
			throw new RuntimeException("合成图片失败", e);
		}
	}
	
	/**
	 *   文件转 base64
	 * @param file
	 * @return
	 */
	public static String file2Base64(File file) {
		if (file == null) {
			return null;
		}
		String base64 = null;
		FileInputStream fin = null;
		try {
			fin = new FileInputStream(file);
			byte[] buff = new byte[fin.available()];
			fin.read(buff);
			base64 = Base64.encode(buff);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (fin != null) {
				try {
					fin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return base64;
	}
	
	/**
	 * 指定图片宽度和高度或压缩比例对图片进行压缩
	 * @param file	文件
	 * @param rate	压缩比例（为空或者1，原比例压缩）
	 */
	public static String reduceImg(File file,Float rate) throws Exception {
		String res = "";
		File srcfile = file;
		int widthdist = 0, heightdist = 0;
		// 检查图片文件是否存在
//		Float rate = null;
//		rate = 1f;
//		System.out.println(file.length());
		// 如果比例不为空则说明是按比例压缩
		if (rate != null && rate > 0) {
			// 获得源图片的宽高存入数组中
			int[] results = getImgWidthHeight(srcfile);
			if (results == null || results[0] == 0 || results[1] == 0) {
				return "";
			} else {
				// 按比例缩放或扩大图片大小，将浮点型转为整型
				widthdist = (int) (results[0] * rate);
				heightdist = (int) (results[1] * rate);
			}
		}
		// 开始读取文件并进行压缩
		Image src = ImageIO.read(srcfile);

		// 构造一个类型为预定义图像类型之一的 BufferedImage
		BufferedImage tag = new BufferedImage((int) widthdist, (int) heightdist, BufferedImage.TYPE_INT_RGB);

		// 绘制图像 getScaledInstance表示创建此图像的缩放版本，返回一个新的缩放版本Image,按指定的width,height呈现图像
		// Image.SCALE_SMOOTH,选择图像平滑度比缩放速度具有更高优先级的图像缩放算法。
		tag.getGraphics().drawImage(src.getScaledInstance(widthdist, heightdist, Image.SCALE_SMOOTH), 0, 0, null);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		ImageIO.write(tag, "jpg", outputStream);
		String base64Img = Base64.encode(outputStream.toByteArray());
		res = "data:image/jpg;base64," + base64Img.toString();
		outputStream.close();
		// 创建文件输出流
		return res;
	}
	public static int[] getImgWidthHeight(File file) {
		InputStream is = null;
		BufferedImage src = null;
		int result[] = { 0, 0 };
		try {
			// 获得文件输入流
			is = new FileInputStream(file);
			// 从流里将图片写入缓冲图片区
			src = ImageIO.read(is);
			result[0] = src.getWidth(null); // 得到源图片宽
			result[1] = src.getHeight(null);// 得到源图片高
			is.close(); // 关闭输入流
		} catch (Exception ef) {
			ef.printStackTrace();
		}

		return result;
	}
}
