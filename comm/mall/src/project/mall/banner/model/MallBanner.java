package project.mall.banner.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class MallBanner extends EntityObject<String> {
    private static final long serialVersionUID = -2824221137127724548L;

    //封面图
    private String imgUrl;

    //序号
    private int sort;

    //轮播类型 pc/h5
    private String type;

    //跳转链接
    private String link;

    //图片类型 0-小图 1-大图
    private int imgType;

    //备注
    private String remarks;

    //创建时间
    private Date createTime;

}
