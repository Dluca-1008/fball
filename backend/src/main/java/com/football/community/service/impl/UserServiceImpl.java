package com.football.community.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.football.community.dto.LoginDto;
import com.football.community.dto.RegisterDto;
import com.football.community.dto.ChangePasswordDto;
import com.football.community.entity.User;
import com.football.community.exception.BusinessException;
import com.football.community.repository.UserMapper;
import com.football.community.repository.RoleMapper;

import java.util.ArrayList;
import com.football.community.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@Slf4j
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleMapper roleMapper;

    @Override
    public User findByUsername(String username) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        User user = getOne(wrapper);

        if (user != null) {
            Set<String> permissions = getUserPermissions(user.getId());
            user.setPermissions(permissions);

            List<String> roles = getUserRoles(user.getId());
            user.setRoles(new HashSet<>(roleMapper.selectRolesByUserId(user.getId())));
        }

        return user;
    }

    @Override
    @Transactional
    public User register(RegisterDto dto) {
        User existing = findByUsername(dto.getUsername());
        if (existing != null) {
            throw new BusinessException("用户名已存在");
        }

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setNickname(dto.getNickname());
        user.setStatus(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        save(user);

        roleMapper.assignRole(user.getId(), 2L);

        return user;
    }

    @Override
    public User login(LoginDto dto) {
        User user = findByUsername(dto.getUsername());
        if (user == null) {
            log.error("登录失败: 用户不存在 - {}", dto.getUsername());
            throw new BusinessException("用户不存在");
        }

        boolean matches = passwordEncoder.matches(dto.getPassword(), user.getPassword());
        log.info("登录验证 - 用户名: {}, 密码匹配: {}", dto.getUsername(), matches);

        if (!matches) {
            log.error("登录失败: 密码错误 - 用户名: {}", dto.getUsername());
            throw new BusinessException("密码错误");
        }

        if (user.getStatus() != 1) {
            log.error("登录失败: 账号已被禁用 - 用户名: {}", dto.getUsername());
            throw new BusinessException("账号已被禁用");
        }

        user.setLastLoginTime(LocalDateTime.now());
        updateById(user);

        log.info("登录成功: 用户名: {}, ID: {}", dto.getUsername(), user.getId());
        return user;
    }

    @Override
    public Set<String> getUserPermissions(Long userId) {
        return baseMapper.selectPermissionsByUserId(userId);
    }

    @Override
    public List<String> getUserRoles(Long userId) {
        return new ArrayList<>(baseMapper.selectRoleCodesByUserId(userId));
    }

    @Override
    public IPage<User> getUserList(int page, int size, String keyword) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (keyword != null && !keyword.isEmpty()) {
            wrapper.like(User::getUsername, keyword)
                   .or().like(User::getNickname, keyword)
                   .or().like(User::getEmail, keyword);
        }
        wrapper.orderByDesc(User::getCreatedAt);
        return page(new Page<>(page, size), wrapper);
    }

    @Override
    public User updateUserStatus(Long userId, Integer status) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        user.setStatus(status);
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
        return user;
    }

    @Override
    @Transactional
    public void assignRoles(Long userId, List<Long> roleIds) {
        roleMapper.deleteRolesByUserId(userId);
        for (Long roleId : roleIds) {
            roleMapper.assignRole(userId, roleId);
        }
    }

    @Override
    public void deleteUser(Long userId) {
        User user = getById(userId);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        removeById(userId);
    }

    @Override
    public void changePassword(String username, ChangePasswordDto dto) {
        User user = findByUsername(username);
        if (user == null) {
            throw new BusinessException("用户不存在");
        }
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BusinessException("旧密码错误");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        user.setUpdatedAt(LocalDateTime.now());
        updateById(user);
    }
}
