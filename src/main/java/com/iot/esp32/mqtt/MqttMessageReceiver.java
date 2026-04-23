package com.iot.esp32.mqtt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.esp32.model.dto.DeviceStateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Component
public class MqttMessageReceiver {

    // 注入 Jackson 的核心处理类
    @Autowired
    private ObjectMapper objectMapper;

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(Message<?> message) {

        String topic = (String) message.getHeaders().get(MqttHeaders.RECEIVED_TOPIC);
        String payload = (String) message.getPayload();

        // 提取 MAC 地址 (从 device/esp32s3/ESP32S3-MAC/state 中截取)
        String[] topicParts = topic.split("/");
        String macAddress = topicParts.length > 2 ? topicParts[2] : "UNKNOWN";

        try {
            // JSON 字符串一键反序列化为 Java 对象
            DeviceStateDto dto = objectMapper.readValue(payload, DeviceStateDto.class);

            System.out.println("==================================================");
            System.out.println("[成功解析设备上报数据] 设备ID: " + macAddress);

            // 业务路由分发
            if ("offline".equals(dto.getStatus())) {
                System.out.println("警报：设备已掉线 (LWT遗嘱触发)！");
                // TODO: 去数据库把该设备标记为离线
            } else if ("online".equals(dto.getStatus())) {
                System.out.println("提示：设备重新上线！");
                // TODO: 去数据库把该设备标记为在线
            } else {
                // 如果既不是纯粹的 online 也不是 offline，那就是正常的遥测数据包了
                System.out.println(" 遥测数据 -> 温度: " + dto.getTemp() + "°C | 信号: " + dto.getRssi() + "dBm | 内存: " + dto.getHeapKb() + "KB");
                // TODO: 存入 InfluxDB 时序数据库
            }
            System.out.println("==================================================\n");

        } catch (Exception e) {
            System.err.println("JSON 解析失败！原文: " + payload);
            e.printStackTrace();
        }
    }
}