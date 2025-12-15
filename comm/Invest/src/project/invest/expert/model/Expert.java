package project.invest.expert.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 专家管理
 */
@Data
public class Expert extends EntityObject<String> {
	private static final long serialVersionUID = -4670490376092518726L;
	/**
	 * 昵称
	 */
	private String name;

	/**
	 * 个人介绍
	 */
	private String content;

	/**
	 * 简介
	 */
	private String summary;

	/**
     * 语言
	 */
	private String lang;

    /**
     * 图片
	 */
	private String iconImg;

    /**
     * 序号
	 */
	private Integer sort;

    /**
     * 创建时间
	 */
	private Date createTime;

    /**
     * 状态值
	 */
	private Integer status;

}
