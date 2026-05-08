package com.qinyoucheng.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qinyoucheng.auth.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserMapper extends BaseMapper<SysUser> {

    SysUser selectByUsername(@Param("username") String username, @Param("appKey") String appKey);

    IPage<SysUser> selectUserPage(Page<SysUser> page, @Param("username") String username,
                                   @Param("status") Integer status, @Param("appKey") String appKey);

    List<String> selectPermissionsByUserId(@Param("userId") Long userId);

    List<Long> selectRoleIdsByUserId(@Param("userId") Long userId);
}
