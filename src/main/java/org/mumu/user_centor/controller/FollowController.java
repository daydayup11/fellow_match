package org.mumu.user_centor.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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
    @GetMapping
    public BaseResponse<Boolean> follow( @RequestParam("id") Long id,@RequestParam("isFollow") Boolean isFollow) {
        return followService.follow(id, isFollow);
    }
    //是否关注
    @GetMapping("/or/not")
    public BaseResponse<Boolean> isFollow(Long id) {
        return followService.isFollow(id);
    }

    /**
     * 共同关注
     * @param id 对象用户id
     * @return
     */
    @GetMapping("/common")
    public BaseResponse<List<UserVo>> followCommons(Long id){
        return followService.followCommons(id);
    }

    /**
     *
     * @return
     */
    @GetMapping("/myFollow")
    public BaseResponse<List<User>> searchMyFollow(){
        return followService.searchMyFollow();
    }
}
