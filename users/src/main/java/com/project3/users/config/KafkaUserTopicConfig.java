package com.project3.users.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaUserTopicConfig {

    @Bean
    public NewTopic updateGigTopic() {
        return TopicBuilder.name("update-gig-topic").build();
    }

    @Bean
    public NewTopic seedGigTopic() {
        return TopicBuilder.name("seed-gig-topic").build();
    }
}
