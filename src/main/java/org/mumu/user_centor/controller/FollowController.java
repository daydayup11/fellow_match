package org.mumu.user_centor.controller;

import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.vo.UserVo;
import org.mumu.user_centor.service.FollowService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/follow")
public class FollowController {
    @Resource
    private FollowService followService;
    //关注
    @PutMapping("/{id}/{isFollow}")
    public BaseResponse<Boolean> follow(@PathVariable("id") Long followUserId, @PathVariable("isFollow") Boolean isFollow) {
        return followService.follow(followUserId, isFollow);
    }
    //是否关注
    @GetMapping("/or/not/{id}")
    public BaseResponse<Boolean> isFollow(@PathVariable("id") Long followUserId) {
        return followService.isFollow(followUserId);
    }

    /**
     * 共同关注
     * @param id 对象用户id
     * @return
     */
    @GetMapping("/common/{id}")
    public BaseResponse<List<UserVo>> followCommons(@PathVariable("id") Long id){
        return followService.followCommons(id);
    }
}
