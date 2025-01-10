package com.project3.users.kafka;

import com.project3.users.dto.ReviewMessageDetailsDto;
import com.project3.users.dto.SeedMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserProducer {
    private final KafkaTemplate<String, ReviewMessageDetailsDto> kafkaTemplate1;
    private final KafkaTemplate<String, SeedMessageDto> kafkaTemplate2;

    public void sendUpdateGigTopic(ReviewMessageDetailsDto reviewMessageDetailsDto) {
        Message<ReviewMessageDetailsDto> message = MessageBuilder
                .withPayload(reviewMessageDetailsDto)
                .setHeader(KafkaHeaders.TOPIC, "update-gig-topic")
                .build();

        kafkaTemplate1.send(message);
    }

    public void sendSeedGigTopic(SeedMessageDto seedMessageDto) {
        Message<SeedMessageDto> message = MessageBuilder
                .withPayload(seedMessageDto)
                .setHeader(KafkaHeaders.TOPIC, "seed-gig-topic")
                .build();

        kafkaTemplate2.send(message);
    }
}
