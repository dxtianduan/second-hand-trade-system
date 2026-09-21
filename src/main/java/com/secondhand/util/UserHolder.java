package com.secondhand.util;

import com.secondhand.common.exception.BusinessException;
import com.secondhand.common.result.ErrorCode;
import com.secondhand.dto.UserDTO;

/**
 * 当前登录用户的线程级持有者。
 * <p>
 * <b>为什么需要它</b>：拦截器解析出用户后，希望 Service 层能直接拿到，
 * 而不用把 userId 一层层往下传参。ThreadLocal 正好干这个 ——
 * 每个请求由独立线程处理，各存各的，互不干扰。
 * <p>
 * <b>为什么必须清理</b>：Tomcat 的线程是**复用**的（线程池）。
 * 如果只 set 不 remove，下一个请求复用同一线程时会读到上一个用户的身份，
 * 直接导致**认证绕过 + 用户数据串号**。所以 preHandle 进方法先清、
 * afterCompletion 出方法再清，两头都要做。
 * <p>
 * 这里用"先 remove 再 set"的写法，而不是覆盖式 set —— 是为了确保即便
 * 上一个请求异常退出没执行到 afterCompletion，也不会污染当前请求。
 */
public class UserHolder {

    private static final ThreadLocal<UserDTO> TL = new ThreadLocal<>();

    private UserHolder() {
    }

    /** 存入当前登录用户（由拦截器调用） */
    public static void saveUser(UserDTO user) {
        TL.remove();          // 先清，防止历史残留
        TL.set(user);
    }

    /** 获取当前登录用户，可能为 null（未登录或已放行的公开接口） */
    public static UserDTO getUser() {
        return TL.get();
    }

    /**
     * 获取当前登录用户，未登录则抛 401。
     * <p>
     * 用于「必须登录才能执行」的业务方法，省掉每处都判空。
     */
    public static UserDTO requireUser() {
        UserDTO user = TL.get();
        if (user == null) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED);
        }
        return user;
    }

    /** 获取当前登录用户ID，未登录则抛 401 */
    public static Long requireUserId() {
        return requireUser().getId();
    }

    /** 获取当前登录用户ID，未登录返回 null */
    public static Long getUserId() {
        UserDTO user = TL.get();
        return user == null ? null : user.getId();
    }

    /**
     * 要求当前用户必须是管理员，否则抛 403。
     * 用于管理端接口。
     */
    public static UserDTO requireAdmin() {
        UserDTO user = requireUser();
        if (!user.isAdmin()) {
            throw new BusinessException(ErrorCode.FORBIDDEN);
        }
        return user;
    }

    /** 清理线程变量 —— 必须在请求结束时调用 */
    public static void removeUser() {
        TL.remove();
    }
}
