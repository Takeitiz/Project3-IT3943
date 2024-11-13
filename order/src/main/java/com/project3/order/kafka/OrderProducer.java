package com.project3.order.kafka;

import com.project3.order.dto.OrderMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, OrderMessageDto> kafkaTemplate;

    public void sendOrderEmailTopic(OrderMessageDto orderMessageDto) {
        Message<OrderMessageDto> message = MessageBuilder
                .withPayload(orderMessageDto)
                .setHeader(KafkaHeaders.TOPIC, "order-email-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendUserSellerTopic(OrderMessageDto orderMessageDto) {
        Message<OrderMessageDto> message = MessageBuilder
                .withPayload(orderMessageDto)
                .setHeader(KafkaHeaders.TOPIC, "user-seller-topic")
                .build();

        kafkaTemplate.send(message);
    }

    public void sendUserBuyerTopic(OrderMessageDto orderMessageDto) {
        Message<OrderMessageDto> message = MessageBuilder
                .withPayload(orderMessageDto)
                .setHeader(KafkaHeaders.TOPIC, "user-buyer-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
