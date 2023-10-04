package com.nntk.restplus.util;

import com.nntk.restplus.annotation.RestPlus;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;

public class AnnotationUtil {

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
            String value = AnnotationUtil.getAnnotationValue(i, RestPlus.class, name);
            if (StringUtils.hasLength(value)) {
                baseUrl = value;
                break;
            }
        }
        return baseUrl;
    }




}
