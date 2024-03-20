package com.lcl.lclrpc.core.registry;

import com.lcl.lclrpc.core.api.RegistryCenter;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class ZkRegistryCenter implements RegistryCenter {

    private CuratorFramework client = null;

    @Override
    public void start() {
        RetryPolicy retryPolicy = new ExponentialBackoffRetry(1000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString("localhost:2181")
                .namespace("lclrpc")
                .retryPolicy(retryPolicy)
                .build();
        log.info("ZkClient starting...");
        client.start();

    }

    @Override
    public void stop() {
        log.info("ZkClient stopping...");
        client.close();
    }

    @Override
    public void register(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 创建服务持久化节点
            if(client.checkExists().forPath(servicePath) == null) {
                client.create().withMode(CreateMode.PERSISTENT).forPath(servicePath, "service".getBytes());
            }
            // 创建实例实例临时节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info("======>>>>>> Register to zk service: {}, instance: {}", service, instance);
            if(client.checkExists().forPath(instancePath) == null) {
                client.create().withMode(CreateMode.EPHEMERAL).forPath(instancePath, "instance".getBytes());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregister(ServiceMeta service, InstanceMeta instance) {
        String servicePath = "/" + service.toPath();
        try {
            // 如果服务节点不存在，则直接返回
            if(client.checkExists().forPath(servicePath) == null) {
                return;
            }
            // 删除实例节点
            String instancePath = servicePath + "/" + instance.toPath();
            log.info("======>>>>>> unregister from zk: {}", instancePath);
            client.delete().quietly().forPath(instancePath);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InstanceMeta> fetchAll(ServiceMeta service) {
        String servicePath = "/" + service.toPath();
        try {
            List<String> nodes = client.getChildren().forPath(servicePath);
            log.info("Fetch all service ......");
            nodes.forEach(node -> log.info("Fetch service: {}", node));
            return mapInstance(nodes);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @NotNull
    private static List<InstanceMeta> mapInstance(List<String> nodes) {
        return nodes.stream().map(x -> {
            String[] strs = x.split("_");
            return InstanceMeta.http(strs[0], Integer.parseInt(strs[1]));
        }).collect(Collectors.toList());
    }

    @Override
    @SneakyThrows
    public void subscribe(ServiceMeta service, ChangeListener listener) {
        final TreeCache cache = TreeCache.newBuilder(client, "/" + service.toPath())
                .setCacheData(true).setMaxDepth(2)
                .build();
        cache.getListenable().addListener((curator, event) -> {
            log.info("subscribe event: {}", event);
            List<InstanceMeta> nodes = fetchAll(service);
            listener.fire(new Event(nodes));
        });
        cache.start();
    }


}
