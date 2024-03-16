package com.lcl.lclrpc.core.api;

import lombok.Data;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Data
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
    private Object[] parameters;
}
