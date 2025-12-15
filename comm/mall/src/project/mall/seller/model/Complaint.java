package project.mall.seller.model;

import kernel.bo.EntityObject;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor  // 添加此注解以生成全参构造函数
public class Complaint extends EntityObject<String> {
    private static final long serialVersionUID = -1833741377542576072L;

//    投诉用户id
    private String partyId;

//    投诉用户code
    private String userCode;

//    被投诉店铺ID
    private String storeId;

//    被投诉店铺code
    private String storeCode;

//    投诉类型(1长时间不发货、2卖家服务态度恶劣、3卖家交易欺诈、4卖家承诺不履行、0其他)
    private int complaintStatus = 0;

//    投诉内容
    private String content;

//    创建时间
    private Date createTime;

//    凭证图片
    private String imgUrl1;

    private String imgUrl2;

    private String imgUrl3;

    private String imgUrl4;

    private String imgUrl5;

    private String imgUrl6;

    private String imgUrl7;

    private String imgUrl8;

    private String imgUrl9;

//    审核类型(0未审批，1通过，-1驳回)
    private int auditStatus=0;

//    审核时间
    private Date auditTime;

//    驳回原因
    private String remark;

//    操作人
    private String createUser;

    public enum ComplaintTypeEnum {
        NO_DELIVERY_FOR_A_LONG_TIME(1, "No delivery for a long time","长时间不发货"),
        THE_SELLER_HAS_A_BAD_SERVICE_ATTITUDE( 2, "The seller has a bad service attitude","卖家服务态度恶劣"),
        SELLER_TRANSACTION_FRAUD(3, "Seller transaction fraud","卖家交易欺诈"),
        THE_SELLER_FAILS_TO_FULFILL_HIS_PROMISE( 4, "The seller fails to fulfill his promise","卖家承诺不履行"),
        OTHER( 0, "other","其他"),
        ;

        //投诉类型
        private int type;
        private String description;
        private String cn_des;

        ComplaintTypeEnum(int type, String description, String cn_des) {
            this.type = type;
            this.description = description;
            this.cn_des = cn_des;
        }

        public static ComplaintTypeEnum typeOf(int type) {

            ComplaintTypeEnum values[] = ComplaintTypeEnum.values();
            for (ComplaintTypeEnum one : values) {
                if (one.getType()==type) {
                    return one;
                }
            }
            return null;
        }

        public int getType() {
            return type;
        }

        public String getDescription() {
            return description;
        }

        public String getCn_des() {
            return cn_des;
        }
    }

}
