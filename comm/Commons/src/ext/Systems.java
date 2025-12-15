package ext;




import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 自定义标准库扩展
 */
public class Systems {
    /**
     * 编码器
     */
    private static final boolean java9OrLater = detectIsJava9OrLater();
    /**
     * 类型解析器
     */
    private static int devFlag = -1;
    // 是否已解析环境
    private static boolean resolved = false;

    private static Boolean detectIsJava9OrLater() {
        String version = System.getProperty("java.version");
        if (Pattern.compile("^\\d+$").matcher(version).find()) {
            return TypeConv.toInt(version) >= 9;
        }
        return false;
    }

    public static boolean classInJar(Class<?> c) {
        // var className= Thread.currentThread().stackTrace[1].className;
        // val c = Class.forName(className)
        // var c = Typed::class.java

        // note: 将JDK定为1.8
        // val pkg = if (isJava9OrLater()) c.packageName else c.`package`.name
        String pkg = c.getPackage().getName();
        String resName = pkg.replace(".", "/");
        URL pkgPath = c.getClassLoader().getResource(resName);
        String path = pkgPath != null ? pkgPath.getPath() : "";
        return path.contains(".jar!")
                || path.contains(".war!");
    }

    /**
     * 解析环境,如果是生产环境返回true,反之返回false
     */
    public static void resolveEnvironment(Class<?> main) {
        if(resolved) {
            return;
        }
        devFlag = classInJar(main) ? 0 : 1;
        if (dev()) {
            // 在IDEA下开发时设置项目真实的工作空间
            String workspace = System.getProperty("user.dir");
            // Windows下以"\"分隔
            List<String> sep = new ArrayList<>();
            sep.add("/build");
            sep.add("/target");
            sep.add("\\build");
            sep.add("\\target");
            int i = Strings.indexOfAny(workspace, sep);
            if (i != -1) {
                System.setProperty("user.dir", workspace.substring(0, i));
            }
        }
        resolved = true;
    }

    /**
     * 是否为开发环境
     */
    public static boolean dev() {
        if (devFlag == -1) {
            throw new Error("should call method Standard.resolveEnvironment first");
        }
        return devFlag == 1;
    }



    public static Type getActualType(Object o, int index) {
        Type clazz = o.getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) clazz;
        return pt.getActualTypeArguments()[index];
    }



    /**
     * 是否当前运行单元测试
     *
     * @return 是或否
     */
    public static boolean isUnitTesting() {
        Hashtable<Object, Object> properties = System.getProperties();
        String classPath = properties.get("java.class.path").toString();
        if (!classPath.contains("java/test")) {
          //  return false;
        }
        for (Map.Entry<Object, Object> k : properties.entrySet()) {
            if (k.getKey().toString().endsWith("java.command")) {
                String command = k.getValue().toString().toLowerCase();
                if (command.contains("test")) {
                    return true;
                }
                break;
            }
        }
        return false;
    }

    /**
     * 获取版本号数值
     * @param version 版本号
     * @return 数字版本号
     */
    public static int getVersionCode(String version){
       String[] arr = version.split("\\.");
       for(int i=0;i< arr.length;i++){
           int l = arr[i].length();
           if(l < 3) {
               arr[i] = Strings.repeat("0", 3 - l) + arr[i];
           }
       }
       return TypeConv.toInt(String.join("",arr));
    }


}
