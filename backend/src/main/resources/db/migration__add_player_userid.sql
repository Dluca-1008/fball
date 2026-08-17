-- 迁移：为球员表添加 user_id 字段，关联用户
ALTER TABLE players ADD COLUMN IF NOT EXISTS user_id BIGINT AFTER id;
ALTER TABLE players ADD INDEX idx_user_id (user_id);
