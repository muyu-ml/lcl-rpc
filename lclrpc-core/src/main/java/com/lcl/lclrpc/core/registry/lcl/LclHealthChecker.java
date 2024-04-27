package com.lcl.lclrpc.core.registry.lcl;

import com.alibaba.fastjson.JSON;
import com.lcl.lclrpc.core.consumer.HttpInvoker;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * check the health of registry center
 * @Author conglongli
 * @date 2024/4/27 23:24
 */
@Slf4j
public class LclHealthChecker {
    private ScheduledExecutorService consumerExecutor;
    private ScheduledExecutorService providerExecutor;

    public void start() {
        log.info(" ==========>>>> LclRegistryCenter starting with health checker...");
        consumerExecutor = Executors.newScheduledThreadPool(1);
        providerExecutor = Executors.newScheduledThreadPool(1);
    }

    public void stop() {
        log.info(" ==========>>>> LclRegistryCenter stopping with health checker...");
        gracefulShutdown(consumerExecutor);
        gracefulShutdown(providerExecutor);
    }

    public void consumerCheck(LclHealthChecker.Callback callback){
        consumerExecutor.scheduleWithFixedDelay(() -> {
            try {
                callback.call();
            }catch (Exception e) {
                log.error(" ==========>>>> LclRegistryCenter consumer check failed, {}", e.getMessage());
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
    }

    public void providerCheck(LclHealthChecker.Callback callback){
        providerExecutor.scheduleWithFixedDelay(() ->{
            try {
                callback.call();
            }catch (Exception e) {
                log.error(" ==========>>>> LclRegistryCenter provider check failed, {}", e.getMessage());
            }
        }, 1000, 5000, TimeUnit.MILLISECONDS);
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

    public interface Callback {
        void call() throws Exception;
    }

}
