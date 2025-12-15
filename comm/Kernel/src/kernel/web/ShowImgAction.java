package kernel.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.http.HttpServletResponse;

// import org.apache.struts2.ServletActionContext;

import kernel.util.ImageDispatcher;
import kernel.util.PropertiesLoaderUtils;

public class ShowImgAction extends BaseAction {
	private static final long serialVersionUID = 7683481061134646641L;
	private static Properties properties = PropertiesLoaderUtils.loadProperties("config/system.properties");

	protected String imagePath = null;

//	public String showImg() {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		response.setHeader("Access-Control-Allow-Headers", "content-type, x-requested-with");
//		response.setHeader("Access-Control-Allow-Credentials", "true");
//		String strForward = "success";
//		return strForward;
//	}
//
//	public String view() {
//		return "view";
//	}
//
//	public InputStream getDownloadFile() throws FileNotFoundException {
//		BufferedInputStream bis = null;
//		try {
//			boolean goback = false;
//			File fl = null;
//			if ((this.imagePath == null) || (this.imagePath.trim().length() <= 0)) {
//				fl = new File(properties.getProperty("images.dir") + "noimage.jpg");
//				goback = true;
//			}
//			if (!goback) {
//				fl = ImageDispatcher.findFile(this.imagePath);
//				if (fl == null) {
//					fl = new File(properties.getProperty("images.dir") + "noimage.jpg");
//				}
//				if (!fl.exists()) {
//					fl = new File(properties.getProperty("images.dir") + "noimage.jpg");
//				}
//
//			}
//
//			FileInputStream fis = new FileInputStream(fl);
//			bis = new BufferedInputStream(fis);
//		} catch (Throwable localThrowable) {
//		}
//
//		return bis;
//	}

	public String getImagePath() {
		return this.imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
}
