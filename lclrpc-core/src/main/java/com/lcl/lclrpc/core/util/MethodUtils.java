package com.lcl.lclrpc.core.util;

import com.lcl.lclrpc.core.annotation.LclConsumer;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static List<Field> findAnnotatedFields(Class<?> aClass, Class<? extends Annotation> annotationClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null && aClass != Object.class) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(annotationClass)) {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }

}
