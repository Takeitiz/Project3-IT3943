package com.project3.chat.kafka;

import com.project3.chat.dto.MessageDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatProducer {

    private final KafkaTemplate<String, MessageDetailsDto> kafkaTemplate;

    public void publicDirectMessage(MessageDetailsDto messageDetailsDto) {
        Message<MessageDetailsDto> message = MessageBuilder
                .withPayload(messageDetailsDto)
                .setHeader(KafkaHeaders.TOPIC, "chat-topic")
                .build();

        kafkaTemplate.send(message);
    }
}
