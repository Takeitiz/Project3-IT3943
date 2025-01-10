package com.project3.gigs.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaGigTopicConfig {

    @Bean
    public NewTopic updateGigCountTopic() {
        return TopicBuilder.name("update-gig-count-topic").build();
    }

    @Bean
    public NewTopic getSellersTopic() {
        return TopicBuilder.name("get-sellers-topic").build();
    }
}
