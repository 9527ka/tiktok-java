package util;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.CropImageFilter;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageCut {

	/**
	 * 
	 * 缩放图像
	 * 
	 * @param srcImageFile源图像文件地址
	 * 
	 * @param result缩放后的图像地址
	 * 
	 * @param scale缩放比例
	 * 
	 * @param flag缩放选择
	 *            :true 放大; false 缩小;
	 */

	public static void scale(String srcImageFile, String result, String type,double scale,

	boolean flag) {

		try {

			BufferedImage src = ImageIO.read(new File(srcImageFile)); // 读入文件

			int width = src.getWidth(); // 得到源图宽

			int height = src.getHeight(); // 得到源图长

			if (flag) {// 放大

				width = Double.valueOf((width * scale)).intValue();

				height = Double.valueOf((height * scale)).intValue();

			} else {// 缩小

				width = Double.valueOf((width / scale)).intValue();

				height = Double.valueOf((height / scale)).intValue();

			}

			Image image = src.getScaledInstance(width, height,

			Image.SCALE_DEFAULT);

			BufferedImage tag = new BufferedImage(width, height,

			BufferedImage.TYPE_INT_RGB);

			Graphics g = tag.getGraphics();

			g.drawImage(image, 0, 0, null); // 绘制缩小后的图

			g.dispose();

			ImageIO.write(tag, type, new File(result));// 输出到文件流

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	/**
	 * 
	 * 图像切割
	 * 
	 * @param srcImageFile源图像地址
	 * 
	 * @param descDir切片目标文件夹
	 * 
	 * @param destWidth目标切片宽度
	 * 
	 * @param destHeight目标切片高度
	 */

	public static void cut(String srcImageFile, String descDir, int destWidth,

	int destHeight) {

		try {

			Image img;

			ImageFilter cropFilter; // 读取源图像

			BufferedImage bi = ImageIO.read(new File(srcImageFile));

			int srcWidth = bi.getHeight(); // 源图宽度

			int srcHeight = bi.getWidth(); // 源图高度

			if (srcWidth > destWidth && srcHeight > destHeight) {

				Image image = bi.getScaledInstance(srcWidth, srcHeight,

				Image.SCALE_DEFAULT);

				destWidth = 200; // 切片宽度

				destHeight = 150; // 切片高度

				int cols = 0; // 切片横向数量

				int rows = 0; // 切片纵向数量

				// 计算切片的横向和纵向数量

				if (srcWidth % destWidth == 0) {

					cols = srcWidth / destWidth;

				} else {

					cols = (int) Math.floor(srcWidth / destWidth) + 1;

				}

				if (srcHeight % destHeight == 0) {

					rows = srcHeight / destHeight;

				} else {

					rows = (int) Math.floor(srcHeight / destHeight) + 1;

				}

				// 循环建立切片

				// 改进的想法:是否可用多线程加快切割速度

				for (int i = 0; i < rows; i++) {

					for (int j = 0; j < cols; j++) {

						// 四个参数分别为图像起点坐标和宽高

						// 即: CropImageFilter(int x,int y,int width,int height)

						cropFilter = new CropImageFilter(j * 200, i * 150,

						destWidth, destHeight);

						img = Toolkit.getDefaultToolkit().createImage(

						new FilteredImageSource(image.getSource(),

						cropFilter));

						BufferedImage tag = new BufferedImage(destWidth,

						destHeight, BufferedImage.TYPE_INT_RGB);

						Graphics g = tag.getGraphics();

						g.drawImage(img, 0, 0, null); // 绘制缩小后的图

						g.dispose();

						// 输出为文件

						ImageIO.write(tag, "JPEG", new File(descDir

						+ "pre_map_" + i + "_" + j + ".jpg"));

					}

				}

			}

		} catch (Exception e) {

			e.printStackTrace();

		}

	}

	// 图像类型转换GIF->JPG GIF->PNG PNG->JPG PNG->GIF(X)

	public static void convert(String source, String result) {

		try {

			File f = new File(source);

			f.canRead();

			f.canWrite();

			BufferedImage src = ImageIO.read(f);

			ImageIO.write(src, "JPG", new File(result));

		} catch (Exception e) {

			// TODO Auto-generated catch block

			e.printStackTrace();

		}

	}

	// 彩色转为黑白

	public static void gray(String source, String result) {

		try {

			BufferedImage src = ImageIO.read(new File(source));

			ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_GRAY);

			ColorConvertOp op = new ColorConvertOp(cs, null);

			src = op.filter(src, null);

			ImageIO.write(src, "JPEG", new File(result));

		} catch (IOException e) {

			e.printStackTrace();

		}

	}

	public static void main(String[] args) {
		double s = 1024;
		int e = 500;
		double scale = s/e;
         String src = "E:\\apache-tomcat-6.0.37\\wtpwebapps\\mcp0917\\upload\\images\\2c9287a341ca74320141ca76181e0001\\5453797376338493.jpg";
		 scale(src,"E:\\apache-tomcat-6.0.37\\wtpwebapps\\mcp0917\\upload\\images\\2c9287a341ca74320141ca76181e0001\\5453797376338493.jpg","jpg",scale,false);

	}

}