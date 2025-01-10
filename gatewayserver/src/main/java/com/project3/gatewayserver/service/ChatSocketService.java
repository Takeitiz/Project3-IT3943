package com.project3.gatewayserver.service;

import com.corundumstudio.socketio.SocketIOServer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project3.gatewayserver.dto.*;
import io.socket.client.IO;
import io.socket.client.Socket;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.net.URISyntaxException;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatSocketService {

    private final RedisService redisService;
    private Socket chatSocketClient;
    private Socket orderSocketClient;
    private final ObjectMapper mapper = new ObjectMapper();
    private final SocketIOServer socketIOServer;

    @Value("${chat-service.socket-url}")
    private String chatServiceUrl;

    @Value("${order-service.socket-url}")
    private String orderServiceUrl;

    @PostConstruct
    public void init() {
        setupSocketServer();
        connectToExternalServices();
    }

    private void setupSocketServer() {
        // Handle client connections
        socketIOServer.addConnectListener(client -> {
            log.info("Client connected: {}", client.getSessionId());
        });

        // Handle client disconnections
        socketIOServer.addDisconnectListener(client -> {
            log.info("Client disconnected: {}", client.getSessionId());
        });

        // Handle getLoggedInUsers event
        socketIOServer.addEventListener("getLoggedInUsers", Object.class, (client, data, ackRequest) -> {
            List<String> users = redisService.getLoggedInUsersFromCache("loggedInUsers");
            System.out.println(users);
            socketIOServer.getBroadcastOperations().sendEvent("online", users);
        });

        // Handle loggedInUsers event
        socketIOServer.addEventListener("loggedInUsers", String.class, (client, username, ackRequest) -> {
            List<String> users = redisService.saveLoggedInUserToCache("loggedInUsers", username);
            socketIOServer.getBroadcastOperations().sendEvent("online", users);
        });

        // Handle removeLoggedInUser event
        socketIOServer.addEventListener("removeLoggedInUser", String.class, (client, username, ackRequest) -> {
            List<String> users = redisService.removeLoggedInUserFromCache("loggedInUsers", username);
            System.out.println(users);
            socketIOServer.getBroadcastOperations().sendEvent("online", users);
        });

        // Handle category selection
        socketIOServer.addEventListener("category", CategorySelection.class, (client, data, ackRequest) -> {
            System.out.println("Save to category");
            redisService.saveUserSelectedCategory("selectedCategories:" + data.getUsername(), data.getCategory());
        });

        socketIOServer.start();
    }

    private void connectToExternalServices() {
        try {
            IO.Options options = IO.Options.builder()
                    .setReconnection(true)
                    .setReconnectionAttempts(5)
                    .setReconnectionDelay(1000)
                    .setTransports(new String[]{"websocket", "polling"})
                    .build();

            setupChatServiceSocket(options);
            setupOrderServiceSocket(options);

        } catch (Exception e) {
            log.error("Error initializing external socket connections", e);
        }
    }

    private void setupChatServiceSocket(IO.Options options) throws URISyntaxException {
        log.info("Attempting to connect to Chat Service at: {}", chatServiceUrl);
        chatSocketClient = IO.socket(chatServiceUrl, options);

        chatSocketClient.on(Socket.EVENT_CONNECT, args ->
                log.info("Successfully connected to Chat Service"));

        chatSocketClient.on(Socket.EVENT_DISCONNECT, args -> {
            log.warn("Disconnected from Chat Service");
            chatSocketClient.connect();
        });

        chatSocketClient.on(Socket.EVENT_CONNECT_ERROR, args -> {
            log.error("Chat Service connection error: {}", args[0]);
            chatSocketClient.connect();
        });

        // Custom events
        chatSocketClient.on("message received", args -> {
            try {
                JSONObject jsonObject = (JSONObject) args[0];
                String jsonString = jsonObject.toString();
                Message message = mapper.readValue(jsonString, Message.class);
                System.out.println(message);
                socketIOServer.getBroadcastOperations().sendEvent("message received", message);
            } catch (Exception e) {
                log.error("Error processing message received event", e);
            }
        });

        chatSocketClient.on("message updated", args -> {
            try {
                JSONObject jsonObject = (JSONObject) args[0];
                String jsonString = jsonObject.toString();
                Message message = mapper.readValue(jsonString, Message.class);
                socketIOServer.getBroadcastOperations().sendEvent("message updated", message);
            } catch (Exception e) {
                log.error("Error processing message updated event", e);
            }
        });

        chatSocketClient.connect();
    }

    private void setupOrderServiceSocket(IO.Options options) throws URISyntaxException {
        log.info("Attempting to connect to Order Service at: {}", orderServiceUrl);
        orderSocketClient = IO.socket(orderServiceUrl, options);

        orderSocketClient.on(Socket.EVENT_CONNECT, args ->
                log.info("Successfully connected to Order Service"));

        orderSocketClient.on(Socket.EVENT_DISCONNECT, args -> {
            log.warn("Disconnected from Order Service");
            orderSocketClient.connect();
        });

        orderSocketClient.on(Socket.EVENT_CONNECT_ERROR, args -> {
            log.error("Order Service connection error: {}", args[0]);
            orderSocketClient.connect();
        });

        orderSocketClient.on("order notification", args -> {
            try {
                JSONObject jsonObject = (JSONObject) args[0];
                String jsonString = jsonObject.toString();
                Order order = mapper.readValue(jsonString, Order.class);
                jsonObject = (JSONObject) args[1];
                jsonString = jsonObject.toString();
                OrderNotification notification = mapper.readValue(jsonString, OrderNotification.class);
                socketIOServer.getBroadcastOperations().sendEvent("order notification",
                        new OrderNotificationEvent(order, notification));
            } catch (Exception e) {
                log.error("Error processing order notification event", e);
            }
        });

        orderSocketClient.connect();
    }

    @PreDestroy
    public void destroy() {
        if (socketIOServer != null) {
            socketIOServer.stop();
        }
        if (chatSocketClient != null) {
            chatSocketClient.disconnect();
            chatSocketClient.close();
        }
        if (orderSocketClient != null) {
            orderSocketClient.disconnect();
            orderSocketClient.close();
        }
    }
}