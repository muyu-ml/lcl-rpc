package com.lcl.lclrpc.core.api;

import com.alibaba.fastjson.JSONArray;
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
    public static ThreadLocal<Map<String, String>> ContextParameters = new ThreadLocal<>() {
        @Override
        protected Map<String, String> initialValue() {
            return new HashMap<>();
        }
    };

    public String param(String key) {
        return parameters.get(key);
    }

    public static void setContextParameter(Map<String, String> params) {
        ContextParameters.set(params);
    }

    public static String getContextParameter(String key) {
        return ContextParameters.get().get(key);
    }

    public static void removeContextParamerters() {
        ContextParameters.remove();
    }

    public static void setContextParameter(String key, String value) {
        ContextParameters.get().put(key, value);
    }
}
