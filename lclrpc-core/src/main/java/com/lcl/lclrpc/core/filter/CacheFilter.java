package com.lcl.lclrpc.core.filter;

import com.lcl.lclrpc.core.api.Filter;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import org.springframework.core.annotation.Order;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


/**
 * cache filter
 * 所有 Filter 的最后一个，防止被其他 Filter 覆盖
 */
@Order(Integer.MAX_VALUE)
public class CacheFilter implements Filter {

    // todo: 替换成 guava cache，加容量和过期时间
    static Map<String, Object> cache = new ConcurrentHashMap();

    @Override
    public Object preFilter(RpcRequest request) {
        return cache.get(request.toString());
    }

    @Override
    public Object postFilter(RpcRequest request, RpcResponse response, Object result) {
        cache.putIfAbsent(request.toString(), result);
        return result;
    }
}
