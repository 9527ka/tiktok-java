package project.mall.combo.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class ComboUser extends EntityObject<String> {

	private String comboId;
	private int promoteNum;
	private long stopTime;
}
