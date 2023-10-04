package com.nntk.restplus.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TypeUtil {

    public static ParameterizedType toParameterizedType(Type type) {

        return (ParameterizedType) type;
    }
}
