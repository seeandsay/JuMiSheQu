-- ============================================
-- 剧迷社区 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS drama_community
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_unicode_ci;

USE drama_community;

-- ============================================
-- 用户表
-- ============================================
CREATE TABLE IF NOT EXISTS `user` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `phone`       VARCHAR(20)  DEFAULT NULL COMMENT '手机号',
  `email`       VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
  `password`    VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
  `nickname`    VARCHAR(50)  NOT NULL COMMENT '昵称',
  `avatar`      VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_phone` (`phone`),
  UNIQUE KEY `uk_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- ============================================
-- 剧集表
-- ============================================
CREATE TABLE IF NOT EXISTS `drama` (
  `id`           BIGINT       NOT NULL AUTO_INCREMENT COMMENT '剧集ID',
  `name`         VARCHAR(200) NOT NULL COMMENT '剧集名称',
  `poster`       VARCHAR(500) DEFAULT NULL COMMENT '海报URL',
  `description`  TEXT         DEFAULT NULL COMMENT '剧集简介',
  `genre`        VARCHAR(100) DEFAULT NULL COMMENT '类型（多个用逗号分隔）',
  `release_date` DATE         DEFAULT NULL COMMENT '上映日期',
  `create_time`  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_name` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='剧集表';

-- ============================================
-- 评价表
-- ============================================
CREATE TABLE IF NOT EXISTS `review` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT '评价ID',
  `user_id`     BIGINT       NOT NULL COMMENT '用户ID',
  `drama_id`    BIGINT       NOT NULL COMMENT '剧集ID',
  `rating`      TINYINT      NOT NULL COMMENT '评分（1-10）',
  `content`     VARCHAR(500) NOT NULL COMMENT '文字评价（最多500字）',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_user_id` (`user_id`),
  KEY `idx_drama_id` (`drama_id`),
  KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- ============================================
-- 验证码表
-- ============================================
CREATE TABLE IF NOT EXISTS `verify_code` (
  `id`          BIGINT       NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `target`      VARCHAR(100) NOT NULL COMMENT '手机号或邮箱',
  `code`        VARCHAR(10)  NOT NULL COMMENT '验证码',
  `expire_time` DATETIME     NOT NULL COMMENT '过期时间',
  `create_time` DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`),
  KEY `idx_target` (`target`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='验证码表';
