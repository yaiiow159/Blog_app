package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.blog.annotation.NotifyByEmail;
import com.blog.dao.CommentPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.UserJpaRepository;
import com.blog.dao.UserReportPoRepository;
import com.blog.dto.CommentDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.CommentPoMapper;
import com.blog.po.CommentPo;
import com.blog.po.PostPo;
import com.blog.po.UserPo;
import com.blog.po.UserReportPo;
import com.blog.service.CommentService;
import com.blog.utils.SpringSecurityUtils;

import jakarta.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

@Service
@Slf4j
public class CommentServiceImpl implements CommentService {
    private static final int MAX_REPORT_NUM = 5;
    @Resource
    private PostPoRepository postPoRepository;
    @Resource
    private CommentPoRepository commentPoRepository;
    @Resource
    private UserJpaRepository userJpaRepository;
    @Resource
    private UserReportPoRepository userReportPoRepository;
    @Resource
    private JavaMailSender javaMailSender;
    @Resource(name = "mailExecutor")
    private ThreadPoolExecutor mailExecutor;
    // 每當有人創建新評論，通知該作者 該文章有新評論了
    @Override
    @NotifyByEmail("comment")
    public CommentDto createComment(Long postId, CommentDto commentDto) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該文章"));
        commentDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentDto.setCreatUser(SpringSecurityUtils.getCurrentUser());
        commentDto.setName(SpringSecurityUtils.getCurrentUser());
        // 使用NAME 查詢 使用者
        UserPo userPo = userJpaRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new ResourceNotFoundException("找不到該用戶"));
        commentDto.setPostId(String.valueOf(postPo.getId()));
        CommentPo commentPo = CommentPoMapper.INSTANCE.toPo(commentDto);
        commentPo.setPost(postPo);
        commentPo.setUser(userPo);
        CommentDto dto = CommentPoMapper.INSTANCE.toDto(commentPoRepository.save(commentPo));
        dto.setPostId(String.valueOf(postPo.getId()));
        return dto;
    }

    @Override
    public CommentDto findComment(Long postId,Long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
        CommentPo commentPo = postPo.getComments().stream().filter(c -> c.getId().equals(id)).findFirst().orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
        CommentDto dto = CommentPoMapper.INSTANCE.toDto(commentPo);
        dto.setPostId(String.valueOf(postPo.getId()));
        return dto;
    }

    @Override
    public CommentDto updateComment(Long postId,Long id, CommentDto commentDto) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findByIdAndIsDeletedFalse(postId);
        CommentPo commentPo = commentPoRepository.findByIdAndIsDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
        commentDto.setUpdateUser(SpringSecurityUtils.getCurrentUser());
        commentDto.setUpdDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        commentDto.setPostId(String.valueOf(postPo.getId()));
        commentPo = CommentPoMapper.INSTANCE.partialUpdate(commentDto, commentPo);
        commentPo.setPost(postPo);
        return CommentPoMapper.INSTANCE.toDto(commentPoRepository.save(commentPo));
    }

    @Override
    public String deleteComment(Long postId,Long id) throws ResourceNotFoundException {
        JSONObject jsonObject = new JSONObject();
        CommentPo commentPo = commentPoRepository.findByPostIdAndId(postId,id)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
        commentPo.setIsDeleted(true);
        commentPo = commentPoRepository.save(commentPo);
        if(commentPo.getIsDeleted()) {
            jsonObject.put("message", "刪除成功");
            return jsonObject.toJSONString();
        }
        jsonObject.put("message", "刪除失敗");
        return jsonObject.toJSONString();
    }

    @Override
    public List<CommentDto> findAllComments(Long postId) throws ResourceNotFoundException {
        postPoRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到該文章"));
        List<CommentPo> commentPoList = commentPoRepository.findAllByPostIdAndIsDeletedFalse(postId);
        if(CollectionUtils.isEmpty(commentPoList))
            return null;
        return CommentPoMapper.INSTANCE.toDtoList(commentPoList);
    }

    @Override
    public String reportComment(CommentDto commentDto) throws ResourceNotFoundException {
        JSONObject jsonObject = new JSONObject();
        UserPo userPo = userJpaRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new ResourceNotFoundException("找不到該用戶"));
        if(userReportPoRepository.findByUserId(userPo.getId()).size() >= MAX_REPORT_NUM){
            userPo.setLocked(true);
            userJpaRepository.save(userPo);
            // 寄送郵件通知該使用者帳戶已被鎖戶
            CompletableFuture.runAsync(() -> {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(userPo.getUserName());
                message.setSubject("帳戶已被鎖戶");
                message.setFrom("test123@gmail.com");
                message.setText("您的帳戶已被鎖戶 + " + userPo.getUserName() + "請聯繫管理員進行解鎖");
                javaMailSender.send(message);
            }, mailExecutor).whenComplete((a, b) -> {
                log.info("寄送郵件通知該使用者帳戶已被鎖戶成功");
            }).exceptionally(throwable -> {
                log.error(throwable.getMessage());
                return null;
            });

            throw new ValidateFailedException("該用戶已經檢舉過五次，該帳戶進行鎖戶");
        }
        // 設置該留言已被檢舉
        CommentPo commentPo = commentPoRepository.findById(commentDto.getId()).orElseThrow(() -> new ResourceNotFoundException("找不到該留言"));
        commentPo.setIsReport(true);
        commentPoRepository.save(commentPo);

        UserReportPo userReportPo = new UserReportPo();
        userReportPo.setUser(userPo);
        userReportPo.setReason(commentDto.getReason());
        userReportPoRepository.save(userReportPo);
        jsonObject.put("message", "檢舉成功");
        return jsonObject.toJSONString();
    }

    @Override
    @Transactional
    public synchronized void addCommentLike(Long postId, Long commentId) {
        commentPoRepository.addCommentLike(postId, commentId);
    }

    @Override
    @Transactional
    public synchronized void addCommentDisLike(Long postId, Long commentId) {
        commentPoRepository.addCommentDisLike(postId, commentId);
    }

    @Override
    public synchronized Long getCommentLikeCount(Long commentId) {
        return commentPoRepository.getCommentLike(commentId);
    }

    @Override
    public synchronized Long getCommentDisLikeCount(Long commentId) {
        return commentPoRepository.getCommentDisLike(commentId);
    }

}
