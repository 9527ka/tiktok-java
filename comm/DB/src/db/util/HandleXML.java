package db.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class HandleXML {

    /**	
     * logger
     */
	private static Logger logger = LogManager.getLogger(HandleXML.class); 

    /**	
     * <p>Description: XML Document 写入文件  </p>
     * <p>Create Time:   </p>
     * @param document XML Document
     * @param filePath 文件路径
     * @throws Exception 异常
     */
    public static void writeToXML(Document document, String filePath) throws Exception {
        OutputStream os = null;
        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            OutputFormat format = OutputFormat.createPrettyPrint();
            format.setEncoding("UTF-8"); // 设置XML文件的编码格式
            os = new FileOutputStream(filePath);
            XMLWriter xmlout = new XMLWriter(os, format);
            xmlout.write(document);
            xmlout.close();
        } catch (Exception e) {
        	logger.error("write to xml [" + filePath + "] error", e);
            throw new Exception(e);
        } finally {
            IOUtil.closeQuietly(os);
        }
    }

    /**	
     * <p>Description: Get XML Document  </p>
     * <p>Create Time: 2013-2-6   </p>
     * @param fis      文件输入流
     * @param rootName 根节点名称
     * @return XML Document
     */
    public static Document getDocument(FileInputStream fis, String rootName) {
        Document document = null;
        try {
            SAXReader saxReader = new SAXReader();
            document = saxReader.read(fis);
        } catch (DocumentException e) {
            document = DocumentHelper.createDocument();
            document.addElement(rootName); // root
        }

        return document;
    }

    /**	
     * <p>Description: 删除XML Document查找到的第一条记录    </p>
     * <p>Create Time: 2013-2-6   </p>
     * @author weiminghua
     * @param xpathExpression 匹配正则
     * @param document XML Document 
     * @return  删除结果
     */
    public static boolean deleteFirstNode(String xpathExpression, Document document) {
        List<?> list = document.selectNodes(xpathExpression);
        if (list.size() > 0) {
            Element e = (Element) list.get(0);
            return e.getParent().remove(e);
        }
        return false;
    }

}
