package com.blog.service.impl;

import com.blog.dao.RolePoRepository;
import com.blog.dao.UserGroupPoRepository;
import com.blog.dao.UserPoRepository;
import com.blog.dto.UserDto;
import com.blog.dto.UserGroupDto;
import com.blog.enumClass.GroupAuthEnum;
import com.blog.enumClass.UserRoleEnum;
import com.blog.exception.ValidateFailedException;
import com.blog.mapper.UserPoMapper;
import com.blog.po.RolePo;
import com.blog.po.UserGroupPo;
import com.blog.po.UserPo;
import com.blog.service.AuthService;
import com.blog.utils.UUIDUtil;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {

    private final StringRedisTemplate stringRedisTemplate;
    private final UserPoRepository userJpaRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final JavaMailSender javaMailSender;
    private final RolePoRepository rolePoRepository;
    private final UserGroupPoRepository userGroupPoRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    /**
     * 重設密碼，保存至db當中
     *
     * @param token 重設密碼的token
     * @param newPassword 新密碼
     */
    @Override
    @Transactional
    public void resetPassword(final String token,final String newPassword){
        // 比對存放在redis中的token
        String email = stringRedisTemplate.opsForValue().get(token);
        UserPo userPo = userJpaRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("找不到使用者 " + email));
        userPo.setPassword(passwordEncoder.encode(newPassword));

        logger.debug("新密碼(hash加鹽): " + newPassword);

        userJpaRepository.saveAndFlush(userPo);
        // 更新spring-security的數據
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPo.getUserName());
        // 生成新的usernamePasswordAuthenticationToken
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        logger.info("重設密碼 " + userPo.getUserName() + " 成功");
    }

    /**
     * 重設密碼信件發送
     *
     * @param email 電子郵件
     */
    @Override
    public void forgotPassword(final String email) throws MessagingException, UsernameNotFoundException {
        userJpaRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("找不到該使用者對應的電子郵件" + email));
        String token = UUIDUtil.getUUID32();
        stringRedisTemplate.opsForValue().set(token, email, 5, TimeUnit.MINUTES);

        final String content =
                "<a target='_blank' style='color: #1a73e8; text-decoration: none; font-weight: bold' href='http://localhost:3030/api/v1/auth/reset?token=" + token + "'>點擊這裡重置密碼</a>";

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(javaMailSender.createMimeMessage());
        mimeMessageHelper.setTo(email);
        mimeMessageHelper.setSubject("重置密碼");
        mimeMessageHelper.setText(content, true);

        javaMailSender.send(mimeMessageHelper.getMimeMessage());

        logger.info("重設密碼信件發送至 " + email);
    }

    /**
     * 註冊會員功能
     *
     * @param userDto  使用者資訊
     *
     * @throws Exception  遇到異常時通一拋出
     */
    @Override
    @Transactional
    public void register(UserDto userDto) {
        if (!validateUserInfo(userDto)) {
            logger.error("註冊失敗, 請檢查使用者資訊");
            throw new ValidateFailedException("註冊失敗, 請檢查使用者資訊");
        }
        if(userJpaRepository.findByUserNameOrEmail(userDto.getUserName(), userDto.getEmail()).isPresent()) {
            logger.error("註冊失敗, 使用者名稱或電子郵件已經被註冊");
            throw new ValidateFailedException("註冊失敗, 使用者名稱或電子郵件已經被註冊");
        }
        logger.info("註冊使用者: " + userDto.getUserName());
        UserPo userPo = UserPoMapper.INSTANCE.toPo(userDto);

        // 設置預設角色
        if(userPo.getRoles() == null) {
            RolePo defaultRolePo = rolePoRepository.findByName(UserRoleEnum.ROLE_USER.getRoleName()).orElseThrow(() ->
                    new ValidateFailedException("註冊失敗, 未找到預設的使用者角色"));
            HashSet<RolePo> rolePos = new HashSet<>();
            rolePos.add(defaultRolePo);
            userPo.setRoles(rolePos);
        }

        // 設置預設群組
        if(userPo.getUserGroupPo() == null) {
            UserGroupPo defaultUserGroupPo =
                    userGroupPoRepository.findByGroupName(GroupAuthEnum.USER.name())
                            .orElseThrow(() -> new ValidateFailedException("註冊失敗, 未找到預設的使用者群組"));
            userPo.setUserGroupPo(defaultUserGroupPo);
        }

        userPo.setPassword(passwordEncoder.encode(userDto.getPassword()));
        userPo = userJpaRepository.saveAndFlush(userPo);
        // 更新spring-security中的數據
        UserDetails userDetails = userDetailsService.loadUserByUsername(userPo.getUserName());
        // 生成新的usernamePasswordAuthenticationToken
        var usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        logger.info("註冊 " + userPo.getUserName() + " 成功");
    }

    private boolean validateUserInfo(UserDto userDto) {
        return Objects.nonNull(userDto) && Objects.nonNull(userDto.getEmail()) && Objects.nonNull(userDto.getPassword());
    }

}
