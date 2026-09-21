package com.secondhand.common.result;

import lombok.Getter;

/**
 * 业务错误码。
 * <p>
 * 与《接口文档》1.5 错误码表一一对应，前端按此表做处理。
 * 编码规则：通用类 1xxx，商品类 2xxx，订单类 3xxx，评价类 4xxx，聊天类 5xxx。
 */
@Getter
public enum ErrorCode {

    // ==================== 通用 ====================
    SUCCESS(200, "操作成功"),
    PARAM_ERROR(400, "参数校验失败"),
    UNAUTHORIZED(401, "登录已过期，请重新登录"),
    FORBIDDEN(403, "无操作权限"),
    NOT_FOUND(404, "资源不存在"),
    CONFLICT(409, "状态冲突，请刷新后重试"),
    TOO_MANY_REQUESTS(429, "请求过于频繁，请稍后再试"),
    SYSTEM_ERROR(500, "系统繁忙，请稍后再试"),

    // ==================== 用户类 1xxx ====================
    USERNAME_EXISTS(1001, "用户名已存在"),
    LOGIN_FAILED(1002, "用户名或密码错误"),
    USER_BANNED(1003, "账号已被封禁"),
    OLD_PASSWORD_ERROR(1004, "原密码错误"),
    BALANCE_NOT_ENOUGH(1005, "余额不足"),
    USER_NOT_FOUND(1006, "用户不存在"),
    PHONE_EXISTS(1007, "手机号已被注册"),
    SMS_CODE_ERROR(1008, "验证码错误或已过期"),
    CREDIT_TOO_LOW(1009, "信用分不足，暂无法发布商品"),

    // ==================== 商品类 2xxx ====================
    GOODS_NOT_FOUND(2001, "商品不存在"),
    GOODS_SOLD_OUT(2002, "商品已售出"),
    GOODS_OFF_SHELF(2003, "商品已下架"),
    CANNOT_BUY_OWN_GOODS(2004, "不能购买自己发布的商品"),
    GOODS_NOT_OWNED(2005, "只能操作自己发布的商品"),

    // ==================== 订单类 3xxx ====================
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_STATUS_ERROR(3002, "订单状态不允许该操作"),
    ORDER_CLOSED(3003, "订单已超时关闭"),
    ORDER_NOT_OWNED(3004, "只能操作自己的订单"),
    REFUND_EXISTS(3005, "该订单已有进行中的退款申请"),

    // ==================== 评价类 4xxx ====================
    REVIEW_EXISTS(4001, "已评价过该订单"),
    ORDER_NOT_FINISHED(4002, "订单未完成，暂不可评价"),

    // ==================== 聊天类 5xxx ====================
    CANNOT_CHAT_SELF(5001, "不能与自己聊天"),
    CONVERSATION_NOT_FOUND(5002, "会话不存在"),

    // ==================== 文件类 6xxx ====================
    FILE_EMPTY(6001, "上传文件为空"),
    FILE_TOO_LARGE(6002, "文件大小超出限制"),
    FILE_TYPE_NOT_ALLOWED(6003, "文件类型不允许");

    private final Integer code;
    private final String msg;

    ErrorCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }
}
