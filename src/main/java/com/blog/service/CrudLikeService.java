package com.blog.service;

import java.util.List;

public interface CrudLikeService <T> extends BaseService<T> {
    void like(Long id);
    void cancelLike(Long id);
    Integer queryLikeCount(Long id);
}
