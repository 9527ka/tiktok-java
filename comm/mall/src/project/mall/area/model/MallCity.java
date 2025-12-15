package project.mall.area.model;

import kernel.bo.EntityObject;
import lombok.Data;

import java.util.Date;

@Data
public class MallCity extends EntityObject<Long> {
    private static final long serialVersionUID = 8096944949372440876L;

    private String cityNameCn;

    private String cityNameEn;
    private String cityNameTw;
    private Long stateId;
    private MallState mallState;
    private MallCountry mallCountry;
    private Long countryId;
    private Integer flag;
    private Date updatedAt;

    // private String updatedBy;

}
