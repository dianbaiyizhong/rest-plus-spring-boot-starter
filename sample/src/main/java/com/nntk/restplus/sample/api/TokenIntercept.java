package com.nntk.restplus.sample.api;

import com.nntk.restplus.intercept.RestPlusHandleIntercept;
import com.nntk.restplus.strategy.HttpExecuteContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class TokenIntercept implements RestPlusHandleIntercept {
    @Override
    public HttpExecuteContext handle(HttpExecuteContext context) {
        log.info("token拦截器，你可以在这里验证，并把token塞入到参数里");
        Map<String,String> header = new HashMap<>();
        header.put("token", "=====");
        context.setHeaderMap(header);
        return context;
    }
}
