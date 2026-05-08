package com.qinyoucheng.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qinyoucheng.auth.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {

    void batchInsert(@Param("list") List<SysRoleMenu> list);

    void deleteByRoleId(@Param("roleId") Long roleId);
}
