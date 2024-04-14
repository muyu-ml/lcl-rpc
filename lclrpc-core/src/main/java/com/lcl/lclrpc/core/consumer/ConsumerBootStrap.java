package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.annotation.LclConsumer;
import com.lcl.lclrpc.core.api.*;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import com.lcl.lclrpc.core.util.MethodUtils;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 消费端启动类
 */
@Data
@Slf4j
public class ConsumerBootStrap implements ApplicationContextAware, EnvironmentAware {

    ApplicationContext applicationContext;
    Environment environment;

    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;
    @Value("${app.version}")
    private String version;
    @Value("${app.retries}")
    private int retries;
    @Value("${app.timeout}")
    private int timeout;

    private Map<String, Object> stub = new HashMap<>();

    public void start() {
        // 获取路由和负载均衡
        Router<InstanceMeta> router = applicationContext.getBean(Router.class);
        Loadbalancer<InstanceMeta> loadbalancer = applicationContext.getBean(Loadbalancer.class);
        RegistryCenter rc = applicationContext.getBean(RegistryCenter.class);
        List<Filter> filters = applicationContext.getBeansOfType(Filter.class).values().stream().toList();

        RpcContext context = new RpcContext();
        context.setRouter(router);
        context.setLoadbalancer(loadbalancer);
        context.setFilters(filters);
        context.getParameters().put("app.retries", String.valueOf(retries));
        context.getParameters().put("app.timeout", String.valueOf(timeout));

        String[] names = applicationContext.getBeanDefinitionNames();
        for (String name : names) {
            Object bean = applicationContext.getBean(name);
            List<Field> fields = MethodUtils.findAnnotatedFields(bean.getClass(), LclConsumer.class);
            fields.stream().forEach(f -> {
                try {
                    Class<?> service = f.getType();
                    String serviceName = service.getCanonicalName();
                    Object consumer = stub.get(serviceName);
                    if (consumer == null) {
                        consumer = createConsumerFromRegistry(service, context, rc);
                        stub.put(serviceName, consumer);
                    }
                    f.setAccessible(true);
                    f.set(bean, consumer);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    private Object createConsumer(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        return Proxy.newProxyInstance(service.getClassLoader(), new Class<?>[]{service}, new LclInvoketionHandler(service, context, providers));
    }

    private Object createConsumerFromRegistry(Class<?> service, RpcContext context, RegistryCenter rc) {
        String serviceName = service.getCanonicalName();
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(serviceName).build();
        List<InstanceMeta> providers = rc.fetchAll(serviceMeta);
        providers.forEach(x -> log.info("fetch provider: {}", x));
        rc.subscribe(serviceMeta, event -> {
            providers.clear();
            providers.addAll(event.getData());
        });
        return createConsumer(service, context, providers);
    }

}
