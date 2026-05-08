package com.qinyoucheng.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.auth.system.entity.SysOperationLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysOperationLogMapper extends BaseMapper<SysOperationLog> {

    IPage<SysOperationLog> selectLogPage(Page<SysOperationLog> page,
                                           @Param("username") String username,
                                           @Param("operationType") String operationType,
                                           @Param("appKey") String appKey);
}
