package com.nntk.restplus.annotation;

import com.nntk.restplus.intercept.RestPlusHandleIntercept;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Intercept {

    Class<? extends RestPlusHandleIntercept>[] classType() default {};

}