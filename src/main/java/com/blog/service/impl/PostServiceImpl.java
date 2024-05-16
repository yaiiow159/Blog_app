package com.blog.service.impl;

import com.blog.annotation.SendMail;
import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.TagPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.PostDto;
import com.blog.enumClass.PostStatus;
import com.blog.exception.ResourceNotFoundException;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.CategoryPoMapper;
import com.blog.mapper.PostPoMapper;
import com.blog.mapper.TagPoMapper;
import com.blog.po.CategoryPo;
import com.blog.po.PostPo;
import com.blog.service.GoogleStorageService;
import com.blog.service.PostService;
import com.blog.utils.SpringSecurityUtil;

import jakarta.annotation.Resource;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.Predicate;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


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
    private TagPoRepository tagPoRepository;
    @Resource
    private EntityManager  entityManager;

    @Resource
    private GoogleStorageService googleStorageService;

    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    @Transactional(rollbackFor = Exception.class)
    @SendMail(type = "post",operation = "add")
    public void add(PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        userJpaRepository.findByUserName(SpringSecurityUtil.getCurrentUser())
                .map(user -> {
                    postDto.setAuthorEmail(user.getEmail());
                    postDto.setAuthorName(user.getUserName());
                    return postDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("使用者不存在" + SpringSecurityUtil.getCurrentUser()));
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("找不到該分類序號" + postDto.getCategoryId()));
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        postPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        postPo.setCreatUser(SpringSecurityUtil.getCurrentUser());
        postPo.setStatus(PostStatus.PUBLISHED.getStatus());
        postPo.setCategory(categoryPo);
        //上傳文章圖
        postPoRepository.saveAndFlush(postPo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createDraft(PostDto postDto) {
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        postPo.setCreatUser(SpringSecurityUtil.getCurrentUser());
        postPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        postPo.setStatus(PostStatus.DRAFT.getStatus());
        postPoRepository.saveAndFlush(postPo);
    }
    @SendMail(operation = "edit")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long postId, PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        userJpaRepository.findByUserName(SpringSecurityUtil.getCurrentUser())
                .map(user -> {
                    postDto.setAuthorEmail(user.getEmail());
                    postDto.setAuthorName(user.getUserName());
                    return postDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("使用者名稱為" + SpringSecurityUtil.getCurrentUser() + "的使用者不存在"));
        postDto.setUpdateUser(SpringSecurityUtil.getCurrentUser());
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(postDto.getCategoryId()).orElseThrow(() -> new ResourceNotFoundException("找不到分類編號" + postDto.getCategoryId() + "的分類"));
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(() -> new ResourceNotFoundException("找不到該文章編號" + postId + "的文章"));
        postPo = PostPoMapper.INSTANCE.partialUpdate(postDto, postPo);
        postPo.setCategory(categoryPo);
        postPoRepository.saveAndFlush(postPo);
    }
    @Override
    public PostDto findPostById(Long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        CategoryPo categoryPo = postPo.getCategory();
        PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
        postDto.setCategoryDto(CategoryPoMapper.INSTANCE.toDto(categoryPo));
        return postDto;
    }
    @Override
    public Page<PostDto> findAll(String title, String authorName, Integer page, Integer size) {
        Specification<PostPo> spec = (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNotNull(root.get("category")));
            if (null != title) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (null != authorName) {
                predicates.add(criteriaBuilder.like(root.get("authorName"), "%" + authorName + "%"));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<PostPo> postPos = postPoRepository.findAll(spec, pageable);
        List<PostDto> dtoList = new ArrayList<>(postPos.getSize());
        postPos.forEach(postPo -> {
            PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
            postDto.setCategoryId(postPo.getCategory().getId());
            dtoList.add(postDto);
        });
        dtoList.forEach(postDto -> {
            postDto.setTagDtoList(TagPoMapper.INSTANCE.toDtoList(tagPoRepository.findAllTagsByPostId(postDto.getId())));
            postDto.setCategoryDto(CategoryPoMapper.INSTANCE.toDto(categoryPoRepository.findById(postDto.getCategoryId()).orElse(null)));
        });
        return new PageImpl<>(dtoList, pageable, postPos.getTotalElements());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String delete(Long id) throws ResourceNotFoundException, IOException {
        PostPo postPo = postPoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("找不到該文章編號" + id + "的文章"));
        postPoRepository.deleteById(id);
        googleStorageService.deleteFile(postPo.getImageName());
        return "刪除成功";
    }


    @Override
    public PostDto findPostByCategoryId(Long id, Long postId) throws ResourceNotFoundException {
        CategoryPo categoryPo = categoryPoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("找不到該分類編號" + id + "的分類"));
        List<PostPo> postPoList = categoryPo.getPosts();
        PostDto postDto = null;
        for (PostPo postPo : postPoList) {
            if (postId.equals(postPo.getId())) {
                postDto = PostPoMapper.INSTANCE.toDto(postPo);;
                break;
            }
        }
        return postDto;
    }

    // 查詢創建時間最新的前五筆文章
    @Override
    public List<PostDto> getLatestPost() {
        List<PostPo> postPoList = postPoRepository.findTop5ByOrderByIdDesc();
        List<PostDto> postDtoList = new ArrayList<>();
        for (PostPo postPo : postPoList) {
            PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
            postDto.setTagDtoList(TagPoMapper.INSTANCE.toDtoList(tagPoRepository.findAllTagsByPostId(postPo.getId())));
            postDto.setCategoryDto(postPo.getCategory() != null ? CategoryPoMapper.INSTANCE.toDto(postPo.getCategory()) : null);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    @Override
    public List<PostDto> getHotPost() {
        List<PostPo> postPoList = postPoRepository.findPopularPost();
        List<PostDto> postDtoList = new ArrayList<>();
        for (PostPo postPo : postPoList) {
            PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
            postDto.setTagDtoList(TagPoMapper.INSTANCE.toDtoList(tagPoRepository.findAllTagsByPostId(postPo.getId())));
            postDto.setCategoryDto(postPo.getCategory() != null ? CategoryPoMapper.INSTANCE.toDto(postPo.getCategory()) : null);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    @Override
    public List<PostDto> searchByKeyword(String keyword) {
        SearchSession searchSession = Search.session(entityManager);
        try {
            SearchResult<PostPo> searchResult = searchSession.search(PostPo.class)
                    .where((postPo) -> postPo.match().fields("title","content","author_name").matching(keyword + "*"))
                    .fetchAll();
            List<PostPo> postPos = searchResult.hits();

            List<PostDto> postDtoList = new ArrayList<>();
            for (PostPo postPo : postPos) {
                PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
                postDto.setTagDtoList(TagPoMapper.INSTANCE.toDtoList(tagPoRepository.findAllTagsByPostId(postPo.getId())));
                postDto.setCategoryDto(postPo.getCategory() != null ? CategoryPoMapper.INSTANCE.toDto(postPo.getCategory()) : null);
                postDtoList.add(postDto);
            }
            return postDtoList;
        } catch (Exception e) {
            log.error("搜尋文章失敗", e);
            return Collections.emptyList();
        }
    }



    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "likeCount", key = "#postId")
    public void addLike(Long postId) {
        postPoRepository.addLike(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "likeCount", key = "#postId")
    public void disLike(Long postId) {
        if(Boolean.FALSE.equals(stringRedisTemplate.hasKey("like:" + postId))) {
            return;
        }
        stringRedisTemplate.delete("like:" + postId);
        postPoRepository.disLike(postId);
    }
    @Override
    public Long getViewsCount(Long postId) {
        return postPoRepository.getViewsCountById(postId);
    }

    @Override
    public Long getLikesCountById(Long postId) {
        return postPoRepository.getLikesCountById(postId);
    }

    @Override
    public void upload(MultipartFile file, Long postId) throws IOException, ExecutionException, InterruptedException {
        final String imageName = generateImageName(file);
        CompletableFuture<String> result = googleStorageService.uploadFile(file, imageName);
        if(result.get() == null) {
            throw new IOException("上傳圖片失敗" + imageName);
        }
        postPoRepository.updateImageName(result.get(), postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "bookmarkCount", key = "#postId")
    public void addBookmark(Long postId){
        postPoRepository.addBookmark(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "bookmarkCount", key = "#postId")
    public void deleteBookmark(Long postId) {
        postPoRepository.deleteBookmark(postId);
    }

    @Override
    @Cacheable(value = "likeCount", key = "#postId")
    public Integer getLikesCount(Long postId) {
        return postPoRepository.getLikeCount(postId);
    }

    @Override
    @Cacheable(value = "bookmarkCount", key = "#postId")
    public Integer getBookmarksCount(Long postId) {
        return postPoRepository.getBookmarkCount(postId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    @CacheEvict(value = "postViewCount", key = "#postId")
    public void addPostView(Long postId) {
        postPoRepository.addPostView(postId);
    }

    @Override
    @Cacheable(value = "dislikeCount", key = "#postId")
    public Integer getDislikesCount(Long postId) {
        return postPoRepository.getDislikeCount(postId);
    }

    @Override
    public List<PostDto> searchByTag(Long id) {
        List<PostPo> postPos = postPoRepository.findAllByTagId(id);
        List<PostDto> postDtoList = new ArrayList<>();
        for (PostPo postPo : postPos) {
            PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
            postDto.setTagDtoList(TagPoMapper.INSTANCE.toDtoList(tagPoRepository.findAllTagsByPostId(postPo.getId())));
            postDto.setCategoryDto(postPo.getCategory() != null ? CategoryPoMapper.INSTANCE.toDto(postPo.getCategory()) : null);
            postDtoList.add(postDto);
        }
        return postDtoList;
    }

    private String generateImageName(MultipartFile imgFile) {
        String originalFilename = imgFile.getOriginalFilename();
        String extension = "";
        if (Objects.requireNonNull(originalFilename).lastIndexOf(".") > -1) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return UUID.randomUUID() + extension;
    }

}
