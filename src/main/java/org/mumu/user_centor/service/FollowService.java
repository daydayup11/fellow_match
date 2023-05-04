package org.mumu.user_centor.service;

import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.model.domain.Follow;
import com.baomidou.mybatisplus.extension.service.IService;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.vo.UserVo;

import java.util.List;

/**
* @author HP
* @description 针对表【follow】的数据库操作Service
* @createDate 2023-04-29 00:23:21
*/
public interface FollowService extends IService<Follow> {

    BaseResponse<Boolean> follow(Long followUserId, Boolean isFollow);

    BaseResponse<Boolean> isFollow(Long followUserId);

    BaseResponse<List<UserVo>> followCommons(Long id);

    BaseResponse<List<User>> searchMyFollow();
}
