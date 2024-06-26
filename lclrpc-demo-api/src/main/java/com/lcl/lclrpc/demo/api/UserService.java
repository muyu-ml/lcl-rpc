package com.lcl.lclrpc.demo.api;


import java.util.List;
import java.util.Map;

/**
 * @author conglongli
 * @date 2024/03/07
 */
public interface UserService {

    User findById(int id);

    User findById(int id, String name);

    long getId(long id);

    long getId(User user);

    long getId(float id);

    String getName();

    String getName(int id);

    int[] getIds();
    long[] getLongIds();
    int[] getIds(int[] ids);

    User[] findUsers(User[] users);

    List<User> getList(List<User> userList);

    Map<String, User> getMap(Map<String, User> userMap);

    Boolean getFlag(boolean flag);

    User findById(long id);

    User ex(boolean flag);

    void setTimeoutPort(String timeoutPort);

    User find(int timeout);

    String echoParameter(String key);
}
