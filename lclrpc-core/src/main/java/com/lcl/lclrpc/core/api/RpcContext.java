package com.lcl.lclrpc.core.api;

import com.lcl.lclrpc.core.meta.InstanceMeta;
import lombok.Data;

import java.util.List;

@Data
public class RpcContext {
    Router<InstanceMeta> router;
    Loadbalancer<InstanceMeta> loadbalancer;
    List<Filter> filters;
}
