package com.lcl.lclrpc.core.api;

/**
 * 过滤器
 */
public interface Filter {

    Object preFilter(RpcRequest request);

    Object postFilter(RpcRequest request, RpcResponse response, Object result);


    Filter Default = new Filter() {
        @Override
        public Object preFilter(RpcRequest request) {
            return null;
        }

        @Override
        public RpcResponse postFilter(RpcRequest request, RpcResponse response, Object result) {
            return response;
        }
    };

}
