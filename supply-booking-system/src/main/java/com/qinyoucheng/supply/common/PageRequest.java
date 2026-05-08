package com.qinyoucheng.supply.common;

import lombok.Data;

import javax.validation.constraints.Min;

@Data
public class PageRequest {

    @Min(value = 1, message = "页码不能小于1")
    private int pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    private int pageSize = 10;

    public int getOffset() {
        return (pageNum - 1) * pageSize;
    }
}
