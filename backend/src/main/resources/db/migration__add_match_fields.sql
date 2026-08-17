-- 迁移脚本：在 MySQL 客户端直接执行（Navicat / DBeaver / 命令行均可）
-- 如果字段已存在会报错，忽略即可

-- 1. 添加赛事名称字段
ALTER TABLE matches ADD COLUMN name VARCHAR(100) DEFAULT NULL COMMENT '赛事名称';

-- 2. 添加赛事类型字段
ALTER TABLE matches ADD COLUMN match_type VARCHAR(20) DEFAULT NULL COMMENT '赛事类型: league=联赛 cup=杯赛 friendly=友谊赛';

-- 3. 创建友谊赛邀请表
CREATE TABLE match_friend_requests (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  sender_team_id BIGINT NOT NULL COMMENT '发起邀请的球队ID',
  receiver_team_id BIGINT NOT NULL COMMENT '被邀请球队ID',
  match_date DATETIME NOT NULL COMMENT '建议比赛时间',
  venue VARCHAR(200) NOT NULL COMMENT '建议比赛场地',
  status TINYINT DEFAULT 0 COMMENT '状态: 0=待处理 1=已接受 2=已拒绝',
  created_by BIGINT COMMENT '发起人用户ID',
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  KEY idx_sender (sender_team_id),
  KEY idx_receiver (receiver_team_id),
  KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='友谊赛邀请表';
