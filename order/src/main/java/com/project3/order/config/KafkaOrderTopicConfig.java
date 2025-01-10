package com.project3.order.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaOrderTopicConfig {

    @Bean
    public NewTopic orderEmailTopic() {
        return TopicBuilder.name("order-email-topic").build();
    }

    @Bean
    public NewTopic userSellerTopic() {
        return TopicBuilder.name("user-seller-topic").build();
    }

    @Bean
    public NewTopic userBuyerTopic() {
        return TopicBuilder.name("user-buyer-gig-topic").build();
    }
}
