package project.web.admin.controller.vo;

import lombok.Data;

import java.io.Serializable;
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
public class OrderReq {
    private static final long serialVersionUID = 573145816239266985L;
    private String secret;
    private String partyId;
    private List<ItemReq> items;
    private String createUser;

}
