package com.secondhand.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用户实体，对应表 {@code tb_user}。
 * <p>
 * 注意：{@code credit} 是信用分（0-100），不是余额；
 * {@code balance} 才是钱包余额，用 {@link BigDecimal} 而非 double，
 * 避免浮点误差（0.1 + 0.2 != 0.3 那种坑）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_user")
public class User extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /** 用户 ID，数据库自增 */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 登录账号，唯一 */
    private String username;

    /** 密码，BCrypt 加密存储。禁止返回给前端，也禁止打日志 */
    private String password;

    /** 昵称 */
    private String nickname;

    /** 手机号 */
    private String phone;

    /** 邮箱 */
    private String email;

    /** 头像 URL */
    private String icon;

    /** 性别：0未知 1男 2女 */
    private Integer gender;

    /** 所在城市 */
    private String city;

    /** 信用分（0-100） */
    private Integer credit;

    /** 钱包余额（元） */
    private BigDecimal balance;

    /** 状态：0封禁 1正常 */
    private Integer status;

    // ==================== 常量与判断方法 ====================

    /** 状态：正常 */
    public static final int STATUS_NORMAL = 1;
    /** 状态：封禁 */
    public static final int STATUS_BANNED = 0;

    /** 是否被封禁。拦截器里用来提前拒绝请求，避免封禁用户继续下单 */
    public boolean isBanned() {
        return this.status != null && this.status == STATUS_BANNED;
    }
}
