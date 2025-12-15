package project.mall.seller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

public class SellerOrderLineDTO implements Serializable {
    // caster2023 注释掉，因为有的场景返回的是小时字符串 , 不设置timezone，取服务器设置的默认时区
    // @JsonFormat(pattern = "yyyy-MM-dd")
    private String dayString;

    private String countSales = "0";

    private String countVisits = "0";

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public String getCountSales() {
        return countSales;
    }

    public void setCountSales(String countSales) {
        this.countSales = countSales;
    }

    public String getCountVisits() {
        return countVisits;
    }

    public void setCountVisits(String countVisits) {
        this.countVisits = countVisits;
    }
}
