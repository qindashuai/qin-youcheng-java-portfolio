package com.qindashuai.rag.common;

import lombok.Data;

import java.util.List;

@Data
public class PageResult<T> {

    private long total;
    private int pageNum;
    private int pageSize;
    private int pages;
    private List<T> records;

    public PageResult() {}

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
}
