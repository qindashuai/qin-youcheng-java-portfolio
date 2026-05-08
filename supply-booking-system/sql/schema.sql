CREATE DATABASE IF NOT EXISTS supply_booking DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE supply_booking;

CREATE TABLE IF NOT EXISTS `supplier` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `supplier_code` VARCHAR(64) NOT NULL COMMENT '供应商编码',
    `supplier_name` VARCHAR(128) NOT NULL COMMENT '供应商名称',
    `contact_person` VARCHAR(64) NOT NULL COMMENT '联系人',
    `contact_phone` VARCHAR(20) NOT NULL COMMENT '联系电话',
    `business_scope` VARCHAR(512) DEFAULT NULL COMMENT '经营范围',
    `address` VARCHAR(256) DEFAULT NULL COMMENT '地址',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_supplier_code` (`supplier_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商信息表';

CREATE TABLE IF NOT EXISTS `supplier_qualification` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `qualification_type` VARCHAR(64) NOT NULL COMMENT '资质类型',
    `qualification_name` VARCHAR(128) NOT NULL COMMENT '资质名称',
    `certificate_no` VARCHAR(64) DEFAULT NULL COMMENT '证书编号',
    `issue_date` DATE DEFAULT NULL COMMENT '发证日期',
    `expire_date` DATE NOT NULL COMMENT '到期日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-失效 1-有效 2-即将过期',
    `file_url` VARCHAR(512) DEFAULT NULL COMMENT '资质文件URL',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_expire_date` (`expire_date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='供应商资质表';

CREATE TABLE IF NOT EXISTS `time_slot` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `slot_date` DATE NOT NULL COMMENT '日期',
    `start_time` TIME NOT NULL COMMENT '开始时间',
    `end_time` TIME NOT NULL COMMENT '结束时间',
    `max_capacity` INT NOT NULL DEFAULT 10 COMMENT '最大容量',
    `current_booked` INT NOT NULL DEFAULT 0 COMMENT '已预约数量',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `version` INT NOT NULL DEFAULT 0 COMMENT '乐观锁版本号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_slot_date_time` (`slot_date`, `start_time`, `end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='时间段表';

CREATE TABLE IF NOT EXISTS `booking_order` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `booking_no` VARCHAR(64) NOT NULL COMMENT '预约单号',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `time_slot_id` BIGINT NOT NULL COMMENT '时间段ID',
    `booking_date` DATE NOT NULL COMMENT '预约日期',
    `booking_time` VARCHAR(32) NOT NULL COMMENT '预约时段',
    `vehicle_no` VARCHAR(20) DEFAULT NULL COMMENT '车牌号',
    `driver_name` VARCHAR(64) DEFAULT NULL COMMENT '司机姓名',
    `driver_phone` VARCHAR(20) DEFAULT NULL COMMENT '司机电话',
    `goods_type` VARCHAR(128) DEFAULT NULL COMMENT '货物类型',
    `goods_quantity` DECIMAL(10,2) DEFAULT NULL COMMENT '货物数量',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待确认 1-已确认 2-已入园 3-已完成 4-已取消',
    `confirm_time` DATETIME DEFAULT NULL COMMENT '确认时间',
    `cancel_reason` VARCHAR(256) DEFAULT NULL COMMENT '取消原因',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_booking_no` (`booking_no`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_time_slot_id` (`time_slot_id`),
    KEY `idx_booking_date` (`booking_date`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='预约订单表';

CREATE TABLE IF NOT EXISTS `park_entry` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `booking_id` BIGINT NOT NULL COMMENT '预约订单ID',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `entry_no` VARCHAR(64) NOT NULL COMMENT '入园登记号',
    `vehicle_no` VARCHAR(20) DEFAULT NULL COMMENT '车牌号',
    `driver_name` VARCHAR(64) DEFAULT NULL COMMENT '司机姓名',
    `entry_time` DATETIME DEFAULT NULL COMMENT '入园时间',
    `exit_time` DATETIME DEFAULT NULL COMMENT '离园时间',
    `gate_no` VARCHAR(32) DEFAULT NULL COMMENT '门禁通道号',
    `status` TINYINT NOT NULL DEFAULT 0 COMMENT '状态：0-待入园 1-已入园 2-已离园',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_entry_no` (`entry_no`),
    KEY `idx_booking_id` (`booking_id`),
    KEY `idx_supplier_id` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='入园登记表';

CREATE TABLE IF NOT EXISTS `cold_chain_vehicle` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `vehicle_no` VARCHAR(20) NOT NULL COMMENT '车牌号',
    `vehicle_type` VARCHAR(64) DEFAULT NULL COMMENT '车辆类型',
    `temperature_range` VARCHAR(64) DEFAULT NULL COMMENT '温控范围',
    `vehicle_capacity` DECIMAL(10,2) DEFAULT NULL COMMENT '车辆容量(吨)',
    `inspection_expire_date` DATE DEFAULT NULL COMMENT '检验到期日期',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用 1-启用',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    KEY `idx_supplier_id` (`supplier_id`),
    KEY `idx_vehicle_no` (`vehicle_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='冷链车辆信息表';

CREATE TABLE IF NOT EXISTS `receiving_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `booking_id` BIGINT NOT NULL COMMENT '预约订单ID',
    `supplier_id` BIGINT NOT NULL COMMENT '供应商ID',
    `entry_id` BIGINT DEFAULT NULL COMMENT '入园登记ID',
    `receiving_no` VARCHAR(64) NOT NULL COMMENT '收台单号',
    `goods_type` VARCHAR(128) DEFAULT NULL COMMENT '货物类型',
    `goods_quantity` DECIMAL(10,2) DEFAULT NULL COMMENT '实际收货数量',
    `receiving_person` VARCHAR(64) DEFAULT NULL COMMENT '收货人',
    `receiving_time` DATETIME DEFAULT NULL COMMENT '收货时间',
    `quality_status` TINYINT DEFAULT NULL COMMENT '质检状态：0-不合格 1-合格 2-待检',
    `remark` VARCHAR(512) DEFAULT NULL COMMENT '备注',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT NOT NULL DEFAULT 0 COMMENT '逻辑删除：0-未删除 1-已删除',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_receiving_no` (`receiving_no`),
    KEY `idx_booking_id` (`booking_id`),
    KEY `idx_supplier_id` (`supplier_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收台记录表';

CREATE TABLE IF NOT EXISTS `operation_log` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    `module` VARCHAR(64) DEFAULT NULL COMMENT '操作模块',
    `operation` VARCHAR(256) DEFAULT NULL COMMENT '操作描述',
    `method` VARCHAR(256) DEFAULT NULL COMMENT '请求方法',
    `params` TEXT DEFAULT NULL COMMENT '请求参数',
    `ip` VARCHAR(64) DEFAULT NULL COMMENT '请求IP',
    `user_id` BIGINT DEFAULT NULL COMMENT '操作人ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';
