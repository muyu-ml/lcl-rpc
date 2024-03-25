package com.lcl.lclrpc.core.provider;

import com.lcl.lclrpc.core.annotation.LclProvider;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ProviderMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import com.lcl.lclrpc.core.util.MethodUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Method;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Map;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Data
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {

    /**
     *
     */
    private ApplicationContext applicationContext;
    RegistryCenter rc;

    /**
     *
     */
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    private InstanceMeta instance;
    @Value("${server.port}")
    private int port;
    @Value("${app.id}")
    private String app;
    @Value("${app.namespace}")
    private String namespace;
    @Value("${app.env}")
    private String env;
    @Value("${app.version}")
    private String version;



    /**
     *
     */
    @PostConstruct
    public void init() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(LclProvider.class);
        rc = applicationContext.getBean(RegistryCenter.class);
        providers.forEach((k, v) -> log.info("provider: " + k + " -> " + v));
        providers.values().forEach(this::genIntrface);
    }

    @SneakyThrows
    public void start() {
        log.info("====>>> ProviderBootstrap start");
        // 注册服务
        String ip = InetAddress.getLocalHost().getHostAddress();
        this.instance = InstanceMeta.http(ip, port);
        rc.start();
        skeleton.keySet().forEach(this :: registerService);
    }

    @PreDestroy
    public void stop() {
        log.info("====>>> ProviderBootstrap stop");
        skeleton.keySet().forEach(this :: unregisterService);
        rc.stop();
    }

    private void unregisterService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).version(version).build();
        rc.unregister(serviceMeta, instance);
    }

    private void registerService(String service) {
        ServiceMeta serviceMeta = ServiceMeta.builder()
                .app(app).namespace(namespace).env(env).name(service).version(version).build();
        // 注册服务
        rc.register(serviceMeta, instance);
    }

    /**
     * @param impl
     */
    private void genIntrface(Object impl) {
        // 兼容多接口
        Arrays.stream(impl.getClass().getInterfaces()).forEach(
                service -> {
                    Arrays.stream(service.getMethods())
                            .filter(method -> !MethodUtils.isObjectMethod(method))
                            .forEach(method -> createProviderMeta(service, impl, method));
                });
    }

    private void createProviderMeta(Class<?> service, Object impl, Method method) {
        ProviderMeta providerMeta = ProviderMeta.builder()
                .method(method)
                .methodSign(MethodUtils.buildMethodSign(method))
                .serviceImpl(impl)
                .build();
        log.info("create a providerMeta: " + providerMeta);
        skeleton.add(service.getCanonicalName(), providerMeta);
    }


}
