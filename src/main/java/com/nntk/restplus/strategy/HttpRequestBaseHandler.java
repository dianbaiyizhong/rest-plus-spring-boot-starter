package com.nntk.restplus.strategy;


import com.nntk.restplus.annotation.*;
import com.nntk.restplus.entity.RestPlusResponse;
import com.nntk.restplus.intercept.RestPlusHandleIntercept;
import com.nntk.restplus.util.AnnotationUtil;
import com.nntk.restplus.util.SpringUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.CodeSignature;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.http.MediaType;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class HttpRequestBaseHandler {

    public void setRequestType(Class<? extends Annotation> requestType) {
        this.requestType = requestType;
    }

    private Class<? extends Annotation> requestType;

    public RestPlusResponse execute(ProceedingJoinPoint joinPoint, HttpExecuteContext httpExecuteContext) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class<?> clazz = method.getDeclaringClass();
        // 解析base url
        String baseUrl = AnnotationUtil.getValue(clazz, RestPlus.class, "baseUrl");


        boolean isFormData = Arrays.stream(method.getAnnotations()).anyMatch(annotation -> annotation.annotationType() == FormData.class);

        if (isFormData) {
            httpExecuteContext.setContentType(MediaType.MULTIPART_FORM_DATA_VALUE);
        } else {
            httpExecuteContext.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }


        String childUrl = AnnotationUtil.getAnnotationValue(method, requestType, "url");
        String url = baseUrl + childUrl;

        String[] paramNames = ((CodeSignature) joinPoint.getSignature()).getParameterNames();
        Object[] paramValues = joinPoint.getArgs();
        Map<String, Object> paramMap = new HashMap<>();
        for (int i = 0; i < paramNames.length; i++) {
            String paramName = paramNames[i];
            paramMap.put(paramName, paramValues[i]);
        }

        Parameter[] parameters = method.getParameters();
        Map<String, String> pathMap = new HashMap<>();
        for (Parameter parameter : parameters) {
            String value = AnnotationUtil.getAnnotationValue(parameter, Path.class, "value");
            if (value != null) {
                if (paramMap.containsKey(parameter.getName())) {
                    pathMap.put(value, paramMap.get(parameter.getName()).toString());
                    // 从paramMap删掉path类型参数，方便后面转换
                    paramMap.remove(parameter.getName());
                }
            }
        }

        Map<String, Object> requestBody = null;
        Map<String, String> headerMap = null;

        File filePath = null;
        for (Parameter parameter : parameters) {
            boolean body = AnnotationUtil.hasAnnotation(parameter, Body.class);
            boolean header = AnnotationUtil.hasAnnotation(parameter, Header.class);
            boolean isFilePath = AnnotationUtil.hasAnnotation(parameter, FilePath.class);
            if (body) {
                requestBody = (Map<String, Object>) paramMap.get(parameter.getName());
            }
            if (header) {
                headerMap = (Map<String, String>) paramMap.get(parameter.getName());
            }
            if (isFilePath) {
                if (paramMap.get(parameter.getName()) instanceof File) {
                    filePath = (File) paramMap.get(parameter.getName());
                } else {
                    filePath = new File((String) paramMap.get(parameter.getName()));
                }
            }
        }

        httpExecuteContext.setDownloadFilePath(filePath);
        String formatUrl = parseTemplate(url, pathMap);

        httpExecuteContext.setUrl(formatUrl);
        httpExecuteContext.setBodyMap(requestBody);
        httpExecuteContext.setHeaderMap(headerMap);
        // 拦截器模式
        Class<? extends RestPlusHandleIntercept>[] interceptList = AnnotationUtil.getObject(clazz, Intercept.class, "classType");

        if (interceptList != null) {
            for (Class<? extends RestPlusHandleIntercept> object : interceptList) {
                RestPlusHandleIntercept handleIntercept = SpringUtil.getBean(object);
                httpExecuteContext = handleIntercept.handle(httpExecuteContext);
            }
        }


        // 模板方法模式
        return executeHttp(httpExecuteContext);
    }


    public abstract RestPlusResponse executeHttp(HttpExecuteContext context);


    public static String parseTemplate(String template, Map properties) {
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
