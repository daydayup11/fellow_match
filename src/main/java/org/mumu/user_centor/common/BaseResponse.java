package org.mumu.user_centor.common;

import lombok.Data;

import java.io.Serializable;
@Data
public class BaseResponse <T> implements Serializable {
    private int code;

    private T data;

    private String message;

    private String description;

    public BaseResponse(int code, T data, String message,String description) {
        this.code = code;
        this.data = data;
        this.message = message;
        this.description = description;
    }

    public BaseResponse(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

    public BaseResponse(ErrorCode errorCode){
        //失败没有数据
        this(errorCode.getCode(),null,errorCode.getMessage(),errorCode.getDescription());
    }
    public BaseResponse(ErrorCode errorCode,String message){
        //失败没有数据
        this(errorCode.getCode(),null,message,errorCode.getDescription());
    }
    public BaseResponse(ErrorCode errorCode,String message,String description){
        //失败没有数据
        this(errorCode.getCode(),null,message,description);
    }
}
