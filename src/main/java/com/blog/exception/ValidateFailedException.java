package com.blog.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class ValidateFailedException extends RuntimeException {

    public ValidateFailedException(String message){
        super(message);
    }
        private final List<DomainErrorStatus> errors = new ArrayList<>();

    public ValidateFailedException(DomainErrorStatus... errors) {
            super("Validation failed");
            if (errors != null && errors.length > 0)
                this.errors.addAll(Arrays.asList(errors));
        }

    public ValidateFailedException(DomainErrorStatus error, String message) {
        super("Validation failed");
        error.setMessage(message);
        errors.add(error);
    }

    public void addError(DomainErrorStatus error){
        errors.add(error);
    }

    public void addErrors(List<DomainErrorStatus> errors){
        this.errors.addAll(errors);
    }

    @Getter
    @JsonFormat(shape = JsonFormat.Shape.OBJECT)
    public enum DomainErrorStatus {

        CAPTCHA_VALIDATION_ERROR("J001","圖形驗證碼錯誤"),
        JWT_AUTHENTICATION_ACCESS_ERROR("J002","JWT認證時發生錯誤"),
        JWT_AUTHENTICATION_REFRESH_ERROR("J003","授權認證時刷新錯誤"),
        JWT_AUTHENTICATION_TOKEN_EXPIRED("J004","JWT-token已過期"),
        BAD_CREDENTIALS("J005","帳號密碼不正確，或是密碼未加密"),

        RESOURCE_NOT_FOUND("RE001","資源不存在"),
        RESOURCE_ALREADY_EXISTS("RE002","資源已存在"),
        RESOURCE_INVALID("RE003","資源無效"),
        RESOURCE_DELETED("RE004","資源已刪除"),
        RESOURCE_LOCKED("RE005","資源已被鎖定"),
        RESOURCE_CANNOT_DELETE("RE006","資源不能被刪除"),
        RESOURCE_CANNOT_UPDATE("RE007","更新物件時發生錯誤"),
        RESOURCE_CANNOT_CREATE("RE008","創建物件時發生錯誤"),
        RESOURCE_CANNOT_GET("RE009","取得物件時發生錯誤"),
        RESOURCE_IS_EMPTY("RE010", "資源不得為空"),

        CATEGORY_HAS_POSTS("C001", "分類已有文章"),
        REVIEW_LEVEL_IS_EMPTY("V001", "覆核權限等級不得為空"),
        USER_ALREADY_EXISTS("U001","用戶已存在"),
        EMAIL_ALREADY_EXISTS("U002","電子郵箱已存在"),

        USER_REGISTER_FAILED("U003", "註冊失敗"),
        USER_LOCKED("U004", "用戶已被鎖定"),;
        private String message;
        private final String code;

        DomainErrorStatus(String code, String message) {
            this.message = message;
            this.code = code;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}

