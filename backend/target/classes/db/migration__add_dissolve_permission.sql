-- 1. 添加 team:dissolve 权限
INSERT INTO sys_permission (permission_name, permission_code, parent_id, type, sort_order)
VALUES ('解散球队', 'team:dissolve', 6, 2, 5);

-- 2. 将 team:dissolve 权限分配给球队管理员角色 (role_id=5)
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 5, id FROM sys_permission WHERE permission_code = 'team:dissolve';
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission WHERE permission_code = 'team:dissolve';