package com.qindashuai.auth.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.qindashuai.auth.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    List<SysMenu> selectMenuTree(@Param("appKey") String appKey);

    List<SysMenu> selectMenusByRoleId(@Param("roleId") Long roleId);

    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);
}
