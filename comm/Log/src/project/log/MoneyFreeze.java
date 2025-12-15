package project.log;

import kernel.bo.EntityObject;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class MoneyFreeze extends EntityObject<String> {
	private static final long serialVersionUID = -5914896022101327097L;

	private String partyId;
	private String operator;
	private Double amount;
	private String moneyLog;
	private Date beginTime;
	private Date endTime;
	private Date createTime;
	// 资金状态：0-已解冻，1-冻结中
	private Integer status;
	private String reason;
}
