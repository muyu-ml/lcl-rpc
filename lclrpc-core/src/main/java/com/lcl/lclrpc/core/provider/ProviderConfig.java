package com.lcl.lclrpc.core.provider;

import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.registry.zk.ZkRegistryCenter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Slf4j
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
    ProviderInvoker providerInvoker(@Autowired ProviderBootstrap providerBootstrap){
        return new ProviderInvoker(providerBootstrap);
    }

    @Bean
    @Order(Integer.MIN_VALUE)
    public ApplicationRunner providerRunner(@Autowired ProviderBootstrap providerBootstrap) {
        return args -> {
            log.info("ProviderConfig starting...");
            providerBootstrap.start();
            log.info("ProviderConfig started...");
        };
    }

    @Bean //(initMethod = "start", destroyMethod = "stop")
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }


}