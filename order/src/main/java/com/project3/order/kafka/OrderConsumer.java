package com.project3.order.kafka;

import com.project3.order.dto.ReviewMessageDetailsDto;
import com.project3.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderConsumer {

    private final OrderService orderService;

    @KafkaListener(topics = "jobber-review-topic")
    public void consumeAuthEmailMessages(ReviewMessageDetailsDto reviewMessageDetails) throws MessagingException {
        orderService.updateOrderReview(reviewMessageDetails);
    }
}
