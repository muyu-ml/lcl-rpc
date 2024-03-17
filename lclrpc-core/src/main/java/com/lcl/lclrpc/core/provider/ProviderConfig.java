package com.lcl.lclrpc.core.provider;

import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.consumer.ConsumerBootStrap;
import com.lcl.lclrpc.core.registry.ZkRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import java.util.List;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Configuration
public class ProviderConfig {

    /**
     * @return {@link ProviderBootstrap}
     */
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap();
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return args -> {
            System.out.printf("ProviderConfig starting...%n");
            providerBootstrap.start();
            System.out.printf("ProviderConfig started...%n");
        };
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }


}