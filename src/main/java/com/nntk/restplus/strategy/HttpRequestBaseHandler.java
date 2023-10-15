package com.nntk.restplus.strategy;


import com.nntk.restplus.abs.AbsBasicRespObserver;
import com.nntk.restplus.annotation.*;
import com.nntk.restplus.entity.RestPlusResponse;
import com.nntk.restplus.intercept.RestPlusHandleIntercept;
import com.nntk.restplus.util.AnnotationUtil;
import com.nntk.restplus.util.RestAnnotation;
import com.nntk.restplus.util.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.MediaType;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HttpRequestBaseHandler {

    public void setRequestType(Class<? extends Annotation> requestType) {
        this.requestType = requestType;
    }

    private Class<? extends Annotation> requestType;

    public RestPlusResponse execute(ProceedingJoinPoint joinPoint, HttpExecuteContext httpExecuteContext, AbsBasicRespObserver observer) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();

        Class<?> clazz = method.getDeclaringClass();
        // 解析base url
        String baseUrl = AnnotationUtil.getValue(clazz, RestPlus.class, "baseUrl");

        List<RestAnnotation> methodParameter = AnnotationUtil.getMethodParameter(joinPoint);

        boolean isFormData = Arrays.stream(method.getAnnotations()).anyMatch(annotation -> annotation.annotationType() == FormData.class);

        if (isFormData) {
            httpExecuteContext.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        } else {
            httpExecuteContext.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }


        String childUrl = AnnotationUtil.getAnnotationValue(method, requestType, "url");
        String url = baseUrl + childUrl;


        Map<String, String> pathMap = new HashMap<>();
        Map<String, Object> requestBody = new HashMap<>();
        Map<String, String> headerMap = new HashMap<>();
        File filePath = null;

        for (RestAnnotation restAnnotation : methodParameter) {
            if (restAnnotation.getAnnotation() == Body.class) {
                if (restAnnotation.getParameterValue() != null) {
                    requestBody.putAll((Map<String, Object>) restAnnotation.getParameterValue());
                }
            }
            if (restAnnotation.getAnnotation() == Header.class) {
                headerMap.putAll((Map<String, String>) restAnnotation.getParameterValue());
            }
            if (restAnnotation.getAnnotation() == FilePath.class) {
                if (restAnnotation.getParameterValue() instanceof File) {
                    filePath = (File) restAnnotation.getParameterValue();
                } else {
                    filePath = new File((String) restAnnotation.getParameterValue());
                }
            }
            if (restAnnotation.getAnnotation() == Path.class) {
                String value = AnnotationUtil.getAnnotationValue(restAnnotation.getParameter(), Path.class, "value");
                pathMap.put(value, restAnnotation.getParameterValue() + "");
            }
        }


        httpExecuteContext.setDownloadFilePath(filePath);
        String formatUrl = parseTemplate(url, pathMap);

        httpExecuteContext.setUrl(formatUrl);
        httpExecuteContext.setBodyMap(requestBody);
        httpExecuteContext.setHeaderMap(headerMap);
        // 责任链模式
        Class<? extends RestPlusHandleIntercept>[] interceptList = AnnotationUtil.getObject(clazz, Intercept.class, "classType");

        if (interceptList != null) {
            for (Class<? extends RestPlusHandleIntercept> object : interceptList) {
                RestPlusHandleIntercept handleIntercept = SpringUtil.getBean(object);
                httpExecuteContext = handleIntercept.handle(httpExecuteContext);
            }
        }

        // 观察者模式，发送消息
        observer.beforeRequest();
        // 模板方法模式
        return executeHttp(httpExecuteContext);
    }


    public abstract RestPlusResponse executeHttp(HttpExecuteContext context);


    private static String parseTemplate(String template, Map<String, String> properties) {
        if (template == null || template.isEmpty() || properties == null) {
            return template;
        }
        String r = "\\{([^\\}]+)\\}";
        Pattern pattern = Pattern.compile(r);
        Matcher matcher = pattern.matcher(template);
        while (matcher.find()) {
            String group = matcher.group();
            Object o = properties.get(group.replaceAll(r, "$1"));
            if (o != null) {
                template = template.replace(group, String.valueOf(o));
            } else {
                template = template.replace(group, "");
            }
        }
        return template;
    }

}
