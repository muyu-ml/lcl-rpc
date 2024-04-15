package com.lcl.lclrpc.core.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "lclrpc.app")
public class AppConfigProperties {
    private String id = "app1";
    private String namespace = "public";
    private String env = "dev";
    private String version = "1.0.0";
}
