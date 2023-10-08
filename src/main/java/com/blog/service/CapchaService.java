package com.blog.service;

import com.google.code.kaptcha.Producer;

import java.io.IOException;
import java.util.Map;

public interface CapchaService {

    // 生成token
    Map<String,Object> createToken(String text);

    //生成capcha驗證碼
    Map<String, Object> capchaCreator() throws IOException;
    // 驗證驗證碼
    Boolean verifyCapchaCode(String text,String code);
}
