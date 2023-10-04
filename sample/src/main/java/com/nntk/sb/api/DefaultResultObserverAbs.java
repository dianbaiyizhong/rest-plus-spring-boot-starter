package com.nntk.sb.api;

import com.nntk.restplus.abs.AbsBasicRespObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结果统一处理，可以用来搞一些日志输出等
 */
@Slf4j
@Component
public class DefaultResultObserverAbs extends AbsBasicRespObserver {
    @Override
    public void callBusinessFail(int code, String messages) {
        log.info("callBusinessFail...");
    }

    @Override
    public void complete() {
        log.info("complete...");
    }

    @Override
    public void callHttpFail(int httpStatus, String message) {
        log.info("callHttpFail...{},{}", httpStatus, message);

    }

    @Override
    public void callUnknownException(Throwable throwable) {
        log.error("callUnknownException...{}", throwable);
    }

    @Override
    public void callHttpSuccess() {
        log.info("callHttpSuccess...");
    }

    @Override
    public void callBusinessSuccess() {
        log.info("callBusinessSuccess...");

    }
}
