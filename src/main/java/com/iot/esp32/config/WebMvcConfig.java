package com.iot.esp32.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

/**
 * Web 静态资源配置
 * 作用：将项目根目录下的 firmware 文件夹，映射为可供 ESP32 下载的公网 URL
 */
@Slf4j
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保项目根目录下有一个名叫 firmware 的文件夹
        String firmwarePath = System.getProperty("user.dir") + File.separator + "firmware" + File.separator;

        // 映射规则：当访问 http://localhost:8080/fw/xxx.bin 时，直接去本地 firmware 文件夹拿文件
        registry.addResourceHandler("/fw/**")
                .addResourceLocations("file:" + firmwarePath);

        log.info("[FOTA 模块] 固件静态托管目录已挂载: {}", firmwarePath);
    }
}