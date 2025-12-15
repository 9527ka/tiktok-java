package kernel.util;

import cn.hutool.core.util.StrUtil;
import lombok.experimental.UtilityClass;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.SynthesizingMethodParameter;
import org.springframework.web.method.HandlerMethod;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * 类工具类
 *
 * @author L.cm
 */
@UtilityClass
public class ClassTools extends org.springframework.util.ClassUtils {
	private final static Logger logger = LogManager.getLogger(ClassTools.class);

	// 缓存起来，提升效率，field：完整类名#属性名
	private final static ConcurrentHashMap<String, Field> cacheMap = new ConcurrentHashMap();

	private final ParameterNameDiscoverer PARAMETERNAMEDISCOVERER = new DefaultParameterNameDiscoverer();

	/**
	 * 获取方法参数信息
	 * @param constructor 构造器
	 * @param parameterIndex 参数序号
	 * @return {MethodParameter}
	 */
	public MethodParameter getMethodParameter(Constructor<?> constructor, int parameterIndex) {
		MethodParameter methodParameter = new SynthesizingMethodParameter(constructor, parameterIndex);
		methodParameter.initParameterNameDiscovery(PARAMETERNAMEDISCOVERER);
		return methodParameter;
	}

	/**
	 * 获取方法参数信息
	 * @param method 方法
	 * @param parameterIndex 参数序号
	 * @return {MethodParameter}
	 */
	public MethodParameter getMethodParameter(Method method, int parameterIndex) {
		MethodParameter methodParameter = new SynthesizingMethodParameter(method, parameterIndex);
		methodParameter.initParameterNameDiscovery(PARAMETERNAMEDISCOVERER);
		return methodParameter;
	}

	/**
	 * 获取Annotation
	 * @param method Method
	 * @param annotationType 注解类
	 * @param <A> 泛型标记
	 * @return {Annotation}
	 */
	public <A extends Annotation> A getAnnotation(Method method, Class<A> annotationType) {
		Class<?> targetClass = method.getDeclaringClass();
		// The method may be on an interface, but we need attributes from the target
		// class.
		// If the target class is null, the method will be unchanged.
		Method specificMethod = getMostSpecificMethod(method, targetClass);
		// If we are dealing with method with generic parameters, find the original
		// method.
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);
		// 先找方法，再找方法上的类
		A annotation = AnnotatedElementUtils.findMergedAnnotation(specificMethod, annotationType);

		if (null != annotation) {
			return annotation;
		}
		// 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
		return AnnotatedElementUtils.findMergedAnnotation(specificMethod.getDeclaringClass(), annotationType);
	}

	/**
	 * 获取Annotation
	 * @param handlerMethod HandlerMethod
	 * @param annotationType 注解类
	 * @param <A> 泛型标记
	 * @return {Annotation}
	 */
	public <A extends Annotation> A getAnnotation(HandlerMethod handlerMethod, Class<A> annotationType) {
		// 先找方法，再找方法上的类
		A annotation = handlerMethod.getMethodAnnotation(annotationType);
		if (null != annotation) {
			return annotation;
		}
		// 获取类上面的Annotation，可能包含组合注解，故采用spring的工具类
		Class<?> beanType = handlerMethod.getBeanType();
		return AnnotatedElementUtils.findMergedAnnotation(beanType, annotationType);
	}

	/**
	 *
	 * @Description: 判断是否是基础类型
	 *               注意：将枚举类型也当成了基础类型
	 * @time 2023年1月29日 下午3:05:19
	 * @author caster2023
	 * @param clazz
	 * @return
	 * boolean
	 * @throws
	 */
	public static boolean isBaseType(Class<?> clazz) {
		if (clazz.isPrimitive()) {
			return true;
		}

		if (clazz == Integer.class) {
            return true;
        } else if (clazz == Float.class) {
            return true;
        } else if (clazz == Long.class) {
            return true;
        } else if (clazz == Double.class) {
            return true;
        } else if (clazz == Byte.class) {
            return true;
        } else if (clazz == Boolean.class) {
            return true;
        } else if (clazz == String.class) {
            return true;
        }

	    return false;
	}

	public static List<Field> listDateTypeFields(Class clazz) {
		List<Field> dateTypeFieldList = new ArrayList();
		List<Field> allFieldList = getAllFields(clazz);
		for (Field oneField : allFieldList) {
			Class fieldType = oneField.getType();
			if (fieldType == Date.class) {
				dateTypeFieldList.add(oneField);
			}
		}

		return dateTypeFieldList;
	}

	@SuppressWarnings("unchecked")
	public static List<Class> getAllClassByInterface(Class c) {
		List returnClassList = new ArrayList<Class>();
		// 判断是不是接口,不是接口不作处理
		if (c.isInterface()) {
			Package pkg = c.getPackage();
			String packageName = "";
			if (pkg != null) {
				packageName = c.getPackage().getName(); // 获得当前包名
			} else {
				packageName = c.getName().substring(0, c.getName().lastIndexOf(".")); // 获得当前包名
			}
			try {
				List<Class> allClass = getClasses(packageName, c.getClassLoader());// 获得当前包以及子包下的所有类

				// 判断是否是一个接口
				for (int i = 0; i < allClass.size(); i++) {
					if (c.isAssignableFrom(allClass.get(i))) {
						if (!c.equals(allClass.get(i))) {
							returnClassList.add(allClass.get(i));
						}
					}
				}
			} catch (Exception e) {
				RuntimeException ex = new RuntimeException(
						"[ClassUtil getAllClassByInterface] getAllClassByInterface failed.");
				ex.initCause(e);
				throw ex;
			}
		}
		return returnClassList;
	}

	public static List<Class> getClasses(String packageName, ClassLoader classLoader)
			throws ClassNotFoundException, IOException {
		String path = packageName.replace(".", "/");
		Enumeration<URL> resources = classLoader.getResources(path);
		List<File> dirs = new ArrayList<File>();
		while (resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			dirs.add(new File(resource.getFile()));
		}

		ArrayList<Class> classes = new ArrayList<Class>();
		for (File directory : dirs) {
			classes.addAll(findClass(directory, packageName, classLoader));
		}

		return classes;
	}

	private static List<Class> findClass(File directory, String packageName, ClassLoader classLoader)
			throws ClassNotFoundException {
		List<Class> classes = new ArrayList<Class>();
		if (!directory.exists()) {
			return classes;
		}
		File[] files = directory.listFiles();
		for (File file : files) {
			if (file.isDirectory()) {
				assert !file.getName().contains(".");
				classes.addAll(findClass(file, packageName + "." + file.getName(), classLoader));
			} else if (file.getName().endsWith(".class")) {
				classes.add(Class.forName(packageName + "." + file.getName().substring(0, file.getName().length() - 6),
						false, classLoader));
			}
		}
		return classes;
	}

	/**
	 * 判断类是否函数类.<br>
	 * 首先,类不能是抽象的,其次,类必须实现函数接口
	 *
	 * @param c
	 *            类
	 * @return 是否是函数类
	 */
	public static boolean isFunction(Class<?> c, Class<?> interfaceClazz) {
		if (c == null) {
			return false;
		}
		if (c.isInterface()) {
			return false;
		}
		if (Modifier.isAbstract(c.getModifiers())) {
			return false;// 抽象
		}
		// Class<?>[] interfaces = c.getInterfaces();
		// if (interfaces == null || interfaces.length == 0) {
		// return false;
		// }
		// for (Class<?> i : interfaces) {
		// if (i == IFunction.class) {
		// return true;
		// }
		// }
		return interfaceClazz.isAssignableFrom(c);
	}

	public static boolean isAnnotation(Class<? extends Annotation> ann, Class<?> c) {
		if (ann == null) {
			return false;
		}
		if (!c.isInterface()) {
			return false;
		}

		return c.isAnnotationPresent(ann);
	}

	/**
	 * 获取项目的path下所有的文件夹和文件
	 *
	 * @return 文件列表
	 */
	private static List<File> listPaths() {
		List<File> files = new ArrayList<File>();
		String jars = System.getProperty("java.class.path");
		if (jars == null) {
			logger.warn("[ClassUtil listPaths] java.class.path is null!");
			return files;
		} else {
			// web 容器下的类路径可能不同，查找 WEB-INF下的类文件
			logger.info("java.class.path:" + jars);
		}

		URL root = Thread.currentThread().getContextClassLoader().getResource("");// ClassUtil.class.getClassLoader().getResource("");
		if (root == null) {
			logger.error("path root is null!");
			return files;
		}
		String path = null;
		try {
			path = URLDecoder.decode(root.getFile(), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			logger.error("[ClassUtil listPaths] decode the root file error:", e);
			return files;
		}

		File dir = new File(path);
		String[] array = (jars).split(";");
		if (array != null) {
			for (String s : array) {
				if (s == null) {
					continue;
				}
				File f = new File(s);
				if (f.exists()) {
					files.add(f);
				} else {
					// 有些jar就在系统目录下,省略了路径,要加上
					File jar = new File(dir, s);
					if (jar.exists()) {
						files.add(jar);
					}
				}
			}
		}

		return files;
	}

	/**
	 * 根据接口类，查找所有实现了指定接口的类
	 *
	 * @param pkg
	 *            包名,此处只是为了限定,防止漫无目的的查找.不用设置也可以,就要每找到一个类就要加载一次判断了
	 * @return 类列表
	 */
	public static List<Class<?>> getClassesByInterface(String pkg, Class<?> interfaceClazz) {
		List<Class<?>> list = new ArrayList<Class<?>>();
		for (File f : listPaths()) {
			// 如果是以文件的形式保存在服务器上
			if (f.isDirectory()) {
				// 获取包的物理路径
				String path = pkg.replace('.', File.separatorChar);
				dirWalker(interfaceClazz, path, f, list);
			} else {// 尝试是否是jar文件
				// 获取jar
				JarFile jar = null;
				try {
					jar = new JarFile(f);
				} catch (IOException e) {
					// 有可能不是一个jar
				}
				if (jar == null) {
					continue;
				}
				String path = pkg.replace('.', '/');
				// 从此jar包 得到一个枚举类
				Enumeration<JarEntry> entries = jar.entries();
				// 同样的进行循环迭代
				while (entries.hasMoreElements()) {
					// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
					JarEntry entry = entries.nextElement();
					String name = entry.getName();
					// 如果是以/开头的
					if (name.charAt(0) == '/') {
						// 获取后面的字符串
						name = name.substring(1);
					}
					// 如果前半部分和定义的包名相同
					if (name.contains(path)) {
						if (name.endsWith(".class") && !entry.isDirectory()) {
							name = name.replace("/", ".").substring(0, name.lastIndexOf("."));
							try {
								Class<?> c = Class.forName(name);
								if (isFunction(c, interfaceClazz)) {
									list.add(c);
								}
							} catch (Exception e) {
								// 找不到无所谓了
							}
						}
					}
				}
			}
		}

		return list;
	}


	/**
	 * 根据接口类，查找所有实现了指定接口的类
	 *
	 * @param pkg
	 *            包名,此处只是为了限定,防止漫无目的的查找.不用设置也可以,就要每找到一个类就要加载一次判断了
	 * @return 类列表
	 */
	public static List<Class<?>> getClassesByAnnotation(String pkg, Class<? extends Annotation> ann) {
		List<Class<?>> list = new ArrayList<Class<?>>();
		for (File f : listPaths()) {
			// 如果是以文件的形式保存在服务器上
			if (f.isDirectory()) {
				// 获取包的物理路径
				String path = pkg.replace('.', File.separatorChar);
				dirWalker2(ann, path, pkg, f, list);
			} else {// 尝试是否是jar文件
				// 获取jar
				JarFile jar = null;
				try {
					jar = new JarFile(f);
				} catch (IOException e) {
					// 有可能不是一个jar
				}
				if (jar == null) {
					continue;
				}
				String path = pkg.replace('.', '/');
				// 从此jar包 得到一个枚举类
				Enumeration<JarEntry> entries = jar.entries();
				// 同样的进行循环迭代
				while (entries.hasMoreElements()) {
					// 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
					JarEntry entry = entries.nextElement();
					String name = entry.getName();
					// 如果是以/开头的
					if (name.charAt(0) == '/') {
						// 获取后面的字符串
						name = name.substring(1);
					}
					// 如果前半部分和定义的包名相同
					if (name.contains(path)) {
						if (name.endsWith(".class") && !entry.isDirectory()) {
							name = name.replace("/", ".").substring(0, name.lastIndexOf("."));
							try {
								Class<?> c = Class.forName(name);
								if (isAnnotation(ann, c)) {
									list.add(c);
								}
							} catch (Exception e) {
								// 找不到无所谓了
							}
						}
					}
				}
			}
		}

		return list;
	}

	/**
	 * 遍历文件夹下所有的类
	 *
	 * @param path
	 *            包路径
	 * @param file
	 *            文件
	 * @param list
	 *            保存类列表
	 */
	private static void dirWalker(Class<?> interfaceClazz, String path, File file, List<Class<?>> list) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					dirWalker(interfaceClazz, path, f, list);
				}
			} else {
				Class<?> c = loadClassByFile(interfaceClazz, path, file);
				if (c != null) {
					list.add(c);
				}
			}
		}
	}

	private static void dirWalker2(Class<? extends Annotation> ann, String path, String pkg, File file, List<Class<?>> list) {
		if (file.exists()) {
			if (file.isDirectory()) {
				for (File f : file.listFiles()) {
					dirWalker2(ann, path, pkg, f, list);
				}
			} else {
				Class<?> c = loadClassWithAnnotation(ann, path, pkg, file);
				if (c != null) {
					list.add(c);
				}
			}
		}
	}

	/**
	 * 从文件加载类
	 *
	 * @param pkg
	 *            包路径
	 * @param file
	 *            文件
	 * @return 类或者null
	 */
	private static Class<?> loadClassByFile(Class<?> interfaceClazz, String pkg, File file) {
		if (!file.isFile()) {
			return null;
		}
		String name = file.getName();
		if (name.endsWith(".class")) {
			String ap = file.getAbsolutePath();
			if (!ap.contains(pkg)) {
				return null;
			}
			name = ap.substring(ap.indexOf(pkg) + pkg.length());
			if (name.startsWith(File.separator)) {
				name = name.substring(1);
			}
			String path = (pkg + "." + name.substring(0, name.lastIndexOf("."))).replace(File.separatorChar, '.');
			try {
				Class<?> c = Class.forName(path);
				if (isFunction(c, interfaceClazz)) {
					return c;
				}
			} catch (ClassNotFoundException e) {
				// do nothing
			}
		}

		return null;
	}

	private static Class<?> loadClassWithAnnotation(Class<? extends Annotation> ann, String path2, String pkg, File file) {
		if (!file.isFile()) {
			return null;
		}
		String name = file.getName();
		if (name.endsWith(".class")) {
			String ap = file.getAbsolutePath();
			ap = ap.replace(File.separator, ".");
			if (!ap.contains(pkg)) {
				return null;
			}

			name = ap.substring(ap.indexOf(pkg) + pkg.length());
			if (name.startsWith(File.separator)) {
				name = name.substring(1);
			}
			String path = (pkg + "." + name.substring(0, name.lastIndexOf("."))).replace(File.separatorChar, '.');
			try {
				Class<?> c = Class.forName(path);
				if (isAnnotation(ann, c)) {
					return c;
				}
			} catch (ClassNotFoundException e) {
				// do nothing
			}
		}

		return null;
	}

	/**
	 *
	 * @Description: 获得某个类的所有声明的字段，即包括public、private和proteced
	 * @time 2023年1月29日 下午3:38:15
	 * @author caster2023
	 * @param clazz
	 * @return
	 * List<Field>
	 * @throws
	 */
	public static List<Field> getAllFields(Class<?> clazz) {
		List<Field> fieldList = new ArrayList();
		for(; clazz != Object.class ; clazz = clazz.getSuperclass()) {
			try {
				Field[] localFields = clazz.getDeclaredFields();
				for (Field oneField : localFields) {
					fieldList.add(oneField);
				}
			} catch (Exception e) {

			}
		}

		return fieldList;
	}

	public static Field getFieldByName(Class<?> clazz, String propertyName) {
		if (clazz == null || StrUtil.isBlank(propertyName)) {
			return null;
		}

		String key = clazz.getName() + "#" + propertyName;
		Field retField = cacheMap.get(key);
		if (retField != null) {
			return retField;
		}

		List<Field> optionFields = new ArrayList();

		List<Field> allFieldList = getAllFields(clazz);
		for(Field oneField : allFieldList) {
			if (oneField.getName().equals(propertyName)) {
				retField = oneField;
				optionFields.add(oneField);
				break;
			} else if (oneField.getName().equalsIgnoreCase(propertyName)) {
				optionFields.add(oneField);
			}
		}
		if (retField == null) {
			for(Field oneField : allFieldList) {
				if (oneField.getType() == boolean.class) {
					String tmpName = "is" + propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
					if (oneField.getName().equals(tmpName)) {
						retField = oneField;
						optionFields.add(oneField);
						break;
					} else if (oneField.getName().equalsIgnoreCase(tmpName)) {
						optionFields.add(oneField);
					}
				}
			}
		}

		if (retField != null) {
			cacheMap.putIfAbsent(key, retField);
			return retField;
		}
		if (optionFields.size() > 0) {
			logger.warn("未能在类:{} 中找到严格匹配属性名:{} 的字段对象，将返回一个疑似字段:{}", clazz, propertyName, optionFields.get(0).getName());
			cacheMap.putIfAbsent(key, optionFields.get(0));
			return optionFields.get(0);
		}

		return null;
	}

}
