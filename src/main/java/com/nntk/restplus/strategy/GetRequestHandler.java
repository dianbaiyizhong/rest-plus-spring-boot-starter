package com.nntk.restplus.strategy;

import com.nntk.restplus.abs.AbsHttpFactory;
import com.nntk.restplus.entity.RestPlusResponse;
import org.springframework.stereotype.Component;

@Component
public class GetRequestHandler extends HttpRequestBaseHandler {


    @Override
    public RestPlusResponse executeHttp(HttpExecuteContext context) {
        AbsHttpFactory httpFactory = context.getHttpFactory();
        return httpFactory.get(context);
    }
}
