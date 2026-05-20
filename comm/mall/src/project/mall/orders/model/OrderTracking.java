package project.mall.orders.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

/**
 * 订单物流轨迹: 由商家/管理员手动录入, 用户在 "查看物流" 弹窗中按时间倒序展示.
 */
@Data
public class OrderTracking extends EntityObject<String> {

    private static final long serialVersionUID = 1L;

    private String orderId;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date eventTime;

    /**
     * 地点 / 转运中心, 如 "广州转运中心"
     */
    private String location;

    /**
     * 自定义状态文本, 如 "已揽收" / "在途" / "派送中" / "已签收" / "异常"
     */
    private String status;

    /**
     * 详情, 如 "包裹已离开广州转运中心"
     */
    private String description;

    /**
     * 录入人 partyId
     */
    private String creator;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
}
