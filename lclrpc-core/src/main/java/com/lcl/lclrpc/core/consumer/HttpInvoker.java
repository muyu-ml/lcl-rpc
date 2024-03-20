package com.lcl.lclrpc.core.consumer;

import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.meta.InstanceMeta;

public interface HttpInvoker {
    RpcResponse<?> post(RpcRequest rpcRequest, String url);
}
