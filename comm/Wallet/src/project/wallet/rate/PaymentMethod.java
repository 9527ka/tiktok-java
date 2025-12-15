package project.wallet.rate;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;
@Data
public class PaymentMethod extends EntityObject<String> {

	private static final long serialVersionUID = -6658527079884278L;

	/**
	 * 会员ID
	 */
	private String partyId;

	/**
	 * 0=银行卡
	 */
	private int payType;

	/**
	 * 真实姓名
	 */
	private String realName;

	/**
	 * 开户行名称
	 */
	private String bankName;

	/**
	 * 卡号
	 */
	private String bankAccount;

	/**
	 * 1=默认
	 */
	private int status;

	private Date createTime;


}
