package com.lcl.lclrpc.demo.api;


/**
 * @author conglongli
 * @date 2024/03/07
 */
public interface OrderService {

    /**
     * 根据id获取订单信息
     * @param orderId
     * @return {@link Order}
     */
    Order getOrderById(Integer orderId);
}
