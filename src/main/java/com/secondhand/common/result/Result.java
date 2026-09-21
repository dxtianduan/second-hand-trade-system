package com.secondhand.common.result;

import lombok.Data;

import java.io.Serializable;

/**
 * 统一响应结果。
 * <p>
 * 项目约定：HTTP 状态码恒为 200，业务成败通过 {@code code} 字段判断。
 * 这样前端拦截器只需处理一种情况，不用同时判断 HTTP 状态和业务码。
 *
 * @param <T> 业务数据类型
 */
@Data
public class Result<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 业务状态码，200 表示成功 */
    private Integer code;

    /** 提示信息，可直接展示给用户 */
    private String msg;

    /** 业务数据，无数据时为 null */
    private T data;

    private Result(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // ==================== 成功 ====================

    /** 成功，无返回数据 */
    public static <T> Result<T> success() {
        return new Result<>(ErrorCode.SUCCESS.getCode(), "操作成功", null);
    }

    /** 成功，带返回数据 */
    public static <T> Result<T> success(T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), "操作成功", data);
    }

    /** 成功，自定义提示语 + 数据 */
    public static <T> Result<T> success(String msg, T data) {
        return new Result<>(ErrorCode.SUCCESS.getCode(), msg, data);
    }

    // ==================== 失败 ====================

    /** 失败，使用预设错误码 */
    public static <T> Result<T> error(ErrorCode errorCode) {
        return new Result<>(errorCode.getCode(), errorCode.getMsg(), null);
    }

    /** 失败，使用预设错误码 + 自定义提示语 */
    public static <T> Result<T> error(ErrorCode errorCode, String msg) {
        return new Result<>(errorCode.getCode(), msg, null);
    }

    /** 失败，自定义状态码与提示语 */
    public static <T> Result<T> error(Integer code, String msg) {
        return new Result<>(code, msg, null);
    }

    /** 失败，仅自定义提示语，状态码用 500 */
    public static <T> Result<T> error(String msg) {
        return new Result<>(ErrorCode.SYSTEM_ERROR.getCode(), msg, null);
    }
}
