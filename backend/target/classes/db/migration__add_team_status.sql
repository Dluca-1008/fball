-- 给 teams 表添加 status 列 (1=正常, 0=已解散)
ALTER TABLE teams ADD COLUMN status INT DEFAULT 1 AFTER created_by;

-- 将已有球队的状态设为 1
UPDATE teams SET status = 1 WHERE status IS NULL;
