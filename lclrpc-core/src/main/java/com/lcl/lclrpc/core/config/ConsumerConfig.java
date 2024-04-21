package com.lcl.lclrpc.core.config;

import com.lcl.lclrpc.core.api.*;
import com.lcl.lclrpc.core.cluster.GrayRouter;
import com.lcl.lclrpc.core.cluster.RoundRibonLoadbalancer;
import com.lcl.lclrpc.core.consumer.ConsumerBootStrap;
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
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

import java.util.List;

@Configuration
@Slf4j
@Import({AppConfigProperties.class, ConsumerConfigProperties.class})
public class ConsumerConfig {

    @Autowired
    private AppConfigProperties appConfigProperties;
    @Autowired
    private ConsumerConfigProperties ConsumerConfigProperties;

    @Bean
    @ConditionalOnProperty(prefix = "apollo.bootstrap", name = "enabled")
    @ConditionalOnMissingBean
    ApolloChangeListener consumer_apolloChangeListener() {
        return new ApolloChangeListener();
    }

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
        return new GrayRouter(ConsumerConfigProperties.getGrayRatio());
    }

    @Bean
    public Filter filter(){
        return new ParameterFilter();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnMissingBean
    public RegistryCenter consumer_rc() {
        return new ZkRegistryCenter();
    }

    @Bean
    public RpcContext createContext(@Autowired Router router,
                                    @Autowired Loadbalancer loadbalancer,
                                    @Autowired List<Filter> filters){
        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadbalancer(loadbalancer);
        context.setFilters(filters);
        context.getParameters().put("app.id" , appConfigProperties.getId());
        context.getParameters().put("app.namespace", appConfigProperties.getNamespace());
        context.getParameters().put("app.env", appConfigProperties.getEnv());
        context.setConsumerConfigProperties(ConsumerConfigProperties);
        return context;

    }


}
