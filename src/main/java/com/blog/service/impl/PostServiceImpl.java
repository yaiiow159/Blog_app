package com.blog.service.impl;

import com.blog.annotation.SendMail;
import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.TagPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.PostDto;
import com.blog.enumClass.PostStatus;
import com.blog.exception.ResourceNotFoundException;
import com.blog.mapper.CategoryPoMapper;
import com.blog.mapper.PostPoMapper;
import com.blog.mapper.TagPoMapper;
import com.blog.po.CategoryPo;
import com.blog.po.PostPo;
import com.blog.service.GoogleStorageService;
import com.blog.service.PostService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
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


    @Override
    @Transactional(rollbackFor = Exception.class)
    @SendMail(type = "post",operation = "add")
    public void add(PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        userJpaRepository.findByUserName(SpringSecurityUtils.getCurrentUser())
                .map(user -> {
                    postDto.setAuthorEmail(user.getEmail());
                    postDto.setAuthorName(user.getUserName());
                    return postDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("使用者不存在"));
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(postDto.getCategoryId()).orElseThrow(ResourceNotFoundException::new);
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        postPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        postPo.setCreatUser(SpringSecurityUtils.getCurrentUser());
        postPo.setStatus(PostStatus.PUBLISHED.getStatus());
        postPo.setCategory(categoryPo);
        //上傳文章圖片
        uploadFile(postPo, postDto);
        postPoRepository.saveAndFlush(postPo);
    }
    @SendMail(type = "post", operation = "edit")
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void edit(Long postId, PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        userJpaRepository.findByUserName(SpringSecurityUtils.getCurrentUser())
                .map(user -> {
                    postDto.setAuthorEmail(user.getEmail());
                    postDto.setAuthorName(user.getUserName());
                    return postDto;
                })
                .orElseThrow(() -> new ResourceNotFoundException("使用者不存在"));
        postDto.setUpdateUser(SpringSecurityUtils.getCurrentUser());
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(postDto.getCategoryId()).orElseThrow(ResourceNotFoundException::new);
        PostPo postPo = postPoRepository.findById(postId).orElseThrow(ResourceNotFoundException::new);
        postPo = PostPoMapper.INSTANCE.partialUpdate(postDto, postPo);
        postPo.setCategory(categoryPo);
        deleteFile(postPo);
        uploadFile(postPo, postDto);
        postPoRepository.saveAndFlush(postPo);
    }
    @Override
    public PostDto findPostById(Long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        CategoryPo categoryPo = postPo.getCategory();
        return downloadImage(postPo, categoryPo);
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
            if(postPo.getImageName()!= null){
                try {
                    byte[] image = googleStorageService.downloadFile(postPo.getImageName());
                    if(image != null) {
                        postDto.setImageBytes(image);
                    }
                } catch (Exception e) {
                    log.error("下載文章圖片失敗", e);
                }
            }
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
    public String delete(Long id) throws ResourceNotFoundException {
        PostPo postPo = postPoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
        deleteFile(postPo);
        postPoRepository.deleteById(id);
        return "刪除成功";
    }


    @Override
    public PostDto findPostByCategoryId(Long id, Long postId) throws ResourceNotFoundException {
        CategoryPo categoryPo = categoryPoRepository.findById(id).orElseThrow(ResourceNotFoundException::new);
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
        List<PostPo> postPoList = postPoRepository.findTop5ByOrderByIdDesc();
        List<PostDto> postDtoList = new ArrayList<>();
        for (PostPo postPo : postPoList) {
            PostDto postDto = downloadImage(postPo);
            postDto.setTagDtoList(TagPoMapper.INSTANCE.toDtoList(tagPoRepository.findAllTagsByPostId(postPo.getId())));
            postDto.setCategoryDto(postPo.getCategory() != null ? CategoryPoMapper.INSTANCE.toDto(postPo.getCategory()) : null);
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
                    .where((postPo) -> postPo.match().fields("title", "content","author_name").matching(keyword + "*"))
                    .fetchAll();
            List<PostPo> postPos = searchResult.hits();
            return postPos.stream()
                    .map(this::downloadImage)
                    .peek(postDto -> {
                        postDto.setTagDtoList(TagPoMapper.INSTANCE.toDtoList(tagPoRepository.findAllTagsByPostId(postDto.getId())));
                        postDto.setCategoryDto(postDto.getCategoryId() != null ? CategoryPoMapper.INSTANCE.toDto(categoryPoRepository.findById(postDto.getCategoryId()).orElse(null)) : null);
                    })
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
    public void createDraft(PostDto postDto) throws ExecutionException, InterruptedException, IOException {
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        postPo.setCreatUser(SpringSecurityUtils.getCurrentUser());
        postPo.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        uploadFile(postPo,postDto);
        postPo.setStatus(PostStatus.DRAFT.getStatus());
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
                byte[] image = googleStorageService.downloadFile(postPo.getImageName());
                postDto.setImageBytes(image);
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
                byte[] image = googleStorageService.downloadFile(postPo.getImageName());
                postDto.setImageBytes(image);
            } catch (Exception e) {
                log.error("下載文章圖片失敗", e);
            }
        }
        return postDto;
    }

    private void uploadFile(PostPo postPo,PostDto postDto) throws IOException, ExecutionException, InterruptedException {
        //上傳文章圖片
        if(!ObjectUtils.isEmpty(postDto.getImage()) && postDto.getImage() != null) {
            // 上傳圖片智google storage
            String imageName = postDto.getImage().getOriginalFilename();
            CompletableFuture<String> result = googleStorageService.uploadFile(postDto.getImage(), imageName);
            if(result.equals("上傳文件成功")) {
                postPo.setImageName(imageName);
            }
        }
    }

    private void deleteFile(PostPo postPo) {
        if(!ObjectUtils.isEmpty(postPo.getImageName()) && postPo.getImageName() != null) {
            try {
                googleStorageService.deleteFile(postPo.getImageName());
                if(postPo.getImageName() != null) {
                    postPo.setImageName(null);
                }
            } catch (Exception e) {
                log.error("刪除文章圖片失敗", e);
            }
        }
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
