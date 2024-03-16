package com.lcl.lclrpc.core.provider;

import com.lcl.lclrpc.core.annotation.LclProvider;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import com.lcl.lclrpc.core.meta.ProviderMeta;
import com.lcl.lclrpc.core.util.MethodUtils;
import com.lcl.lclrpc.core.util.TypeUtils;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Data
@Slf4j
public class ProviderBootstrap implements ApplicationContextAware {

    /**
     *
     */
    private ApplicationContext applicationContext;

    /**
     *
     */
    private MultiValueMap<String, ProviderMeta> skeleton = new LinkedMultiValueMap<>();

    /**
     *
     */
    @PostConstruct
    public void start() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(LclProvider.class);
        providers.forEach((k, v) -> System.out.println("provider: " + k + " -> " + v));
        providers.values().forEach(this::genIntrface);
        // 启动Netty服务
    }

    /**
     * @param obj
     */
    private void genIntrface(Object obj) {
        Class<?> itfer = obj.getClass().getInterfaces()[0];
        Method[] methods = itfer.getMethods();
        for (Method method : methods) {
            if(MethodUtils.isObjectMethod(method)){
                continue;
            }
            createProviderMeta(itfer, obj, method);
        }
    }

    private void createProviderMeta(Class<?> itfer, Object obj, Method method) {
        ProviderMeta providerMeta = new ProviderMeta();
        providerMeta.setMethod(method);
        providerMeta.setMethodSign(MethodUtils.buildMethodSign(method));
        providerMeta.setServiceImpl(obj);
        log.info("create a providerMeta: " + providerMeta);
        skeleton.add(itfer.getCanonicalName(), providerMeta);
    }

    /**
     * @param request
     * @return {@link RpcResponse}
     */
    public RpcResponse invoke(RpcRequest request) {
        RpcResponse rpcResponse = new RpcResponse();
        List<ProviderMeta> providerMetas = skeleton.get(request.getService());
        try {
            ProviderMeta providerMeta = findProviderMeta(providerMetas, request.getMethodSign());
            Method method = providerMeta.getMethod();
            // 类型转换，防止对象被序列化后丢失类型信息
            Object[] parameters = processParams(request.getParameters(), method.getParameterTypes());
            Object result = method.invoke(providerMeta.getServiceImpl(), parameters);
            rpcResponse.setStatus(true);
            rpcResponse.setData(result);
        } catch (InvocationTargetException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RuntimeException(e.getTargetException().getMessage()));
        } catch (IllegalAccessException e) {
            rpcResponse.setStatus(false);
            rpcResponse.setEx(new RuntimeException(e.getMessage()));
        }
        return rpcResponse;
    }

    private Object[] processParams(Object[] args, Class<?>[] parameterTypes) {
        if(args == null || args.length == 0){
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
     * @param providerMetas
     * @param methodSign
     * @return
     */
    private ProviderMeta findProviderMeta(List<ProviderMeta> providerMetas, String methodSign) {
        return providerMetas.stream().filter(x -> x.getMethodSign().equals(methodSign)).findFirst().orElse(null);
    }
}
