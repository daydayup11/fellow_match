package org.mumu.user_centor.config;

import org.mumu.user_centor.utils.LoginInterceptor;
import org.mumu.user_centor.utils.RefreshTokenInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

@Configuration
public class WebMvcConfg implements WebMvcConfigurer {
    @Resource
    private StringRedisTemplate stringRedisTemplate;
 
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        //设置允许跨域的路径
        registry.addMapping("/**")
                //设置允许跨域请求的域名
                //当**Credentials为true时，**Origin不能为星号，需为具体的ip地址【如果接口不带cookie,ip无需设成具体ip】
                .allowedOrigins("http://10.169.100.208:3000", "http://10.174.224.237:3000", "http://127.0.0.1:8080")
                //是否允许证书 不再默认开启
                .allowCredentials(true)
                //设置允许的方法
                .allowedMethods("*")
                //跨域允许时间
                .maxAge(3600);
    }
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //登录拦截器
        registry.addInterceptor(new LoginInterceptor()).excludePathPatterns(
                "/user/recommend",
                "/upload/**",
                "/post/hot",
                "/user/register",
                "/user/login",
                "/doc.html",
                "/webjars/**",
                "/swagger-resources/**", "/webjars/**", "/v2/**", "/swagger-ui.html/**",
                "/api", "/api-docs", "/api-docs/**", "/doc.html/**"
        ).order(1);
        //token刷新的拦截器
        registry.addInterceptor(new RefreshTokenInterceptor(stringRedisTemplate)).addPathPatterns("/**").order(0);//order越小优先级越高
    }

}