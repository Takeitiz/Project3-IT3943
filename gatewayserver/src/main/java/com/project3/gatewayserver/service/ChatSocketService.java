package com.project3.gatewayserver.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project3.gatewayserver.dto.Message;
import io.socket.client.IO;
import io.socket.client.Socket;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSocketService {

    private Socket chatSocketClient;
    private final ObjectMapper mapper = new ObjectMapper();

    @Value("${chat-service.socket-url}")
    private String chatServiceUrl;

    @PostConstruct
    public void connect() {
        try {
            IO.Options options = IO.Options.builder()
                    .setReconnection(true)
                    .setReconnectionAttempts(5)
                    .setReconnectionDelay(1000)
                    .setTransports(new String[]{"websocket", "polling"}) // Add this line
                    .build();

            log.info("Attempting to connect to chat service at: {}", chatServiceUrl);
            chatSocketClient = IO.socket(chatServiceUrl, options);

            chatSocketClient.on(Socket.EVENT_CONNECT, args -> {
                log.info("Successfully connected to chat service");
            });

            chatSocketClient.on(Socket.EVENT_DISCONNECT, args -> {
                log.warn("Disconnected from chat service");
                chatSocketClient.connect();
            });

            chatSocketClient.on(Socket.EVENT_CONNECT_ERROR, args -> {
                log.error("Connection error: {}", args[0]);
                if (args[0] instanceof Exception) {
                    ((Exception) args[0]).printStackTrace();
                }
                chatSocketClient.connect();
            });

            chatSocketClient.on("message received", args -> {
                if (args != null && args[0] instanceof Message) {
                    // add thêm phần connected từ front end và api gate way
                    // this.io.emit("message receive", args)
                }
            });

            chatSocketClient.on("message updated", args -> {
                if (args != null && args[0] instanceof Message) {
                    // add thêm phần connected từ front end và api gate way
                    // this.io.emit("message updated", args)
                }
            });

            chatSocketClient.connect();

        } catch (Exception e) {
            log.error("Error initializing socket connection", e);
        }
    }

    @PreDestroy
    public void disconnect() {
        if (chatSocketClient != null) {
            log.info("Disconnecting from chat service");
            chatSocketClient.disconnect();
        }
    }
}