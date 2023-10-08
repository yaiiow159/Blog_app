package com.blog.service.impl;

import com.blog.dao.CommentPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.CommentPoMapper;
import com.blog.mapper.PostPoMapper;
import com.blog.po.CommentPo;
import com.blog.po.PostPo;
import com.blog.service.CommentService;
import com.blog.utils.LoginUtils;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Resource
    private PostPoRepository postPoRepository;
    @Resource
    private CommentPoRepository commentPoRepository;
    @Override
    public CommentDto createComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
        commentDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentDto.setCreatUser(LoginUtils.getCurrentUser());
        commentDto.setPost(PostPoMapper.INSTANCE.toDto(postPo));
        return CommentPoMapper.INSTANCE.toDto(commentPoRepository.save(CommentPoMapper.INSTANCE.toPo(commentDto)));
    }

    @Override
    public CommentDto findComment(Long postId,Long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
        CommentPo commentPo = postPo.getComments().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() -> new ResourceNotFoundException());
        return CommentPoMapper.INSTANCE.toDto(commentPo);
    }

    @Override
    public CommentDto updateComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
        CommentPo commentPo = postPo.getComments().stream().filter(c -> c.getName().equals(commentDto.getName())).findFirst().orElseThrow(() -> new ResourceNotFoundException());
        commentDto.setUpdateUser(LoginUtils.getCurrentUser());
        commentDto.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentDto.setPost(PostPoMapper.INSTANCE.toDto(postPo));
        commentPo = CommentPoMapper.INSTANCE.partialUpdate(commentDto, commentPo);
        return CommentPoMapper.INSTANCE.toDto(commentPoRepository.save(commentPo));
    }

    @Override
    public String deleteComment(Long id) throws ResourceNotFoundException {
        CommentPo commentPo = commentPoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        commentPo.setIsDeleted(true);
        commentPo = commentPoRepository.save(commentPo);
        if(commentPo.getIsDeleted())
            return "success";

        return "fail";
    }

    @Override
    public Page<CommentDto> findAllComments(Long postId,int page,int size, String sort) throws ResourceNotFoundException {
        postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
        List<CommentPo> commentPoList = commentPoRepository.findAllByPostId(postId);
        if(CollectionUtils.isEmpty(commentPoList))
            return null;
        return new PageImpl<>(CommentPoMapper.INSTANCE.toDtoList(commentPoList), PageRequest.of(page,size, Sort.by(sort)), commentPoList.size());
    }
}
