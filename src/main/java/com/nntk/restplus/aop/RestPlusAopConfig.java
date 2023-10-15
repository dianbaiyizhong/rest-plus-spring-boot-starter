package com.nntk.restplus.aop;

import com.nntk.restplus.abs.AbsBasicRespObserver;
import com.nntk.restplus.abs.AbsRespHandleRule;
import com.nntk.restplus.abs.AbsHttpFactory;
import com.nntk.restplus.annotation.Download;
import com.nntk.restplus.annotation.RestPlus;
import com.nntk.restplus.entity.RestPlusResponse;
import com.nntk.restplus.returntype.Call;
import com.nntk.restplus.returntype.RestPlusVoid;
import com.nntk.restplus.strategy.HttpExecuteContext;
import com.nntk.restplus.strategy.HttpRequestBaseHandler;
import com.nntk.restplus.strategy.HttpRequestSelector;
import com.nntk.restplus.util.AnnotationUtil;
import com.nntk.restplus.util.HttpRespObserver;
import com.nntk.restplus.util.SpringUtil;
import com.nntk.restplus.util.TypeUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.WildcardType;
import java.util.Arrays;


@Component
@Aspect
public class RestPlusAopConfig {

    private static final Logger log = LoggerFactory.getLogger(RestPlusAopConfig.class);

    @Resource
    private HttpRequestSelector httpRequestSelector;

    @Pointcut("execution(@com.nntk.restplus.annotation.* * *(..))")
    public void execute() {


    }

    @Around("execute()")
    public Object interceptAnnotation(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();


        Class<?> clazz = method.getDeclaringClass();


        // 获取定义的结果判断逻辑和处理逻辑
        Class<AbsRespHandleRule> respHandlerClass = AnnotationUtil.getAnnotationValue(clazz, RestPlus.class, "respHandler");
        Class<AbsBasicRespObserver> observerClass = AnnotationUtil.getAnnotationValue(clazz, RestPlus.class, "observe");


        AbsRespHandleRule handler = SpringUtil.getBean(respHandlerClass);
        AbsBasicRespObserver observer = SpringUtil.getBean(observerClass);
        observer.setMethodName(method.getName());
        observer.setRequestClass(method.getDeclaringClass());

        HttpExecuteContext context = new HttpExecuteContext();
        observer.setHttpExecuteContext(context);

        // 工厂模式：获取http 工厂类
        Class<AbsHttpFactory> httpFactoryClass = AnnotationUtil.getObject(clazz, RestPlus.class, "httpFactory");
        AbsHttpFactory httpFactory = SpringUtil.getBean(httpFactoryClass);
        if (httpFactoryClass == AbsHttpFactory.class) {
            throw new RuntimeException("you must extends AbsHttpFactory...");
        } else {
            context.setHttpFactory(httpFactory);
        }

        Annotation requestTypeAnnotation = Arrays.stream(method.getAnnotations()).filter(annotationValue -> httpRequestSelector.isRequestType(annotationValue.annotationType())).findAny().get();

        // 是否包含[下载]注解
        boolean isDownload = AnnotationUtil.hasAnnotation(method, Download.class);
        context.setDownload(isDownload);
        HttpRequestBaseHandler select = httpRequestSelector.select(requestTypeAnnotation.annotationType());


        // 获取返回类型
        Type genericReturnType = method.getGenericReturnType();
        if (genericReturnType == Void.class) {
            RestPlusVoid vo = new RestPlusVoid();
            try {
                RestPlusResponse response = select.execute(joinPoint, context, observer);
                vo.setHttpStatus(response.getHttpStatus());
                handler.setHttpBody(response.getBody());
            } catch (Exception e) {
                vo.setThrowable(e);
            } finally {
                vo.setRespBodyHandleRule(handler);

            }
            // 自动触发观察
            HttpRespObserver.observe(observer, vo.getThrowable(), vo.getHttpStatus(), vo.getRespBodyHandleRule(), false);

            return vo;
        } else {
            Call<Object> call = new Call<>();
            try {
                RestPlusResponse response = select.execute(joinPoint, context, observer);
                Type type = method.getGenericReturnType();
                Type typeArgument = TypeUtil.toParameterizedType(type).getActualTypeArguments()[0];
                if (typeArgument instanceof Class) {
                    call.setReturnType(typeArgument);
                } else if (typeArgument instanceof WildcardType) {
                } else {
                    call.setReturnType(((ParameterizedType) typeArgument).getRawType());
                }
                call.setHttpStatus(response.getHttpStatus());
                handler.setHttpBody(response.getBody());
                call.setBodyStream(response.getBodyStream());
                call.setDownloadFile(context.getDownloadFilePath());
            } catch (Exception e) {
                call.setThrowable(e);
            } finally {
                call.setRespBodyHandleRule(handler);
                call.setConfigObserver(observer);
                call.setHttpFactory(httpFactory);
            }

            return call;
        }


    }


}
