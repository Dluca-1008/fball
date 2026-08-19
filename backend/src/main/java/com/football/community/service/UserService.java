package com.football.community.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.football.community.entity.User;
import com.football.community.dto.RegisterDto;
import com.football.community.dto.LoginDto;
import com.football.community.dto.ChangePasswordDto;
import com.football.community.dto.UpdateUserInfoDto;

import java.util.List;
import java.util.Set;

/**
 * 用户服务接口。
 * <p>提供用户注册、登录、权限管理等业务逻辑。</p>
 */
public interface UserService extends IService<User> {

    /**
     * 根据用户名查找用户。
     * @param username 用户名
     * @return 用户实体，未找到返回 null
     */
    User findByUsername(String username);

    /**
     * 注册新用户。
     * @param dto 注册信息
     * @return 创建后的用户实体
     */
    User register(RegisterDto dto);

    /**
     * 用户登录。
     * @param dto 登录信息
     * @return 用户实体
     */
    User login(LoginDto dto);

    /**
     * 获取用户权限集合。
     * @param userId 用户ID
     * @return 权限字符串集合
     */
    Set<String> getUserPermissions(Long userId);

    /**
     * 获取用户角色列表。
     * @param userId 用户ID
     * @return 角色名称列表
     */
    List<String> getUserRoles(Long userId);

    /**
     * 分页查询用户列表。
     * @param page 页码
     * @param size 每页大小
     * @param keyword 搜索关键词
     * @return 分页结果
     */
    IPage<User> getUserList(int page, int size, String keyword);

    /**
     * 更新用户状态。
     * @param userId 用户ID
     * @param status 新状态（0-禁用, 1-启用）
     * @return 更新后的用户实体
     */
    User updateUserStatus(Long userId, Integer status);

    /**
     * 为用户分配角色。
     * @param userId 用户ID
     * @param roleIds 角色ID列表
     */
    void assignRoles(Long userId, List<Long> roleIds);

    /**
     * 删除用户。
     * @param userId 用户ID
     */
    void deleteUser(Long userId);

    /**
     * 修改用户密码。
     * @param username 用户名
     * @param dto 密码修改信息
     */
    void changePassword(String username, ChangePasswordDto dto);

    /**
     * 更新用户个人资料。
     * @param userId 用户ID
     * @param dto 用户信息更新内容
     * @return 更新后的用户实体
     */
    User updateUserInfo(Long userId, UpdateUserInfoDto dto);
}
