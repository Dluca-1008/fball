package com.football.community.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.football.community.dto.Result;
import com.football.community.entity.Role;
import com.football.community.entity.User;
import com.football.community.repository.RoleMapper;
import com.football.community.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "管理员管理", description = "管理员操作接口")
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleMapper roleMapper;

    @Operation(summary = "获取角色列表", description = "获取系统中所有角色")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @GetMapping("/roles")
    @PreAuthorize("hasAuthority('user:view')")
    public Result<List<Role>> getRoles() {
        List<Role> roles = roleMapper.selectList(null);
        return Result.success(roles);
    }

    @Operation(summary = "分页查询用户", description = "根据关键词分页查询用户列表")
    @Parameters({
            @Parameter(name = "page", description = "页码", example = "1"),
            @Parameter(name = "size", description = "每页大小", example = "10"),
            @Parameter(name = "keyword", description = "搜索关键词")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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

    @Operation(summary = "获取用户详情", description = "根据ID获取用户详细信息")
    @Parameter(name = "id", description = "用户ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "查询成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "404", description = "用户不存在"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
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

    @Operation(summary = "更新用户状态", description = "启用或禁用指定用户")
    @Parameters({
            @Parameter(name = "id", description = "用户ID", example = "1"),
            @Parameter(name = "status", description = "用户状态(0-禁用,1-启用)")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PutMapping("/users/{id}/status")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<User> updateUserStatus(@PathVariable Long id, @RequestParam Integer status) {
        User user = userService.updateUserStatus(id, status);
        user.setPassword(null);
        return Result.success(user);
    }

    @Operation(summary = "分配角色", description = "为指定用户分配一个或多个角色")
    @Parameters({
            @Parameter(name = "id", description = "用户ID", example = "1"),
            @Parameter(description = "角色ID列表")
    })
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "分配成功"),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @PostMapping("/users/{id}/roles")
    @PreAuthorize("hasAuthority('user:edit')")
    public Result<?> assignRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.assignRoles(id, roleIds);
        return Result.success();
    }

    @Operation(summary = "删除用户", description = "根据ID删除指定用户")
    @Parameter(name = "id", description = "用户ID", example = "1")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "401", description = "未认证"),
            @ApiResponse(responseCode = "403", description = "无权限"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    @DeleteMapping("/users/{id}")
    @PreAuthorize("hasAuthority('user:delete')")
    public Result<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }
}
