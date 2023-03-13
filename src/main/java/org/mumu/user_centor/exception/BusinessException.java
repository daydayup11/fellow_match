package org.mumu.user_centor.exception;

import lombok.Data;
import org.mumu.user_centor.common.ErrorCode;

/**
 * 自定义异常类，全局异常处理
 */
@Data
public class BusinessException extends RuntimeException{

    //扩充字段
    private final int code;
    public final String description;

    public BusinessException(String message, int code, String description) {
        super(message);
        this.code = code;
        this.description = description;
    }

    //自定义构造函数
     public BusinessException(ErrorCode errorCode){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = errorCode.getDescription();
     }

    public BusinessException(ErrorCode errorCode,String description){
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
        this.description = description;
    }
}
