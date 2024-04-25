package com.lcl.lclrpc.core.consumer.http;

import com.alibaba.fastjson.JSON;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.consumer.HttpInvoker;
import com.lcl.lclrpc.core.meta.InstanceMeta;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OkHttpInvoker implements HttpInvoker {

    final static MediaType JSONTYPE = MediaType.get("application/json; charset=utf-8");
    OkHttpClient client;

    public OkHttpInvoker(int timeout){
        this.client = new OkHttpClient.Builder()
                .connectionPool(new okhttp3.ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .build();
    }

    @Override
    public RpcResponse post(RpcRequest rpcRequest, String url) {
        String reqJson = JSON.toJSONString(rpcRequest);
        log.debug("reqJson: {}", reqJson);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(reqJson, JSONTYPE))
                .build();
        try {
            String respJson = this.client.newCall(request).execute().body().string();
            log.debug("respJson: {}", respJson);
            RpcResponse rpcResponse = JSON.parseObject(respJson, RpcResponse.class);
            return rpcResponse;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String post(String requestString, String url) {
        log.debug(" ======>>>> post url：{}, requestString: {}", url, requestString);
        Request request = new Request.Builder()
                .url(url)
                .post(RequestBody.create(requestString, JSONTYPE))
                .build();
        try {
            String respJson = this.client.newCall(request).execute().body().string();
            log.debug("respJson: {}", respJson);
            return respJson;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public String get(String url) {
        log.debug(" ======>>>> get url：{}", url);
        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();
        try {
            String respJson = this.client.newCall(request).execute().body().string();
            log.debug("respJson: {}", respJson);
            return respJson;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
