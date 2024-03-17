package com.lcl.lclrpc.core.api;

import lombok.Data;

import java.util.List;

@Data
public class RpcContext {
    Router router;
    Loadbalancer loadbalancer;
    List<Filter> filters;
}
