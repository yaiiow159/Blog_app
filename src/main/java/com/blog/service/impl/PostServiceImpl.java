package com.blog.service.impl;

import com.blog.annotation.NotifyByEmail;
import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.PostPoMapper;
import com.blog.po.CategoryPo;
import com.blog.po.PostPo;
import com.blog.po.UserPo;
import com.blog.service.AwsS3ClientService;
import com.blog.service.PostService;
import com.blog.utils.FileUtils;
import com.blog.utils.SpringSecurityUtils;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    @Resource
    private CategoryPoRepository categoryPoRepository;
    @Resource
    private PostPoRepository postPoRepository;
    @Resource
    private UserPoRepository userJpaRepository;
    @Resource
    private EntityManager  entityManager;
    @Resource
    private AwsS3ClientService awsS3ClientService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void add(PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        Optional<UserPo> optional = userJpaRepository.findByUserName(SpringSecurityUtils.getCurrentUser());
        if(optional.isPresent()){
            postDto.setAuthorEmail(optional.get().getEmail());
            postDto.setAuthorName(optional.get().getUserName());
        } else {
            throw new ResourceNotFoundException("使用者不存在");
        }
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findByIdAndIsDeletedFalse(postDto.getCategoryId()).orElseThrow(ResourceNotFoundException::new);
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        postPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        postPo.setCreatUser(SpringSecurityUtils.getCurrentUser());
        postPo.setCategory(categoryPo);
        //上傳文章圖片
        uploadFile(postPo, postDto);
        postPoRepository.saveAndFlush(postPo);
    }
    @NotifyByEmail
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long postId, PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        Optional<UserPo> userPoOptional = userJpaRepository.findByUserName(SpringSecurityUtils.getCurrentUser());
        if(userPoOptional.isPresent()){
            postDto.setAuthorEmail(userPoOptional.get().getEmail());
            postDto.setAuthorName(userPoOptional.get().getUserName());
        } else {
            throw new ResourceNotFoundException("使用者不存在");
        }
        postDto.setUpdateUser(SpringSecurityUtils.getCurrentUser());
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(postDto.getCategoryId()).orElseThrow(ResourceNotFoundException::new);
        PostPo postPo = postPoRepository.findByPostIdAndIsDeletedFalseAndCategoryId(postId, postDto.getCategoryId()).orElseThrow(ResourceNotFoundException::new);
        postPo = PostPoMapper.INSTANCE.partialUpdate(postDto, postPo);
        postPo.setCategory(categoryPo);
        // 將multipartFile轉換成lob數據
        uploadFile(postPo, postDto);
        deleteFile(postPo);
        postPoRepository.saveAndFlush(postPo);
    }
    @Override
    public PostDto findPostById(Long id) {
        PostPo postPo = postPoRepository.findByIdAndIsDeletedFalse(id);
        CategoryPo categoryPo = postPo.getCategory();
        return downloadImage(postPo, categoryPo);
    }
    @Override
    public Page<PostDto> findAll(String title, String authorName, Integer page, Integer size) {
        Specification<PostPo> spec = (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNotNull(root.get("category")));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            if (null != title) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (null != authorName) {
                predicates.add(criteriaBuilder.like(root.get("authorName"), "%" + authorName + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PostPo> postPos = postPoRepository.findAll(spec, pageable);
        List<PostDto> dtoList = new ArrayList<>(postPos.getSize());
        postPos.forEach(postPo -> {
            PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
            if(postPo.getImageName()!= null){
                try {
                    byte[] image = awsS3ClientService.downloadFileFromS3Bucket(postPo.getImageName());
                    if(image != null) {
                        postDto.setImage(image);
                    }
                } catch (Exception e) {
                    log.error("下載文章圖片失敗", e);
                }
            }
            postDto.setCategoryId(postPo.getCategory().getId());
            dtoList.add(postDto);
        });
        return new PageImpl<>(dtoList, pageable, postPos.getTotalElements());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        postPo.setIsDeleted(true);
        deleteFile(postPo);
        postPoRepository.saveAndFlush(postPo);
        return "刪除成功";
    }


    @Override
    public PostDto findPostByCategoryId(Long id, Long postId) throws ResourceNotFoundException {
        Optional<CategoryPo> categoryPoOptional = categoryPoRepository.findByIdAndIsDeletedFalse(id);
        if (categoryPoOptional.isEmpty()) {
            throw new ResourceNotFoundException("該分類不存在");
        }
        CategoryPo categoryPo = categoryPoOptional.get();
        List<PostPo> postPoList = categoryPo.getPosts();
        PostDto postDto = null;
        for (PostPo postPo : postPoList) {
            if (postId.equals(postPo.getId())) {
                postDto = PostPoMapper.INSTANCE.toDto(postPo);
                postDto = downloadImage(postPo);
                break;
            }
        }
        return postDto;
    }

    // 查詢創建時間最新的前五筆文章
    @Override
    public List<PostDto> getLatestPost() {
        List<PostPo> postPoList = postPoRepository.findTop5ByIsDeletedFalseAndAndCreateDate();
        List<PostDto> postDtoList = new ArrayList<>();
        for (PostPo postPo : postPoList) {
            PostDto postDto = downloadImage(postPo);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    @Override
    public List<PostDto> getHotPost() {
        List<PostPo> postPoList = postPoRepository.findTop5Posts();
        List<PostDto> postDtoList = new ArrayList<>();
        for (PostPo postPo : postPoList) {
            PostDto postDto = downloadImage(postPo);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    @Override
    public List<PostDto> searchByKeyword(String keyword) {
        SearchSession searchSession = Search.session(entityManager);
        try {
            SearchResult<PostPo> searchResult = searchSession.search(PostPo.class)
                    .where((postPo) -> postPo.match().fields("title", "content","author_name").matching(keyword + "*"))
                    .fetchAll();
            List<PostPo> postPos = searchResult.hits();
            return postPos.stream()
                    .map(this::downloadImage)
                    .filter(Objects::nonNull)
                    .toList();
        } catch (Exception e) {
            log.error("搜尋文章失敗", e);
            return Collections.emptyList();
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addLike(String postId) {
        postPoRepository.addLike(Long.parseLong(postId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void disLike(String postId) {
        postPoRepository.disLike(Long.parseLong(postId));
    }

    @Override
    public Long getLikeCount(String postId) {
        return postPoRepository.getLikeCount(Long.parseLong(postId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addView(String postId) {
        postPoRepository.addView(Long.parseLong(postId));
    }

    @Override
    public Long getViewCount(String postId) {
        return postPoRepository.getViewCount(Long.parseLong(postId));
    }

    @Override
    public void createDraft(PostDto postDto) {
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        postPo.setIsDeleted(false);
        postPo.setCreatUser(SpringSecurityUtils.getCurrentUser());
        postPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        postPoRepository.saveAndFlush(postPo);
    }

    @Override
    public Long getViewsCount(Long postId) {
        return postPoRepository.getViewsCountById(postId);
    }

    @Override
    public Long getLikesCountById(Long postId) {
        return postPoRepository.getLikesCountById(postId);
    }

    private PostDto downloadImage(PostPo postPo, CategoryPo categoryPo) {
        PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
        postDto.setCategoryId(categoryPo.getId());
        if (postPo.getImageName() != null) {
            try {
                byte[] image = awsS3ClientService.downloadFileFromS3Bucket(postPo.getImageName());
                postDto.setImage(image);
            } catch (Exception e) {
                log.error("下載文章圖片失敗", e);
            }
        }
        return postDto;
    }

    private PostDto downloadImage(PostPo postPo) {
        PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
        if (postPo.getImageName() != null) {
            try {
                byte[] image = awsS3ClientService.downloadFileFromS3Bucket(postPo.getImageName());
                postDto.setImage(image);
            } catch (Exception e) {
                log.error("下載文章圖片失敗", e);
            }
        }
        return postDto;
    }

    private void uploadFile(PostPo postPo, PostDto postDto) throws IOException {
        //上傳文章圖片
        if(!ObjectUtils.isEmpty(postDto.getMultipartFile())) {
            MultipartFile image = postDto.getMultipartFile();
            String fileName = FileUtils.generateFileName(image);
            File convertFile = FileUtils.convertMultipartFileToFile(image);
            try {
                CompletableFuture<String> result = awsS3ClientService.uploadFileToS3Bucket(fileName, convertFile);
                if(!result.get().equals("文件上傳成功")){
                    log.error("上傳文章圖片失敗");
                    throw new ResourceNotFoundException("上傳文章圖片失敗");
                } else {
                    postPo.setImageName(fileName);
                }
            } catch (Exception e) {
                log.error("上傳文章圖片失敗", e);
            }
        }
    }

    private void deleteFile(PostPo postPo) {
        if(!ObjectUtils.isEmpty(postPo.getImageName()) && postPo.getImageName() != null) {
            try {
                awsS3ClientService.deleteFileFromS3Bucket(postPo.getImageName());
            } catch (Exception e) {
                log.error("刪除文章圖片失敗", e);
            }
        }
    }


}
