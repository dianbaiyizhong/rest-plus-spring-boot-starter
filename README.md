# rest-plus-spring-boot-starter



<a target="_blank" href="https://search.maven.org/artifact/io.github.dianbaiyizhong/rest-plus-spring-boot-starter">
		<img src="https://img.shields.io/maven-central/v/io.github.dianbaiyizhong/rest-plus-spring-boot-starter.svg?label=Maven%20Central" /></a>

<a target="_blank" href="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">
		<img src="https://img.shields.io/badge/JDK-8+-green.svg" />
</a>



## 简介

rest-plus旨在打造一个***简易的http请求方式***和***对请求结果的灵活处理***优化库

## 痛点解决
#### 处理不同接口的返回结果格式差异

有些服务业务状态码不一样，例如code，statusCode等



#### 统一每一个请求的处理过程

你在编码过程中，会有大量的请求方法中不得不包含以下这类代码
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
                    log.info("====到这一步总算拿到了想要的结果:{}", userInfos);
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
在以上代码中包含着对整个请求过程的异常捕获（try catch），http status的判断，以及对业务状态码（code）的判断



#### 灵活适配每一个接口处理逻辑的差异

针对不同的http请求结果处理，可以通过重写方式，实现不同的逻辑







> 综上所述，为了优化这个场景，这个rest-plus就出来了，我们接下来看下他如何能解决这些问题

## 快速开始

#### 引入依赖

> 本软件主打一个纯净，不引入任何第三方依赖，例如guava，common-lang，fast-json等等常用库，完全基于spring原生组件开发

```xml

<dependency>
    <groupId>io.github.dianbaiyizhong</groupId>
    <artifactId>rest-plus-spring-boot-starter</artifactId>
    <version>1.x</version>
</dependency>
```
springboot3.x
```xml
<dependency>
    <groupId>io.github.dianbaiyizhong</groupId>
    <artifactId>rest-plus-spring-boot-starter</artifactId>
    <version>3.x</version>
</dependency>
```


#### springboot启动类加个注解不过分吧

```java
@SpringBootApplication
@EnableRestPlus // 重要注解，有了它才能启用功能
public class SampleApplication {
    public static void main(String[] args) {
        SpringApplication.run(SampleApplication.class, args);
    }
}
```
#### 自定义“结果处理规则”

> 采用适配器模式，为每一种服务设定好自己的处理规则

```java
@Component
public class MyResponseHandleRule extends AbsResponseHandleRule {

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


#### 自定义“结果观察者”类

> 采用观察者模式，为每一类接口设置不同的处理方式，例如打印日志，抛异常等

```java
@Slf4j
@Component
public class MyResultObserver extends AbsBasicRespObserver {
    @Override
    public void beforeRequest() {
        log.info("====你可以获取到当前的class:{}和方法:{}", getRequestClass(), getMethodName());
        log.info("请求之前的操作:{}", getHttpExecuteContext().getUrl());
    }

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



#### 定义api接口
```java
@RestPlus(
        baseUrl = "http://127.0.0.1:8080/api/user",
        responseHandler = MyResponseHandleRule.class,
        observe = MyResultObserver.class
)
public interface UserInfoApi {

    @GET(url = "/list/{id}")
    Call<List<UserInfo>> getList(@Path("id") Integer userId, @QueryParam("num") int num, @QueryMap Map<String, Object> map);

}

```
通过上面的定义可以看到，responseHandler和observe都是方才已经定义好的，把baseUrl写一下，请求方式用@GET注解一下
> 接口定义方式借鉴了retrofit，个人比较喜欢他那种方式

#### 注入使用
```java
@Resource
private UserInfoApi userInfoApi;
        List<UserInfo> userInfos = userInfoApi.getList(1, 10, paramMap).executeForData();
```

自定义观察者，通过“覆写”的方式来自定义，你还可以选择是否保留super.callBusinessFail来决定是否要执行默认的操作
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
        }).executeForData();
```

如果你还要选择更多的观察方式，你可以直接从不同的切入点进行覆写
```java
@Override
public void callHttpSuccess() {
        super.callHttpSuccess();
        }
```


你可以在本项目的sample工程中查看例子，我例举了4种场景，包括了上传文件和下载文件都有


#### 自定义http请求方式
> 本项目因为不引入除了spring外的第三方库，所以用的是restTemplate完成http请求，采用工厂模式，你可以自定义实现自己的http客户端产品
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

#### 自定义拦截器

> 采用责任链模式，可配置多个拦截器，一般可以用来配置header的token请求

```java
@Component
@Slf4j
public class TokenIntercept implements RestPlusHandleIntercept {
    @Override
    public HttpExecuteContext handle(HttpExecuteContext context) {
        log.info("token拦截器，你可以在这里验证，并把token塞入到参数里");
        Map<String,String> header = new HashMap<>();
        header.put("token", "=====");
        context.setHeaderMap(header);
        return context;
    }
}
```

```java
@RestPlus(
        baseUrl = "http://127.0.0.1:8080/api/user",
        responseHandler = MyResponseHandleRule.class,
        observe = MyResultObserver.class
)
@Intercept(classType = {LogIntercept.class, TokenIntercept.class})
public interface UserInfoApi extends BaseApi {
```

