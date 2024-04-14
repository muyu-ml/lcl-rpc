package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.api.*;
import com.lcl.lclrpc.core.consumer.http.OkHttpInvoker;
import com.lcl.lclrpc.core.governance.SlidingTimeWindow;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.util.MethodUtils;
import com.lcl.lclrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


@Slf4j
public class LclInvoketionHandler implements InvocationHandler {


    Class<?> service;

    RpcContext context;
    final List<InstanceMeta> providers;
    final List<InstanceMeta> isolateProviders = new ArrayList<>();
    final List<InstanceMeta> halfOpenProviders = new ArrayList<>();

    Map<String, SlidingTimeWindow> windows = new HashMap<>();

    HttpInvoker httpInvoker;

    ScheduledExecutorService executor;




    public LclInvoketionHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
        int timeout = Integer.parseInt(context.getParameters().getOrDefault("app.timeout", "1000"));
        httpInvoker = new OkHttpInvoker(timeout);
        this.executor = Executors.newScheduledThreadPool(1);
        // delay 10 秒启动，每隔 60 秒检查一次
        this.executor.scheduleWithFixedDelay(this::halfOpen, 10, 60, TimeUnit.SECONDS);
    }

    /**
     * 半开状态
     */
    private void halfOpen() {
        log.debug("====>>> halfOpen：{}, isolateProviders:{}", System.currentTimeMillis(), isolateProviders);
        halfOpenProviders.clear();
        halfOpenProviders.addAll(isolateProviders);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // 不允许调用 Object 的方法
        if(MethodUtils.isObjectMethod(method)) {
            return null;
        }

        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setService(service.getCanonicalName());
        rpcRequest.setMethodSign(MethodUtils.buildMethodSign(method));
        rpcRequest.setParameters(args);

        int retries = Integer.parseInt(context.getParameters().getOrDefault("app.retries", "1"));
        while (retries-- > 0) {
            log.debug("=======>>> retries：{}", retries);
            try {
                // 前置过滤器
                for (Filter filter : context.getFilters()) {
                    Object preResult = filter.preFilter(rpcRequest);
                    if (preResult != null) {
                        log.debug("{} ========>>>>: {}", filter.getClass(), preResult);
                        return preResult;
                    }
                }

                InstanceMeta instance;
                // 服务隔离处理：如果存在半开状态的服务提供者，就选择半开状态的服务提供者，否则选择一个服务提供者
                synchronized (halfOpenProviders){
                    if(halfOpenProviders.isEmpty()){
                        // 路由选择一个服务提供者
                        List<InstanceMeta> instances = context.getRouter().route(providers);
                        instance = context.getLoadbalancer().choose(instances);
                        log.debug("loadbalancer choose url ====>>> {}", instance);
                    } else {
                        instance = halfOpenProviders.get(0);
                        log.debug("check alive instance ===>>> {}", instance);
                    }
                }

                RpcResponse<?> rpcResponse;
                Object result;

                String url = instance.toUrl();
                try {
                    rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());
                    result = castResponseToReturnResult(method, rpcResponse);
                } catch (Exception e) {
                    // 故障规则的统计和隔离
                    // 每一次异常，记录一次，统计30S内的异常次数，超过阈值，就做故障隔离
                    SlidingTimeWindow window = windows.get(url);
                    if(window == null) {
                        window = new SlidingTimeWindow(30);
                        windows.put(url, window);
                    }
                    window.record(System.currentTimeMillis());
                    log.debug("instance {} in windows 30s exception times: {}", url, window.getSum());
                    // 如果30s内异常次数超过10次，就做故障隔离
                    if(window.getSum() >= 10) {
                        log.error("instance {} is breaked", url);
                        isolate(instance);
                    }
                    throw e;
                }

                // 服务隔离处理：如果服务提供者恢复，就将其从隔离状态恢复
                synchronized (providers){
                    if(!providers.contains(instance)){
                        isolateProviders.remove(instance);
                        providers.add(instance);
                        log.debug("====>>>  instance {} is recoverd, isolateProviders = {}, providers = {}", instance, isolateProviders, providers);
                    }
                }

                // 后置过滤器
                for (Filter filter : context.getFilters()) {
                    Object filterResult = filter.postFilter(rpcRequest, rpcResponse, result);
                    if (filterResult != null) {
                        return filterResult;
                    }
                }
                return result;
            } catch (RuntimeException ex){
                if(!(ex.getCause() instanceof SocketTimeoutException)) {
                    throw ex;
                }
            }
        }
        return null;
    }

    /**
     * 故障隔离
     * @param instance
     */
    private void isolate(InstanceMeta instance) {
        log.debug("====>>>  isolate instance: {}", instance);
        providers.remove(instance);
        log.debug("====>>>  providers = {}", providers);
        if(!isolateProviders.contains(instance)){
            isolateProviders.add(instance);
        }
        log.debug("====>>>  isolateProviders = {}", isolateProviders);
    }

    @Nullable
    private static Object castResponseToReturnResult(Method method, RpcResponse<?> rpcResponse) {
        if(rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            // 不是JSONObject 类型说明是基本数据类型，可以直接返回
            return TypeUtils.castMethodReturnType(method, data);
        } else {
            Exception exception = rpcResponse.getEx();
            if(exception instanceof RpcException ex){
                throw ex;
            }
            throw new RpcException(exception, RpcException.UnKnownEx);
        }
    }


}
