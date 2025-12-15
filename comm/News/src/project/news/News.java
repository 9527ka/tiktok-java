package project.news;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 新闻管理
 *
 */
@Data
public class News extends EntityObject<String> {
	private static final long serialVersionUID = -4670490376092518726L;
	/**
	 * 标题
	 */
	private String title;
	/**
	 * 内容
	 */
	private String content;

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
	 * 发布时间
	 */
	private String releaseTime;

	/**
	 * 状态值
	 */
	private Integer status;

}
