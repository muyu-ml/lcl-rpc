package com.lcl.lclrpc.core.annotation;

import com.lcl.lclrpc.core.consumer.ConsumerConfig;
import com.lcl.lclrpc.core.provider.ProviderConfig;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Import({ProviderConfig.class, ConsumerConfig.class})
public @interface EnableLclrpc {
}
