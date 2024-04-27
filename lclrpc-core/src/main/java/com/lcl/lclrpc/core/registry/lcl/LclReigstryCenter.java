package com.lcl.lclrpc.core.registry.lcl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.consumer.HttpInvoker;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import com.lcl.lclrpc.core.registry.ChangeListener;
import com.lcl.lclrpc.core.registry.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * implemention for LclRegistryCenter
 * @Author conglongli
 * @date 2024/4/24 23:04
 */
@Slf4j
public class LclReigstryCenter implements RegistryCenter {

    private static final String REG_PATH = "/reg";
    private static final String UN_REG_PATH = "unreg";
    private static final String VERSION_PATH = "/version";
    private static final String FIND_ALL_PATH = "/findAll";
    private static final String RENEWS_PATH  = "/renews";

    @Value("${lclrpc.lclregistry.servers}")
    private String servers;


    Map<String, Long> VERSIONS = new HashMap<>();

    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();
    LclHealthChecker healthChecker = new LclHealthChecker();

    @Override
    public void start() {
        healthChecker.start();
        providerCheck();
    }

    @Override
    public void stop() {
        healthChecker.stop();
    }

    public void providerCheck(){
        healthChecker.providerCheck(() ->{
            log.debug(" ==========>>>> LclRegistryCenter ping...");
            RENEWS.keySet().stream().forEach(
                    instance -> {
                        log.debug(" ==========>>>> LclRegistryCenter renew instance {} ", instance);
                        HttpInvoker.httpPost(JSON.toJSONString(instance), renewPath(RENEWS.get(instance)), Long.class);
                    }
            );
        });
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ==========>>>> LclRegistryCenter registered service {} @ {}", service, instance);
        HttpInvoker.httpPost(JSON.toJSONString(instance), regPath(service), void.class);
        log.info(" ==========>>>> LclRegistryCenter registered service {} @ {} success", service, instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ==========>>>> LclRegistryCenter unRegistered service {} @ {}", service, instance);
        HttpInvoker.httpPost(JSON.toJSONString(instance), unRegPath(service), void.class);
        log.info(" ==========>>>> LclRegistryCenter unRegistered service {} @ {} success", service, instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ==========>>>> LclRegistryCenter fetchAll instances service {}", service);
        List<InstanceMeta> instanceMetas = HttpInvoker.httpGet(findAllPath(service), new TypeReference<List<InstanceMeta>>(){});
        log.info(" ==========>>>> LclRegistryCenter fetchAll instances service {} success", service);
        return instanceMetas;
    }


    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        healthChecker.consumerCheck(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(versionPath(service), Long.class);
            log.info(" ==========>>>> LclRegistryCenter subscribe service = {} , newVersion: {} -> {}", service, version, newVersion);
            if(newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        });
    }

    private String regPath(ServiceMeta service) {
        return path(REG_PATH, service);
    }

    private String unRegPath(ServiceMeta service) {
        return path(UN_REG_PATH, service);
    }


    private String versionPath(ServiceMeta service) {
        return path(VERSION_PATH, service);
    }

    private String findAllPath(ServiceMeta service) {
        return path(FIND_ALL_PATH, service);
    }

    private String renewPath(List<ServiceMeta> serviceMetaList) {
        return path(RENEWS_PATH, serviceMetaList);
    }

    private String path(String context, ServiceMeta service) {
        return servers + context + "?service=" + service.toPath();
    }

    private String path(String context, List<ServiceMeta> serviceMetaList) {
        String services = String.join(",", serviceMetaList.stream().map(ServiceMeta :: toPath).collect(Collectors.toList()));
        log.debug(" ==========>>>> LclRegistryCenter renew instance  for {} ", services);
        return servers + context + "?services=" + services;
    }
}
