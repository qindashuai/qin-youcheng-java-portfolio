package com.qinyoucheng.rag.common;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PageRequest {

    @Min(value = 1, message = "页码必须大于0")
    private int pageNum = 1;

    @Min(value = 1, message = "每页数量必须大于0")
    private int pageSize = 10;

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
