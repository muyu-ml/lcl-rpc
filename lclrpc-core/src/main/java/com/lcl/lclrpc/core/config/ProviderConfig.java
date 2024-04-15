package com.lcl.lclrpc.core.config;

import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.provider.ProviderBootstrap;
import com.lcl.lclrpc.core.provider.ProviderInvoker;
import com.lcl.lclrpc.core.registry.zk.ZkRegistryCenter;
import com.lcl.lclrpc.core.transport.SpringBootTransport;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.annotation.Order;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Slf4j
@Configuration
@Import({SpringBootTransport.class, AppConfigProperties.class, ProviderConfigProperties.class})
public class ProviderConfig {

    @Value("${server.port:8080}")
    private int port;
    @Autowired
    private AppConfigProperties appConfigProperties;
    @Autowired
    private ProviderConfigProperties providerConfigProperties;


    /**
     * @return {@link ProviderBootstrap}
     */
    @Bean
    ProviderBootstrap providerBootstrap() {
        return new ProviderBootstrap(port, appConfigProperties, providerConfigProperties);
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
    @ConditionalOnMissingBean
    public RegistryCenter provider_rc() {
        return new ZkRegistryCenter();
    }


}