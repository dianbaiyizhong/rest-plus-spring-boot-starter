package com.nntk.restplus.util;

import com.nntk.restplus.abs.AbsRespHandleRule;
import com.nntk.restplus.abs.AbsBasicRespObserver;
import org.springframework.http.HttpStatus;

public class HttpRespObserver {


    public static void observe(AbsBasicRespObserver observer, Throwable throwable, int httpStatus,
                               AbsRespHandleRule absRespHandleRule, boolean download) {
        absRespHandleRule.init(absRespHandleRule.getHttpBody());
        observer.complete();
        if (throwable != null) {
            observer.callUnknownException(throwable);
        } else {
            if (httpStatus != HttpStatus.OK.value()) {
                observer.callHttpFail(httpStatus, absRespHandleRule.getHttpBody());
            } else {
                observer.callHttpSuccess();
                if (!download) {
                    if (absRespHandleRule.isBusinessSuccess()) {
                        observer.callBusinessSuccess();
                    } else {
                        observer.callBusinessFail(absRespHandleRule.getCode(), absRespHandleRule.getMessage());
                    }
                }

            }
        }
    }

}
