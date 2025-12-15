package project.web.admin.controller.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ItemReq {
    private static final long serialVersionUID = -4900213401019956641L;
    private String itemId;
    private Integer count;
    private BigDecimal price;
}
