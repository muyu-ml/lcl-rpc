package com.lcl.lclrpc.core.provider;

import com.lcl.lclrpc.core.api.RpcContext;
import com.lcl.lclrpc.core.api.RpcException;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.governance.SlidingTimeWindow;
import com.lcl.lclrpc.core.meta.ProviderMeta;
import com.lcl.lclrpc.core.util.TypeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class ProviderInvoker {

    private MultiValueMap<String, ProviderMeta> skeleton;

    final Map<String, SlidingTimeWindow> windows = new HashMap<>();

    // todo 改成map，针对不同的服务用不同的流控
    // todo 改为全局流控（对多个节点共享数值），例如将流控值放到redis中
    private final int trafficControl;// = 20;
    final Map<String, String> metas;

    public ProviderInvoker(ProviderBootstrap providerBootstrap){
        this.skeleton = providerBootstrap.getSkeleton();
        this.metas = providerBootstrap.getProviderConfigProperties().getMetas();
        this.trafficControl = Integer.parseInt(this.metas.getOrDefault("tc", "20"));
    }

    /**
     * @param request
     * @return {@link RpcResponse}
     */
    public RpcResponse<Object> invoke(RpcRequest request) {
        log.debug(" ======>>> Provider.invoke request:{}", request);
        if(!request.getParams().isEmpty()){
            request.getParams().forEach(RpcContext :: setContextParameter);
        }
        RpcResponse<Object> rpcResponse = new RpcResponse();

        String service = request.getService();
        // 服务端限流，主流做法是使用令牌桶、漏桶算法
        synchronized (windows){
            SlidingTimeWindow window = windows.computeIfAbsent(service, k -> new SlidingTimeWindow());
            int trafficControl = Integer.parseInt(metas.getOrDefault("tc", "20"));
            if(window.calcSum() >= trafficControl){
                System.out.println(window);
                throw new RpcException("service " + service + " invoked in 30s/[" +
                        window.getSum() + "] larger than tpsLimit = " + trafficControl, RpcException.ExceedLimitEx);
            }
            window.record(System.currentTimeMillis());
            log.debug("service {} in window with {}", service, window.getSum());
        }


        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = providerMeta.getMethod();
            // 类型转换，防止对象被序列化后丢失类型信息
            Object[] parameters = processParams(request.getArgs(), method.getParameterTypes());
            Object result = method.invoke(providerMeta.getServiceImpl(), parameters);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
        } catch (InvocationTargetException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RpcException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RpcException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processParams(Object[] args, Class<?>[] parameterTypes) {
        if (args == null || args.length == 0) {
            return args;
        }
        Object[] actuals = new Object[args.length];
        for (int i = 0; i < args.length; i++) {
            actuals[i] = TypeUtils.cast(args[i], parameterTypes[i]);
        }
        return actuals;
    }

    /**
     * 获取方法签名相同的 ProviderMeta
     *
     * @param providerMetas
     * @param methodSign
     * @return
     */
    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst().orElse(null);
    }
}
