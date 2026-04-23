/*

package com.iot.esp32.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class LocalStorageServiceImpl implements StorageService {

    @Value("${iot.storage.local.upload-dir}")
    private String uploadDir;

    @Value("${iot.storage.local.download-url-prefix}")
    private String downloadPrefix;

    @Override
    public String store(MultipartFile file, String version) {
        try {
            // 1. 确保目录存在
            File folder = new File(uploadDir);
            if (!folder.exists()) folder.mkdirs();

            // 2. 构造文件名 (例如: firmware_v2.bin)
            String fileName = "firmware_" + version + ".bin";
            Path targetPath = Paths.get(uploadDir).resolve(fileName);

            // 3. 保存到硬盘
            file.transferTo(targetPath.toFile());

            // 4. 返回直链：前缀 + 文件名
            return downloadPrefix + fileName;
        } catch (IOException e) {
            throw new RuntimeException("固件保存失败", e);
        }
    }
}

*/