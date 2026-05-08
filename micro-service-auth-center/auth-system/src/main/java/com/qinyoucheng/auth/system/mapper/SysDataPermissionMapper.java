package com.qinyoucheng.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.auth.system.entity.SysDataPermission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDataPermissionMapper extends BaseMapper<SysDataPermission> {

    IPage<SysDataPermission> selectPageWithDetail(Page<SysDataPermission> page,
                                                    @Param("roleId") Long roleId,
                                                    @Param("appKey") String appKey);

    SysDataPermission selectByRoleId(@Param("roleId") Long roleId, @Param("appKey") String appKey);
}
