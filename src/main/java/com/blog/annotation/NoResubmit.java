package com.blog.annotation;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;

/**
 * 防止重複提將 預設五秒
 */
@Inherited
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({java.lang.annotation.ElementType.METHOD})
public @interface NoResubmit {

    int delaySecond() default 5;
}
