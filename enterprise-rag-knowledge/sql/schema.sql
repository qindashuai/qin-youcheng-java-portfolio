CREATE DATABASE IF NOT EXISTS `enterprise_rag` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE `enterprise_rag`;

CREATE TABLE IF NOT EXISTS `knowledge_base` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `name` VARCHAR(128) NOT NULL COMMENT '知识库名称',
    `description` VARCHAR(512) DEFAULT NULL COMMENT '知识库描述',
    `category` VARCHAR(64) DEFAULT NULL COMMENT '分类：POLICY/FAULT/PROCESS/OTHER',
    `document_count` INT DEFAULT 0 COMMENT '文档数量',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_category` (`category`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识库表';

CREATE TABLE IF NOT EXISTS `knowledge_document` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `knowledge_base_id` BIGINT NOT NULL COMMENT '所属知识库ID',
    `title` VARCHAR(256) NOT NULL COMMENT '文档标题',
    `file_name` VARCHAR(256) NOT NULL COMMENT '原始文件名',
    `file_path` VARCHAR(512) NOT NULL COMMENT '文件存储路径',
    `file_type` VARCHAR(32) NOT NULL COMMENT '文件类型：PDF/DOCX/TXT',
    `file_size` BIGINT DEFAULT 0 COMMENT '文件大小(字节)',
    `chunk_count` INT DEFAULT 0 COMMENT '分块数量',
    `vector_status` TINYINT DEFAULT 0 COMMENT '向量化状态：0-待处理 1-处理中 2-已完成 3-失败',
    `parse_status` TINYINT DEFAULT 0 COMMENT '解析状态：0-待处理 1-处理中 2-已完成 3-失败',
    `chunk_strategy` VARCHAR(32) DEFAULT 'FIXED_SIZE' COMMENT '分块策略：FIXED_SIZE/SEMANTIC',
    `chunk_size` INT DEFAULT 512 COMMENT '分块大小',
    `chunk_overlap` INT DEFAULT 64 COMMENT '分块重叠',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_knowledge_base_id` (`knowledge_base_id`),
    KEY `idx_vector_status` (`vector_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识文档表';

CREATE TABLE IF NOT EXISTS `document_chunk` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `document_id` BIGINT NOT NULL COMMENT '所属文档ID',
    `knowledge_base_id` BIGINT NOT NULL COMMENT '所属知识库ID',
    `chunk_index` INT NOT NULL COMMENT '分块序号',
    `content` TEXT NOT NULL COMMENT '分块内容',
    `token_count` INT DEFAULT 0 COMMENT 'Token数量',
    `embedding` MEDIUMTEXT DEFAULT NULL COMMENT '向量嵌入(JSON格式存储)',
    `vector_status` TINYINT DEFAULT 0 COMMENT '向量化状态：0-未向量化 1-已向量化',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_document_id` (`document_id`),
    KEY `idx_knowledge_base_id` (`knowledge_base_id`),
    KEY `idx_vector_status` (`vector_status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='文档分块表';

CREATE TABLE IF NOT EXISTS `chat_conversation` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `conversation_id` VARCHAR(64) NOT NULL COMMENT '会话唯一标识',
    `title` VARCHAR(256) DEFAULT NULL COMMENT '会话标题',
    `user_id` VARCHAR(64) DEFAULT 'anonymous' COMMENT '用户ID',
    `knowledge_base_id` BIGINT DEFAULT NULL COMMENT '关联知识库ID',
    `message_count` INT DEFAULT 0 COMMENT '消息数量',
    `status` TINYINT DEFAULT 1 COMMENT '状态：0-已关闭 1-进行中',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_conversation_id` (`conversation_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_knowledge_base_id` (`knowledge_base_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话会话表';

CREATE TABLE IF NOT EXISTS `chat_message` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `conversation_id` VARCHAR(64) NOT NULL COMMENT '所属会话ID',
    `role` VARCHAR(16) NOT NULL COMMENT '角色：USER/ASSISTANT/SYSTEM',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `intent_type` VARCHAR(32) DEFAULT NULL COMMENT '意图类型：POLICY/FAULT/PROCESS/OTHER',
    `source_chunks` TEXT DEFAULT NULL COMMENT '引用的分块ID列表(JSON)',
    `model_name` VARCHAR(64) DEFAULT NULL COMMENT '使用的模型名称',
    `token_usage` INT DEFAULT 0 COMMENT 'Token消耗',
    `response_time` INT DEFAULT 0 COMMENT '响应时间(ms)',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_intent_type` (`intent_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='对话消息表';

CREATE TABLE IF NOT EXISTS `feedback_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `message_id` BIGINT NOT NULL COMMENT '关联消息ID',
    `conversation_id` VARCHAR(64) NOT NULL COMMENT '关联会话ID',
    `user_id` VARCHAR(64) DEFAULT 'anonymous' COMMENT '用户ID',
    `score` TINYINT NOT NULL COMMENT '评分：1-5星',
    `comment` VARCHAR(1024) DEFAULT NULL COMMENT '文字反馈',
    `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_message_id` (`message_id`),
    KEY `idx_conversation_id` (`conversation_id`),
    KEY `idx_score` (`score`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='反馈记录表';
