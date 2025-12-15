package project.mall.activity.core.vo;

import lombok.Data;

@Data
public class ActivityParam {
    /**
     * 参数类型参考枚举类型， 例如：FruitDialActivityJoinRuleAttrEnum
     * 关联枚举类型后，方便业务代码做判断调用
     */
    private String code;

    // 对外展示的参数标题
    private String title;

    // 活动实例使用，对应参数值
    private String value;

    // 参数说明，可用于支持前端展示
    private String description;
}
