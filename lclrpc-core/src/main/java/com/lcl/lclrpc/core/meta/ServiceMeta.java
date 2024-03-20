package com.lcl.lclrpc.core.meta;

import lombok.Builder;
import lombok.Data;

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
    private String version;

    public String toPath(){
        return String.format("%s_%s_%s_%s", app, namespace, env, name);
    }
}
