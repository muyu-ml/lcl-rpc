package com.lcl.lclrpc.core.cluster;

import com.lcl.lclrpc.core.api.Loadbalancer;

import java.util.List;
import java.util.Random;

public class RandomLoadbalancer<T> implements Loadbalancer<T> {

    Random random = new Random();

    @Override
    public T choose(List<T> providers) {
        if(providers != null && providers.size() > 0) {
            return providers.get(random.nextInt(providers.size()));
        }
        return null;
    }
}
