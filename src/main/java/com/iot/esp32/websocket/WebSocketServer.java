package com.iot.esp32.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * 实时遥测数据广播
 * 浏览器端通过 ws://localhost:8080/ws/telemetry 连接此端点
 */
@Slf4j
@Component
@ServerEndpoint("/ws/telemetry")
public class WebSocketServer {

    // 线程安全的集合，用来存放目前所有连进来的前端浏览器 Session
    private static final CopyOnWriteArraySet<Session> sessionPool = new CopyOnWriteArraySet<>();

    @OnOpen
    public void onOpen(Session session) {
        sessionPool.add(session);
        log.info("[WebSocket] 新终端接入！当前在线大屏数: {}", sessionPool.size());
    }

    @OnClose
    public void onClose(Session session) {
        sessionPool.remove(session);
        log.info("[WebSocket] 终端断开！当前在线大屏数: {}", sessionPool.size());
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("[WebSocket] 发生错误！", error);
    }

    /**
     * 核心群发方法：向所有在线的浏览器广播最新的设备数据
     * @param message JSON 格式的字符串
     */
    public static void broadcastMessage(String message) {
        for (Session session : sessionPool) {
            try {
                if (session.isOpen()) {
                    session.getAsyncRemote().sendText(message);
                }
            } catch (Exception e) {
                log.error("[WebSocket] 消息推送失败", e);
            }
        }
    }
}