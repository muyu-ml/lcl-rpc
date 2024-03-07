package com.lcl.lclrpc.demo.provider;

import com.lcl.lclrpc.core.annotation.LclProvider;
import com.lcl.lclrpc.demo.api.User;
import com.lcl.lclrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

/**
 * @author conglongli
 * @date 2024/03/07
 * @doc
 */
@Component
@LclProvider
public class UserServiceImpl implements UserService {
    /**
     * @param id
     * @return {@link User}
     */
    @Override
    public User getUserById(Integer id) {
        return new User(id, "lcl-" + System.currentTimeMillis());
    }
}
