package com.blog.service.impl;

import com.blog.annotation.Notification;
import com.blog.dao.CategoryPoRepository;
import com.blog.dao.PostPoRepository;
import com.blog.dao.TagPoRepository;
import com.blog.dto.PostDto;

import com.blog.enumClass.PostStatusEnum;
import com.blog.mapper.CategoryPoMapper;
import com.blog.mapper.PostPoMapper;
import com.blog.mapper.TagPoMapper;
import com.blog.po.CategoryPo;
import com.blog.po.PostPo;
import com.blog.po.TagPo;
import com.blog.service.PostService;

import com.blog.utils.SpringSecurityUtil;
import jakarta.mail.MethodNotSupportedException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;

import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;

import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    private final CategoryPoRepository categoryPoRepository;
    private final PostPoRepository postPoRepository;
    private final TagPoRepository tagPoRepository;
    private final EntityManager entityManager;
    private static final Logger logger = LoggerFactory.getLogger(PostServiceImpl.class);
    /**
     * 新增文章
     *
     * @param postDto 文章資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    @Notification(operation = "add", operatedClass = PostPo.class)
    public void save(PostDto postDto) throws Exception {
        if (postDto == null) {
            throw new IllegalArgumentException("請輸入文章資訊");
        }
        // 建立與分類關聯關係
        CategoryPo categoryPo = categoryPoRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該分類序號" + postDto.getCategoryId() + "的資料"));
        PostPo postPo = PostPoMapper.INSTANCE.toPo(postDto);
        // 建立與標籤關聯關係
        List<Long> tagIds = postDto.getTagIds();
        if (!tagIds.isEmpty()) {
            List<TagPo> tagPos = tagPoRepository.findAllById(tagIds);
            postPo.setTags(new HashSet<>(tagPos));
        } else {
            throw new IllegalArgumentException("至少選擇一個標籤 ");
        }
        postPo.setCategory(categoryPo);
        logger.debug("新增文章中....: {}", postPo);
        postPoRepository.saveAndFlush(postPo);
        logger.debug("新增文章成功, id: {}", postPo.getId());
    }

    /**
     * 編輯文章
     *
     * @param postDto 文章資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    @Notification(operation = "edit", operatedClass = PostDto.class)
    public void update(PostDto postDto) throws Exception {
        if (postDto == null) {
            throw new IllegalArgumentException("請輸入文章資訊");
        }
        // 建立與分類關聯關係
        CategoryPo categoryPo = categoryPoRepository.findById(postDto.getCategoryId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該分類序號" + postDto.getCategoryId() + "的資料"));
        PostPo postPo = postPoRepository.findById(postDto.getId())
                .orElseThrow(() -> new EntityNotFoundException("找不到該文章序號" + postDto.getId() + "的資料"));
        // 更新文章
        postPo = PostPoMapper.INSTANCE.partialUpdate(postDto, postPo);
        // 建立與標籤關聯關係
        List<Long> tagIds = postDto.getTagIds();
        if (!tagIds.isEmpty()) {
            List<TagPo> tagPos = tagPoRepository.findAllById(tagIds);
            postPo.setTags(new HashSet<>(tagPos));
        } else {
            throw new IllegalArgumentException("至少選擇一個標籤 ");
        }
        postPo.setCategory(categoryPo);
        logger.debug("更新文章中.... {}", postPo);
        postPoRepository.saveAndFlush(postPo);
        logger.debug("更新文章成功, id: {}", postPo.getId());
    }

    /**
     * 刪除文章 (暫不提供)
     *
     * @param postDto 文章資訊
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(PostDto postDto) throws Exception {
        throw new MethodNotSupportedException("刪除文章不支援此方法");
    }

    /**
     * 刪除文章
     *
     * @param id 文章序號
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public void delete(Long id) throws Exception {
        if(id == null) {
            throw new IllegalArgumentException("請輸入文章序號");
        }
        try {
            logger.info("刪除文章中.... {}", id);
            postPoRepository.deleteById(id);
        } catch (Exception e) {
            logger.error("刪除文章時遭遇錯誤: {}", e.getMessage());
            throw new IllegalArgumentException("刪除文章時遭遇錯誤");
        }
    }

    /**
     * 搜尋指定序號的文章
     *
     * @param id 文章序號
     * @return PostDto 文章資訊
     */
    @Override
    public PostDto findById(Long id) {
        PostPo postPo = postPoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("找不到該文章序號" + id + "的資料"));
        return PostPoMapper.INSTANCE.toDto(postPo);
    }

    /**
     * 搜詢所有文章
     *
     * @return List<PostDto> 文章集合
     * @throws Exception 遭遇異常時拋出
     */
    @Override
    public List<PostDto> findAll() throws Exception {
        return postPoRepository.findAll().stream().map(PostPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 搜尋分頁文章
     *
     * @param page 當前頁數
     * @param pageSize 每頁顯示筆數
     * @return Page<PostDto> 文章分頁集合
     * @throws Exception  遭遇異常時拋出
     */
    @Override
    public Page<PostDto> findAll(Integer page, Integer pageSize) throws Exception {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return postPoRepository.findAll(pageable).map(PostPoMapper.INSTANCE::toDto);
    }


    /**
     * 增加按讚數
     *
     * @param id  文章序號
     */
    @Override
    @Cacheable(value = "like", key = "#id")
    public  void like(Long id) {
        postPoRepository.addLike(id);
    }

    /**
     * 減少按讚數
     *
     * @param id 文章序號
     */
    @Override
    @Cacheable(value = "disLike", key = "#id")
    public void cancelLike(Long id) {
        postPoRepository.disLike(id);
    }

    /**
     * 查詢文章按讚數
     *
     * @param id 文章序號
     * @return List<PostDto> 文章按讚數集合
     */
    @Override
    @CacheEvict(value = "like", key = "#id")
    public Integer queryLikeCount(Long id) {
        return postPoRepository.getLikeCount(id);
    }

    /**
     * 搜尋最新文章
     *
     * @return List<PostDto> 最新文章
     */
    @Override
    public List<PostDto> findLatestPost() {
        return postPoRepository.findTop10ByOrderByIdDesc().stream().map(PostPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 搜尋 最熱門文章
     *
     * @return List<PostDto> 最熱門文章
     */
    @Override
    public List<PostDto> findPopularPost() {
        return postPoRepository.findPopularPost().stream().map(PostPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 取得個人文章
     *
     * @return List<PostDto> 個人文章
     */
    @Override
    public List<PostDto> getPersonalPost() {
        return postPoRepository.getPersonalPost(SpringSecurityUtil.getCurrentUser()).stream().map(PostPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 取得個人收藏文章
     *
     * @return List<PostDto> 個人收藏
     */
    @Override
    public List<PostDto> findFavoritePost() {
        return postPoRepository.getFavoritePost(SpringSecurityUtil.getCurrentUser()).stream().map(PostPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 搜尋符合關鍵字的文章集合˙
     *
     * @param keyword 關鍵字
     * @return List<PostDto> 符合關鍵字的文章集合
     */
    @Override
    public List<PostDto> findByKeyword(String keyword) {
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
            logger.error("搜尋文章失敗", e);
            return Collections.emptyList();
        }
    }

    /**
     * 搜尋文章
     *
     * @param id 文章序號
     * @return List<PostDto> 文章標籤集合
     */
    @Override
    public List<PostDto> findByTag(Long id) {
        return postPoRepository.findByTag(id).stream().map(PostPoMapper.INSTANCE::toDto).collect(Collectors.toList());
    }

    /**
     * 搜尋符合查詢條件的文章分頁集合
     *
     * @param page 當前頁數
     * @param pageSize 每頁筆數
     * @param title 文章標題
     * @param authorName 作者
     * @return Page<PostDto> 文章分頁
     */
    @Override
    public Page<PostDto> findAll(Integer page, Integer pageSize, String title, String authorName,String authorEmail) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        Specification<PostPo> specification = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.hasText(title)) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (StringUtils.hasText(authorName)) {
                predicates.add(criteriaBuilder.like(root.get("authorName"), "%" + authorName + "%"));
            }
            if (StringUtils.hasText(authorEmail)) {
                predicates.add(criteriaBuilder.like(root.get("authorEmail"), "%" + authorEmail + "%"));
            }
            return query.where(predicates.toArray(new Predicate[0])).getRestriction();
        };
        Page<PostPo> postPos = postPoRepository.findAll(specification, pageable);
        return postPos.map(PostPoMapper.INSTANCE::toDto);
    }

    /**
     * 取消 文章收藏
     *
     * @param id 文章序號
     */
    @Override
    public void deleteBookmark(Long id) {
        postPoRepository.deleteBookmark(id);
    }

    /**
     * 增加 文章收藏
     *
     * @param id 文章序號
     */
    @Override
    public void addBookmark(Long id) {
        postPoRepository.addBookmark(id);
    }

    /**
     * 儲存草稿
     *
     * @param postDto 草稿文章資訊
     */
    @Override
    public void saveDraft(PostDto postDto) {
        postDto.setStatus(PostStatusEnum.DRAFT.getStatus());
        postPoRepository.saveAndFlush(PostPoMapper.INSTANCE.toPo(postDto));
    }

    /**
     * @param id 文章序號
     */
    @Override
    public void addView(Long id) {
        postPoRepository.addPostView(id);
    }

    /**
     * 取得文章總讚數
     *
     * @param postId 文章序號
     * @return 文章讚數
     */
    @Override
    public Integer getDislikesCount(Long postId) {
        return postPoRepository.getDislikeCount(postId);
    }

    /**
     * 取得文章總收藏數
     *
     * @param postId 文章序號
     * @return 文章收藏數
     */
    @Override
    public Integer getBookmarksCount(Long postId) {
        return postPoRepository.getBookmarkCount(postId);
    }

    /**
     * 取得文章總瀏覽數
     *
     * @param postId 文章序號
     * @return 文章瀏覽數
     */
    @Override
    public Long getViewsCount(Long postId) {
        return postPoRepository.getViewsCountById(postId);
    }

    /**
     * 取得文章總讚數
     *
     * @param postId 文章序號
     * @return 文章讚數
     */
    @Override
    public Integer getLikesCount(Long postId) {
        return postPoRepository.getLikeCount(postId);
    }
}

