package com.nntk.restplus.util;

import com.nntk.restplus.annotation.RestPlus;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

public class AnnotationUtil {


    public static List<RestAnnotation> getMethodParameter(ProceedingJoinPoint joinPoint) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        List<RestAnnotation> result = new ArrayList<>();
        Parameter[] paramNames = method.getParameters();
        Object[] paramValues = joinPoint.getArgs();
        if (paramValues.length != paramNames.length) {
            throw new RuntimeException("parameters size is not equals...");
        }

        for (int i = 0; i < paramNames.length; i++) {
            Parameter parameter = paramNames[i];
            if (parameter.getAnnotations().length == 0) {
                continue;
            }
            Class<? extends Annotation> aClass = parameter.getAnnotations()[0].annotationType();
            RestAnnotation restAnnotation = new RestAnnotation();
            restAnnotation.setAnnotation(aClass);
            restAnnotation.setParameterValue(paramValues[i]);
            restAnnotation.setIndex(i);
            restAnnotation.setParameter(parameter);
            result.add(restAnnotation);
        }

        return result;
    }


    @SuppressWarnings("unchecked")
    public static <T> T getAnnotationValue(AnnotatedElement annotationEle, Class<? extends Annotation> annotationType, String propertyName) {
        Annotation annotation = AnnotationUtils.getAnnotation(annotationEle, annotationType);
        if (null == annotation) {
            return null;
        } else {
            Method method = ReflectionUtils.findMethod(annotation.annotationType(), propertyName);
            return null == method ? null : (T) ReflectionUtils.invokeMethod(method, annotation, new Object[0]);
        }
    }


    public static boolean hasAnnotation(AnnotatedElement annotatedElement, Class<? extends Annotation> annotationType) {
        return AnnotationUtils.getAnnotation(annotatedElement, annotationType) != null;
    }


    public static <T> T getObject(Class<?> clazz, Class<? extends Annotation> annotation, String name) {

        T tClass = AnnotationUtil.getAnnotationValue(clazz, annotation, name);

        Class<?>[] is = clazz.getInterfaces();
        // 遍历继承关系，获取到对应的值
        for (Class<?> i : is) {
            T value = AnnotationUtil.getAnnotationValue(i, annotation, name);
            if (value != null) {
                tClass = value;
                break;
            }
        }
        return tClass;

    }


    public static String getValue(Class<?> clazz, Class<? extends Annotation> annotation, String name) {

        String baseUrl = AnnotationUtil.getAnnotationValue(clazz, annotation, name);
        Class<?>[] is = clazz.getInterfaces();
        // 遍历继承关系，获取到对应的值
        for (Class<?> i : is) {
            String value = AnnotationUtil.getAnnotationValue(i, annotation, name);
            if (StringUtils.hasLength(value)) {
                baseUrl = value;
                break;
            }
        }
        return baseUrl;
    }


}
