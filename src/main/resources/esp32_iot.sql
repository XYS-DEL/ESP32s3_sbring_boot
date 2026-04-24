CREATE DATABASE IF NOT EXISTS esp32_iot DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE esp32_iot;

-- 创建设备数字孪生表
CREATE TABLE IF NOT EXISTS iot_device (
    mac_address VARCHAR(20) PRIMARY KEY COMMENT '设备物理MAC (主键，如 E83DC1FA71BC)',
    device_name VARCHAR(50) DEFAULT '未命名设备' COMMENT '用户自定义的设备别名',
    status VARCHAR(20) DEFAULT 'offline' COMMENT '在线状态: online, offline',
    current_fw_ver VARCHAR(20) DEFAULT 'UNKNOWN' COMMENT '当前运行的固件版本',
    last_active_time DATETIME COMMENT '最后一次收到心跳或遥测数据的时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '设备首次接入系统的注册时间'
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='物联网设备注册与状态表';