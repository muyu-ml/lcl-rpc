package com.lcl.lclrpc.core.util;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Arrays;

@Slf4j
public class MethodUtils {

    public static boolean isObjectMethod(Method method) {
        return Object.class.equals(method.getDeclaringClass());
    }

    public static String buildMethodSign(Method method) {
        StringBuilder sb = new StringBuilder(method.getName());
        sb.append("@").append(method.getParameterCount());
        Arrays.stream(method.getParameterTypes()).forEach(
                parameterType -> sb.append("_").append(parameterType.getCanonicalName())
        );
        return sb.toString();
    }

}
