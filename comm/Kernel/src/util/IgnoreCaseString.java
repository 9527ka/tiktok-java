package util;

import java.io.Serializable;

public class IgnoreCaseString implements Comparable<IgnoreCaseString>, Serializable {
	/**
	 * Member Description
	 */
	private static final long serialVersionUID = -247637491060007575L;

	public static final IgnoreCaseString EMPTY = IgnoreCaseString.valueOf("");

	/**
	 * origStr string对象
	 */
	private final String origStr;

	/**
	 * lowerCaseStr string对象
	 */
	private final String lowerCaseStr;

	// 需要抛出异常， Assert.assertEquals("d", IgnoreCaseString.valueOf(null).toString());
	public static IgnoreCaseString valueOf(String str) {
		if (str == null) {
			return null;
		}
		return new IgnoreCaseString(str);
	}

	/**
	 * 
	 * @param value
	 *            String参数
	 */
	private IgnoreCaseString(String value) {
		if (value == null) {
			throw new NullPointerException("value不能为空");
		}

		origStr = value;
		lowerCaseStr = value.toLowerCase();
	}

	public char charAt(int i) {
		return origStr.charAt(i);
	}

	public int compareTo(IgnoreCaseString o) {
		if (o == null) {
			return 1;
		}

		if (this == o) {
			return 0;
		}

		return lowerCaseStr.compareTo(o.lowerCaseStr);
	}

	public boolean contains(IgnoreCaseString other) {
		if (other == null) {
			return false;
		}

		return lowerCaseStr.contains(other.lowerCaseStr);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (obj instanceof IgnoreCaseString) {
			IgnoreCaseString other = (IgnoreCaseString) obj;
			return lowerCaseStr.equals(other.lowerCaseStr);
		}

		return false;
	}

	/**
	 * @return byte数组 origStr转换成byte数组
	 */
	public byte[] getBytes() {
		return origStr.getBytes();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((lowerCaseStr == null) ? 0 : lowerCaseStr.hashCode());
		return result;
	}

	public int length() {
		return origStr.length();
	}

	// TODO ztt
	// null的情况是否应该存在
	public String[] split(String regex) {
		if (origStr == null) {
			return null;
		}

		return origStr.split(regex);
	}

	public char[] toCharArray() {
		return origStr.toCharArray();
	}

	public String toLowerCase() {
		return origStr.toLowerCase();
	}

	@Override
	public String toString() {
		return origStr;
	}

	public String toUpperCase() {
		return origStr.toUpperCase();
	}

	public String trim() {
		return origStr.trim();
	}

}