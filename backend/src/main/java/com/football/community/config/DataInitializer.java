package com.football.community.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.football.community.entity.Permission;
import com.football.community.repository.PermissionMapper;
import com.football.community.repository.RoleMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private PermissionMapper permissionMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        ensureTeamStatusColumn();
        ensurePermissionExists("解散球队", "team:dissolve", 6L, 2, 5);
        ensurePermissionExists("设置成员状态", "team_member:set_status", 12L, 2, 6);
    }

    private void ensureTeamStatusColumn() {
        try {
            jdbcTemplate.execute("ALTER TABLE teams ADD COLUMN status INT DEFAULT 1 AFTER created_by");
            jdbcTemplate.update("UPDATE teams SET status = 1 WHERE status IS NULL");
            log.info("已自动添加 teams.status 列");
        } catch (Exception e) {
            // 列已存在，忽略
        }
    }

    private void ensurePermissionExists(String name, String code, Long parentId, int type, int sortOrder) {
        LambdaQueryWrapper<Permission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Permission::getPermissionCode, code);
        Permission existing = permissionMapper.selectOne(wrapper);

        if (existing == null) {
            Permission permission = new Permission();
            permission.setPermissionName(name);
            permission.setPermissionCode(code);
            permission.setParentId(parentId);
            permission.setType(type);
            permission.setSortOrder(sortOrder);
            permission.setStatus(1);
            permissionMapper.insert(permission);
            existing = permission;
            log.info("已自动添加权限: {} ({})", name, code);
        }

        roleMapper.assignPermissionIfNotExists(5L, existing.getId());
    }
}
