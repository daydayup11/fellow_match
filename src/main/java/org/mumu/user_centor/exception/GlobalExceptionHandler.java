package org.mumu.user_centor.exception;

import org.mumu.user_centor.common.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.common.ResultUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 捕获代码中所有的异常，内部消化，集中处理，让前端得到更加详细的业务报错、信息
 * 同时屏蔽掉项目框架本身的异常（不暴露服务器的内部状态）
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(BusinessException.class)
    public BaseResponse businessException(BusinessException e){
        log.error("runtimeException"+e.getMessage(),e);
        return ResultUtils.error( e.getCode(),e.getMessage(),e.getDescription());
    }

    @ExceptionHandler(RuntimeException.class)
    public BaseResponse runtimeExceptionHandler(RuntimeException e){
        log.error("runtimeException",e);
        return ResultUtils.error(ErrorCode.SYSTEM_ERROR,e.getMessage(),"");
    }
}
