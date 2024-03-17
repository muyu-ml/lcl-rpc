package com.lcl.lclrpc.core.cluster;

import com.lcl.lclrpc.core.api.Loadbalancer;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class RoundRibonLoadbalancer<T> implements Loadbalancer<T> {

    AtomicInteger index = new AtomicInteger(0);

    @Override
    public T choose(List<T> providers) {
        if(providers != null && providers.size() > 0) {
            int i = index.get();
            return providers.get((index.getAndIncrement()&0x7fffffff) % providers.size());
        }
        return null;
    }
}
