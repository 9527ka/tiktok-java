package project.mall.utils;

import java.util.List;

public class MallPageInfo {

    private int pageSize = 20;

    private int pageNum = 1;

    private int totalElements = 0;

    private List elements;

    public MallPageInfo(int pageSize, int pageNum, int totalElements, List elements) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.totalElements = totalElements;
        this.elements = elements;
    }

    public MallPageInfo(){

    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public List getElements() {
        return elements;
    }

    public void setElements(List elements) {
        this.elements = elements;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }
}
