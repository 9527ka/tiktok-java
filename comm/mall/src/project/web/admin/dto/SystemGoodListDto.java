package project.web.admin.dto;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;


@Data
public class SystemGoodListDto {
    private  String id;

    private String goodName;

    private  String categoryName;

    private  String imgUrl1;
    private  Integer isShelf;


    private String categoryId;

    private String secondaryCategoryId;

    private String createTime;

    private Double systemPrice;

    private  Integer updateStatus;
}
