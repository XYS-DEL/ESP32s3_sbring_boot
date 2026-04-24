package com.iot.esp32.model.entity;

import com.influxdb.annotations.Column;
import com.influxdb.annotations.Measurement;
import lombok.Data;

import java.time.Instant;

/**
 * 遥测数据时序实体类
 * 映射 InfluxDB 中的 "esp32_sensor" 表 (Measurement)
 */
@Data
@Measurement(name = "esp32_sensor")
public class TelemetryData {

    // Tag (标签)：必须设为 true，它是索引，以后按设备查数据极快
    @Column(tag = true)
    private String macAddress;

    // Field (字段)：实际的物理量
    @Column
    private Float temp;

    @Column
    private Integer heapKb;

    @Column
    private Integer rssi;

    // 时间戳：InfluxDB 最核心的灵魂
    @Column(timestamp = true)
    private Instant time;
}