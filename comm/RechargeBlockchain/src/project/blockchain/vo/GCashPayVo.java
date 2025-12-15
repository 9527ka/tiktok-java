package project.blockchain.vo;

import lombok.Data;

import java.util.Map;

@Data
public class GCashPayVo {
    /**
     * 通讯标识
     */
    private Integer code;
    /**
     * 信息
     */
    private String msg;

    /**
     * 数据
     */
    private Map<String, Object> data;
}
