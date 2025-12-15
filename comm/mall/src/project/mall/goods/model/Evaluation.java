package project.mall.goods.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class Evaluation extends EntityObject<String> {
    private static final long serialVersionUID = 8096944949372440876L;

    // 商品id
    private String sellerGoodsId;

    // 系统商品id
    private String systemGoodsId;

    // 商家id
    private String sellerId;
    // 用户id

    /**
     * 评论用户账号
     */
    private String userName;

    /**
     * 评论用户id
     * caster 增加于 2023-3-15
     */
    private String partyId;

    /**
     * 评论用户名称（冗余字段）
     * caster 增加于 2023-3-15
     */
    private String partyName;

    /**
     * 评论用户图标（冗余字段）
     * caster 增加于 2023-3-15
     */
    private String partyAvatar;

    /**
     * 评论模板编码：
     * 0-手写评论
     * 其他-评论模板
     *
     * caster 增加于 2023-3-15
     */
    private String template;

    /**
     * 评论来源类型：1-普通评论，2-演示账号评论，3-测试账号评论
     * caster 增加于 2023-3-15
     */
    private Integer sourceType;

    /**
     * 对应商品状态：1-正常，2-下架
     * caster 增加于 2023-3-15
     */
    private Integer goodsStatus;

    /**
     * 评论时间，一般该值等同于 createTime，但该值可以根据业务修改
     * caster 增加于 2023-3-15
     */
    private Date evaluationTime;

    /**
     * 商品属性
     * greg 增加于 2023-3-20
     */
    private String skuId;

    // 评价类型 1-好评 2-中评 3-差评
    private Integer evaluationType;

    //评分
    private Integer rating;

    //评论时间
    private Date createTime;

    //评论内容
    private String content;

    //订单id
    private  String orderId;

    /**
     * 是否启用 0-启用 1-禁用
     */
    private int status;

    /**
     * 国家代码
     */
    private int countryId;

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



}
