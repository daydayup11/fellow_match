package org.mumu.user_centor.service;

import org.mumu.user_centor.model.User;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
* @author HP
* @description 针对表【user】的数据库操作Service
* @createDate 2023-01-18 22:40:59
*/
public interface UserService extends IService<User> {
    long userRegister(String userAccount,String userPassword,String checkPassword);
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
    User getSafetyUser(User originUser);
    int logout(HttpServletRequest request);
}
