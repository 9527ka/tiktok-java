package kernel.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import ext.TypeConv;
import kernel.annotation.BeanFieldName;
import kernel.constants.DataTimeConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * 通用对象处理类。
 * 参考：org.apache.commons.lang.ObjectUtils
 */
public abstract class ObjectTools {
	private static Logger logger = LoggerFactory.getLogger(ObjectTools.class);

	private static final int INITIAL_HASH = 7;
	private static final int MULTIPLIER = 31;

	private static final String EMPTY_STRING = "";
	private static final String NULL_STRING = "null";
	private static final String ARRAY_START = "{";
	private static final String ARRAY_END = "}";
	private static final String EMPTY_ARRAY = ARRAY_START + ARRAY_END;
	private static final String ARRAY_ELEMENT_SEPARATOR = ", ";

	// 缓存起来，提升效率，field：完整类名#属性名
	//private final static ConcurrentHashMap<String, Field> cacheMap = new ConcurrentHashMap();

	/**
	 *
	 * beanToMap:(JavaBean 对象转化为一个  Map). <br/>
	 *           javabean的属性将变成map对象的key，javabean对象的属性值，将原封不动地变成map对象的value;
	 *           javabean的父类中的属性也会提取出来;
	 *
	 * @param bean
	 * @return
	 * @throws IntrospectionException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map beanToMap(Object bean) {
		Class type = bean.getClass();
		Map returnMap = new HashMap();
		String curFieldName = "";
		Field curField = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			PropertyDescriptor[] propertyDescriptors =  beanInfo.getPropertyDescriptors();
			for (int i = 0; i< propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				String jsonAttrName = propertyName;
				if (!propertyName.equals("class")) {
					Method readMethod = descriptor.getReadMethod();

					curField = ClassTools.getFieldByName(type, propertyName);
					if (curField == null) {
						continue;
					}
					curFieldName = propertyName;

					// System.out.println("=========> propertyName:" + propertyName + ", curField:" + curField);
					Annotation[] fieldAnns = curField.getAnnotations();
					if (fieldAnns != null && fieldAnns.length > 0) {
						for (Annotation oneFieldAnn : fieldAnns) {
							// oneFieldAnn:class com.sun.proxy.$Proxy5, SerializedName.class:interface com.google.gson.annotations.SerializedName
							//System.out.println("=========> propertyName:" + propertyName + ", oneFieldAnn:" + oneFieldAnn.getClass() + ", SerializedName.class:" + SerializedName.class);
							// System.out.println("=========> propertyName:" + propertyName + ", oneFieldAnn:" + oneFieldAnn.annotationType() + ", SerializedName.class:" + SerializedName.class);
							if (oneFieldAnn.annotationType() == BeanFieldName.class) {
								BeanFieldName serialFieldAnn = (BeanFieldName)oneFieldAnn;
								if (StrUtil.isNotBlank(serialFieldAnn.value())) {
									jsonAttrName = serialFieldAnn.value();
									// System.out.println("=========> propertyName:" + propertyName + ", jsonAttrName:" + jsonAttrName);
									break;
								}
							}
						}
					}

					Object result = readMethod.invoke(bean, new Object[0]);
					if (result != null) {
						returnMap.put(jsonAttrName, result);
					} else {
						if (descriptor.getPropertyType() == String.class) {
							returnMap.put(jsonAttrName, "");
						} else {
							returnMap.put(jsonAttrName, null);
						}
					}
				}
			}
		} catch(Exception e) {
			logger.error("[ObjectHelper beanToMap] 在转换类:" + type + " 对象的属性字段:" + curFieldName + " 时得到属性对象:" + curField + " 报错:", e);
		}

		return returnMap;
	}

	/**
	 *
	 * convertMap:(将一个 Map 对象转化为一个 JavaBean). <br/>
	 *
	 * @param type
	 * @param map
	 * @return
	 * @throws IntrospectionException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws InvocationTargetException
	 */
	@SuppressWarnings("rawtypes")
	public static Object mapToBean(Class type, Map map) {
		Object obj = null;
		try {
			BeanInfo beanInfo = Introspector.getBeanInfo(type);
			obj = type.newInstance();
			PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
			for (int i = 0; i < propertyDescriptors.length; i++) {
				PropertyDescriptor descriptor = propertyDescriptors[i];
				String propertyName = descriptor.getName();
				if (map.containsKey(propertyName)) {
					Object value = map.get(propertyName);
					Object[] args = new Object[1];
					args[0] = value;
					descriptor.getWriteMethod().invoke(obj, args);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return obj;
	}

	/**
	 *
	 * getObjectFromBlob: 将输入流转换为对象. <br/>
	 *
	 * @param stream
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Object getObjectFromInputStream(ByteArrayInputStream stream)
			throws ClassNotFoundException, IOException, SQLException {
		Object obj = null;
		if (null != stream) {
			if (stream.available() == 0) {
			} else {
				ObjectInputStream in = new ObjectInputStream(stream);
				try {
					obj = in.readObject();
				} finally {
					in.close();
				}
			}
		}
		return obj;
	}

	/**
	 * 判断Object对象是否为空
	 *
	 * @param o
	 * @return boolean
	 */
	public static boolean isEmpty(Object o) {
		return null == o || (o instanceof String ? StrUtil.isEmpty((String) o) : false);
	}

	/**
	 * 判断Object对象是否为空
	 *
	 * @param o
	 * @return boolean
	 */
	public static boolean isNotEmpty(Object o) {
		return null != o && (o instanceof String ? StrUtil.isNotEmpty((String) o) : true);
	}

	/**
	 * Determine whether the given object is an array: either an Object array or
	 * a primitive array.
	 *
	 * @param obj
	 *            the object to check
	 */
	public static boolean isArray(Object obj) {
		return (obj != null && obj.getClass().isArray());
	}

	/**
	 * Determine whether the given array is empty: i.e. <code>null</code> or of
	 * zero length.
	 *
	 * @param array
	 *            the array to check
	 */
	public static boolean isEmpty(Object[] array) {
		return (array == null || array.length == 0);
	}


	/**
	 * Append the given object to the given array, returning a new array
	 * consisting of the input array contents plus the given object.
	 *
	 * @param array
	 *            the array to append to (can be <code>null</code>)
	 * @param obj
	 *            the object to append
	 * @return the new array (of the same component type; never
	 *         <code>null</code>)
	 */
	public static <A, O extends A> A[] addObjectToArray(A[] array, O obj) {
		Class<?> compType = Object.class;
		if (array != null) {
			compType = array.getClass().getComponentType();
		} else if (obj != null) {
			compType = obj.getClass();
		}
		int newArrLength = (array != null ? array.length + 1 : 1);
		@SuppressWarnings("unchecked")
		A[] newArr = (A[]) Array.newInstance(compType, newArrLength);
		if (array != null) {
			System.arraycopy(array, 0, newArr, 0, array.length);
		}
		newArr[newArr.length - 1] = obj;
		return newArr;
	}

	// ---------------------------------------------------------------------
	// Convenience methods for content-based equality/hash-code handling
	// ---------------------------------------------------------------------

	/**
	 * Determine if the given objects are equal, returning <code>true</code> if
	 * both are <code>null</code> or <code>false</code> if only one is
	 * <code>null</code>.
	 * <p>
	 * Compares arrays with <code>Arrays.equals</code>, performing an equality
	 * check based on the array elements rather than the array reference.
	 *
	 * @param o1
	 *            first Object to compare
	 * @param o2
	 *            second Object to compare
	 * @return whether the given objects are equal
	 * @see Arrays#equals
	 */
	public static boolean nullSafeEquals(Object o1, Object o2) {
		if (o1 == o2) {
			return true;
		}
		if (o1 == null || o2 == null) {
			return false;
		}
		if (o1.equals(o2)) {
			return true;
		}
		if (o1.getClass().isArray() && o2.getClass().isArray()) {
			if (o1 instanceof Object[] && o2 instanceof Object[]) {
				return Arrays.equals((Object[]) o1, (Object[]) o2);
			}
			if (o1 instanceof boolean[] && o2 instanceof boolean[]) {
				return Arrays.equals((boolean[]) o1, (boolean[]) o2);
			}
			if (o1 instanceof byte[] && o2 instanceof byte[]) {
				return Arrays.equals((byte[]) o1, (byte[]) o2);
			}
			if (o1 instanceof char[] && o2 instanceof char[]) {
				return Arrays.equals((char[]) o1, (char[]) o2);
			}
			if (o1 instanceof double[] && o2 instanceof double[]) {
				return Arrays.equals((double[]) o1, (double[]) o2);
			}
			if (o1 instanceof float[] && o2 instanceof float[]) {
				return Arrays.equals((float[]) o1, (float[]) o2);
			}
			if (o1 instanceof int[] && o2 instanceof int[]) {
				return Arrays.equals((int[]) o1, (int[]) o2);
			}
			if (o1 instanceof long[] && o2 instanceof long[]) {
				return Arrays.equals((long[]) o1, (long[]) o2);
			}
			if (o1 instanceof short[] && o2 instanceof short[]) {
				return Arrays.equals((short[]) o1, (short[]) o2);
			}
		}
		return false;
	}

	/**
	 * Return as hash code for the given object; typically the value of
	 * <code>{@link Object#hashCode()}</code>. If the object is an array, this
	 * method will delegate to any of the <code>nullSafeHashCode</code> methods
	 * for arrays in this class. If the object is <code>null</code>, this method
	 * returns 0.
	 * 
	 * @see #nullSafeHashCode(Object[])
	 * @see #nullSafeHashCode(boolean[])
	 * @see #nullSafeHashCode(byte[])
	 * @see #nullSafeHashCode(char[])
	 * @see #nullSafeHashCode(double[])
	 * @see #nullSafeHashCode(float[])
	 * @see #nullSafeHashCode(int[])
	 * @see #nullSafeHashCode(long[])
	 * @see #nullSafeHashCode(short[])
	 */
	public static int nullSafeHashCode(Object obj) {
		if (obj == null) {
			return 0;
		}
		if (obj.getClass().isArray()) {
			if (obj instanceof Object[]) {
				return nullSafeHashCode((Object[]) obj);
			}
			if (obj instanceof boolean[]) {
				return nullSafeHashCode((boolean[]) obj);
			}
			if (obj instanceof byte[]) {
				return nullSafeHashCode((byte[]) obj);
			}
			if (obj instanceof char[]) {
				return nullSafeHashCode((char[]) obj);
			}
			if (obj instanceof double[]) {
				return nullSafeHashCode((double[]) obj);
			}
			if (obj instanceof float[]) {
				return nullSafeHashCode((float[]) obj);
			}
			if (obj instanceof int[]) {
				return nullSafeHashCode((int[]) obj);
			}
			if (obj instanceof long[]) {
				return nullSafeHashCode((long[]) obj);
			}
			if (obj instanceof short[]) {
				return nullSafeHashCode((short[]) obj);
			}
		}
		return obj.hashCode();
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(Object[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + nullSafeHashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(boolean[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(byte[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(char[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(double[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(float[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(int[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(long[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + hashCode(array[i]);
		}
		return hash;
	}

	/**
	 * Return a hash code based on the contents of the specified array. If
	 * <code>array</code> is <code>null</code>, this method returns 0.
	 */
	public static int nullSafeHashCode(short[] array) {
		if (array == null) {
			return 0;
		}
		int hash = INITIAL_HASH;
		int arraySize = array.length;
		for (int i = 0; i < arraySize; i++) {
			hash = MULTIPLIER * hash + array[i];
		}
		return hash;
	}

	/**
	 * Return the same value as <code>{@link Boolean#hashCode()}</code>.
	 * 
	 * @see Boolean#hashCode()
	 */
	public static int hashCode(boolean bool) {
		return bool ? 1231 : 1237;
	}

	/**
	 * Return the same value as <code>{@link Double#hashCode()}</code>.
	 * 
	 * @see Double#hashCode()
	 */
	public static int hashCode(double dbl) {
		long bits = Double.doubleToLongBits(dbl);
		return hashCode(bits);
	}

	/**
	 * Return the same value as <code>{@link Float#hashCode()}</code>.
	 * 
	 * @see Float#hashCode()
	 */
	public static int hashCode(float flt) {
		return Float.floatToIntBits(flt);
	}

	/**
	 * Return the same value as <code>{@link Long#hashCode()}</code>.
	 * 
	 * @see Long#hashCode()
	 */
	public static int hashCode(long lng) {
		return (int) (lng ^ (lng >>> 32));
	}

	// ---------------------------------------------------------------------
	// Convenience methods for toString output
	// ---------------------------------------------------------------------

	/**
	 * Determine the class name for the given object.
	 * <p>
	 * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
	 * 
	 * @param obj
	 *            the object to introspect (may be <code>null</code>)
	 * @return the corresponding class name
	 */
	public static String nullSafeClassName(Object obj) {
		return (obj != null ? obj.getClass().getName() : NULL_STRING);
	}

	/**
	 * Return a String representation of the specified Object.
	 * <p>
	 * Builds a String representation of the contents in case of an array.
	 * Returns <code>"null"</code> if <code>obj</code> is <code>null</code>.
	 * 
	 * @param obj
	 *            the object to build a String representation for
	 * @return a String representation of <code>obj</code>
	 */
	public static String nullSafeToString(Object obj) {
		if (obj == null) {
			return NULL_STRING;
		}
		if (obj instanceof String) {
			return (String) obj;
		}
		if (obj instanceof Object[]) {
			return nullSafeToString((Object[]) obj);
		}
		if (obj instanceof boolean[]) {
			return nullSafeToString((boolean[]) obj);
		}
		if (obj instanceof byte[]) {
			return nullSafeToString((byte[]) obj);
		}
		if (obj instanceof char[]) {
			return nullSafeToString((char[]) obj);
		}
		if (obj instanceof double[]) {
			return nullSafeToString((double[]) obj);
		}
		if (obj instanceof float[]) {
			return nullSafeToString((float[]) obj);
		}
		if (obj instanceof int[]) {
			return nullSafeToString((int[]) obj);
		}
		if (obj instanceof long[]) {
			return nullSafeToString((long[]) obj);
		}
		if (obj instanceof short[]) {
			return nullSafeToString((short[]) obj);
		}
		String str = obj.toString();
		return (str != null ? str : EMPTY_STRING);
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(Object[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(String.valueOf(array[i]));
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(boolean[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(byte[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(char[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append("'").append(array[i]).append("'");
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(double[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(float[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}

			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(int[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(long[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * Return a String representation of the contents of the specified array.
	 * <p>
	 * The String representation consists of a list of the array's elements,
	 * enclosed in curly braces (<code>"{}"</code>). Adjacent elements are
	 * separated by the characters <code>", "</code> (a comma followed by a
	 * space). Returns <code>"null"</code> if <code>array</code> is
	 * <code>null</code>.
	 * 
	 * @param array
	 *            the array to build a String representation for
	 * @return a String representation of <code>array</code>
	 */
	public static String nullSafeToString(short[] array) {
		if (array == null) {
			return NULL_STRING;
		}
		int length = array.length;
		if (length == 0) {
			return EMPTY_ARRAY;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < length; i++) {
			if (i == 0) {
				sb.append(ARRAY_START);
			} else {
				sb.append(ARRAY_ELEMENT_SEPARATOR);
			}
			sb.append(array[i]);
		}
		sb.append(ARRAY_END);
		return sb.toString();
	}

	/**
	 * 复制对象obj，类似于值传递，非引用
	 */
	public static Object cloneObject(Object obj) throws Exception {
		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		ObjectOutputStream out = new ObjectOutputStream(byteOut);
		out.writeObject(obj);
		ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());
		ObjectInputStream in = new ObjectInputStream(byteIn);
		return in.readObject();
	}

	/**
	 * 获得一个对象各个属性的字节流
	 */
//	@SuppressWarnings("unchecked")
//	public synchronized static byte[] getPropertyStream(Object entityName) throws Exception {
//		Class c = entityName.getClass();
//		Field field[] = c.getDeclaredFields();
//		ByteBuffer bbuf = ByteBuffer.allocate(1024 * 10);
//		for (Field f : field) {
//			Object v = invokeMethod(entityName, f.getName(), null);
//			if (f.getType().toString().equals("int")) {// 支持Int
//				bbuf.put(ToolObjectToByte.intToByte(Integer.parseInt(v.toString())));
//			} else if (f.getType().toString().equals("class java.lang.String")) {// 支持
//																					// String
//				bbuf.put(v.toString().getBytes());
//			} else if (f.getType().toString().equals("short")) {// 支持 Short
//				bbuf.put(ToolObjectToByte.shortToByte((Short) v));
//			} else if (f.getType().toString().equals("byte")) {// 支持 Byte
//				bbuf.put((Byte) v);
//			}
//		}
//		// 回绕缓冲区 一是将 curPointer 移到 0, 二是将 endPointer 移到有效数据结尾
//		bbuf.flip();
//		byte[] byten = new byte[bbuf.limit()];
//		bbuf.get(byten, bbuf.position(), bbuf.limit()); // 得到目前为止缓冲区所有的数据
//		return byten;
//	}

	/**
	 * 获得对象属性的值
	 */
	@SuppressWarnings("unchecked")
	public static Object getValue(Object owner, String attrName) throws Exception {
		Class ownerClass = owner.getClass();
		String methodName = "get" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
		Method method = null;
		try {
			method = ownerClass.getMethod(methodName, new Class[]{});
		} catch (SecurityException e) {
			logger.error("[ObjectUtil getValue] exception:", e);
			throw e;
		} catch (NoSuchMethodException e) {
			if (attrName.startsWith("is")) {
				methodName = attrName;
			} else {
				methodName = "is" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
			}
			try {
				method = ownerClass.getMethod(methodName, new Class[]{});
			} catch (NoSuchMethodException e1) {
				throw e1;
			} catch (SecurityException e2) {
				throw e2;
			} catch (Exception e3) {
				throw e3;
			}
		}
		
		return method.invoke(owner, new Object[]{});
	}

	@SuppressWarnings("unchecked")
	public static void setValue(Object obj, String attrName, Object value) {
		setValue(obj, attrName, value, true);
	}
	
	@SuppressWarnings("unchecked")
	public static void setValue(Object obj, String attrName, Object value, boolean isForceCastTypeIfNotMatch) {
		Class ownerClass = obj.getClass();
		String methodName = "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
		Method method = null;
		Object setMethodValue = null;
		try {
			Class methodParamType = null;
			Class inputParamType = null;
			if (value != null) {
			    inputParamType = value.getClass();
			}
			// 足矣，返回本类和继承来的所有公共方法
			Method[] allMethods = ownerClass.getMethods();
			List<Method> mayMethodList = new ArrayList();
			for (Method oneMethod : allMethods) {
				if (!oneMethod.getName().equals(methodName)) {
					continue;
				}
				Class[] curMethodParameterTypes = oneMethod.getParameterTypes();
				if (curMethodParameterTypes.length != 1) {
					continue;
				}
				
				mayMethodList.add(oneMethod);
				if (inputParamType == null) {
					method = oneMethod;
					methodParamType = curMethodParameterTypes[0];
					inputParamType = methodParamType;
					break;
				}
				
				if (inputParamType == curMethodParameterTypes[0]) {
					method = oneMethod;
					methodParamType = curMethodParameterTypes[0];
					setMethodValue = value;
					break;
				} else if (curMethodParameterTypes[0] != Object.class && curMethodParameterTypes[0].isAssignableFrom(inputParamType)) {
					// 注意：泛型的参数也能跑进来（Object类型）
					method = oneMethod;
					methodParamType = curMethodParameterTypes[0];
					setMethodValue = value;
					break;
				} else {
					logger.debug("类:" + ownerClass.getName() + " 中的当前方法:" + oneMethod.getName() + " 的输入参数类型和传入的值类型不匹配，方法预期参数类型为:" + curMethodParameterTypes[0] + ", 输入参数类型为:" + methodParamType);
				}
			}
			
			if (isForceCastTypeIfNotMatch 
			        && inputParamType != null
			        && method == null 
			        && mayMethodList.size() > 0) {
				logger.debug("类:" + ownerClass.getName() + " 中没有找到匹配的方法:" + methodName + " 将降低参数匹配标准继续查找...");
				if (inputParamType == Long.class
						|| inputParamType == long.class
						|| inputParamType == Integer.class
						|| inputParamType == int.class
						|| inputParamType == Float.class
						|| inputParamType == float.class
						|| inputParamType == Double.class
						|| inputParamType == double.class
						|| inputParamType == String.class
				        || inputParamType == BigDecimal.class) {
					for (int i = 0; i < mayMethodList.size(); i++) {
						Class[] curMethodParameterTypes = mayMethodList.get(i).getParameterTypes();
						try {
							if (curMethodParameterTypes[0] == Integer.class || curMethodParameterTypes[0] == int.class) {
								// 对象字段属性类型为 int 类型时
								if (value == null) {
									if (curMethodParameterTypes[0] == int.class) {
									    setMethodValue = 0;
									} else {
									    setMethodValue = null;
									}
								} else {
								    setMethodValue = new Double(value.toString()).intValue();
								}
								method = mayMethodList.get(i);
								break;
							} else if (curMethodParameterTypes[0] == Long.class || curMethodParameterTypes[0] == long.class) {
								// 对象字段属性类型为 int 类型时
								if (value == null) {
									if (curMethodParameterTypes[0] == long.class) {
									    setMethodValue = 0L;
									} else {
									    setMethodValue = null;
									}
								} else {
								    setMethodValue = new Double(value.toString()).longValue();
								}
								method = mayMethodList.get(i);
								break;
							} else if (curMethodParameterTypes[0] == Float.class || curMethodParameterTypes[0] == float.class) {
								// 对象字段属性类型为 int 类型时
								if (value == null) {
									if (curMethodParameterTypes[0] == float.class) {
									    setMethodValue = 0.0f;
									} else {
									    setMethodValue = null;
									}
								} else {
								    setMethodValue = new Double(value.toString()).floatValue();
								}
								method = mayMethodList.get(i);
								break;
							} else if (curMethodParameterTypes[0] == Double.class || curMethodParameterTypes[0] == double.class) {
								// 对象字段属性类型为 int 类型时
								if (value == null) {
									if (curMethodParameterTypes[0] == double.class) {
									    setMethodValue = 0.0d;
									} else {
									    setMethodValue = null;
									}
								} else {
								    setMethodValue = new Double(value.toString()).doubleValue();
								}
								method = mayMethodList.get(i);
								break;
							} else if (curMethodParameterTypes[0] == BigDecimal.class) {
								// 对象字段属性类型为 BigDecimal 类型时
								if (value == null) {
									setMethodValue = null;
								} else {
									setMethodValue = new BigDecimal(String.valueOf(value.toString()));
								}
								method = mayMethodList.get(i);
								break;
							} else if (curMethodParameterTypes[0] == Date.class) {
								// 对象字段属性类型为日期类型时
								if (value == null) {
									method = mayMethodList.get(i);
									break;
								} else {
									if (value instanceof Date) {
										setMethodValue = value;
									} else if (value instanceof LocalDate) {
										setMethodValue = DateTimeTools.localDate2Date((LocalDate)value);
									} else if (value instanceof LocalTime) {
										setMethodValue = DateTimeTools.localTime2Date((LocalTime)value);
									}if (value instanceof LocalDateTime) {
										setMethodValue = DateTimeTools.localDateTime2Date((LocalDateTime)value);
									} else if (StrUtil.isNumeric(value.toString().trim())) {
										setMethodValue = new Date(Long.parseLong(value.toString().trim()));
									} else {
										setMethodValue = DateUtil.parse(value.toString(), DataTimeConstants.PATTERN_DATE_TIME);
									}
									if (setMethodValue != null) {
										method = mayMethodList.get(i);
										break;
									}
								}
							} else if (curMethodParameterTypes[0] == LocalDate.class) {
								// 对象字段属性类型为日期类型时
								if (value == null) {
									method = mayMethodList.get(i);
									break;
								} else {
									if (value instanceof Date) {
										setMethodValue = DateTimeTools.date2LocalDate((Date)value);
									} else if (value instanceof LocalDate) {
										setMethodValue = value;
									} else if (value instanceof LocalTime) {
										Date tmpDate = DateTimeTools.localTime2Date((LocalTime)value);
										setMethodValue = DateTimeTools.date2LocalDate(tmpDate);
									} if (value instanceof LocalDateTime) {
										Date tmpDate = DateTimeTools.localDateTime2Date((LocalDateTime)value);
										setMethodValue = DateTimeTools.date2LocalDate(tmpDate);
									} else if (StrUtil.isNumeric(value.toString().trim())) {
										Date tmpDate = new Date(Long.parseLong(value.toString().trim()));
										setMethodValue = DateTimeTools.date2LocalDate(tmpDate);
									} else {
										Date tmpDate = DateUtil.parse(value.toString(), DataTimeConstants.PATTERN_DATE_TIME);
										setMethodValue = DateTimeTools.date2LocalDate(tmpDate);
									}
									if (setMethodValue != null) {
										method = mayMethodList.get(i);
										break;
									}
								}
							} else if (curMethodParameterTypes[0] == LocalTime.class) {
								// 对象字段属性类型为日期类型时
								if (value == null) {
									method = mayMethodList.get(i);
									break;
								} else {
									if (value instanceof Date) {
										setMethodValue = DateTimeTools.date2LocalTime((Date)value);
									} else if (value instanceof LocalTime) {
										setMethodValue = value;
									} else if (value instanceof LocalDate) {
										Date tmpDate = DateTimeTools.localDate2Date((LocalDate)value);
										setMethodValue = DateTimeTools.date2LocalTime(tmpDate);
									} if (value instanceof LocalDateTime) {
										Date tmpDate = DateTimeTools.localDateTime2Date((LocalDateTime)value);
										setMethodValue = DateTimeTools.date2LocalTime(tmpDate);
									} else if (StrUtil.isNumeric(value.toString().trim())) {
										Date tmpDate = new Date(Long.parseLong(value.toString().trim()));
										setMethodValue = DateTimeTools.date2LocalTime(tmpDate);
									} else {
										Date tmpDate = DateUtil.parse(value.toString(), DataTimeConstants.PATTERN_DATE_TIME);
										setMethodValue = DateTimeTools.date2LocalTime(tmpDate);
									}
									if (setMethodValue != null) {
										method = mayMethodList.get(i);
										break;
									}
								}
							} else if (curMethodParameterTypes[0] == LocalDateTime.class) {
								// 对象字段属性类型为日期类型时
								if (value == null) {
									method = mayMethodList.get(i);
									break;
								} else {
									if (value instanceof Date) {
										setMethodValue = DateTimeTools.date2LocalDateTime((Date)value);
									} else if (value instanceof LocalDateTime) {
										setMethodValue = value;
									} else if (value instanceof LocalDate) {
										Date tmpDate = DateTimeTools.localDate2Date((LocalDate)value);
										setMethodValue = DateTimeTools.date2LocalDateTime(tmpDate);
									} if (value instanceof LocalTime) {
										Date tmpDate = DateTimeTools.localTime2Date((LocalTime)value);
										setMethodValue = DateTimeTools.date2LocalDateTime(tmpDate);
									} else if (StrUtil.isNumeric(value.toString().trim())) {
										Date tmpDate = new Date(Long.parseLong(value.toString().trim()));
										setMethodValue = DateTimeTools.date2LocalDateTime(tmpDate);
									} else {
										Date tmpDate = DateUtil.parse(value.toString(), DataTimeConstants.PATTERN_DATE_TIME);
										setMethodValue = DateTimeTools.date2LocalDateTime(tmpDate);
									}
									if (setMethodValue != null) {
										method = mayMethodList.get(i);
										break;
									}
								}
							} else if (curMethodParameterTypes[0] == String.class) {
                                // 字符串类型
							    method = mayMethodList.get(i);
                                if (value == null) {
                                    break;
                                } else {
                                    setMethodValue = String.valueOf(value);
                                    break;
                                }
                            }
						} catch (NumberFormatException e) {
							logger.warn("降级匹配在类:" + ownerClass.getName() + " 中找到的方法:" + methodName + " 在转换输入参数:" + value + " 时出现数字转换异常");
						} catch (Exception e) {
							logger.error("降级匹配在类:" + ownerClass.getName() + " 中找到的方法:" + methodName + " 在转换输入参数:" + value + " 时出现数据转换异常", e);
						}
					}
				}
				if (method != null) {
					logger.debug("类:" + ownerClass.getName() + " 中降低匹配标注找到新方法:" + methodName);
				} else {
					setMethodValue = value;
					method = mayMethodList.get(0);
				}
			}
			
			if (method == null) {
				if (attrName.startsWith("is")) {
					methodName = "set" + attrName.substring(2, 3).toUpperCase() + attrName.substring(3);
				} else {
					methodName = "set" + attrName.substring(0, 1).toUpperCase() + attrName.substring(1);
				}
				for (Method oneMethod : allMethods) {
					if (!oneMethod.getName().equals(methodName)) {
						continue;
					}
					Class[] curMethodParameterTypes = oneMethod.getParameterTypes();
					if (curMethodParameterTypes.length != 1) {
						continue;
					}
					
					if (curMethodParameterTypes[0] == Boolean.class) {
					    method = oneMethod;
                        if (value == null || StrUtil.isBlank(value.toString())) {
                            setMethodValue = null;
                        } else {
                            setMethodValue = Boolean.valueOf(value.toString());
                        }
                    } else if (curMethodParameterTypes[0] == boolean.class) {
                        method = oneMethod;
                        if (value == null || StrUtil.isBlank(value.toString())) {
                            setMethodValue = false;
                        } else {
                            setMethodValue = Boolean.valueOf(value.toString());
                        }
                    }
					
					break;
				}
				if (method != null) {
					logger.debug("强制在类:" + ownerClass.getName() + " 中降低匹配标注找到新方法:" + methodName);
				}
			}
		} catch (Exception e1) {
			logger.error("执行类:" + ownerClass.getName() + " 的方法:" + methodName + " 时报错:", e1);
			RuntimeException err = new RuntimeException(e1);
			throw err;
		}
		
		if (method == null) {
			//System.out.println("===================> 没有找到类[" + obj.getClass() + "]的[" + value == null ? "未知" : value.getClass() + "]类型的属性[" + attrName + "]兼容的set方法");
			Exception e = new NoSuchFieldException("没有找到类[" + obj.getClass() + "]的[" + value == null ? "未知" : value.getClass() + "]类型的属性[" + attrName + "]兼容的set方法");
			RuntimeException err = new RuntimeException(e);
			throw err;
		}
		
		try {
			method.invoke(obj, new Object[]{setMethodValue});
		} catch (Exception e) {
			logger.error("执行类:" + ownerClass.getName() + " 的方法:" + methodName + " 时报错，参数:" + value, e);
			RuntimeException err = new RuntimeException(e);
			throw err;
		}
	}
	
	public static Object byteToObject(byte[] bytes) {
		Object obj = null;
		try {
			// bytearray to object
			ByteArrayInputStream bi = new ByteArrayInputStream(bytes);
			ObjectInputStream oi = new ObjectInputStream(bi);

			obj = oi.readObject();
			bi.close();
			oi.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return obj;
	}

	public static byte[] objectToByte(Object obj) {
		byte[] bytes = null;
		try {
			// object to bytearray
			ByteArrayOutputStream bo = new ByteArrayOutputStream();
			ObjectOutputStream oo = new ObjectOutputStream(bo);
			oo.writeObject(obj);

			bytes = bo.toByteArray();

			bo.close();
			oo.close();
		} catch (Exception e) {
			System.out.println("translation" + e.getMessage());
			e.printStackTrace();
		}
		return bytes;
	}

	

//	/**
//	 * 在32位的JVM中，可以通过读取class文件偏移为12的long来获取size。
//	 * 
//	 * @param object
//	 * @return
//	 */
//	public static long sizeOf(Object object){
//	    return getUnsafe().getAddress(
//	        normalize(getUnsafe().getInt(object, 4L)) + 12L);
//	}
	/**
	 * 将有符号int转为无符号long
	 * 
	 * @param value
	 * @return
	 */
	private static long normalize(int value) {
	    if(value >= 0) return value;
	    return (~0L >>> 32) & value;
	}



}
