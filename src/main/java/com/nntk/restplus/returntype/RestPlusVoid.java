package com.nntk.restplus.returntype;

import com.nntk.restplus.abs.AbsBodyHandleRule;



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

    public AbsBodyHandleRule getRespBodyHandleRule() {
        return absBodyHandleRule;
    }

    public void setRespBodyHandleRule(AbsBodyHandleRule absBodyHandleRule) {
        this.absBodyHandleRule = absBodyHandleRule;
    }

    private Throwable throwable;
    private AbsBodyHandleRule absBodyHandleRule;
}
