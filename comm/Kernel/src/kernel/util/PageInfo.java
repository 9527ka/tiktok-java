package kernel.util;

import kernel.web.Page;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class PageInfo implements Serializable {

    private static final long serialVersionUID = 1L;
    private List elements = new ArrayList<>();
    private static final int DEFAULT_PAGE_SIZE = 10;

    private int pageSize = DEFAULT_PAGE_SIZE;

    private int pageNum = 1;

    private int totalElements = 0;

    public static final Page EMPTY_PAGE = new Page();

    public PageInfo() {
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public void setPageNum(int pageNum) {
        if (pageNum > 0) {
            this.pageNum = pageNum;
        }
    }

    public PageInfo(int pageNum, int pageSize, int totalElements) {
        if (pageNum > 0) {
            this.pageNum = pageNum;
        }
        if (pageSize > 0) {
            this.pageSize = pageSize;
        }
        if (totalElements > 0)
            this.totalElements = totalElements;
    }

    public boolean isFirstPage() {
        return getPageNum() == 1;
    }

    public boolean isLastPage() {
        return getPageNum() >= getTotalPage();
    }

    public boolean hasNextPage() {
        return getTotalPage() > getPageNum();
    }

    public boolean hasPreviousPage() {
        return getPageNum() > 1;
    }

    public int getTotalPage() {
        return this.totalElements % this.pageSize == 0 ? this.totalElements / this.pageSize
                : this.totalElements / this.pageSize + 1;
    }

    public List getElements() {
        return this.elements;
    }

    public void setElements(List elements) {
        this.elements = elements;
    }

    public int getTotalElements() {
        return this.totalElements;
    }

    public void setTotalElements(int totalElements) {
        if (totalElements > 0) {
            this.totalElements = totalElements;
        }
        if (this.pageNum > getTotalPage())
            this.pageNum = getTotalPage();
    }

    public int getFirstElementNumber() {
        return (getPageNum() - 1) * getPageSize();
    }

    public int getLastElementNumber() {
        int fullPage = getFirstElementNumber() + getPageSize() - 1;
        return getTotalElements() < fullPage ? getTotalElements() : fullPage;
    }

    public int getNextPageNumber() {
        return getPageNum() + 1;
    }

    public int getPreviousPageNumber() {
        return getPageNum() - 1;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getPageNum() {
        return this.pageNum;
    }

    public int getFirstPage() {
        return 1;
    }
}
