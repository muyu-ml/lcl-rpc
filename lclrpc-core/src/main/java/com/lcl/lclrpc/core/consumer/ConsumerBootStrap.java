package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.annotation.LclConsumer;
import com.lcl.lclrpc.core.api.Loadbalancer;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.api.Router;
import com.lcl.lclrpc.core.api.RpcContext;
import com.lcl.lclrpc.core.registry.ChangeListener;
import com.lcl.lclrpc.core.registry.Event;
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
import java.util.stream.Collectors;

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
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadbalancer(loadbalancer);


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
                        object = createConsumerFromRegistry(service, context, rc);
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

    private Object createConsumerFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();
        List<String> providers = mapUrl(rc.fetchAll(serviceName));
        providers.forEach(x -> log.info("fetch provider: {}", x));
        rc.subscribe(serviceName, event -> {
            providers.clear();
            providers.addAll(mapUrl(event.getData()));
        });
        return createConsumer(service, context, providers);
    }

    private List<String> mapUrl(List<String> nodes){
        return nodes.stream().map(x -> "http://" + x.replace("_", ":")).collect(Collectors.toList());
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
