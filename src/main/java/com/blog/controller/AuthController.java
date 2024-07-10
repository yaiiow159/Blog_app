package com.blog.controller;

import com.blog.annotation.NoResubmit;
import com.blog.response.ResponseBody;
import com.blog.dto.ResetPasswordDto;
import com.blog.dto.UserDto;
import com.blog.exception.ValidateFailedException;
import com.blog.jwt.JwtRequestBody;
import com.blog.jwt.JwtResponseBody;
import com.blog.service.AuthService;
import com.blog.utils.JwtTokenUtil;

import com.blog.utils.SpringSecurityUtil;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;


@Tag(name = "JWT認證相關功能", description = "JWT 認證")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final Producer producer;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String JWT_TYPE = "Bearer";

    /*
     * 取得驗證碼
     *
     * @param response HttpServletResponse
     */
    @Operation(summary = "取得驗證碼", description = "取得驗證碼", tags = {"驗證碼"})
    @GetMapping(value = "/captchaCode")
    public void getCaptcha(HttpServletResponse response) {
        response.setContentType("image/jpeg"); // 設定為回傳一個 jpg 檔案
        response.setHeader("Pragma", "No-cache");  // 禁止緩存
        response.setHeader("Cache-Control", "no-cache"); // 禁止緩存
        response.setDateHeader("Expires", 0);
        String capText = producer.createText();
        BufferedImage bi = producer.createImage(capText);
        stringRedisTemplate.opsForValue().set(Constants.KAPTCHA_SESSION_KEY, capText);
        stringRedisTemplate.expire(Constants.KAPTCHA_SESSION_KEY, 60, TimeUnit.SECONDS);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            ImageIO.write(bi, "jpg", out); // 輸出圖片
            out.flush();
        } catch (Exception e) {
            log.error("取得驗證碼失敗", e);
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.CAPTCHA_VALIDATION_ERROR, "取得驗證碼失敗");
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    /**
     * 取得生成令牌
     *
     * @param jwtRequest 帳號與密碼與驗證碼
     * @return 生成令牌
     */
    @NoResubmit
    @Operation(summary = "取得生成令牌", description = "取得生成令牌")
    @PostMapping(value = "/login")
    public ResponseBody<JwtResponseBody> getJwtToken(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(required = true)
            @RequestBody @Validated JwtRequestBody jwtRequest) {
        // 驗證圖形驗證碼
        final String captchaCode = stringRedisTemplate.opsForValue().get(Constants.KAPTCHA_SESSION_KEY);
        //驗證圖形驗證碼
        if (Objects.equals(captchaCode, null) || !captchaCode.equalsIgnoreCase(jwtRequest.getCaptchaCode())) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.CAPTCHA_VALIDATION_ERROR, "驗證碼錯誤, 請重新輸入");
        }
        //取得驗證碼
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        //取得使用者權限
        Set<String> roles = SpringSecurityUtil.getCurrentUserAuthorities();
        //取得jwt令牌
        final String token = jwtTokenUtil.generateToken(authentication);
        JwtResponseBody res = JwtResponseBody.builder()
                .token(token)
                .type(JWT_TYPE)
                .account(user.getUsername())
                .roles(roles)
                .build();

        return new ResponseBody<>(true, "登入成功", res, HttpStatus.OK);
    }

    /**
     * 忘記密碼功能 - 發送電子郵件至 指定前端畫面
     *
     * @param email 電子郵件
     * @return 發送結果
     */
    @NoResubmit
    @PostMapping("/forgetPassword")
    @Operation(summary = "忘記密碼", description = "忘記密碼並發送電子郵件", tags = {"JWT認證相關功能"})
    public ResponseBody<String> forgotPassword(@Parameter(description = "電子郵件") @NotBlank(message = "電子郵件不可為空") String email) {
        try {
            authService.forgotPassword(email);
        } catch (Exception e) {
            log.error("發送電子郵件失敗 失敗原因 : {}", e.getMessage());
            return new ResponseBody<>(false, "發送電子郵件失敗, 請聯繫管理員", HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "發送電子郵件成功", HttpStatus.OK);
    }


    /**
     * 重設密碼功能
     *
     * @param request 重設密碼資訊
     * @return 重設密碼結果
     */
    @NoResubmit
    @Operation(summary = "重設密碼", description = "重設密碼並更新密碼")
    @PostMapping("/resetPassword")
    public ResponseBody<String> resetPassword(@RequestBody @Validated ResetPasswordDto request) {
        try {
            authService.resetPassword(request.getToken(), request.getNewPassword());
        } catch (Exception e) {
            log.error("重設密碼失敗 失敗原因 : {}", e.getMessage());
            return new ResponseBody<>(false, "重設密碼失敗, 請聯繫管理員", HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "重設密碼成功", HttpStatus.OK);
    }

    /**
     * 註冊使用者
     *
     * @param userDto 帳號與密碼
     * @return 註冊結果
     */
    @NoResubmit
    @PostMapping("/register")
    @Operation(summary = "註冊使用者", description = "註冊使用者並將使用者訊息返回給前端")
    public ResponseBody<String> register(@Parameter(description = "帳號與密碼") @RequestBody @Validated UserDto userDto) {
        try {
            authService.register(userDto);
        } catch (Exception e) {
            log.error("註冊失敗 失敗原因 : {}", e.getMessage());
            return new ResponseBody<>(false, "註冊失敗, 請聯繫管理員", HttpStatus.BAD_REQUEST);
        }
        return new ResponseBody<>(true, "註冊成功", HttpStatus.OK);
    }

}
