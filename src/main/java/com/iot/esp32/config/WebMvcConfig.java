package com.iot.esp32.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 静态资源配置
 * 作用：将项目根目录下的 firmware 文件夹，映射为可供 ESP32 下载的公网 URL
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 以 jar 包所在目录为根，映射 firmware 文件夹
        registry.addResourceHandler("/fw/**")
                .addResourceLocations("file:firmware/");

        log.info("[FOTA 模块] 固件静态托管目录已挂载: ./firmware/");
    }
}