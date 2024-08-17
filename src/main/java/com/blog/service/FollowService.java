package com.blog.service;

public interface FollowService {
    /**
     * 檢查該文章作者是否已被關注
     */
    boolean checkIsFollowed(Long followeeId);

    /**
     * 關注文章作者
     */
    void follow(Long followeeId);
}
