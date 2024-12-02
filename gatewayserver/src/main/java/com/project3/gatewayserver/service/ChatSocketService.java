//package com.project3.gatewayserver.service;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.project3.gatewayserver.dto.Message;
//import com.project3.gatewayserver.dto.Notification;
//import com.project3.gatewayserver.dto.Order;
//import io.socket.client.IO;
//import io.socket.client.Socket;
//import jakarta.annotation.PostConstruct;
//import jakarta.annotation.PreDestroy;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//@Service
//@RequiredArgsConstructor
//@Slf4j
//public class ChatSocketService {
//
//    private Socket chatSocketClient;
//    private Socket orderSocketClient;
//    private final ObjectMapper mapper = new ObjectMapper();
//
//    @Value("${chat-service.socket-url}")
//    private String chatServiceUrl;
//
//    @Value("${order-service.socket-url}")
//    private String orderServiceUrl;
//
//    @PostConstruct
//    public void connect() {
//        try {
//            IO.Options options = IO.Options.builder()
//                    .setReconnection(true)
//                    .setReconnectionAttempts(5)
//                    .setReconnectionDelay(1000)
//                    .setTransports(new String[]{"websocket", "polling"})
//                    .build();
//
//            // === Connect to Chat Service ===
//            log.info("Attempting to connect to Chat Service at: {}", chatServiceUrl);
//            chatSocketClient = IO.socket(chatServiceUrl, options);
//
//            chatSocketClient.on(Socket.EVENT_CONNECT, args -> {
//                log.info("Successfully connected to Chat Service");
//            });
//
//            chatSocketClient.on(Socket.EVENT_DISCONNECT, args -> {
//                log.warn("Disconnected from Chat Service");
//            });
//
//            chatSocketClient.on(Socket.EVENT_CONNECT_ERROR, args -> {
//                log.error("Chat Service connection error: {}", args[0]);
//                if (args[0] instanceof Exception) {
//                    ((Exception) args[0]).printStackTrace();
//                }
//            });
//
//            chatSocketClient.on("message received", args -> {
//                if (args != null && args[0] instanceof Message) {
//                    // add thêm phần connected từ front end và api gate way
//                    // this.io.emit("message receive", args)
//                }
//            });
//
//            chatSocketClient.on("message updated", args -> {
//                if (args != null && args[0] instanceof Message) {
//                    // add thêm phần connected từ front end và api gate way
//                    // this.io.emit("message updated", args)
//                }
//            });
//
//            chatSocketClient.connect();
//
//            // === Connect to Order Service ===
//            log.info("Attempting to connect to Order Service at: {}", orderServiceUrl);
//            orderSocketClient = IO.socket(orderServiceUrl, options);
//
//            orderSocketClient.on(Socket.EVENT_CONNECT, args -> {
//                log.info("Successfully connected to Order Service");
//            });
//
//            orderSocketClient.on(Socket.EVENT_DISCONNECT, args -> {
//                log.warn("Disconnected from Order Service");
//            });
//
//            orderSocketClient.on(Socket.EVENT_CONNECT_ERROR, args -> {
//                log.error("Order Service connection error: {}", args[0]);
//                if (args[0] instanceof Exception) {
//                    ((Exception) args[0]).printStackTrace();
//                }
//            });
//
//            orderSocketClient.on("order notification", args -> {
//                if (args != null && args[0] instanceof Order && args[1] instanceof Notification) {
//                    // add thêm phần connected từ front end và api gate way
//                    // this.io.emit("message updated", args)
//                }
//            });
//
//            orderSocketClient.connect();
//
//        } catch (Exception e) {
//            log.error("Error initializing socket connection", e);
//        }
//    }
//
//    @PreDestroy
//    public void disconnect() {
//        if (chatSocketClient != null) {
//            log.info("Disconnecting from Chat Service");
//            chatSocketClient.disconnect();
//            chatSocketClient.close();
//        }
//        if (orderSocketClient != null) {
//            log.info("Disconnecting from Order Service");
//            orderSocketClient.disconnect();
//            orderSocketClient.close();
//        }
//    }
//}