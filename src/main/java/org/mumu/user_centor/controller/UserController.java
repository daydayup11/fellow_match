package org.mumu.user_centor.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.commons.lang3.StringUtils;
import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.common.ResultUtils;
import org.mumu.user_centor.exception.BusinessException;
import org.mumu.user_centor.model.User;
import org.mumu.user_centor.model.request.UserLoginRequest;
import org.mumu.user_centor.model.request.UserRegisterRequest;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = {"http://47.115.226.171"},allowCredentials = "true")
public class UserController {
    @Autowired
    UserService userService;

    /**
     * 用户注册
     * @param userRegisterRequest 注册请求体
     * @return 用户id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest){
        if(userRegisterRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求体为空");
        }
        String userAccount = userRegisterRequest.getUserAccount();
        String userPassword = userRegisterRequest.getUserPassword();
        String checkPassword = userRegisterRequest.getCheckPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword,checkPassword)){
            return ResultUtils.error(ErrorCode.PARAMS_ERROR);
        }
        long result= userService.userRegister(userAccount, userPassword, checkPassword);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     * @param userLoginRequest 登录请求体
     * @return 用户实体
     */
    @PostMapping("/login")
    public BaseResponse<User> userLogin(@RequestBody UserLoginRequest userLoginRequest, HttpServletRequest request){
        if( userLoginRequest == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"请求参数为空");
        }
        String userAccount = userLoginRequest.getUserAccount();
        String userPassword = userLoginRequest.getUserPassword();
        if (StringUtils.isAnyBlank(userAccount,userPassword)){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.userLogin(userAccount, userPassword,request);
        return ResultUtils.success(user);
    }

    /**
     * 按用户名模糊查询用户
     * @param username 用户名
     * @return 查询结果
     */
    @GetMapping("/search")
    public BaseResponse<List<User>> userSearch(HttpServletRequest request,String username){
        if(!isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);
        }
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(username)){
//            默认模糊查询%username%
            queryWrapper.like("username",username);
        }
        List<User> users =  userService.list(queryWrapper);
        return ResultUtils.success(users);
    }

    @GetMapping("/current")
    public BaseResponse<User> getCurrentUser(HttpServletRequest request){
        Object o = request.getSession().getAttribute(UserServiceImpl.USER_LOGIN_STATE);
        User user = (User) o;
        if(user == null){
            throw new BusinessException(ErrorCode.NOT_LOGIN);
        }
        //查询数据库更新用户信息，如果是一个用户信息经常变动的系统
        user = userService.getById(user.getId());
        User safetyUser = userService.getSafetyUser(user);
        return ResultUtils.success(safetyUser);
    }

    /**
     * 删除用户
     * @param id 用户id
     * @return true-删除成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteUser(HttpServletRequest request,long id){
        if(isAdmin(request)){
            throw new BusinessException(ErrorCode.NO_AUTH);

        }
        if (id<=0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        Boolean b = userService.removeById(id);
        return ResultUtils.success(b);

    }

    @PostMapping("/logout")
    public BaseResponse<Integer> logout(HttpServletRequest request){
        if(request == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);

        }
        return ResultUtils.success(userService.logout(request));
    }

    /**
     * 判断是否为管理员
     * @return 是返回true
     */
    private boolean isAdmin(HttpServletRequest request){
        User user =(User) request.getSession().getAttribute(UserServiceImpl.USER_LOGIN_STATE);
        if(user == null||user.getUserRole()!=1){
            return false;
        }else{
            return true;
        }

    }

    @GetMapping("/search/tags")
    public BaseResponse<List<User>> searchByTags(@RequestParam(required = false) List<String> tagList){
       if (CollectionUtils.isEmpty(tagList)){
           throw new BusinessException(ErrorCode.PARAMS_ERROR,"参数为空");
       }
        List<User> userList = userService.searchUsersByTags(tagList);
        return ResultUtils.success(userList);
    }

    /**
     * 主页用户推荐
     * @param request
     * @param pageSize
     * @param pageNum
     * @return
     */
    @GetMapping("/recommend")
    public BaseResponse<Page<User>> recommendUsers(HttpServletRequest request,long pageSize,long pageNum){
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        Page<User> users = userService.page(new Page<>(pageNum,pageSize),queryWrapper);
        return ResultUtils.success(users);
    }



}
