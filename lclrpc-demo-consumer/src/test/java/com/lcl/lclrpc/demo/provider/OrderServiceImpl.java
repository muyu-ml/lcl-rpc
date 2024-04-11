package com.lcl.lclrpc.demo.provider;

import com.lcl.lclrpc.core.annotation.LclProvider;
import com.lcl.lclrpc.demo.api.Order;
import com.lcl.lclrpc.demo.api.OrderService;
import org.springframework.stereotype.Component;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Component
@LclProvider
public class OrderServiceImpl implements OrderService {
    /**
     * @param orderId
     * @return {@link Order}
     */
    @Override
    public Order getOrderById(Integer orderId) {
        if(orderId == 404){
            throw new RuntimeException("order not found --- 404");
        }
        return new Order(orderId, "order-" + System.currentTimeMillis());
    }
}
