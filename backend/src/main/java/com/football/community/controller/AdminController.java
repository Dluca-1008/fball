package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Role;
import com.football.community.entity.User;
import com.football.community.repository.RoleMapper;
import com.football.community.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleMapper roleMapper;

    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<List<Role>> getRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return Result.success(roles);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<IPage<User>> getUsers(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword) {
        IPage<User> users = userService.getUserList(page, size, keyword);
        users.getRecords().forEach(user -> {
            user.setPassword(null);
            List<Role> userRoles = roleMapper.selectRolesByUserId(user.getId());
            user.setRoles(new java.util.HashSet<>(userRoles));
        });
        return Result.success(users);
    }

    @GetMapping("/users/{id}")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<User> getUser(@PathVariable Long id) {
        User user = userService.getById(id);
        if (user != null) {
            user.setPassword(null);
            List<Role> userRoles = roleMapper.selectRolesByUserId(id);
            user.setRoles(new java.util.HashSet<>(userRoles));
        }
        return Result.success(user);
    }

    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<User> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userService.updateUserStatus(id, status);
        user.setPassword(null);
        return Result.success(user);
    }

    @PostMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<?> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }

    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
