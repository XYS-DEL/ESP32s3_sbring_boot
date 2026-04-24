package com.iot.esp32.controller;

import com.iot.esp32.model.Result;
import com.iot.esp32.model.entity.IotDevice;
import com.iot.esp32.service.DeviceControlService;
import com.iot.esp32.service.DeviceManagerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
        return "🎯 指令已发送至设备: " + mac;
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
}