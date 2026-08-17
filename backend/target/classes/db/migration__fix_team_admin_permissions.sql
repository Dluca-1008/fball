-- 迁移：修复球队管理员权限
-- 给 team_admin (role_id=5) 添加 team:edit 权限(permission_id=9)
-- 使球队管理员能够管理所属球队

INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 5, id FROM sys_permission WHERE permission_code = 'team:edit'
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 同时添加 team:manage 父权限（如果需要）
-- INSERT INTO sys_role_permission (role_id, permission_id)
-- SELECT 5, id FROM sys_permission WHERE permission_code = 'team:manage'
-- ON DUPLICATE KEY UPDATE role_id = role_id;
