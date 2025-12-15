package project.mall.notification.utils.notify;

/**
 * 通知业务类型
 */
public enum NotificationBizTypeEnum {
    DEMO("demo", 3,"测试"),
    INBOX_CUSTOME_MESSAGE("inbox_custome_message", 3, "站内信-自定义通知"),
    INBOX_SELLER_CREDIT_UPDATED("inbox_seller_credit_updated", 3, "站内信-商家信誉分变更通知"),
    INBOX_FREEZE_SELLER_MONEY("inbox_freeze_seller_money", 3, "站内信-冻结商家资金通知"),
    INBOX_UNFREEZE_SELLER_MONEY("inbox_unfreeze_seller_money", 3, "站内信-解冻商家资金通知"),
    INBOX_NEW_ORDER_SUBMITTED("inbox_new_order_submitted", 3, "站内信-用户新建订单通知"),
    INBOX_FINISH_ORDER("inbox_finish_order", 3, "站内信-订单完成事件通知"),
    INBOX_REPLY_BUYER_IM("inbox_reply_buyer_im", 3, "站内信-尽快回复IM中的买家咨询通知"),
    INBOX_ORDER_SHIPPED_OVERTIME("inbox_order_shipped_overtime", 3, "站内信-订单超时未发货提醒"),
    INBOX_ORDER_PURCHASED_OVERTIME("inbox_order_purchased_overtime", 3, "站内信-订单逾期未采购提醒"),
    INBOX_RECHARGE_SUCCESS("inbox_recharge_success", 3, "站内信-充值审核通过提醒"),
    INBOX_WITHDRAW_SUCCESS("inbox_withdraw_success", 3, "站内信-提现成功提醒"),
    INBOX_STORE_AUDIT_SUCCESS("inbox_store_audit_success", 3, "站内信-店铺审核通过提醒"),
    INBOX_STORE_AUDIT_FAIL("inbox_store_audit_fail", 3, "站内信-店铺审核失败提醒"),
    INBOX_STORE_COMPLIANT_SUCCESS("inbox_store_compliant_success", 3, "站内信-店铺被投诉审核通过提醒"),
    INBOX_BUYER_COMPLIANT_SUCCESS("inbox_buyer_compliant_success", 3, "站内信-买家投诉审核通过提醒"),
    INBOX_BUYER_COMPLIANT_FAIL("inbox_buyer_compliant_audit_fail", 3, "站内信-买家投诉审核通过提醒"),

    EMAIL_CUSTOME_MESSAGE("email_custome_message", 2, "邮件-自定义通知"),
    EMAIL_SELLER_CREDIT_UPDATED("email_seller_credit_updated", 2, "邮件-商家信誉分变更通知"),
    EMAIL_FREEZE_SELLER_MONEY("email_freeze_seller_money", 2, "邮件-冻结商家资金通知"),
    EMAIL_UNFREEZE_SELLER_MONEY("email_unfreeze_seller_money", 2, "邮件-解冻商家资金通知"),
    EMAIL_NEW_ORDER_SUBMITTED("email_new_order_submitted", 2, "邮件-用户新建订单通知"),
    EMAIL_FINISH_ORDER("email_finish_order", 2, "邮件-订单完成事件通知"),
    EMAIL_REPLY_BUYER_IM("email_reply_buyer_im", 2, "邮件-尽快回复IM中的买家咨询通知"),
    EMAIL_ORDER_SHIPPED_OVERTIME("email_order_shipped_overtime", 2, "邮件-订单超时未发货提醒"),
    EMAIL_ORDER_PURCHASED_OVERTIME("email_order_purchased_overtime", 2, "邮件-订单逾期未采购提醒"),
    EMAIL_SEND_VERIFICATION_CODE("email_send_verification_code", 2, "邮件-发送邮箱验证码"),
    EMAIL_RECHARGE_SUCCESS("email_recharge_success", 2, "邮件-充值审核通过提醒"),
    EMAIL_WITHDRAW_SUCCESS("email_withdraw_success", 2, "邮件-提现成功提醒"),
    EMAIL_STORE_AUDIT_SUCCESS("email_store_audit_success", 2, "邮件-店铺审核通过提醒"),
    EMAIL_STORE_AUDIT_FAIL("email_store_audit_fail", 2, "邮件-店铺审核失败提醒"),
    EMAIL_STORE_COMPLIANT_SUCCESS("email_store_compliant_success", 2, "邮件-店铺被投诉审核通过提醒"),
    EMAIL_BUYER_COMPLIANT_SUCCESS("email_buyer_compliant_success", 2, "邮件-买家投诉审核通过提醒"),
    EMAIL_BUYER_COMPLIANT_FAIL("email_buyer_compliant_fail", 2, "邮件-买家投诉审核通过提醒"),

    SMS_SELLER_CREDIT_UPDATED("sms_seller_credit_updated", 1, "短信-商家信誉分变更通知"),
    SMS_FREEZE_SELLER_MONEY("sms_freeze_seller_money", 1, "短信-冻结商家资金通知"),
    SMS_UNFREEZE_SELLER_MONEY("sms_unfreeze_seller_money", 1, "短信-解冻商家资金通知"),
    SMS_NEW_ORDER_SUBMITTED("sms_new_order_submitted", 1, "短信-用户新建订单通知"),
    SMS_FINISH_ORDER("sms_finish_order", 1, "短信-订单完成事件通知"),
    SMS_REPLY_BUYER_IM("sms_reply_buyer_im", 1, "短信-尽快回复IM中的买家咨询通知"),
    SMS_ORDER_SHIPPED_OVERTIME("sms_order_shipped_overtime", 1, "短信-订单超时未发货提醒"),
    SMS_ORDER_PURCHASED_OVERTIME("sms_order_purchased_overtime", 1, "短信-订单逾期未采购提醒"),
    SMS_SEND_VERIFICATION_CODE("sms_send_verification_code", 1, "短信-发送短信验证码"),
    SMS_RECHARGE_SUCCESS("sms_recharge_success", 1, "短信-充值审核通过提醒"),
    SMS_WITHDRAW_SUCCESS("sms_withdraw_success", 1, "短信-提现成功提醒"),
    SMS_STORE_AUDIT_SUCCESS("sms_store_audit_success", 1, "短信-店铺审核通过提醒"),
    SMS_STORE_AUDIT_FAIL("sms_store_audit_fail", 1, "短信-店铺审核失败提醒"),
    SMS_STORE_COMPLIANT_SUCCESS("sms_store_compliant_success", 1, "短信-店铺被投诉审核通过提醒"),
    SMS_BUYER_COMPLIANT_SUCCESS("sms_buyer_compliant_success", 1, "短信-买家投诉审核通过提醒"),
    SMS_BUYER_COMPLIANT_FAIL("sms_buyer_compliant_fail", 1, "短信-买家投诉审核通过提醒"),
    INBOX_BONUS_SUCCESS("inbox_bonus_success", 3, "站内信-赠送彩金提醒商家"),
    INBOX_RECHARGE_AUDIT_FAIL("inbox_recharge_audit_fail", 3, "站内信-充值审核驳回提醒"),
    INBOX_WITHDRAWAL_AUDIT_FAIL("inbox_withdrawal_audit_fail", 3, "站内信-提现审核驳回提醒");
	;

    private String bizType;
    // 1-短信，2-email, 3-站内信
    private int type;
    private String description;


    private NotificationBizTypeEnum(String bizType, int type, String description) {
    	this.bizType = bizType;
    	this.description = description;
    }

    public static NotificationBizTypeEnum bizTypeOf(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }

		NotificationBizTypeEnum values[] = NotificationBizTypeEnum.values();
        for (NotificationBizTypeEnum one : values) {
            if (one.getBizType().equals(code)) {
                return one;
            }
        }

        return null;
    }

	public String getBizType() {
		return bizType;
	}

    public int getType() {
        return type;
    }

    public String getDescription() {
		return description;
	}
}
