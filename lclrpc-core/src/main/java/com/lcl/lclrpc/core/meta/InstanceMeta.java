package com.lcl.lclrpc.core.meta;

import com.alibaba.fastjson.JSON;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;


/**
 * 描述provider服务实例的元数据
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InstanceMeta {
    private String schema;
    private String host;
    private int port;
    private String context;

    private boolean status;
    private Map<String, String> parameters = new HashMap<>();

    public InstanceMeta(String schema, String host, int port, String context) {
        this.schema = schema;
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public String toPath() {
        return String.format("%s_%d", host, port);
    }

    public static InstanceMeta http(String host, int port) {
        return new InstanceMeta("http", host, port, "lclrpc");
    }

    public String toUrl() {
        return String.format("%s://%s:%d/%s", schema, host, port, context);
    }

    public String toMetas() {
        return JSON.toJSONString(this.getParameters());
    }
}
