package com.iot.esp32.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 🗄设备数字孪生实体类 (映射 iot_device 表)
 */
@Data
@TableName("iot_device") // 告诉框架这个类对应哪张表
public class IotDevice {

    // 因为 MAC 地址是字符串，不是自增的数字，所以主键类型必须声明为 INPUT (用户输入)
    @TableId(type = IdType.INPUT)
    private String macAddress;

    private String deviceName;
    private String status;
    private String currentFwVer;

    private LocalDateTime lastActiveTime;
    private LocalDateTime createTime;
}