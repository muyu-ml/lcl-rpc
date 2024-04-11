package com.lcl.lclrpc.core.api;

import lombok.Data;

@Data
public class RpcException extends RuntimeException{

    private String errorCode;

    public RpcException() {
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public RpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    // TODO：改为枚举
    // X：表示技术类异常
    // Y：业务类异常
    // Z：unknown，不确定的异常，确定后再将其归类到 X 或 Y
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
    public static final String UnKnownEx = "Z001" + "-" + "unknown";

}
