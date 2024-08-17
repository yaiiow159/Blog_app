package com.blog.controller;

import com.blog.annotation.NoResubmit;
import com.blog.response.ResponseBody;
import com.blog.dto.PostDto;
import com.blog.exception.ResourceNotFoundException;
import com.blog.service.PostService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "文章相關功能", description = "文章相關功能")
@Slf4j
@RestController
@RequestMapping("/api/v1/posts")
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @GetMapping("/latest")
    @Operation(summary = "查詢最新文章",description = "查詢最新文章")
    public ResponseBody<List<PostDto>> getLatestPost(){
        List<PostDto> latestPosts;
        try {
            latestPosts = postService.findLatestPost();
            return new ResponseBody<>(true, "查詢成功",latestPosts , HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/popular")
    @Operation(summary = "查詢熱門文章",description = "查詢熱門文章")
    public ResponseBody<List<PostDto>> getPopularPost(){
        List<PostDto> popularPosts;
        try {
            popularPosts = postService.findPopularPost();
            return new ResponseBody<>(true, "查詢成功", popularPosts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/personal")
    @Operation(summary = "查詢個人文章",description = "查詢個人文章")
    public ResponseBody<List<PostDto>> getPersonalPost(){
        List<PostDto> personalPosts;
        try {
            personalPosts = postService.getPersonalPost();
            return new ResponseBody<>(true, "查詢成功", personalPosts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/favorite")
    @Operation(summary = "查詢個人收藏文章",description = "查詢個人收藏文章")
    public ResponseBody<List<PostDto>> getFavoritePost(){
        List<PostDto> favoritePosts;
        try {
            favoritePosts = postService.findFavoritePost();
            return new ResponseBody<>(true, "查詢成功", favoritePosts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "查詢文章",description = "利用id查詢文章")
    public ResponseBody<PostDto> getPost(@Parameter(description = "文章id",example = "1") @PathVariable Long id) {
        try {
            PostDto postDto = postService.findById(id);
            return new ResponseBody<>(true, "查詢成功", postDto, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/searchByKeyword")
    @Operation(summary = "關鍵字查詢文章",description = "關鍵字全文查詢文章")
    public ResponseBody<List<PostDto>> getByKeyword(@Parameter(description = "關鍵字",example = "關鍵字")@RequestParam(name = "keyword") String keyword) {
        List<PostDto> keyWordPosts;
        try {
            keyWordPosts = postService.findByKeyword(keyword);
            return new ResponseBody<>(true, "查詢成功", keyWordPosts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/searchByTag/{id}")
    @Operation(summary = "標籤查詢文章",description = "標籤查詢文章")
    public ResponseBody<List<PostDto>> getByTag(@Parameter(description = "標籤id",example = "1") @PathVariable Long id) {
        List<PostDto> tagPosts;
        try {
            tagPosts = postService.findByTag(id);
            return new ResponseBody<>(true, "查詢成功", tagPosts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping
    @Operation(summary = "查詢文章",description = "查詢文章")
    public ResponseBody<Page<PostDto>> getByPage(@Parameter(description = "標題",example = "這是一個標題")@RequestParam(name = "title",required = false) String title,
                                                 @Parameter(description = "作者",example = "Timmy") @RequestParam(name = "authorName",required = false) String authorName,
                                                 @Parameter(description = "作者郵箱") @RequestParam(name = "authorEmail",required = false) String authorEmail,
                                                 @Parameter(description = "頁數") @RequestParam(name = "page", defaultValue = "1",required = false) Integer page,
                                                 @Parameter(description = "大小",example = "10" ) @RequestParam(name = "pageSize",defaultValue = "10",required = false) Integer pageSize) {
        Page<PostDto> postDtoPage;
        try {
            postDtoPage = postService.findAll(page,pageSize,title,authorName,authorEmail);
            return new ResponseBody<>(true, "查詢成功", postDtoPage, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseBody<>(false, "查詢錯誤 錯誤原因: " + e.getMessage(),null, HttpStatus.BAD_REQUEST);
        }
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(value = "/draft", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "創建草稿API",description = "創建草稿API")
    public ResponseBody<PostDto> createDraft(@Validated @RequestBody PostDto postDto) {
        try {
            postService.saveDraft(postDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "創建失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "創建成功", HttpStatus.OK);
    }


    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "創建發布文章API",description = "創建發布文章API")
    public ResponseBody<PostDto> createPost (@RequestBody @Validated PostDto postDto) {
        try {
            postService.save(postDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "創建失敗, 失敗原因為" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "創建成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "更新發布文章API",description = "更新發布文章API")
    public ResponseBody<PostDto> updatePost(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "文章資訊")
            @RequestBody @Validated PostDto postDto)  {
        try {
            postService.update(postDto);
        } catch (Exception e) {
            return new ResponseBody<>(false, "更新失敗, 失敗原因為" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "更新成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "刪除發布文章API",description = "刪除發布文章API")
    public ResponseBody<String> deletePost(@Parameter(description = "刪除文章id",example = "1") @PathVariable Long id){
        try {
            postService.delete(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "刪除失敗, 失敗原因為" + e.getMessage(), null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "刪除成功", HttpStatus.OK);
    }


    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("{id}/views")
    @Operation(summary = "增加文章瀏覽次數API",description = "增加文章瀏覽次數API")
    public ResponseBody<String> addPostView(@Parameter(description = "文章id",example = "1") @PathVariable Long id) {
        try {
            postService.addView(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "增加失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "增加成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{id}/bookmarks")
    @Operation(summary = "取消收藏文章API",description = "取消收藏文章API")
    public ResponseBody<String> deleteBookmark(@Parameter(description = "取消收藏文章id",example = "1") @PathVariable Long id) {
        try {
            postService.deleteBookmark(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "取消收藏失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "取消收藏成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{id}/bookmarks")
    @Operation(summary = "收藏文章API",description = "收藏文章API")
    public ResponseBody<String> addBookmark(@Parameter(description = "收藏文章id",example = "1") @PathVariable Long id) throws ResourceNotFoundException {
        try {
            postService.addBookmark(id);
        } catch (Exception e) {
            return new ResponseBody<>(false, "收藏失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "收藏成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @PostMapping("/{postId}/like")
    @Operation(summary = "按讚文章API",description = "按讚文章API")
    public ResponseBody<String> addLike(@Parameter(description = "按讚文章id",example = "1") @PathVariable Long postId){
        try {
            postService.like(postId);
        } catch (Exception e) {
            return new ResponseBody<>(false, "按讚失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "按讚成功", HttpStatus.OK);
    }

    @NoResubmit(delaySecond = 3)
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @DeleteMapping("/{postId}/like")
    @Operation(summary = "取消按讚文章API",description = "取消按讚文章API")
    public ResponseBody<String> deleteLike(@Parameter(description = "取消按讚文章id",example = "1") @PathVariable Long postId) {
        try {
            postService.cancelLike(postId);
        } catch (Exception e) {
            return new ResponseBody<>(false, "取消按讚失敗", null, HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "取消按讚成功", HttpStatus.OK);
    }


    // likesCount
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{postId}/likesCount")
    @Operation(summary = "查詢案讚人數",description = "按讚人數API")
    public ResponseBody<Integer> getLikesCount(@Parameter(description = "文章id",example = "1") @PathVariable Long postId) {
        return new ResponseBody<>(true, "按讚人數", postService.getLikesCount(postId), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("{postId}/dislikesCount")
    @Operation(summary = "查詢倒讚人數",description = "倒讚人數API")
    public ResponseBody<Integer> getDislikesCount(@Parameter(description = "文章id",example = "1") @PathVariable Long postId) {
        return new ResponseBody<>(true, "倒讚人數", postService.getDislikesCount(postId), HttpStatus.OK);
    }

    // 查詢收藏人數
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{postId}/bookmarksCount")
    @Operation(summary = "查詢收藏人數",description = "收藏人數API")
    public ResponseBody<Integer> getBookmarksCount(@Parameter(description = "文章id",example = "1") @PathVariable Long postId) {
        return new ResponseBody<>(true, "收藏人數", postService.getBookmarksCount(postId), HttpStatus.OK);
    }

    @GetMapping("{postId}/viewsCount")
    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @Operation(summary = "查詢文章瀏覽次數",description = "查詢文章瀏覽次數")
    public ResponseBody<Long> getViewsCount(@Parameter(description = "文章id",example = "1") @PathVariable Long postId) {
        return new ResponseBody<>(true, "查詢成功", postService.getViewsCount(postId), HttpStatus.OK);
    }
}
