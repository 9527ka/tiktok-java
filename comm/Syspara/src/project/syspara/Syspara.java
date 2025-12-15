package project.syspara;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import kernel.bo.EntityObject;
import kernel.util.StringUtils;

/**
 * 系统参数
 */
public class Syspara extends EntityObject<String> {

	private static final long serialVersionUID = -6738793277776213674L;

	private String partyId;

	private String code;

	/**
	 * 0/ROOT可见； 2/管理员可见； 1/用户参数；
	 */
	private int type;

	/**
	 * 0/可修改；1/不可修改；
	 */
	private int modify;

	/**
	 * 排序
	 */
	private int order;

	private String value;

	/**
	 * 业务含义
	 */
	private String notes;

	/**
	 * 类型0：连单，1：dapp
	 */
	private int bagType;

	public int getModify() {
		return modify;
	}

	public void setModify(int modify) {
		this.modify = modify;
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int order) {
		this.order = order;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常
	 * </p>
	 * 
	 * @return
	 */
	public Integer getInteger() {
		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}

		try {
			return Integer.parseInt(value);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常
	 * </p>
	 * 
	 * @return
	 */
	public Long getLong() {
		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}
		try {
			return Long.parseLong(value);
		} catch (Exception ex) {
			return null;
		}
		
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常
	 * </p>
	 * 
	 * @return
	 */
	public Double getDouble() {
		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}
		try {
			return Double.parseDouble(value);
		} catch (Exception ex) {
			return null;
		}
		
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常
	 * </p>
	 * 
	 * @return
	 */
	public Float getFloat() {
		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}

		try {
			return Float.parseFloat(value);
		} catch (Exception ex) {
			return null;
		}
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常
	 * </p>
	 * 
	 * @return
	 */
	public BigDecimal getBigDecimal() {
		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}
		
		try {
			return new BigDecimal(value);
		} catch (Exception ex) {
			return null;
		}
		
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常<BR />
	 * 3.默认格式：yyyy-MM-dd
	 * </p>
	 * 
	 * @return "yyyy-MM-dd"
	 * @throws ParseException
	 */
	public Date getDate() {
		return getDate("yyyy-MM-dd");
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常<BR />
	 * 3.按传入的日期格式，将value转化成date
	 * </p>
	 * 
	 * @param date
	 * @return
	 * @throws ParseException
	 */
	public Date getDate(String pattern) {
		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}
		try {
			return (new SimpleDateFormat(pattern)).parse(value);
		} catch (Exception ex) {
			return null;
		}
		
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常<BR />
	 * 3.按指定的enum的类型转化
	 * </p>
	 * 
	 * @param str
	 * @return
	 * @throws ClassNotFoundException
	 */
	@SuppressWarnings("unchecked")
	public Enum<?> getEnum(String enumType) {
		try {
			return getEnum(Class.forName(enumType).asSubclass(Enum.class));
		} catch (ClassNotFoundException ex) {
			throw new IllegalArgumentException(ex);
		}
	}

	/**
	 * <p>
	 * Description: 1.空值或者空字串返回null<BR />
	 * 2.如果转换失败，直接抛异常<BR />
	 * 3.按指定的enum的类型转化
	 * </p>
	 * 
	 * @param enumType
	 * @return
	 */
	public <T extends Enum<T>> T getEnum(Class<T> enumType) {
		if (StringUtils.isNullOrEmpty(value)) {
			return null;
		}

		return T.valueOf(enumType, value);
	}

	/**
	 * <p>
	 * Description: 调用object的toString文件保存数据
	 * </p>
	 * 
	 * @param value
	 */
	public void setValue(Object value) {
		setValue((value == null) ? null : value.toString());
	}

	/**
	 * 
	 * <p>
	 * Description: 获取boolean值，会将参数值转化为boolean值
	 * </p>
	 * 
	 * @return true or false
	 */
	public boolean getBoolean() {
		if ("Y".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value) || "1".equals(value)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public Syspara clone() throws CloneNotSupportedException {
		return (Syspara) super.clone();
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getBagType() {
		return bagType;
	}

	public void setBagType(int bagType) {
		this.bagType = bagType;
	}
}
