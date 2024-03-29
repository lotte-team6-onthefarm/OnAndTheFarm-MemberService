package com.team6.onandthefarmmemberservice.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Base64;
import java.util.Date;

@Slf4j
@Component
public class JwtTokenUtil {

    private final Key secretKey;

    private Long tokenPeriod; //60분*24시간=1440분, 86400000 = 1000L * 60L * 1440L

    private Long refreshPeriod; //7,776,000,000 = 1000L * 60L * 60L * 24L * 30L * 3L

    public static final String TOKEN_PREFIX = "Bearer ";

    Environment env;


    @Autowired
    public JwtTokenUtil(Environment env) {
        this.env = env;

        String secretKey = env.getProperty("custom-api-key.jwt.secret");
        tokenPeriod = Long.parseLong(env.getProperty("custom-api-key.jwt.token-period"));
        refreshPeriod = Long.parseLong(env.getProperty("custom-api-key.jwt.refresh-token-period"));

        // secretKey 바이트로 변환하여 Base64로 인코딩
        String encodingSecretKey = Base64.getEncoder().encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));
        // Base64 byte[]로 변환
        byte[] decodedByte = Base64.getDecoder().decode(encodingSecretKey.getBytes(StandardCharsets.UTF_8));
        // byte[]로 key 생성
        this.secretKey = Keys.hmacShaKeyFor(decodedByte);
    }

    public Token generateToken(Long id, String role) {
        Claims claims = Jwts.claims();
        claims.put("role", role);
        claims.put("id", id);

        Date now = new Date();
        return new Token(
                Jwts.builder()
                        .setSubject("Access Token")
                        .setClaims(claims)
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + tokenPeriod))
                        .signWith(secretKey, SignatureAlgorithm.HS512)
                        .compact(),
                Jwts.builder()
                        .setSubject("Refresh Token")
                        .setIssuedAt(now)
                        .setExpiration(new Date(now.getTime() + refreshPeriod))
                        .signWith(secretKey, SignatureAlgorithm.HS512)
                        .compact());
    }

    // 토큰에 담긴 payload 값 가져오기
    public Claims extractAllClaims(String token) throws ExpiredJwtException {
        String tokenDelPrefix = token.replace(TOKEN_PREFIX, "");
        return Jwts.parserBuilder()
                .setSigningKey(secretKey)
                .build()
                .parseClaimsJws(tokenDelPrefix)
                .getBody();
    }

    // 토큰 만료되었는지 확인
    public boolean checkExpiredToken(String token) {
        final Date expiration = extractAllClaims(token).getExpiration();
        return expiration.before(new Date());
    }

    // 토큰 만료 시간 가져오기
    public Long getTokenExpirationAsLong(String token) {
        // 남은 유효시간
        Date expiration = extractAllClaims(token).getExpiration();
        // 현재 시간
        Long now = new Date().getTime();
        return (expiration.getTime() - now);
    }

    //UserId 가져오기
    public Long getId(String token) {
        return extractAllClaims(token).get("id", Long.class);
    }

    // Role 가져오기
    public String getRole(String token){
        return extractAllClaims(token).get("role", String.class);
    }

    // token 유효성 확인
    public Boolean validateToken(String token) {
        String tokenDelPrefix = token.replace(TOKEN_PREFIX, "");

        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(tokenDelPrefix);
            return true;
        } catch (SignatureException ex) {
            log.error("validateToken - 유효하지 않은 JWT 서명입니다.");
            throw new SignatureException("유효하지 않은 JWT 서명입니다.");
        } catch (MalformedJwtException ex) {
            log.error("validateToken - 올바르지 않은 JWT 토큰입니다.");
            throw new MalformedJwtException("올바르지 않은 JWT 토큰입니다.");
        } catch (ExpiredJwtException ex) {
            log.error("validateToken - 만료된 JWT 토큰입니다.");
            throw new NullPointerException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException ex) {
            log.error("validateToken - 지원하지 않는 형식의 JWT 토큰입니다.");
            throw new UnsupportedJwtException("지원하지 않는 형식의 JWT 토큰입니다.");
        } catch (IllegalArgumentException ex) {
            log.error("validateToken - 정보가 담겨있지 않은 빈 토큰입니다.");
            throw new IllegalArgumentException("정보가 담겨있지 않은 빈 토큰입니다.");
        }
    }
}
