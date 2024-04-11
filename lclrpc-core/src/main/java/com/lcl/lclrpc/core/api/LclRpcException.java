package com.lcl.lclrpc.core.api;

import lombok.Data;

@Data
public class LclRpcException extends RuntimeException{

    private String errorCode;

    public LclRpcException() {
    }

    public LclRpcException(String message) {
        super(message);
    }

    public LclRpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public LclRpcException(Throwable cause) {
        super(cause);
    }

    public LclRpcException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public LclRpcException(Throwable cause, String errorCode) {
        super(cause);
        this.errorCode = errorCode;
    }

    // X：表示技术类异常
    // Y：业务类异常
    // Z：unknown，不确定的异常，确定后再将其归类到 X 或 Y
    public static final String SocketTimeoutEx = "X001" + "-" + "http_invoke_timeout";
    public static final String NoSuchMethodEx = "X002" + "-" + "method_not_exists";
    public static final String UnKnownEx = "Z001" + "-" + "unknown";

}
