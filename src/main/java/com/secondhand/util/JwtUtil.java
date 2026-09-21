package com.secondhand.util;

import com.secondhand.config.JwtProperties;
import com.secondhand.dto.UserDTO;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * JWT 工具类：签发与校验 token。
 * <p>
 * <b>JWT 是什么</b>：一段用密钥签过名的字符串，里面能塞少量数据（这里是用户ID和昵称）。
 * 服务端不需要存 session，只要验签通过就能确认"这串东西是我发的、没被改过"。
 * 代价是**签发后无法主动作废**（除非引入黑名单），所以别把敏感信息放进去。
 *
 * @see JwtProperties 密钥与有效期配置
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtUtil {

    private final JwtProperties jwtProperties;

    private static final String CLAIM_USER_ID = "userId";
    private static final String CLAIM_USERNAME = "username";
    private static final String CLAIM_NICKNAME = "nickname";
    private static final String CLAIM_ROLE = "role";

    /**
     * 签发 token。
     *
     * @param user 登录用户信息
     * @return 签好名的 token 字符串
     */
    public String createToken(UserDTO user) {
        Date now = new Date();
        Date expireAt = new Date(now.getTime() + jwtProperties.getExpire());

        return Jwts.builder()
                .claim(CLAIM_USER_ID, user.getId())
                .claim(CLAIM_USERNAME, user.getUsername())
                .claim(CLAIM_NICKNAME, user.getNickname())
                .claim(CLAIM_ROLE, user.getRole())
                // 主题放用户ID，方便日志排查
                .setSubject(String.valueOf(user.getId()))
                .setIssuedAt(now)
                .setExpiration(expireAt)
                .signWith(SignatureAlgorithm.HS256, jwtProperties.getSecret())
                .compact();
    }

    /**
     * 解析并校验 token。
     * <p>
     * 签名不对、被篡改、已过期都会抛 {@link JwtException}，
     * 这里统一捕获返回 null，由调用方决定怎么处理（通常是返回 401）。
     *
     * @param token 待校验的 token
     * @return 解析出的用户信息；token 非法时返回 null
     */
    public UserDTO parseToken(String token) {
        if (token == null || token.trim().isEmpty()) {
            return null;
        }
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(jwtProperties.getSecret())
                    .parseClaimsJws(token)
                    .getBody();

            UserDTO user = new UserDTO();
            user.setId(claims.get(CLAIM_USER_ID, Number.class).longValue());
            user.setUsername(claims.get(CLAIM_USERNAME, String.class));
            user.setNickname(claims.get(CLAIM_NICKNAME, String.class));
            Number role = claims.get(CLAIM_ROLE, Number.class);
            user.setRole(role == null ? 0 : role.intValue());
            return user;
        } catch (JwtException | IllegalArgumentException e) {
            // 过期或签名不合法都走这里，属于预期内情况，打 warn 即可
            log.warn("token 校验失败: {}", e.getMessage());
            return null;
        }
    }

    /**
     * 从请求头值中剥掉前缀，取出纯 token。
     * <p>
     * 前端传来的是 {@code "Bearer eyJhbGci..."}，需要去掉 {@code "Bearer "} 前缀。
     *
     * @param headerValue 请求头原始值
     * @return 纯 token；格式不对时返回 null
     */
    public String resolveToken(String headerValue) {
        if (headerValue == null || headerValue.trim().isEmpty()) {
            return null;
        }
        String prefix = jwtProperties.getPrefix();
        if (prefix != null && !prefix.isEmpty() && headerValue.startsWith(prefix)) {
            return headerValue.substring(prefix.length()).trim();
        }
        // 兼容前端不带前缀的情况
        return headerValue.trim();
    }
}
