package com.lcl.lclrpc.core.api;

import java.util.List;

public interface Router<T> {
    Router Default = providers -> providers;

    List<T> route(List<T> providers);
}
