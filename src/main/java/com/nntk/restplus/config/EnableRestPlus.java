package com.nntk.restplus.config;


import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(RestPlusRegistrar.class)
public @interface EnableRestPlus {
    String[] value() default {};

    String[] basePackages() default {};
}
