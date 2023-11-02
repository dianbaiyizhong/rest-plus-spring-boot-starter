package com.nntk.restplus.sample.api;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nntk.restplus.abs.AbsResponseHandleRule;
import org.springframework.stereotype.Component;

/**
 * 自定义返回结果定义规则
 */
@Component
public class MyResponseHandleRule extends AbsResponseHandleRule {

    private JSONObject jsonObject;

    @Override
    public void init(String httpBody) {
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public boolean isBusinessSuccess() {
        return getCode() == 0;
    }


    @Override
    public String getMessage() {
        return "";
    }

    @Override
    public String getData() {
        return getHttpBody();
    }

}
