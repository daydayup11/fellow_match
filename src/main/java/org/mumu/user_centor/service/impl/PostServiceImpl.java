package org.mumu.user_centor.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.mumu.user_centor.common.BaseResponse;
import org.mumu.user_centor.common.ErrorCode;
import org.mumu.user_centor.common.ResultUtils;
import org.mumu.user_centor.exception.BusinessException;
import org.mumu.user_centor.model.domain.Follow;
import org.mumu.user_centor.model.domain.Post;
import org.mumu.user_centor.model.domain.PostComment;
import org.mumu.user_centor.model.domain.User;
import org.mumu.user_centor.model.dto.UserDTO;
import org.mumu.user_centor.model.request.DeleteRequest;
import org.mumu.user_centor.model.request.PostCommentAddRequest;
import org.mumu.user_centor.model.vo.PostCommentVO;
import org.mumu.user_centor.model.vo.PostVo;
import org.mumu.user_centor.model.vo.ScrollResult;
import org.mumu.user_centor.model.vo.UserVo;
import org.mumu.user_centor.service.FollowService;
import org.mumu.user_centor.service.PostCommentService;
import org.mumu.user_centor.service.PostService;
import org.mumu.user_centor.mapper.PostMapper;
import org.mumu.user_centor.service.UserService;
import org.mumu.user_centor.utils.UserHolder;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static org.mumu.user_centor.constant.PostConstant.MAX_PAGE_SIZE;
import static org.mumu.user_centor.constant.RedisConstant.FEED_KEY;
import static org.mumu.user_centor.constant.RedisConstant.POST_LIKED_KEY;

/**
* @author HP
* @description 针对表【post】的数据库操作Service实现
* @createDate 2023-04-28 16:19:35
*/
@Service
public class PostServiceImpl extends ServiceImpl<PostMapper, Post>
    implements PostService{

    @Resource
    FollowService followService;
    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    PostCommentService postCommentService;
    @Resource
    UserService userService;
    @Override
    public BaseResponse<Long> savePost(Post post) {
        // 获取登录用户
        User user = UserHolder.getUser();
        post.setUserId(user.getId());
        // 保存帖子
        boolean isSuccess = save(post);
        if(!isSuccess){
            return ResultUtils.error(ErrorCode.SYSTEM_ERROR,"新增帖子失败");
        }
        //查询帖子作者的所有粉丝 select * from tb_follow where follow_user_id = ?
        List<Follow> followList = followService.query().eq("followUserId", user.getId()).list();
        //推送帖子给所有粉丝
        for(Follow follow : followList){
            Long userId = follow.getUserId();
            String key = FEED_KEY + userId;
            //将新添加的帖子保存到每个粉丝的收件箱中
            stringRedisTemplate.opsForZSet().add(key,post.getId().toString(),System.currentTimeMillis());
        }
        // 返回id
        return ResultUtils.success(post.getId());
    }

    /**
     * 查看帖子详情
     * @param id
     * @return
     */
    @Override
    public BaseResponse<PostVo> queryPostById(Long id) {
        Post post = getById(id);
        if(post == null){
            return ResultUtils.error(ErrorCode.PARAMS_NULL_ERROR,"帖子不存在");
        }
        //查询blog有关的用户和评论
        List<PostCommentVO> postCommentVOS = queryPostUser(post);
        PostVo postVo = new PostVo();
        postVo.setPost(post);
        postVo.setPostCommentList(postCommentVOS);
        //查询创建人信息
        Long userId = post.getUserId();
        User user = userService.getById(userId);
        UserVo userVo = BeanUtil.copyProperties(user, UserVo.class);
        postVo.setCreateUser(userVo);
        return ResultUtils.success(postVo);
    }
    /**
     * 按点赞先后顺序展示前五用户
     * @param id
     * @return
     */
    @Override
    public BaseResponse<List<UserDTO>> queryPostLikes(Long id) {
        String key = POST_LIKED_KEY + id;
        //查询top5的点赞用户 zrange key 0 4
        Set<String> top5 = stringRedisTemplate.opsForZSet().range(key, 0, 4);
        if(top5 == null||top5.isEmpty()){
            //没人点赞返回空
            return ResultUtils.success(Collections.emptyList());
        }
        //解析用户id
        List<Long> ids = top5.stream().map(Long::valueOf).collect(Collectors.toList());
        String idStr = StrUtil.join(",", ids);
        // 3.根据用户id查询用户 WHERE id IN ( 5 , 1 ) ORDER BY FIELD(id, 5, 1)
        List<UserDTO> userDTOS = userService.query()
                .in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list()
                .stream()
                .map(user -> BeanUtil.copyProperties(user, UserDTO.class))
                .collect(Collectors.toList());
        // 4.返回
        return ResultUtils.success(userDTOS);

    }

    /**
     * 按照点赞数量展示帖子
     * @param current
     * @return
     */
    @Override
    public BaseResponse<List<Post>> queryHotPost(Integer current) {
        Page<Post> page = query().orderByDesc("liked").page(new Page<>(current, MAX_PAGE_SIZE));
        //获取当前页数据
        List<Post> records = page.getRecords();
        //查询用户
        records.forEach(blog -> {
            queryPostUser(blog);
            isBlogLiked(blog);
        });
        return ResultUtils.success(records);
    }

    /**
     * 查找帖子创作人信息和评论
     * @param post
     */
    private List<PostCommentVO> queryPostUser(Post post) {
        Long userId = post.getUserId();
        User user = userService.getById(userId);
        post.setName(user.getUsername());
        post.setAvatar(user.getAvatarUrl());
        Long postId = post.getId();
        if (postId <= 0){
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        //返回类
        List<PostCommentVO> postCommentVOList = new ArrayList<>();
        //该帖子的所有评论
        List<PostComment> comments = postCommentService.query().eq("postId", postId).list();
        //帖子有评论就加载评论
        if (comments != null && comments.size() > 0) {
            //查询评论用户的信息
            List<Long> userIdList = comments.stream().map(PostComment::getUserId).collect(Collectors.toList());
            QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
            userQueryWrapper.in("id", userIdList);
            // userId -> user 用户id对应用户信息
            Map<Long, List<User>> userListMap = userService.list(userQueryWrapper)
                    .stream().collect(Collectors.groupingBy(User::getId));
            //将查出来的用户信息与评论信息对接
            //将信息复制到返回类中
            comments.forEach(postComment -> {
                PostCommentVO postCommentVO = new PostCommentVO();
                BeanUtils.copyProperties(postComment, postCommentVO);
                postCommentVOList.add(postCommentVO);
            });
            //将用户信息对接给评论
            postCommentVOList.forEach(postCommentVO -> {
                User user1 = userListMap.get(postCommentVO.getUserId()).get(0);
                postCommentVO.setUsername(user1.getUsername());
                postCommentVO.setAvatarUrl(user1.getAvatarUrl());
            });
        }
        return postCommentVOList ;
    }

    /**
     * 判断当前用户是否给帖子点赞
     * @param post
     */
    private void isBlogLiked(Post post) {
        // 1.获取登录用户
        User user = UserHolder.getUser();
        if (user == null) {
            // 用户未登录，无需查询是否点赞
            return;
        }
        Long userId = user.getId();
        // 2.判断当前登录用户是否已经点赞
        String key = "post:liked:" + post.getId();
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        post.setIsLike(score != null);
    }

    /**
     * 给帖子点赞
     * @param id
     * @return
     */
    @Override
    public BaseResponse<Boolean> likePost(Long id) {
        //1.判断当前用户是否点过赞
        Long userId = UserHolder.getUser().getId();
        String key = POST_LIKED_KEY + id;
        Double score = stringRedisTemplate.opsForZSet().score(key, userId.toString());
        if(score == null){
            //2.如果未点赞
            //数据库对应的blog赞+1
            boolean isSuccess = update().setSql("liked = liked + 1").eq("id", id).update();
            //redis保存用户
            if (isSuccess){
                stringRedisTemplate.opsForZSet().add(key,userId.toString(),System.currentTimeMillis());
            }
        }else{
            //3.如果已经点过赞
            boolean isSuccess = update().setSql("liked = liked - 1").eq("id", id).update();
            //redis删除用户
            if (isSuccess){
                stringRedisTemplate.opsForZSet().remove(key,userId.toString());
            }
        }
        return ResultUtils.success(true);
    }

    /**
     * 推送：滚动分页查询帖子
     * @param max
     * @param offset
     * @return
     */
    @Override
    public BaseResponse<ScrollResult> queryPostOfFollow(Long max, Integer offset) {
        //1.获取当前用户
        Long userId = UserHolder.getUser().getId();
        //2.查询收件箱 zrevrangebyscore key max min limit offset count
        String key = FEED_KEY + userId;
        Set<ZSetOperations.TypedTuple<String>> typedTuples = stringRedisTemplate.opsForZSet().reverseRangeByScoreWithScores(key, 0, max, offset, 2);
        if (typedTuples == null||typedTuples.isEmpty()){
            return ResultUtils.success(new ScrollResult());
        }
        //3.解析数据blogId、minTime（时间戳）、offset
        List<Long> ids = new ArrayList<>(typedTuples.size());
        long minTime = 0;
        int os = 1;//默认小于等于跳过一个即他自己
        for(ZSetOperations.TypedTuple<String> tuple : typedTuples){
            //获取笔记id
            ids.add(Long.valueOf(tuple.getValue()));
            //获取分数即时间戳
            long time = tuple.getScore().longValue();
            if(time == minTime){
                os++;
            }else{
                minTime = time;
                os = 1;
            }
        }
        //如果新一次分页查询还是分数相同，需要加上上一次的偏移量
        os = minTime == max ? os : os + offset;
        // 5.根据id查询post
        String idStr = StrUtil.join(",", ids);
        List<Post> posts = query().in("id", ids).last("ORDER BY FIELD(id," + idStr + ")").list();
        for (Post post : posts) {
            // 5.1.查询blog有关的用户
            queryPostUser(post);
            // 5.2.查询blog是否被点赞
            isBlogLiked(post);
        }
        // 6.封装并返回
        ScrollResult r = new ScrollResult();
        r.setList(posts);
        r.setOffset(os);
        r.setMinTime(minTime);
        return ResultUtils.success(r);
    }

    /**
     * 删除帖子及其评论
     * @param id
     * @return
     */
    @Override
    public Boolean deletePost(Long id) {
        Post post = getById(id);
        User user = UserHolder.getUser();
        if(!userService.isAdmin(user)&&post.getUserId() != user.getId()){
            throw new BusinessException(ErrorCode.NO_AUTH, "无访问权限");
        }
        QueryWrapper<PostComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("postId", id);
        //删除帖子的评论。如果没有评论则不删除
        long postCommentCount = postCommentService.count(queryWrapper);
        if (postCommentCount > 0){
            boolean remove = postCommentService.remove(queryWrapper);
            if (!remove) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "删除帖子评论失败");
            }
        }
        return removeById(id);
    }
}




