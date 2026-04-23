package com.iot.esp32.config;

import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

@Configuration
public class MqttConfig {

    // 从 application.yml 中动态注入配置参数
    @Value("${mqtt.url}")
    private String mqttUrl;

    @Value("${mqtt.client-id}")
    private String clientId;

    @Value("${mqtt.topic.state-sub}")
    private String stateSubTopic;

    @Value("${mqtt.completion-timeout}")
    private int completionTimeout;

    @Value("${mqtt.keep-alive}")
    private int keepAlive;

    /**
     * 1. MQTT 客户端 (底层引擎)
     * 负责管理和 EMQX 服务器的底层 TCP 连接、自动重连机制
     */
    @Bean
    public MqttPahoClientFactory mqttClientFactory() {
        DefaultMqttPahoClientFactory factory = new DefaultMqttPahoClientFactory();
        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[]{mqttUrl});
        options.setCleanSession(true);
        options.setKeepAliveInterval(keepAlive);
        options.setAutomaticReconnect(true); // 极其重要：后端断网自动重连
        // 如果你的 Broker 有密码，取消下面两行的注释
        // options.setUserName("admin");
        // options.setPassword("password".toCharArray());

        factory.setConnectionOptions(options);
        return factory;
    }

    // ==========================================
    // 入站组件：负责接收 ESP32 发来的状态数据
    // ==========================================

    /**
     * 管道 1：定义入站消息通道
     */
    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * 适配器：将 MQTT 的 Topic 监听器绑定到我们的消息通道内
     */
    @Bean
    public MessageProducer inbound() {
        // 创建入站适配器，监听指定的通配符主题
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        clientId + "-inbound", // 入站端专用的 ClientID 后缀
                        mqttClientFactory(),
                        stateSubTopic);

        adapter.setCompletionTimeout(completionTimeout);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(0); // 对应 ESP32 端的 QoS 0
        adapter.setOutputChannel(mqttInputChannel()); // 将收到的消息倒进上面定义的进水管
        return adapter;
    }

    // ==========================================
    // 出站组件：负责向 ESP32 发送控制指令/OTA 链接
    // ==========================================

    /**
     * 管道 2：定义出站消息通道
     */
    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * 处理器：将消息通道里的数据推送到 MQTT 服务器
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound() {
        MqttPahoMessageHandler messageHandler =
                new MqttPahoMessageHandler(clientId + "-outbound", mqttClientFactory());

        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(0);
        return messageHandler;
    }
}