package com.lcl.lclrpc.core.api;

import com.lcl.lclrpc.core.meta.InstanceMeta;

import java.util.List;

public interface Loadbalancer<T> {

    Loadbalancer<InstanceMeta> Default = providers -> (providers == null || providers.size() == 0) ? null : providers.get(0);

    T choose(List<T> providers);
}
