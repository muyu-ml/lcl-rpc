package com.lcl.lclrpc.core.annotation;

import java.lang.annotation.*;


/**
 * 服务提供者注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface LclProvider {

}
