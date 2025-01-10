package com.project3.gigs.kafka;

import com.project3.gigs.dto.UpdateGigCountMessageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GigProducer {
    private final KafkaTemplate<String, UpdateGigCountMessageDto> kafkaTemplate;

    public void sendUpdateGigCountTopic(UpdateGigCountMessageDto updateGigCountMessageDto) {
        Message<UpdateGigCountMessageDto> message = MessageBuilder
                .withPayload(updateGigCountMessageDto)
                .setHeader(KafkaHeaders.TOPIC, "update-gig-count-topic")
                .build();
        kafkaTemplate.send(message);
    }

    public void sendGetSellersTopic(Integer count) {
        Message<Integer> message = MessageBuilder
                .withPayload(count)
                .setHeader(KafkaHeaders.TOPIC, "get-sellers-topic")
                .build();
        kafkaTemplate.send(message);
    }
}
