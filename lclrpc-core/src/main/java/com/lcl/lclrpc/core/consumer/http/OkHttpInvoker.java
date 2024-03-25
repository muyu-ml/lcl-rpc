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

    public OkHttpInvoker(){
        this.client = new OkHttpClient.Builder()
                .connectionPool(new okhttp3.ConnectionPool(16, 60, TimeUnit.SECONDS))
                .readTimeout(1, TimeUnit.SECONDS)
                .writeTimeout(1, TimeUnit.SECONDS)
                .connectTimeout(1, TimeUnit.SECONDS)
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
}
