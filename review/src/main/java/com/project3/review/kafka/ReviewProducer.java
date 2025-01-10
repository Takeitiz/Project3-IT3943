package com.project3.review.kafka;

import com.project3.review.dto.ReviewMessageDetailsDto;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ReviewProducer {

    private final KafkaTemplate<String, ReviewMessageDetailsDto> kafkaTemplate;

    public void sendJobberReviewTopic(ReviewMessageDetailsDto reviewMessageDetails) {
        Message<ReviewMessageDetailsDto> message = MessageBuilder
                .withPayload(reviewMessageDetails)
                .setHeader(KafkaHeaders.TOPIC, "jobber-review-topic")
                .build();
        Message<ReviewMessageDetailsDto> messageUser = MessageBuilder
                .withPayload(reviewMessageDetails)
                .setHeader(KafkaHeaders.TOPIC, "jobber-review-user-topic")
                .build();

        kafkaTemplate.send(message);
        kafkaTemplate.send(messageUser);
    }

    public void sendJobberReviewUserTopic(ReviewMessageDetailsDto reviewMessageDetails) {
        Message<ReviewMessageDetailsDto> message = MessageBuilder
                .withPayload(reviewMessageDetails)
                .setHeader(KafkaHeaders.TOPIC, "jobber-review-user-topic")
                .build();

        kafkaTemplate.send(message);
    }

}
