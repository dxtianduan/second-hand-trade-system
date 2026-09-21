package com.secondhand.interceptor;

import com.secondhand.common.result.ErrorCode;
import com.secondhand.common.result.Result;
import com.secondhand.config.JwtProperties;
import com.secondhand.dto.UserDTO;
import com.secondhand.util.JwtUtil;
import com.secondhand.util.UserHolder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

/**
 * 登录拦截器：解析 token 并放入 {@link UserHolder}。
 * <p>
 * <b>只负责"识别身份"，不负责"是否允许访问"</b> ——
 * 因为有一批接口是公开的（商品列表、商品详情等），是否拦截由
 * {@code WebMvcConfig} 的路径规则决定。
 * <p>
 * 这样设计的好处：公开接口如果顺带带了 token，也能拿到登录身份
 * （用于返回 "isFavorite" 这类个性化字段），而不用重复写解析逻辑。
 */
@Component
@RequiredArgsConstructor
public class LoginInterceptor implements HandlerInterceptor {

    private final JwtUtil jwtUtil;
    private final JwtProperties jwtProperties;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        // 第一件事就清理，避免上一个请求的残留污染当前请求
        UserHolder.removeUser();

        String header = request.getHeader(jwtProperties.getHeader());
        String token = jwtUtil.resolveToken(header);
        if (token == null) {
            // 没带 token：公开接口放行（UserHolder 为 null），需登录的接口由注册规则拦住
            return true;
        }

        UserDTO user = jwtUtil.parseToken(token);
        if (user == null) {
            // 带了 token 但无效/过期：直接 401，让前端清 token 跳登录页
            writeUnauthorized(response);
            return false;
        }

        UserHolder.saveUser(user);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        // 必须清理！Tomcat 线程是复用的，不清理会导致用户身份串号
        UserHolder.removeUser();
    }

    /** 写出 401 响应，结构与全局统一响应保持一致 */
    private void writeUnauthorized(HttpServletResponse response) throws Exception {
        response.setStatus(HttpServletResponse.SC_OK);   // 项目约定 HTTP 恒为 200
        response.setContentType("application/json;charset=UTF-8");
        Result<Void> result = Result.error(ErrorCode.UNAUTHORIZED);
        try (PrintWriter writer = response.getWriter()) {
            writer.write(objectMapper.writeValueAsString(result));
            writer.flush();
        }
    }
}
