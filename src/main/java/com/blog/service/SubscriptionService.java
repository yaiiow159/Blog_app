package com.blog.service;

import com.blog.dto.PostDto;
import com.blog.dto.SubscriptionDto;
import com.blog.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface SubscriptionService {
    String subscribe(String username, Long postId, String authorName, String email) throws ResourceNotFoundException;

    String unSubscribe(String username, Long postId);
    List<SubscriptionDto> findByAuthorNameOrAuthorEmail(String authorName, String authorEmail);

    String checkSubscription(String username, Long postId);
}
