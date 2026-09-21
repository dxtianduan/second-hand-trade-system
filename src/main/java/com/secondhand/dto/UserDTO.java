package com.secondhand.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 登录用户上下文。
 * <p>
 * 只放**鉴权必需的最小信息**，不要塞整个 User 实体（含密码等敏感字段）。
 * 这个对象会被放进 ThreadLocal，随请求生命周期存在，所以越轻量越好。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 用户ID */
    private Long id;

    /** 用户名 */
    private String username;

    /** 昵称 */
    private String nickname;

    /** 头像 */
    private String icon;

    /** 角色：0 普通用户 1 管理员 */
    private Integer role;

    /** 是否管理员 */
    public boolean isAdmin() {
        return role != null && role == 1;
    }
}
