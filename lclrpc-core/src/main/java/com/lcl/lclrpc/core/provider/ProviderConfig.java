package com.lcl.lclrpc.core.provider;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
}