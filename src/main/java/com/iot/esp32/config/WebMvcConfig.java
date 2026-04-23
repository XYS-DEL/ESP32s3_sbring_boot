/*
package com.iot.esp32.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${iot.storage.local.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 核心：把 URL 路径 /fw/download/** 映射到硬盘的 uploadDir 文件夹
        // 注意：file: 前缀表示这是一个物理磁盘路径
        String path = "file:" + new File(uploadDir).getAbsolutePath() + "/";
        registry.addResourceHandler("/fw/download/**")
                .addResourceLocations(path);
    }
}
 */