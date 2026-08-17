-- 迁移：修复赛事相关权限问题

-- 1. 给球队管理员(team_admin, role_id=5)添加 match:add 权限(permission_id=21)
-- 用于友谊赛邀请功能
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 5, id FROM sys_permission WHERE permission_code = 'match:add'
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 2. 给普通用户(user, role_id=2)添加 match:view 权限(permission_id=20)
-- 确保普通用户能看到赛事列表
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code = 'match:view'
ON DUPLICATE KEY UPDATE role_id = role_id;

-- 3. 给普通用户(user, role_id=2)添加 stat:match 权限(permission_id=38)
-- 确保普通用户能看到赛事统计
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 2, id FROM sys_permission WHERE permission_code = 'stat:match'
ON DUPLICATE KEY UPDATE role_id = role_id;
