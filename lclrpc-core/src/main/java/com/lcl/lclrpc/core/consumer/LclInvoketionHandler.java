package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.api.*;
import com.lcl.lclrpc.core.consumer.http.OkHttpInvoker;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import com.lcl.lclrpc.core.util.MethodUtils;
import com.lcl.lclrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;


@Slf4j
public class LclInvoketionHandler implements InvocationHandler {


    Class<?> service;

    RpcContext context;
    List<InstanceMeta> providers;

    HttpInvoker httpInvoker = new OkHttpInvoker();



    public LclInvoketionHandler(Class<?> service, RpcContext context, List<InstanceMeta> providers) {
        this.service = service;
        this.context = context;
        this.providers = providers;
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

        // 路由选择一个服务提供者
        List<InstanceMeta> instances = context.getRouter().route(providers);
        InstanceMeta instance = context.getLoadbalancer().choose(instances);
        log.info("loadbalancer choose url ====>>> {}", instance);
        RpcResponse<?> rpcResponse = httpInvoker.post(rpcRequest, instance.toUrl());

        if(rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            // 不是JSONObject 类型说明是基本数据类型，可以直接返回
            return TypeUtils.castMethodReturnType(method, data);
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }


}
