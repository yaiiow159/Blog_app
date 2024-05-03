package com.blog.controller;

import com.blog.dto.ApiResponse;
import com.blog.dto.ForgotPasswordDto;
import com.blog.dto.ResetPasswordRequest;
import com.blog.dto.UserDto;
import com.blog.exception.ValidateFailedException;
import com.blog.jwt.JwtRequestBody;
import com.blog.jwt.JwtResponseBody;
import com.blog.service.AuthService;
import com.blog.service.UserService;
import com.blog.utils.JwtTokenUtil;

import com.blog.utils.SpringSecurityUtils;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
import javax.naming.AuthenticationNotSupportedException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
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
    private final UserService userService;
    private final AuthService authService;
    private final JwtTokenUtil jwtTokenUtil;
    private final Producer producer;
    private final StringRedisTemplate stringRedisTemplate;
    private static final String JWT_TYPE = "Bearer";

    /**
     * 取得圖驗證碼
     * @return
     * @throws IOException
     */
    @Operation(summary = "取得驗證碼", description = "取得驗證碼", tags = {"驗證碼"})
    @GetMapping(value = "/captchaCode")
    public void getCaptcha(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("image/jpeg"); // 設定為回傳一個 jpg 檔案
        response.setHeader("Pragma", "No-cache");  // 禁止緩存
        response.setHeader("Cache-Control", "no-cache"); // 禁止緩存
        response.setDateHeader("Expires", 0);
        String capText = producer.createText();
        BufferedImage bi = producer.createImage(capText) ;
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

    @Operation(summary = "取得生成令牌", description = "取得生成令牌", tags = {"JWT認證相關功能"})
    @PostMapping(value = "/login")
    public ApiResponse<JwtResponseBody> getJwtToken(@Parameter(description = "帳號與密碼與電子郵件") @Validated @RequestBody JwtRequestBody jwtRequest) {
        // 驗證圖形驗證碼
        final String captchaCode = stringRedisTemplate.opsForValue().get(Constants.KAPTCHA_SESSION_KEY);
        //驗證圖形驗證碼
        if (Objects.equals(captchaCode, null) || !captchaCode.equalsIgnoreCase(jwtRequest.getCaptchaCode())) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.CAPTCHA_VALIDATION_ERROR, "驗證碼錯誤");
        }
        //取得驗證碼
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();
        //取得使用者權限
        Set<String> roles = SpringSecurityUtils.getCurrentUserAuthorities();
        //取得jwt令牌
        final String token = jwtTokenUtil.generateToken(authentication);
        JwtResponseBody res = JwtResponseBody.builder()
                .token(token)
                .type(JWT_TYPE)
                .account(user.getUsername())
                .roles(roles)
                .build();

        return new ApiResponse<>(true, "登入成功", res, HttpStatus.OK);
    }

    @PostMapping("/forgetPassword")
    @Operation(summary = "忘記密碼", description = "忘記密碼並發送電子郵件", tags = {"JWT認證相關功能"})
    public ApiResponse<String> forgotPassword(@Validated @RequestBody ForgotPasswordDto request) throws MessagingException, NoSuchAlgorithmException {
        authService.forgotPassword(request.getEmail());
        return new ApiResponse<>(true, "發送電子郵件成功", HttpStatus.OK);
    }

    @Operation(summary = "重設密碼", description = "重設密碼並更新密碼")
    @PostMapping("/resetPassword")
    public ApiResponse<String> resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return new ApiResponse<>(true, "重設密碼成功", HttpStatus.OK);
    }

    @PostMapping("/register")
    @Operation(summary = "註冊使用者", description = "註冊使用者並將使用者訊息返回給前端")
    public ApiResponse<String> register(@Parameter(description = "帳號與密碼") @Validated @RequestBody UserDto userDto) throws AuthenticationNotSupportedException {
        String result = userService.register(userDto);
        if(!result.equals("註冊成功")) {
            return new ApiResponse<>(false, "註冊失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "註冊成功", HttpStatus.OK);
    }

    @PostMapping("/logout")
    @Operation(summary = "登出", description = "登出")
    public ApiResponse<String> logout(@RequestHeader("Authorization") String token) throws AuthenticationNotSupportedException {
        final String jwtToken = token.substring(7);
        try {
            userService.logout(jwtToken);
        } catch (Exception e) {
            log.error("登出失敗", e);
            return new ApiResponse<>(false, "登出失敗", HttpStatus.BAD_REQUEST);
        }
        return new ApiResponse<>(true, "登出成功", HttpStatus.OK);
    }

}
