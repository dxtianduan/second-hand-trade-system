package com.secondhand.config;

import com.secondhand.interceptor.LoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置：注册拦截器、配置跨域。
 * <p>
 * <b>路径规则是这个项目的鉴权地图</b>，改动这里等于改动全站权限，
 * 新增模块时记得来确认一遍。
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final LoginInterceptor loginInterceptor;

    /**
     * 公开路径：无需登录即可访问。
     * <p>
     * 设计原则：只放"浏览类"接口。任何涉及**写操作**或**个人数据**的接口都不应出现在这里。
     */
    private static final String[] PUBLIC_PATHS = {
            // 认证：注册/登录/发验证码，显然不能要求先登录
            "/api/auth/register",
            "/api/auth/login",
            "/api/auth/sms-code",
            // 商品浏览
            "/api/goods",
            "/api/goods/*",
            "/api/goods/*/comments",
            // 分类、公告
            "/api/categories/**",
            "/api/notices/**",
            // 他人主页与评价（不带 phone/email/balance，可公开）
            "/api/users/*",
            "/api/users/*/reviews",
            // Druid 监控页（仅本地开发用，上线务必关掉）
            "/druid/**",
            // 错误页
            "/error"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(loginInterceptor)
                // 拦截全部接口
                .addPathPatterns("/api/**")
                // 排除公开路径
                .excludePathPatterns(PUBLIC_PATHS)
                .order(1);
    }

    /**
     * 跨域配置。
     * <p>
     * 注意 {@code allowedOriginPatterns} 而不是 {@code allowedOrigins} ——
     * 后者在 {@code allowCredentials=true} 时不允许写 {@code "*"}，会直接抛异常。
     * 前端本地调试端口不固定，用 patterns 更省事。
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }
}
