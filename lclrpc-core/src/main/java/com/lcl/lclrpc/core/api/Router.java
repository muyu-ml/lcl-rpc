package com.lcl.lclrpc.core.api;

import com.lcl.lclrpc.core.meta.InstanceMeta;

import java.util.List;

public interface Router<T> {
    Router<InstanceMeta> Default = providers -> providers;

    List<T> route(List<T> providers);
}
