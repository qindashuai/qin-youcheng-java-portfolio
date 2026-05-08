package com.qindashuai.auth.common;

import lombok.Data;

import javax.validation.constraints.Min;
import java.io.Serializable;

@Data
public class PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    @Min(value = 1, message = "页码不能小于1")
    private Integer pageNum = 1;

    @Min(value = 1, message = "每页条数不能小于1")
    private Integer pageSize = 10;

    private String orderBy;

    private String orderDirection = "ASC";

    public long getOffset() {
        return (long) (pageNum - 1) * pageSize;
    }
}
