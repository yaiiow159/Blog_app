package com.blog.listener;

import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;

public class RemovalCustomerListener implements RemovalListener<String, String> {
    @Override
    public void onRemoval(RemovalNotification<String, String> removalNotification) {
        System.out.println("主鍵 = " + removalNotification.getKey() +
                "  值為 = " + removalNotification.getValue() +
                "  移除原因 = " + removalNotification.getCause());
    }
}