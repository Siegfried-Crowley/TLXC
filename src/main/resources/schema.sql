-- 创建数据库
CREATE DATABASE IF NOT EXISTS algorithm_training DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE algorithm_training;

-- 用户表
CREATE TABLE IF NOT EXISTS `user` (
                                      `id` INT AUTO_INCREMENT PRIMARY KEY,
                                      `username` VARCHAR(32) NOT NULL UNIQUE,
    `hashed_password` VARCHAR(255) NOT NULL,
    `nickname` VARCHAR(64) DEFAULT '',
    `role` VARCHAR(20) DEFAULT 'user',
    `status` VARCHAR(20) DEFAULT 'active',
    `total_points` INT DEFAULT 0,
    `streak_days` INT DEFAULT 0,
    `last_pass_date` DATE DEFAULT NULL,
    `last_login_at` DATETIME DEFAULT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (`username`),
    INDEX idx_role (`role`),
    INDEX idx_status (`status`),
    INDEX idx_total_points (`total_points`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题目表
CREATE TABLE IF NOT EXISTS `problem` (
                                         `id` INT AUTO_INCREMENT PRIMARY KEY,
                                         `title` VARCHAR(255) NOT NULL,
    `difficulty` VARCHAR(20) NOT NULL,
    `tags` TEXT,
    `description` TEXT NOT NULL,
    `input_description` TEXT,
    `output_description` TEXT,
    `examples` TEXT,
    `hint` TEXT,
    `constraints` TEXT,
    `source` VARCHAR(255) DEFAULT '',
    `status` VARCHAR(20) DEFAULT 'draft',
    `time_limit_ms` INT DEFAULT 1000,
    `memory_limit_mb` INT DEFAULT 128,
    `created_by` INT DEFAULT NULL,
    `starter_code` TEXT,
    `is_active` BOOLEAN DEFAULT TRUE,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_title (`title`),
    INDEX idx_difficulty (`difficulty`),
    INDEX idx_status (`status`),
    INDEX idx_is_active (`is_active`),
    FOREIGN KEY (`created_by`) REFERENCES `user`(`id`) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 标签表
CREATE TABLE IF NOT EXISTS `tag` (
    `id` INT AUTO_INCREMENT PRIMARY KEY,
    `name` VARCHAR(50) NOT NULL UNIQUE,
    `color` VARCHAR(20) DEFAULT '#22d3ee',
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_name (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 题目-标签关联表
CREATE TABLE IF NOT EXISTS `problem_tag` (
    `problem_id` INT NOT NULL,
    `tag_id` INT NOT NULL,
    PRIMARY KEY (`problem_id`, `tag_id`),
    FOREIGN KEY (`problem_id`) REFERENCES `problem`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`tag_id`) REFERENCES `tag`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 测试用例表
CREATE TABLE IF NOT EXISTS `test_case` (
                                           `id` INT AUTO_INCREMENT PRIMARY KEY,
                                           `problem_id` INT NOT NULL,
                                           `input_data` TEXT NOT NULL,
                                           `expected_output` TEXT NOT NULL,
                                           `is_hidden` BOOLEAN DEFAULT FALSE,
                                           `score_weight` INT DEFAULT 1,
                                           `remark` VARCHAR(255) DEFAULT '',
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_problem_id (`problem_id`),
    FOREIGN KEY (`problem_id`) REFERENCES `problem`(`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 提交记录表
CREATE TABLE IF NOT EXISTS `submission` (
                                            `id` INT AUTO_INCREMENT PRIMARY KEY,
                                            `user_id` INT NOT NULL,
                                            `problem_id` INT NOT NULL,
                                            `code` TEXT NOT NULL,
                                            `language` VARCHAR(20) DEFAULT 'python',
    `status` VARCHAR(50) NOT NULL,
    `runtime_ms` INT DEFAULT 0,
    `passed_cases` INT DEFAULT 0,
    `total_cases` INT DEFAULT 0,
    `error_message` TEXT,
    `score_delta` INT DEFAULT 0,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (`user_id`),
    INDEX idx_problem_id (`problem_id`),
    INDEX idx_status (`status`),
    INDEX idx_created_at (`created_at`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`problem_id`) REFERENCES `problem`(`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 错题本表
CREATE TABLE IF NOT EXISTS `wrong_book` (
                                            `id` INT AUTO_INCREMENT PRIMARY KEY,
                                            `user_id` INT NOT NULL,
                                            `problem_id` INT NOT NULL,
                                            `last_submission_id` INT DEFAULT NULL,
                                            `status` VARCHAR(20) DEFAULT 'unmastered',
    `note` TEXT,
    `wrong_count` INT DEFAULT 1,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (`user_id`),
    INDEX idx_problem_id (`problem_id`),
    INDEX idx_status (`status`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`problem_id`) REFERENCES `problem`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`last_submission_id`) REFERENCES `submission`(`id`) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 积分日志表
CREATE TABLE IF NOT EXISTS `point_log` (
                                           `id` INT AUTO_INCREMENT PRIMARY KEY,
                                           `user_id` INT NOT NULL,
                                           `problem_id` INT DEFAULT NULL,
                                           `points` INT NOT NULL,
                                           `reason` VARCHAR(255) NOT NULL,
    `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (`user_id`),
    INDEX idx_problem_id (`problem_id`),
    INDEX idx_created_at (`created_at`),
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`problem_id`) REFERENCES `problem`(`id`) ON DELETE SET NULL
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 每日比赛表
CREATE TABLE IF NOT EXISTS `daily_contest` (
                                               `id` INT AUTO_INCREMENT PRIMARY KEY,
                                               `contest_date` DATE NOT NULL,
                                               `title` VARCHAR(255) DEFAULT '',
    `status` VARCHAR(20) DEFAULT 'active',
    `generated_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
    `finalized_at` DATETIME DEFAULT NULL,
    UNIQUE KEY uk_contest_date (`contest_date`),
    INDEX idx_status (`status`),
    INDEX idx_finalized_at (`finalized_at`)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 比赛题目关联表
CREATE TABLE IF NOT EXISTS `contest_problem` (
                                                 `id` INT AUTO_INCREMENT PRIMARY KEY,
                                                 `contest_id` INT NOT NULL,
                                                 `problem_id` INT NOT NULL,
                                                 `order_index` INT DEFAULT 0,
                                                 INDEX idx_contest_id (`contest_id`),
    INDEX idx_problem_id (`problem_id`),
    INDEX idx_order_index (`order_index`),
    FOREIGN KEY (`contest_id`) REFERENCES `daily_contest`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`problem_id`) REFERENCES `problem`(`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 比赛参与表
CREATE TABLE IF NOT EXISTS `contest_participation` (
                                                       `id` INT AUTO_INCREMENT PRIMARY KEY,
                                                       `contest_id` INT NOT NULL,
                                                       `user_id` INT NOT NULL,
                                                       `started_at` DATETIME DEFAULT CURRENT_TIMESTAMP,
                                                       `deadline_at` DATETIME NOT NULL,
                                                       `submitted_at` DATETIME DEFAULT NULL,
                                                       `total_score` INT DEFAULT 0,
                                                       `accepted_count` INT DEFAULT 0,
                                                       `total_runtime_ms` INT DEFAULT 0,
                                                       `rank` INT DEFAULT NULL,
                                                       INDEX idx_contest_id (`contest_id`),
    INDEX idx_user_id (`user_id`),
    INDEX idx_submitted_at (`submitted_at`),
    INDEX idx_total_score (`total_score`),
    INDEX idx_rank (`rank`),
    FOREIGN KEY (`contest_id`) REFERENCES `daily_contest`(`id`) ON DELETE CASCADE,
    FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 插入默认管理员账号 (密码: admin123)
INSERT INTO `user` (`username`, `hashed_password`, `nickname`, `role`, `status`, `total_points`) VALUES
    ('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', '管理员', 'admin', 'active', 0);

-- 插入示例题目数据
INSERT INTO `problem` (`title`, `difficulty`, `tags`, `description`, `input_description`, `output_description`, `examples`, `hint`, `constraints`, `status`, `is_active`, `created_by`) VALUES
                                                                                                                                                                                            ('两数之和', 'easy', '["数组", "哈希表"]', '给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出和为目标值的那两个整数，并返回它们的数组下标。', 'nums = [2,7,11,15], target = 9', '[0,1]', '[{"input": [[2,7,11,15], 9], "output": [0,1]}]', '你可以假设每种输入只会对应一个答案。', '2 <= nums.length <= 10^4', 'published', TRUE, 1),
                                                                                                                                                                                            ('两数相加', 'medium', '["链表", "数学"]', '给你两个非空的链表，表示两个非负的整数。它们每位数字都是按照逆序的方式存储的，并且每个节点只能存储一位数字。请你将两个数相加，并以相同形式返回一个表示和的链表。', 'l1 = [2,4,3], l2 = [5,6,4]', '[7,0,8]', '[{"input": [[2,4,3], [5,6,4]], "output": [7,0,8]}]', '如果链表中的数字不是按逆序存储怎么办？', '每个链表中的节点数在范围 [1, 100] 内', 'published', TRUE, 1),
                                                                                                                                                                                            ('最长回文子串', 'medium', '["字符串", "动态规划"]', '给你一个字符串 s，找到 s 中最长的回文子串。', 's = "babad"', '"bab"', '[{"input": ["babad"], "output": "bab"}]', '你可以假设 s 的最大长度为 1000。', '1 <= s.length <= 1000', 'published', TRUE, 1);

-- 插入测试用例
INSERT INTO `test_case` (`problem_id`, `input_data`, `expected_output`, `is_hidden`) VALUES
                                                                                         (1, '[[2,7,11,15], 9]', '[0,1]', FALSE),
                                                                                         (1, '[[3,2,4], 6]', '[1,2]', FALSE),
                                                                                         (2, '[[2,4,3], [5,6,4]]', '[7,0,8]', FALSE),
                                                                                         (3, '["babad"]', '"bab"', FALSE),
                                                                                         (3, '["cbbd"]', '"bb"', FALSE);
