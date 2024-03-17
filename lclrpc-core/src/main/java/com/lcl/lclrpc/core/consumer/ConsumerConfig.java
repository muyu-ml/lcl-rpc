package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.Router;
import com.lcl.lclrpc.core.cluster.RandomLoadbalancer;
import com.lcl.lclrpc.core.cluster.RoundRibonLoadbalancer;
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

    @Bean
    public Loadbalancer loadbalancer() {
        return new RoundRibonLoadbalancer();
    }

    @Bean
    public Router router() {
        return Router.Default;
    }
}
