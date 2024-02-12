package com.blog.service.impl;

import com.amazonaws.util.IOUtils;
import com.blog.dao.PostHistoryPoRepository;
import com.blog.dto.PostDto;
import com.blog.dto.PostHistoryPoDto;
import com.blog.mapper.PostHistoryPoMapper;
import com.blog.mapper.PostPoMapper;
import com.blog.po.PostHistoryPo;
import com.blog.po.PostPo;
import com.blog.service.AwsS3ClientService;
import com.blog.service.PostHistoryService;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostHistoryServiceImpl implements PostHistoryService {
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final PostHistoryPoRepository postHistoryPoRepository;

    private final AwsS3ClientService awsS3ClientService;
    @Override
    public PostHistoryPo transformToHistory(PostPo postPo) {
        PostHistoryPo postHistoryPo = new PostHistoryPo();
        postHistoryPo.setPostId(postPo.getId());
        postHistoryPo.setContent(postPo.getContent());
        postHistoryPo.setAuthorName(postPo.getAuthorName());
        postHistoryPo.setAuthorEmail(postPo.getAuthorEmail());
        postHistoryPo.setTitle(postPo.getTitle());
        postHistoryPo.setImageName(postPo.getImageName());
        return postHistoryPo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHistoryWherePostIdIn(List<Long> postIds) {
        postHistoryPoRepository.deleteWherePostIdIn(postIds);
    }

    @Override
    public void saveAll(List<PostHistoryPo> postHistoryPos) {
        postHistoryPoRepository.saveAll(postHistoryPos);
    }

    @Override
    public List<PostHistoryPo> findAll() {
        return postHistoryPoRepository.findAll();
    }

    @Override
    public Page<PostHistoryPoDto> getHistoryPosts(String title, String authorName, String startTime, String endTime, int page, int size, String sort, String direction) {
        LocalDateTime start = startTime == null ? null : LocalDateTime.parse(startTime, dateTimeFormatter);
        LocalDateTime end = endTime == null ? null : LocalDateTime.parse(endTime, dateTimeFormatter);
        Specification<PostHistoryPo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (title != null && !title.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("title"), title));
            }
            if (authorName != null && !authorName.isEmpty()) {
                predicates.add(criteriaBuilder.equal(root.get("authorName"), authorName));
            }
            if (start != null && end != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime"), start));
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime"), end));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        Pageable pageable = PageRequest.of(page - 1 , size, Sort.Direction.fromString(direction), sort);
        Page<PostHistoryPo> postHistoryPoPage = postHistoryPoRepository.findAll(specification, pageable);
        List<PostHistoryPoDto> postHistoryPoDtoList = new ArrayList<>();
        if(!postHistoryPoPage.getContent().isEmpty()){
            // 從s3 取得圖片
            for (int i = 0; i < postHistoryPoPage.getContent().size(); i++) {
                PostHistoryPo postHistoryPo = postHistoryPoPage.getContent().get(i);
                PostHistoryPoDto postHistoryPoDto = downloadImage(postHistoryPo);
                postHistoryPoDtoList.add(postHistoryPoDto);
            }
        }
        return new PageImpl<>(postHistoryPoDtoList, pageable, postHistoryPoPage.getTotalElements());
    }

    private PostHistoryPoDto downloadImage(PostHistoryPo postHistoryPoPo) {
        PostHistoryPoDto postHistoryPoDto = PostHistoryPoMapper.INSTANCE.toDto(postHistoryPoPo);
        if (postHistoryPoPo.getImageName() != null) {
            try {
                InputStream inputStream = awsS3ClientService.downloadFileFromS3Bucket(postHistoryPoPo.getImageName());
                byte[] image = (inputStream != null) ? IOUtils.toByteArray(inputStream) : null;
                postHistoryPoDto.setImage(image);
            } catch (Exception e) {
                log.error("下載文章圖片失敗", e);
            }
        }
        return postHistoryPoDto;
    }

}
