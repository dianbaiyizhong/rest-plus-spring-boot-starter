package com.nntk.restplus.sample.api;

import com.nntk.restplus.intercept.RestPlusHandleIntercept;
import com.nntk.restplus.strategy.HttpExecuteContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TokenIntercept implements RestPlusHandleIntercept {
    @Override
    public HttpExecuteContext handle(HttpExecuteContext context) {

        log.info("=======MyIntercept2");

        return context;
    }
}
