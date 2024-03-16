package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.annotation.LclConsumer;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class ConsumerBootStrap implements ApplicationContextAware {

    ApplicationContext applicationContext;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = findAnnotatedFields(bean.getClass());
            fields.stream().forEach(f -> {
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object object = stub.get(serviceName);
                    if (object == null) {
                        object = createConsumer(service);
                    }
                    f.setAccessible(true);
                    f.set(bean, object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private Object createConsumer(Class<?> service) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new LclInvoketionHandler(service));
    }

    private List<Field> findAnnotatedFields(Class<?> aClass) {
        List<Field> result = new ArrayList<>();
        while (aClass != null && aClass != Object.class) {
            Field[] fields = aClass.getDeclaredFields();
            for (Field field : fields) {
                if (field.isAnnotationPresent(LclConsumer.class)) {
                    result.add(field);
                }
            }
            aClass = aClass.getSuperclass();
        }
        return result;
    }
}
