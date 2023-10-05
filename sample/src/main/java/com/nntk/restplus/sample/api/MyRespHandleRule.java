package com.nntk.restplus.sample.api;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nntk.restplus.abs.AbsRespHandleRule;
import org.springframework.stereotype.Component;

/**
 * 自定义返回结果定义规则
 */
@Component
public class MyRespHandleRule extends AbsRespHandleRule {

    private JSONObject jsonObject;

    @Override
    public void init(String httpBody) {
        this.jsonObject = JSON.parseObject(httpBody);
    }

    @Override
    public int getCode() {
        return jsonObject.getIntValue("code");
    }

    @Override
    public boolean isBusinessSuccess() {
        return getCode() == 0;
    }


    @Override
    public String getMessage() {
        return jsonObject.getString("message");
    }

    @Override
    public String getData() {
        return jsonObject.getString("data");
    }

}
