package project.mall.area.model;

import kernel.bo.EntityObject;
import lombok.Data;

@Data
public class MobilePrefix extends EntityObject<String> {
    private static final long serialVersionUID = -2728993235001568570L;

    private String country;

    private String mobilePrefix;
}
