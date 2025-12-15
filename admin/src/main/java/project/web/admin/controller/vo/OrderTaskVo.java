package project.web.admin.controller.vo;

import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: code
 * @BelongsPackage: project.web.admin.controller.vo
 * @Author: tangpeng
 * @CreateTime: 2024-11-05  22:50
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class OrderTaskVo {
    protected static final long serialVersionUID = 1L;
    private String partyId;
    private String addressId;
    private Integer orderMode;
    private String datePicker;
    private List<ItemReq> order;
    private String createUser;
    private Integer orderCount;


}
