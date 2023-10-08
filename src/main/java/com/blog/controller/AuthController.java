package com.blog.controller;

import com.blog.dto.UserDto;
import com.blog.jwt.JwtRequestBody;
import com.blog.jwt.JwtResponseBody;
import com.blog.service.CapchaService;
import com.blog.service.UserService;
import com.blog.utils.JwtTokenUtil;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.naming.AuthenticationNotSupportedException;
import java.io.IOException;
import java.util.Map;


@Tag(name = "JWT認證相關功能", description = "JWT 認證")
@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final CapchaService capchaService;
    private final UserService userService;
    private final JwtTokenUtil jwtTokenUtil;
    private static final String JWT_TYPE = "Bearer";

    @Operation(summary = "取得驗證碼", description = "取得驗證碼")
    @PostMapping(value = "/captcha")
    public ResponseEntity<Map<String,Object>> getCaptcha() throws IOException {
        Map<String, Object> creator = capchaService.capchaCreator();
        return ResponseEntity.ok(creator);
    }

    @Operation(summary = "取得生成令牌", description = "取得生成令牌")
    @PostMapping(value = "/login")
    public ResponseEntity<JwtResponseBody> getJwtToken(@Parameter(description = "帳號與密碼與電子郵件") @Validated @RequestBody JwtRequestBody jwtRequest) {
        // 驗證碼驗碼
        if (!capchaService.verifyCapchaCode(jwtRequest.getToken(), jwtRequest.getCapchaCode())) {
            MultiValueMap<String, String> error = new HttpHeaders();
            error.add("captchaCode", "驗證碼錯誤");
            return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
        }

        //驗證是否登入成功
        Authentication authentication =
                authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(jwtRequest.getUsername(), jwtRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User user = (User) authentication.getPrincipal();

        //取得jwt令牌
        final String token = jwtTokenUtil.generateToken(authentication);
        JwtResponseBody res = JwtResponseBody.builder()
                .token(token)
                .type(JWT_TYPE)
                .account(user.getUsername())
                .build();

        return ResponseEntity.ok(res);
    }

    @PostMapping("/register")
    @Operation(summary = "註冊使用者", description = "註冊使用者並將使用者訊息返回給前端")
    public ResponseEntity<UserDto> register(@Parameter(description = "帳號與密碼") @Validated @RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.register(userDto), HttpStatus.CREATED);
    }

    @PostMapping("/logout")
    @Operation(summary = "登出", description = "登出")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token) throws AuthenticationNotSupportedException {
        String jwtToken = token.substring(7);
        String result = userService.logout(jwtToken);
        return new ResponseEntity<>(result,HttpStatus.OK);
    }

}
