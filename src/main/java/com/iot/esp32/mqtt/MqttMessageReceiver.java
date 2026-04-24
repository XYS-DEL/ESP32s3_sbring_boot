package com.iot.esp32.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.esp32.model.dto.DeviceStateDto;
import com.iot.esp32.service.DeviceManagerService;
import com.iot.esp32.service.TelemetryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MqttMessageReceiver {

    // 注入 Jackson 的核心处理类
    @Autowired
    private ObjectMapper objectMapper;

    // 注入设备状态管理服务
    @Autowired
    private DeviceManagerService deviceManagerService;

    // 引入 TelemetryService
    @Autowired
    private TelemetryService telemetryService;
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {

        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();

        // 提取 MAC 地址 (从 device/esp32s3/ESP32S3-MAC/state 中截取)
        String[] topicParts = topic.split("/");
        String rawMac = topicParts.length > 2 ? topicParts[2] : "UNKNOWN";

        // 剔除 "ESP32S3-" 前缀，保证存入 MySQL 的是纯净的物理 MAC 地址 (如 E83DC1FA71BC)
        String macAddress = rawMac.replace("ESP32S3-", "");

        try {
            // JSON 字符串一键反序列化为 Java 对象
            DeviceStateDto dto = objectMapper.readValue(payload, DeviceStateDto.class);

            // 只要报文里带有 status 字段，立刻调用 Service 进行数据库落盘！
            if (dto.getStatus() != null) {
                deviceManagerService.upsertDeviceStatus(macAddress, dto.getStatus());
            }

            // 业务路由分发
            if ("offline".equals(dto.getStatus())) {
                log.warn("警报：设备 [{}] 已掉线 (LWT遗嘱触发)！", macAddress);
            } else if ("online".equals(dto.getStatus())) {
                log.info("提示：设备 [{}] 重新上线！", macAddress);
            } else {
                // 如果既不是纯粹的 online 也不是 offline，那就是正常的遥测数据包了
                log.info("设备 [{}] 遥测数据 -> 温度: {}°C | 信号: {}dBm | 内存: {}KB",
                        macAddress, dto.getTemp(), dto.getRssi(), dto.getHeapKb());
                telemetryService.saveTelemetry(macAddress, dto.getTemp(), dto.getHeapKb(), dto.getRssi());
                String wsMessage = String.format("{\"mac\":\"%s\", \"data\":%s}", macAddress, payload);
                com.iot.esp32.websocket.WebSocketServer.broadcastMessage(wsMessage);
            }

        } catch (Exception e) {
            // 打印错误日志并将异常堆栈对象 e 传入，底层会自动格式化输出错误细节
            log.error("JSON 解析失败！原文: {}", payload, e);
        }
    }
}