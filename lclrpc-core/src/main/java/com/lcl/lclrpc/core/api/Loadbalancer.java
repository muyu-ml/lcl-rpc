package com.lcl.lclrpc.core.api;

import java.util.List;

public interface Loadbalancer<T> {

    Loadbalancer Default = providers -> (providers == null || providers.size() == 0) ? null : providers.get(0);

    T choose(List<T> providers);
}
