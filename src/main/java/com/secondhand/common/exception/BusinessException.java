package com.secondhand.common.exception;

import com.secondhand.common.result.ErrorCode;
import lombok.Getter;

/**
 * 业务异常。
 * <p>
 * 业务规则不满足时抛出，由 {@code GlobalExceptionHandler} 统一转成 {@code Result} 返回，
 * 这样业务代码里不用到处写 try-catch 和手动构造返回值。
 * <p>
 * 继承 {@link RuntimeException} 而不是 Exception，是为了不污染方法签名
 * （否则每个方法都要声明 throws），也让 Spring 的 {@code @Transactional} 默认能回滚。
 */
@Getter
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final Integer code;

    public BusinessException(ErrorCode errorCode) {
        super(errorCode.getMsg());
        this.code = errorCode.getCode();
    }

    /** 使用预设错误码，但替换提示语（比如带上具体的商品名） */
    public BusinessException(ErrorCode errorCode, String msg) {
        super(msg);
        this.code = errorCode.getCode();
    }

    public BusinessException(Integer code, String msg) {
        super(msg);
        this.code = code;
    }
}
