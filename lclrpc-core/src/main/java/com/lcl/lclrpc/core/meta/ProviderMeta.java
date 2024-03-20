package com.lcl.lclrpc.core.meta;

import lombok.Builder;
import lombok.Data;

import java.lang.reflect.Method;

@Data
@Builder
public class ProviderMeta {
    // 方法名
    Method method;
    // 方法签名
    String methodSign;
    // 服务实现类
    Object serviceImpl;
}
