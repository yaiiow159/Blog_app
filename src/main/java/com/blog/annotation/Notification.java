package com.blog.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Inherited
@Retention(RetentionPolicy.RUNTIME)
public @interface Notification {
    Class<?> operatedClass();
    String operation() default "";
}