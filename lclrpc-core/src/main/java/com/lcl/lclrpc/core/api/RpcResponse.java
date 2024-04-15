package com.lcl.lclrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    /**
     * 是否成功
     */
    private boolean status;
    /**
     * 返回数据
     */
    private T data;

    /**
     * 异常信息
     */
    RpcException ex;
}
