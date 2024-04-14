package com.lcl.lclrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.Builder;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * 描述provider服务的元数据
 */
@Data
@Builder
public class ServiceMeta {
    private String app;
    private String namespace;
    // 环境
    private String env;
    private String name;
    private Map<String, String> parameters = new HashMap<>();

    public String toPath(){
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }

    public String toMetas() {
        return JSON.toJSONString(this.getParameters());
    }
}
