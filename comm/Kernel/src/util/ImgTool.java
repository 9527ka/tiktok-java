package util;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImgTool {
	//private BufferedImage subImg;

	/**
	 * 截图
	 * 
	 * @param srcPath
	 * @param startX
	 * @param startY
	 * @param width
	 * @param height
	 */
	/*public void cut(String srcPath, int startX, int startY, int width,
			int height) {
		try {
			BufferedImage bufImg = ImageIO.read(new File(srcPath));
			int w = bufImg.getWidth();
			int h = bufImg.getHeight();
			//如果宽比高大，并且宽大于400，裁减的坐标等比例放大
			if(w>=h && w>400){
			    startX = startX*w/400;
			    startY = startY*w/400;
			    width = width*w/400;
			    height = height*w/400;
			}
			//如果宽比高小，并且高大于400，裁减的坐标等比例放大
			if(w<h && h>400){
			    startX = startX*h/400;
                startY = startY*h/400;
                width = width*h/400;
                height = height*h/400;
			}
			subImg = bufImg.getSubimage(startX, startY, width, height);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	/**
	 * 保存截图。
	 * 
	 * @param bufImg
	 * @param imgType
	 * @param tarPath
	 */
	public void save(String imgType, String srcPath, String tarPath, int startX, int startY, int width, int height, int maxWidth) {
		try {
		    BufferedImage bufImg = ImageIO.read(new File(srcPath));
		    int oldWidth = bufImg.getWidth();
		    int oldHieght = bufImg.getHeight();
		    int w = bufImg.getWidth();
            int h = bufImg.getHeight();
            //如果宽比高大，并且宽大于maxWidth，裁减的坐标等比例放大
            if(w>=h && w>maxWidth){
                startX = startX*w/maxWidth;
                startY = startY*w/maxWidth;
                width = width*w/maxWidth;
                if(width>oldWidth){
                    width = oldWidth;
                }
                height = height*w/maxWidth;
                if(height>oldHieght){
                    height = oldHieght;
                }
            }
            //如果宽比高小，并且高大于maxWidth，裁减的坐标等比例放大
            if(w<h && h>maxWidth){
                startX = startX*h/maxWidth;
                startY = startY*h/maxWidth;
                width = width*h/maxWidth;
                if(width>oldWidth){
                    width = oldWidth;
                }
                height = height*h/maxWidth;
                if(height>oldHieght){
                    height = oldHieght;
                }
            }
            
            BufferedImage subImg = bufImg.getSubimage(startX, startY, width, height);
            
			File file = new File(tarPath);
			if(!file.getParentFile().exists()) {
				file.getParentFile().mkdirs();
			}
			ImageIO.write(subImg, imgType, file);
			
			//宽如果大于640，则按比例缩减
            if(subImg.getWidth()>640){
                double scale = (double)subImg.getWidth()/640;
                width = (int) (width/scale);
                height = (int) (height/scale);
                BufferedImage tempImg = new BufferedImage(width, height,
                        BufferedImage.TYPE_INT_RGB);
                tempImg.getGraphics().drawImage(
                        subImg.getScaledInstance(width, height,
                                Image.SCALE_FAST), 0, 0, null);
                ImageIO.write(tempImg, imgType,file);
            }
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
