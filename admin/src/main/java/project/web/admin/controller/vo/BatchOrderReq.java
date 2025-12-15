package project.web.admin.controller.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * @BelongsProject: code
 * @BelongsPackage: project.web.admin.controller.vo
 * @Author: tangpeng
 * @CreateTime: 2024-11-05  23:05
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class BatchOrderReq {
    private static final long serialVersionUID = -82328037365315952L;
    private List<String> partyIds;
    private List<String> items;
    private List<String> skuIds;
    private String[] datetime;
    private int[] time_limit;
    private BigDecimal[] price_limit;
    private int[] item_limit;
    private BigDecimal total_amount;
    private int total_count;
    private String createUser;
}
