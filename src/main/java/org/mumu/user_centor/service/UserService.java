package org.mumu.user_centor.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.vo.UserVo;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
* @author HP
* @description 针对表【user】的数据库操作Service
* @createDate 2023-03-16 19:23:52
*/
public interface UserService extends IService<User> {

    long userRegister(String userAccount,String userPassword,String checkPassword);
    User userLogin(String userAccount, String userPassword, HttpServletRequest request, HttpServletResponse response);
    User getSafetyUser(User originUser);
    int logout(HttpServletRequest request);
    List<User> searchUsersByTags(List<String> tagNameList);
    /**
     * 是否为管理员
     * @param request
     * @return
     */
    boolean isAdmin(HttpServletRequest request);

    /**
     * 是否为管理员
     * @param loginUser
     * @return
     */
    boolean isAdmin(User loginUser);

    /**
     * 获取当前用户
     * @param request
     * @return
     */
    User getCurrentUser(HttpServletRequest request);

    /**
     * 更新用户信息
     * @param user
     * @param loginUser
     * @return
     */
    int updateUser(User user, User loginUser);

    List<User> matchUsers(long num, User loginUser);

    BaseResponse<Page<UserVo>> searchUserByDistance(Integer pageSize, Integer pageNum, Double x, Double y);

}
