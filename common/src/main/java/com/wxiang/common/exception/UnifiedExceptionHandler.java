package com.wxiang.common.exception;

import com.wxiang.common.result.R;
import com.wxiang.common.result.ResponseEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 通用的异常处理方案
 */
@Slf4j
@RestControllerAdvice
public class UnifiedExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    public R handlerException(Exception e){  // e 表示原始异常对象，主要包含异常跟踪栈信息
        log.error(e.getMessage(), e);
        return R.error();
    }

    // 特定异常处理方案
    @ExceptionHandler(value = BadSqlGrammarException.class)
    public R handlerException(BadSqlGrammarException e){
        log.error(e.getMessage(), e);
        return R.setResult(ResponseEnum.BAD_SQL_GRAMMAR_ERROR);
    }

    // 通用异常处理方案，根据接收的枚举对象，可以处理各种不同的异常
    @ExceptionHandler(value = BusinessException.class)
    public R handlerException(BusinessException e){
        log.error(e.getMessage(), e);
        return R.error().message(e.getMessage()).code(e.getCode());
    }

    /** 批量异常处理
     * Controller 上一层相关异常
     * @param e
     * @return
     */
    @ExceptionHandler({
            NoHandlerFoundException.class,
            HttpRequestMethodNotSupportedException.class,
            HttpMediaTypeNotSupportedException.class,
            MissingPathVariableException.class,
            MissingServletRequestParameterException.class,
            TypeMismatchException.class,
            HttpMessageNotReadableException.class,
            HttpMessageNotWritableException.class,
            MethodArgumentNotValidException.class,
            HttpMediaTypeNotAcceptableException.class,
            ServletRequestBindingException.class,
            ConversionNotSupportedException.class,
            MissingServletRequestPartException.class,
            AsyncRequestTimeoutException.class
    })
    public R handlerServletException(Exception e){
        log.error(e.getMessage(), e);
        // SERVLET_ERROR(-102, "servlet请求异常"), //-2xx 参数校验
        return R.error().message(ResponseEnum.SERVLET_ERROR.getMessage()).code(ResponseEnum.SERVLET_ERROR.getCode());
    }

}
