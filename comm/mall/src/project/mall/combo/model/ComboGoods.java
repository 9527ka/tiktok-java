package project.mall.combo.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 商品配置的直通车记录
 */
@Data
public class ComboGoods extends EntityObject<String> {
	private String comboId;
	private String comboRid;
	private String partyId;
	private String sellGoodsId;
	private long stopTime;
	private Date createTime;
}
