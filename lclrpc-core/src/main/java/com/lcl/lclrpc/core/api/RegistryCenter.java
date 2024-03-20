package com.lcl.lclrpc.core.api;

import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.registry.ChangeListener;
import org.apache.curator.framework.recipes.cache.CuratorCacheListenerBuilder;

import java.util.List;

public interface RegistryCenter {
    void start();      
    void stop();

    // provider 侧
    void register(String service, InstanceMeta instance);
    void unregister(String service, InstanceMeta instance);

    // consumer 侧
    List<InstanceMeta> fetchAll(String service);

    void subscribe(String service, ChangeListener listener);

    class StacticRegistryCenter implements RegistryCenter {

        List<InstanceMeta> providers;

        public StacticRegistryCenter(List<InstanceMeta> providers) {
            this.providers = providers;
        }

        @Override
        public void start() {
            System.out.println("StacticRegistryCenter start");
        }

        @Override
        public void stop() {
            System.out.println("StacticRegistryCenter stop");
        }

        @Override
        public void register(String service, InstanceMeta instance) {
            System.out.println("StacticRegistryCenter register");
        }

        @Override
        public void unregister(String service, InstanceMeta instance) {
            System.out.println("StacticRegistryCenter unregister");
        }

        @Override
        public List<InstanceMeta> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangeListener listener) {

        }
    }
}
