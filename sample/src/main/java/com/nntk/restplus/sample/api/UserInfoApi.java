package com.nntk.restplus.sample.api;


import com.nntk.restplus.annotation.*;
import com.nntk.restplus.returntype.Call;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestPlus(
        baseUrl = "http://127.0.0.1:8080/api/user",
        respHandler = MyRespHandleRule.class,
        observe = DefaultResultObserver.class
)
@Intercept(classType = {LogIntercept.class, TokenIntercept.class})
public interface UserInfoApi extends BaseApi {

    @GET(url = "/list/{id}")
    Call<List<UserInfo>> getList(@Path("id") Integer userId, @QueryParam("num") int num, @QueryMap Map<String, Object> map);


    @POST(url = "/login")
    Call<RespEntity> login1(@Body Map<String, Object> map);


    @POST(url = "/login")
    Call<?> login2(@Body Map<String, Object> map);


    @POST(url = "/login")
    void login3(@Body Map<String, Object> map);


    @FormData
    @POST(url = "/register")
    void register(@Body Map<String, Object> map, @Header Map<String, String> headerMap);

    @FormData
    @POST(url = "/upload")
    Call<?> upload(@Body Map<String, Object> map);

    @Download
    @GET(url = "/download")
    Call<File> download(@FilePath String filePath);


}
