package com.blog.utils;

/**
 * @author Timmy
 * threadLocal工去類
 */
public class ThreadLocalUtils {

    public static final ThreadLocal<Object> threadLocal = new ThreadLocal<>();

    public static<T> T get() {
        return (T) threadLocal.get();
    }

    public static void set(Object value) {
        threadLocal.set(value);
    }

    public static void remove() {
        threadLocal.remove();
    }
}
