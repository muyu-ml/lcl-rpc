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

    @Value("${lclrpc.lclregistry.servers}")
    private String servers;

    private ScheduledExecutorService consumerExecutor;
    Map<String, Long> VERSIONS = new HashMap<>();
    private ScheduledExecutorService providerExecutor;
    MultiValueMap<InstanceMeta, ServiceMeta> RENEWS = new LinkedMultiValueMap<>();

    @Override
    public void start() {
        log.info(" ==========>>>> LclRegistryCenter starting... to servers: {}", servers);
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor.scheduleWithFixedDelay(() -> {
            try{
                log.debug(" ==========>>>> LclRegistryCenter ping...");
                RENEWS.keySet().stream().forEach(
                        instance -> {
                            String services = String.join(",", RENEWS.get(instance).stream().map(ServiceMeta::toPath).collect(Collectors.toList()));
                            RENEWS.get(instance).stream().map(x -> x.toPath()).collect(Collectors.toList());
                            log.debug(" ==========>>>> LclRegistryCenter renew instance {} @ {}", instance, services);
                            HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/renews?services=" + services, Long.class);
                        }
                );
            } catch (Exception e) {
                log.error(" ==========>>>> LclRegistryCenter ping failed, {}", e.getMessage());
            }
        }, 5000, 5000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void stop() {
        log.info(" ==========>>>> LclRegistryCenter stopping...");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    private void gracefulShutdown(ScheduledExecutorService executorService) {
        executorService.shutdown();
        try {
            executorService.awaitTermination(1000, TimeUnit.MILLISECONDS);
            if(!executorService.isTerminated()) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {

        }
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        log.info(" ==========>>>> LclRegistryCenter registered service {} @ {}", service, instance);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/reg?service=" + service.toPath(), void.class);
        log.info(" ==========>>>> LclRegistryCenter registered service {} @ {} success", service, instance);
        RENEWS.add(instance, service);
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        log.info(" ==========>>>> LclRegistryCenter unRegistered service {} @ {}", service, instance);
        HttpInvoker.httpPost(JSON.toJSONString(instance), servers + "/unreg?service=" + service.toPath(), void.class);
        log.info(" ==========>>>> LclRegistryCenter unRegistered service {} @ {} success", service, instance);
        RENEWS.remove(instance, service);
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        log.info(" ==========>>>> LclRegistryCenter fetchAll instances service {}", service);
        List<InstanceMeta> instanceMetas = HttpInvoker.httpGet(servers + "/findAll?service=" + service.toPath(), new TypeReference<List<InstanceMeta>>(){});
        log.info(" ==========>>>> LclRegistryCenter fetchAll instances service {} success", service);
        return instanceMetas;
    }

    @Override
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        consumerExecutor.scheduleWithFixedDelay(() -> {
            Long version = VERSIONS.getOrDefault(service.toPath(), -1L);
            Long newVersion = HttpInvoker.httpGet(servers + "/version?service=" + service.toPath(), Long.class);
            log.info(" ==========>>>> LclRegistryCenter subscribe service = {} , newVersion: {} -> {}", service, version, newVersion);
            if(newVersion > version) {
                List<InstanceMeta> instances = fetchAll(service);
                listener.fire(new Event(instances));
                VERSIONS.put(service.toPath(), newVersion);
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }
}
