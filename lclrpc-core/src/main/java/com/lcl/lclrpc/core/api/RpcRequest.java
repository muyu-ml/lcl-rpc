package com.lcl.lclrpc.core.api;

import lombok.Data;

@Data
public class RpcRequest {
    private String service; // 服务名
    private String methodName; // 方法名
    private Object[] parameters; // 参数
}
