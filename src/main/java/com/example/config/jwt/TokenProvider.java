package com.example.config.jwt;

import com.example.controller.dto.jwt.TokenDto;
import com.example.exception.CustomException;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

import static com.example.exception.ErrorCode.INVALID_ACCESS_TOKEN;

@Slf4j
@Component
public class TokenProvider {
    public static String BEARER_TYPE;
    public static String AUTHORIZATION_HEADER;
    public static String AUTHORITIES_KEY;
    public static String SECRET_KEY;
    public static long ACCESS_TOKEN_EXPIRE_TIME;
    public static long REFRESH_TOKEN_EXPIRE_TIME;

    private final Key key;

    public TokenProvider(
            @Value("${jwt.bearer_type}") String bearerType,
            @Value("${jwt.authorization_header}") String authorizationHeader,
            @Value("${jwt.authorities_key}") String authoritiesKey,
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access_token_expire_time}") long accessTokenExpireTime,
            @Value("${jwt.refresh_token_expire_time}") long refreshTokenExpireTime) {

        BEARER_TYPE = bearerType;
        AUTHORIZATION_HEADER = authorizationHeader;
        AUTHORITIES_KEY = authoritiesKey;
        SECRET_KEY = secretKey;
        ACCESS_TOKEN_EXPIRE_TIME = 1000 * 60 * accessTokenExpireTime;
        REFRESH_TOKEN_EXPIRE_TIME = 1000 * 60 * refreshTokenExpireTime;

        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(SECRET_KEY);
        this.key = new SecretKeySpec(apiKeySecretBytes, SignatureAlgorithm.HS256.getJcaName());
    }

    public TokenDto generateTokenDto(Authentication authentication) {
        // 권한들 가져오기
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = System.currentTimeMillis();

        // Access Token 생성
        Date accessTokenExpiresIn = new Date(now + ACCESS_TOKEN_EXPIRE_TIME);
        String accessToken = Jwts.builder()
                .setSubject(authentication.getName())
                .claim(AUTHORITIES_KEY, authorities)
                .setExpiration(accessTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        // Refresh Token 생성
        Date refreshTokenExpiresIn = new Date(now + REFRESH_TOKEN_EXPIRE_TIME);
        String refreshToken = Jwts.builder()
                .setExpiration(refreshTokenExpiresIn)
                .signWith(SignatureAlgorithm.HS256, key)
                .compact();

        return TokenDto.builder()
                .grantedType(BEARER_TYPE)
                .accessToken(accessToken)
                .accessTokenExpiresIn(accessTokenExpiresIn.getTime())
                .refreshToken(refreshToken)
                .build();
    }

    public Authentication getAuthentication(String accessToken) {
        // 토큰 복호화
        Claims claims = parseClaims(accessToken);
        if (claims.get(AUTHORITIES_KEY) == null) {
            throw new CustomException(INVALID_ACCESS_TOKEN);
        }

        // 클레임에서 권한 정보 가져오기
        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // UserDetails 객체를 만들어서 Authentication 리턴
        UserDetails principal = new User(claims.getSubject(), "", authorities);
        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(key).parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException exception) {
            log.error("Invalid JWT signature. token : {},", token);
        } catch (ExpiredJwtException exception) {
            log.info("Expired JWT token. token : {}, token");
        } catch (UnsupportedJwtException exception) {
            log.info("Unsupported JWT token. token : {}, token");
        } catch (IllegalArgumentException exception) {
            log.info("Invalid JWT token. token : {}, token");
        }
        return false;
    }

    private Claims parseClaims(String accessToken) {
        try {
            return Jwts.parser()
                    .setSigningKey(key)
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        }
    }
}