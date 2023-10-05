package com.nntk.restplus.sample.api;

import cn.hutool.core.io.IoUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.nntk.restplus.abs.AbsHttpFactory;
import com.nntk.restplus.entity.RestPlusResponse;
import com.nntk.restplus.strategy.HttpExecuteContext;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.function.BiConsumer;

/**
 * 工厂模式，实现子产品，底层映射调用
 * 1.自定义数据转化逻辑（可以用gson，fastjson，jackson等）
 * 2.自定义http请求方式，有hutool，okhttp，restTemplate
 */
@Component
public class HutoolHttpFactory extends AbsHttpFactory {

    @Override
    public String toJsonString(Object object) {
        return JSON.toJSONString(object);
    }

    @Override
    public <T> T parseObject(String json, Type type) {
        return JSON.parseObject(json, type);
    }

    @Override
    public RestPlusResponse post(HttpExecuteContext context) {
        HttpRequest httpRequest = HttpUtil.createPost(context.getUrl());
        if (context.getHeaderMap() != null) {
            httpRequest.addHeaders(context.getHeaderMap());
        }
        httpRequest.contentType(context.getContentType());
        if (context.getContentType().equals(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            httpRequest.form(context.getBodyMap());

        } else {
            httpRequest.body(JSON.toJSONString(context.getBodyMap()));
        }

        HttpResponse response = httpRequest.execute();
        RestPlusResponse restPlusResponse = new RestPlusResponse();
        restPlusResponse.setHttpStatus(response.getStatus());
        restPlusResponse.setBody(response.body());
        return restPlusResponse;
    }

    @Override
    public RestPlusResponse put(HttpExecuteContext context) {
        return null;
    }

    @Override
    public RestPlusResponse delete(HttpExecuteContext context) {
        return null;
    }

    @Override
    public RestPlusResponse get(HttpExecuteContext context) {
        HttpRequest httpRequest = HttpRequest.get(context.getUrl());
        HttpResponse response = httpRequest.execute();
        RestPlusResponse restPlusResponse = new RestPlusResponse();
        restPlusResponse.setHttpStatus(response.getStatus());

        if (context.isDownload()) {
            restPlusResponse.setBodyStream(IoUtil.readBytes(response.bodyStream()));
        } else {
            restPlusResponse.setBody(response.body());
        }
        return restPlusResponse;
    }
}
