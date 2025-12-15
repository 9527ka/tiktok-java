package project.web.api.dto;

import lombok.Data;

@Data
public class CountTypeDto {

    /**
     * 有图
     */
    private  String havePicture="0";


    /**
     * 好评
     */
    private  String positiveComments="0";


    /**
     * 中评
     */
    private  String mediumReview="0";

    /**
     * 差评
     */
    private  String negativeComment="0";
}
