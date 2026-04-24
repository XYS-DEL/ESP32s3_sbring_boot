package com.iot.esp32.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.esp32.model.dto.DeviceStateDto;
import com.iot.esp32.service.DeviceManagerService;
import com.iot.esp32.service.TelemetryService;
import com.iot.esp32.websocket.WebSocketServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttMessageReceiver {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private DeviceManagerService deviceManagerService;

    @Autowired
    private TelemetryService telemetryService;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {
        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();

        String[] topicParts = topic.split("/");
        String rawMac = topicParts.length > 2 ? topicParts[2] : "UNKNOWN";
        String macAddress = rawMac.replace("ESP32S3-", "");

        try {
            DeviceStateDto dto = objectMapper.readValue(payload, DeviceStateDto.class);

            // 只要报文里带有 status 字段，立刻调用 Service 进行数据库落盘！
            if (dto.getStatus() != null) {
                deviceManagerService.upsertDeviceStatus(macAddress, dto.getStatus());
            }

            // 【全局广播】任何消息都推送给前端
            String wsMessage = String.format("{\"mac\":\"%s\", \"data\":%s}", macAddress, payload);
            WebSocketServer.broadcastMessage(wsMessage);

            // 业务路由与落盘分发
            if ("offline".equals(dto.getStatus())) {
                log.warn("警报：设备 [{}] 已掉线 (LWT遗嘱触发)！", macAddress);
            } else if ("online".equals(dto.getStatus())) {
                log.info("提示：设备 [{}] 重新上线！", macAddress);
            } else {
                // 常规遥测数据包
                log.info("设备 [{}] 遥测数据 -> 温度: {}°C | 信号: {}dBm | 内存: {}KB",
                        macAddress, dto.getTemp(), dto.getRssi(), dto.getHeapKb());
                // 防止空指针写入 InfluxDB
                if (dto.getTemp() != null && dto.getHeapKb() != null && dto.getRssi() != null) {
                    telemetryService.saveTelemetry(macAddress, dto.getTemp(), dto.getHeapKb(), dto.getRssi());
                }
            }

        } catch (Exception e) {
            log.error("JSON 解析失败！原文: {}", payload, e);
        }
    }
}