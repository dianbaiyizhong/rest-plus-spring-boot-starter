package com.nntk.restplus.sample.controller;

import com.nntk.restplus.sample.api.UserInfo;
import com.nntk.restplus.sample.api.DefaultResultObserver;
import com.nntk.restplus.sample.api.UserInfoApi;
import com.nntk.restplus.sample.api.RespEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
public class MyController {


    @Resource
    private UserInfoApi userInfoApi;


    @GetMapping("/test")
    public Object test() {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sex", "男");
        List<UserInfo> data = userInfoApi.getList(1, 2, paramMap)
                .executeForResult();

        return data;
    }

    @GetMapping("/login1")
    public Object login1() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sex", "男");
        RespEntity respEntity = userInfoApi.login1(paramMap)
                .executeForResult();
        return respEntity;
    }

    @GetMapping("/login2")
    public Object login2() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sex", "男");
        userInfoApi.login2(paramMap).observe(new DefaultResultObserver() {
            @Override
            public void complete() {
                super.complete();
                log.info("=====my complete");
            }
        });
        return "success";
    }

    @GetMapping("/login3")
    public Object login3() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sex", "男");
        userInfoApi.login3(paramMap);
        return "success";
    }

    @GetMapping("/register")
    public Object register() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("name", "sss");

        Map<String, String> header = new HashMap<>();
        header.put("TenantId", "hello header");
        userInfoApi.register(paramMap, header);
        return "success";
    }

    @GetMapping("/upload")
    public Object upload() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("type", 1);
        FileSystemResource resource = new FileSystemResource(new File("C:\\Users\\hhm\\Downloads\\Capture001.png"));

        paramMap.put("file", resource);
        userInfoApi.upload(paramMap);
        return "success";
    }


    @GetMapping("/download")
    public Object download() {
        File file = userInfoApi.download("C:\\Users\\hhm\\Downloads\\11\\Capture0011111.png").executeForData();
        return "success:" + file.length();
    }
}
