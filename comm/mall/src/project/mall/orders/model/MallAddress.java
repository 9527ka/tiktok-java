package project.mall.orders.model;

import kernel.bo.EntityObject;
import lombok.Data;

import javax.persistence.Transient;
import java.util.Date;
@Data
public class MallAddress extends EntityObject<String> {

	private static final long serialVersionUID = -6658527079884278L;

	/**
	 * 会员ID
	 */
	private String partyId;

	/**
	 * 是否默认
	 */
	private int status;
	/**
	 * 手机号
	 */
	private String phone;

	/**
	 * 邮箱
	 */
	private String email;

	/**
	 * 联系人
	 */
	private String contacts;

	/**
	 * 邮编
	 */
	private String postcode;

	/**
	 * 国家
	 */
	private String country;


	/**
	 * 州/省
	 */
	private String province;

	/**
	 * 市
	 */
	private String city;

	/**
	 * 地址
	 */
	private String address;

	private Date createTime;

	/**
	 * 国家代码
	 */
	private int countryId;

	/**
	 * 省代码
	 */
	private int provinceId;

	/**
	 * 城市代码
	 */
	private int cityId;

	// 非表字段，使用的语种
	@Transient
	private transient String language;
}
