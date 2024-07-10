package com.blog.service;

import com.blog.exception.ResourceNotFoundException;
import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface BaseService <T> {
    void save(T t) throws Exception;

    void update(T t) throws Exception;

    void delete(T t) throws Exception;

    void delete(Long id) throws Exception;

    T findById(Long id) throws EntityNotFoundException, Exception;

    List<T> findAll() throws Exception;

    Page<T> findAll(Integer page, Integer pageSize) throws Exception;

}
