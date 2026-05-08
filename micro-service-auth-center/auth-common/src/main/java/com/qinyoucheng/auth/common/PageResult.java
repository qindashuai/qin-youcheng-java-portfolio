package com.qinyoucheng.auth.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long total;
    private Integer pageNum;
    private Integer pageSize;
    private Integer totalPages;
    private List<T> records;

    public PageResult() {
    }

    public PageResult(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        this.total = total;
        this.pageNum = pageNum;
        this.pageSize = pageSize;
        this.totalPages = (int) Math.ceil((double) total / pageSize);
        this.records = records;
    }

    public static <T> PageResult<T> of(Long total, Integer pageNum, Integer pageSize, List<T> records) {
        return new PageResult<>(total, pageNum, pageSize, records);
    }
}
