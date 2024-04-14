package com.lcl.lclrpc.core.filter;

import com.lcl.lclrpc.core.api.Filter;
import com.lcl.lclrpc.core.api.RpcContext;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;

import java.util.Map;

public class ParameterFilter implements Filter {
    @Override
    public Object preFilter(RpcRequest request) {
        Map<String, String> params = RpcContext.ContextParameters.get();
        if (params != null) {
            request.getParams().putAll(params);
        }
        return null;
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        RpcContext.ContextParameters.get().clear(); // 如果不清空，会导致线程复用时参数混乱
        return null;
    }
}
