package com.football.community.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.football.community.entity.Role;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RoleMapper extends BaseMapper<Role> {

    @Select("SELECT r.* FROM sys_role r " +
            "INNER JOIN sys_user_role ur ON r.id = ur.role_id " +
            "WHERE ur.user_id = #{userId} AND r.status = 1")
    List<Role> selectRolesByUserId(@Param("userId") Long userId);

    @Insert("INSERT INTO sys_user_role (user_id, role_id, created_at) VALUES (#{userId}, #{roleId}, NOW())")
    void assignRole(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Insert("INSERT IGNORE INTO sys_user_role (user_id, role_id, created_at) VALUES (#{userId}, #{roleId}, NOW())")
    void assignRoleIfNotExists(@Param("userId") Long userId, @Param("roleId") Long roleId);

    @Delete("DELETE FROM sys_user_role WHERE user_id = #{userId}")
    void deleteRolesByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM sys_role WHERE role_code = #{roleCode}")
    Role selectByCode(@Param("roleCode") String roleCode);

    @Insert("INSERT IGNORE INTO sys_role_permission (role_id, permission_id) VALUES (#{roleId}, #{permissionId})")
    void assignPermissionIfNotExists(@Param("roleId") Long roleId, @Param("permissionId") Long permissionId);
}
