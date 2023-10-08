package com.blog.service.impl;

import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.CategoryPoMapper;
import com.blog.mapper.PostPoMapper;
import com.blog.po.CategoryPo;
import com.blog.po.PostPo;
import com.blog.service.PostService;
import com.blog.utils.LoginUtils;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.persistence.criteria.Predicate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {
    @Resource
    private CategoryPoRepository categoryPoRepository;
    @Resource
    private PostPoRepository postPoRepository;

    @Override
    public PostDto createPost(Long categoryId,PostDto postDto) throws ResourceNotFoundException {
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException());
        postDto.setCreatUser(LoginUtils.getCurrentUser());
        postDto.setCategory(CategoryPoMapper.INSTANCE.toDto(categoryPo));
        postDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        return PostPoMapper.INSTANCE.toDto(
                postPoRepository.save(PostPoMapper.INSTANCE.toPo(postDto)));
    }

    @Override
    public PostDto updatePost(Long categoryId,PostDto postDto) throws ResourceNotFoundException {
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException());
        PostPo postPo = postPoRepository.findById(postDto.getId()).orElse(null);
        if (postPo == null) {
            throw new ResourceNotFoundException();
        }
        //更新
        postDto.setCategory(CategoryPoMapper.INSTANCE.toDto(categoryPo));
        postDto.setUpdateUser(LoginUtils.getCurrentUser());
        postDto.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        postPoRepository.save(PostPoMapper.INSTANCE.toPo(postDto));
        return PostPoMapper.INSTANCE.toDto(postPo);
    }

    @Override
    public List<PostDto> getAllPosts() {
        return PostPoMapper.INSTANCE.toDtoList(postPoRepository.findByIsDeletedFalse());
    }

    @Override
    public PostDto getOnePost(long id) {
        return PostPoMapper.INSTANCE.toDto(
                postPoRepository.findById(id).orElse(null));
    }

    @Override
    public Page<PostDto> getAllPosts(String title,String content,String description,int page, int size, String sort) {
        Specification<PostPo> spec = (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = Arrays.asList(
                    criteriaBuilder.like(root.get("title"), "%" + title + "%"),
                    criteriaBuilder.like(root.get("content"), "%" + content + "%"),
                    criteriaBuilder.like(root.get("description"), "%" + description + "%")
            );
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page, size,Sort.by(sort));
        return postPoRepository.findAll(pageable).map(PostPoMapper.INSTANCE::toDto);
    }

    @Override
    public String deletePost(long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException());
        postPo.setIsDeleted(true);
        postPoRepository.save(postPo);
        postPo = postPoRepository.findByIdAndIsDeletedFalse(id);
        if(postPo != null){
            return "刪除成功";
        }
        return "刪除失敗";
    }

    @Override
    public Page<PostDto> findPosts(Long id) throws ResourceNotFoundException {
        Optional<CategoryPo> optional = categoryPoRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        CategoryPo categoryPo = optional.get();
        Pageable pageable = PageRequest.of(0, 10,Sort.by("createDate"));
        return new PageImpl<>(PostPoMapper.INSTANCE.toDtoList(categoryPo.getPosts()), pageable, categoryPo.getPosts().size());
    }

    @Override
    public PostDto findTheOnePostsByCategory(Long id, Long postId) throws ResourceNotFoundException {
        //先找出分類
        Optional<CategoryPo> optional = categoryPoRepository.findById(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException();
        }
        CategoryPo categoryPo = optional.get();
        //再找出文章
        List<PostPo> posts = categoryPo.getPosts();
        for (PostPo postPo : posts) {
            if (Objects.equals(postId, postPo.getId())) {
                return PostPoMapper.INSTANCE.toDto(postPo);
            } else {
                throw new ResourceNotFoundException();
            }
        }
        return null;
    }

}
