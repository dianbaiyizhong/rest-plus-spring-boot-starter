package com.nntk.restplus.annotation;

import com.nntk.restplus.RestTemplateHttpFactory;
import com.nntk.restplus.abs.AbsHttpFactory;
import com.nntk.restplus.abs.AbsBasicRespObserver;
import com.nntk.restplus.abs.AbsBodyHandleRule;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface RestPlus {
    String baseUrl() default "";

    Class<? extends AbsBodyHandleRule> respHandler() default AbsBodyHandleRule.class;

    Class<? extends AbsBasicRespObserver> observe() default AbsBasicRespObserver.class;

    Class<? extends AbsHttpFactory> httpFactory() default RestTemplateHttpFactory.class;

}
