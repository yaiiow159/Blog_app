package com.blog.service.impl;

import com.alibaba.fastjson2.JSONObject;
import com.amazonaws.util.IOUtils;
import com.blog.annotation.NotifyByEmail;
import com.blog.coverter.PageConverter;
import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.UserJpaRepository;
import com.blog.dto.PostDto;
import com.blog.enumClass.PostStatus;
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
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
@Slf4j
public class PostServiceImpl implements PostService {
    @Resource
    private CategoryPoRepository categoryPoRepository;
    @Resource
    private PostPoRepository postPoRepository;
    @Resource
    private UserJpaRepository userJpaRepository;
    @Resource
    private EntityManager  entityManager;
    @Resource
    private AwsS3ClientService awsS3ClientService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDto createPost(Long categoryId,PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        Optional<UserPo> optional = userJpaRepository.findByUserName(SpringSecurityUtils.getCurrentUser());
        if(optional.isPresent()){
            postDto.setAuthorEmail(optional.get().getEmail());
            postDto.setAuthorName(optional.get().getUserName());
        } else {
            throw new ResourceNotFoundException("使用者不存在");
        }
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findByIdAndIsDeletedFalse(categoryId).orElseThrow(ResourceNotFoundException::new);
        postDto.setCreatUser(SpringSecurityUtils.getCurrentUser());
        postDto.setCategoryId(String.valueOf(categoryId));
        postDto.setCreateDate(LocalDateTime.now(ZoneId.of("Asia/Taipei")));
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        File file = null;
        if(!ObjectUtils.isEmpty(postDto.getMultipartFile())) {
            MultipartFile image = postDto.getMultipartFile();
            file = FileUtils.convertMultipartFileToFile(image);
            String fileName = FileUtils.generateFileName(image);
            try {
                CompletableFuture<String> result = awsS3ClientService.uploadFileToS3Bucket(fileName, file);
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
        postPo.setCategory(categoryPo);
        // 設置狀態為發布
        postPo.setStatus(PostStatus.PUBLISHED);
        PostDto dto = PostPoMapper.INSTANCE.toDto(postPoRepository.save(postPo));
        dto.setCategoryId(String.valueOf(categoryId));
        if(null != file){
            dto.setImage(FileCopyUtils.copyToByteArray(Objects.requireNonNull(file)));
        }
        return dto;
    }

    @NotifyByEmail
    @Override
    @Transactional(rollbackFor = Exception.class)
    public PostDto updatePost(Long categoryId,Long postId,PostDto postDto) throws ResourceNotFoundException, IOException, ExecutionException, InterruptedException {
        // 查詢使用者email 以及名稱
        Optional<UserPo> optional = userJpaRepository.findByUserName(SpringSecurityUtils.getCurrentUser());
        if(optional.isPresent()){
            postDto.setAuthorEmail(optional.get().getEmail());
            postDto.setAuthorName(optional.get().getUserName());
        } else {
            throw new ResourceNotFoundException("使用者不存在");
        }
        postDto.setUpdateUser(SpringSecurityUtils.getCurrentUser());
        //驗證文章內容 防止xss注入攻擊
        String content = postDto.getContent();
        String cleanContent = Jsoup.clean(content, Safelist.relaxed());
        postDto.setContent(cleanContent);
        // 確認該篇文章是否已經存在分類中
        CategoryPo categoryPo = categoryPoRepository.findById(categoryId).orElseThrow(ResourceNotFoundException::new);
        PostPo postPo = postPoRepository.findByPostIdAndIsDeletedFalseAndCategoryId(postId, categoryId).orElseThrow(ResourceNotFoundException::new);
        postPo = PostPoMapper.INSTANCE.partialUpdate(postDto, postPo);
        postPo.setCategory(categoryPo);
        // 將multipartFile轉換成lob數據
        File file = null;
        if(!ObjectUtils.isEmpty(postDto.getMultipartFile())) {
            MultipartFile image = postDto.getMultipartFile();
            file = FileUtils.convertMultipartFileToFile(image);
            String fileName = FileUtils.generateFileName(image);
            try {
                CompletableFuture<String> result = awsS3ClientService.uploadFileToS3Bucket(fileName, file);
                if(!result.get().equals("文件上傳成功")){
                    log.error("上傳文章圖片失敗");
                    throw new ResourceNotFoundException("上傳文章圖片失敗");
                } else {
                    postPo.setImageName(fileName);
                }
            } catch (Exception e) {
                log.error("上傳文章圖片失敗", e);
            }
        } else if (null != postPo.getImageName() && !postPo.getImageName().isEmpty()) {
            CompletableFuture<String> result = awsS3ClientService.deleteFileFromS3Bucket(postPo.getImageName());
            if(!result.get().equals("文件删除成功")) {
                log.error("上傳文章圖片失敗");
                throw new ResourceNotFoundException("上傳文章圖片失敗");
            } else {
                postPo.setImageName(null);
            }
        }
        postPoRepository.save(postPo);
        PostDto dto = PostPoMapper.INSTANCE.toDto(postPo);
        dto.setCategoryId(String.valueOf(categoryId));
        if(null != file){
            dto.setImage(FileCopyUtils.copyToByteArray(Objects.requireNonNull(file)));
        }
        return dto;
    }

    @Override
    public List<PostDto> getAllPosts() {
        List<PostPo> postPoList = postPoRepository.findByIsDeletedFalse();
        return postPoList.stream()
                .map(this::downloadImage).toList();
    }

    @Override
    public PostDto getOnePost(long id) {
        PostPo postPo = postPoRepository.findByIdAndIsDeletedFalse(id);
        CategoryPo categoryPo = postPo.getCategory();
        return downloadImage(postPo, categoryPo);
    }
    @Override
    public Page<PostDto> getAllPosts(String title, String content, String authorName, int page, int size, String sort, String direction) {
        Specification<PostPo> spec = (root, query, criteriaBuilder) ->{
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.isNotNull(root.get("category")));
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));
            if (null != title) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (null != content) {
                predicates.add(criteriaBuilder.like(root.get("content"), "%" + content + "%"));
            }
            if (null != authorName) {
                predicates.add(criteriaBuilder.like(root.get("authorName"), "%" + authorName + "%"));
            }
            predicates.add(criteriaBuilder.equal(root.get("isDeleted"), false));

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };

        Pageable pageable = null;
        if (direction.equals("desc")) {
            pageable = PageRequest.of(page - 1, size, Sort.by(sort).descending());
        } else {
            pageable = PageRequest.of(page - 1, size, Sort.by(sort).ascending());
        }
        Page<PostPo> postPos = postPoRepository.findAll(spec, pageable);
        List<PostDto> dtoList = new ArrayList<>(postPos.getSize());
        postPos.forEach(postPo -> {
            PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
            if(postPo.getImageName()!= null){
                try {
                    InputStream inputStream = awsS3ClientService.downloadFileFromS3Bucket(postPo.getImageName());
                    if(inputStream != null) {
                        byte[] image = IOUtils.toByteArray(inputStream);
                        postDto.setImage(image);
                    }
                } catch (Exception e) {
                    log.error("下載文章圖片失敗", e);
                }
            }
            postDto.setCategoryId(String.valueOf(postPo.getCategory().getId()));
            dtoList.add(postDto);
        });
        return new PageImpl<>(dtoList, pageable, postPos.getTotalElements());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deletePost(long id) throws ResourceNotFoundException {
        JSONObject jsonObject = new JSONObject();
        PostPo postPo = postPoRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("文章不存在"));
        postPo.setIsDeleted(true);
        try{
            if(postPo.getImageName() != null) {
                CompletableFuture<String> result = awsS3ClientService.deleteFileFromS3Bucket(postPo.getImageName());
                if(!result.get().equals("文件删除成功")) {
                    log.error("刪除文章圖片失敗");
                    throw new ResourceNotFoundException("刪除文章圖片失敗");
                }
            }
        } catch (Exception e) {
            log.error("刪除文章圖片失敗", e);
        }
        postPoRepository.save(postPo);
        postPo = postPoRepository.findByIdAndIsDeletedFalse(id);
        if(postPo != null){
        jsonObject.put("message", "刪除失敗");
        return jsonObject.toJSONString();
        }
        jsonObject.put("message", "刪除成功");
        return jsonObject.toJSONString();
    }

    @Override
    public Page<PostDto> findPosts(Long id) throws ResourceNotFoundException {
        Optional<CategoryPo> optional = categoryPoRepository.findByIdAndIsDeletedFalse(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("該分類不存在");
        }
        CategoryPo categoryPo = optional.get();
        List<PostPo> posts = categoryPo.getPosts();

        List<PostDto> dtoList = new ArrayList<>();
        for (PostPo postPo : posts) {
            PostDto postDto = null;
            postDto = downloadImage(postPo);
            dtoList.add(postDto);
        }

        return PageConverter.covertToPage(dtoList, 1, dtoList.size());
    }

    @Override
    public PostDto findTheOnePostsByCategory(Long id, Long postId) throws ResourceNotFoundException {
        Optional<CategoryPo> optional = categoryPoRepository.findByIdAndIsDeletedFalse(id);
        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("該分類不存在");
        }
        CategoryPo categoryPo = optional.get();
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

    @Override
    public List<PostPo> findByCreateDateBefore(LocalDateTime nowDate) {
        return postPoRepository.findByCreateDateBefore(nowDate);
    }

    @Override
    public boolean existsByPostId(Long postId) {
        return postPoRepository.existsById(postId);
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
    public synchronized Long getLikeCount(String postId) {
        return postPoRepository.getLikeCount(Long.parseLong(postId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void addView(String postId) {
        postPoRepository.addView(Long.parseLong(postId));
    }

    @Override
    public synchronized Long getViewCount(String postId) {
        return postPoRepository.getViewCount(Long.parseLong(postId));
    }

    @Override
    public PostDto createDraft(PostDto postDto) {
        return null;
    }

    private PostDto downloadImage(PostPo postPo, CategoryPo categoryPo) {
        PostDto postDto = PostPoMapper.INSTANCE.toDto(postPo);
        postDto.setCategoryId(String.valueOf(categoryPo.getId()));
        if (postPo.getImageName() != null) {
            try {
                InputStream inputStream = awsS3ClientService.downloadFileFromS3Bucket(postPo.getImageName());
                byte[] image = (inputStream != null) ? IOUtils.toByteArray(inputStream) : null;
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
                InputStream inputStream = awsS3ClientService.downloadFileFromS3Bucket(postPo.getImageName());
                byte[] image = (inputStream != null) ? IOUtils.toByteArray(inputStream) : null;
                postDto.setImage(image);
            } catch (Exception e) {
                log.error("下載文章圖片失敗", e);
            }
        }
        return postDto;
    }

}
