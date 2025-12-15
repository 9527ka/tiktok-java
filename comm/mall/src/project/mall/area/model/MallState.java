package project.mall.area.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class MallState extends EntityObject<Long> {
    private static final long serialVersionUID = 8096944949372440876L;

    private String stateNameCn;

    private String stateNameEn;

    private String stateNameTw;
    private MallCountry mallCountry;
    private Long countryId;
    private Integer flag;
    //创建时间

    //创建时间
    private Date updatedAt;

   // private String updatedBy;

}
