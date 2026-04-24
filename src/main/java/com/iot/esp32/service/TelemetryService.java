package com.iot.esp32.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.iot.esp32.model.entity.TelemetryData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class TelemetryService {

    @Autowired
    private InfluxDBClient influxDBClient;

    /**
     * 将遥测数据异步/同步写入 InfluxDB
     */
    public void saveTelemetry(String macAddress, Float temp, Integer heapKb, Integer rssi) {
        try {
            // 1. 组装时序数据点
            TelemetryData data = new TelemetryData();
            data.setMacAddress(macAddress);
            data.setTemp(temp);
            data.setHeapKb(heapKb);
            data.setRssi(rssi);
            data.setTime(Instant.now()); // 打上当前精准的 UTC 时间戳

            // 2. 获取写入 API 并执行写入 (微秒级精度)
            WriteApiBlocking writeApi = influxDBClient.getWriteApiBlocking();
            writeApi.writeMeasurement(WritePrecision.US, data);

            // 为了保持控制台清爽，这里可以不用 println，写入极其快速静默
            // log.info("[InfluxDB 落盘] 设备 {} 数据已记录", macAddress);

        } catch (Exception e) {
            log.error("写入 InfluxDB 失败！", e);
        }
    }
}