/**
 * 查找图片地址
 */
package kernel.util;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import kernel.springframework.ServiceLocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * 
 * @author cwj
 *
 *
 */
public class ImageDispatcher {
	private final static Logger logger = LoggerFactory.getLogger(ImageDispatcher.class);

	private static boolean startUp;
	private static String saveDir;
	private static List viewDirs;	
	//查找缩略图 但是原图片不存在！
	private static Map<String, String> noFindImgZoom ;
	private static List noFindImgZoomNeed =new ArrayList();
	private static List noFindImgZoomSupply=new ArrayList() ;
	
	private static Properties properties = PropertiesLoaderUtils.loadProperties("config/system.properties");

	//查找图片， 但原图片不存在！ 
	private static Map<String, String> noFindImg;
	
	private static Map<String,String> noFindPerson;
	
	public static String getSaveDir() {
		return saveDir;
	}

	public static void setSaveDir(String saveDir) {
		ImageDispatcher.saveDir = saveDir;
		logger.info("[ImageDispatcher setSaveDir] saveDir:{}", saveDir);
	}

	public static boolean isStartUp() {
		return startUp;
	}

	public static void setStartUp(String startUp) {
		if ("true".equals(startUp)) {
			ImageDispatcher.startUp = true;
		} else {
			ImageDispatcher.startUp = false;
		}
	}

	public static List getViewDirs() {
		return viewDirs;
	}
	
	public static void setViewDirs(List viewDirs) {
		logger.info("[ImageDispatcher setViewDirs] viewDirs:{}", JsonUtils.bean2Json(viewDirs));
		ImageDispatcher.viewDirs = viewDirs;
	}
	
	/**
	 * 响应客户端请求的图片的过滤器处理过程
	 * @param request
	 * @param response
	 * @param filterChain
	 * @throws IOException
	 * @throws ServletException
	 */
	public static void doImageFilter(ServletRequest request, ServletResponse response, 
			FilterChain filterChain) throws IOException, ServletException {
		HttpServletRequest httpReq = (HttpServletRequest) request;
		HttpServletResponse httpResp = (HttpServletResponse) response;
		String reqStr = httpReq.getServletPath();
		FileInputStream imgStream = null;
		OutputStream toClientStream = null;

		try {
			File file = findFile(reqStr);
			if (file != null) {
				imgStream = new FileInputStream(file);
				int i = imgStream.available(); //得到文件大小
				httpResp.setContentType("image/*");	//设置文件类型
				httpResp.setContentLength(i);		//设置文件大小
				toClientStream = httpResp.getOutputStream();	//得到输出流
				byte data[] = new byte[1024];
				//逐步输出数据
				while( (i = imgStream.read(data)) > 0){
					toClientStream.write(data,0,i);
					toClientStream.flush();
				}
			} else {
				filterChain.doFilter(request, response);
			}
		} catch(IOException e) {
			throw e;
		} finally {
			if (toClientStream != null) {
				toClientStream.close();
			}			
			if (imgStream != null) {
				imgStream.close();
			}		
		}
	}

	/**
	 * 从数据库中得到图片地址	
	 * @param filePath : /img/2007/10/18/15/T3NHUG4MRO1D.jpg
	 * @return
	 */
	public static String getImg(String filePath) {
		String imgPath = "";
		if (!StringUtils.isNullOrEmpty(filePath) && filePath.indexOf("http://") != -1) {
			return filePath;
		}
		if (filePath == null) {
//			if (noFindImg == null || noFindImg.isEmpty()) {
//				noFindImg = new HashMap<String, String>();
//				noFindImg.put("1", "../sysImg/no-find-1.jpg");
//				noFindImg.put("2", "../sysImg/no-find-2.jpg");
//				noFindImg.put("3", "../sysImg/no-find-3.jpg");
//			}
//			imgPath = noFindImg.get(type);
//			return imgPath;
			return null;
		}
		
		
		File file = ImageDispatcher.findFile(filePath);
		if (file == null) {
			//原图片不存在！
			return null;
		} else {
			imgPath = ServiceLocator.getMessage("http.server.host") + filePath;
		}
		return imgPath;
	}
		
	
	/**
	 * 根据相对路径查找真实路径,如果不存在返回空
	 * @return 
	 */
	public static File findFile(String absPath) {
		File file = null;
		if (startUp && saveDir != null) {
			file = new File(saveDir + absPath);
			if (file.isFile()) {
				return file;
			} else if (viewDirs != null) {
				for (Iterator it = viewDirs.iterator(); it.hasNext(); ) {
					file = new File((String)it.next() + absPath);
					if (file.isFile()) {
						return file;
					}
				}
			}
		} else {
			file = new File(properties.getProperty("images.dir") + absPath);
			if (file.isFile()) {
				return file;
			}
		}
		return null;
	}

	public static Map<String, String> getNoFindImgZoom() {
		return noFindImgZoom;
	}
	public static void setNotFindImgZoom(Map<String, String> noFindImgZoom) {
		ImageDispatcher.noFindImgZoom = noFindImgZoom;
	}

	public static Map<String, String> getNoFindImg() {
		return noFindImg;
	}
	public static void setNoFindImg(Map<String, String> noFindImg) {
		ImageDispatcher.noFindImg = noFindImg;
	}	
	
	/**
	 * 删除一张原图
	 * @param filePath
	 */
	public static void delFile(String filePath){
		File file = findFile(filePath);
		if(file != null){
			file.delete();
		}
	}

	public static void main(String[] args) {
//		//System.out.println(ImageDispatcher.getNotImage());2007/10/18/15/T3NHUG4MRO1D.jpg
//		//System.out.println(ImageDispatcher.getImgZoom("", "2"));
//		//System.out.println(ImageDispatcher.getImg("", "2"));
		ImageDispatcher.delFile("2007/10/18/20/PT517L1U6841.jpg");
		
	}

	public static List getNoFindImgZoomNeed() {
		return noFindImgZoomNeed;
	}

	public static void setNoFindImgZoomNeed(List noFindImgZoomNeed) {
		ImageDispatcher.noFindImgZoomNeed = noFindImgZoomNeed;
	}

	public static List getNoFindImgZoomSupply() {
		return noFindImgZoomSupply;
	}

	public static void setNoFindImgZoomSupply(List noFindImgZoomSupply) {
		ImageDispatcher.noFindImgZoomSupply = noFindImgZoomSupply;
	}

}
