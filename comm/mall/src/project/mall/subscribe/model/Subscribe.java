package project.mall.subscribe.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class Subscribe extends EntityObject<String> {

    private static final long serialVersionUID = -2636900219962035359L;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 创建时间
     */
    private Date createTime;
}
