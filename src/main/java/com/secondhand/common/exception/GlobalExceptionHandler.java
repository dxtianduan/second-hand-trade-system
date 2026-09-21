package com.secondhand.common.exception;

import com.secondhand.common.result.ErrorCode;
import com.secondhand.common.result.Result;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.stream.Collectors;

/**
 * 全局异常处理器。
 * <p>
 * 统一把各类异常转成 {@link Result}，保证前端拿到的永远是同一种结构。
 * <p>
 * 设计要点：
 * 1. 业务异常（BusinessException）单独处理，保留其原始错误码；
 * 2. 参数校验异常给出具体是哪个字段不对，方便前端定位；
 * 3. 兜底分支捕获所有未知异常，打日志但**不把堆栈返回给前端**（避免泄露实现细节）。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 业务异常：最常见，由业务代码主动抛出 */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.error(e.getCode(), e.getMessage());
    }

    /** @RequestBody 上的 @Valid 校验失败 */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleValidException(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + " " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        return Result.error(ErrorCode.PARAM_ERROR, msg.isEmpty() ? ErrorCode.PARAM_ERROR.getMsg() : msg);
    }

    /** 方法参数上的 @Validated 校验失败（如 @PathVariable @Min(1)） */
    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolation(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining("; "));
        return Result.error(ErrorCode.PARAM_ERROR, msg);
    }

    /** 缺少必填的请求参数 */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public Result<Void> handleMissingParam(MissingServletRequestParameterException e) {
        return Result.error(ErrorCode.PARAM_ERROR, "缺少必要参数：" + e.getParameterName());
    }

    /** 参数类型不匹配，如把 "abc" 传给 Integer */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public Result<Void> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        return Result.error(ErrorCode.PARAM_ERROR, "参数格式错误：" + e.getName());
    }

    /** 请求体不是合法 JSON */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Result<Void> handleNotReadable(HttpMessageNotReadableException e) {
        return Result.error(ErrorCode.PARAM_ERROR, "请求体格式错误，请检查 JSON 是否合法");
    }

    /** 上传文件超过限制 */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public Result<Void> handleMaxUploadSize(MaxUploadSizeExceededException e) {
        return Result.error(ErrorCode.FILE_TOO_LARGE);
    }

    /** 访问了不存在的路径（需在 application.yml 里开 throw-exception-if-no-handler-found） */
    @ExceptionHandler(NoHandlerFoundException.class)
    public Result<Void> handleNoHandlerFound(NoHandlerFoundException e) {
        return Result.error(ErrorCode.NOT_FOUND, "接口不存在：" + e.getRequestURL());
    }

    /**
     * 兜底：捕获所有未被上面处理的异常。
     * <p>
     * 注意这里只输出简短的错误摘要，不把完整堆栈发给前端 ——
     * 堆栈里可能包含表名、类名、SQL 片段等内部信息。
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        // 生产环境建议改成 log.error("系统异常", e);
        e.printStackTrace();
        return Result.error(ErrorCode.SYSTEM_ERROR);
    }
}
