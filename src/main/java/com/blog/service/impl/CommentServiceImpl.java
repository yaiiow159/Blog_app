package com.blog.service.impl;

import com.blog.annotation.SendMail;
import com.blog.dao.CommentPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dao.UserReportPoRepository;
import com.blog.dto.CommentDto;
import com.blog.enumClass.CommentReport;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.CommentPoMapper;
import com.blog.po.CommentPo;
import com.blog.po.PostPo;
import com.blog.po.UserPo;
import com.blog.po.UserReportPo;
import com.blog.service.CommentService;
import com.blog.utils.SpringSecurityUtils;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class CommentServiceImpl implements CommentService {
    private static final int MAX_REPORT_NUM = 5;
    @Resource
    private PostPoRepository postPoRepository;
    @Resource
    private CommentPoRepository commentPoRepository;
    @Resource
    private UserPoRepository userJpaRepository;
    @Resource
    private UserReportPoRepository userReportPoRepository;
    @Resource
    private JavaMailSender javaMailSender;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    // 每當有人創建新評論，通知該作者 該文章有新評論了
    @Override
    @SendMail(type = "comment",operation = "add")
    public void add(Long postId, CommentDto commentDto) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該文章"));
        // 使用NAME 查詢 使用者
        UserPo userPo = userJpaRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new ResourceNotFoundException("找不到該用戶"));
        CommentPo commentPo = CommentPoMapper.INSTANCE.toPo(commentDto);
        commentPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentPo.setCreatUser(SpringSecurityUtils.getCurrentUser());
        commentPo.setIsReport(CommentReport.NOT_REPORTED.getStatus());
        commentPo.setPost(postPo);
        commentPo.setUser(userPo);
        commentPoRepository.saveAndFlush(commentPo);
    }

    @Override
    public CommentDto findComment(Long postId,Long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
        CommentPo commentPo = postPo.getComments().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
        return CommentPoMapper.INSTANCE.toDto(commentPo);
    }

    @Override
    @SendMail(type = "comment",operation = "edit")
    public void edit(Long postId, Long id, CommentDto commentDto) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("找不到該文章"));
        CommentPo commentPo = commentPoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
        commentPo = CommentPoMapper.INSTANCE.partialUpdate(commentDto, commentPo);
        commentPo.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentPo.setUpdateUser(SpringSecurityUtils.getCurrentUser());
        commentPo.setPost(postPo);
        commentPoRepository.saveAndFlush(commentPo);
    }

    @Override
    public void delete(Long postId, Long id) throws ResourceNotFoundException {
        commentPoRepository.deleteById(id);
    }

    @Override
    public List<CommentDto> findAllComments(Long postId) throws ResourceNotFoundException {
        postPoRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該文章"));
        List<CommentPo> commentPoList = commentPoRepository.findAllByPostId(postId);
        if(CollectionUtils.isEmpty(commentPoList))
            return null;
        return CommentPoMapper.INSTANCE.toDtoList(commentPoList);
    }

    @Override
    public void reportComment(CommentDto commentDto) throws ResourceNotFoundException {
        UserPo userPo = userJpaRepository.findByUserName(commentDto.getName())
                .orElseThrow(() -> new ResourceNotFoundException("找不到該用戶"));
        CommentPo commentPo = commentPoRepository.findById(commentDto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
        commentPo.setIsReport(CommentReport.IS_REPORTED.getStatus());
        commentPoRepository.saveAndFlush(commentPo);

        UserReportPo userReportPo = new UserReportPo();
        userReportPo.setUser(userPo);
        userReportPo.setReason(commentDto.getReason());
        userReportPoRepository.saveAndFlush(userReportPo);
    }

    @Override
    public void likeComment(Long postId, Long id) {
        // 加鎖 避免重複點擊
        if(Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember("commentLike:" + postId, id.toString())))
            return;
        stringRedisTemplate.opsForSet().add("commentLike:" + postId, id.toString());
        commentPoRepository.addLikeCount(postId, id);
    }

    @Override
    public void cancelLikeComment(Long postId, Long id) {
        // 加鎖 避免重複點擊
        if(!Boolean.TRUE.equals(stringRedisTemplate.opsForSet().isMember("commentLike:" + postId, id.toString())))
            return;
        stringRedisTemplate.opsForSet().remove("commentLike:" + postId, id.toString());
        commentPoRepository.cancelLikeCount(postId, id);
    }

    @Override
    public Integer findLikeCount(Long postId, Long id) {
        return commentPoRepository.findLikeCount(postId, id);
    }

}
