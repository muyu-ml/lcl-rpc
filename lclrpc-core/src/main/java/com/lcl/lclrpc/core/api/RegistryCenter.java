package com.lcl.lclrpc.core.api;

import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.meta.ServiceMeta;
import com.lcl.lclrpc.core.registry.ChangeListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.recipes.cache.CuratorCacheListenerBuilder;

import java.util.List;

public interface RegistryCenter {
    void start();      
    void stop();

    // provider 侧
    void register(ServiceMeta service, InstanceMeta instance);
    void unregister(ServiceMeta service, InstanceMeta instance);

    // consumer 侧
    List<InstanceMeta> fetchAll(ServiceMeta service);

    void subscribe(ServiceMeta service, ChangeListener listener);

    @Slf4j
    class StacticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StacticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {
            log.info("StacticRegistryCenter start");
        }

        @Override
        public void stop() {
            log.info("StacticRegistryCenter stop");
        }

        @Override
        public void register(ServiceMeta service, InstanceMeta instance) {
            log.info("StacticRegistryCenter register");
        }

        @Override
        public void unregister(ServiceMeta service, InstanceMeta instance) {
            log.info("StacticRegistryCenter unregister");
        }

        @Override
        public List<InstanceMeta> fetchAll(ServiceMeta service) {
            return providers;
        }

        @Override
        public void subscribe(ServiceMeta service, ChangeListener listener) {

        }
    }
}
