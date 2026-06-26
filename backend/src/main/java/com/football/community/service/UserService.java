package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.User;
import com.football.community.dto.RegisterDto;
import com.football.community.dto.LoginDto;
import com.football.community.dto.ChangePasswordDto;

import java.util.List;
import java.util.Set;

public interface UserService extends IService<User> {

    User findByUsername(String username);

    User register(RegisterDto dto);

    User login(LoginDto dto);

    Set<String> getUserPermissions(Long userId);

    List<String> getUserRoles(Long userId);

    IPage<User> getUserList(int page, int size, String keyword);

    User updateUserStatus(Long userId, Integer status);

    void assignRoles(Long userId, List<Long> roleIds);

    void deleteUser(Long userId);

    void changePassword(String username, ChangePasswordDto dto);
}
