package zero.zeroapp.handler;

import io.jsonwebtoken.*;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtHandler {
    private String type = "Bearer ";

    public String createToken(String encodedKey, String subject, long maxAgeSeconds) {
        Date now = new Date();
        return type + Jwts.builder()
                .setSubject(subject) // 토큰에 저장될 데이터 지정 -> member의 id값
                .setIssuedAt(now) // 토큰 발급일 지정
                .setExpiration(new Date(now.getTime() + maxAgeSeconds * 1000L)) //토큰 만료일 지정
                .signWith(SignatureAlgorithm.HS256, encodedKey)
                .compact();
    }

    public String extractSubject(String encodedKey, String token) {
        return parse(encodedKey, token).getBody().getSubject();
    }

    public boolean validate(String encodedKey, String token) {
        try {
            parse(encodedKey, token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    private Jws<Claims> parse(String key, String token) {
        return Jwts.parser() //parser을 이용해 사용뙨 key를 지정 후 파싱
                .setSigningKey(key)
                .parseClaimsJws(untype(token));
    }

    private String untype(String token) { // 토큰 문자열에서 토큰타입 제거
        return token.substring(type.length());
    }
}
