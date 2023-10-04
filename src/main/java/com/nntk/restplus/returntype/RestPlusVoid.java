package com.nntk.restplus.returntype;

import com.nntk.restplus.abs.AbsRespHandleRule;



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

    public AbsRespHandleRule getRespBodyHandleRule() {
        return absRespHandleRule;
    }

    public void setRespBodyHandleRule(AbsRespHandleRule absRespHandleRule) {
        this.absRespHandleRule = absRespHandleRule;
    }

    private Throwable throwable;
    private AbsRespHandleRule absRespHandleRule;
}
