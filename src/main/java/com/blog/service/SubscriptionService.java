package com.blog.service;

import com.blog.dto.PostDto;
import com.blog.dto.SubscriptionDto;
import com.blog.exception.ResourceNotFoundException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubscriptionService {
    void subscribe(String username, Long postId, String authorName, String email) throws  Exception;

    void unSubscribe(String username, Long postId) throws Exception;
    List<SubscriptionDto> findByAuthorNameOrAuthorEmail(String authorName, String authorEmail);

    boolean checkSubscription(String username, Long postId);
}
