package com.nntk.restplus.sample;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import com.nntk.restplus.sample.api.DefaultResultObserver;
import com.nntk.restplus.sample.api.RespEntity;
import com.nntk.restplus.sample.api.UserInfo;
import com.nntk.restplus.sample.api.UserInfoApi;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
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
        RespEntity respEntity = userInfoApi.login1(paramMap)
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
        userInfoApi.upload(paramMap).observe(new DefaultResultObserver() {
            @Override
            public void complete() {
                super.complete();
                log.info("上传完成，开始发送队列消息");
            }
        });
    }
}
