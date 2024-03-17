package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.api.Router;
import com.lcl.lclrpc.core.cluster.RandomLoadbalancer;
import com.lcl.lclrpc.core.cluster.RoundRibonLoadbalancer;
import com.lcl.lclrpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
public class ConsumerConfig {

    @Value("${lclrpc.providers}")
    String services;

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

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }
}
