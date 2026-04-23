package com.iot.esp32;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.integration.config.EnableIntegration;

/**
 * IoT 后端中台启动类
 * 这是整个 Java 程序的入口点
 */
@SpringBootApplication
@EnableIntegration // 显式开启 Spring Integration 模块
public class IotBackendApplication {

    public static void main(String[] args) {
        // 启动 Spring 容器，初始化我们在 MqttConfig 中定义的各种管道和 Bean
        SpringApplication.run(IotBackendApplication.class, args);

        System.out.println("\n==================================================");
        System.out.println("ESP32 物联网后端中台启动成功！");
        System.out.println("正在监听 EMQX 数据动脉...");
        System.out.println("==================================================\n");
    }

}