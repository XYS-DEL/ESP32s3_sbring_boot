package com.iot.esp32.service;

import com.iot.esp32.model.entity.IotDevice;
import com.iot.esp32.repository.IotDeviceMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class DeviceManagerService {

    @Autowired
    private IotDeviceMapper deviceMapper;

    /**
     * 处理设备的状态流转（上线/离线）与自动注册
     */
    public void upsertDeviceStatus(String macAddress, String status) {
        // 1. 去数据库里查查，有没有这台设备
        IotDevice device = deviceMapper.selectById(macAddress);
        LocalDateTime now = LocalDateTime.now();

        if (device == null) {
            // 2. 查无此机：说明这是一台刚出厂、第一次连上服务器的新 ESP32！
            device = new IotDevice();
            device.setMacAddress(macAddress);
            // 自动起个代号，取 MAC 地址最后 4 位
            String shortMac = macAddress.length() > 4 ? macAddress.substring(macAddress.length() - 4) : macAddress;
            device.setDeviceName("未命名节点-" + shortMac);
            device.setStatus(status);
            device.setLastActiveTime(now);
            device.setCreateTime(now);

            // 执行 INSERT
            deviceMapper.insert(device);
            log.info("[自动注册] 发现全新 ESP32 节点并已建档，MAC: {}", macAddress);

        } else {
            // 3. 更新状态
            device.setStatus(status);
            device.setLastActiveTime(now);

            // 执行 UPDATE
            deviceMapper.updateById(device);
            log.info("[状态更新] 设备 {} 状态流转为: {}", macAddress, status);
        }
    }

    /**
     * 获取所有设备的列表
     */
    public List<IotDevice> getAllDevices() {
        // 传入 null 代表没有任何 WHERE 条件，直接 SELECT * FROM iot_device;
        return deviceMapper.selectList(null);
    }
}