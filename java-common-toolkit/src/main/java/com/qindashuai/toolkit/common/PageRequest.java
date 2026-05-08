package com.qindashuai.toolkit.common;

import lombok.Data;

import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 500;

    private int pageNum = DEFAULT_PAGE_NUM;
    private int pageSize = DEFAULT_PAGE_SIZE;
    private String orderBy;
    private String orderDirection = "ASC";

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum < 1 ? DEFAULT_PAGE_NUM : pageNum;
    }

    public void setPageSize(int pageSize) {
        if (pageSize < 1) {
            this.pageSize = DEFAULT_PAGE_SIZE;
        } else if (pageSize > MAX_PAGE_SIZE) {
            this.pageSize = MAX_PAGE_SIZE;
        } else {
            this.pageSize = pageSize;
        }
    }

    public long getOffset() {
        return (long) (this.pageNum - 1) * this.pageSize;
    }
}
