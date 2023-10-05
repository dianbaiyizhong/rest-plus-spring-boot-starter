# rest-plus-spring-boot-starter

## 简介

rest-plus旨在打造一个http请求结果的统一处理优化库

## 现状痛点
服务中经常涉及到要调用第三方接口，例如阿里云Api，腾讯云Api，百度云Api，而每一个服务都会调用不同接口，例如获取用户信息，登录验证，上传图片

我们可以看下接口返回格式，可以有以下类型

1. code为0代表业务成功
```json
{
  "code": 0,
  "message": "success"
}
```

2. code为200代表业务成功
```json
{
  "code": 0,
  "message": "success"
}
```

3. statusCode为1代表业务成功
```json
{
  "statusCode": 0,
  "message": "success"
}
```
实属看得脑溢血，最要命的事，你在编码过程中，会有大量的请求方法中不得不包含以下这类代码
```java
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
```
看到没有，一堆try cache，一堆if判断，各种else

到这里，有些小伙伴会说，统一封装一下不就好了，其实说的也没毛病，不过假设有某个请求需要扩展，例如要在发生错误的时候，发送消息队列，这个时候，还得从封装中再封装？

> 综上所述，为了优化这个场景，这个rest-plus就出来了，我们接下来看下他如何能解决我的问题

## 快速开始

### 引入依赖

> 本软件主打一个纯净，不引入过多第三方依赖，包括guava，common-lang，fast-json等等常用库，完全基于spring原生组件开发

```xml

<dependency>
    <groupId>io.github.dianbaiyizhong</groupId>
    <artifactId>rest-plus-spring-boot-starter</artifactId>
    <version>1.0.1</version>
</dependency>
```

### springboot启动类加个注解不过分吧

```java

@EnableRestPlus
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
```
### 自定义“结果处理规则”
```java
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
```
通过继承AbsRespHandleRule类，自定义自己的规则，从代码不难看出，在这里你可以定义自己的规则，例如什么情况下，业务才是正常的（code=0）



### 自定义“结果观察者”类

```java

@Slf4j
@Component
public class DefaultResultObserver extends AbsBasicRespObserver {
    @Override
    public void callBusinessFail(int code, String messages) {
        log.info("callBusinessFail...");
    }

    @Override
    public void complete() {
        log.info("无论最终结果如何，请求已经完成了");
    }

    @Override
    public void callHttpFail(int httpStatus, String message) {
        log.info("http状态码不是200...{},{}", httpStatus, message);
    }

    @Override
    public void callUnknownException(Throwable throwable) {
        log.error("发生了未知错误，一般是状态码500的错误...", throwable);
    }

    @Override
    public void callHttpSuccess() {
        log.info("状态码200...");
    }

    @Override
    public void callBusinessSuccess() {
        log.info("业务请求成功了，一般是code为0...");

    }
}
```

这个类一般可以用来打印日志，不需要每一个http请求方法都要做重复的事情

### 定义api接口
```java
@RestPlus(
        baseUrl = "http://127.0.0.1:8080/api/user",
        respHandler = MyRespHandleRule.class,
        observe = DefaultResultObserver.class
)
public interface UserInfoApi {

    @GET(url = "/list/{id}")
    Call<List<UserInfo>> getList(@Path("id") Integer userId, @QueryParam("num") int num, @QueryMap Map<String, Object> map);

}

```
通过上面的定义可以看到，respHandler和observe都是方才已经定义好的，把baseUrl写一下，请求方式用@GET注解一下
> 定义方式有点像retrofit，其实请求方式我这里也是站在了巨人肩膀，模仿了一下，我比较喜欢他那种方式

### 注入使用
```java


        @Resource
        private UserInfoApi userInfoApi;

        List<UserInfo> userInfos = userInfoApi.getList(1, 10, paramMap)
                .observe(new DefaultResultObserver() {
                    @Override
                    public void callBusinessFail(int code, String messages) {
                        super.callBusinessFail(code, messages);
                        log.info("=====业务请求失败了，我要发送消息队列...todo");
                    }
                })
                .executeForData();

```

自定义观察者，通过“覆写”的方式来自定义，你还可以选择是否保留super.callBusinessFail来决定是否要执行默认的操作
```java
                    @Override
                    public void callBusinessFail(int code, String messages) {
                        super.callBusinessFail(code, messages);
                        log.info("=====业务请求失败了，我要发送消息队列...todo");
                    }
```

如果你还要选择更多的观察方式，你可以直接覆写完成
```java
           @Override
           public void callHttpSuccess() {
                  super.callHttpSuccess();
            }
```


你可以在本项目的sample工程中查看例子，我例举了4种场景，包括了上传文件和下载文件都有


### 自定义http请求方式
> 本项目因为不引入除了spring外的第三方库，所以用的是restTemplate完成http请求，你可以自定义实现自己的http客户端
通过继承AbsHttpFactory,下面是用hutool来实现http请求
```java
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

```
