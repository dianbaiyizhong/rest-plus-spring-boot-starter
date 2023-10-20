package com.nntk.restplus.returntype;

import com.nntk.restplus.abs.AbsResponseHandleRule;



public class RestPlusVoid {
    private int httpStatus;

    public int getHttpStatus() {
        return httpStatus;
    }

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public AbsResponseHandleRule getRespBodyHandleRule() {
        return absResponseHandleRule;
    }

    public void setRespBodyHandleRule(AbsResponseHandleRule absResponseHandleRule) {
        this.absResponseHandleRule = absResponseHandleRule;
    }

    private Throwable throwable;
    private AbsResponseHandleRule absResponseHandleRule;
}
