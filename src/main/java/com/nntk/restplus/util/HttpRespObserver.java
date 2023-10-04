package com.nntk.restplus.util;

import com.nntk.restplus.abs.AbsBodyHandleRule;
import com.nntk.restplus.abs.AbsBasicRespObserver;
import org.springframework.http.HttpStatus;

public class HttpRespObserver {


    public static void observe(AbsBasicRespObserver observer, Throwable throwable, int httpStatus,
                               AbsBodyHandleRule absBodyHandleRule, boolean download) {
        absBodyHandleRule.init(absBodyHandleRule.getHttpBody());
        observer.complete();
        if (throwable != null) {
            observer.callUnknownException(throwable);
        } else {
            if (httpStatus != HttpStatus.OK.value()) {
                observer.callHttpFail(httpStatus, absBodyHandleRule.getHttpBody());
            } else {
                observer.callHttpSuccess();
                if (!download) {
                    if (absBodyHandleRule.isBusinessSuccess()) {
                        observer.callBusinessSuccess();
                    } else {
                        observer.callBusinessFail(absBodyHandleRule.getCode(), absBodyHandleRule.getMessage());
                    }
                }

            }
        }
    }

}
