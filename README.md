# 🚀 ESP32-S3 IoT Central Command (Java Backend)

> 这是一个专为 ESP32-S3 工业级节点量身打造的 Java Spring Boot 物联网后端中台。采用非阻塞的异步事件驱动架构与 Spring Integration 管道，完美接管边缘设备的双向全双工通信、高可用数据流转以及 LWT 遗嘱生命周期追踪。

[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language](https://img.shields.io/badge/Language-Java%2021%20LTS-b07219.svg)](https://openjdk.org/projects/jdk/21/)
[![Framework](https://img.shields.io/badge/Framework-Spring%20Boot%203.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Integration](https://img.shields.io/badge/Integration-Spring%20MQTT-red.svg)]()
## 📖 项目简介 (Overview)

本项目是为 ESP32-S3 工业级物联网节点量身打造的 Java Spring Boot 后端中台。  
系统采用非阻塞的异步事件驱动架构，通过 MQTT 协议与边缘设备进行全双工通信，负责设备生命周期管理、海量遥测数据接入、远程指令下发以及 FOTA 固件统筹分发。

目前系统已完美对接运行着 FreeRTOS 与 LittleFS 离线缓存机制的 ESP32-S3 硬件，具备极高的网络容灾与反假死能力。


## ✨ 核心特性 (Key Features)

- 🔌 **企业级 MQTT 集成**：基于 spring-integration-mqtt 搭建底层管道，实现出入站 (Inbound/Outbound) 消息通道解耦与连接池自动重连机制。
- 🔄 **设备数字孪生 (Device Twin)**：全面接管 MQTT LWT（遗嘱机制），精准识别设备断线（Offline）、重连（Online）状态，实时追踪边缘节点生命周期。
- 📦 **动态数据结构化映射**：通过 Jackson 框架与强大的容错注解，将单片机上报的 C 风格文本 JSON 实时反序列化为安全的 Java POJO (DeviceStateDto)。
- 🛡️ **高可用与防抖动防御**：定制化 Broker Keep-Alive 与 Socket 超时策略，完美适配边缘设备的 TLS 1.2 加密开销与弱网环境，消除连接假死与抖动。
- 🚀 **FOTA 固件分发就绪 [挂起状态]**：预留基于 HTTP 静态资源托管的广域网 OTA 升级接口层，支持按设备 MAC 地址精准推送下载 URL。

## 🛠️ 技术栈选型 (Tech Stack)

- **核心框架**：Java 21 LTS + Spring Boot 3.x
- **协议接入层**：Eclipse Paho + Spring Integration MQTT
- **数据处理层**：Jackson (JSON 解析) + Lombok (实体简化)
- **规划数据库**：MySQL (设备元数据) + InfluxDB (时序遥测) + Redis (高频缓存)

## 📂 核心目录结构 (Directory Structure)
```plaintext
src/main/java/com/iot/esp32
├── config/                          # 核心配置层 | MQTT通道、连接池、全局组件
├── mqtt/                            # 协议接入层 | MQTT消息监听、消费逻辑
├── controller/                      # HTTP接口层 | REST 业务接口（规划中）
├── service/                         # 业务逻辑层 | 设备路由、指令下发、固件管理
├── model/
│   ├── entity/                      # 数据库持久化实体
│   └── dto/                         # 数据传输对象 | 设备上报报文结构化映射
└── repository/                      # 数据访问层 | 持久化操作（规划中）
```

## 📡 MQTT 主题规范 (Topic Specifications)

本系统与边缘硬件遵循以下严格的主题路由规范：

### 1. 上行数据流 (设备 -> 云端)

**Topic**: `device/esp32s3/+/state` (使用通配符捕获全局心跳)

**Payload 示例**:

```json
// 常规遥测包 (Telemetry)
{"temp": 40.4, "heap_kb": 226, "uptime": "0d 1h 5m", "uptime_sec": 3900, "rssi": -45, "status": "online_OTA_V2", "ip": "10.0.0.15"}

// 遗嘱/上线包 (LWT)
{"status": "offline"}
```
### 2. 下行控制流 (云端 -> 设备)
**Topic**: `device/esp32s3/{MAC_ADDRESS}/command`

**Payload 示例**:
```json
// 硬件控制 (RGB 调色)
{"r": 255, "g": 0, "b": 0}

// 远程 OTA 触发 (研发中)
{"cmd": "ota", "url": "http://domain.com/fw/firmware_v2.bin"}
```
## 🚀 快速启动 (Getting Started)

### 环境要求 (Prerequisites)

- JDK 21 或更高版本
- Maven 3.8+
- 可用的 MQTT Broker (例如本地运行的 EMQX 或线上服务器)

### 运行步骤

1. 克隆本仓库到本地。
2. 打开 `src/main/resources/application.yml`，修改 MQTT 服务器地址：

    ```yaml
    mqtt:
      url: tcp://你的EMQX服务器IP:1883
      # 如有密码，请在 MqttConfig.java 中取消对应代码的注释
    ```

3. 在 IDEA 中刷新 Maven 依赖。
4. 运行 `IotBackendApplication.java`。
5. 为 ESP32 设备上电，观察 IDEA 控制台，见证数据流的汇合！

## 🗺️ 未来路线图 (Roadmap)

- [ ] **Phase 1**: 集成 InfluxDB，将解析出的 DeviceStateDto 遥测数据落盘。
- [ ] **Phase 2**: 集成 Spring WebSocket，将实时温度向前端大屏推送。
- [ ] **Phase 3**: 编写下行网关接口，实现 Web 端对设备板载 RGB 灯的颜色反向控制。
- [ ] **Phase 4**: 唤醒并完善 FOTA (Firmware Over-The-Air) 静态资源分发模块。

Built with ❤️ for High-Availability IoT Systems.