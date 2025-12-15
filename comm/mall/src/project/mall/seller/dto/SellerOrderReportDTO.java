package project.mall.seller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.math.BigDecimal;

public class SellerOrderReportDTO implements Serializable {

//    @JsonFormat(pattern = "yyyy-MM-dd", timezone = "GMT+8") 取服务器设置的默认时区，不显式设置
    @JsonFormat(pattern = "yyyy-MM-dd")
    private String dayString;

    private Long orderNum;

    private Long orderCancel;
    private Long orderReturns;

    private BigDecimal totalProfit;
    private BigDecimal totalSales;

    @Override
    public String toString() {
        return "SellerOrderReportDTO{" +
                "dayString='" + dayString + '\'' +
                ", orderNum=" + orderNum +
                ", orderCancel=" + orderCancel +
                ", orderReturns=" + orderReturns +
                ", totalProfit=" + totalProfit +
                ", totalSales=" + totalSales +
                '}';
    }

    public String getDayString() {
        return dayString;
    }

    public void setDayString(String dayString) {
        this.dayString = dayString;
    }

    public Long getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Long orderNum) {
        this.orderNum = orderNum;
    }

    public Long getOrderCancel() {
        return orderCancel;
    }

    public void setOrderCancel(Long orderCancel) {
        this.orderCancel = orderCancel;
    }

    public Long getOrderReturns() {
        return orderReturns;
    }

    public void setOrderReturns(Long orderReturns) {
        this.orderReturns = orderReturns;
    }

    public BigDecimal getTotalProfit() {
        return totalProfit;
    }

    public void setTotalProfit(BigDecimal totalProfit) {
        this.totalProfit = totalProfit;
    }

    public BigDecimal getTotalSales() {
        return totalSales;
    }

    public void setTotalSales(BigDecimal totalSales) {
        this.totalSales = totalSales;
    }
}
