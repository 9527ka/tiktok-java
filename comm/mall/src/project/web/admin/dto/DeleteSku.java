package project.web.admin.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class DeleteSku implements Serializable {

    private String goodsId;

    private String skuId;

}
