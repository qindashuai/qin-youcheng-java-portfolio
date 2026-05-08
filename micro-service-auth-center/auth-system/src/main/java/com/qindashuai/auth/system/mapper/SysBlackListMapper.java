package com.qindashuai.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.auth.system.entity.SysBlackList;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysBlackListMapper extends BaseMapper<SysBlackList> {

    IPage<SysBlackList> selectPageWithCondition(Page<SysBlackList> page,
                                                  @Param("targetType") Integer targetType,
                                                  @Param("appKey") String appKey);
}
