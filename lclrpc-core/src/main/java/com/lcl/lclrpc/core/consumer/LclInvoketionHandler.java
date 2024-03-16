package com.lcl.lclrpc.core.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.util.MethodUtils;
import com.lcl.lclrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.TimeUnit;

@Slf4j
public class LclInvoketionHandler implements InvocationHandler {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
    Class<?> service;

    public LclInvoketionHandler(Class<?> service) {
        this.service = service;
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

        RpcResponse rpcResponse = post(rpcRequest);

        if(rpcResponse.isStatus()) {
            Object data = rpcResponse.getData();
            // 不是JSONObject 类型说明是基本数据类型，可以直接返回
            if(data instanceof JSONObject) {
                JSONObject jsonResult = (JSONObject) data;
                return JSON.parseObject(jsonResult.toJSONString(), method.getReturnType());
            }
            if(data instanceof JSONArray jsonArray) {
                // 将 JSONArray 转换为方法签名的ReturnType数组类型
                Object[] objArray = jsonArray.toArray();
                Class<?> componentType = method.getReturnType().getComponentType();
                Object resultArray = Array.newInstance(componentType, objArray.length);
                for (int i = 0; i < objArray.length; i++) {
                    Array.set(resultArray, i, objArray[i]);
                }
                return resultArray;
            }
            return TypeUtils.cast(data, method.getReturnType());
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

    public RpcResponse post(RpcRequest rpcRequest) {
        String reqJson = JSON.toJSONString(rpcRequest);
        System.out.printf("reqJson: %s%n", reqJson);
        Request request = new Request.Builder()
                .url("http://localhost:8080/")
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
