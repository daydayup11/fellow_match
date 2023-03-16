package org.mumu.user_centor.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.mumu.user_centor.model.User;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author HP
* @description 针对表【user】的数据库操作Service
* @createDate 2023-03-16 19:23:52
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount,String userPassword,String checkPassword);
    User userLogin(String userAccount, String userPassword, HttpServletRequest request);
    User getSafetyUser(User originUser);
    int logout(HttpServletRequest request);
    List<User> searchUsersByTags(List<String> tagNameList);
}
