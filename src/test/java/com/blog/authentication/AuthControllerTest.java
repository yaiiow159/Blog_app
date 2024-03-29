//package com.blog.authentication;
//
//import com.alibaba.fastjson2.JSONObject;
//import com.alibaba.fastjson2.annotation.JSONBuilder;
//import com.blog.controller.AuthController;
//import com.blog.dto.UserDto;
//import com.blog.jwt.JwtRequestBody;
//import com.blog.service.CapchaService;
//import com.blog.service.UserService;
//import com.blog.utils.JwtTokenUtil;
//import org.aspectj.lang.annotation.Before;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.Mockito;
//import org.mockito.junit.MockitoJUnitRunner;
//import org.mockito.junit.jupiter.MockitoExtension;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.test.context.event.annotation.BeforeTestClass;
//import org.springframework.test.context.event.annotation.BeforeTestMethod;
//import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.util.*;
//
//import static org.mockito.Mockito.*;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@ExtendWith(MockitoExtension.class)
//public class AuthControllerTest {
//
//    @InjectMocks
//    private AuthController authController;
//
//    @Mock
//    private AuthenticationManager authenticationManager;
//
//    @Mock
//    private CapchaService capchaService;
//
//    @Mock
//    private JwtTokenUtil jwtTokenUtil;
//
//    @Mock
//    private UserService userService;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    public void setUp() {
//        mockMvc = MockMvcBuilders
//                .standaloneSetup(authController)
//                .build();
//    }
//
//    @Test
//    public void testGetCaptcha() throws Exception {
//        Map<String,Object> map = new HashMap<>();
//        map.put("code", "test");
//        when(capchaService.capchaCreator()).thenReturn(map);
//
//        mockMvc.perform(post("/api/v1/auth/captcha"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.code").value("test"));
//
//        verify(capchaService, times(1)).capchaCreator();
//    }
//
//    @Test
//    public void testGetJwtToken() throws Exception {
//        JwtRequestBody jwtRequest = new JwtRequestBody();
//        jwtRequest.setUsername("testuser");
//        jwtRequest.setPassword("testpassword");
//        jwtRequest.setCaptchaCode("testcode");
//        jwtRequest.setCaptchaKey("testkey");
//        jwtRequest.setEmail("testemail");
//        //模擬 驗證圖形驗證碼
//        when(capchaService.verifyCapchaCode(jwtRequest.getCaptchaKey(), jwtRequest.getCaptchaCode())).thenReturn(true);
//        // 模擬 authenticationManager 的行為
//        // 模擬AuthenticationManager的行為
//        // 创建一个 UserDetails 对象，用于模拟通过认证的用户
//        UserDetails userDetails = new User("test", "test", new ArrayList<>());
//
//        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, "test");
//        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
//        //模擬 jwtTokenUtil 的行為
//        when(jwtTokenUtil.generateToken(any(Authentication.class))).thenReturn("mocked-jwt-token");
//        // 发起POST请求
//        mockMvc.perform(post("/api/v1/auth/login")
//                        .content(JSONObject.toJSONString(jwtRequest))
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.account").value(userDetails.getUsername()))
//                .andExpect(jsonPath("$.email").value(jwtRequest.getEmail()))
//                .andExpect(jsonPath("$.type").value("Bearer"))
//                .andExpect(jsonPath("$.roles").isArray())
//                .andExpect(jsonPath("$.token").value("mocked-jwt-token"));
//        verify(authenticationManager, times(1)).authenticate(any());
//    }
//
//    @Test
//    public void testLogout() throws Exception {
//        String mockToken = "mocked-jwt-token";
//        String authorizationHeader = "Bearer " + mockToken;
//        String json = "{\"message\":\"logout successful\"}";
//        when(userService.logout(eq(mockToken))).thenReturn(json);
//        // 发起POST请求
//        mockMvc.perform(post("/api/v1/auth/logout")
//                        .header("Authorization", authorizationHeader))
//                .andExpect(status().isOk())
//                .andExpect(content().json("{\"message\":\"登出成功\"}"));
//        verify(userService, times(1)).logout(any());
//    }
//
//}
