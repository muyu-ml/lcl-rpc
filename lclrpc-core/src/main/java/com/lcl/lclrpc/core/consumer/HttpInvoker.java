package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;

public interface HttpInvoker {
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
