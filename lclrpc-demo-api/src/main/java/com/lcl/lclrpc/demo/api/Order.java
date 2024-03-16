package com.lcl.lclrpc.demo.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    /**
     *
     */
    private Integer orderId;
    /**
     *
     */
    private String price;
}
