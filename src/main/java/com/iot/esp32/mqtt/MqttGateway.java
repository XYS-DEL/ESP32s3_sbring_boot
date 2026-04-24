package com.iot.esp32.mqtt;

import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

/**
 * MQTT 下行指令网关
 * 它的作用是把 Java 方法的调用，路由到我们在 MqttConfig 中定义的 mqttOutboundChannel 出站通道
 */
@Component
@MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
public interface MqttGateway {

    /**
     * 发送指令到指定的主题
     * @param topic 目标主题 (动态指定)
     * @param payload 消息体 (JSON 字符串)
     */
    void sendToMqtt(@Header(MqttHeaders.TOPIC) String topic, String payload);
}