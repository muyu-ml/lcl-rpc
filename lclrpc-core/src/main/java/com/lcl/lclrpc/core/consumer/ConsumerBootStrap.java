package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.annotation.LclConsumer;
import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.Router;
import com.lcl.lclrpc.core.api.RpcContext;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.util.Strings;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Slf4j
public class ConsumerBootStrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;
    Environment environment;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        // 获取路由和负载均衡
        Router router = applicationContext.getBean(Router.class);
        Loadbalancer loadbalancer = applicationContext.getBean(Loadbalancer.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadbalancer(loadbalancer);


        // 获取服务提供者的地址
        String urls = environment.getProperty("lclrpc.providers", "");
        if(Strings.isEmpty(urls)){
            log.error("lclrpc.providers is empty");
        }
        List<String> providers = List.of(urls.split(","));


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
                        object = createConsumer(service, context, providers);
                    }
                    f.setAccessible(true);
                    f.set(bean, object);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<String> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new LclInvoketionHandler(service, context, providers));
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
