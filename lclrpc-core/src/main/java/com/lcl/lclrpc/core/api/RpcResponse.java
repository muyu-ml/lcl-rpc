package com.lcl.lclrpc.core.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RpcResponse<T> {
    private boolean status; // 是否成功
    private T data; // 返回数据
}
