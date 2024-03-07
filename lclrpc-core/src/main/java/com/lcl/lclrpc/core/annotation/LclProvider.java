package com.lcl.lclrpc.core.annotation;

import java.lang.annotation.*;


/**
 * @author conglongli
 * @date 2024/03/07
 * @doc 服务提供者注解
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Inherited
public @interface LclProvider {

}
