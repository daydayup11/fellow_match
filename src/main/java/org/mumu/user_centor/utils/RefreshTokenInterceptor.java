package org.mumu.user_centor.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.vo.UserVo;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.mumu.user_centor.constant.RedisConstant.LOGIN_USER_KEY;
import static org.mumu.user_centor.constant.RedisConstant.LOGIN_USER_TTL;

public class RefreshTokenInterceptor implements HandlerInterceptor {

    private StringRedisTemplate stringRedisTemplate;

    public RefreshTokenInterceptor(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求头中的token
        String token = request.getHeader("authorization");
        if(StrUtil.isBlank(token)){
            //不存在放行
            return true;
        }
        //2.基于token获取redis中的用户
        String key = LOGIN_USER_KEY + token;
        Map<Object,Object> userMap = stringRedisTemplate.opsForHash().entries(key);
        //3.判断用户是否存在
        if(userMap.isEmpty()){
            return true;
        }
        //4.将查询到的hash数据转为UserVo
        User userVo = BeanUtil.fillBeanWithMap(userMap, new User(), false);
        //5.存在保护用户信息到ThreadLocal
        UserHolder.saveUser(userVo);
        //6.刷新token有效期
        stringRedisTemplate.expire(key,LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
