package project.web.admin.dto;

import lombok.Data;

@Data
public class LotteryQueryDto {

    private String name;

    private Integer status;

    private String startTime;

    private String endTime;
}
