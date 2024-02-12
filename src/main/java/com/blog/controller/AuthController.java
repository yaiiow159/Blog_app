package com.blog.controller;

import com.blog.dto.ForgotPasswordRequest;
import com.blog.dto.ResetPasswordRequest;
import com.blog.dto.UserDto;
import com.blog.exception.ValidateFailedException;
import com.blog.jwt.JwtRequestBody;
import com.blog.jwt.JwtResponseBody;
import com.blog.service.AuthService;
import com.blog.service.CapchaService;
import com.blog.service.UserService;
import com.blog.utils.JwtTokenUtil;

import com.blog.utils.SpringSecurityUtils;
import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.Producer;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.FastByteArrayOutputStream;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.imageio.ImageIO;
import javax.naming.AuthenticationNotSupportedException;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


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
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.CAPTCHA_IMAGE_ERROR, "取得驗證碼失敗");
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    @Operation(summary = "取得生成令牌", description = "取得生成令牌", tags = {"JWT認證相關功能"})
    @PostMapping(value = "/login")
    public ResponseEntity<JwtResponseBody> getJwtToken(@Parameter(description = "帳號與密碼與電子郵件") @Validated @RequestBody JwtRequestBody jwtRequest) {
        // 驗證圖形驗證碼
        final String captchaCode = stringRedisTemplate.opsForValue().get(Constants.KAPTCHA_SESSION_KEY);
        //驗證圖形驗證碼
        if (Objects.equals(captchaCode, null) || !captchaCode.equalsIgnoreCase(jwtRequest.getCaptchaCode())) {
            throw new ValidateFailedException(ValidateFailedException.DomainErrorStatus.CAPTCHA_CODE_ERROR, "驗證碼錯誤");
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

        return ResponseEntity.ok(res);
    }

    @PostMapping("/forget")
    @Operation(summary = "忘記密碼", description = "忘記密碼並發送電子郵件", tags = {"JWT認證相關功能"})
    public ResponseEntity<String> forgotPassword(@Validated @RequestBody ForgotPasswordRequest request) throws MessagingException, NoSuchAlgorithmException {
        authService.forgotPassword(request.getEmail());
        return ResponseEntity.ok("重設密碼電子郵件已發送至 " + request.getEmail());
    }

    @Operation(summary = "重設密碼", description = "重設密碼並更新密碼")
    @PostMapping("/reset")
    public ResponseEntity<String> resetPassword(@Validated @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request.getToken(), request.getNewPassword());
        return ResponseEntity.ok("密碼重設成功");
    }

    @PostMapping("/register")
    @Operation(summary = "註冊使用者", description = "註冊使用者並將使用者訊息返回給前端")
    public ResponseEntity<String> register(@Parameter(description = "帳號與密碼") @Validated @RequestBody UserDto userDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(userDto));
    }

    @PostMapping("/logout")
    @Operation(summary = "登出", description = "登出")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) throws AuthenticationNotSupportedException {
        final String jwtToken = token.substring(7);
        return ResponseEntity.status(HttpStatus.OK).body(userService.logout(jwtToken));
    }

}
