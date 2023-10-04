package com.nntk.restplus.intercept;

import com.nntk.restplus.strategy.HttpExecuteContext;

public interface RestPlusHandleIntercept {

    public HttpExecuteContext handle(HttpExecuteContext context);

}
