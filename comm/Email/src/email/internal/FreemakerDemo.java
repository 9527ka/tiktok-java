package email.internal;

import freemarker.cache.TemplateLoader;
import freemarker.core.BugException;
import freemarker.core.TextBlock;
import freemarker.core._CoreAPI;
import freemarker.debug.impl.DebuggerService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateExceptionHandler;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FreemakerDemo {

    public static void main(String[] args) throws Exception {
        String tplContent = "listvalue:<br/><#list list as v>${v} - ${v_index} [${v_has_next?string(\"y\",\"n\")}]</#list>";
        String tplFileName = "none.ftl";
        // this.freeMarkerConfigurer.getConfiguration()
        Configuration cfg = new Configuration(); //Freemarker的起始类,要使用Freemarker功能必须通过该类
        cfg.setDirectoryForTemplateLoading(new File("D:/tmp"));//freemarker从什么地方加载模板文件
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.IGNORE_HANDLER);//忽略异常

        TemplateLoader templateLoader = cfg.getTemplateLoader();
        final StringWriter sw = new StringWriter();
        final char[] buf = new char[4096];
        try (Reader reader = templateLoader.getReader(new File("D:/tmp/none.ftl"), "UTF-8")) {
            fetchChars:
            while (true) {
                int charsRead = reader.read(buf);
                if (charsRead > 0) {
                    sw.write(buf, 0, charsRead);
                } else if (charsRead < 0) {
                    break fetchChars;
                }
            }
        }
        tplContent = sw.toString();

        //Template template = Template.getPlainTextTemplate(tplFileName, tplContent, cfg);
        Template template;
        try {
            template = new Template(tplFileName, tplFileName, new StringReader(tplContent), cfg);
            template.setEncoding("UTF-8");
        } catch (IOException e) {
            throw new BugException("Plain text template creation failed", e);
        }
        //Template template = cfg.getTemplate(tplFileName, "UTF-8");

        Map<String, List<String>> dataMap = new HashMap<String, List<String>>();
        List<String> list = new ArrayList<String>();
        for (int i = 0; i < 10; i++) {
            list.add("listvalue" + i);
        }
        dataMap.put("list", list);

        FreemakerBizDataDemo data = new FreemakerBizDataDemo();
        data.setList(list);

        StringWriter out = new StringWriter(1024);
//        template.process(dataMap, out);
        template.process(new Object(), out);
        String finishedContent = out.toString();
        System.out.println("==========> finishedContent:" + finishedContent);
    }
}
