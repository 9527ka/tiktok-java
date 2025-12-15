package project.mall.seller.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
public class MallLevelCondExpr {

    private List<Param> params;

    /**
     * 示例："#popularizeUserCount >= 10 && (#rechargeAmount >= 5000 || #sellAmount > 5000)"
     */
    private String expression;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Param {
        /**
         * 参数类型参考枚举类型： UpgradeMallLevelCondParamTypeEnum
         */
        private String code;

        private String title;

        private String value;

        // value 类型，参考枚举类型：ValueTypeEnum 的 code 值
        //private int valueType;

    }

}
