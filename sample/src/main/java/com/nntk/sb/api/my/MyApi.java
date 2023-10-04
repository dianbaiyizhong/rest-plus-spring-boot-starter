package com.nntk.sb.api.my;


import com.nntk.restplus.annotation.*;
import com.nntk.restplus.returntype.Call;

import java.io.File;
import java.util.List;
import java.util.Map;

@RestPlus(
        baseUrl = "http://localhost:8080",
        respHandler = MyAbsBodyHandleRule.class
)
@Intercept(classType = {MyIntercept.class, MyIntercept2.class})
public interface MyApi extends MyBaseApi {

    @GET(url = "/api/list/{id}")
    Call<List<UserInfo>> getList(@Path("id") Integer userName, @QueryParam("num") int num, @QueryMap Map<String, Object> map);


    @POST(url = "/api/login")
    Call<MyBodyEntity> login1(@Body Map<String, Object> map);


    @POST(url = "/api/login")
    Call<?> login2(@Body Map<String, Object> map);


    @POST(url = "/api/login")
    void login3(@Body Map<String, Object> map);


    @FormData
    @POST(url = "/api/register")
    void register(@Body Map<String, Object> map, @Header Map<String, String> headerMap);

    @FormData
    @POST(url = "/api/upload")
    void upload(@Body Map<String, Object> map);

    @Download
    @GET(url = "/api/download")
    Call<File> download(@FilePath String filePath);


}
