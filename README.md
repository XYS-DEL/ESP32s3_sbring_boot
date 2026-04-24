# 🚀 ESP32-S3 IoT Central Command (Java Backend)

## esp32硬件仓库地址：https://github.com/XYS-DEL/ESP32-S3-MQTT-Boilerplate

> 这是一个专为 ESP32-S3 节点量身打造的 Java Spring Boot 物联网后端中台。采用非阻塞的异步事件驱动架构与 Spring Integration 管道，完美接管边缘设备的双向全双工通信、高可用数据流转以及 LWT 遗嘱生命周期追踪。
> 
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![Language](https://img.shields.io/badge/Language-Java%2021%20LTS-b07219.svg)](https://openjdk.org/projects/jdk/21/)
[![Framework](https://img.shields.io/badge/Framework-Spring%20Boot%203.x-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Database](https://img.shields.io/badge/Database-MySQL%208-4479A1.svg)]()
[![TSDB](https://img.shields.io/badge/TSDB-InfluxDB%20V2-22ADF6.svg)]()

## 📖 项目简介 (Overview)

本项目是为 ESP32-S3 物联网节点量身打造的 Java Spring Boot 后端中台。  
系统采用非阻塞的异步事件驱动架构，通过 MQTT 协议与边缘设备进行全双工通信，负责设备生命周期管理、海量遥测数据接入、远程指令下发以及 FOTA 固件统筹分发。

目前系统已对接运行着 FreeRTOS 与 LittleFS 离线缓存机制的 ESP32-S3 硬件，具备极高的网络容灾与反假死能力，并已全面打通双数据库持久化架构。

## ✨ 核心特性 (Key Features)

- 🔌 ** MQTT 集成**：基于 `spring-integration-mqtt` 搭建底层管道，实现出入站 (Inbound/Outbound) 消息通道解耦与连接池自动重连。
- 🔄 **设备数字孪生 (Device Twin)**：全面接管 MQTT LWT（遗嘱机制），精准识别设备断线（Offline）、重连（Online）状态。自动解析 MAC 地址并在 MySQL 中完成设备免密注册与状态流转。
- 🌊 **海量时序吞吐引擎**：集成 InfluxDB V2 官方 SDK，将单片机高频上报的温度、内存、RSSI 信号等遥测数据，以纳秒级时间戳精准无阻塞打入时序数据库。
- 📦 ** API 规范**：封装标准化 `Result<T>` 统一响应结构，对外暴露 RESTful 接口（如设备列表大盘查询），为前端 Vue/React 数据大屏提供标准支持。
- ⚔️ **端云双向全双工控制**：利用 `@MessagingGateway` 实现面向接口的下行指令路由，支持通过 HTTP API 毫秒级反向控制硬件边缘节点的物理外设（如 RGB 调色）。
- 🛡️ **高可用防御与日志追踪**：定制化 Broker Keep-Alive 与 Socket 超时策略；全面采用 `@Slf4j` 日志体系接管系统流转记录。

## 🛠️ 技术栈选型 (Tech Stack)

- **核心框架**：Java 21 LTS + Spring Boot 3.x
- **协议接入层**：Eclipse Paho + Spring Integration MQTT
- **持久化双擎**：
   - **MySQL 8 + MyBatis-Plus** (负责设备元数据、生命周期状态落盘)
   - **InfluxDB 2.7** (负责超高频物理传感器遥测数据落盘)
- **数据处理层**：Jackson (JSON 解析) + Lombok (实体简化)

## 📂 核心目录结构 (Directory Structure)
```plaintext
src/main/java/com/iot/esp32
├── config/                          # 核心配置层 | MQTT通道、InfluxDB客户端全局组件
├── mqtt/                            # 协议接入层 | MQTT消息监听、网关转发
├── controller/                      # HTTP接口层 | RESTful 业务API (如设备控制、列表查询)
├── service/                         # 业务逻辑层 | 设备路由、持久化分发、指令构建
├── model/
│   ├── entity/                      # 持久化实体 | MySQL与InfluxDB的表映射 (Measurement)
│   └── dto/                         # 传输对象   | 设备上报报文结构化映射
└── repository/                      # 数据访问层 | MyBatis-Plus Mapper 接口
```
# 📡 MQTT 主题规范 (Topic Specifications)
本系统与边缘硬件遵循以下严格的主题路由规范：

## 1. 上行数据流 (设备 -> 云端)
Topic: `device/esp32s3/+/state` (使用通配符捕获全局心跳)

Payload 示例:

```json
// 常规遥测包 (Telemetry -> 流入 InfluxDB)
{"temp": 40.4, "heap_kb": 226, "uptime": "0d 1h 5m", "uptime_sec": 3900, "rssi": -45, "status": "online_OTA_V2", "ip": "10.0.0.15"}
```
## 2. 下行数据流 (云端 -> 设备)
Topic: `device/esp32s3/+/command` (使用通配符捕获全局指令)
Payload 示例:
    
```json
// 硬件控制 (RGB 调色)
{"r": 255, "g": 0, "b": 0}

// 远程 OTA 触发 (研发中)
{"cmd": "ota", "url": "http://domain.com/fw/firmware_v2.bin"}
 ```

# 🚀 快速启动 (Getting Started)

## 环境要求 (Prerequisites)
- JDK 21 或更高版本
- Maven 3.8+
- 可用的 MQTT Broker (例如本地运行的 EMQX)
- MySQL 8.x 实例 (建库 `esp32_iot`)
- InfluxDB 2.x 实例 (获取 Token、Org 与 Bucket)

## 开发者极速上手指南 (Quick Start)

为了让本项目在你的本地环境中顺利运行，请在启动前后端之前，仔细核对并修改以下暴露的配置项：

###  1. 云端中台配置 (Spring Boot Backend)

**文件路径**: `src/main/resources/application.yml`

* **MySQL 数据库配置**:
  提前在本地创建名为 `esp32_iot` 的数据库。
    ```yaml
    spring.datasource.username: root           # 替换为你的 MySQL 账号
    spring.datasource.password: your_password  # 替换为你的 MySQL 密码
    ```
* **时序引擎配置 (InfluxDB V2)**:
  确保 InfluxDB 2.x 已在本地运行（默认端口 8086），并生成全权限 Token。
    ```yaml
    spring.influx.token: "你的 API Token"
    spring.influx.org: "你的组织名 (如 iot_org)"
    spring.influx.bucket: "你的存储桶 (如 esp32_telemetry)"
    ```
* **MQTT 管道配置**:
    ```yaml
    mqtt.url: tcp://127.0.0.1:1883  # 替换为你实际的 MQTT Broker 地址
    ```

**文件路径**: `src/main/java/com/iot/esp32/controller/DeviceController.java`

* **FOTA 局域网分发 IP (极其关键)**:
  用于拼接固件的下载 URL。必须修改为运行 Spring Boot 的电脑在当前局域网下的真实 IP（例如 `192.168.31.x`），否则 ESP32 将无法找到服务器拉取固件。
    ```java
    // 在 triggerOta 方法中修改
    String localIp = "192.168.x.x"; 
    ```

###  2. 边缘节点配置 (ESP32-S3 C++ 端)

**文件路径**: `src/config.h` (或你定义常量的头文件)

* **物理网络与网关**:
    ```cpp
    #define WIFI_SSID "你的_WiFi_名称"
    #define WIFI_PASSWORD "你的_WiFi_密码"
    #define MQTT_BROKER "192.168.x.x"  # 指向运行 MQTT Broker 的服务器 IP
    ```

**文件路径**: `platformio.ini`

* **局域网 OTA 无线烧录**:
  当需要通过 IDE 进行局域网极速推送固件时，确保该配置与代码中生成的 `clientId` 一致。
    ```ini
    ; 确保该地址能够被 mDNS 解析，或直接替换为 ESP32 所在的局域网 IP
    upload_port = ESP32S3-E83DC1FA71BC.local 
    upload_flags = --auth=admin123
    ```
###  3. FOTA 空中升级实战演练 (WAN OTA Pipeline)

要想体验本项目最硬核的广域网无感升级（脱离局域网与数据线），请按以下标准工业流程操作：

1. **埋设新版特征**：在 ESP32 的 C++ 代码中修改版本标识（例如将 `publishStatusWithData` 中上报的状态改为 `"status": "online_OTA_V3"`）。
2. **仅编译不烧录**：拔掉数据线！在 PlatformIO 或 Arduino IDE 中点击 **Build（编译）**，并在工程的 build 目录下提取生成的 `.bin` 二进制文件。
3. **固件云端托管**：将该 `.bin` 文件重命名（如 `v3.bin`），并直接丢进 Spring Boot 项目根目录下的 `firmware` 文件夹中（系统已将其映射为静态资源 URL）。
4. **一键核爆触发**：在浏览器或 Postman 中调用以下 HTTP 接口，向指定设备下发升级指令（注意替换 MAC 地址与固件文件名）：
   ```http
   GET http://localhost:8080/api/device/{填入设备真实MAC}/ota?version=V3.0&fileName=v3.bin