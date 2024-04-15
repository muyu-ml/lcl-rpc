package com.lcl.lclrpc.core.annotation;

import com.lcl.lclrpc.core.config.ConsumerConfig;
import com.lcl.lclrpc.core.config.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableLclrpc {
}
