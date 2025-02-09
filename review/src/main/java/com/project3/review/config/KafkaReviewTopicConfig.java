package com.project3.review.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaReviewTopicConfig {

    @Bean
    public NewTopic jobberReviewTopic() {
        return TopicBuilder.name("jobber-review-topic").build();
    }
    @Bean
    public NewTopic jobberReviewUserTopic() {
        return TopicBuilder.name("jobber-review-user-topic").build();
    }
}
