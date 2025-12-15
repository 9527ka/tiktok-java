package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class SystemComment extends EntityObject<String> {
    private static final long serialVersionUID = 5651559284886837528L;

    //评分
    private int score;

    //图片1
    private String imgUrl1;

    private String imgUrl2;

    private String imgUrl3;

    private String imgUrl4;

    private String imgUrl5;

    private String imgUrl6;

    private String imgUrl7;

    private String imgUrl8;

    private String imgUrl9;

    //评论内容
    private String content;

    //'状态 0-启用， 1-禁用'
    private int status;

    private Date createTime;

    private   String systemGoodId;
}
