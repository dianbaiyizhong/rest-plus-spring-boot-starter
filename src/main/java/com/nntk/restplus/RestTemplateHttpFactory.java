package com.nntk.restplus;

import com.nntk.restplus.abs.AbsHttpFactory;
import com.nntk.restplus.entity.RestPlusResponse;
import com.nntk.restplus.strategy.HttpExecuteContext;
import com.nntk.restplus.util.JacksonUtil;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RestTemplateHttpFactory extends AbsHttpFactory {

    @Override
    public String toJsonString(Object object) {
        return JacksonUtil.objectToJson(object);
    }

    @Override
    public <T> T parseObject(String json, Type type) {
        return JacksonUtil.jsonToObject(json, (Class) type);
    }

    @Override
    public RestPlusResponse post(HttpExecuteContext context) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(context.getContentType()));

        if (context.getHeaderMap() != null) {
            headers.setAll(context.getHeaderMap());
        }
        ResponseEntity<String> responseEntity = null;

        if (context.getContentType().equals(MediaType.MULTIPART_FORM_DATA_VALUE)) {
            HttpEntity<MultiValueMap<String, Object>> entity = new HttpEntity<>(map2MultiValueMap(context.getBodyMap()), headers);
            responseEntity = restTemplate.postForEntity(context.getUrl(), entity, String.class);

        } else {
            HttpEntity<String> entity = new HttpEntity<>(context.getHttpFactory().toJsonString(context.getBodyMap()), headers);
            responseEntity = restTemplate.postForEntity(context.getUrl(), entity, String.class);
        }
        RestPlusResponse restPlusResponse = new RestPlusResponse();
        restPlusResponse.setHttpStatus(responseEntity.getStatusCodeValue());
        restPlusResponse.setBody(responseEntity.getBody());
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
        RestTemplate restTemplate = new RestTemplate();

        RestPlusResponse restPlusResponse = new RestPlusResponse();

        if (context.isDownload()) {
            ResponseEntity<byte[]> responseEntity = restTemplate.exchange(context.getUrl()
                    , HttpMethod.GET, null,
                    byte[].class, new HashMap<>());
            restPlusResponse.setHttpStatus(responseEntity.getStatusCodeValue());
            restPlusResponse.setBodyStream(responseEntity.getBody());
        } else {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(context.getUrl(), String.class);
            restPlusResponse.setHttpStatus(responseEntity.getStatusCodeValue());
            restPlusResponse.setBody(responseEntity.getBody());
        }
        return restPlusResponse;
    }


    private static MultiValueMap<String, Object> map2MultiValueMap(Map<String, Object> params) {
        MultiValueMap<String, Object> multiValueMap = new LinkedMultiValueMap<>();
        for (Map.Entry<String, Object> entry : params.entrySet()) {

            List<Object> list = new ArrayList<>();
            list.add(entry.getValue());
            multiValueMap.put(entry.getKey(), list);

        }
        return multiValueMap;
    }

}
