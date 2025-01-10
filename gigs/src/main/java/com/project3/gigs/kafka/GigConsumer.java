package com.project3.gigs.kafka;

import com.project3.gigs.dto.ReviewMessageDetailsDto;
import com.project3.gigs.dto.SeedMessageDto;
import com.project3.gigs.service.impl.GigService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.MessagingException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class GigConsumer {

    private final GigService gigService;

    @KafkaListener(topics = "update-gig-topic")
    public void consumeUpdateGigMessages(ReviewMessageDetailsDto reviewMessageDetailsDto) throws MessagingException {
        gigService.updateGigReview(reviewMessageDetailsDto);
    }

    @KafkaListener(topics = "seed-gig-topic")
    public void consumeSeedGigMessages(SeedMessageDto seedMessageDto) throws MessagingException {
        gigService.seedData(seedMessageDto.getSellers(), seedMessageDto.getCount());
    }
}
