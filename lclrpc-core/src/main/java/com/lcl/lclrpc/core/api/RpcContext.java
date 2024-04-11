package com.lcl.lclrpc.core.api;

import com.lcl.lclrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class RpcContext {
    Router<InstanceMeta> router;
    Loadbalancer<InstanceMeta> loadbalancer;
    List<Filter> filters;
    private Map<String, String> parameters = new HashMap<>();
}
