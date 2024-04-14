package com.lcl.lclrpc.core.transport;

import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.provider.ProviderInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SpringBootTransport {

    /**
     *
     */
    @Autowired
    private ProviderInvoker providerInvoker;

    /**
     * 使用HTTP + JSON 作为序列化和通信协议
     * @param request
     * @return {@link RpcResponse}
     */
    @RequestMapping("/lclrpc")
    public RpcResponse invoke(@RequestBody RpcRequest request) {
        return providerInvoker.invoke(request);
    }
}
