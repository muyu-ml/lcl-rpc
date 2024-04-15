package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.api.Filter;
import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.api.Router;
import com.lcl.lclrpc.core.cluster.GrayRouter;
import com.lcl.lclrpc.core.cluster.RoundRibonLoadbalancer;
import com.lcl.lclrpc.core.filter.CacheFilter;
import com.lcl.lclrpc.core.filter.MockFilter;
import com.lcl.lclrpc.core.filter.ParameterFilter;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

@Configuration
@Slf4j
public class ConsumerConfig {

//    @Value("${lclrpc.providers}")
//    String services;

    public void setGrayRatio(int grayRatio) {
        this.grayRatio = grayRatio;
    }

    @Value("${app.grayRatio:10}")
    private int grayRatio;

    @Bean
    ConsumerBootStrap createConsumerBootStrap() {
        return new ConsumerBootStrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner consumerRunner(@Autowired ConsumerBootStrap consumerBootStrap) {
        return args -> {
            log.info("ConsumerConfig starting...");
            consumerBootStrap.start();
            log.info("ConsumerConfig started...");
        };
    }

    @Bean
    public Loadbalancer<InstanceMeta> loadbalancer() {
        return new RoundRibonLoadbalancer();
    }

    @Bean
    public Router<InstanceMeta> router() {
        return new GrayRouter(grayRatio);
    }

//    @Bean
//    public Filter filter1() {
//        return new CacheFilter();
//    }

//    @Bean
//    public Filter filter2() {
//        return new MockFilter();
//    }
    @Bean
    public Filter filter(){
        return new ParameterFilter();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }


}
