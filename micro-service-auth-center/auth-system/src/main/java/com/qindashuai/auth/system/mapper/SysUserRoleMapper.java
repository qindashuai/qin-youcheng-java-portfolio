package com.qindashuai.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qindashuai.auth.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {

    void batchInsert(@Param("list") List<SysUserRole> list);

    void deleteByUserId(@Param("userId") Long userId);

    void deleteByRoleId(@Param("roleId") Long roleId);
}
