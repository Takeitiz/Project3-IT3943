package com.project3.auth.kafka;

import com.project3.auth.dto.AuthBuyerMessageDetailsDto;
import com.project3.auth.dto.AuthEmailMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthProducer {
    private final KafkaTemplate<String, AuthBuyerMessageDetailsDto> kafkaTemplate1;
    private final KafkaTemplate<String, AuthEmailMessageDto> kafkaTemplate2;

    public void sendUserBuyerTopic(AuthBuyerMessageDetailsDto authBuyerMessageDetailsDto) {
        Message<AuthBuyerMessageDetailsDto> message = MessageBuilder
                .withPayload(authBuyerMessageDetailsDto)
                .setHeader(KafkaHeaders.TOPIC, "user-buyer-auth-topic")
                .build();

        kafkaTemplate1.send(message);
    }

    public void sendAuthEmailTopic(AuthEmailMessageDto authEmailMessageDto) {
        Message<AuthEmailMessageDto> message = MessageBuilder
                .withPayload(authEmailMessageDto)
                .setHeader(KafkaHeaders.TOPIC, "auth-email-topic")
                .build();

        kafkaTemplate2.send(message);
    }
}
