package com.iot.esp32.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.esp32.mqtt.MqttGateway;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 设备下行控制服务
 */
@Slf4j // 自动为当前类生成一个名为 log 的工业级日志对象
@Service
public class DeviceControlService {

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private ObjectMapper objectMapper;

    // 从 yml 注入下发前缀: "device/esp32s3/"
    @Value("${mqtt.topic.command-pub-prefix}")
    private String commandPubPrefix;

    /**
     * 控制设备 RGB 灯光
     */
    public void changeDeviceColor(String macAddress, int r, int g, int b) {

        // 1. 智能修复前缀：防止前端多传或漏传 "ESP32S3-"
        String deviceId = macAddress.startsWith("ESP32S3-") ? macAddress : "ESP32S3-" + macAddress;

        // 2. 拼接精准打击的 Topic
        String targetTopic = commandPubPrefix + deviceId + "/command";

        // 3. 构建 JSON 数据字典
        Map<String, Integer> colorMap = new HashMap<>();
        colorMap.put("r", r);
        colorMap.put("g", g);
        colorMap.put("b", b);

        try {
            // 将 Map 转换为纯文本 JSON 字符串
            String payload = objectMapper.writeValueAsString(colorMap);

            // 调用网关发送
            mqttGateway.sendToMqtt(targetTopic, payload);

            log.info("🔫 [指令下发成功] 目标主题: {} | 载荷内容: {}", targetTopic, payload);

        } catch (Exception e) {
            // 日志替换 printStackTrace，把异常对象 e 传在最后，它会自动打印错误堆栈
            log.error("[指令下发失败] JSON 序列化或 MQTT 发送异常！目标: {}", targetTopic, e);
        }
    }
}