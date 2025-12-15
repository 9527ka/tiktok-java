package project.mall.utils;

import cn.hutool.core.lang.generator.SnowflakeGenerator;
import cn.hutool.core.util.IdUtil;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

public class IdUtils {


    /**
     * 生成订单号（25位）：时间（精确到毫秒）+3位随机数+5位用户id
     */
    public static synchronized String getOrderNum() {
        //时间（精确到毫秒）
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String localDate = LocalDateTime.now().format(ofPattern);
        //5位用户id
        String orderNum = "SC" + localDate;
        return orderNum;
    }


    /**
     *
     */
    public static synchronized String getSellerGoodsId() {
        DateTimeFormatter ofPattern = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
        String localDate = LocalDateTime.now().format(ofPattern);
        return localDate.substring(2);
    }
    public static synchronized String getSellerGoodsSkuId() {
        long id = IdUtil.getSnowflakeNextId();
        return id+"";
    }

 
    
}
