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
@Slf4j
@Service
public class DeviceControlService {

    @Autowired
    private MqttGateway mqttGateway;

    @Autowired
    private ObjectMapper objectMapper;

    @Value("${mqtt.topic.command-pub-prefix}")
    private String commandPubPrefix;

    /**
     * 控制设备 RGB 灯光
     */
    public void changeDeviceColor(String macAddress, int r, int g, int b) {
        String deviceId = macAddress.startsWith("ESP32S3-") ? macAddress : "ESP32S3-" + macAddress;
        String targetTopic = commandPubPrefix + deviceId + "/command";

        Map<String, Integer> colorMap = new HashMap<>();
        colorMap.put("r", r);
        colorMap.put("g", g);
        colorMap.put("b", b);

        try {
            String payload = objectMapper.writeValueAsString(colorMap);
            mqttGateway.sendToMqtt(targetTopic, payload);
            log.info("[指令下发成功] 目标主题: {} | 载荷内容: {}", targetTopic, payload);
        } catch (Exception e) {
            log.error("[指令下发失败] JSON 序列化或 MQTT 发送异常！目标: {}", targetTopic, e);
        }
    }

    /**
     * 下发 FOTA 空中升级指令
     */
    public void sendOtaCommand(String macAddress, String version, String downloadUrl) {
        String deviceId = macAddress.startsWith("ESP32S3-") ? macAddress : "ESP32S3-" + macAddress;
        String targetTopic = commandPubPrefix + deviceId + "/command";

        Map<String, String> otaMap = new HashMap<>();
        otaMap.put("cmd", "ota");
        otaMap.put("version", version);
        otaMap.put("url", downloadUrl);

        try {
            String payload = objectMapper.writeValueAsString(otaMap);
            mqttGateway.sendToMqtt(targetTopic, payload);
            log.info("[FOTA 指令发射] 目标: {} | 升级版本: {} | 固件地址: {}", targetTopic, version, downloadUrl);
        } catch (Exception e) {
            log.error("FOTA 指令序列化/发送失败！", e);
        }
    }
}