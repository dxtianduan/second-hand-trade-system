package com.secondhand.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 相关配置，绑定 application.yml 里 {@code secondhand.jwt.*}。
 * <p>
 * 用类型安全绑定而不是 {@code @Value} 逐个注入，好处是 IDE 能提示、
 * 拼错字段名启动就会报错（早失败），而不是运行时才拿不到值。
 */
@Data
@Component
@ConfigurationProperties(prefix = "secondhand.jwt")
public class JwtProperties {

    /** 签名密钥。生产环境必须通过环境变量注入，不要硬编码在配置文件里 */
    private String secret;

    /** token 有效期（毫秒），默认 7 天 */
    private Long expire = 604800000L;

    /** 存放 token 的请求头名称 */
    private String header = "Authorization";

    /** token 前缀，注意末尾带空格 */
    private String prefix = "Bearer ";
}
