package kernel.web;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Properties;
import java.util.Random;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
// import org.apache.struts2.ServletActionContext;

import kernel.util.JsonUtils;
import kernel.util.PropertiesLoaderUtils;

public class UploadImgAction extends BaseAction {
	private static Log logger = LogFactory.getLog(UploadImgAction.class);
	private static final long serialVersionUID = 4590792756444167149L;
	private File file;
	private static Properties properties = PropertiesLoaderUtils.loadProperties("config/system.properties");

//	public String execute() throws IOException {
//		HttpServletResponse response = ServletActionContext.getResponse();
//		response.setContentType("application/json;charset=UTF-8");
//		response.setHeader("Access-Control-Allow-Origin", "*");
//		ResultObject resultObject = new ResultObject();
//		PrintWriter out = response.getWriter();
//
//		try {
//
//			String fileFileName = file.getName();
//
//			HashMap extMap = new HashMap();
//			extMap.put("image", "jpg,png");
//			if (this.file.length() / 1024L > 30720L) {
//				this.error = "图片大小不能超过30M";
//
//				resultObject.setCode("1");
//				resultObject.setMsg(error);
//				this.result = JsonUtils.getJsonString(resultObject);
//				out.println(this.result);
//				return null;
//			}
//			SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
//			String ymd = sdf.format(new Date());
//
//			String fileDir = properties.getProperty("images.dir");
//			File f = new File(fileDir + "/" + ymd);
//			if ((!f.exists()) && (!f.mkdirs())) {
//				this.error = ("文件:" + fileDir + "创建失败!");
//				resultObject.setCode("1");
//				resultObject.setMsg("服务器错误");
//				logger.warn(error);
//				this.result = JsonUtils.getJsonString(resultObject);
//				out.println(this.result);
//				return null;
//			}
//
////			String fileExt = fileFileName.substring(fileFileName.lastIndexOf(".") + 1).toLowerCase();
////			if (!Arrays.asList(((String) extMap.get("image")).split(",")).contains(fileExt)) {
//////				this.error = ("上传图片是不允许的扩展名。\n只允许" + (String) extMap.get("image") + "格式。");
//////				resultObject.setCode("1");
//////				resultObject.setMsg(error);
//////				this.result = JsonUtils.getJsonString(resultObject);
//////				out.println(this.result);
//////				return null;
////			}
//
//			String imagePath = "";
//			SimpleDateFormat sdf2 = new SimpleDateFormat("yyyyMMddHHmmss");
//			String yms = sdf2.format(new Date());
//
//			String imageDir = ymd + "/" + yms + new Random().nextInt(10000000) + "." + "png";
//			imagePath = fileDir + imageDir.toLowerCase().trim();
//
//			FileInputStream in = new FileInputStream(this.file);
//
//			FileOutputStream outputStream = new FileOutputStream(imagePath);
//
//			BufferedInputStream inputStream = new BufferedInputStream(in);
//			byte[] buf = new byte[1024];
//			int length = 0;
//			while ((length = inputStream.read(buf)) != -1) {
//				outputStream.write(buf, 0, length);
//			}
//			resultObject.setData(imageDir);
//		} catch (FileNotFoundException e) {
//			this.error = "文件上传失败";
//			resultObject.setCode("1");
//			resultObject.setMsg("服务器错误");
//			logger.error(error, e);
//			this.result = JsonUtils.getJsonString(resultObject);
//			out.println(this.result);
//			return null;
//		} catch (Exception e) {
//			this.error = "文件上传失败";
//
//			resultObject.setCode("1");
//			resultObject.setMsg("服务器错误");
//			logger.error(error, e);
//			this.result = JsonUtils.getJsonString(resultObject);
//			out.println(this.result);
//			return null;
//
//		}
//
//		this.result = JsonUtils.getJsonString(resultObject);
//		out.println(this.result);
//		return null;
//	}

	public void setFile(File file) {
		this.file = file;
	}

}
