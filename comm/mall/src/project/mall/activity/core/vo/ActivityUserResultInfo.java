package project.mall.activity.core.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ActivityUserResultInfo {
    private String refId;

    private int refType;

    private Map<String, Object> extraInfo;

}
