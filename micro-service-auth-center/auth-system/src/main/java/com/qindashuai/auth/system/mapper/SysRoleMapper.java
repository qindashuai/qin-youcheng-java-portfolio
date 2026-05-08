package com.qindashuai.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.auth.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMapper extends BaseMapper<SysRole> {

    IPage<SysRole> selectRolePage(Page<SysRole> page, @Param("roleName") String roleName,
                                   @Param("status") Integer status, @Param("appKey") String appKey);

    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    boolean hasUsersByRoleId(@Param("roleId") Long roleId);
}
