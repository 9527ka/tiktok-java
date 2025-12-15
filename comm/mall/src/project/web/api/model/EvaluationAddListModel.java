package project.web.api.model;

import lombok.Data;

import java.util.List;

@Data
public class EvaluationAddListModel {

    private List<EvaluationAddModel> evaluationAdds;
    private  String orderId;

    @Data
    public static class  EvaluationAddModel{
       private String sellerGoodsId;
        private String evaluationType;
        private  String rating;
        private  String content;

        //图片1
        private String imgUrl1;

        private String imgUrl2;

        private String imgUrl3;

        private String imgUrl4;

        private String imgUrl5;

        private String imgUrl6;

        private String imgUrl7;

        private String imgUrl8;

        private String imgUrl9;

    }
}
