package com.nntk.restplus.sample;

import com.nntk.restplus.sample.SampleApplication;
import com.nntk.restplus.sample.api.UserInfoApi;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import javax.annotation.Resource;

@SpringBootTest(classes = SampleApplication.class)
public class RestTest {

    @Resource
    private UserInfoApi userInfoApi;

    /**
     * 最常见的一种请求方式
     */
    @Test
    public void postJson() {
        userInfoApi.download("C:\\Users\\hhm\\Downloads\\11\\Capture0011111.png");
    }

}
