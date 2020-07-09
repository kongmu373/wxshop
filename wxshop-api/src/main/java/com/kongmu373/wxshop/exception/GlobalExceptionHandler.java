package com.kongmu373.wxshop.exception;

import com.kongmu373.wxshop.entity.ErrorMessage;
import com.kongmu373.wxshop.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.servlet.http.HttpServletResponse;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理参数异常
     *
     * @param rep 返回的response
     * @param e   HttpMessageNotReadableException  参数错误
     * @return 返回异常信息
     */
    @ExceptionHandler(value = HttpMessageNotReadableException.class)
    @ResponseBody
    public Result httpMessageNotReadableExceptionHandler(HttpServletResponse rep, HttpMessageNotReadableException e) {
        log.error("发生参数错误异常！原因是：{}", e.getMessage());
        rep.setStatus(400);
        return Result.create(ErrorMessage.GOODS_BAD_REQUEST, null);
    }

    /**
     * 处理参数异常
     *
     * @param rep 返回的response
     * @param e   MethodArgumentTypeMismatchException  参数错误
     * @return 返回异常信息
     */
    @ExceptionHandler(value = MethodArgumentTypeMismatchException.class)
    @ResponseBody
    public Result methodArgumentTypeMismatchExceptionHandler(HttpServletResponse rep, MethodArgumentTypeMismatchException e) {
        log.error("发生参数错误异常！原因是：{}", e.getMessage());
        rep.setStatus(400);
        return Result.create(ErrorMessage.GOODS_BAD_REQUEST, null);
    }

    /**
     * 处理自定义的业务异常
     *
     * @param rep 返回的response
     * @param e   ErrorException 业务异常
     * @return 返回异常信息
     */
    @ExceptionHandler(value = ErrorException.class)
    @ResponseBody
    public Result errorExceptionHandler(HttpServletResponse rep, ErrorException e) {
        log.error("发生业务异常！原因是：{}", e.getMessage());
        rep.setStatus(e.getCode());
        return Result.create(e.getMessage(), null);
    }


    /**
     * 处理其他异常
     *
     * @param rep 返回的response
     * @param e   Exception 未知异常
     * @return 返回异常信息
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Result exceptionHandler(HttpServletResponse rep, Exception e) {
        log.error("未知异常！原因是:", e);
        rep.setStatus(500);
        return Result.create(e.getMessage(), null);
    }
}
