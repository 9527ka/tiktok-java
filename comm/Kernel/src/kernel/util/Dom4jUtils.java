package kernel.util;



import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.QName;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.SAXValidator;
import org.dom4j.io.XMLWriter;
import org.dom4j.util.XMLErrorHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Dom4j 的一个工具类实现

 * @author 
 * @version 1.0 changed.log  添加一些增删改属性的静态方法
 */
public abstract class Dom4jUtils {

    public static final String DEFAULT_ENCODING = "GB2312";


    public static void validateDocument(Document document, final String xsdName, final InputStream xsdStream)
            throws SAXException, ParserConfigurationException {
        // 读取mib-mapping验证文件
        EntityResolver resolver = new EntityResolver() {
            public InputSource resolveEntity(String publicId, String systemId) {
                if (systemId.endsWith(xsdName)) {
                    return new InputSource(xsdStream);
                }
                return null;
            }
        };

        // 创建默认的XML错误处理器
        XMLErrorHandler errorHandler = new XMLErrorHandler();

        // 获取基于 SAX 的解析器的实例
        SAXParserFactory factory = SAXParserFactory.newInstance();
        // 解析器在解析时验证 XML 内容。
        factory.setValidating(true);
        // 指定由此代码生成的解析器将提供对 XML 名称空间的支持。
        factory.setNamespaceAware(true);
        // 使用当前配置的工厂参数创建 SAXParser 的一个新实例。
        XMLReader parser = factory.newSAXParser().getXMLReader();
        parser.setEntityResolver(resolver);
        parser.setFeature("http://xml.org/sax/features/validation", true);
        parser.setFeature("http://apache.org/xml/features/validation/schema", true);
        parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);

        // 创建一个SAXValidator校验工具，并设置校验工具的属性
        SAXValidator validator = new SAXValidator(parser);
        // 设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。
        validator.setErrorHandler(errorHandler);
        // 校验
        validator.validate(document);

        // 如果错误信息不为空，说明校验失败，打印错误信息
        if (errorHandler.getErrors().hasContent()) {
            throw new SAXException(errorHandler.getErrors().asXML());
        }
    }

//    public static void validateDocument(Document document) throws SAXException, ParserConfigurationException {
//        
//
//        // 创建默认的XML错误处理器
//        XMLErrorHandler errorHandler = new XMLErrorHandler();
//
//        // 获取基于 SAX 的解析器的实例
//        SAXParserFactory factory = SAXParserFactory.newInstance();
//        // 解析器在解析时验证 XML 内容。
//        factory.setValidating(true);
//        // 指定由此代码生成的解析器将提供对 XML 名称空间的支持。
//        factory.setNamespaceAware(true);
//        // 使用当前配置的工厂参数创建 SAXParser 的一个新实例。
//        XMLReader parser = factory.newSAXParser().getXMLReader();
//        parser.setEntityResolver(resolver);
//        parser.setFeature("http://xml.org/sax/features/validation", true);
//        parser.setFeature("http://apache.org/xml/features/validation/schema", true);
//        parser.setFeature("http://apache.org/xml/features/validation/schema-full-checking", true);
//
//        // 创建一个SAXValidator校验工具，并设置校验工具的属性
//        SAXValidator validator = new SAXValidator(parser);
//        // 设置校验工具的错误处理器，当发生错误时，可以从处理器对象中得到错误信息。
//        validator.setErrorHandler(errorHandler);
//        // 校验
//        validator.validate(document);
//
//        // 如果错误信息不为空，说明校验失败，打印错误信息
//        if (errorHandler.getErrors().hasContent()) {
//            throw new SAXException(errorHandler.getErrors().asXML());
//        }
//    }

    /**
     * Create dom4j document from xmlSource
     * 
     * @param xmlSource
     *            Object
     * @param validate
     *            boolean
     * @param encoding
     *            String
     * @throws DocumentException
     * @throws IOException
     * @return Document
     */
    public static Document createDOM4jDocument(Object xmlSource, boolean validate, String encoding)
            throws DocumentException, IOException {
        // Use xerces and validate XML file
        Document doc = null;
        SAXReader saxReader = new SAXReader(true);
        saxReader.setValidation(validate);
        if (encoding == null || encoding.equals("")) {
            encoding = DEFAULT_ENCODING;
        }

        // Check input source type
        if (xmlSource instanceof String) {
            if (((String) xmlSource).startsWith("<")) {
                // Treat the String as XML code
                StringReader reader = new StringReader(xmlSource.toString());
                doc = saxReader.read(reader, encoding);
            }
            else {
                doc = saxReader.read(new File((String) xmlSource));
            }
        }
        else if (xmlSource instanceof File) {
            doc = saxReader.read((File) xmlSource);
        }
        else if (xmlSource instanceof InputStream) {
            doc = saxReader.read((InputStream) xmlSource);
        }
        else if (xmlSource instanceof Reader) {
            doc = saxReader.read((Reader) xmlSource);
        }
        else if (xmlSource instanceof URL) {
            doc = saxReader.read((URL) xmlSource);
        }
        else if (xmlSource instanceof Document) { // 080908 added
            doc = (Document) xmlSource;
        }

        return doc;
    }

    /**
     * 新建文档
     * 
     * @return Document 文档节点
     * @added by jiayc
     */
    public static Document createDocument() {
        DocumentFactory factory = new DocumentFactory();
        Document document = factory.createDocument();
        return document;
    }

    /**
     * This method will generate XML file in a StringBuffer based on the given
     * Dom4j object.
     * 
     * @param dom4jObj
     *            Object
     * @param encoding
     *            String
     * @throws IOException
     * @return StringBuffer
     */
    public static/* synchronized closed by cxy */StringBuffer generateXMLStringBuffer(Object dom4jObj, String encoding)
            throws IOException {
        StringWriter writer = new StringWriter();
        OutputFormat outformat = OutputFormat.createPrettyPrint();
        // 设置编码类型
        if (encoding == null || encoding.trim().equals("")) {
            encoding = DEFAULT_ENCODING;
        }
        outformat.setEncoding(encoding);
        // dom4j 支持直接输出OBJECT
        XMLWriter xmlWriter = null;
        xmlWriter = new XMLWriter(writer, outformat);
        xmlWriter.write(dom4jObj);
        xmlWriter.flush();

        return writer.getBuffer();
    }

    /**
     * 反射调用DOM4J的某方法，生成BUFFER，不是很有用？只能调一层？
     * 
     * @param dom4jObj
     *            Object
     * @param encoding
     *            String
     * @param className
     *            String
     * @param methodName
     *            String
     * @param parameterTypes
     *            Class[]
     * @param parameterValues
     *            Object[]
     * @throws IOException
     * @throws ClassNotFoundException
     * @throws SecurityException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @return StringBuffer
     */
    public static/** synchronized */
    StringBuffer generateXMLStringBufferByMethodInvoke(Object dom4jObj, String encoding, String className,
            String methodName, Class<?>[] parameterTypes, Object[] parameterValues) throws IOException,
            ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            IllegalAccessException, InvocationTargetException {
        Class<?> componentClass = null;
        componentClass = Class.forName(className);
        Method method;
        method = componentClass.getMethod(methodName, parameterTypes);
        Object o = method.invoke(dom4jObj, parameterValues);
        return generateXMLStringBuffer(o, encoding);
    }

    /**
     * 插入节点，不是好方法，可有好方法替代？
     * 
     * @param parent
     *            Element param insert
     * @param insertBefore
     *            Element
     * @return Element
     */
    public static Element insertChild(Element parent, Element toInsert, Element insertBefore) {
        String str = parent.asXML();
        String before = insertBefore.asXML();
        String insert = toInsert.asXML();
        int pos = -1;
        if ((pos = str.indexOf(before)) >= 0) {
            str = str.substring(0, pos) + insert + str.substring(pos);
        }

        try {
            parent = Dom4jUtils.createDOM4jDocument(str, false, "").getRootElement();
        } catch (Exception ex) {
        }

        return parent;
    }

    /**
     * 插入节点，不是好方法，可有好方法替代？
     * 
     * @param parent
     *            Element param insert
     * @param insertBefore
     *            Element
     * @return Element
     */
    public static Element insertChildAfter(Element parent, Element toInsert, Element insertAfter) {
        String str = parent.asXML();
        String after = insertAfter.asXML();
        String insert = toInsert.asXML();
        int pos = -1;
        if ((pos = str.indexOf(after)) >= 0) {
            pos += after.length();
            str = str.substring(0, pos) + insert + str.substring(pos);
        }

        try {
            parent = Dom4jUtils.createDOM4jDocument(str, false, "").getRootElement();
        } catch (Exception ex) {
        }

        return parent;
    }

    /**
     * 添加新Element节点，值为String类型
     * 
     * @param parent
     *            父节点
     * @param name
     *            新节点名称
     * @param value
     *            新节点值
     * @return element
     * @throws XMLDocException
     */
    public static Element appendChild(Element parent, String name, String value) {
        if (parent == null) {
            return null;
        }

        Element element = parent.addElement(new QName(name, parent.getNamespace()));
        if (value != null) {
            element.addText(value);
        }
        return element;
    }

    /**
     * 删除子节点
     * 
     * @param parent
     *            Element 父节点
     * @param name
     *            String 删除节点名
     * @param value
     *            String 删除节点值
     * @return Element
     */
    public static Element removeChild(Element parent, String name, String value) {
        if (parent == null) {
            return null;
        }

        Iterator<?> iter = parent.elementIterator();
        Element del = null;
        while (iter.hasNext()) {
            Element tmp = (Element) iter.next();
            if (tmp.getName().equals(name) && tmp.getText().equals(value)) {
                del = tmp;
            }
        }
        if (del != null) {
            parent.remove(del);
        }
        return parent;
    }

    /**
     * 添加属性
     * 
     * @param ele
     *            Element
     * @param attributeName
     *            String
     * @param attributeValue
     *            String
     * @return Element
     */
    public static Element appendAttribute(Element ele, String attributeName, String attributeValue) {
        if (ele == null) {
            return null;
        }

        ele.addAttribute(attributeName, attributeValue);
        return ele;
    }

    /**
     * 删除属性
     * 
     * @param ele
     *            Element
     * @param attributeName
     *            String
     * @return Element
     */
    public static Element removeAttribute(Element ele, String attributeName) {
        if (ele == null) {
            return null;
        }

        Attribute att = ele.attribute(attributeName);
        ele.remove(att);
        return ele;
    }

    /**
     * 修改属性
     * 
     * @param ele
     *            Element
     * @param attributeName
     *            String
     * @param attributeValue
     *            String
     * @return Element
     */
    public static Element setAttribute(Element ele, String attributeName, String attributeValue) {
        if (ele == null) {
            return null;
        }

        Attribute att = ele.attribute(attributeName);
        if (att != null) {
            att.setText(attributeValue);
        }
        else {
            Dom4jUtils.appendAttribute(ele, attributeName, attributeValue);
        }
        return ele;
    }

    /**
     * 取Element节点下的所有子节点内容，转换成字符串
     * 
     * @param element
     *            Element
     * @return String
     */
    public static String getElementContext(Element element) {
        if (element == null) {
            return null;
        }
        String str = element.getText();
        for (Iterator<?> i = element.elementIterator(); i.hasNext();) {
            Element elementTmp = (Element) i.next();
            str = str + elementTmp.asXML();
            // do something
        }
        return str;
    }

    /**
     * 
     * @param element
     *            Element
     * @param path
     *            String
     * @param attr
     *            String
     * @return String
     */
    public static String getElementContext(Element element, String path, String attr) {
        Element el = element.element(path);
        if (attr == null || attr.trim().equals("")) {
            return el.getText();
        }
        else {
            return el.attributeValue(attr);
        }
    }

    /**
     * 根据XPATH 获取元素内容，text。 "/path/@seq" 获取属性值 "/path/" 获取元素
     * 
     * @param element
     *            Element
     * @param path
     *            String
     * @return String
     */
    public static String getElementContext(Element element, String path) {
        if (element == null || path == null) {
            return null;
        }

        Object o = element.selectSingleNode(path);
        if (o == null) { // 无此节点
            return null;
        }

        if (o instanceof Element) { // 1、元素 Element
            Element e = (Element) o;
            if (e.isTextOnly()) { // text only
                return e.getText();
            }
            else { // element
                try {
                    return Dom4jUtils.generateXMLStringBuffer(e, "").toString();
                } catch (IOException ex1) {
                    return null;
                }
            }
        }
        else if (o instanceof Attribute) { // 2、属性 Attribute
            return ((Attribute) o).getValue();
        }
        else { // 3、其他 Other node
            try {
                return Dom4jUtils.generateXMLStringBuffer(o, "").toString();
            } catch (IOException ex) {
                return null;
            }
        }
    }

    /**
     * 根据XPATH 获取元素内容，text。 "/path/@seq" 获取属性值 "/path/" 获取元素
     * 
     * @param element
     *            Element
     * @param path
     *            String
     * @return String
     */
    public static String[] getElementContextArray(Element element, String path) {
        if (element == null || path == null) {
            return null;
        }
        List<?> nodes = element.selectNodes(path);
        String[] eleContext = new String[nodes.size()];
        Iterator<?> iter = nodes.iterator();
        int length = 0;
        while (iter.hasNext()) {
            Object o = (Object) iter.next();
            // Object o = element.selectNodes(path);
            if (o instanceof Element) { // 1、元素 Element
                Element e = (Element) o;
                if (e.isTextOnly()) { // text only
                    // return e.getText();
                    eleContext[length] = e.getText();
                    length++;
                }
                else { // element
                    try {
                        eleContext[length] = Dom4jUtils.generateXMLStringBuffer(e, "").toString();
                        length++;
                        // return Dom4jUtil.generateXMLStringBuffer(e,
                        // "").toString();
                    } catch (IOException ex1) {
                        return null;
                    }
                }
            }
            else if (o instanceof Attribute) { // 2、属性 Attribute
                // return ( (Attribute) o).getValue();
                eleContext[length] = ((Attribute) o).getValue();
                length++;
            }
            else { // 3、其他 Other node
                try {
                    eleContext[length] = Dom4jUtils.generateXMLStringBuffer(o, "").toString();
                    length++;
                    // return Dom4jUtil.generateXMLStringBuffer(o,
                    // "").toString();
                } catch (IOException ex) {
                    return null;
                }
            }
        }
        return eleContext;
    }

    /**
     * 添加新Element节点，值为String类型
     * 
     * @param root
     *            根节点
     * @param xpath
     *            新节点路径 eg:/root/a/a1 or /root/b/b1/@seq
     * @param value
     *            新节点值
     * @param isGroup
     *            boolean
     * @return element
     */
    public static Element appendElement(Element root, String xpath, String value, boolean isGroup) {
        if (root == null) {
            return null;
        }
        int j = xpath.lastIndexOf("/");
        String path = xpath.substring(0, j);
        Element node = (Element) root.selectSingleNode(path);
        String[] paths = xpath.split("/");
        if (isGroup) {
            if (node == null) {
                for (int i = 2; i < paths.length; i++) {
                    root = root.addElement(new QName(paths[i], root.getNamespace()));
                }
            }
            else {
                root = node.addElement(new QName(paths[paths.length - 1], root.getNamespace()));
            }
        }
        else {
            for (int i = 2; i < paths.length; i++) {
                root = root.addElement(new QName(paths[i], root.getNamespace()));
            }
        }

        if (value != null) {
            root.addText(value);

        }

        return root;
    }

    /**
     * 
     * @param args
     *            String[]
     */
    public static void main(String[] args) throws IOException {
        Document document = Dom4jUtils.createDocument();
        Element root = document.addElement(new QName("root"));

        Dom4jUtils.appendChild(root, "hi", "hi");
        Dom4jUtils.appendChild(root, "hi1", "1");
        Dom4jUtils.appendChild(root, "hi2", "2");

        root.addElement("kaka");

        root = Dom4jUtils.insertChild(root, (Element) root.selectSingleNode("hi1"),
                (Element) root.selectSingleNode("hi"));
        root = Dom4jUtils.insertChildAfter(root, (Element) root.selectSingleNode("hi2"),
                (Element) root.selectSingleNode("hi"));

        System.out.println(Dom4jUtils.generateXMLStringBuffer(root, ""));
    }
}
