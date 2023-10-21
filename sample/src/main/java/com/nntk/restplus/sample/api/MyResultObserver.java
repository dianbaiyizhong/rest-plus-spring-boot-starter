package com.nntk.restplus.sample.api;

import com.nntk.restplus.abs.AbsBasicRespObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 结果统一处理，可以用来搞一些日志输出等
 */
@Slf4j
@Component
public class MyResultObserver extends AbsBasicRespObserver {
    @Override
    public void beforeRequest() {
        log.info("====你可以获取到当前的class:{}和方法:{}", getRequestClass(), getMethodName());
        log.info("请求之前的操作:{}", getHttpExecuteContext().getUrl());
    }

    @Override
    public void callBusinessFail(int code, String messages) {
        log.info("callBusinessFail...");
    }

    @Override
    public void complete() {
        log.info("无论最终结果如何，请求已经完成了");
    }

    @Override
    public void callHttpFail(int httpStatus, String message) {
        log.info("http状态码不是200...{},{}", httpStatus, message);
    }

    @Override
    public void callUnknownException(Throwable throwable) {
        log.error("发生了未知错误，一般是状态码500的错误...", throwable);
    }

    @Override
    public void callHttpSuccess() {
        log.info("状态码200...");
    }

    @Override
    public void callBusinessSuccess() {
        log.info("业务请求成功了，一般是code为0...");

    }
}
