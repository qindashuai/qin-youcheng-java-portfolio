package com.qindashuai.auth.system.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qindashuai.auth.common.BusinessException;
import com.qindashuai.auth.common.PageResult;
import com.qindashuai.auth.common.ResultCode;
import com.qindashuai.auth.system.dto.UserDTO;
import com.qindashuai.auth.system.entity.SysUser;
import com.qindashuai.auth.system.entity.SysUserRole;
import com.qindashuai.auth.system.mapper.SysUserMapper;
import com.qindashuai.auth.system.mapper.SysUserRoleMapper;
import com.qindashuai.auth.system.service.UserService;
import com.qindashuai.auth.system.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    @Resource
    private SysUserMapper userMapper;

    @Resource
    private SysUserRoleMapper userRoleMapper;

    @Override
    public PageResult<UserVO> listUsers(String username, Integer status, String appKey, Integer pageNum, Integer pageSize) {
        Page<SysUser> page = new Page<>(pageNum, pageSize);
        IPage<SysUser> userPage = userMapper.selectUserPage(page, username, status, appKey);
        List<UserVO> voList = userPage.getRecords().stream().map(this::convertToVO).collect(Collectors.toList());
        return PageResult.of(userPage.getTotal(), pageNum, pageSize, voList);
    }

    @Override
    public UserVO getUserById(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        UserVO vo = convertToVO(user);
        vo.setRoleIds(userMapper.selectRoleIdsByUserId(id));
        return vo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO createUser(UserDTO userDTO) {
        SysUser existing = userMapper.selectByUsername(userDTO.getUsername(), userDTO.getAppKey());
        if (existing != null) {
            throw new BusinessException(ResultCode.USER_ALREADY_EXISTS);
        }

        SysUser user = new SysUser();
        BeanUtils.copyProperties(userDTO, user);
        user.setPassword(BCrypt.hashpw(userDTO.getPassword()));
        user.setStatus(userDTO.getStatus() != null ? userDTO.getStatus() : 1);
        userMapper.insert(user);

        if (userDTO.getRoleIds() != null && !userDTO.getRoleIds().isEmpty()) {
            assignRoles(user.getId(), userDTO.getRoleIds());
        }

        log.info("创建用户: userId={}, username={}", user.getId(), user.getUsername());
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVO updateUser(Long id, UserDTO userDTO) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }

        if (userDTO.getNickname() != null) user.setNickname(userDTO.getNickname());
        if (userDTO.getEmail() != null) user.setEmail(userDTO.getEmail());
        if (userDTO.getPhone() != null) user.setPhone(userDTO.getPhone());
        if (userDTO.getAvatar() != null) user.setAvatar(userDTO.getAvatar());
        if (userDTO.getStatus() != null) user.setStatus(userDTO.getStatus());
        if (userDTO.getDeptId() != null) user.setDeptId(userDTO.getDeptId());

        userMapper.updateById(user);

        if (userDTO.getRoleIds() != null) {
            assignRoles(id, userDTO.getRoleIds());
        }

        log.info("更新用户: userId={}", id);
        return convertToVO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long id) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        userMapper.deleteById(id);
        userRoleMapper.deleteByUserId(id);
        log.info("删除用户: userId={}", id);
    }

    @Override
    public void resetPassword(Long id, String newPassword) {
        SysUser user = userMapper.selectById(id);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        user.setPassword(BCrypt.hashpw(newPassword));
        userMapper.updateById(user);
        log.info("重置用户密码: userId={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.deleteByUserId(userId);
        if (roleIds != null && !roleIds.isEmpty()) {
            List<SysUserRole> userRoles = new ArrayList<>();
            for (Long roleId : roleIds) {
                SysUserRole userRole = new SysUserRole();
                userRole.setUserId(userId);
                userRole.setRoleId(roleId);
                userRoles.add(userRole);
            }
            userRoleMapper.batchInsert(userRoles);
        }
        log.info("分配角色: userId={}, roleIds={}", userId, roleIds);
    }

    @Override
    public UserVO getUserByUsername(String username, String appKey) {
        SysUser user = userMapper.selectByUsername(username, appKey);
        if (user == null) {
            throw new BusinessException(ResultCode.USER_NOT_FOUND);
        }
        return convertToVO(user);
    }

    private UserVO convertToVO(SysUser user) {
        UserVO vo = new UserVO();
        BeanUtils.copyProperties(user, vo);
        return vo;
    }
}
