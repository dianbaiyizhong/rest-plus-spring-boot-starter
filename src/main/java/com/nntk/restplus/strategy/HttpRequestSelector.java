package com.nntk.restplus.strategy;

import com.nntk.restplus.annotation.GET;
import com.nntk.restplus.annotation.POST;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

@Component
public class HttpRequestSelector {

    private Map<Class<? extends Annotation>, HttpRequestBaseHandler> requestTypeMap = new HashMap<>();


    public boolean isRequestType(Class<? extends Annotation> type) {
        return requestTypeMap.containsKey(type);
    }

    @Resource
    private GetRequestHandler getRequestHandler;

    @Resource
    private PostRequestHandler postRequestHandler;

    @PostConstruct
    public void postConstruct() {

        requestTypeMap.put(GET.class, getRequestHandler);
        requestTypeMap.put(POST.class, postRequestHandler);

    }

    public HttpRequestBaseHandler select(Class<? extends Annotation> type) {
        HttpRequestBaseHandler handler = requestTypeMap.get(type);
        handler.setRequestType(type);
        return handler;
    }

}
