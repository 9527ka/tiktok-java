package project.syspara;

import cn.hutool.core.util.StrUtil;

public enum SysParaCode {
    // 用户通过手机短信登录或者校验身份时使用
    SEND_CODE_TEXT("send_code_text", "短信验证码消息模板"),

    // 用户修改绑定邮箱时使用
    SEND_EMAIL_CODE_TEXT("send_email_code_text", "邮件验证码消息模板"),

    // 用户修改绑定邮箱时使用
    SEND_EMAIL_CODE_TITLE("send_email_code_title", "邮件验证码标题"),

    // 用户上架商品时校验充值指标
    ONSHELF_RECHARGE_AMOUNT_LIMIT("onshelf_recharge_amount_limit", "上架商品校验充值金额最低指标"),

    // 是否实时统计商品展示权重
    SYNC_REFRESH_SELLER_GOODS_SHOW_WEIGHT("sync_refresh_seller_goods_show_weight", "发生相关事件后是否同步更新商品展示权重"),

    // 几天内新上架的商品要被标记为推荐新品
    NEW_SELLER_GOODS_DAY_LIMIT("new_seller_goods_day_limit", "设置新商品时间限制"),

    // 商铺升级业务中的有效累计充值金额
    VALID_RECHARGE_AMOUNT_FOR_SELLER_UPGRADE("valid_recharge_amount_for_seller_upgrade", "店铺升级业务有效充值额度"),

    // 商铺升级业务中的计算团队人数的累计有效充值额度
    VALID_RECHARGE_AMOUNT_FOR_TEAM_NUM("valid_recharge_amount_for_team_num", "商铺升级业务中的计算团队人数的累计有效充值额度"),

    // 商城推广拉人活动，首次充值有效充值额度
    VALID_RECHARGE_AMOUNT_FOR_FIRST_RECHARGE_BONUS("valid_recharge_amount_for_first_recharge_bonus", "商城推广拉人活动首次充值赠送礼金有效充值额度"),

    // 商城订单佣金是否扣除卖家利润
    REBATE_IS_DEDUCTED("rebate_is_deducted", "返佣是否扣除卖家订单利润：0-不扣除,1-扣除"),

    // 商城卖家注册赠送礼金
    MALL_SELLER_SIGN_BONUS("mall_seller_sign_bonus", "商城卖家注册赠送礼金"),

    // 商城卖家注册赠送礼金，激活礼金比例
    MALL_SELLER_SIGN_BONUS_RATIO("mall_seller_sign_bonus_ratio", "商城卖家注册赠送礼金，激活礼金比例"),

    // 是否需要绑定提现地址，默认或无配置记录时为：不需要（false）
    MUST_BIND_WITHDRAW_ADDRESS("must_bind_withdraw_address", "是否需要绑定提现地址"),

    // 是否需要绑定提现地址，默认或无配置记录时为：不需要（false）
    RECHARGE_COMMISSION_RATE("recharge_commission_rate", "充值提成比例"),

    CLERK_IS_OPEN("clerk_is_open", "盘口是否显示业务员需求"),

    WITHDRAW_COMMISSION_RATE("withdraw_commission_rate", "提现提成扣除"),

    RECHARGE_IS_OPEN("recharge_is_open", "充值审核是否可以修改金额 0-不修改 1-修改"),

    GOODS_BUY_MIN("goods_buy_min", "全局商品起购数量"),

    ;


    private String code;

    private String description;

    private SysParaCode(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static SysParaCode codeOf(String inputCode) {
        if (StrUtil.isBlank(inputCode)) {
            return null;
        }

        SysParaCode[] values = SysParaCode.values();
        for (SysParaCode one : values) {
            if (one.getCode().equals(inputCode)) {
                return one;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
