ALTER TABLE team_members ADD COLUMN IF NOT EXISTS member_type VARCHAR(20) DEFAULT 'player' AFTER role;

CREATE TABLE IF NOT EXISTS coaches (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  team_id BIGINT,
  name VARCHAR(100) NOT NULL,
  role_title VARCHAR(50),
  nationality VARCHAR(50),
  birth_date DATE,
  experience_years INT,
  avatar VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (team_id) REFERENCES teams(id)
);
