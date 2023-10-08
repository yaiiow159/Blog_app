package com.blog.converter;


import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter
public class CharJdbcTypeConverter implements AttributeConverter<Character, String> {

    @Override
    public String convertToDatabaseColumn(Character attribute) {
        // 實體屬性到資料庫字段的轉換邏輯
        return String.valueOf(attribute);
    }

    @Override
    public Character convertToEntityAttribute(String dbData) {
        // 資料庫字段到實體屬性的轉換邏輯
        return dbData.charAt(0);
    }
}