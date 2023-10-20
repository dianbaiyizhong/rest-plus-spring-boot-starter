package com.nntk.restplus.returntype;

import com.nntk.restplus.abs.AbsBasicRespObserver;
import com.nntk.restplus.abs.AbsResponseHandleRule;
import com.nntk.restplus.abs.AbsHttpFactory;
import com.nntk.restplus.util.HttpRespObserver;
import com.nntk.restplus.util.IoUtil;
import org.springframework.http.HttpStatus;

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


    public void setRespBodyHandleRule(AbsResponseHandleRule absResponseHandleRule) {
        this.absResponseHandleRule = absResponseHandleRule;
    }

    private AbsBasicRespObserver configObserver;

    private AbsResponseHandleRule absResponseHandleRule;

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
        HttpRespObserver.observe(observer, throwable, httpStatus, absResponseHandleRule, bodyStream != null);
        return this;
    }


    public T executeForResult() {
        if (!isObserve) {
            HttpRespObserver.observe(configObserver, throwable, httpStatus, absResponseHandleRule, bodyStream != null);
        }
        if (bodyStream != null) {
            return (T) IoUtil.byteToFile(bodyStream, downloadFile);
        }
        if (httpStatus == HttpStatus.OK.value() && absResponseHandleRule.isBusinessSuccess()) {
            return httpFactory.parseObject(absResponseHandleRule.getHttpBody(), returnType);
        } else {
            return null;
        }
    }

    public T executeForData() {
        if (!isObserve) {
            HttpRespObserver.observe(configObserver, throwable, httpStatus, absResponseHandleRule, bodyStream != null);
        }
        if (httpStatus == HttpStatus.OK.value() && absResponseHandleRule.isBusinessSuccess()) {
            return httpFactory.parseObject(absResponseHandleRule.getData(), returnType);
        } else {
            return null;
        }
    }
}
