package org.mumu.user_centor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.common.ResultUtils;
import org.mumu.user_centor.exception.BusinessException;
import org.mumu.user_centor.model.domain.Follow;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.vo.UserVo;
import org.mumu.user_centor.service.FollowService;
import org.mumu.user_centor.mapper.FollowMapper;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.utils.UserHolder;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.mumu.user_centor.constant.RedisConstant.FOLLOW_KEY;

/**
* @author HP
* @description 针对表【follow】的数据库操作Service实现
* @createDate 2023-04-29 00:23:21
*/
@Service
public class FollowServiceImpl extends ServiceImpl<FollowMapper, Follow>
    implements FollowService{
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    UserService userService;

    /**
     * 关注用户
     * @param followUserId 需要关注的用户id
     * @param isFollow 是否关注过
     * @return
     */
    @Override
    public BaseResponse<Boolean> follow(Long followUserId, Boolean isFollow) {
        // 1.获取登录用户
        Long userId = UserHolder.getUser().getId();
        String key = "follows:" + userId;
        // 1.判断到底是关注还是取关
        if (isFollow) {
            // 2.关注，新增数据
            Follow follow = new Follow();
            follow.setUserId(userId);
            follow.setFollowUserId(followUserId);
            boolean isSuccess = save(follow);
            if (isSuccess) {
                // 把关注用户的id，放入redis的set集合 sadd userId followerUserId
                stringRedisTemplate.opsForSet().add(key, followUserId.toString());
            }else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        } else {
            // 3.取关，删除 delete from tb_follow where user_id = ? and follow_user_id = ?
            boolean isSuccess = remove(new QueryWrapper<Follow>()
                    .eq("userId", userId).eq("followUserId", followUserId));
            if (isSuccess) {
                // 把关注用户的id从Redis集合中移除
                stringRedisTemplate.opsForSet().remove(key, followUserId.toString());
            }else {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR);
            }
        }
        return ResultUtils.success(true);
    }

    /**
     * 是否关注
     * @param followUserId 用户id
     * @return
     */
    @Override
    public BaseResponse<Boolean> isFollow(Long followUserId) {
        // 1.获取登录用户
        Long userId = UserHolder.getUser().getId();
        // 2.查询是否关注 select count(*) from tb_follow where user_id = ? and follow_user_id = ?
        Integer count = query().eq("userId", userId).eq("followUserId", followUserId).count();
        // 3.判断
        return ResultUtils.success(count > 0);
    }

    /**
     * 查看共同好友
     * @param id 对象用户id
     * @return
     */
    @Override
    public BaseResponse<List<UserVo>> followCommons(Long id) {
        //1.获取当前用户
        Long userId = UserHolder.getUser().getId();
        String key = FOLLOW_KEY +userId;
        //2.求交集
        String key2 = FOLLOW_KEY + id;
        Set<String> intersect = stringRedisTemplate.opsForSet().intersect(key, key2);
        if(intersect == null||intersect.isEmpty()){
            return ResultUtils.success((Collections.emptyList()));
        }
        //3.解析id集合
        List<Long> ids = intersect.stream().map(Long::valueOf).collect(Collectors.toList());
        //4.查询用户
        List<User> users = userService.listByIds(ids);
        List<UserVo> userVos = new ArrayList<>();
        for (User user : users) {
            User safetyUser = userService.getSafetyUser(user);
            userVos.add(BeanUtil.copyProperties(safetyUser,UserVo.class));
        }
        return ResultUtils.success(userVos);
    }

    @Override
    public BaseResponse<List<User>> searchMyFollow() {
        User user = UserHolder.getUser();
        if (user == null){
            ResultUtils.error(ErrorCode.NOT_LOGIN);
        }
        List<Follow> follows = query().eq("userId", user.getId()).list();
        //5.根据id查询user
        List<Long> ids = new ArrayList<>();
        for (Follow follow : follows) {
            Long followUserId = follow.getFollowUserId();
            ids.add(followUserId);
        }
        String idStr = StrUtil.join(",", ids);
        List<User> users = userService.query().in("id", idStr).list();
        return ResultUtils.success(users);
    }
}




