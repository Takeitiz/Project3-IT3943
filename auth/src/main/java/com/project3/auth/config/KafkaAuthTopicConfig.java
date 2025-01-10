package com.project3.auth.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaAuthTopicConfig {

    @Bean
    public NewTopic userBuyerTopic() {
        return TopicBuilder.name("user-buyer-auth-topic").build();
    }

    @Bean
    public NewTopic authEmailTopic() {
        return TopicBuilder.name("auth-email-topic").build();
    }
}
