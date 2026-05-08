package com.qinyoucheng.auth.system.service;

import com.qinyoucheng.auth.common.PageResult;
import com.qinyoucheng.auth.system.dto.UserDTO;
import com.qinyoucheng.auth.system.vo.UserVO;

import java.util.List;

public interface UserService {

    PageResult<UserVO> listUsers(String username, Integer status, String appKey, Integer pageNum, Integer pageSize);

    UserVO getUserById(Long id);

    UserVO createUser(UserDTO userDTO);

    UserVO updateUser(Long id, UserDTO userDTO);

    void deleteUser(Long id);

    void resetPassword(Long id, String newPassword);

    void assignRoles(Long userId, List<Long> roleIds);

    UserVO getUserByUsername(String username, String appKey);
}
