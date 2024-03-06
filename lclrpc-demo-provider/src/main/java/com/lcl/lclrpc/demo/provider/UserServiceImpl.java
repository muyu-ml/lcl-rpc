package com.lcl.lclrpc.demo.provider;

import com.lcl.lclrpc.core.annotation.LclProvider;
import com.lcl.lclrpc.demo.api.User;
import com.lcl.lclrpc.demo.api.UserService;
import org.springframework.stereotype.Component;

@Component
@LclProvider
public class UserServiceImpl implements UserService {
    @Override
    public User getUserById(Integer id) {
        return new User(id, "lcl-" + System.currentTimeMillis());
    }
}
