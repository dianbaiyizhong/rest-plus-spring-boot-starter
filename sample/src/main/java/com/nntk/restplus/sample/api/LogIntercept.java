package com.nntk.restplus.sample.api;

import com.nntk.restplus.intercept.RestPlusHandleIntercept;
import com.nntk.restplus.strategy.HttpExecuteContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class LogIntercept implements RestPlusHandleIntercept {
    @Override
    public HttpExecuteContext handle(HttpExecuteContext context) {

        log.info("日志拦截器===你可以在这里全局处理打印日志...");

        return context;
    }
}
