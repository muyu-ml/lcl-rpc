package com.lcl.lclrpc.core.api;

import com.lcl.lclrpc.core.registry.ChangeListener;
import org.apache.curator.framework.recipes.cache.CuratorCacheListenerBuilder;

import java.util.List;

public interface RegistryCenter {
    void start();      
    void stop();

    // provider 侧
    void register(String service, String instance);
    void unregister(String service, String instance);

    // consumer 侧
    List<String> fetchAll(String service);

    void subscribe(String service, ChangeListener listener);

    class StacticRegistryCenter implements RegistryCenter {

        List<String> providers;

        public StacticRegistryCenter(List<String> providers) {
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
        public void register(String service, String instance) {
            System.out.println("StacticRegistryCenter register");
        }

        @Override
        public void unregister(String service, String instance) {
            System.out.println("StacticRegistryCenter unregister");
        }

        @Override
        public List<String> fetchAll(String service) {
            return providers;
        }

        @Override
        public void subscribe(String service, ChangeListener listener) {

        }
    }
}
