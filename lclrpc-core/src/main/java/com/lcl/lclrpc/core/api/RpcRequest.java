package com.lcl.lclrpc.core.api;

import lombok.Data;
import lombok.ToString;

import java.util.HashMap;
import java.util.Map;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Data
@ToString
public class RpcRequest {
    /**
     * 服务名
     */
    private String service;
    /**
     * 方法名
     */
    private String methodSign;
    /**
     * 参数
     */
    private Object[] args;

    /**
     * 跨调用方要传递的参数
     */
    private Map<String, String> params = new HashMap<>();
}
