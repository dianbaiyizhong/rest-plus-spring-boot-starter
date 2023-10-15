package com.nntk.restplus.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;
import java.util.Objects;

public class RestAnnotation {
    private Class<? extends Annotation> annotation;


    public Class<? extends Annotation> getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Class<? extends Annotation> annotation) {
        this.annotation = annotation;
        this.name = annotation.getName();
    }

    public Object getParameterValue() {
        return parameterValue;
    }

    public void setParameterValue(Object parameterValue) {
        this.parameterValue = parameterValue;
    }


    private Object parameterValue;

    public Parameter getParameter() {
        return parameter;
    }

    public void setParameter(Parameter parameter) {
        this.parameter = parameter;
    }

    private Parameter parameter;


    private String name;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestAnnotation that = (RestAnnotation) o;
        return index == that.index && Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, index);
    }

    public String getName() {
        return name;
    }


    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private int index;


    @Override
    public String toString() {
        return name + ":" + parameterValue;
    }
}
