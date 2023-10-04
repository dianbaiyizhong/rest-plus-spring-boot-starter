package com.nntk.sb.api.my;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nntk.restplus.abs.AbsBodyHandleRule;
import org.springframework.stereotype.Component;

@Component
public class MyAbsBodyHandleRule extends AbsBodyHandleRule {

    private JSONObject jsonObject;

    @Override
    public void init(String httpBody) {
        this.jsonObject = JSON.parseObject(httpBody);
    }

    @Override
    public int getCode() {
        return jsonObject.getInteger("code");
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
