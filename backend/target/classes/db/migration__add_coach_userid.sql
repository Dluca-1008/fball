-- 迁移：为教练表添加 user_id 字段，关联用户
-- 同时确保 players 表的 user_id 存在（幂等）
ALTER TABLE players ADD COLUMN IF NOT EXISTS user_id BIGINT;
ALTER TABLE coaches ADD COLUMN IF NOT EXISTS user_id BIGINT;
