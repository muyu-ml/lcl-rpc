package com.lcl.lclrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.lcl.lclrpc.core.api.*;
import com.lcl.lclrpc.core.util.MethodUtils;
import com.lcl.lclrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LclInvoketionHandler implements InvocationHandler {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
    Class<?> service;

    RpcContext context;
    List<String> providers;



    public LclInvoketionHandler(Class<?> service, RpcContext context, List<String> providers) {
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
        List<String> urls = context.getRouter().route(providers);
        String url = (String) context.getLoadbalancer().choose(urls);
        log.info("loadbalancer choose url ====>>> {}", url);
        RpcResponse rpcResponse = post(rpcRequest, url);

        if(rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            // 不是JSONObject 类型说明是基本数据类型，可以直接返回
            return TypeUtils.castMethodReturnType(method, data);
        } else {
            Exception ex = rpcResponse.getEx();
            throw new RuntimeException(ex);
        }
    }

    OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectionPool(new okhttp3.ConnectionPool(16, 60, TimeUnit.SECONDS))
            .readTimeout(1, TimeUnit.SECONDS)
            .writeTimeout(1, TimeUnit.SECONDS)
            .connectTimeout(1, TimeUnit.SECONDS)
            .build();

    public RpcResponse post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.printf("reqJson: %s%n", reqJson);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String respJson = okHttpClient.newCall(request).execute().body().string();
            System.out.printf("respJson: %s%n", respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
