package com.iot.esp32.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * 设备状态数据传输对象
 * 用于将单片机发来的 JSON 映射为 Java 对象
 */
@Data // Lombok 注解：自动生成 get/set/toString 方法，代码极其清爽
@JsonIgnoreProperties(ignoreUnknown = true) // 容错神级注解：如果单片机多发了字段，Java 找不到对应的属性也不会报错抛异常
public class DeviceStateDto {

    // 💡 注意：这里全部使用包装类 (Integer, Float) 而不是基本数据类型 (int, float)
    // 因为设备上线/离线遗嘱只发 {"status": "online/offline"}，其他字段根本不存在
    // 如果用基本类型会报错，包装类找不到对应字段会自动赋值为 null，极其安全！

    private String status;      // 状态：online, offline, online_OTA_V2
    private Float temp;         // 温度

    @JsonProperty("heap_kb")    // 如果 Java 命名规范和 JSON 键名不一致，可以用这个映射
    private Integer heapKb;     // 剩余内存 (KB)

    private String uptime;      // 格式化的运行时间

    @JsonProperty("uptime_sec")
    private Long uptimeSec;     // 运行总秒数

    private Integer rssi;       // Wi-Fi 信号强度
    private String ip;          // 局域网 IP
}