package com.lcl.lclrpc.demo.api;


/**
 * @author conglongli
 * @date 2024/03/07
 */
public interface UserService {

    /**
     * 根据id获取用户信息
     * @param id
     * @return {@link User}
     */
    User getUserById(Integer id);

    User getUserById(Integer id, String name);

    int getId(Integer id);

    String getName();

    int getHeight();
}
