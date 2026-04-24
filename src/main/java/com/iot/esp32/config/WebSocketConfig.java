package com.iot.esp32.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * WebSocket 核心配置类
 */
@Configuration
public class WebSocketConfig {

    /**
     * 注入 ServerEndpointExporter
     * 它的作用是自动扫描并注册所有打上 @ServerEndpoint 注解的 WebSocket 终端
     */
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}