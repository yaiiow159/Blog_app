package com.blog.service.impl;

import com.blog.annotation.SendMail;
import com.blog.dao.CommentPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dao.UserReportPoRepository;
import com.blog.dto.CommentDto;
import com.blog.enumClass.CommentReport;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.CommentPoMapper;
import com.blog.po.CommentPo;
import com.blog.po.PostPo;
import com.blog.po.UserPo;
import com.blog.po.UserReportPo;
import com.blog.producer.NotificationProducer;
import com.blog.service.CommentService;
import com.blog.utils.SpringSecurityUtil;

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
import java.util.Objects;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    @Resource
    private PostPoRepository postPoRepository;
    @Resource
    private CommentPoRepository commentPoRepository;
    @Resource
    private UserPoRepository userJpaRepository;
    @Resource
    private UserReportPoRepository userReportPoRepository;

    @Resource
    private NotificationProducer notificationProducer;

    @Resource
    private StringRedisTemplate stringRedisTemplate;
    // 每當有人創建新評論，通知該作者 該文章有新評論了
    @Override
    @SendMail(type = "comment",operation = "add")
    @Transactional(rollbackFor = Exception.class)
    public void add(Long postId, CommentDto commentDto) throws ResourceNotFoundException {
            PostPo postPo = postPoRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該文章序號" + postId + "的資料"));
        // 使用NAME 查詢 使用者
        UserPo userPo = userJpaRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new ResourceNotFoundException("找不到該使用者" + commentDto.getName() + "的資料"));
        CommentPo commentPo = CommentPoMapper.INSTANCE.toPo(commentDto);
        commentPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentPo.setCreatUser(SpringSecurityUtil.getCurrentUser());
        commentPo.setIsReport(CommentReport.NOT_REPORTED.getStatus());
        commentPo.setDislikes(0L);
        commentPo.setLikes(0L);
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
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long postId, Long id, CommentDto commentDto) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("找不到文章序號為" + postId + "的留言"));
        CommentPo commentPo = commentPoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("找不到留言序號為" + id + "的留言"));
        commentPo = CommentPoMapper.INSTANCE.partialUpdate(commentDto, commentPo);
        commentPo.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentPo.setUpdateUser(SpringSecurityUtil.getCurrentUser());
        commentPo.setPost(postPo);
        commentPoRepository.saveAndFlush(commentPo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long postId, Long id) throws ResourceNotFoundException {
        commentPoRepository.deleteById(id);
    }

    @Override
    public List<CommentDto> findAllComments(Long postId) throws ResourceNotFoundException {
        postPoRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到文章序號為" + postId + "的留言"));
        List<CommentPo> commentPoList = commentPoRepository.findAllByPostId(postId);
        if(CollectionUtils.isEmpty(commentPoList))
            return null;
        return CommentPoMapper.INSTANCE.toDtoList(commentPoList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportComment(CommentDto commentDto) {
        notificationProducer.sendReviewNotification(commentDto);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCommentlike(Long postId, Long id){
        // 確認同一使用者只能按讚一次
        Boolean isLikeCheck = stringRedisTemplate.opsForSet()
                .isMember("commentLike" + id, Objects.requireNonNull(SpringSecurityUtil.getCurrentUser()));
        if(Boolean.TRUE.equals(isLikeCheck)) {
            return;
        }
        stringRedisTemplate.opsForSet().add("commentLike" + id, Objects.requireNonNull(SpringSecurityUtil.getCurrentUser()));
        commentPoRepository.addCommentLike(postId, id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addCommentDislike(Long postId, Long id) {
        // 確認同一使用者只能按讚一次
        Boolean isDislikeCheck = stringRedisTemplate.opsForSet()
                .isMember("commentDislike" + id, Objects.requireNonNull(SpringSecurityUtil.getCurrentUser()));
        if(Boolean.TRUE.equals(isDislikeCheck)) {
            return;
        }
        stringRedisTemplate.opsForSet().add("commentDislike" + id, Objects.requireNonNull(SpringSecurityUtil.getCurrentUser()));
        commentPoRepository.addCommentDisLike(postId, id);
    }

    @Override
    public Integer findLikeCount(Long postId, Long id) {
        return commentPoRepository.findLikeCount(postId, id);
    }

}
