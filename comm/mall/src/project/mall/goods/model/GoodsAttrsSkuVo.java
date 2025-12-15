package project.mall.goods.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统skuId和商品属性映射关系
 */
@Data
public class GoodsAttrsSkuVo {
    // 系统商品id
    private String systemGoodsId;
    private List<GoodsAttrsValuesVo> attrsValuesVos = new ArrayList<>();

    // key是属性值id，按字典序升序，用_拼接，value是skuId
    // 例如颜色id是红色的为a,内存16g为c，对应的skuId是sadsadsa,存入的是a_c->sadsadsa
    private Map<String, String> attrsIdSkuId = new HashMap<>();
}
