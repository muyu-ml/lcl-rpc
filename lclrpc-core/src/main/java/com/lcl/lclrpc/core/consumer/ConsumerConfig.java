package com.lcl.lclrpc.core.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
public class ConsumerConfig {
    @Bean
    ConsumerBootStrap createConsumerBootStrap() {
        return new ConsumerBootStrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerRunner(@Autowired ConsumerBootStrap consumerBootStrap) {
        return args -> {
            System.out.printf("ConsumerConfig starting...%n");
            consumerBootStrap.start();
            System.out.printf("ConsumerConfig started...%n");
        };
    }
}
