package com.project3.chat.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {

    private final SocketIOServer server;

    @PostConstruct
    public void startServer() {
        server.start();
        log.info("Socket.IO server started on port {}", server.getConfiguration().getPort());
    }

    @PreDestroy
    public void stopServer() {
        server.stop();
        log.info("Socket.IO server stopped");
    }


}
