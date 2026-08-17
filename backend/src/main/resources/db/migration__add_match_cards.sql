-- 迁移：创建比赛红黄牌表
CREATE TABLE IF NOT EXISTS match_cards (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  match_id BIGINT NOT NULL,
  team_id BIGINT NOT NULL,
  player_id BIGINT,
  card_type TINYINT NOT NULL COMMENT '1=黄牌 2=红牌',
  minute INT,
  created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
  KEY idx_match_id (match_id),
  KEY idx_team_id (team_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='比赛红黄牌记录表';
