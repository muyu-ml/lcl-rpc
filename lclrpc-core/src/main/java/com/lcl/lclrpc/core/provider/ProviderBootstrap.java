package com.lcl.lclrpc.core.provider;

import com.lcl.lclrpc.core.annotation.LclProvider;
import com.lcl.lclrpc.core.api.RpcRequest;
import com.lcl.lclrpc.core.api.RpcResponse;
import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Data
public class ProviderBootstrap implements ApplicationContextAware {

    /**
     *
     */
    private ApplicationContext applicationContext;

    /**
     *
     */
    private Map<String, Object> skeleton = new HashMap<>();

    /**
     * @param request
     * @return {@link RpcResponse}
     */
    public RpcResponse invokeRequest(RpcRequest request) {
        Object bean = skeleton.get(request.getService());
        try {
            Method method = findMethod(bean.getClass(), request.getMethodName());
            Object result = method.invoke(bean, request.getParameters());
            return new RpcResponse(true, result);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * @param aClass
     * @param methodName
     * @return {@link Method}
     */
    private Method findMethod(Class<?> aClass, String methodName) {
        Method[] methods = aClass.getMethods();
        for (Method method : methods) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        throw new RuntimeException("no such method: " + methodName);
    }

    /**
     *
     */
    @PostConstruct
    public void buildProviders() {
        Map<String, Object> providers = applicationContext.getBeansWithAnnotation(LclProvider.class);
        providers.forEach((k, v) -> System.out.println("provider: " + k + " -> " + v));
        providers.values().forEach(this::genIntrface);
        // 启动Netty服务
    }

    /**
     * @param obj
     */
    private void genIntrface(Object obj) {
        Class<?> anInterface = obj.getClass().getInterfaces()[0];
        skeleton.put(anInterface.getName(), obj);
    }

}
