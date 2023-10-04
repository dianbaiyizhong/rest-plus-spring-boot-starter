package com.nntk.restplus.returntype;

import com.nntk.restplus.abs.AbsBasicRespObserver;
import com.nntk.restplus.abs.AbsBodyHandleRule;
import com.nntk.restplus.abs.AbsHttpFactory;
import com.nntk.restplus.util.HttpRespObserver;
import com.nntk.restplus.util.IoUtil;

import java.io.File;
import java.lang.reflect.Type;


public class Call<T> {


    public void setBodyStream(byte[] bodyStream) {
        this.bodyStream = bodyStream;
    }

    private byte[] bodyStream;

    public void setHttpStatus(int httpStatus) {
        this.httpStatus = httpStatus;
    }

    private int httpStatus;


    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    private Throwable throwable;

    public void setDownloadFile(File downloadFile) {
        this.downloadFile = downloadFile;
    }

    private File downloadFile;

    public void setConfigObserver(AbsBasicRespObserver configObserver) {
        this.configObserver = configObserver;
    }


    public void setRespBodyHandleRule(AbsBodyHandleRule absBodyHandleRule) {
        this.absBodyHandleRule = absBodyHandleRule;
    }

    private AbsBasicRespObserver configObserver;

    private AbsBodyHandleRule absBodyHandleRule;

    public void setReturnType(Type returnType) {
        this.returnType = returnType;
    }

    private Type returnType;

    private boolean isObserve = false;

    public void setHttpFactory(AbsHttpFactory httpFactory) {
        this.httpFactory = httpFactory;
    }

    private AbsHttpFactory httpFactory;


    public Call<T> observe(AbsBasicRespObserver observer) {
        isObserve = true;

        HttpRespObserver.observe(observer, throwable, httpStatus, absBodyHandleRule, bodyStream != null);
        return this;
    }

    public T executeForResult() {
        if (!isObserve) {
            observe(configObserver);
        }
        String data = absBodyHandleRule.getHttpBody();
        return httpFactory.parseObject(data, returnType);
    }

    public T executeForData() {
        if (!isObserve) {
            observe(configObserver);
        }

        if (bodyStream != null) {
            return (T) IoUtil.byteToFile(bodyStream, downloadFile);
        }
        String data = absBodyHandleRule.getData();
        return httpFactory.parseObject(data, returnType);
    }

}
