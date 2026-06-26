-- 初始化角色
INSERT INTO sys_role (role_name, role_code, description) VALUES
('系统管理员', 'admin', '拥有系统所有权限'),
('普通用户', 'user', '基础浏览和社区功能'),
('赛事主办方', 'organizer', '管理赛事相关功能'),
('商家', 'merchant', '管理商品和订单'),
('球队管理员', 'team_admin', '管理本球队相关功能');

-- 初始化权限
INSERT INTO sys_permission (permission_name, permission_code, parent_id, type, sort_order) VALUES
('用户管理', 'user:manage', 0, 1, 1),
('用户查看', 'user:view', 1, 2, 1),
('用户新增', 'user:add', 1, 2, 2),
('用户编辑', 'user:edit', 1, 2, 3),
('用户删除', 'user:delete', 1, 2, 4),
('球队管理', 'team:manage', 0, 1, 2),
('球队查看', 'team:view', 6, 2, 1),
('球队新增', 'team:add', 6, 2, 2),
('球队编辑', 'team:edit', 6, 2, 3),
('球队删除', 'team:delete', 6, 2, 4),
('解散球队', 'team:dissolve', 6, 2, 5),
('球队成员管理', 'team_member:manage', 0, 1, 3),
('成员查看', 'team_member:view', 12, 2, 1),
('成员邀请', 'team_member:invite', 12, 2, 2),
('成员审批', 'team_member:approve', 12, 2, 3),
('成员移除', 'team_member:remove', 12, 2, 4),
('设置管理员', 'team_member:set_admin', 12, 2, 5),
('设置成员状态', 'team_member:set_status', 12, 2, 6),
('赛事管理', 'match:manage', 0, 1, 4),
('赛事查看', 'match:view', 19, 2, 1),
('赛事新增', 'match:add', 19, 2, 2),
('赛事编辑', 'match:edit', 19, 2, 3),
('赛事删除', 'match:delete', 19, 2, 4),
('社区管理', 'post:manage', 0, 1, 5),
('帖子查看', 'post:view', 24, 2, 1),
('帖子发布', 'post:add', 24, 2, 2),
('帖子编辑', 'post:edit', 24, 2, 3),
('帖子删除', 'post:delete', 24, 2, 4),
('商城管理', 'product:manage', 0, 1, 6),
('商品查看', 'product:view', 29, 2, 1),
('商品新增', 'product:add', 29, 2, 2),
('商品编辑', 'product:edit', 29, 2, 3),
('商品删除', 'product:delete', 29, 2, 4),
('订单管理', 'order:manage', 0, 1, 7),
('订单查看', 'order:view', 34, 2, 1),
('订单处理', 'order:process', 34, 2, 2),
('数据统计', 'stat:manage', 0, 1, 8),
('赛事统计', 'stat:match', 37, 2, 1),
('销售统计', 'stat:sales', 37, 2, 2),
('球队统计', 'stat:team', 37, 2, 3);

-- 管理员拥有所有权限
INSERT INTO sys_role_permission (role_id, permission_id)
SELECT 1, id FROM sys_permission;

-- 普通用户权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 2), (2, 7), (2, 13), (2, 20), (2, 25), (2, 30), (2, 35);

-- 赛事主办方权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(3, 2), (3, 7), (3, 13), (3, 20), (3, 21), (3, 22), (3, 23), (3, 25), (3, 30), (3, 38);

-- 商家权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(4, 2), (4, 7), (4, 13), (4, 20), (4, 25), (4, 30), (4, 31), (4, 32), (4, 33), (4, 35), (4, 36), (4, 39);

-- 球队管理员权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(5, 2), (5, 7), (5, 11), (5, 13), (5, 14), (5, 15), (5, 16), (5, 17), (5, 18), (5, 19), (5, 25), (5, 30), (5, 35), (5, 40);

-- 创建管理员用户 (密码: admin123)
INSERT INTO users (username, password, nickname, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5EH', '系统管理员', 1);

-- 分配管理员角色
INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);
