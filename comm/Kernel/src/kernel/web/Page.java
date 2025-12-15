package kernel.web;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Page implements Serializable {
	private static final long serialVersionUID = 1L;
	private List elements = new ArrayList();

	private int pageSize = 10;
	public static final int DEFAULT_PAGE_SIZE = 10;
	private int thisPageNumber = 1;

	private int totalElements = 0;

	public static final Page EMPTY_PAGE = new Page();

	public Page() {
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public void setThisPageNumber(int thisPageNumber) {
		if (thisPageNumber > 0) {
			this.thisPageNumber = thisPageNumber;
		}
		if (this.thisPageNumber > getTotalPage())
			this.thisPageNumber = getTotalPage();
	}

	public Page(int thisPageNumber, int pageSize, int totalElements) {
		if (thisPageNumber > 0) {
			this.thisPageNumber = thisPageNumber;
		}
		if (pageSize > 0) {
			this.pageSize = pageSize;
		}
		if (totalElements >= 0)
			this.totalElements = totalElements;
	}

	public boolean isFirstPage() {
		return getThisPageNumber() == 1;
	}

	public boolean isLastPage() {
		return getThisPageNumber() >= getTotalPage();
	}

	public boolean hasNextPage() {
		return getTotalPage() > getThisPageNumber();
	}

	public boolean hasPreviousPage() {
		return getThisPageNumber() > 1;
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
		if (totalElements >= 0) {
			this.totalElements = totalElements;
		}
		if (this.thisPageNumber > getTotalPage())
			this.thisPageNumber = getTotalPage();
	}

	public int getFirstElementNumber() {
		return (getThisPageNumber() - 1) * getPageSize();
	}

	public int getLastElementNumber() {
		int fullPage = getFirstElementNumber() + getPageSize() - 1;
		return getTotalElements() < fullPage ? getTotalElements() : fullPage;
	}

	public int getNextPageNumber() {
		return getThisPageNumber() + 1;
	}

	public int getPreviousPageNumber() {
		return getThisPageNumber() - 1;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public int getThisPageNumber() {
		return this.thisPageNumber;
	}

	public int getFirstPage() {
		return 1;
	}
}
