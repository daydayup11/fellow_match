package org.mumu.user_centor.utils;


import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.exception.BusinessException;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getMethod().equals("OPTIONS")){
            return true;
        }
        if(UserHolder.getUser() == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //6.放行
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();//移除用户防内存泄露
    }
}
