package com.nntk.restplus.sample;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.nntk.restplus.sample.api.MyResultObserver;
import com.nntk.restplus.sample.api.RespEntity;
import com.nntk.restplus.sample.api.UserInfo;
import com.nntk.restplus.sample.api.UserInfoApi;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest(classes = SampleApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@Slf4j
public class RestTest {

    @Resource
    private UserInfoApi userInfoApi;


    /**
     * 下载
     */
    @Test
    public void download() {
        String tmpDir = FileUtil.getTmpDirPath();
        tmpDir = tmpDir + "test.png";
        log.info("下载路径:{}", tmpDir);
        File file = userInfoApi.download(tmpDir).executeForResult();
        log.info("下载成功，文件大小:{}", file.length());

        Assert.assertEquals(50734, file.length());
    }

    /**
     * 最常见的一种请求方式，一般用于注册，登录等post逻辑
     */
    @Test
    public void postJson() {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sex", "男");
        Map<String, Object> paramMap2 = new HashMap<>();
        paramMap2.put("class", "三年二班");
        RespEntity respEntity = userInfoApi.login1(paramMap, paramMap2)
                .executeForResult();
        System.out.println(respEntity);

        Assert.assertEquals(respEntity.getCode(), 0);

    }


    /**
     * 最常见的一种请求方式，一般用于获取列表等get方式
     */
    @Test
    public void getJson() {

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sex", "男");


        List<UserInfo> userInfos = userInfoApi.getList(1, 10, paramMap)
                .observe(new MyResultObserver() {
                    @Override
                    public void callBusinessFail(int code, String messages) {
                        super.callBusinessFail(code, messages);
                        log.info("=====业务请求失败了，我要发送消息队列...todo");
                    }

                    @Override
                    public void callHttpSuccess() {
                        super.callHttpSuccess();
                    }
                })
                .executeForData();
        Assert.assertEquals(userInfos.size(), 2);

    }


    @Test
    public void upload() {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("type", 1);
        String tmpDir = FileUtil.getTmpDirPath();
        tmpDir = tmpDir + "test.png";
        File file = FileUtil.writeFromStream(ResourceUtil.getStream("image.webp"), new File(tmpDir));
        paramMap.put("file", file);
        userInfoApi.upload(paramMap).observe(new MyResultObserver() {
            @Override
            public void complete() {
                super.complete();
                log.info("上传完成，开始发送队列消息");
            }
        });
    }


    @Test
    public void notBadExample() {

        HttpRequest httpRequest = HttpUtil.createGet("http://127.0.0.1:8080/api/user/list/1");

        try {
            HttpResponse response = httpRequest.execute();
            if (response.getStatus() == 200) {
                String body = response.body();
                JSONObject jsonObject = JSON.parseObject(body);
                if (jsonObject.getInteger("code") == 0) {
                    String data = jsonObject.getString("data");
                    List<UserInfo> userInfos = JSON.parseArray(data, UserInfo.class);
                    log.info("====到这一部总算拿到了想要的结果:{}", userInfos);
                } else {
                    log.error("code不为0");
                }
            } else {
                log.error("http请求异常");
            }
        } catch (Exception e) {
            log.error("遇到了未知异常");
        }


    }

}
