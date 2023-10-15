package com.nntk.restplus.abs;

import com.nntk.restplus.strategy.HttpExecuteContext;

public abstract class AbsBasicRespObserver {


    public Class<?> getRequestClass() {
        return requestClass;
    }

    public void setRequestClass(Class<?> requestClass) {
        this.requestClass = requestClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    private Class<?> requestClass;


    private String methodName;

    public HttpExecuteContext getHttpExecuteContext() {
        return httpExecuteContext;
    }

    public void setHttpExecuteContext(HttpExecuteContext httpExecuteContext) {
        this.httpExecuteContext = httpExecuteContext;
    }

    private HttpExecuteContext httpExecuteContext;


    public abstract void beforeRequest();


    /**
     * 业务异常，也就是所谓的code不等于0
     */
    public abstract void callBusinessFail(int code, String messages);

    /**
     * 请求结束
     */
    public abstract void complete();

    /**
     * http异常，也就是所谓的http状态码不等于200
     */
    public abstract void callHttpFail(int httpStatus, String message);


    /**
     * 未知异常，一般是后台500
     */
    public abstract void callUnknownException(Throwable throwable);


    /**
     * http请求正常，也就是所谓的http状态码等于200
     */
    public abstract void callHttpSuccess();

    /**
     * http请求正常，且code等于0，业务正常
     */
    public abstract void callBusinessSuccess();


}
