package com.qinyoucheng.toolkit.common;

import lombok.Data;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(long total, int pageNum, int pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.pages = pageSize > 0 ? (int) Math.ceil((double) total / pageSize) : 0;
        this.records = records;
    }

    public static <T> PageResult<T> of(long total, int pageNum, int pageSize, List<T> records) {
        return new PageResult<>(total, pageNum, pageSize, records);
    }

    public static <T> PageResult<T> empty(int pageNum, int pageSize) {
        return new PageResult<>(0, pageNum, pageSize, Collections.emptyList());
    }

    public static <T> PageResult<T> empty() {
        return new PageResult<>(0, PageRequest.DEFAULT_PAGE_NUM, PageRequest.DEFAULT_PAGE_SIZE, Collections.emptyList());
    }
}
