package project.mall.combo.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class ComboLang extends EntityObject<String> {
	private static final long serialVersionUID = 6824319877915158468L;

//	@TableField("comboId")
	private String comboId;

//	@ApiModelProperty(value = "语言")
//	@TableField("LANG")
	private String lang;


//	@ApiModelProperty(value = "名称")
//	@TableField("NAME")
	private String name;

//	@ApiModelProperty(value = "简介")
//	@TableField("CONTENT")
	private String content;

	//假删除 0-正常 1-删除
	private int status;
}
