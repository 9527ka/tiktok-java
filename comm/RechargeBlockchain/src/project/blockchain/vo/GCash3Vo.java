package project.blockchain.vo;

import lombok.Data;

import java.util.Map;

@Data
public class GCash3Vo {
    /**
     * 状态码 成功.200 失败.400
     */
    private Integer code;
    /**
     * 状态信息
     */
    private String msg;
    /**
     * 返回数据
     */
    private Map<String, Object> data;
}
