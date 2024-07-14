package com.blog.utils;

import com.blog.exception.JwtDomainException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Component
@Slf4j
public class JwtTokenUtil {

    @Value("${app.jwt-expiration-milliseconds}")
    public long expirationTime;

    @Value("${app.jwt-secret}")
    private String secret;

     /**
     * 簽發 token
     */
    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + expirationTime);

        Map<String,Object> claims = new HashMap<>();

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setId(UUIDUtil.getUUID32())
                .setExpiration(expireDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String generateRefreshToken(){
        Date currentDate = new Date();
        Date expireDate = new Date(currentDate.getTime() + expirationTime);
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setId(UUIDUtil.getUUID32())
                .setExpiration(expireDate)
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSignInKey(){
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(secret)
        );
    }

    /**
     * 取得令牌中的所有的聲明
     */
    // get username from Jwt token
    public String getUsername(String token){
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    // validate Jwt token
    public boolean validateToken(String token){
        try{
            Jwts.parserBuilder()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parse(token);
            return Boolean.TRUE;
        } catch (MalformedJwtException ex) {
            throw new JwtDomainException(HttpStatus.BAD_REQUEST,"不正確的jwt-token格式");
        } catch (ExpiredJwtException ex) {
            throw new JwtDomainException(HttpStatus.BAD_REQUEST,"jwt-token 已過期");
        } catch (UnsupportedJwtException ex) {
            throw new JwtDomainException(HttpStatus.BAD_REQUEST, "不支援的jwt-token類型");
        } catch (IllegalArgumentException ex) {
            throw new JwtDomainException(HttpStatus.BAD_REQUEST, "Jwt-token的payload為空");
        }
    }
}
