package com.blog.service.impl;

import com.blog.dao.CommentPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.CommentDto;

import com.blog.mapper.CommentPoMapper;
import com.blog.po.CommentPo;
import com.blog.po.PostPo;
import com.blog.po.UserPo;
import com.blog.producer.NotificationProducer;
import com.blog.service.CommentService;
import com.blog.service.CrudLikeService;


import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final PostPoRepository postPoRepository;
    private final CommentPoRepository commentPoRepository;
    private final UserPoRepository userJpaRepository;
    private final NotificationProducer notificationProducer;

    private static final Logger logger = LoggerFactory.getLogger(CommentServiceImpl.class);


    /**
     * 新增評論
     *
     * @param commentDto 評論資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    @Transactional
    public void save(CommentDto commentDto) throws Exception {
        if(commentDto == null) {
            throw new IllegalArgumentException("新增評論資訊不得為空");
        }
        CommentPo commentPo = CommentPoMapper.INSTANCE.toPo(commentDto);
        // 建立與文章的關聯
        PostPo postPo = postPoRepository.findById(commentDto.getPostId()).orElseThrow(() -> new EntityNotFoundException("找不到文章"));
        UserPo userPO = userJpaRepository.findByUserName(commentDto.getName()).orElseThrow(() -> new EntityNotFoundException("找不到使用者"));
        commentPo.setPost(postPo);
        commentPo.setUser(userPO);

        logger.debug("新增評論資訊: {}", commentPo);
        commentPoRepository.saveAndFlush(commentPo);
        logger.debug("新增評論成功, id {}", commentPo.getId());
    }

    /**
     * 更新評論
     *
     * @param commentDto 評論資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void update(CommentDto commentDto) throws Exception {
        if(commentDto == null) {
            throw new IllegalArgumentException("更新評論資訊不得為空");
        }
        CommentPo commentPo = commentPoRepository.findById(commentDto.getId()).orElseThrow(() -> new EntityNotFoundException("找不到評論"));
        commentPo = CommentPoMapper.INSTANCE.partialUpdate(commentDto, commentPo);

        logger.debug("更新評論資訊: {}", commentPo);
        commentPoRepository.saveAndFlush(commentPo);
        logger.debug("更新評論成功, id {}", commentPo.getId());
    }

    /**
     * 刪除評論
     *
     * @param commentDto 評論資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(CommentDto commentDto) throws Exception {
        throw new MethodNotSupportedException("該刪除評論方法不支援");
    }

    /**
     * 刪除評論
     *
     * @param id 評論序號
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(Long id) throws Exception {
        if(id == null) {
            throw new IllegalArgumentException("刪除評論資訊不得為空");
        }
        if(!commentPoRepository.existsById(id)) {
            throw new EntityNotFoundException("找不到評論id " + id + "的資料");
        }
        commentPoRepository.deleteById(id);
    }

    /**
     * 搜尋指定序號的評論
     *
     * @param id 評論序號
     * @return CommentDto 評論資訊
     * @throws EntityNotFoundException 遇到異常則拋出
     */
    @Override
    public CommentDto findById(Long id) throws EntityNotFoundException {
        if(id == null) {
            throw new IllegalArgumentException("查詢評論資訊不得為空");
        }
        CommentPo commentPo = commentPoRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("找不到評論"));
        return CommentPoMapper.INSTANCE.toDto(commentPo);
    }

    /**
     * 搜尋所有評論
     *
     * @return List<CommentDto> 所有評論集合
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public List<CommentDto> findAll() throws Exception {
        return commentPoRepository.findAll().stream().map(CommentPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 搜尋所有 分頁評論
     *
     * @param page 當前頁數
     * @param pageSize 每頁顯示筆數
     * @return Page<CommentDto> 分頁評論
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public Page<CommentDto> findAll(Integer page, Integer pageSize) throws Exception {
        Pageable pageable = PageRequest.of(page, pageSize);
        return commentPoRepository.findAll(pageable).map(CommentPoMapper.INSTANCE::toDto);
    }

    /**
     * 檢舉評論內容
     *
     * @param commentDto 評論資訊
     */
    @Override
    public void report(CommentDto commentDto) {
        // 交給生產者 傳遞 待審核評論
        notificationProducer.sendReviewNotification(commentDto);
    }

    /**
     * 根據文章序號查詢評論
     *
     * @param postId 文章序號
     * @return List<CommentDto> 評論列表
     */
    @Override
    public List<CommentDto> findAll(Long postId) {
        return commentPoRepository.findAllByPostId(postId).stream().map(CommentPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 增加 按讚數
     *
     * @param id 評論序號
     */
    @Override
    public void like(Long id) {
        commentPoRepository.addCommentLike(id);
    }

    /**
     * 減少 按讚數
     *
     * @param id 評論序號
     */
    @Override
    public void cancelLike(Long id) {
        commentPoRepository.addCommentDisLike(id);
    }

    /**
     * 搜尋按讚數
     *
     * @param id 評論序號
     * @return Integer 按讚數
     */
    @Override
    public Integer queryLikeCount(Long id) {
        return commentPoRepository.getCommentLike(id);
    }
}
