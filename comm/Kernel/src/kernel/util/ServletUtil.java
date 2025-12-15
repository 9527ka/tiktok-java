package kernel.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class ServletUtil {

	
	public static final String ENCODING = "UTF-8";
	

	
	/**
	 * 输出XML信息
	 * 
	 * @param response
	 * @param xmlStr
	 * @throws IOException
	 */
	public static void outputXML(HttpServletResponse response, String xmlStr)
			throws IOException {
		response.setContentType("text/html;charset=" + ENCODING);
		PrintWriter out = response.getWriter();
		out.println(xmlStr);
		out.close();		
	}
	
	
}
