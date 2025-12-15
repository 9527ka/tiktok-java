package project.mall.goods.dto;

import java.util.List;

public class SellerTopNListDto implements java.io.Serializable {
    private long time;

    private String type;

    private String timeRangeKey;

    private List<SellerTopNDto> list;

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTimeRangeKey() {
        return timeRangeKey;
    }

    public void setTimeRangeKey(String timeRangeKey) {
        this.timeRangeKey = timeRangeKey;
    }

    public List<SellerTopNDto> getList() {
        return list;
    }

    public void setList(List<SellerTopNDto> list) {
        this.list = list;
    }
}
