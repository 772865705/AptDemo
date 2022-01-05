package com.zy.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by ZhaoYue on 2022/1/2.
 */
@Target({ElementType.FIELD,ElementType.METHOD})
@Retention(RetentionPolicy.CLASS)
public @interface MyAnnotation {
    int value();
    String name();
}
