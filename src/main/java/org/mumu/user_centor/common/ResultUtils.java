package org.mumu.user_centor.common;

/**
 * 让这个类帮我们生成返回对象
 */
public class ResultUtils {

    /**
     * 成功
     * @param data
     * @param <T>
     * @return
     */
    public static <T> BaseResponse success(T data){
        return new BaseResponse(0, data,"ok");
    }
    public static <T> BaseResponse error(int code,String message,String description){
        return new BaseResponse(code, message,description);
    }

    /**
     * 失败
     * @param errorCode 自定义错误码
     * @return
     */
    public static BaseResponse error(ErrorCode errorCode){
        return new BaseResponse(errorCode);
    }
    public static BaseResponse error(ErrorCode errorCode,String message){
        return new BaseResponse(errorCode,message);
    }
    public static BaseResponse error(ErrorCode errorCode,String message,String description){
        return new BaseResponse(errorCode,message,description);
    }
}
