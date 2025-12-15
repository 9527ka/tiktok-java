package project.mall.goods.model;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationVo extends Evaluation {
    // 用户名称
//    private String partyName;
    // 头像
    private String avatar;

    private String createTimeStr;

    private String commentTime;

    private String countryName;

    private List<GoodsAttributeVo> attributes;
}
