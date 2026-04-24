package com.iot.esp32.controller;

import com.iot.esp32.model.Result;
import com.iot.esp32.model.entity.IotDevice;
import com.iot.esp32.service.DeviceControlService;
import com.iot.esp32.service.DeviceManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/device")
public class DeviceController {

    @Autowired
    private DeviceControlService deviceControlService;

    // 注入设备管理服务
    @Autowired
    private DeviceManagerService deviceManagerService;

    /**
     * 原有的：控制 RGB 灯光接口
     */
    @GetMapping("/{mac}/color")
    public String setDeviceColor(@PathVariable("mac") String mac, @RequestParam int r, @RequestParam int g, @RequestParam int b) {
        deviceControlService.changeDeviceColor(mac, r, g, b);
        return "指令已发送至设备: " + mac;
    }

    /**
     * 获取所有设备列表的 RESTful 接口
     * 测试方式：浏览器访问 http://localhost:8080/api/device/list
     */
    @GetMapping("/list")
    public Result<List<IotDevice>> getDeviceList() {
        // 1. 去数据库查询所有设备
        List<IotDevice> deviceList = deviceManagerService.getAllDevices();

        // 2. 用刚才写的统一包装外壳包起来返回给前端
        return Result.success(deviceList);
    }
    /**
     * 触发设备进行 OTA 空中升级
     * 测试示例: GET http://localhost:8080/api/device/E83DC1FA71BC/ota?version=V2.0&fileName=firmware_v2.bin
     */
    @GetMapping("/{mac}/ota")
    public Result<String> triggerOta(
            @PathVariable("mac") String mac,
            @RequestParam String version,
            @RequestParam String fileName) {

        // 动态拼装局域网下载地址 (注意：在真实服务器上，这里应替换为服务器的公网 IP 或域名)
        // 这里是你电脑在局域网的 IP，ESP32 能通过这个 IP 找到你电脑
        String localIp = " IP 地址";
        String downloadUrl = "http://" + localIp + ":8080/fw/" + fileName;

        deviceControlService.sendOtaCommand(mac, version, downloadUrl);
        return Result.success("FOTA 升级指令已定向推送至: " + mac);
    }
}