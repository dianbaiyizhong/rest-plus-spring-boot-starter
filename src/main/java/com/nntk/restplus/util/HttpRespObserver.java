package com.nntk.restplus.util;

import com.nntk.restplus.abs.AbsResponseHandleRule;
import com.nntk.restplus.abs.AbsBasicRespObserver;
import org.springframework.http.HttpStatus;

public class HttpRespObserver {


    public static void observe(AbsBasicRespObserver observer, Throwable throwable, int httpStatus,
                               AbsResponseHandleRule absResponseHandleRule, boolean download) {
        absResponseHandleRule.init(absResponseHandleRule.getHttpBody());
        observer.complete();
        if (throwable != null) {
            observer.callUnknownException(throwable);
        } else {
            if (httpStatus != HttpStatus.OK.value()) {
                observer.callHttpFail(httpStatus, absResponseHandleRule.getHttpBody());
            } else {
                observer.callHttpSuccess();
                if (!download) {
                    if (absResponseHandleRule.isBusinessSuccess()) {
                        observer.callBusinessSuccess();
                    } else {
                        observer.callBusinessFail(absResponseHandleRule.getCode(), absResponseHandleRule.getMessage());
                    }
                }

            }
        }
    }

}
